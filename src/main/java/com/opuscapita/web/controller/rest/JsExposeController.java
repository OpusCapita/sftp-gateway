package com.opuscapita.web.controller.rest;

import com.opuscapita.web.service.JsLoaderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/api/js")
public class JsExposeController extends AbstractRestController {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final JsLoaderService jsLoaderService;

    @Autowired
    public JsExposeController(
            final JsLoaderService _jsLoaderService
    ) {
        this.jsLoaderService = _jsLoaderService;
    }

    @GetMapping(value = "/{js}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public ResponseEntity<String> getJs(
            @PathVariable String js
    ) {
        List<String> jsFiles;
        ResponseEntity<String> responseEntity;
        try {
            jsFiles = this.jsLoaderService.getResourceFiles("/static/built/" + js);
            responseEntity = new ResponseEntity<>(jsFiles.toString(), HttpStatus.OK);
        } catch (IOException | NullPointerException e) {
            responseEntity = new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return responseEntity;
    }
}
