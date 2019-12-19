package com.opuscapita.web.controller.rest;

import com.opuscapita.web.service.JsLoaderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/static")
public class JsExposeController extends AbstractRestController {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final JsLoaderService jsLoaderService;

    @Autowired
    public JsExposeController(
            final JsLoaderService _jsLoaderService
    ) {
        this.jsLoaderService = _jsLoaderService;
    }

    private ResponseEntity<String> getJs(
            String js
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

    @GetMapping(value = "/{js}", produces = "application/javascript")
    @ResponseBody
    public ResponseEntity<String> getJsFromFileSystem(
            @PathVariable String js
    ) {
        String jsFile;
        ResponseEntity<String> responseEntity;
        try {
            jsFile = this.jsLoaderService.getResourceFromFileSystem(js);
            responseEntity = new ResponseEntity<>(jsFile, HttpStatus.OK);
        } catch (IOException | NullPointerException e) {
            logger.warn("Javascript file could not be found. Loading from the Archive");
            responseEntity = this.getJs(js);
        }
        return responseEntity;
    }
}
