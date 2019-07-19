package com.opuscapita.sftp.service;

import com.opuscapita.auth.model.AuthResponse;
import com.opuscapita.sftp.utils.SFTPHelper;
import lombok.Getter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.sshd.common.AttributeRepository;
import org.apache.sshd.server.session.ServerSession;
import org.apache.sshd.server.subsystem.sftp.AbstractSftpEventListenerAdapter;
import org.apache.sshd.server.subsystem.sftp.DirectoryHandle;
import org.apache.sshd.server.subsystem.sftp.FileHandle;
import org.apache.sshd.server.subsystem.sftp.Handle;

import java.io.File;
import java.io.IOException;
import java.nio.file.CopyOption;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class SFTPEventListener extends AbstractSftpEventListenerAdapter {
    private Log log = LogFactory.getLog(SFTPEventListener.class);
    private AttributeRepository.AttributeKey<AuthResponse> authResponseAttributeKey;
    @Getter
    private AuthResponse authResponse;
    public SFTPEventListener() {
        super();
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


    public interface FileUploadCompleteListener {
        void onFileReady(File file);

        void onFileReadyForUpload(File file);
    }

    private List<FileUploadCompleteListener> fileReadyListeners = new ArrayList<>();

    public void addFileUploadCompleteListener(FileUploadCompleteListener listener) {
        fileReadyListeners.add(listener);
    }

    public void removeFileUploadCompleteListener(FileUploadCompleteListener listener) {
        fileReadyListeners.remove(listener);
    }

    @Override
    public void destroying(ServerSession serverSession) {
        log.info("destroying");

    }

    @Override
    public void open(ServerSession serverSession, String remoteHandle, Handle localHandle) {
        /**
         * LS
         */
        File openedFile = localHandle.getFile().toFile();
        log.info("open " + remoteHandle);
    }

    @Override
    public void read(ServerSession serverSession, String remoteHandle, DirectoryHandle localHandle, Map<String, Path> entries) {
        log.info("read " + remoteHandle + " - " + localHandle.toString());
    }

    @Override
    public void blocking(ServerSession serverSession, String remoteHandle, FileHandle localHandle, long offset, long length, int mask) {
    }

    @Override
    public void blocked(ServerSession serverSession, String remoteHandle, FileHandle localHandle, long offset, long length, int mask, Throwable thrown) {
    }

    @Override
    public void unblocking(ServerSession serverSession, String remoteHandle, FileHandle localHandle, long offset, long length) {
    }

    @Override
    public void writing(ServerSession session, String remoteHandle, FileHandle localHandle, long offset, byte[] data, int dataOffset, int dataLen) throws IOException {
//        super.writing(session, remoteHandle, localHandle, offset, data, dataOffset, dataLen);
        log.info(String.format("User %s writing file: \"%s\"", session.getUsername(), localHandle.getFile().toAbsolutePath()));
    }

    @Override
    public void written(ServerSession session, String remoteHandle, FileHandle localHandle, long offset, byte[] data, int dataOffset, int dataLen, Throwable thrown) throws IOException {
//        super.written(session, remoteHandle, localHandle, offset, data, dataOffset, dataLen, thrown);
        log.info(String.format("User %s written file: \"%s\"", session.getUsername(), localHandle.getFile().toAbsolutePath()));
    }

    @Override
    public void opening(ServerSession session, String remoteHandle, Handle localHandle) throws IOException {
        File toUpload = localHandle.getFile().toFile();
        log.info("PUT file " + toUpload);
    }

    @Override
    public void closing(ServerSession serverSession, String remoteHandle, Handle localHandle) {
        File closedFile = localHandle.getFile().toFile();
        if (closedFile.exists() && closedFile.isFile()) {
            log.info(String.format("User %s closed file: \"%s\"", serverSession.getUsername(), localHandle.getFile().toAbsolutePath()));

            for (FileUploadCompleteListener fileReadyListener : fileReadyListeners) {
                fileReadyListener.onFileReady(closedFile);
            }
        }
    }

    @Override
    public void created(ServerSession serverSession, Path path, Map<String, ?> attrs, Throwable thrown) {
        String username = serverSession.getUsername();
        log.info(String.format("User %s created: \"%s\"", username, path.toString()));
    }

    @Override
    public void moved(ServerSession session, Path srcPath, Path dstPath, Collection<CopyOption> opts, Throwable thrown) throws IOException {
        String username = session.getUsername();
        log.info(String.format("User %s moved: \"%s\" to \"%s\"", username, srcPath.toString(), dstPath.toString()));
    }

    @Override
    public void linking(ServerSession serverSession, Path source, Path target, boolean symLink) throws UnsupportedOperationException {
        log.warn(String.format("Blocked user %s attempt to create a link to \"%s\" at \"%s\"", serverSession.getUsername(), target.toString(), source.toString()));
        throw new UnsupportedOperationException("Creating links is not permitted");
    }

    @Override
    public void linked(ServerSession serverSession, Path source, Path target, boolean symLink, Throwable thrown) {

    }

    @Override
    public void modifyingAttributes(ServerSession serverSession, Path path, Map<String, ?> attrs) {

    }

    @Override
    public void modifiedAttributes(ServerSession serverSession, Path path, Map<String, ?> attrs, Throwable thrown) {
        String username = serverSession.getUsername();
    }

//    @Override
//    public void destroying(ServerSession session) {
//        log.info(session.getActiveSessionCountForUser(session.getUsername()));
//        log.info("destroying");
//    }
//
//    @Override
//    public void opening(ServerSession session, String remoteHandle, Handle localHandle) {
//        log.info("opening");
//    }
//
//    @Override
//    public void open(ServerSession session, String remoteHandle, Handle localHandle) {
//        log.info("open");
//    }
//
//    @Override
//    public void openFailed(ServerSession session, String remotePath, Path localPath, boolean isDirectory, Throwable thrown) {
//        log.info("openFailed");
//    }
//
//    @Override
//    public void writing(
//            ServerSession session, String remoteHandle, FileHandle localHandle,
//            long offset, byte[] data, int dataOffset, int dataLen) {
//        log.info("write(" + session + ")[" + localHandle.getFile() + "] offset=" + offset + ", requested=" + dataLen);
//    }
//
//    @Override
//    public void written(
//            ServerSession session, String remoteHandle, FileHandle localHandle,
//            long offset, byte[] data, int dataOffset, int dataLen, Throwable thrown) {
//        log.info("written(" + session + ")[" + localHandle.getFile() + "] offset=" + offset + ", requested=" + dataLen);
//    }
//
//    @Override
//    public void creating(ServerSession session, Path path, Map<String, ?> attrs) throws IOException {
//        super.creating(session, path, attrs);
//        log.info("creating(" + session + ")[" + path.toString() + "]");
//    }
//
//    @Override
//    public void created(ServerSession session, Path path, Map<String, ?> attrs, Throwable thrown) throws IOException {
//        super.created(session, path, attrs, thrown);
//        log.info("created(" + session + ")[" + path.toString() + "]");
//    }
//
//    @Override
//    public void read(ServerSession session, String remoteHandle, DirectoryHandle localHandle, Map<String, Path> entries) throws IOException {
//        super.read(session, remoteHandle, localHandle, entries);
//        log.info("List Files: " + entries.toString());
////        throw new AccessDeniedException(remoteHandle);
//    }
//
//    @Override
//    public void read(ServerSession session, String remoteHandle, FileHandle localHandle, long offset, byte[] data, int dataOffset, int dataLen, int readLen, Throwable thrown) throws IOException {
////        super.read(session, remoteHandle, localHandle, offset, data, dataOffset, dataLen, readLen, thrown);
//        log.info("Get File: " + remoteHandle);
//    }
//
//    @Override
//    public void reading(ServerSession session, String remoteHandle, FileHandle localHandle, long offset, byte[] data, int dataOffset, int dataLen) throws IOException {
////        super.reading(session, remoteHandle, localHandle, offset, data, dataOffset, dataLen);
//        log.info("Reading File: " + remoteHandle + ", " + localHandle.getFile().toAbsolutePath().toString());
//    }
}
