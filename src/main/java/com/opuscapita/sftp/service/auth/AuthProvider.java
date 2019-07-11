package com.opuscapita.sftp.service.auth;

import com.opuscapita.auth.AuthService;
import com.opuscapita.auth.model.AuthResponse;
import com.opuscapita.sftp.service.mockdata.User;
import com.opuscapita.sftp.utils.UserFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.sshd.common.AttributeRepository;
import org.apache.sshd.common.config.keys.AuthorizedKeyEntry;
import org.apache.sshd.server.auth.AsyncAuthException;
import org.apache.sshd.server.auth.password.PasswordAuthenticator;
import org.apache.sshd.server.auth.password.PasswordChangeRequiredException;
import org.apache.sshd.server.auth.pubkey.PublickeyAuthenticator;
import org.apache.sshd.server.session.ServerSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.PublicKey;
import java.util.List;

@Service
public class AuthProvider implements PublickeyAuthenticator, PasswordAuthenticator {

	private Log log = LogFactory.getLog(AuthProvider.class);

	private AuthService authService;

	private final List<User> users = UserFactory.generateMockUserList();

	@Autowired
	public AuthProvider(AuthService _service) {
		this.authService = _service;
	}

	@Override
	public boolean authenticate(String username, String password, ServerSession session)
			throws PasswordChangeRequiredException, AsyncAuthException {
		AuthResponse response = this.authService.authenticateWithPassword(username,password);
		AttributeRepository.AttributeKey<AuthResponse> authResponseAttributeKey = new AttributeRepository.AttributeKey();
		session.setAttribute(authResponseAttributeKey, response);
		return response.getStatusCode().is2xxSuccessful();
	}

	@Override
	public boolean authenticate(String username, PublicKey key, ServerSession session) throws AsyncAuthException {
		if (this.users.contains(new User(username, null, AuthorizedKeyEntry.toString(key)))) {
			log.info("User " + username + " successfully logged in with public key");
			return true;
		}
		return false;
	}
}
