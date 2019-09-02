package com.opuscapita.tnt.service;

import com.opuscapita.tnt.model.TxSchemaV1;
import com.opuscapita.tnt.model.config.TNTConfiguration;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

public class TxService {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Getter
    private RestTemplate restTemplate;
    private final TNTConfiguration configuration;
    private final String jwt;
    private final String tenantId;

    @Getter
    private final TxSchemaV1 transaction = new TxSchemaV1();
    private final String url;

    private String response;

    public TxService(
            RestTemplateBuilder _restTemplateBuilder,
            TNTConfiguration _configuration,
            String _tenantId,
            String _jwt
    ) {
        this.restTemplate = _restTemplateBuilder.build();
        this.configuration = _configuration;
        this.jwt = _jwt;
        this.tenantId = _tenantId;
        this.url = _configuration.getMethod() + "://" + _configuration.getUrl() + ":" + _configuration.getPort() + "/api/events";
    }

    public boolean sendTx() {
        log.info("Send Transaction");

        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        headers.set("X-User-Id-Token", jwt);
        HttpEntity<String> entity = new HttpEntity<>("body", headers);
        log.info("Setting http headers content type to application json");

        restTemplate.exchange(this.url, HttpMethod.POST, entity, String.class);
        return true;
    }

}
