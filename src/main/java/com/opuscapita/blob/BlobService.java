package com.opuscapita.blob;

import com.opuscapita.auth.model.AuthResponse;
import com.opuscapita.blob.Exception.BlobException;
import com.opuscapita.blob.config.BlobConfiguration;
import com.opuscapita.blob.model.BlobResponse;
import com.opuscapita.blob.model.Scope;
import com.opuscapita.blob.service.BlobInterface;
import lombok.Getter;
import lombok.Setter;
import org.apache.sshd.common.subsystem.sftp.SftpException;
import org.apache.sshd.common.util.logging.AbstractLoggingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;


@Component
@ComponentScan
public class BlobService extends AbstractLoggingBean implements BlobInterface {

    @Getter
    @Autowired
    private RestTemplate restTemplate;

    @Getter
    @Setter
    private AuthResponse authResponse;

    @Getter
    @Autowired
    private BlobConfiguration blobConfiguration;

    @Autowired
    public BlobService(BlobConfiguration _blobConfig, RestTemplateBuilder _restTemplateBuilder) {
        this.blobConfiguration = _blobConfig;
        this.restTemplate = _restTemplateBuilder.build();
    }

    @Override
    public List<BlobResponse> listFiles(String path) throws BlobException, SftpException {
        log.debug("File list requested from blob service for folder: " + path);
        if (!this.isAuthenticated()) {
            throw new SftpException(HttpStatus.UNAUTHORIZED.value(), "You are not authorized");
        }
        try {
            ResponseEntity<BlobResponse[]> result = get(path, BlobResponse[].class);
            log.debug("File list fetched successfully from blob service for folder: " + path);
            return new ArrayList<>(Arrays.asList(result.getBody()));
        } catch (Exception e) {
            throw new BlobException("Error occurred while trying to read the file list from blob service.");
        }
    }

    @Override
    public BlobResponse storeFile(InputStream data, String path) throws BlobException, SftpException {
        log.info("File storage requested from blob service to path: " + path);
        try {
            String endpoint = getEndpoint(path);
            log.info("Putting file to endpoint: " + endpoint);

            HttpHeaders headers = new HttpHeaders();
            headers.set("Transfer-Encoding", "chunked");
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.set("X-User-Id-Token", this.authResponse.getId_token());
            HttpEntity<Resource> entity = new HttpEntity<>(new InputStreamResource(data), headers);
            log.info("Wrapped and set the request body as input stream");
            ResponseEntity<BlobResponse> result = restTemplate.exchange(endpoint, HttpMethod.PUT, entity, BlobResponse.class);
            log.info("File stored successfully to blob service path: " + path);
            return result.getBody();
        } catch (Exception e) {
            throw new BlobException("Error occurred while trying to put the file to blob service");
        }
    }

    @Override
    public void readFile(String tenantId, String path, Scope scope) {
        throw new UnsupportedOperationException("readFile is not implemented");
    }

    @Override
    public void getFileInfo(String tenantId, String path, Scope scope) {
        throw new UnsupportedOperationException("getFileInfo is not implemented");

    }

    @Override
    public void deleteFile(String tenantId, String path, Scope scope) {
        throw new UnsupportedOperationException("deleteFile is not implemented");

    }

    @Override
    public void copyFile(String tenantId, String path, Scope scope) {
        throw new UnsupportedOperationException("copyFile is not implemented");

    }

    @Override
    public void moveFile(String tenantId, String path, Scope scope) {
        throw new UnsupportedOperationException("moveFile is not implemented");

    }

    @Override
    public void copyDirectory(String tenantId, String path, Scope scope) {
        throw new UnsupportedOperationException("copyDirectory is not implemented");

    }

    @Override
    public void moveDirectory(String tenantId, String path, Scope scope) {
        throw new UnsupportedOperationException("moveDirectory is not implemented");

    }

    private <T> ResponseEntity<T> get(String path, Class<T> type) throws Exception {
        log.info("Reading file from endpoint: " + path);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.set("X-User-Id-Token", this.authResponse.getId_token());
        HttpEntity<String> entity = new HttpEntity<>("body", headers);
        log.info("Setting http headers content type to application json");

        return restTemplate.exchange(this.getEndpoint(path), HttpMethod.GET, entity, type);
    }

    private boolean isAuthenticated() {
        return this.authResponse != null;
    }

    private String getEndpoint(String path) {

        return UriComponentsBuilder
                .fromUriString(this.getBlobConfiguration().getMethod() + "://" + this.getBlobConfiguration().getUrl())
                .port(this.getBlobConfiguration().getPort())
                .path("/api/" + this.getAuthResponse().getTenantId() + "/" + this.getBlobConfiguration().getType() + path)
                .queryParam("inline", "true")
                .queryParam("createMissing", "true")
                .queryParam("recursive", "false")
                .toUriString();
    }
}
