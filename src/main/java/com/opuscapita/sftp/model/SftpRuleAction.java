package com.opuscapita.sftp.model;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.UUID;

@Entity(name = "SftpRuleAction")
public class SftpRuleAction {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Getter
    private UUID action_id;

    @Getter
    @Setter
    private String name;

    @Getter
    @Setter
    private String fileFilter;

    @Getter
    @Setter
    @ManyToOne
    @JoinColumn(name = "rule_id", nullable = false)
    private SftpRuleEntity ruleEntity;

}
