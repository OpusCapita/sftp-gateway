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
        for (String str : args) {
            if ("--local".equals(str)) {
                System.setProperty(LOCALPROPERTY, "true");
            }
        }

        new SpringApplicationBuilder(SFTPjApplication.class)
                .bannerMode(Banner.Mode.OFF)
                .run(args);
    }
}
