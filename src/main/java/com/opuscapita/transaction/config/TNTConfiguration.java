package com.opuscapita.transaction.config;

import com.opuscapita.SFTPjApplication;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.util.Optional;

@Configuration
@Data
public class TNTConfiguration {

    @Value(value = "${tnt.event-path}")
    private String path;
    @Value(value = "${tnt.active:true}")
    private boolean active;
    @Value(value = "${tnt.service-name:tnt}")
    private String serviceName;

    private final DiscoveryClient discoveryClient;

    @Autowired
    public TNTConfiguration(
            final DiscoveryClient _discoveryClient
    ) {
        this.discoveryClient = _discoveryClient;
    }

    private Optional<ServiceInstance> serviceUrl() {
        Optional<ServiceInstance> opt = Optional.empty();
        for (ServiceInstance si : this.discoveryClient.getInstances(this.getServiceName())) {
            opt = Optional.of(si);
            break;
        }

        return opt;
    }

    public String getUri() {
        Optional<ServiceInstance> opt = this.serviceUrl();
        if (Boolean.parseBoolean(System.getProperty(SFTPjApplication.LOCALPROPERTY))) {
            return opt.map(serviceInstance -> serviceInstance.getUri().getScheme() + "://" + this.serviceName + ':' + serviceInstance.getPort() + this.path).orElse("");
        }
        return opt.map(serviceInstance -> serviceInstance.getUri().toString() + this.path).orElse("");
    }
}