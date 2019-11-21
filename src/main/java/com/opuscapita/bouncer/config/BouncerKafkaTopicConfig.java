package com.opuscapita.bouncer.config;

import lombok.Getter;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.kafka.core.KafkaAdmin;

import java.util.HashMap;
import java.util.Map;

@Configuration
@PropertySource(value = "classpath:application-bouncer.properties")
public class BouncerKafkaTopicConfig {
    @Getter
    @Value(value = "${bouncer.service-name}")
    private String serviceName;
    @Getter
    @Value(value = "${bouncer.kafka.bootstrapAddress}")
    private String bootstrapAddress;
    @Getter
    @Value(value = "${bouncer.kafka.topicName}")
    private String topic;
    @Getter
    @Value(value = "${bouncer.kafka.numberOfPartitions}")
    private int numberOfPartitions;
    @Getter
    @Value(value = "${bouncer.kafka.replicationFactor}")
    private short replicationFactor;

    @Bean
    public KafkaAdmin bouncerKafkaAdmin() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        configs.put(AdminClientConfig.RECONNECT_BACKOFF_MS_CONFIG, 5000);
        return new KafkaAdmin(configs);
    }

    @Bean
    public NewTopic bouncerTopic() {
        return this.newTopic(this.topic, this.numberOfPartitions, this.replicationFactor);
    }

    private NewTopic newTopic(
            final String _topic,
            final int _numberOfPartitions,
            final short _replicationFactor
    ) {
        return new NewTopic(_topic, _numberOfPartitions, _replicationFactor);
    }
}
