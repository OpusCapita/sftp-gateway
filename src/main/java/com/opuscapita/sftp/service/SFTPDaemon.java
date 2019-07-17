package com.opuscapita.sftp.service;

import com.opuscapita.sftp.config.SFTPConfiguration;
import com.opuscapita.sftp.service.auth.AuthProvider;
import org.apache.sshd.common.NamedFactory;
import org.apache.sshd.common.PropertyResolverUtils;
import org.apache.sshd.common.file.FileSystemFactory;
import org.apache.sshd.common.util.logging.AbstractLoggingBean;
import org.apache.sshd.server.ServerAuthenticationManager;
import org.apache.sshd.server.SshServer;
import org.apache.sshd.server.command.Command;
import org.apache.sshd.server.keyprovider.SimpleGeneratorHostKeyProvider;
import org.apache.sshd.server.subsystem.sftp.SftpSubsystemFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
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

    private SFTPConfiguration configuration;
    private SshServer sshd = SshServer.setUpDefaultServer();
    private AuthProvider authProvider;

    @Autowired
    public SFTPDaemon(SFTPConfiguration configuration, AuthProvider authProvider) {
        this.configuration = configuration;
        this.authProvider = authProvider;
        List<NamedFactory<Command>> subsystemFactories = new ArrayList<>();
        subsystemFactories.add(this.createDefaultSftpSubsystem());
        this.sshd.setSubsystemFactories(subsystemFactories);

        this.sshd.setPort(this.configuration.getPort());
        this.sshd.setKeyPairProvider(new SimpleGeneratorHostKeyProvider(new File("host.ser").toPath()));
        PropertyResolverUtils.updateProperty(this.sshd, ServerAuthenticationManager.WELCOME_BANNER,
                this.configuration.getWelcome());
        this.sshd.setPublickeyAuthenticator(this.authProvider);
        this.sshd.setPasswordAuthenticator(this.authProvider);
        FileSystemFactory fs = new BlobFileSystem();
        this.sshd.setFileSystemFactory(fs);

    }

    private SftpSubsystemFactory createDefaultSftpSubsystem() {
        OCSftpSubsystemFactory factory = new OCSftpSubsystemFactory.Builder().build();

        SFTPEventListener sftpEventListener = new SFTPEventListener();
        factory.addSftpEventListener(sftpEventListener);

        return factory;
    }

    @PostConstruct
    public void start() throws IOException {
        if (!this.sshd.isStarted()) {
            this.sshd.start();
        }
        log.info("SFTP server is running on port " + this.configuration.getPort());
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
