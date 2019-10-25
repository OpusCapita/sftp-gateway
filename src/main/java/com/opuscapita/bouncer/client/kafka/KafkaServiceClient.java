package com.opuscapita.bouncer.client.kafka;

import com.opuscapita.bouncer.client.ServiceClient;
import com.opuscapita.bouncer.config.BouncerKafkaTopicConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

@Service
@Qualifier("kafkaServiceClient")
@Primary
public class KafkaServiceClient implements ServiceClient {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final BouncerKafkaTopicConfig kafkaTopic;

    @Autowired
    public KafkaServiceClient(
            final KafkaTemplate<String, String> _kafkaTemplate,
            final BouncerKafkaTopicConfig _kafkaTopic
    ) {
        this.kafkaTemplate = _kafkaTemplate;
        this.kafkaTopic = _kafkaTopic;
    }

    @Override
    public void sendEvent(final String message) {
        StringBuilder _messageBuilder = new StringBuilder();
        _messageBuilder.append("{")
                .append("\"serviceName\": \"" + this.kafkaTopic.getServiceName() + "\",")
                .append("\"permissions\": " + message)
                .append("}");
        ListenableFuture<SendResult<String, String>> future = this.kafkaTemplate.send(this.kafkaTopic.getTopic(), _messageBuilder.toString());

        future.addCallback(new ListenableFutureCallback<SendResult<String, String>>() {

            @Override
            public void onSuccess(SendResult<String, String> result) {
                log.info("Sent message=[" + message +
                        "] with offset=[" + result.getRecordMetadata().offset() + "]");
            }

            @Override
            public void onFailure(Throwable ex) {
                log.error("Unable to send message=["
                        + message + "] due to : " + ex.getMessage());
            }
        });

        log.info("Send Transaction");
    }
}
