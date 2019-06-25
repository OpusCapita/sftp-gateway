package com.opuscapita.sftp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableConfigurationProperties
@ComponentScan({"com.opuscapita.sftp"})
public class SftPjApplication {

	public static void main(String[] args) {
		SpringApplication.run(SftPjApplication.class, args);
	}

}
