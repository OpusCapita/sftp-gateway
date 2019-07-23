package com.opuscapita.sftp.service.commands;

import com.opuscapita.auth.model.AuthResponse;
import com.opuscapita.blob.BlobService;
import com.opuscapita.sftp.utils.SFTPHelper;
import org.apache.sshd.common.AttributeRepository;
import org.apache.sshd.common.util.buffer.Buffer;
import org.apache.sshd.common.util.threads.CloseableExecutorService;
import org.apache.sshd.server.subsystem.sftp.SftpErrorStatusDataHandler;
import org.apache.sshd.server.subsystem.sftp.SftpFileSystemAccessor;
import org.apache.sshd.server.subsystem.sftp.SftpSubsystem;
import org.apache.sshd.server.subsystem.sftp.UnsupportedAttributePolicy;

import java.io.IOException;
import java.nio.file.FileSystem;

public class OCSftpSubsystem extends SftpSubsystem {
    private BlobService blobService;

    public OCSftpSubsystem(
            CloseableExecutorService executorService,
            UnsupportedAttributePolicy policy,
            SftpFileSystemAccessor accessor,
            SftpErrorStatusDataHandler errorStatusDataHandler,
            BlobService _blobService
    ) {
        super(executorService, policy, accessor, errorStatusDataHandler);
        this.blobService = _blobService;
    }

    @Override
    protected void doRealPath(Buffer buffer, int id) throws IOException {
        if (!this.getServerSession().isAuthenticated()) {
            throw new IOException();
        }
        this.blobService.setAuthResponse(this.getAuthResponse());
        super.doRealPath(buffer, id);
    }

    @Override
    public void setFileSystem(FileSystem fileSystem) {
        if (fileSystem != this.fileSystem) {
            this.fileSystem = fileSystem;
            this.defaultDir = fileSystem.getPath("/").toAbsolutePath().normalize();
        }
    }

    private AuthResponse getAuthResponse() {
        AttributeRepository.AttributeKey<AuthResponse> authResponseAttributeKey = SFTPHelper.findAttributeKey(getServerSession(), AuthResponse.class);
        AuthResponse authResponse = getServerSession().getAttribute(authResponseAttributeKey);
        return authResponse;
    }

}
