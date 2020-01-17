package com.opuscapita;

import org.springframework.boot.Banner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

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

    public static void main(String[] args) throws IOException, InterruptedException {
        System.setProperty(LOCALPROPERTY, "false");
        Process process = null;
        for (String str : args) {
            if ("--local".equals(str)) {
                System.setProperty(LOCALPROPERTY, "true");
            } else if ("--setup-consul".equals(str)) {
                process = Runtime.getRuntime().exec("sh -c /usr/app/setup-consul.sh");
            }
        }

        if (process != null) {
            process.waitFor();
        }
        new SpringApplicationBuilder(SFTPjApplication.class)
                .bannerMode(Banner.Mode.OFF)
                .run(args);
    }
}
