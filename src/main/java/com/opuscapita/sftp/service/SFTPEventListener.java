package com.opuscapita.sftp.service;

import com.opuscapita.auth.model.AuthResponse;
import com.opuscapita.sftp.service.uploadlistener.FileUploadCompleteListener;
import com.opuscapita.sftp.utils.SFTPHelper;
import com.opuscapita.transaction.service.TxService;
import lombok.Getter;
import org.apache.sshd.common.AttributeRepository;
import org.apache.sshd.server.session.ServerSession;
import org.apache.sshd.server.subsystem.sftp.AbstractSftpEventListenerAdapter;
import org.apache.sshd.server.subsystem.sftp.DirectoryHandle;
import org.apache.sshd.server.subsystem.sftp.Handle;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class SFTPEventListener extends AbstractSftpEventListenerAdapter {
    @Getter
    private AuthResponse authResponse;

    private final SFTPDaemon service;

    public SFTPEventListener(SFTPDaemon _service) {
        super();
        this.service = _service;
    }

    private List<FileUploadCompleteListener> fileReadyListeners = new ArrayList<>();

    public void addFileUploadCompleteListener(FileUploadCompleteListener listener) {
        fileReadyListeners.add(listener);
    }

    public void removeFileUploadCompleteListener(FileUploadCompleteListener listener) {
        fileReadyListeners.remove(listener);
    }

    @Override
    public void initialized(ServerSession session, int version) {
        /*
        Loading the backend filesystem from Azure Blob container
         */
        super.initialized(session, version);

        AttributeRepository.AttributeKey<AuthResponse> authResponseAttributeKey = SFTPHelper.findAttributeKey(session, AuthResponse.class);
        this.authResponse = session.getAttribute(authResponseAttributeKey);
    }

    @Override
    public void closed(ServerSession session, String remoteHandle, Handle localHandle, Throwable thrown) throws IOException {
        Path path = localHandle.getFile();

        log.info(String.format("User %s closed file: \"%s\"", session.getUsername(), localHandle.getFile().toAbsolutePath()));
        if (!(localHandle instanceof DirectoryHandle)) {
            for (FileUploadCompleteListener fileReadyListener : fileReadyListeners) {
                fileReadyListener.onPathReady(path, session);
            }
        }
    }

    @Override
    public void destroying(ServerSession session) {
        for (FileUploadCompleteListener fileReadyListener : fileReadyListeners) {
            this.removeFileUploadCompleteListener(fileReadyListener);
        }
    }

    @Override
    public void removing(ServerSession session, Path path, boolean isDirectory) throws IOException {
        super.removing(session, path, isDirectory);
        log.info("{} removing", path);
    }

    @Override
    public void opening(ServerSession session, String remoteHandle, Handle localHandle) {
        log.info("opening");
    }

    @Override
    public void open(ServerSession session, String remoteHandle, Handle localHandle) {
        log.info("open");
        AttributeRepository.AttributeKey<TxService> txServiceAttributeKey = new AttributeRepository.AttributeKey<>();
        session.setAttribute(txServiceAttributeKey, new TxService(this.service.getKafkaTemplate()));
    }

    @Override
    public void openFailed(ServerSession session, String remotePath, Path localPath, boolean isDirectory, Throwable thrown) {
        log.info("openFailed");
    }
}
