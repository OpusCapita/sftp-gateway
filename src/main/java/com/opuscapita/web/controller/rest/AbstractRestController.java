package com.opuscapita.web.controller.rest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Objects;

public class AbstractRestController {

    boolean canAccess(final String _jwt) {
        if (Objects.isNull(_jwt)) {
            return false;
        }

        return true;
    }
}
