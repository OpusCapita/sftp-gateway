package com.opuscapita.s2p.blob.blobfilesystem.client;

import com.opuscapita.s2p.blob.blobfilesystem.BlobDirEntry;
import com.opuscapita.s2p.blob.blobfilesystem.BlobPath;
import com.opuscapita.s2p.blob.blobfilesystem.client.Exception.BlobException;
import lombok.Getter;
import org.apache.sshd.common.util.logging.AbstractLoggingBean;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.net.URL;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

//@Service
public class BlobFileSystemClient extends AbstractLoggingBean {
    //    @Autowired
//    private BlobConfiguration configuration;
    @Getter
    private RestTemplate restTemplate;
    @Getter
    private final URL rootUrl;

    public BlobFileSystemClient(RestTemplateBuilder _restTemplateBuilder, URL rootUrl) {
        this.restTemplate = _restTemplateBuilder.build();
        this.rootUrl = rootUrl;
    }

    /**
     * @param path
     * @param jwt
     * @return
     * @throws BlobException
     */
    public Map<String, BlobDirEntry> listFiles(BlobPath path, String jwt) throws BlobException {
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
            ResponseEntity<BlobDirEntry[]> result = get(path, jwt, BlobDirEntry[].class, HttpMethod.GET);
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
     * @param jwt
     * @return
     * @throws BlobException
     */
    public BlobDirEntry listFile(BlobPath path, String jwt) throws BlobException {
        log.debug("File requested from blob service: " + path.toString());
        try {
            ResponseEntity<String> result = get(path, jwt, String.class, HttpMethod.HEAD);
            log.info("File fetched successfully from blob service: " + path.toString());
            return BlobDirEntry.fromJson(URLDecoder.decode(result.getHeaders().getFirst("X-File-Info")));
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new BlobException("Error occurred while trying to read the file from blob service.");
        }
    }

    /**
     * @param path
     * @param jwt
     * @param createMissing
     * @return
     * @throws BlobException
     */
    public BlobDirEntry createDirectory(BlobPath path, String jwt, boolean createMissing) throws BlobException {
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
            return BlobDirEntry.fromJson(URLDecoder.decode(result.getHeaders().getFirst("X-File-Info")));
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new BlobException("Error occurred while trying to create the Directory.");
        }
    }

    /**
     * @param path
     * @param jwt
     * @param type
     * @param httpMethod
     * @param <T>
     * @return
     * @throws Exception
     */

    private <T> ResponseEntity<T> get(BlobPath path, String jwt, Class<T> type, HttpMethod httpMethod) throws Exception {
        log.info("Reading file from endpoint: " + path);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.set("X-User-Id-Token", jwt);
        HttpEntity<String> entity = new HttpEntity<>("body", headers);
        log.info("Setting http headers content type to application json");

        return restTemplate.exchange(this.rootUrl.toString() + path, httpMethod, entity, type);
    }
}
