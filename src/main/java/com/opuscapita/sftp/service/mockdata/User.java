package com.opuscapita.sftp.service.mockdata;

public class User {

	private String username = "";
	private String password = "";
	private String key = "";

	public User(String _username, String _password) {
		this.setUsername(_username);
		this.setPassword(_password);
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
		if (other.getUsername().equals(this.getUsername())) {
			if ((!this.getPassword().isEmpty() && other.getPassword().equals(this.getPassword()))
					|| (!this.getKey().isEmpty() && other.getKey().equals(this.getKey()))) {
				return true;
			}
			return false;
		}
		return false;
	}

}
