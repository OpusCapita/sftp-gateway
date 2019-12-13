package com.opuscapita.web.controller.rest;

import com.opuscapita.sftp.model.SftpServiceConfigEntity;
import com.opuscapita.sftp.model.SftpServiceConfigRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/api/sftp")
public class RestfulController extends AbstractRestController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private SftpServiceConfigRepository serviceConfigRepository;

    @Autowired
    public RestfulController(
            SftpServiceConfigRepository _sftpServiceConfigRepository
    ) {
        this.serviceConfigRepository = _sftpServiceConfigRepository;
    }

    @GetMapping("/listHeaders")
    @ResponseBody
    public ResponseEntity<String> listAllHeaders(
            @RequestHeader Map<String, String> headers) {
        headers.forEach((key, value) -> logger.info(String.format("Header '%s' = %s", key, value)));

        return new ResponseEntity<>(
                String.format("Listed %d headers", headers.size()), HttpStatus.OK);
    }

    @GetMapping(value = "/{businesspartnerId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<List<SftpServiceConfigEntity>> getAllServiceConfigurations(
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

    @PostMapping(value = "/", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<List<SftpServiceConfigEntity>> postAllServiceConfigurations(
            @RequestHeader(name = "X-User-Id-Token") String jwt,
            @RequestBody List<SftpServiceConfigEntity> configEntityList
    ) {
        if (Objects.isNull(jwt)) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        if (!this.canAccess(jwt)) {
            new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        return new ResponseEntity<>(this.serviceConfigRepository.saveAll(configEntityList), HttpStatus.OK);
    }

    @PutMapping(value = "/", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<List<SftpServiceConfigEntity>> putServiceConfiguration(
            @RequestHeader(name = "X-User-Id-Token") String jwt,
            @RequestBody SftpServiceConfigEntity configEntity
    ) {
        if (Objects.isNull(jwt)) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        if (!this.canAccess(jwt)) {
            new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        try {
            this.serviceConfigRepository.save(configEntity);
        } catch (Throwable e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(this.serviceConfigRepository.findAll(), HttpStatus.OK);
    }

    @DeleteMapping(value = "/", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<List<SftpServiceConfigEntity>> deleteServiceConfiguration(
            @RequestHeader(name = "X-User-Id-Token") String jwt,
            @RequestBody SftpServiceConfigEntity configEntityList
    ) {
        if (Objects.isNull(jwt)) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        if (!this.canAccess(jwt)) {
            new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        try {
            this.serviceConfigRepository.delete(configEntityList);
        } catch (Throwable e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(this.serviceConfigRepository.findAll(), HttpStatus.OK);
    }

//    @DeleteMapping(value = "/", consumes = MediaType.APPLICATION_JSON_VALUE)
//    @ResponseBody
//    public ResponseEntity<List<SftpServiceConfigEntity>> deleteServiceConfigurations(
//            @RequestHeader(name = "X-User-Id-Token") String jwt,
//            @RequestBody List<SftpServiceConfigEntity> configEntityList
//    ) {
//        if (Objects.isNull(jwt)) {
//            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
//        }
//        if (!this.canAccess(jwt)) {
//            new ResponseEntity<>(HttpStatus.FORBIDDEN);
//        }
//        try {
//            this.serviceConfigRepository.deleteInBatch(configEntityList);
//        } catch (Throwable e) {
//            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//        return new ResponseEntity<>(HttpStatus.OK);
//    }
}
