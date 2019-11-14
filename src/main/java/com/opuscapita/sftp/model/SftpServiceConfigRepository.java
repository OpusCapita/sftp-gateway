package com.opuscapita.sftp.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SftpServiceConfigRepository extends JpaRepository<SftpServiceConfigEntity, UUID> {

    List<SftpServiceConfigEntity> findByBusinessPartnerId(final String businessPartnerId);
    List<SftpServiceConfigEntity> findByServiceProfileId(final String serviceProfileId);
}
