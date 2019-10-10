package com.opuscapita.sftp.model;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Set;
import java.util.UUID;

@Entity(name = "SftpRule")
public class SftpRuleEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Getter
    private UUID rule_id;

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
    @OneToMany(mappedBy = "ruleEntity")
    private Set<SftpRuleAction> ruleActionList;

    @Getter
    @Setter
    @ManyToOne(optional=false)
    @JoinColumn(name="config_id", nullable=false)
    private SftpConfigEntity configEntity;
}
