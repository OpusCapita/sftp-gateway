package com.opuscapita.sftp.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity(name = "SftpServiceConfig")
public class SftpServiceConfigEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Getter
    @Setter
    private long id;

    @Getter
    @Setter
    private String businessPartnerId;

    @Getter
    @Setter
    private String serviceProfileId;

    @Getter
    @Setter
    private String name;

    @Getter
    @Setter
    private String description;

    @Getter
    @Setter
    private String path;

    @Getter
    @Setter
    private String fileFilter;

    @Getter
    @Setter
    private String action;

    @Getter
    @Setter
    private String actionName;

//
//    @Getter
//    @OneToMany(mappedBy = "serviceConfiguration", fetch = FetchType.EAGER)
//    private Set<SftpServiceConfigAction> ruleActionSet;
}
