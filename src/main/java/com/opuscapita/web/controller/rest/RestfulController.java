package com.opuscapita.web.controller.rest;

import com.opuscapita.sftp.model.SftpServiceConfigEntity;
import com.opuscapita.sftp.model.SftpServiceConfigRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@RestController
@RequestMapping("/api/configs")
public class RestfulController extends AbstractRestController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private SftpServiceConfigRepository serviceConfigRepository;

    @Autowired
    public RestfulController(
            SftpServiceConfigRepository _sftpServiceConfigRepository
    ) {
        this.serviceConfigRepository = _sftpServiceConfigRepository;
    }

    /*
     * GET
     */

    /**
     * Returns all Service Configuration Entites from the Database
     *
     * @param jwt
     * @return
     */
    @GetMapping(value = "/", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<List<SftpServiceConfigEntity>> getAllServiceConfigurations(
            @RequestHeader(name = "X-User-Id-Token") String jwt
    ) {
        if (Objects.isNull(jwt)) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        if (!this.canAccess(jwt)) {
            new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        return new ResponseEntity<>(this.serviceConfigRepository.findAll(), HttpStatus.OK);
    }

    /**
     * Returns all Service Configuration Entites from the Database for a certain Business Partner
     *
     * @param jwt
     * @param businesspartnerId
     * @return
     */
    @GetMapping(value = "/{businesspartnerId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<List<SftpServiceConfigEntity>> getAllServiceConfigurationsByBusinesspartner(
            @RequestHeader(name = "X-User-Id-Token") String jwt,
            @PathVariable String businesspartnerId
    ) {
        if (Objects.isNull(jwt)) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        if (!this.canAccess(jwt)) {
            new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        return new ResponseEntity<>(this.serviceConfigRepository.findByBusinessPartnerId(businesspartnerId), HttpStatus.OK);
    }

    /**
     * Returns all Service Configuration Entites from the Database filtered by Business Partner and Service Profile
     *
     * @param jwt
     * @param businesspartnerId
     * @param serviceProfileId
     * @return
     */
    @GetMapping(value = "/{businesspartnerId}/{serviceProfileId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<List<SftpServiceConfigEntity>> getAllServiceConfigurationsByServiceProfileId(
            @RequestHeader(name = "X-User-Id-Token") String jwt,
            @PathVariable String businesspartnerId,
            @PathVariable String serviceProfileId
    ) {
        if (Objects.isNull(jwt)) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        if (!this.canAccess(jwt)) {
            new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        return new ResponseEntity<>(this.serviceConfigRepository.findByBusinessPartnerIdAndServiceProfileId(businesspartnerId, serviceProfileId), HttpStatus.OK);
    }

    /*
     * CREATE
     */

    /**
     * Creates a new Service Configuration Entity
     *
     * @param jwt
     * @param configEntity
     * @return
     */
    @PostMapping(value = "/", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<List<SftpServiceConfigEntity>> createServiceConfiguration(
            @RequestHeader(name = "X-User-Id-Token") String jwt,
            @RequestBody SftpServiceConfigEntity configEntity
    ) {
        if (Objects.isNull(jwt)) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        if (!this.canAccess(jwt)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        if (configEntity.getId() != null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        try {
            this.serviceConfigRepository.saveAndFlush(configEntity);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(this.serviceConfigRepository.findByBusinessPartnerIdAndServiceProfileId(configEntity.getBusinessPartnerId(), configEntity.getServiceProfileId()), HttpStatus.OK);
    }

    /*
     * EDIT
     */

    /**
     * Overwrites an existing Service Configuration Entity
     *
     * @param jwt
     * @param configEntity
     * @return
     */
    @PutMapping(value = "/", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<List<SftpServiceConfigEntity>> editServiceConfiguration(
            @RequestHeader(name = "X-User-Id-Token") String jwt,
            @RequestBody SftpServiceConfigEntity configEntity
    ) {
        if (Objects.isNull(jwt)) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        if (!this.canAccess(jwt)) {
            new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        if (configEntity.getId() == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        try {
            this.serviceConfigRepository.save(configEntity);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(this.serviceConfigRepository.findByBusinessPartnerIdAndServiceProfileId(configEntity.getBusinessPartnerId(), configEntity.getServiceProfileId()), HttpStatus.OK);
    }

    /*
     * DELETE
     */

    /**
     * Delete all Configuration Entites for a certain business Partner
     *
     * @param jwt
     * @param businessPartnerId
     * @return
     */
    @DeleteMapping(value = "/{businessPartnerId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<List<SftpServiceConfigEntity>> deleteServiceConfigurationsByBusinessPartnerId(
            @RequestHeader(name = "X-User-Id-Token") String jwt,
            @PathVariable String businessPartnerId
    ) {
        if (Objects.isNull(jwt)) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        if (!this.canAccess(jwt)) {
            new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        try {
            this.serviceConfigRepository.deleteAllByBusinessPartnerId(businessPartnerId);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * Delete all Configuration Entites for a certain business Partner based on a service Profile
     *
     * @param jwt
     * @param businessPartnerId
     * @param serviceProfileId
     * @return
     */
    @DeleteMapping(value = "/{businessPartnerId}/{serviceProfileId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<List<SftpServiceConfigEntity>> deleteServiceConfigurationsByServiceProfileId(
            @RequestHeader(name = "X-User-Id-Token") String jwt,
            @PathVariable String businessPartnerId,
            @PathVariable String serviceProfileId
    ) {
        if (Objects.isNull(jwt)) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        if (!this.canAccess(jwt)) {
            new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        try {
            this.serviceConfigRepository.deleteAllByBusinessPartnerIdAndServiceProfileId(businessPartnerId, serviceProfileId);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * Delete one Configuration Entity from a service Profile
     *
     * @param jwt
     * @param businessPartnerId
     * @param serviceProfileId
     * @param configId
     * @return
     */
    @DeleteMapping(value = "/{businessPartnerId}/{serviceProfileId}/{configId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<List<SftpServiceConfigEntity>> deleteServiceConfiguration(
            @RequestHeader(name = "X-User-Id-Token") String jwt,
            @PathVariable String businessPartnerId,
            @PathVariable String serviceProfileId,
            @PathVariable String configId
    ) {
        if (Objects.isNull(jwt)) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        if (!this.canAccess(jwt)) {
            new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        try {
            this.serviceConfigRepository.deleteAllByBusinessPartnerIdAndServiceProfileIdAndId(businessPartnerId, serviceProfileId, UUID.fromString(configId));
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(this.serviceConfigRepository.findByBusinessPartnerIdAndServiceProfileId(businessPartnerId, serviceProfileId), HttpStatus.OK);
    }

}
