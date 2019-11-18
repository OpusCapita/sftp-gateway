package com.opuscapita.sftp.service.uploadlistener;

abstract class AbstractFileUploadListener implements FileUploadListenerInterface {

    final String title;
    final String id;

    AbstractFileUploadListener(
            final String _id,
            final String _title
    ) {
        this.id = _id;
        this.title = _title;
    }
}
