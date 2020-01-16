package com.opuscapita.transaction.service;

import com.opuscapita.auth.model.AuthResponse;
import com.opuscapita.sftp.model.SftpServiceConfigEntity;
import com.opuscapita.transaction.config.TNTConfiguration;
import com.opuscapita.transaction.model.Tx;
import com.opuscapita.transaction.model.properties.Version;
import com.opuscapita.transaction.utils.TxUtils;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;
import org.springframework.web.client.RestTemplate;

public class TxService {

    private final Logger log = LoggerFactory.getLogger(TxService.class);

    @Getter
    private final TNTConfiguration tntConfiguration;
    @Getter
    private final SftpServiceConfigEntity configEntity;
    @Getter
    @Setter
    private Tx transaction = TxUtils.createEventTx(
            Version.V_1_5,
            "{actionId}",
            "{businessPartner}",
            "{senderBusinessparner}",
            "{gatewayId}");

    @Value(value = "${kafka.topic-name}")
    public String topicname;

    private KafkaTemplate<String, String> kafkaTemplate;

    public TxService(
            final SftpServiceConfigEntity _configEntity,
            final KafkaTemplate<String, String> _kafkaTemplate,
            final TNTConfiguration _configuration
    ) {
        this.configEntity = _configEntity;
        this.kafkaTemplate = _kafkaTemplate;
        this.tntConfiguration = _configuration;
    }

    public void sendTx(final AuthResponse authenticationToken) {
        if (this.tntConfiguration.isActive()) {
            try {
                this.sendTntTx(authenticationToken.getId_token());
                log.info("Tnt Transaction Event sent");
            } catch (Exception e) {
                e.printStackTrace();
                log.error("Tnt Transaction could not be started: {}", e.getMessage());
            }
        }

        try {
            this.sendKafkaTx();
            log.info("Kafka Transaction Event sent");
        } catch (Exception e) {
            log.error("Kafka Transaction could not be started: {}", e.getMessage());
        }
    }

    private ResponseEntity<String> sendTntTx(final String jwt) {
        RestTemplate rest = new RestTemplate();
        HttpHeaders header = new HttpHeaders();
        header.setContentType(MediaType.APPLICATION_JSON);
        header.set("X-User-Id-Token", jwt);
        HttpEntity<String> entity = new HttpEntity<>(this.transaction.toString(), header);
        return rest.exchange(this.tntConfiguration.getUri(), HttpMethod.POST, entity, String.class);
    }

    private void sendKafkaTx() {
        ListenableFuture<SendResult<String, String>> future = kafkaTemplate.send(topicname, this.transaction.toString());

        future.addCallback(new ListenableFutureCallback<SendResult<String, String>>() {

            @Override
            public void onSuccess(SendResult<String, String> result) {
                log.info("Sent message=[" + getTransaction().toString() +
                        "] with offset=[" + result.getRecordMetadata().offset() + "]");
            }

            @Override
            public void onFailure(Throwable ex) {
                log.error("Unable to send message=["
                        + getTransaction().toString() + "] due to : " + ex.getMessage());
            }
        });
    }

}
