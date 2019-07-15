package com.opuscapita.auth.model;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.context.annotation.ApplicationScope;

import java.util.Base64;

@ApplicationScope
@Getter
public class User {

    private Log log = LogFactory.getLog(User.class);
    private String id;
    private String email;
    private String phoneNo;
    private String supplierId;
    private String customerId;
    private String status;
    private String mayChangeSupplier;
    private String mayChangeCustomer;
    private String languageId;
    private String firstName;
    private String lastName;
    private String mustBeChanged;
    private String xroles;
    private String tenantName;
    private String[] roles;
    private String aud;
    private String exp;
    private String iat;
    private String iss;

    public User(String _jwt) {
        String[] split_string = _jwt.split("\\.");
        String base64EncodedBody = split_string[1];
        log.info("Jwt Body: " + base64EncodedBody);
        String jsonUser = new String(Base64.getDecoder().decode(base64EncodedBody));

        log.info("Jwt Json Body: " + jsonUser);
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            JsonNode jsonNode = objectMapper.readTree(jsonUser);
            this.id = jsonNode.get("id").asText();
            this.email = jsonNode.get("email").asText();
            this.phoneNo = jsonNode.get("phoneNo").asText();
            this.supplierId = jsonNode.get("supplierId").asText();
            this.customerId = jsonNode.get("customerId").asText();
            this.status = jsonNode.get("status").asText();
            this.mayChangeSupplier = jsonNode.get("mayChangeSupplier").asText();
            this.mayChangeCustomer = jsonNode.get("mayChangeCustomer").asText();
            this.languageId = jsonNode.get("languageId").asText();
            this.firstName = jsonNode.get("firstName").asText();
            this.lastName = jsonNode.get("lastName").asText();
            this.mustBeChanged = jsonNode.get("mustBeChanged").asText();
            this.xroles = jsonNode.get("xroles").asText();
            this.tenantName = jsonNode.get("tenantName").asText();
            this.aud = jsonNode.get("aud").asText();
            this.exp = jsonNode.get("exp").asText();
            this.iat = jsonNode.get("iat").asText();
            this.iss = jsonNode.get("iss").asText();
        } catch (Throwable e) {
            log.error("Can not parse the JSON Response: " + jsonUser);
        }
    }
}
