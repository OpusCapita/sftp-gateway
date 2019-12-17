package com.opuscapita.sftp.service.uploadlistener;

import com.opuscapita.auth.model.AuthResponse;
import com.opuscapita.sftp.utils.SFTPHelper;
import com.opuscapita.transaction.model.TxSchemaV1_5;
import com.opuscapita.transaction.model.document.Content;
import com.opuscapita.transaction.model.properties.DocumentCategory;
import com.opuscapita.transaction.model.properties.EntityType;
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
        TxSchemaV1_5 tx = (TxSchemaV1_5) this.getTxService().getTransaction();

        tx.getEventOrigin().setSystemNode("SFTP");
        tx.getEventOrigin().setSystemType("Gateway");

        tx.getDocument().setNumber(1);
        tx.getDocument().setEntityType(EntityType.DOCUMENT.toString());
        tx.getDocument().getContent().add(
                new Content(
                        path.toAbsolutePath().toString(),
                        path.getFileName().toString(),
                        "blob",
                        DocumentCategory.INBOUND.toString(),
                        1
                )
        );

        this.getTxService().setTransaction(tx);
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
