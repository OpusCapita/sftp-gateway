package com.opuscapita.sftp.service;

import com.opuscapita.sftp.service.auth.AuthProvider;
import org.apache.sshd.common.PropertyResolverUtils;
import org.apache.sshd.common.util.logging.AbstractLoggingBean;
import org.apache.sshd.server.ServerAuthenticationManager;
import org.apache.sshd.server.SshServer;
import org.apache.sshd.server.keyprovider.SimpleGeneratorHostKeyProvider;
import org.apache.sshd.server.subsystem.sftp.SftpSubsystemFactory;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.File;
import java.io.IOException;
import java.util.Collections;

@Service
public class SFTPDaemon extends AbstractLoggingBean {

    private int port = 2222;
    private String hostKeyPath = "host.ser";
    private String welcomeBanner = "\n\nWelcome to OpusCapita SFTP- Gateway\n\n";

    //    private Log log = LogFactory.getLog(SFTPDaemon.class);
    private SftpSubsystemFactory factory;
    private final SshServer sshd = SshServer.setUpDefaultServer();
    private AuthProvider authProvider = new AuthProvider();

    public SFTPDaemon() {
        log.isDebugEnabled();
        this.sshd.setPort(this.port);
        this.sshd.setKeyPairProvider(new SimpleGeneratorHostKeyProvider(new File(hostKeyPath).toPath()));
        PropertyResolverUtils.updateProperty(this.sshd, ServerAuthenticationManager.WELCOME_BANNER,
                this.welcomeBanner);

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
        log.info("SFTP server started");
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
