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
import java.util.Objects;

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
        String jsonUser = new String(Base64.getDecoder().decode(base64EncodedBody));

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            JsonNode jsonNode = objectMapper.readTree(jsonUser);
            this.id = Objects.requireNonNull(jsonNode.get("id")).asText();
            this.email = Objects.requireNonNull(jsonNode.get("email")).asText();
            this.phoneNo = Objects.requireNonNull(jsonNode.get("phoneNo")).asText();
            this.supplierId = Objects.requireNonNull(jsonNode.get("supplierId"), "").asText();
            this.customerId = Objects.requireNonNull(jsonNode.get("customerId"), "").asText();
            this.clientId = Objects.requireNonNull(jsonNode.get("clientId"), "").asText();
            this.status = Objects.requireNonNull(jsonNode.get("status")).asText();
            this.mayChangeSupplier = Objects.requireNonNull(jsonNode.get("mayChangeSupplier"), "false").asBoolean();
            this.mayChangeCustomer = Objects.requireNonNull(jsonNode.get("mayChangeCustomer"), "false").asBoolean();
            this.languageId = Objects.requireNonNull(jsonNode.get("languageId")).asText();
            this.firstName = Objects.requireNonNull(jsonNode.get("firstName"), "").asText();
            this.lastName = Objects.requireNonNull(jsonNode.get("lastName"), "").asText();
            this.mustBeChanged = Objects.requireNonNull(jsonNode.get("mustBeChanged"), "false").asText();
            this.tenantName = Objects.requireNonNull(jsonNode.get("tenantName")).asText();
            this.aud = Objects.requireNonNull(jsonNode.get("aud")).asInt();
            this.exp = Objects.requireNonNull(jsonNode.get("exp")).asInt();
            this.iat = Objects.requireNonNull(jsonNode.get("iat")).asInt();
            this.iss = Objects.requireNonNull(jsonNode.get("iss")).asText();

            JsonNode rolesNode = Objects.requireNonNull(jsonNode.get("roles"));
            if (rolesNode.isArray()) {
                for (JsonNode node : rolesNode) {
                    this.roles.add(node.asText());
                }
            }

            this.businessPartner.setId(Objects.requireNonNull(jsonNode.get("businessPartner")).get("id").asText());
            this.businessPartner.setName(Objects.requireNonNull(jsonNode.get("businessPartner")).get("name").asText());
            this.businessPartner.setIscustomer(Objects.requireNonNull(jsonNode.get("businessPartner")).get("iscustomer").asBoolean());
            this.businessPartner.setIssupplier(Objects.requireNonNull(jsonNode.get("businessPartner")).get("issupplier").asBoolean());
        } catch (Exception e) {
            log.error("Can not parse the JSON Response: " + jsonUser);
            log.error(e.getMessage());
        }
    }

    public boolean hasRole(String _role) {
        if (Objects.isNull(_role))
            return false;
        return this.getRoles().contains(_role);
    }

    public boolean hasRole(List<String> _roles) {
        if (Objects.isNull(_roles))
            return false;
        String[] _rolesArray = null;
        _roles.toArray(_rolesArray);
        return this.hasRole(_rolesArray);
    }

    public boolean hasRole(String[] _roles) {
        if (Objects.isNull(_roles))
            return false;
        boolean hasRole = false;
        for (String role : roles) {
            if (!hasRole) {
                hasRole = hasRole(role);
            }
        }
        return hasRole;
    }
}
