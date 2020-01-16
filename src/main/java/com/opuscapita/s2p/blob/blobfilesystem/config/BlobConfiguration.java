package com.opuscapita.s2p.blob.blobfilesystem.config;

import com.opuscapita.SFTPjApplication;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.Optional;

@Data
@Configuration
public class BlobConfiguration {
    private String url;
    private String method;
    private int port;
    @Value(value = "${blob.access}")
    private String access;
    @Value(value = "${blob.type}")
    private String type;
    @Value(value = "${blob.service-name:blob}")
    private String serviceName;

    private final DiscoveryClient discoveryClient;

    @Autowired
    public BlobConfiguration(
            final DiscoveryClient _discoveryClient
    ) {
        this.discoveryClient = _discoveryClient;
    }

    @PostConstruct
    private void loadConfiguration() {
        Optional<ServiceInstance> opt = this.serviceUrl();
        if (opt.isPresent()) {
            if (Boolean.parseBoolean(System.getProperty(SFTPjApplication.LOCALPROPERTY))) {
                this.setUrl(this.getServiceName());
            } else {
                this.setUrl(opt.get().getHost());
            }
            this.setMethod(opt.get().getUri().getScheme());
            this.setPort(opt.get().getPort());
        }
    }

    private Optional<ServiceInstance> serviceUrl() {
        Optional<ServiceInstance> opt = Optional.empty();
        for (ServiceInstance si : this.discoveryClient.getInstances(this.getServiceName())) {
            opt = Optional.of(si);
            break;
        }

        return opt;
    }
}
