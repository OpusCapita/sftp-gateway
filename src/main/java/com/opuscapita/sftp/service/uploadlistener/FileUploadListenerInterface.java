package com.opuscapita.sftp.service.uploadlistener;

import com.opuscapita.transaction.service.TxService;
import org.apache.sshd.server.session.ServerSession;

import java.nio.file.Path;

public interface FileUploadListenerInterface {
    void onPathReady(Path path, ServerSession session);

    String getId();

    String getTitle();

    String getDescription();

    void setTxService(final TxService _service);

    TxService getTxService();
}
