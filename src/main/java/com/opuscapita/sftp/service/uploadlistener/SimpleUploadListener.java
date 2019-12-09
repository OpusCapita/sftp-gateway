package com.opuscapita.sftp.service.uploadlistener;

import com.opuscapita.auth.model.AuthResponse;
import com.opuscapita.sftp.utils.SFTPHelper;
import org.apache.sshd.common.AttributeRepository;
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
        AttributeRepository.AttributeKey<AuthResponse> authResponseAttributeKey = SFTPHelper.findAttributeKey(session, AuthResponse.class);
        AuthResponse authResponse = session.getAttribute(authResponseAttributeKey);
        this.getTxService().sendTx(authResponse);
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
