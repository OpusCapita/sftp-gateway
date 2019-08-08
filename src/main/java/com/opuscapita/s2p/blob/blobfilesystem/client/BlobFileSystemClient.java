package com.opuscapita.s2p.blob.blobfilesystem.client;

import com.opuscapita.s2p.blob.blobfilesystem.BlobDirEntry;
import com.opuscapita.s2p.blob.blobfilesystem.BlobPath;
import com.opuscapita.s2p.blob.blobfilesystem.client.Exception.BlobException;
import com.opuscapita.s2p.blob.blobfilesystem.config.BlobConfiguration;
import lombok.Getter;
import org.apache.commons.io.IOUtils;
import org.apache.sshd.common.util.buffer.Buffer;
import org.apache.sshd.common.util.buffer.ByteArrayBuffer;
import org.apache.sshd.common.util.logging.AbstractLoggingBean;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.HttpMessageConverterExtractor;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.RestTemplate;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

public class BlobFileSystemClient extends AbstractLoggingBean {
    @Getter
    private RestTemplate restTemplate;
    @Getter
    private final URL rootUrl;
    private final BlobConfiguration configuration;
    private final String jwt;

    public BlobFileSystemClient(
            RestTemplateBuilder _restTemplateBuilder,
            BlobConfiguration configuration,
            String tenant_id,
            String jwt
    ) throws MalformedURLException {
        this.restTemplate = _restTemplateBuilder.build();
        this.configuration = configuration;
        this.jwt = jwt;
        this.rootUrl = new URL("http://blob:3012/api/" + tenant_id + "/files" + "/" + configuration.getAccess()); // + "/onboarding/eInvoiceSupplierOnboarding";

    }

    /**
     * @param path
     * @return
     * @throws BlobException
     */
    public Map<String, BlobDirEntry> listFiles(BlobPath path) throws BlobException {
        log.debug("File list requested from blob service for folder: " + path.toString());
        try {
            if (path.endsWith(".")) {
                path = path.getParent();
            } else if (path.endsWith("..")) {
                path = path.getParent().getParent();
            }
            if (!path.endsWith("/")) {
                path = new BlobPath(path.getFileSystem(), new String(path.toString() + "/").getBytes());
            }
            ResponseEntity<BlobDirEntry[]> result = get(path, BlobDirEntry[].class, HttpMethod.GET);
            log.info("File list fetched successfully from blob service for folder: " + path.toString());
            Map<String, BlobDirEntry> listMap = new HashMap<>();
            for (BlobDirEntry entry : Arrays.asList(result.getBody())) {
                listMap.put(entry.getName(), entry);
            }
            return listMap;
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new BlobException("Error occurred while trying to read the file list from blob service.");
        }
    }

    /**
     * @param path
     * @return
     * @throws BlobException
     */
    public BlobDirEntry listFile(BlobPath path) throws BlobException {
        log.debug("File requested from blob service: " + path.toString());
        try {
            ResponseEntity<String> result = get(path, String.class, HttpMethod.HEAD);
            log.info("File fetched successfully from blob service: " + path.toString());
            return BlobDirEntry.fromJson(URLDecoder.decode(result.getHeaders().getFirst("X-File-Info"), StandardCharsets.UTF_8.name()));
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new BlobException("Error occurred while trying to read the file from blob service.");
        }
    }

