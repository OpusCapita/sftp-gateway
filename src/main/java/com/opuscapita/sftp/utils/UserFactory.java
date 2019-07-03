package com.opuscapita.sftp.utils;

import java.util.ArrayList;
import java.util.List;

import com.opuscapita.sftp.service.mockdata.User;

public class UserFactory {

	private UserFactory() {
	}

	public static List<User> generateMockUserList() {
		List<User> userList = new ArrayList<User>();
		userList.add(new User("stefan", "password"));
		userList.add(new User("test", "password"));
		userList.add(new User("user", "password"));
		userList.add(new User("SME", null, "ssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAACAQCxqvmtPGz8RLoSUC9ES+awYznmQjwhII7PPaNxJUo2/etp2NBW/UvJzxeSn5BNqgPLyRPtmcCp0OSBKluqeeDqezmm/WQqeu12K7+7DvJLuHBKSRF6nCHjGRjBHcj8rRy8r/a3TKBi5IIobJxLEJdUWmU7Daj4Xz1HRvaAbpbA+rP5E6CPQXKqe/YYInp6iOk0RpdgTpMOAciI8rd0oRMQ02wynvM4v5PXgVJpehSFoM5ybdCwpBwem2IYlegkswjF45IDj/0UjlTL056jRPWFPtm/+8bE9pxIhJfOFhUlImQpXscyft4VwNF6H+73E2yO0/SrQ+9qH8XnKAU/2r5N3KI7Ju2rO/rtqkFlrgj3Ci+BcZiUkLV81j6EuqkizQe9fMPD1Qch+qH09+MZc4gj/faoR5C9KpExjJHwBgG/nv5OK0uqiDaWca/T4GW2FIA+fOxpj3lMP0UG+Q86KLwOWlo60jmSzrAkgeeNUzNYYtg3SupykwgponYE9bNYhtSXgPNI/ldWb4wotk6cCicNbT8fzU0vFKZ+HLdF2uxGdh5rRMsa3ywgprimz7Mga8LRhrC+LPgYU9NfHfBScEGwuebe+0XgANAxN4qMpCFbTVkzj2hyKz0NcOTYNbgiYyUz1+vJQxJRJ31MMD76JH0lpjhI9YL4N/3Yade+BMzODw=="));
		return userList;
	}
}
