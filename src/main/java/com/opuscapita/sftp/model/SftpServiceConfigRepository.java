package com.opuscapita.sftp.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Repository
@Transactional
public interface SftpServiceConfigRepository extends JpaRepository<SftpServiceConfigEntity, UUID> {

    List<SftpServiceConfigEntity> findByBusinessPartnerId(final String businessPartnerId);

    List<SftpServiceConfigEntity> findByServiceProfileId(final String serviceProfileId);

    List<SftpServiceConfigEntity> findByBusinessPartnerIdAndServiceProfileId(final String businessPartnerId, final String serviceProfileId);

    void deleteAllByBusinessPartnerId(final String businessPartnerId);

    void deleteAllByBusinessPartnerIdAndServiceProfileId(final String businessPartnerId, final String serviceProfileId);

    void deleteAllByBusinessPartnerIdAndServiceProfileIdAndId(final String businessPartnerId, final String serviceProfileId, final UUID id);
}
