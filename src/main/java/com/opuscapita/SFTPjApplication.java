package com.opuscapita;

import org.springframework.boot.Banner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class SFTPjApplication implements CommandLineRunner {

    public static final String LOCALPROPERTY = "application.local";

    @Override
    public void run(String... args) throws Exception {
    }

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.build();
    }

    public static void main(String[] args) {
        System.setProperty(LOCALPROPERTY, "false");
        System.setProperty("bouncer.permissions.file", "./acl.json");
        for (String str : args) {
            switch (str) {
                case "--create-database":
                    System.setProperty("spring.jpa.hibernate.ddl-auto", "update");
                    break;
                case "--port":
                    System.setProperty("sftp.server.port", str.split("=")[1]);
                    break;
                case "--local":
                    System.setProperty(LOCALPROPERTY, "true");
                    break;
                case "--permissions":
                    System.setProperty("bouncer.permissions.file", str.split("=")[1]);
                    break;
                default:
                    break;
            }
        }

        new SpringApplicationBuilder(SFTPjApplication.class)
                .bannerMode(Banner.Mode.OFF)
                .run(args);
    }
}
