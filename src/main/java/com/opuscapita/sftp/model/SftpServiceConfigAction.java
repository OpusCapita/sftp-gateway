//package com.opuscapita.sftp.model;
//
//import lombok.Getter;
//import lombok.Setter;
//import org.hibernate.annotations.GenericGenerator;
//
//import javax.persistence.*;
//import java.util.UUID;
//
////@Entity(name = "SftpServiceConfigAction")
//public class SftpServiceConfigAction {
//    @Id
//    @GeneratedValue(strategy = GenerationType.AUTO, generator = "uuid2")
//    @GenericGenerator(name = "uuid2", strategy = "uuid2")
//    @Getter
//    private UUID actionId;
//
//    @Getter
//    @Setter
//    private String name;
//
//    @Getter
//    @Setter
//    private String fileFilter;
//
//    @Getter
//    @Setter
//    @ManyToOne
//    @JoinColumn(name = "serviceConfiguration", nullable = false)
//    private SftpServiceConfigEntity serviceConfiguration;
//}
