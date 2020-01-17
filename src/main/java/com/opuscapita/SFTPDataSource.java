package com.opuscapita;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.Optional;

@Component
public class SFTPDataSource {

    @Value("${db-init.service-name:mysql}")
    private String serviceName;

    @Value("${db-init.user:root}")
    private String user;

    @Value("${db-init.password:''}")
    private String password;

    @Value("${db-init.database:gateway}")
    private String database;


    private final DiscoveryClient discoveryClient;

    @Autowired
    public SFTPDataSource(
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
            return opt.map(serviceInstance -> this.serviceName + "://" + this.serviceName + ':' + serviceInstance.getPort()).orElse("");
        }
        return opt.map(serviceInstance -> this.serviceName + "://" + serviceInstance.getHost() + ":" + serviceInstance.getPort()).orElse("");
    }


    @Bean
    @Primary
    public DataSource dataSource() {
        return DataSourceBuilder
                .create()
                .username(user)
                .password(password)
                .url("jdbc:" + this.getBootstrapUri() + "/" + database)
                .driverClassName("com.mysql.cj.jdbc.Driver")
                .build();
    }
}
