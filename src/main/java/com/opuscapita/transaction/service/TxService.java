package com.opuscapita.transaction.service;

import com.opuscapita.transaction.model.TxSchemaV1;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

@Service
public class TxService {

    private final Logger log = LoggerFactory.getLogger(TxService.class);

//    @Getter
//    private RestTemplate restTemplate;
//    private final TNTConfiguration configuration;
//    private final String jwt;
//    private final String tenantId;

    @Getter
    private final TxSchemaV1 transaction = new TxSchemaV1();
//    private final String url;

//    private String response;

    public static String TOPICNAME = "sftp-gateway";

    private KafkaTemplate<String, String> kafkaTemplate;

    public TxService(
            KafkaTemplate<String, String> _kafkaTemplate
//            RestTemplateBuilder _restTemplateBuilder,
//            TNTConfiguration _configuration,
//            String _tenantId,
//            String _jwt
    ) {
        this.kafkaTemplate = _kafkaTemplate;
//        this.restTemplate = _restTemplateBuilder.build();
//        this.configuration = _configuration;
//        this.jwt = _jwt;
//        this.tenantId = _tenantId;
//        this.url = _configuration.getMethod() + "://" + _configuration.getUrl() + ":" + _configuration.getPort() + "/api/events";
    }

    public void sendTx() {
        ListenableFuture<SendResult<String, String>> future = kafkaTemplate.send(TOPICNAME, this.transaction.asJson());

        future.addCallback(new ListenableFutureCallback<SendResult<String, String>>() {

            @Override
            public void onSuccess(SendResult<String, String> result) {
                log.info("Sent message=[" + getTransaction().asJson() +
                        "] with offset=[" + result.getRecordMetadata().offset() + "]");
            }

            @Override
            public void onFailure(Throwable ex) {
                log.error("Unable to send message=["
                        + getTransaction().asJson() + "] due to : " + ex.getMessage());
            }
        });

        log.info("Send Transaction");
    }

}
