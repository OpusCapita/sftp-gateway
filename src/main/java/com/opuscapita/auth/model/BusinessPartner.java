package com.opuscapita.auth.model;

import lombok.Data;
import org.springframework.web.context.annotation.ApplicationScope;

@ApplicationScope
@Data
public class BusinessPartner {
    private String id;
    private String name;
    private boolean iscustomer;
    private boolean issupplier;
}
