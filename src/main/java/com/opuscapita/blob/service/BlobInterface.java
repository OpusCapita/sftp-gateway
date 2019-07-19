package com.opuscapita.blob.service;

import com.opuscapita.blob.Exception.BlobException;
import com.opuscapita.blob.model.BlobResponse;
import com.opuscapita.blob.model.Scope;
import org.apache.sshd.common.subsystem.sftp.SftpException;

import java.io.InputStream;
import java.util.List;

public interface BlobInterface {

    List<BlobResponse> listFiles(String path) throws BlobException, SftpException;

    BlobResponse storeFile(InputStream data, String path) throws BlobException, SftpException;

    void readFile(String tenantId, String path, Scope scope);

    void getFileInfo(String tenantId, String path, Scope scope);

    void deleteFile(String tenantId, String path, Scope scope);

    void copyFile(String tenantId, String path, Scope scope);

    void moveFile(String tenantId, String path, Scope scope);

    void copyDirectory(String tenantId, String path, Scope scope);

    void moveDirectory(String tenantId, String path, Scope scope);
}
