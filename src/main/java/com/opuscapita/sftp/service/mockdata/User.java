package com.opuscapita.sftp.service.mockdata;

import java.security.PublicKey;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class User {

	private Log log = LogFactory.getLog(User.class);

	private String username = "";
	private String password = "";
	private String key = "";
//	private PublicKey _key;

	public User(String _username, String _password) {
		this.setUsername(_username);
		this.setPassword(_password);
	}

	public User(String _username, PublicKey _key) {
		this.setUsername(_username);
//		this.setKey(_key);
	}

	public User(String _username, String _password, PublicKey _key) {
		this.setUsername(_username);
		this.setPassword(_password);
//		this.setKey(_key);
	}

	public User(String _username, String _password, String _key) {
		this.setUsername(_username);
		this.setPassword(_password);
		this.setKey(_key);
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		if (password != null) {
			this.password = password;
		}
	}

//	public PublicKey getKey() {
//		return _key;
//	}
//
//	public void setKey(PublicKey key) {
//		this._key = key;
//	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		if (this.key != null) {
			this.key = key;
		}
	}

	@Override
	public boolean equals(Object obj) {
		User other = (User) obj;
		log.info("This: \n" + this.getKey());
		log.info("Other: \n" + other.getKey());
		log.info("EQ:" + other.getKey().equals(this.getKey()));
		if (other.getUsername().equals(this.getUsername())) {
			if (other.getPassword().equals(this.getPassword()) || other.getKey().equals(this.getKey())) {
				return true;
			}
			return false;
		}
		return false;
	}

}
