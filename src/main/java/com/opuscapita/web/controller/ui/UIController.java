package com.opuscapita.web.controller.ui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class UIController {
    final Logger log = LoggerFactory.getLogger(this.getClass());

    @RequestMapping(value = "/")
    public String index() {
        return "index";
    }


}
