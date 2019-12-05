package com.opuscapita.sftp.service.uploadlistener;

import org.apache.sshd.server.session.ServerSession;
import org.springframework.stereotype.Component;

import java.nio.file.Path;

@Component
public class SimpleUploadListener extends AbstractFileUploadListener implements FileUploadListenerInterface {

    public SimpleUploadListener() {
        super(String.valueOf(1), "Simple Upload Listener", "-");
    }

    @Override
    public void onPathReady(Path path, ServerSession session) {
        this.getTxService().sendTx();
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public String getTitle() {
        return this.title;
    }

    @Override
    public String getDescription() {
        return this.description;
    }
}
