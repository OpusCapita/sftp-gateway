package com.opuscapita.sftp.filesystem;

import org.apache.sshd.server.session.ServerSession;
import org.apache.sshd.server.subsystem.sftp.DirectoryHandle;
import org.apache.sshd.server.subsystem.sftp.FileHandle;
import org.apache.sshd.server.subsystem.sftp.SftpEventListenerManager;
import org.apache.sshd.server.subsystem.sftp.SftpFileSystemAccessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.channels.Channel;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.DirectoryStream;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.util.Set;

public class OCSftpFileSystemAccessor implements SftpFileSystemAccessor {

    protected final Logger log = LoggerFactory.getLogger(this.getClass());

    @Override
    public DirectoryStream<Path> openDirectory(ServerSession session, SftpEventListenerManager subsystem, DirectoryHandle dirHandle, Path dir, String handle) throws IOException {
        log.info("openDirectory " + dir.toString());
//        List<BlobResponse> responseList = new LinkedList<>();
//        try {
//            responseList = this.blobService.listFiles("/public/");
//        } catch (Throwable e) {
//            log.error(e.getMessage());
//        }
        return null;
    }

    @Override
    public SeekableByteChannel openFile(ServerSession session, SftpEventListenerManager subsystem, FileHandle fileHandle, Path file, String handle, Set<? extends OpenOption> options, FileAttribute<?>... attrs) throws IOException {
        log.info("openFile " + file.toString());
        return null;
    }
}
