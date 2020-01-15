package com.opuscapita.web.controller.rest;

import java.util.Objects;

public class AbstractRestController {

    boolean canAccess(final String _jwt) {
        if (Objects.isNull(_jwt)) {
            return false;
        }

        return true;
    }
}
