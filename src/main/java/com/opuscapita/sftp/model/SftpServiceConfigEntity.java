package com.opuscapita.sftp.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Set;

@Entity(name = "SftpServiceConfig")
public class SftpServiceConfigEntity {
//    @Id
//    @GeneratedValue(strategy = GenerationType.AUTO, generator = "uuid2")
//    @GenericGenerator(name = "uuid2", strategy = "uuid2")
//    @Type(type="uuid-char")
//    @Getter
//    @Setter
//    private UUID id;

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
    @OneToMany(mappedBy = "serviceConfiguration", fetch = FetchType.EAGER)
    private Set<SftpServiceConfigAction> ruleActionSet;
}
