package com.opuscapita.sftp.service;

import com.opuscapita.auth.model.AuthResponse;
import com.opuscapita.sftp.utils.SFTPHelper;
import com.opuscapita.transaction.service.TxService;
import lombok.Getter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.sshd.common.AttributeRepository;
import org.apache.sshd.server.session.ServerSession;
import org.apache.sshd.server.subsystem.sftp.AbstractSftpEventListenerAdapter;
import org.apache.sshd.server.subsystem.sftp.DirectoryHandle;
import org.apache.sshd.server.subsystem.sftp.FileHandle;
import org.apache.sshd.server.subsystem.sftp.Handle;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SFTPEventListener extends AbstractSftpEventListenerAdapter {
    private Log log = LogFactory.getLog(SFTPEventListener.class);
    private AttributeRepository.AttributeKey<AuthResponse> authResponseAttributeKey;
    @Getter
    private AuthResponse authResponse;

    private final SFTPDaemon service;

    public SFTPEventListener(SFTPDaemon _service) {
        super();
        this.service = _service;
    }

    public interface FileUploadCompleteListener {
        void onPathReady(Path file);
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
        AuthResponse authResponse = session.getAttribute(authResponseAttributeKey);
        this.authResponse = authResponse;
        log.info("initialized with AccessToken: " + authResponse.getAccess_token());
    }

    @Override
    public void closed(ServerSession session, String remoteHandle, Handle localHandle, Throwable thrown) throws IOException {
        Path file = localHandle.getFile();

        log.info(String.format("User %s closed file: \"%s\"", session.getUsername(), localHandle.getFile().toAbsolutePath()));
        if (!(localHandle instanceof DirectoryHandle)) {
            for (FileUploadCompleteListener fileReadyListener : fileReadyListeners) {
                fileReadyListener.onPathReady(file);
            }
        }
    }

    @Override
    public void destroying(ServerSession session) {
        log.info(session.getActiveSessionCountForUser(session.getUsername()));
        log.info("destroying");
    }

    @Override
    public void opening(ServerSession session, String remoteHandle, Handle localHandle) {
        log.info("opening");
    }

    @Override
    public void open(ServerSession session, String remoteHandle, Handle localHandle) {
        log.info("open");
        AttributeRepository.AttributeKey<TxService> txServiceAttributeKey = new AttributeRepository.AttributeKey();
        session.setAttribute(txServiceAttributeKey, new TxService(this.service.getKafkaTemplate()));
    }

    @Override
    public void openFailed(ServerSession session, String remotePath, Path localPath, boolean isDirectory, Throwable thrown) {
        log.info("openFailed");
    }

    @Override
    public void writing(
            ServerSession session, String remoteHandle, FileHandle localHandle,
            long offset, byte[] data, int dataOffset, int dataLen) {
//        log.info("write(" + session + ")[" + localHandle.getFile() + "] offset=" + offset + ", requested=" + dataLen);
    }

    @Override
    public void written(
            ServerSession session, String remoteHandle, FileHandle localHandle,
            long offset, byte[] data, int dataOffset, int dataLen, Throwable thrown) {
//        log.info("written(" + session + ")[" + localHandle.getFile() + "] offset=" + offset + ", requested=" + dataLen);
    }

    @Override
    public void creating(ServerSession session, Path path, Map<String, ?> attrs) throws IOException {
        super.creating(session, path, attrs);
        log.info("creating(" + session + ")[" + path.toString() + "]");
    }

    @Override
    public void created(ServerSession session, Path path, Map<String, ?> attrs, Throwable thrown) throws IOException {
        super.created(session, path, attrs, thrown);
        log.info("created(" + session + ")[" + path.toString() + "]");
    }

    @Override
    public void read(ServerSession session, String remoteHandle, DirectoryHandle localHandle, Map<String, Path> entries) throws IOException {
        super.read(session, remoteHandle, localHandle, entries);
        log.info("List Files: " + entries.toString());
//        throw new AccessDeniedException(remoteHandle);
    }

    @Override
    public void read(ServerSession session, String remoteHandle, FileHandle localHandle, long offset, byte[] data, int dataOffset, int dataLen, int readLen, Throwable thrown) throws IOException {
//        super.read(session, remoteHandle, localHandle, offset, data, dataOffset, dataLen, readLen, thrown);
        log.info("Get File: " + remoteHandle);
    }

    @Override
    public void reading(ServerSession session, String remoteHandle, FileHandle localHandle, long offset, byte[] data, int dataOffset, int dataLen) throws IOException {
//        super.reading(session, remoteHandle, localHandle, offset, data, dataOffset, dataLen);
        log.info("Reading File: " + remoteHandle + ", " + localHandle.getFile().toAbsolutePath().toString());
    }
}
