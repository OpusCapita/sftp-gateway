package com.opuscapita.auth.model;

import java.util.ArrayList;
import java.util.List;

public class AuthRequest {

    static final String PASSWORD_FLOW = "password";
    static final String PUBLICKEY_FLOW = "publickey";

    private String grant_type = "password";
    private String username;
    private String password;
    private String publicKey;
    private List<String> scope = new ArrayList<>();

    public static AuthRequest createPasswordRequest(String _username, String _password) {
        return new AuthRequest(_username, _password, AuthRequest.PASSWORD_FLOW)
                .addScope("email")
                .addScope("phone")
                .addScope("userInfo")
                .addScope("roles");
    }

    public static AuthRequest createPublickeyRequest(String _username, String _key) {
        return new AuthRequest(_username, _key, AuthRequest.PUBLICKEY_FLOW)
                .addScope("email")
                .addScope("phone")
                .addScope("userInfo")
                .addScope("roles");
    }


    private AuthRequest(String _username, String _key, String _grant_type) {
        this.grant_type = _grant_type;
        this.username = _username;
        if (_grant_type == AuthRequest.PASSWORD_FLOW) {
            this.password = _key;
        } else if (_grant_type == AuthRequest.PUBLICKEY_FLOW) {
            this.publicKey = _key;
        }
    }

    public String getGrant_type() {
        return grant_type;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public List<String> getScope() {
        return scope;
    }

    public AuthRequest addScope(String scope) {
        this.getScope().add(scope);
        return this;
    }

}
