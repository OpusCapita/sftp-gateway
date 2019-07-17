package com.opuscapita.blob;

import com.opuscapita.auth.model.AuthResponse;
import com.opuscapita.blob.model.Scope;
import com.opuscapita.blob.service.BlobInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class BlobClient implements BlobInterface {

    private RestTemplate restTemplate;
    private AuthResponse authResponse;

//    @Autowired
//    private BlobClient(AuthResponse _authResponse, RestTemplateBuilder _restTemplateBuilder) {
//        this.authResponse = _authResponse;
//        this.restTemplate = _restTemplateBuilder.build();
//    }

    public static BlobClient createTempFileClient(AuthResponse _response) {
        return new BlobClient();
    }

    public static BlobClient createDataFileClient(AuthResponse _response) {
        return new BlobClient();
    }

    public static BlobClient createBlobClient(AuthResponse _response) {
        return new BlobClient();
    }

    @Override
    public void listFiles(String tenantId, String path, Scope scope) {

    }

    @Override
    public void storeFile(String tenantId, String path, Scope scope) {

    }

    @Override
    public void readFile(String tenantId, String path, Scope scope) {

    }

    @Override
    public void getFileInfo(String tenantId, String path, Scope scope) {

    }

    @Override
    public void deleteFile(String tenantId, String path, Scope scope) {

    }

    @Override
    public void copyFile(String tenantId, String path, Scope scope) {

    }

    @Override
    public void moveFile(String tenantId, String path, Scope scope) {

    }

    @Override
    public void copyDirectory(String tenantId, String path, Scope scope) {

    }

    @Override
    public void moveDirectory(String tenantId, String path, Scope scope) {

    }
}
