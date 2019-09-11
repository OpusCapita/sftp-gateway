package com.opuscapita.auth.model;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.context.annotation.ApplicationScope;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@ApplicationScope
@Getter
public class User {

    private Log log = LogFactory.getLog(User.class);
    private String id;
    private String email;
    private String phoneNo;
    private String supplierId;
    private String customerId;
    private String clientId;
    private String status;
    private boolean mayChangeSupplier;
    private boolean mayChangeCustomer;
    private String languageId;
    private String firstName;
    private String lastName;
    private String mustBeChanged;
    private String xroles;
    private String tenantName;
    private List<String> roles = new ArrayList<>();
    private int aud;
    private int exp;
    private int iat;
    private String iss;
    private BusinessPartner businessPartner = new BusinessPartner();

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
            this.clientId = jsonNode.get("clientId").asText();
            this.status = jsonNode.get("status").asText();
            this.mayChangeSupplier = jsonNode.get("mayChangeSupplier").asBoolean();
            this.mayChangeCustomer = jsonNode.get("mayChangeCustomer").asBoolean();
            this.languageId = jsonNode.get("languageId").asText();
            this.firstName = jsonNode.get("firstName").asText();
            this.lastName = jsonNode.get("lastName").asText();
            this.mustBeChanged = jsonNode.get("mustBeChanged").asText();
            this.tenantName = jsonNode.get("tenantName").asText();
            this.aud = jsonNode.get("aud").asInt();
            this.exp = jsonNode.get("exp").asInt();
            this.iat = jsonNode.get("iat").asInt();
            this.iss = jsonNode.get("iss").asText();

            JsonNode rolesNode = jsonNode.get("roles");
            if (rolesNode.isArray()) {
                for (JsonNode node : rolesNode) {
                    this.roles.add(node.asText());
                }
            }

            this.businessPartner.setId(jsonNode.get("businessPartner").get("id").asText());
            this.businessPartner.setName(jsonNode.get("businessPartner").get("name").asText());
            this.businessPartner.setIscustomer(jsonNode.get("businessPartner").get("iscustomer").asBoolean());
            this.businessPartner.setIssupplier(jsonNode.get("businessPartner").get("issupplier").asBoolean());
        } catch (Throwable e) {
            log.error("Can not parse the JSON Response: " + jsonUser);
        }
    }
}
