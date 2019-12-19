package com.opuscapita.sftp.model;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.UUID;

@Entity(name = "SftpServiceConfig")
public class SftpServiceConfigEntity {

    @Id
//    @GeneratedValue(strategy = GenerationType.AUTO)
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(columnDefinition = "BINARY(16)")
    @Getter
    @Setter
    private UUID id;

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
