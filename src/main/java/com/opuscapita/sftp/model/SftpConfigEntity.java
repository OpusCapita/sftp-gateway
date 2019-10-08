package com.opuscapita.sftp.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Set;
import java.util.UUID;

@Entity(name = "SftpConfig")
public class SftpConfigEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Getter
    private UUID id;

    @Getter
    @Setter
    private String businessPartnerId;

    @Getter
    @OneToMany(mappedBy = "configEntity")
    private Set<SftpRuleEntity> ruleList;

}
