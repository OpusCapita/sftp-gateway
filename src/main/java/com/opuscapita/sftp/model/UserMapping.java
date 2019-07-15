package com.opuscapita.sftp.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Data
public class UserMapping {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    @Getter
    private Integer id;

    @Getter
    @Setter
    private String username;

    @Getter
    @Setter
    private String path;

    @Getter
    @Setter
    private String fileExtension;

    @Getter
    @Setter
    private String tenantId;

}
