package com.opuscapita.sftp.service;

import com.opuscapita.blob.config.BlobConfiguration;
import com.opuscapita.s2p.blob.blobfilesystem.BlobFileSystem;
import com.opuscapita.s2p.blob.blobfilesystem.BlobHttpFileSystemProvider;
import com.opuscapita.sftp.config.SFTPConfiguration;
import com.opuscapita.sftp.filesystem.BlobFileSystemFactory;
import com.opuscapita.sftp.service.auth.AuthProvider;
import com.opuscapita.sftp.service.commands.OCSftpSubsystemFactory;
import org.apache.sshd.common.NamedFactory;
import org.apache.sshd.common.PropertyResolverUtils;
import org.apache.sshd.common.file.root.RootedFileSystemProvider;
import org.apache.sshd.common.file.virtualfs.VirtualFileSystemFactory;
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
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.util.ArrayList;
import java.util.List;

@Component
@ComponentScan
public class SFTPDaemon extends AbstractLoggingBean {

    private final SFTPConfiguration configuration;
    private final BlobConfiguration blobConfiguration;
    private SshServer sshd = SshServer.setUpDefaultServer();
    private AuthProvider authProvider;
    private OCSftpSubsystemFactory.Builder builder;

    @Autowired
    public SFTPDaemon(SFTPConfiguration _configuration, BlobConfiguration _blobConfiguration, AuthProvider _authProvider, OCSftpSubsystemFactory.Builder _builder) {
        this.configuration = _configuration;
        this.blobConfiguration = _blobConfiguration;
        this.authProvider = _authProvider;
        this.builder = _builder;

//        testFileSystem();

        List<NamedFactory<Command>> subsystemFactories = new ArrayList<>();
        subsystemFactories.add(this.createDefaultSftpSubsystem());
        this.sshd.setSubsystemFactories(subsystemFactories);

        this.sshd.setPort(this.configuration.getPort());
        this.sshd.setKeyPairProvider(new SimpleGeneratorHostKeyProvider(new File("host.ser").toPath()));
        PropertyResolverUtils.updateProperty(this.sshd, ServerAuthenticationManager.WELCOME_BANNER,
                this.configuration.getWelcome());
        this.sshd.setPublickeyAuthenticator(this.authProvider);
        this.sshd.setPasswordAuthenticator(this.authProvider);

        BlobFileSystemFactory fs = new BlobFileSystemFactory(this.blobConfiguration);
//        VirtualFileSystemFactory fs = new VirtualFileSystemFactory();
        this.sshd.setFileSystemFactory(fs);
    }

    private SftpSubsystemFactory createDefaultSftpSubsystem() {
        SftpSubsystemFactory factory = new SftpSubsystemFactory
                .Builder()
                .build();
//        OCSftpSubsystemFactory factory = this.builder.build();
        SFTPEventListener sftpEventListener = new SFTPEventListener();
        factory.addSftpEventListener(sftpEventListener);

        return factory;
    }

    private void testFileSystem() {
        try {
            FileSystem bfs = FileSystems.getFileSystem(new URI("https://jsonplaceholder.typicode.com/"));
            bfs.getPath("todos/1");
        } catch (Exception e) {
            log.error(e.getMessage());
        }
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
