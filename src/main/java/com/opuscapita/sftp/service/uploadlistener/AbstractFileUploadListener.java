package com.opuscapita.sftp.service.uploadlistener;

import com.opuscapita.transaction.service.TxService;

abstract class AbstractFileUploadListener implements FileUploadListenerInterface {

    final String title;
    final String id;
    final String description;
    private TxService txService;

    AbstractFileUploadListener(
            final String _id,
            final String _title,
            final String _desciption
    ) {
        this.id = _id;
        this.title = _title;
        this.description = _desciption;
    }

    @Override
    public final void setTxService(final TxService _txService) {
        this.txService = _txService;
    }

    @Override
    public final TxService getTxService() {
        return this.txService;
    }
}
