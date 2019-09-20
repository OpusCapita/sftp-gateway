package com.opuscapita.sftp.service;

import com.opuscapita.s2p.blob.blobfilesystem.config.BlobConfiguration;
import com.opuscapita.sftp.config.SFTPConfiguration;
import com.opuscapita.sftp.filesystem.BlobFileSystemFactory;
import com.opuscapita.sftp.service.auth.AuthProvider;
import com.opuscapita.sftp.service.commands.OCRestFileSystemAccessor;
import org.apache.sshd.common.NamedFactory;
import org.apache.sshd.common.PropertyResolverUtils;
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
    private SshServer sshd = SshServer.setUpDefaultServer();
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    public SFTPDaemon(
            SFTPConfiguration _configuration,
            BlobConfiguration _blobConfiguration,
            AuthProvider _authProvider,
            KafkaTemplate<String, String> _kafkaTemplate
    ) {
        this.configuration = _configuration;
        this.kafkaTemplate = _kafkaTemplate;
        List<NamedFactory<Command>> subsystemFactories = new ArrayList<>();
        subsystemFactories.add(this.createDefaultSftpSubsystem());
        this.sshd.setSubsystemFactories(subsystemFactories);

        this.sshd.setPort(this.configuration.getPort());
        this.sshd.setKeyPairProvider(new SimpleGeneratorHostKeyProvider(new File("host.ser").toPath()));
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

    KafkaTemplate<String, String> getKafkaTemplate() {
        return this.kafkaTemplate;
    }
}
