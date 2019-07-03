package com.opuscapita.sftp.service.auth;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.sshd.server.auth.AsyncAuthException;
import org.apache.sshd.server.auth.password.PasswordAuthenticator;
import org.apache.sshd.server.auth.password.PasswordChangeRequiredException;
import org.apache.sshd.server.session.ServerSession;

public class PasswordAuthentication implements PasswordAuthenticator {

	private Log log = LogFactory.getLog(PasswordAuthentication.class);
	
	@Override
	public boolean authenticate(String username, String password, ServerSession session)
			throws PasswordChangeRequiredException, AsyncAuthException {
		/**
		 * @ToDo Replace with the OIDC Authentication
		 */
		if (username.equals("test") && password.equals("password")) {
			log.info("User \"" + username + "\" successfully logged in");
			return true;
		}
		log.info("User \"" + username + "\" has tried to log in with wrong password");
		return false;
	}

}
