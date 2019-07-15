package com.opuscapita.auth.model;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.HttpStatus;

public class AuthResponse {


    private Log log = LogFactory.getLog(AuthResponse.class);


    private HttpStatus statusCode;
    private String refresh_token;
    private String token_type;
    private String access_token;
    private String id_token;
    private int expires_in;
    private User user;

    public AuthResponse(HttpStatus _statusCode) {
        this.setStatusCode(_statusCode);
    }

    public AuthResponse(HttpStatus _statusCode, String _json) {
        this.setStatusCode(_statusCode).parseJson(_json);
    }

    public AuthResponse parseJson(String _json) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            JsonNode jsonNode = objectMapper.readTree(_json);
            this.setAccess_token(jsonNode.get("access_token").asText());
            this.setExpires_in(jsonNode.get("expires_in").asInt());
            this.setId_token(jsonNode.get("id_token").asText());
            this.setRefresh_token(jsonNode.get("refresh_token").asText());
            this.setToken_type(jsonNode.get("token_type").asText());
        } catch (Throwable e) {
            log.error("Can not parse the JSON Response: " + _json);
        }

        return this;
    }

    public String getRefresh_token() {
        return refresh_token;
    }

    public AuthResponse setRefresh_token(String refresh_token) {
        this.refresh_token = refresh_token;
        return this;
    }

    public String getToken_type() {
        return token_type;
    }

    public AuthResponse setToken_type(String token_type) {
        this.token_type = token_type;
        return this;
    }

    public String getAccess_token() {
        return access_token;
    }

    public AuthResponse setAccess_token(String access_token) {
        this.access_token = access_token;
        return this;
    }

    public String getId_token() {
        return id_token;
    }

    public AuthResponse setId_token(String id_token) {
        this.id_token = id_token;
        this.setUser(new User(id_token));
        return this;
    }

    public int getExpires_in() {
        return expires_in;
    }

    public AuthResponse setExpires_in(int expires_in) {
        this.expires_in = expires_in;
        return this;
    }

    public HttpStatus getStatusCode() {
        return statusCode;
    }

    public AuthResponse setStatusCode(HttpStatus statusCode) {
        this.statusCode = statusCode;
        return this;
    }

    public User getUser() {
        return user;
    }

    public AuthResponse setUser(User user) {
        this.user = user;
        return this;
    }
}
