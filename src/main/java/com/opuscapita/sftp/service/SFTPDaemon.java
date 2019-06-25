package com.opuscapita.sftp.service;

import java.io.File;
import java.io.IOException;
import java.util.Collections;

import javax.annotation.PostConstruct;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.sshd.common.file.virtualfs.VirtualFileSystemFactory;
import org.apache.sshd.server.SshServer;
import org.apache.sshd.server.auth.AsyncAuthException;
import org.apache.sshd.server.auth.password.PasswordAuthenticator;
import org.apache.sshd.server.auth.password.PasswordChangeRequiredException;
import org.apache.sshd.server.keyprovider.SimpleGeneratorHostKeyProvider;
import org.apache.sshd.server.session.ServerSession;
import org.apache.sshd.server.subsystem.sftp.SftpSubsystemFactory;
import org.springframework.stereotype.Service;

@Service
public class SFTPDaemon {

	private Log log = LogFactory.getLog(SFTPDaemon.class);
	SftpSubsystemFactory factory;
	private String userPath = "C:\\Upload";

	@PostConstruct
	public void startServer() throws IOException {
		start();
	}

	private void start() throws IOException {
		SshServer sshd = SshServer.setUpDefaultServer();
		this.factory = new SftpSubsystemFactory.Builder().build();
		sshd.setFileSystemFactory(new VirtualFileSystemFactory(new File(this.userPath).toPath()));
		sshd.setPort(2222);
		sshd.setKeyPairProvider(new SimpleGeneratorHostKeyProvider(new File("host.ser").toPath()));
		sshd.setSubsystemFactories(Collections.singletonList(factory));
		sshd.setPasswordAuthenticator(new PasswordAuthenticator() {

			@Override
			public boolean authenticate(String username, String password, ServerSession session)
					throws PasswordChangeRequiredException, AsyncAuthException {
				/**
				 * @ToDo Replace with the OIDC Authentication
				 */
				if (username.equals("test") && password.equals("password")) {
					log.debug("User \"" + username + "\" successfully logged in");
					sshd.setFileSystemFactory(
							new VirtualFileSystemFactory(new File(userPath + "\\" + username).toPath()));
					return true;
				}
				return false;
			}
		});
		sshd.start();
		log.info("SFTP server started");
	}
}
