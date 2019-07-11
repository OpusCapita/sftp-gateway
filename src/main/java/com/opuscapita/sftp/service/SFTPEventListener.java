package com.opuscapita.sftp.service;

import com.opuscapita.auth.model.AuthResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.sshd.common.AttributeRepository;
import org.apache.sshd.server.session.ServerSession;
import org.apache.sshd.server.subsystem.sftp.AbstractSftpEventListenerAdapter;
import org.apache.sshd.server.subsystem.sftp.FileHandle;
import org.apache.sshd.server.subsystem.sftp.Handle;

import java.nio.file.Path;

public class SFTPEventListener extends AbstractSftpEventListenerAdapter {
    private Log log = LogFactory.getLog(SFTPEventListener.class);
    private AttributeRepository.AttributeKey<AuthResponse> authResponseAttributeKey;

    public SFTPEventListener() {
        super();
    }

    @Override
    public void initialized(ServerSession session, int version) {
        /*
        Loading the backend filesystem from Azure Blob container
         */
        super.initialized(session, version);
        AttributeRepository.AttributeKey<AuthResponse> authResponseAttributeKey = this.findAttributeKey(session, AuthResponse.class);
        AuthResponse authResponse = session.getAttribute(authResponseAttributeKey);
        log.info("initialized with AccessToken: " + authResponse.getAccess_token());
    }

    @Override
    public void destroying(ServerSession session) {
        /*
        Removing the backend filesystem safely
         */
        log.info("destroying");
    }

    @Override
    public void opening(ServerSession session, String remoteHandle, Handle localHandle) {
        log.info("opening");
    }

    @Override
    public void open(ServerSession session, String remoteHandle, Handle localHandle) {
        log.info("open");
    }

    @Override
    public void openFailed(ServerSession session, String remotePath, Path localPath, boolean isDirectory, Throwable thrown) {
        log.info("openFailed");
    }

    @Override
    public void writing(
            ServerSession session, String remoteHandle, FileHandle localHandle,
            long offset, byte[] data, int dataOffset, int dataLen) {
        log.info("write(" + session + ")[" + localHandle.getFile() + "] offset=" + offset + ", requested=" + dataLen);
    }

    @Override
    public void written(
            ServerSession session, String remoteHandle, FileHandle localHandle,
            long offset, byte[] data, int dataOffset, int dataLen, Throwable thrown) {
        log.info("written(" + session + ")[" + localHandle.getFile() + "] offset=" + offset + ", requested=" + dataLen);
    }

    private AttributeRepository.AttributeKey findAttributeKey(ServerSession session, Class instanceOf) {
        for (AttributeRepository.AttributeKey attributeKey : session.attributeKeys()) {
            if (instanceOf.isInstance(session.getAttribute(attributeKey))) {
                return attributeKey;
            }
        }
        return null;
    }
}
