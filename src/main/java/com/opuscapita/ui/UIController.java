package com.opuscapita.ui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UIController {
    final Logger log = LoggerFactory.getLogger(this.getClass());

    @GetMapping("/")
    public String index() {
        log.info("Endpoint \"/\"");
        return "index";
    }
}
