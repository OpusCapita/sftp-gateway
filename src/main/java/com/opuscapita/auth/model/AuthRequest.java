package com.opuscapita.auth.model;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class AuthRequest {

    static final String PASSWORD_FLOW = "password";
    static final String PUBLICKEY_FLOW = "publickey";

    private final MediaType content_type = MediaType.APPLICATION_FORM_URLENCODED;
    private final String GRANTTYPE = "grant_type";
    private final String USERNAME = "username";
    private final String PASSWORD = "password";
    private final String PUBLICKEY = "publickey";
    private final String SCOPE = "scope";
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
        this.setGrant_type(_grant_type);
        this.setUsername(_username);
        if (_grant_type == AuthRequest.PASSWORD_FLOW) {
            this.setPassword(_key);
        } else if (_grant_type == AuthRequest.PUBLICKEY_FLOW) {
            this.setPublicKey(_key);
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

    public MediaType getContent_type() {
        return content_type;
    }

    private AuthRequest setGrant_type(String grant_type) {
        this.grant_type = grant_type;
        return this;
    }

    private AuthRequest setUsername(String username) {
        this.username = username;
        return this;
    }

    private AuthRequest setPassword(String password) {
        this.password = password;
        return this;
    }

    private AuthRequest setPublicKey(String publicKey) {
        this.publicKey = publicKey;
        return this;
    }

    public List<String> getScope() {
        return scope;
    }

    public String getScopeAsString() {
        String scope = new String();
        for (String _scope : this.getScope()) {
            scope += _scope + " ";
        }
        return scope.trim();

    }

    public AuthRequest addScope(String scope) {
        this.getScope().add(scope);
        return this;
    }

    public HttpHeaders getHttpHeader(String _clientIdentifier, String _clientKey) {
        String clientCredentials = _clientIdentifier + ":" + _clientKey;
        HttpHeaders header = new HttpHeaders();
        header.setContentType(this.getContent_type());
        header.set("Authorization", "Basic " + Base64.getEncoder().encodeToString(clientCredentials.getBytes()));
        return header;
    }

    public MultiValueMap<String, String> getMultiFormData() {
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add(this.GRANTTYPE, this.getGrant_type());
        map.add(this.USERNAME, this.getUsername());
        map.add(this.PASSWORD, this.getPassword());
        map.add(this.SCOPE, this.getScopeAsString());
        return map;
    }

    public String getMultiFormDataAsString() {
        String map = new String();
        MultiValueMap<String, String> multiFormData = this.getMultiFormData();
        for(String key : multiFormData.keySet()) {
            map += key + "=" + multiFormData.get(key).get(0) + "&";
        }
        map = map.substring(0,map.length()-1);
        return map;
    }
}
