package com.opuscapita.sftp.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SftpServiceConfigActionRepository extends JpaRepository<SftpServiceConfigAction, UUID> {

}
