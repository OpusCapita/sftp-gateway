package com.opuscapita.sftp.service.commands;

import com.opuscapita.auth.model.AuthResponse;
import com.opuscapita.blob.BlobService;
import com.opuscapita.s2p.blob.blobfilesystem.BlobFileSystem;
import com.opuscapita.s2p.blob.blobfilesystem.BlobPath;
import com.opuscapita.sftp.utils.SFTPHelper;
import org.apache.sshd.common.AttributeRepository;
import org.apache.sshd.common.util.SelectorUtils;
import org.apache.sshd.common.util.buffer.Buffer;
import org.apache.sshd.common.util.threads.CloseableExecutorService;
import org.apache.sshd.server.subsystem.sftp.SftpErrorStatusDataHandler;
import org.apache.sshd.server.subsystem.sftp.SftpFileSystemAccessor;
import org.apache.sshd.server.subsystem.sftp.SftpSubsystem;
import org.apache.sshd.server.subsystem.sftp.UnsupportedAttributePolicy;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;

public class OCSftpSubsystem extends SftpSubsystem {

    private BlobPath defaultDir = ((BlobFileSystem)fileSystem).getDefaultDir();

    public OCSftpSubsystem(
            CloseableExecutorService executorService,
            UnsupportedAttributePolicy policy,
            SftpFileSystemAccessor accessor,
            SftpErrorStatusDataHandler errorStatusDataHandler
    ) {
        super(executorService, policy, accessor, errorStatusDataHandler);
    }

    @Override
    protected void doRealPath(Buffer buffer, int id) throws IOException {
        if (!this.getServerSession().isAuthenticated()) {
            throw new IOException();
        }
        super.doRealPath(buffer, id);
    }

    @Override
    public void setFileSystem(FileSystem fileSystem) {
        if (fileSystem != this.fileSystem) {
            this.fileSystem = fileSystem;
        }
    }

    @Override
    public BlobPath getDefaultDirectory() {
        return defaultDir;
    }

    @Override
    protected Path resolveFile(String remotePath) throws IOException, InvalidPathException {
        BlobPath defaultDir = getDefaultDirectory();
        String path = SelectorUtils.translateToLocalFileSystemPath(remotePath, '/', defaultDir.getFileSystem());
        Path p = defaultDir.resolve(path);
        if (log.isTraceEnabled()) {
            log.trace("resolveFile({}) {} => {}", getServerSession(), remotePath, p);
        }
        return p;
    }

    private AuthResponse getAuthResponse() {
        AttributeRepository.AttributeKey<AuthResponse> authResponseAttributeKey = SFTPHelper.findAttributeKey(getServerSession(), AuthResponse.class);
        AuthResponse authResponse = getServerSession().getAttribute(authResponseAttributeKey);
        return authResponse;
    }

}
