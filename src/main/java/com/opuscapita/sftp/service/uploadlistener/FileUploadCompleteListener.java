package com.opuscapita.sftp.service.uploadlistener;

import org.apache.sshd.server.session.ServerSession;

import java.nio.file.Path;

public interface FileUploadCompleteListener {
    void onPathReady(Path path, ServerSession session);
}
