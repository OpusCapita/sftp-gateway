package com.opuscapita.sftp.model;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SftpRuleRepository extends JpaRepository<SftpRuleEntity, UUID> {

}
