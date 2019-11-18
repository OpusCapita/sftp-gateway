package com.opuscapita.sftp.service.uploadlistener;

import org.apache.sshd.server.session.ServerSession;

import java.nio.file.Path;

public class SimpleUploadListener extends AbstractFileUploadListener implements FileUploadListenerInterface {

    public SimpleUploadListener() {
        super(String.valueOf(1), "Simple Upload Listener");
    }

    @Override
    public void onPathReady(Path path, ServerSession session) {

    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public String getTitle() {
        return this.title;
    }
}
