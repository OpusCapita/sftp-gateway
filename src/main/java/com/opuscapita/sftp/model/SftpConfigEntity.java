package com.opuscapita.sftp.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity(name = "SftpConfig")
@Data
public class SftpConfigEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Getter
    private UUID id;

    @Getter
    @Setter
    private String path;

    @Getter
    @Setter
    private String fileExtension;

    @Getter
    @Setter
    private String businessPartnerId;

//    @Getter
//    @Setter
//    private List<SftpRuleEntity> rules = new ArrayList<>();
}
