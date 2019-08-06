package com.opuscapita.s2p.blob.blobfilesystem.client;

import com.opuscapita.s2p.blob.blobfilesystem.BlobDirEntry;
import com.opuscapita.s2p.blob.blobfilesystem.BlobPath;
import com.opuscapita.s2p.blob.blobfilesystem.client.Exception.BlobException;
import com.opuscapita.s2p.blob.blobfilesystem.config.BlobConfiguration;
import lombok.Getter;
import org.apache.sshd.common.util.logging.AbstractLoggingBean;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

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

    public byte[] fetchFile(BlobPath path) {
        restTemplate.getMessageConverters().add(
                new ByteArrayHttpMessageConverter());
        if (path.endsWith("/")) {
            path = new BlobPath(path.getFileSystem(), path.toString().substring(0, path.toString().length() - 2).getBytes());
        }
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-User-Id-Token", jwt);
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_OCTET_STREAM));

        HttpEntity<String> entity = new HttpEntity<String>(headers);

        ResponseEntity<byte[]> response = restTemplate.exchange(
                this.rootUrl.toString() + path,
                HttpMethod.GET, entity, byte[].class);
        return response.getBody();
    }
}
