package com.opuscapita.web.controller.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/api/health")
public class HealthController extends AbstractRestController{

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @GetMapping(value = "/check")
    public ResponseEntity<String> healthCheck() {
        logger.info("HealthCheck successfull");
        return new ResponseEntity<>("Yes, I'm alive!", HttpStatus.OK);
    }
}