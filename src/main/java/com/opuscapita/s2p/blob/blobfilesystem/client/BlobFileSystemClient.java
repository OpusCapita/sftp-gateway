package com.opuscapita.s2p.blob.blobfilesystem.client;

import com.opuscapita.s2p.blob.blobfilesystem.BlobPath;
import com.opuscapita.s2p.blob.blobfilesystem.client.Exception.BlobException;
import com.opuscapita.s2p.blob.blobfilesystem.config.BlobConfiguration;
import com.opuscapita.s2p.blob.blobfilesystem.file.BlobDirEntry;
import com.opuscapita.s2p.blob.blobfilesystem.utils.BlobUtils;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class BlobFileSystemClient {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Getter
    private RestTemplate restTemplate;
    @Getter
    private final URL rootUrl;
    private final URL moveUrl;
    private final URL cpyUrl;
    private final URL tierUrl;
    private final URL metadataUrl;
    private final BlobConfiguration configuration;
    private final String jwt;
    private HttpURLConnection connection = null;
    private long sizeInByte;

    public BlobFileSystemClient(
            RestTemplateBuilder _restTemplateBuilder,
            BlobConfiguration configuration,
            String tenant_id,
            String jwt
    ) throws MalformedURLException {
        this.restTemplate = _restTemplateBuilder.build();
        this.configuration = configuration;
        this.jwt = jwt;
        this.rootUrl = new URL("http://" + configuration.getUrl() + ":" + configuration.getPort() + "/api/" + tenant_id + "/files" + "/" + configuration.getAccess());
        this.moveUrl = new URL("http://" + configuration.getUrl() + ":" + configuration.getPort() + "/api/" + tenant_id + "/files" + "/move/" + configuration.getAccess());
        this.cpyUrl = new URL("http://" + configuration.getUrl() + ":" + configuration.getPort() + "/api/" + tenant_id + "/files" + "/copy/" + configuration.getAccess());
        this.tierUrl = new URL("http://" + configuration.getUrl() + ":" + configuration.getPort() + "/api/" + tenant_id + "/files" + "/tier/" + configuration.getAccess());
        this.metadataUrl = new URL("http://" + configuration.getUrl() + ":" + configuration.getPort() + "/api/" + tenant_id + "/files" + "/metadata/" + configuration.getAccess());
    }

    /**
     * @param path
     * @return
     * @throws BlobException
     */
    public Map<String, BlobDirEntry> listFiles(BlobPath path) throws BlobException {
        try {
            if (path.endsWith(".")) {
                path = path.getParent();
            } else if (path.endsWith("..")) {
                path = path.getParent().getParent();
            }
            if (!path.endsWith("/")) {
                path = new BlobPath(path.getFileSystem(), (path.toString() + "/").getBytes());
            }
            ResponseEntity<BlobDirEntry[]> result = get(path, BlobDirEntry[].class, HttpMethod.GET);
            Map<String, BlobDirEntry> listMap = new HashMap<>();
            for (BlobDirEntry entry : Objects.requireNonNull(result.getBody())) {
                listMap.put(entry.toString(), entry);
            }
            return listMap;
        } catch (Exception e) {
            throw new BlobException("Error occurred while trying to read the file list from blob service.");
        }
    }

    /**
     * @param path
     * @return
     * @throws BlobException
     */
    public BlobDirEntry listFile(BlobPath path) throws BlobException {
        try {
            ResponseEntity<String> result = get(path, String.class, HttpMethod.HEAD);
            return BlobDirEntry.fromJson(URLDecoder.decode(Objects.requireNonNull(result.getHeaders().getFirst("X-File-Info")), StandardCharsets.UTF_8.name()));
        } catch (Exception e) {
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
        if (!path.endsWith("/")) {
            path = new BlobPath(path.getFileSystem(), (path.toString() + "/").getBytes());
        }

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.set("X-User-Id-Token", jwt);
            HttpEntity<String> entity = new HttpEntity<>("body", headers);
            log.info("Setting http headers content type to application json");
            ResponseEntity<String> result = restTemplate.exchange(this.rootUrl.toString() + path + "?createMissing=" + createMissing, HttpMethod.PUT, entity, String.class);
            return BlobDirEntry.fromJson(URLDecoder.decode(Objects.requireNonNull(result.getHeaders().getFirst("X-File-Info")), StandardCharsets.UTF_8.name()));
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
    private <T> ResponseEntity<T> get(BlobPath path, Class<T> type, HttpMethod httpMethod) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        headers.set("X-User-Id-Token", jwt);
        HttpEntity<String> entity = new HttpEntity<>("body", headers);

        return restTemplate.exchange(this.rootUrl.toString() + path, httpMethod, entity, type);
    }

    /**
     * @param path
     * @param dst
     * @return
     * @throws IOException
     */
    public int fetchFile(BlobPath path, ByteBuffer dst) throws IOException {
        URL url = new URL(this.rootUrl.toString() + path + "?download=true");
        HttpURLConnection uc = this.openHttpUrlConnection(url, "GET");
        ReadableByteChannel readableByteChannel = Channels.newChannel(uc.getInputStream());

        int totalRead = 0;
        try {
            int read;
            while ((read = readableByteChannel.read(dst)) > 0) {
                totalRead += read;
            }
            this.sizeInByte = totalRead;
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        if (totalRead == 0) {
            return -1;
        }
        return (int) this.sizeInByte;
    }

    /**
     * Upload
     *
     * @param path
     * @param src
     * @return
     * @throws BlobException
     * @throws IOException
     */
    public int putFile(BlobPath path, ByteBuffer src) throws IOException {
        URL url = new URL(this.rootUrl.toString() + path + "?createMissing=true");
        this.openHttpUrlConnection(url, "PUT");
        this.connection.getOutputStream().write(src.array(), src.position(), src.limit() - src.position());
        this.sizeInByte += src.array().length;
        return (int) (this.sizeInByte);
    }

    /**
     * Delete file or directory
     *
     * @param path
     * @param dir
     * @throws BlobException
     */
    public void delete(BlobPath path, boolean dir) throws BlobException {
        try {
            URL url = new URL(this.rootUrl.toString() + path + "?recursive=true");
            if (dir && !path.toString().endsWith(BlobUtils.HTTP_PATH_SEPARATOR_STRING)) {
                url = new URL(this.rootUrl.toString() + path + BlobUtils.HTTP_PATH_SEPARATOR_STRING + "?recursive=true");
            }
            HttpURLConnection uc = openHttpUrlConnection(url, "DELETE");
            int responseCode = uc.getResponseCode();

            if (responseCode != HttpURLConnection.HTTP_ACCEPTED) {
                throw new BlobException(uc.getResponseMessage());
            }
        } catch (IOException e) {
            throw new BlobException(e.getMessage());
        }
    }

    public boolean copy(BlobPath src, BlobPath dst) throws BlobException {
        try {
            URL url = new URL(this.cpyUrl.toString() + src);
            String payload = "{\"dstPath\": \"" + "/" + this.configuration.getAccess() + dst.toString() + "\"}";

            HttpURLConnection uc = (HttpURLConnection) url.openConnection();
            uc.setRequestMethod("PUT");
            uc.setRequestProperty("X-User-Id-Token", jwt);
            uc.setRequestProperty("Content-Type", "application/json");
            uc.setDoOutput(true);
            OutputStreamWriter osw = new OutputStreamWriter(uc.getOutputStream());
            osw.write(payload);
            osw.flush();
            osw.close();

            int responseCode = uc.getResponseCode();

            if (responseCode != HttpURLConnection.HTTP_ACCEPTED) {
                throw new BlobException(uc.getResponseMessage());
            }
            return true;
        } catch (IOException | BlobException e) {
            throw new BlobException(e.getMessage());
        }
    }

    public boolean move(BlobPath src, BlobPath dst) throws BlobException {
        try {
            URL url = new URL(this.moveUrl.toString() + src);
            String payload = "{\"dstPath\": \"" + "/" + this.configuration.getAccess() + dst.toString() + "\"}";

            HttpURLConnection uc = (HttpURLConnection) url.openConnection();
            uc.setRequestMethod("PUT");
            uc.setRequestProperty("X-User-Id-Token", jwt);
            uc.setRequestProperty("Content-Type", "application/json");
            uc.setDoOutput(true);
            OutputStreamWriter osw = new OutputStreamWriter(uc.getOutputStream());
            osw.write(payload);
            osw.flush();
            osw.close();

            int responseCode = uc.getResponseCode();

            if (responseCode != HttpURLConnection.HTTP_ACCEPTED) {
                throw new BlobException(uc.getResponseMessage());
            }
            return true;
        } catch (IOException e) {
            throw new BlobException(e.getMessage());
        }
    }

    private HttpURLConnection openHttpUrlConnection(URL url, String requestMode) throws IOException {
        if (this.connection != null) {
            if (this.connection.getURL().equals(url) && this.connection.getRequestMethod().equals(requestMode)) {
                return this.connection;
            } else {
                this.closeHttpUrlConnection();
            }
        }
        this.sizeInByte = 0;
        this.connection = (HttpURLConnection) url.openConnection();
        if (requestMode != null && !requestMode.equals("GET")) {
            this.connection.setRequestMethod(requestMode);
            this.connection.setChunkedStreamingMode(BlobUtils.DEFAULT_COPY_SIZE);
        }
        this.connection.setRequestProperty("X-User-Id-Token", jwt);
        this.connection.setRequestProperty("Content-Type", "application/octet-stream");
        this.connection.setDoInput(true);
        this.connection.setDoOutput(true);
        this.connection.setUseCaches(true);
        this.connection.connect();
        return this.connection;
    }

    /**
     * @throws IOException
     */
    public void closeHttpUrlConnection() throws IOException {
        if (this.connection != null) {
            int responseCode = this.connection.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_ACCEPTED
                    && responseCode != HttpURLConnection.HTTP_OK
                    && responseCode != HttpURLConnection.HTTP_CREATED) {
                log.warn("Not OK");
            }

            this.connection.disconnect();
            this.connection = null;
        }
        this.sizeInByte = 0;
    }
}
