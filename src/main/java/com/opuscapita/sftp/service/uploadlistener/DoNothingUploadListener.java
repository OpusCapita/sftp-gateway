package com.opuscapita.sftp.service.uploadlistener;

import org.apache.sshd.server.session.ServerSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.nio.file.Path;

@Component
public class DoNothingUploadListener extends AbstractFileUploadListener implements FileUploadListenerInterface {

    @Autowired
    public DoNothingUploadListener() {
        super(String.valueOf(0), "Do Nothing", "-");
    }

    @Override
    public void onPathReady(Path path, ServerSession session) {

    }
}
