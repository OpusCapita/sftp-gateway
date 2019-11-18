package com.opuscapita.web.controller.rest;

import com.opuscapita.sftp.model.SftpServiceConfigEntity;
import com.opuscapita.web.service.EventActionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/api/evnt/")
public class EventController extends AbstractRestController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private EventActionService eventActionService;

    @Autowired
    public EventController(
            EventActionService _eventActionService
    ) {
        this.eventActionService = _eventActionService;
    }

    @GetMapping(value = "/{evntActionId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<List<SftpServiceConfigEntity>> getAllEventsActions(
            @RequestHeader(name = "X-User-Id-Token") String jwt,
            @PathVariable String evntActionId
    ) {
        logger.info(jwt);
        logger.info(evntActionId);
        if (Objects.isNull(jwt)) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        if (!this.canAccess(jwt)) {
            new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping(value = "/", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<List<EventActionService.EventEntry>> getAllEventsActions(
            @RequestHeader(name = "X-User-Id-Token") String jwt
    ) {
        logger.info(jwt);
        if (Objects.isNull(jwt)) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        if (!this.canAccess(jwt)) {
            new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        return new ResponseEntity<>(this.eventActionService.listFileUploadListener(), HttpStatus.OK);
    }

}
