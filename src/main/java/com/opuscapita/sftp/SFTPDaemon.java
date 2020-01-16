package com.opuscapita.sftp;

import com.opuscapita.bouncer.exceptions.EmptyPermissionsException;
import com.opuscapita.bouncer.exceptions.PermissionsNotRegistered;
import com.opuscapita.bouncer.model.RetryConfig;
import com.opuscapita.bouncer.service.Bouncer;
import com.opuscapita.s2p.blob.blobfilesystem.config.BlobConfiguration;
import com.opuscapita.sftp.config.SFTPConfiguration;
import com.opuscapita.sftp.filesystem.BlobFileSystemFactory;
import com.opuscapita.sftp.model.SftpServiceConfigRepository;
import com.opuscapita.sftp.service.SFTPEventListener;
import com.opuscapita.sftp.service.UploadListenerService;
import com.opuscapita.sftp.service.auth.AuthProvider;
import com.opuscapita.sftp.service.commands.OCRestFileSystemAccessor;
import com.opuscapita.transaction.config.TNTConfiguration;
import lombok.Getter;
import org.apache.sshd.common.NamedFactory;
import org.apache.sshd.common.PropertyResolverUtils;
import org.apache.sshd.common.keyprovider.FileKeyPairProvider;
import org.apache.sshd.common.util.logging.AbstractLoggingBean;
import org.apache.sshd.server.ServerAuthenticationManager;
import org.apache.sshd.server.SshServer;
import org.apache.sshd.server.command.Command;
import org.apache.sshd.server.keyprovider.SimpleGeneratorHostKeyProvider;
import org.apache.sshd.server.subsystem.sftp.SftpErrorStatusDataHandler;
import org.apache.sshd.server.subsystem.sftp.SftpSubsystemFactory;
import org.apache.sshd.server.subsystem.sftp.UnsupportedAttributePolicy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
@ComponentScan
public class SFTPDaemon extends AbstractLoggingBean {

    private final SFTPConfiguration configuration;
    @Getter
    private final Bouncer bouncer;
    @Getter
    private SshServer sshd = SshServer.setUpDefaultServer();
    @Getter
    private final TNTConfiguration tntConfiguration;
    @Getter
    private KafkaTemplate<String, String> kafkaTemplate;
    @Getter
    private SftpServiceConfigRepository sftpServiceConfigRepository;
    @Getter
    private UploadListenerService uploadListenerService;

    @Autowired
    public SFTPDaemon(
            SFTPConfiguration _configuration,
            BlobConfiguration _blobConfiguration,
            TNTConfiguration _tntConfiguration,
            AuthProvider _authProvider,
            KafkaTemplate<String, String> _kafkaTemplate,
            Bouncer _bouncer,
            SftpServiceConfigRepository _sftpServiceConfigRepository,
            UploadListenerService _uploadListenerService
    ) {
        this.sftpServiceConfigRepository = _sftpServiceConfigRepository;
        this.uploadListenerService = _uploadListenerService;
        this.configuration = _configuration;
        this.tntConfiguration = _tntConfiguration;
        this.kafkaTemplate = _kafkaTemplate;
        this.bouncer = _bouncer;
        try {
            this.bouncer.registerPermissions(new RetryConfig());
        } catch (PermissionsNotRegistered | EmptyPermissionsException permissionsNotRegistered) {
            log.warn(permissionsNotRegistered.getMessage());
        }

        List<NamedFactory<Command>> subsystemFactories = new ArrayList<>();
        subsystemFactories.add(this.createDefaultSftpSubsystem());
        this.sshd.setSubsystemFactories(subsystemFactories);

        this.sshd.setKeyPairProvider(new SimpleGeneratorHostKeyProvider(new File("host.ser").toPath()));
        this.sshd.setPort(this.configuration.getPort());
        PropertyResolverUtils.updateProperty(this.sshd, ServerAuthenticationManager.WELCOME_BANNER,
                this.configuration.getWelcome());
        this.sshd.setPublickeyAuthenticator(_authProvider);
        this.sshd.setPasswordAuthenticator(_authProvider);

        BlobFileSystemFactory fs = new BlobFileSystemFactory(_blobConfiguration);
        this.sshd.setFileSystemFactory(fs);
    }

    private SftpSubsystemFactory createDefaultSftpSubsystem() {
        SftpSubsystemFactory factory = new SftpSubsystemFactory
                .Builder()
                .withFileSystemAccessor(new OCRestFileSystemAccessor())
                .withUnsupportedAttributePolicy(UnsupportedAttributePolicy.Warn)
                .withSftpErrorStatusDataHandler(SftpErrorStatusDataHandler.DEFAULT)
                .build();
        SFTPEventListener eventListener = new SFTPEventListener(this);
        factory.addSftpEventListener(eventListener);
        return factory;
    }

    @PostConstruct
    public void start() throws IOException {
        if (!this.sshd.isStarted()) {
            this.sshd.start();
        }
        log.info("SFTP server is running on port {}", this.configuration.getPort());
    }

    @PreDestroy
    public void shutdown() {
        try {
            this.sshd.stop();
            log.info("SFTP server stoped");
        } catch (IOException e) {
            log.info("SFTP server is not running");
        } finally {
            this.sshd = null;
        }
    }
}
