package com.opuscapita.sftp.service.auth;

import java.security.PublicKey;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.sshd.common.config.keys.AuthorizedKeyEntry;
import org.apache.sshd.server.auth.AsyncAuthException;
import org.apache.sshd.server.auth.password.PasswordAuthenticator;
import org.apache.sshd.server.auth.password.PasswordChangeRequiredException;
import org.apache.sshd.server.auth.pubkey.PublickeyAuthenticator;
import org.apache.sshd.server.session.ServerSession;

import com.opuscapita.sftp.service.mockdata.User;
import com.opuscapita.sftp.utils.UserFactory;

public class AuthProvider implements PublickeyAuthenticator, PasswordAuthenticator {

	private Log log = LogFactory.getLog(AuthProvider.class);
	private final List<User> users = UserFactory.generateMockUserList();

	@Override
	public boolean authenticate(String username, String password, ServerSession session)
			throws PasswordChangeRequiredException, AsyncAuthException {
		if (this.users.contains(new User(username, password))) {
			log.info("User \"" + username + "\" successfully logged in");
			return true;
		}
		log.info("User \"" + username + "\" has tried to log in with wrong password");
		return false;
	}

	@Override
	public boolean authenticate(String username, PublicKey key, ServerSession session) throws AsyncAuthException {
		log.info("User " + username + " tried the Public Key authentication: " + key.toString() + "\n --- \n\n");
		log.info(AuthorizedKeyEntry.toString(key));
		if (this.users.contains(new User(username, null, AuthorizedKeyEntry.toString(key)))) {
			log.info("User " + username + " successfully logged in with public key");
			return true;
		}
		return false;
	}
}
