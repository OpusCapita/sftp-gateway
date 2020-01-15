package com.opuscapita.transaction.config;

import com.opuscapita.SFTPjApplication;
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
@PropertySource(value = "classpath:application-kafka.properties")
public class TxKafkaTopicConfig {

    @Value(value = "${kafka.serviceName:kafka}")
    private String serviceName;
    @Value(value = "${kafka.topicName}")
    private String topic;
    @Value(value = "${kafka.numberOfPartitions}")
    private int numberOfPartitions;
    @Value(value = "${kafka.replicationFactor}")
    private short replicationFactor;

    private final DiscoveryClient discoveryClient;

    @Autowired
    public TxKafkaTopicConfig(
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
    public KafkaAdmin kafkaAdmin() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, this.getBootstrapUri());
        return new KafkaAdmin(configs);
    }

    @Bean
    public NewTopic sftpGatewayTopic() {
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
