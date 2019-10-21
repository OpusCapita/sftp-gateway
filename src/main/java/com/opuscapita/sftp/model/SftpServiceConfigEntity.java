package com.opuscapita.sftp.model;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Set;
import java.util.UUID;

@Entity(name = "SftpServiceConfig")
public class SftpServiceConfigEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Getter
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
    @OneToMany(mappedBy = "serviceConfiguration")
    private Set<SftpServiceConfigAction> ruleActionSet;
}