    /**
     * @param path
     * @param createMissing
     * @return
     * @throws BlobException
     */
    public BlobDirEntry createDirectory(BlobPath path, boolean createMissing) throws BlobException {
        log.info("Creating new Directory: " + path);

        if (!path.endsWith("/")) {
            path = new BlobPath(path.getFileSystem(), new String(path.toString() + "/").getBytes());
        }

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.set("X-User-Id-Token", jwt);
            HttpEntity<String> entity = new HttpEntity<>("body", headers);
            log.info("Setting http headers content type to application json");
            ResponseEntity<String> result = restTemplate.exchange(this.rootUrl.toString() + path + "?createMissing=" + createMissing, HttpMethod.PUT, entity, String.class);
            return BlobDirEntry.fromJson(URLDecoder.decode(result.getHeaders().getFirst("X-File-Info"), StandardCharsets.UTF_8.name()));
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new BlobException("Error occurred while trying to create the Directory.");
        }
    }

    /**
     * @param path
     * @param type
     * @param httpMethod
     * @param <T>
     * @return
     * @throws Exception
     */

    private <T> ResponseEntity<T> get(BlobPath path, Class<T> type, HttpMethod httpMethod) throws Exception {
        log.info("Reading file from endpoint: " + path);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        headers.set("X-User-Id-Token", jwt);
        HttpEntity<String> entity = new HttpEntity<>("body", headers);
        log.info("Setting http headers content type to application json");

        return restTemplate.exchange(this.rootUrl.toString() + path, httpMethod, entity, type);
    }

    public InputStream fetchFile(BlobPath path) throws IOException {
        URL url = new URL(this.rootUrl.toString() + path + "?download=true");
        HttpURLConnection uc = (HttpURLConnection) url.openConnection();

        uc.setRequestProperty("X-User-Id-Token", jwt);
        uc.setRequestProperty("Content-Type", "application/octet-stream");
        uc.setDoOutput(true);
        int responseCode = uc.getResponseCode();

        if (responseCode != HttpURLConnection.HTTP_OK) {
            throw new FileNotFoundException();
        }
        return uc.getInputStream();
    }

    public int fetchFile(BlobPath path, byte[] dst, int dstOffset, int len) throws IOException {
        URL url = new URL(this.rootUrl.toString() + path + "?download=true");
        HttpURLConnection uc = (HttpURLConnection) url.openConnection();
        InputStream is = null;
        try {
            uc.setRequestProperty("X-User-Id-Token", jwt);
            uc.setRequestProperty("Content-Type", "application/octet-stream");
            int responseCode = uc.getResponseCode();

            if (responseCode != HttpURLConnection.HTTP_OK) {
                throw new FileNotFoundException();
            }
            is = uc.getInputStream();
            is.read(dst, dstOffset, len);
        } catch (IOException e) {
            log.error("Failed while reading bytes from {}: {}", url.toExternalForm(), e.getMessage());
            e.printStackTrace();
        } finally {
            if (is != null) {
                is.close();
            }
        }
        return dst.length - dstOffset;
    }
//
//    public ByteArrayOutputStream fetchFile(BlobPath path) throws IOException {
//        int bytesRead = -1;
//        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(0);
//        InputStream inputStream = null;
//        if (path.endsWith("/")) {
//            path = new BlobPath(path.getFileSystem(), path.toString().substring(0, path.toString().length() - 2).getBytes());
//        }
//        URL url = new URL(this.rootUrl.toString() + path);
//        HttpURLConnection uc = (HttpURLConnection) url.openConnection();
//
//        try {
//            uc.setRequestProperty("X-User-Id-Token", jwt);
//            uc.setRequestProperty("Content-Type", "application/octet-stream");
//            int responseCode = uc.getResponseCode();
//
//            if (responseCode != HttpURLConnection.HTTP_OK) {
//                throw new FileNotFoundException();
//            }
//            inputStream = uc.getInputStream();
//            byte[] buffer = new byte[4096];
//            while ((bytesRead = inputStream.read(buffer)) != -1) {
//                outputStream.write(buffer, 0, bytesRead);
//            }
//            return outputStream;
//        } catch (Exception e) {
//            throw e;
//        } finally {
//            outputStream.close();
//            inputStream.close();
//            uc.disconnect();
//        }
//    }

    public void putFile(BlobPath path, final InputStream fis) throws FileNotFoundException {
        final RequestCallback requestCallback = request -> {
            request.getHeaders().setContentType(MediaType.APPLICATION_OCTET_STREAM);
            request.getHeaders().set("X-User-Id-Token", jwt);
            IOUtils.copy(fis, request.getBody());
        };
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setBufferRequestBody(false);
        restTemplate.setRequestFactory(requestFactory);
        final HttpMessageConverterExtractor<String> responseExtractor =
                new HttpMessageConverterExtractor<String>(String.class, restTemplate.getMessageConverters());

        restTemplate.execute(this.rootUrl.toString() + path, HttpMethod.POST, requestCallback, responseExtractor);
    }

    public int read(long fileOffset, byte[] dst, int dstOffset, int len, AtomicReference<Boolean> eofSignalled)
            throws IOException {
        if (eofSignalled != null) {
            eofSignalled.set(null);
        }

        byte[] id = UUID.randomUUID().toString().getBytes();
        Buffer buffer = new ByteArrayBuffer(id.length + Long.SIZE /* some extra fields */, false);
        buffer.putBytes(id);
        buffer.putLong(fileOffset);
        buffer.putInt(len);
//        eofSignalled.set(true);
        return buffer.getInt();
    }
}
