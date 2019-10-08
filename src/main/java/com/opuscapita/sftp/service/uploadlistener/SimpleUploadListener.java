package com.opuscapita.sftp.service.uploadlistener;

import org.apache.sshd.server.session.ServerSession;

import java.nio.file.Path;

public class SimpleUploadListener implements FileUploadListenerInterface {

    @Override
    public void onPathReady(Path path, ServerSession session) {

    }
}
