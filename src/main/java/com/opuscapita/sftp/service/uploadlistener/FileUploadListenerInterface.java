package com.opuscapita.sftp.service.uploadlistener;

import org.apache.sshd.server.session.ServerSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;

public interface FileUploadListenerInterface {
    String title = "";
    Logger log = LoggerFactory.getLogger(FileUploadListenerInterface.class);

    void onPathReady(Path path, ServerSession session);
}
