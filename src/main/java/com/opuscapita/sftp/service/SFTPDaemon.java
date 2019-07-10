package com.opuscapita.sftp.service;

import com.opuscapita.sftp.config.SFTPConfiguration;
import com.opuscapita.sftp.service.auth.AuthProvider;
import org.apache.sshd.common.PropertyResolverUtils;
import org.apache.sshd.common.util.logging.AbstractLoggingBean;
import org.apache.sshd.server.ServerAuthenticationManager;
import org.apache.sshd.server.SshServer;
import org.apache.sshd.server.keyprovider.SimpleGeneratorHostKeyProvider;
import org.apache.sshd.server.subsystem.sftp.SftpSubsystemFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.File;
import java.io.IOException;
import java.util.Collections;

@Component
@ComponentScan
public class SFTPDaemon extends AbstractLoggingBean {

    private SFTPConfiguration configuration;


    private SftpSubsystemFactory factory;
    private final SshServer sshd = SshServer.setUpDefaultServer();
    private AuthProvider authProvider;

    @Autowired
    public SFTPDaemon(SFTPConfiguration configuration, AuthProvider authProvider) {
        this.configuration = configuration;
        this.authProvider = authProvider;
        log.info(this.configuration.getWelcome());
        this.sshd.setPort(this.configuration.getPort());
        this.sshd.setKeyPairProvider(new SimpleGeneratorHostKeyProvider(new File("host.ser").toPath()));
        PropertyResolverUtils.updateProperty(this.sshd, ServerAuthenticationManager.WELCOME_BANNER,
                this.configuration.getWelcome());

        this.factory = new SftpSubsystemFactory.Builder().build();
        this.factory.addSftpEventListener(new SFTPEventListener());
        this.sshd.setSubsystemFactories(Collections.singletonList(this.factory));
        this.sshd.setPublickeyAuthenticator(this.authProvider);
        this.sshd.setPasswordAuthenticator(this.authProvider);

    }

    @PostConstruct
    public void start() throws IOException {
        if (!this.sshd.isStarted()) {
            this.sshd.start();
        }
        log.info("SFTP server is running on port " + this.configuration.getPort());
    }

    @PreDestroy
    public void shutdown() throws IOException {
        if (this.sshd.isStarted()) {
            this.sshd.stop();
            log.info("SFTP server stoped");
        } else {
            log.info("SFTP server is not running");
        }
    }
}
