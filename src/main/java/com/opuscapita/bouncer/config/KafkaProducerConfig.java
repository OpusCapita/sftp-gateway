package com.opuscapita.bouncer.config;

import com.opuscapita.SFTPjApplication;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Configuration
public class KafkaProducerConfig {

    @Value(value = "${kafka.service-name}")
    private String serviceName;

    private final DiscoveryClient discoveryClient;

    @Autowired
    public KafkaProducerConfig(
            final DiscoveryClient _discoveryClient
    ) {
        this.discoveryClient = _discoveryClient;
    }

    private Optional<ServiceInstance> serviceUrl() {
        Optional<ServiceInstance> opt = Optional.empty();
        for (ServiceInstance si : this.discoveryClient.getInstances(this.serviceName)) {
            opt = Optional.of(si);
            break;
        }

        return opt;
    }

    public String getBootstrapUri() {
        Optional<ServiceInstance> opt = this.serviceUrl();
        if (Boolean.parseBoolean(System.getProperty(SFTPjApplication.LOCALPROPERTY))) {
            return opt.map(serviceInstance -> this.serviceName + ':' + serviceInstance.getPort()).orElse("");
        }
        return opt.map(serviceInstance -> serviceInstance.getHost() + ":" + serviceInstance.getPort()).orElse("");
    }

    @Bean
    public ProducerFactory<String, String> producerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(
                ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
                this.getBootstrapUri());
        configProps.put(
                ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
                StringSerializer.class);
        configProps.put(
                ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                StringSerializer.class);
        configProps.put(
                ProducerConfig.RECONNECT_BACKOFF_MS_CONFIG,
                10000);
        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    public KafkaTemplate<String, String> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }
}
