package com.opuscapita.sftp.model;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SftpConfigRepository extends JpaRepository<SftpConfigEntity, UUID> {

}
