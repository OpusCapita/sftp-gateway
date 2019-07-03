package com.opuscapita.sftp.service.auth;

import java.security.PublicKey;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.sshd.server.auth.AsyncAuthException;
import org.apache.sshd.server.auth.pubkey.PublickeyAuthenticator;
import org.apache.sshd.server.session.ServerSession;

public class PublickeyAuthentication implements PublickeyAuthenticator {
	private Log log = LogFactory.getLog(PublickeyAuthentication.class);

	@Override
	public boolean authenticate(String username, PublicKey key, ServerSession session) throws AsyncAuthException {
		// TODO Auto-generated method stub
		log.info("User " + username + " tried the Public Key authentication: " + key.toString() + " --- \n\n" + key.getEncoded().toString());
		return false;
	}

}
