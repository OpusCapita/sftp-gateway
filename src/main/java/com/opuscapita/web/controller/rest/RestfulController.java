package com.opuscapita.web.controller.rest;

import com.opuscapita.sftp.model.SftpServiceConfigEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/sftp")
public class RestfulController extends AbstractRestController{

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @GetMapping(value = "/{businesspartnerId}", produces= MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<SftpServiceConfigEntity>> getAllServiceConfigurations(
            @RequestHeader(name = "X-User-Id-Token") String jwt,
            @PathVariable String businesspartnerId
    ) {
        logger.info(jwt);
        logger.info(businesspartnerId);
        if(Objects.isNull(jwt)) {
            return UNAUTHORIZED;
        }
        if(!this.canAccess(jwt)) {
            return FORBIDDEN;
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
