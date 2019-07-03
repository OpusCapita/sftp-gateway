package com.opuscapita.sftp.service;

import java.io.File;
import java.io.IOException;
import java.util.Collections;

import javax.annotation.PostConstruct;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.sshd.server.SshServer;
import org.apache.sshd.server.keyprovider.SimpleGeneratorHostKeyProvider;
import org.apache.sshd.server.subsystem.sftp.SftpSubsystemFactory;
import org.springframework.stereotype.Service;

import com.opuscapita.sftp.service.auth.AuthProvider;

@Service
public class SFTPDaemon {

	private Log log = LogFactory.getLog(SFTPDaemon.class);
	SftpSubsystemFactory factory;

	@PostConstruct
	public void startServer() throws IOException {
		start();
	}

	private void start() throws IOException {
		SshServer sshd = SshServer.setUpDefaultServer();
		this.factory = new SftpSubsystemFactory.Builder().build();
		AuthProvider auth = new AuthProvider();
		sshd.setPort(2222);
		sshd.setKeyPairProvider(new SimpleGeneratorHostKeyProvider(new File("host.ser").toPath()));
		sshd.setSubsystemFactories(Collections.singletonList(factory));
		sshd.setPublickeyAuthenticator(auth);
//		sshd.setPasswordAuthenticator(auth);
		sshd.start();
		log.info("SFTP server started");
	}
}
