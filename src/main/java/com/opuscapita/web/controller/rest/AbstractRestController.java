package com.opuscapita.web.controller.rest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Objects;

public class AbstractRestController {

    static final ResponseEntity UNAUTHORIZED = new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    static final ResponseEntity FORBIDDEN = new ResponseEntity<>(HttpStatus.FORBIDDEN);
    static final ResponseEntity OK = new ResponseEntity<>(HttpStatus.OK);

    boolean canAccess(final String _jwt) {
        if (Objects.isNull(_jwt)) {
            return false;
        }

        return false;
    }
}
