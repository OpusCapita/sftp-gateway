package com.opuscapita.blob.service;

import com.opuscapita.blob.model.Scope;

public interface BlobInterface {

    void listFiles(String tenantId, String path, Scope scope);
    void storeFile(String tenantId, String path, Scope scope);
    void readFile(String tenantId, String path, Scope scope);
    void getFileInfo(String tenantId, String path, Scope scope);
    void deleteFile(String tenantId, String path, Scope scope);
    void copyFile(String tenantId, String path, Scope scope);
    void moveFile(String tenantId, String path, Scope scope);
    void copyDirectory(String tenantId, String path, Scope scope);
    void moveDirectory(String tenantId, String path, Scope scope);
}
