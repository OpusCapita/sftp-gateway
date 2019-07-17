package com.opuscapita.sftp.service.commands;

import org.apache.sshd.common.util.buffer.Buffer;
import org.apache.sshd.common.util.threads.CloseableExecutorService;
import org.apache.sshd.server.subsystem.sftp.*;

import java.io.IOException;
import java.nio.file.LinkOption;

public class OCSftpSubsystem extends SftpSubsystem {
    public OCSftpSubsystem(
            CloseableExecutorService executorService,
            UnsupportedAttributePolicy policy,
            SftpFileSystemAccessor accessor,
            SftpErrorStatusDataHandler errorStatusDataHandler
    ) {
        super(executorService, policy, accessor, errorStatusDataHandler);
    }

    @Override
    protected int doReadDir(int id, String handle, DirectoryHandle dir, Buffer buffer, int maxSize, LinkOption... options) throws IOException {
        return super.doReadDir(id, handle, dir, buffer, maxSize, options);
    }
}
