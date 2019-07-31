package com.opuscapita.sftp.service.commands;

import org.apache.sshd.common.util.logging.AbstractLoggingBean;
import org.apache.sshd.server.session.ServerSession;
import org.apache.sshd.server.subsystem.sftp.DirectoryHandle;
import org.apache.sshd.server.subsystem.sftp.FileHandle;
import org.apache.sshd.server.subsystem.sftp.SftpEventListenerManager;
import org.apache.sshd.server.subsystem.sftp.SftpFileSystemAccessor;

import java.io.IOException;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.DirectoryStream;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.util.Set;

public class OCRestFileSystemAccessor extends AbstractLoggingBean implements SftpFileSystemAccessor {
//    @Override
//    public SeekableByteChannel openFile(ServerSession session, SftpEventListenerManager subsystem, FileHandle fileHandle, Path file, String handle, Set<? extends OpenOption> options, FileAttribute<?>... attrs) throws IOException {
//        log.info("openFile");
//        return null;
//    }
//
//    @Override
//    public DirectoryStream<Path> openDirectory(ServerSession session, SftpEventListenerManager subsystem, DirectoryHandle dirHandle, Path dir, String handle) throws IOException {
//        log.info("openDirectory");
//        return null;
//    }
}
