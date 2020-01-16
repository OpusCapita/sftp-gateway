package com.opuscapita.bouncer.config;

import com.opuscapita.SFTPjApplication;
import lombok.Getter;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.kafka.core.KafkaAdmin;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Configuration
public class BouncerKafkaTopicConfig {
    @Getter
    @Value(value = "${sftp.service-name}")
    private String serviceName;
    @Getter
    @Value(value = "${kafka.service-name:kafka}")
    private String kafkaServiceName;
    @Getter
    @Value(value = "${bouncer.topic-name}")
    private String topic;
    @Getter
    @Value(value = "${kafka.number-of-partitions:12}")
    private int numberOfPartitions;
    @Getter
    @Value(value = "${kafka.replication-factor:1}")
    private short replicationFactor;

    private final DiscoveryClient discoveryClient;

    @Autowired
    public BouncerKafkaTopicConfig(
            final DiscoveryClient _discoveryClient
    ) {
        this.discoveryClient = _discoveryClient;
    }

    private Optional<ServiceInstance> serviceUrl() {
        Optional<ServiceInstance> opt = Optional.empty();
        for (ServiceInstance si : this.discoveryClient.getInstances(this.getKafkaServiceName())) {
            opt = Optional.of(si);
            break;
        }

        return opt;
    }

    public String getBootstrapUri() {
        Optional<ServiceInstance> opt = this.serviceUrl();
        if (Boolean.parseBoolean(System.getProperty(SFTPjApplication.LOCALPROPERTY))) {
            return opt.map(serviceInstance -> this.kafkaServiceName + ':' + serviceInstance.getPort()).orElse("");
        }
        return opt.map(serviceInstance -> serviceInstance.getHost() + ":" + serviceInstance.getPort()).orElse("");
    }

    @Bean
    public KafkaAdmin bouncerKafkaAdmin() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, getBootstrapUri());
        configs.put(AdminClientConfig.RECONNECT_BACKOFF_MS_CONFIG, 10000);
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
