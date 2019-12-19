package com.opuscapita.sftp.service;

import com.opuscapita.auth.model.AuthResponse;
import com.opuscapita.sftp.SFTPDaemon;
import com.opuscapita.sftp.model.SftpServiceConfigEntity;
import com.opuscapita.sftp.model.SftpServiceConfigRepository;
import com.opuscapita.sftp.service.uploadlistener.FileUploadListenerInterface;
import com.opuscapita.sftp.utils.SFTPHelper;
import com.opuscapita.transaction.model.properties.Version;
import com.opuscapita.transaction.service.TxService;
import com.opuscapita.transaction.utils.TxUtils;
import lombok.Getter;
import org.apache.sshd.common.AttributeRepository;
import org.apache.sshd.server.session.ServerSession;
import org.apache.sshd.server.subsystem.sftp.AbstractSftpEventListenerAdapter;
import org.apache.sshd.server.subsystem.sftp.DirectoryHandle;
import org.apache.sshd.server.subsystem.sftp.Handle;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class SFTPEventListener extends AbstractSftpEventListenerAdapter {
    @Getter
    private AuthResponse authResponse;

    @Getter
    private final SFTPDaemon service;

    private final SftpServiceConfigRepository serviceConfigRepository;
    private final UploadListenerService uploadListenerService;

    public SFTPEventListener(
            SFTPDaemon _service
    ) {
        super();
        this.service = _service;
        this.serviceConfigRepository = _service.getSftpServiceConfigRepository();
        this.uploadListenerService = _service.getUploadListenerService();
    }

    private Map<String, FileUploadListenerInterface> fileReadyListeners = new HashMap<>();

    public void addFileUploadCompleteListener(String path, String file, FileUploadListenerInterface listener) {
        fileReadyListeners.put(path.replace("*", ".*") + file.replace("*", ".*"), listener);
    }

    public void removeFileUploadCompleteListener(String path, FileUploadListenerInterface fileUploadListener) {
        fileReadyListeners.remove(path, fileUploadListener);
    }

    @Override
    public void initialized(ServerSession session, int version) {
        /*
        Loading the backend filesystem from Azure Blob container
         */
        super.initialized(session, version);

        AttributeRepository.AttributeKey<AuthResponse> authResponseAttributeKey = SFTPHelper.findAttributeKey(session, AuthResponse.class);
        this.authResponse = session.getAttribute(authResponseAttributeKey);

        List<SftpServiceConfigEntity> configProfiles = this.serviceConfigRepository.findByBusinessPartnerId(
                this.authResponse.getUser().getBusinessPartner().getId()
        );

        TxService txService;
        for (SftpServiceConfigEntity entity : configProfiles) {
            try {
                txService = new TxService(
                        entity,
                        this.service.getKafkaTemplate(),
                        this.service.getTntConfiguration()
                );
                txService.setTransaction(TxUtils.createEventTx(
                        Version.V_1_5,
                        entity.getAction(),
                        entity.getBusinessPartnerId(),
                        entity.getBusinessPartnerId(),
                        entity.getServiceProfileId())
                );
                this.addFileUploadCompleteListener(
                        entity.getPath(),
                        entity.getFileFilter(),
                        uploadListenerService.getFileUploadListenerById(entity.getAction(), txService)
                );
            } catch (InstantiationException | IllegalAccessException e) {
                log.error(e.getMessage());
            }
        }

    }

    @Override
    public void closed(ServerSession session, String remoteHandle, Handle localHandle, Throwable thrown) throws IOException {
        Path path = localHandle.getFile();

        log.info(String.format("User %s closed file: \"%s\"", session.getUsername(), localHandle.getFile().toAbsolutePath()));
        Pattern pattern = null;
        if (!(localHandle instanceof DirectoryHandle)) {
            for (String key : fileReadyListeners.keySet()) {
                pattern = Pattern.compile(key);
                if (pattern.matcher(path.toAbsolutePath().toString()).matches()) {
                    this.fileReadyListeners.get(key).onPathReady(path, session);
                }
            }
        }
    }

    @Override
    public void destroying(ServerSession session) {
        for (String key : this.fileReadyListeners.keySet()) {
            this.removeFileUploadCompleteListener(key, this.fileReadyListeners.get(key));
        }
    }
}
