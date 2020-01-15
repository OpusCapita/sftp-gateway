package com.opuscapita.auth.config;

import com.opuscapita.SFTPjApplication;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import javax.annotation.PostConstruct;
import java.util.Optional;

//@PropertySource(value = "classpath:application-auth.properties")
@Data
@Configuration
public class AuthConfiguration {
    @Value(value = "${auth.server.endpoint}")
    private String endpoint;
    @Value(value = "${auth.server.clientKey}")
    private String clientKey;
    @Value("${service-client/username}")
    private String opt;
    @Value(value = "${auth.server.clientSecret}")
    private String clientSecret;
    @Value(value = "${auth.service-name:auth}")
    private String serviceName;

    private final DiscoveryClient discoveryClient;

    @Autowired
    public AuthConfiguration(
            final DiscoveryClient _discoveryClient
    ) {
        this.discoveryClient = _discoveryClient;
    }

    @PostConstruct
    private void loadConfiguration() {
        System.out.println(this.opt);
    }

    private Optional<ServiceInstance> serviceUrl() {
        Optional<ServiceInstance> opt = Optional.empty();
        for (ServiceInstance si : this.discoveryClient.getInstances(this.getServiceName())) {
            opt = Optional.of(si);
            break;
        }

        return opt;
    }

    public String getUrl() {
        Optional<ServiceInstance> opt = this.serviceUrl();
        if (Boolean.parseBoolean(System.getProperty(SFTPjApplication.LOCALPROPERTY))) {
            return opt.map(serviceInstance -> serviceInstance.getUri().getScheme() + "://" + this.serviceName + ':' + serviceInstance.getPort()).orElse("");
        }
        return opt.map(serviceInstance -> serviceInstance.getUri().toString()).orElse("");
    }
}
