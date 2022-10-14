package com.fdmgroup.documentuploader;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan("com.fdmgroup.documentuploader.config")
public class DocumentUploaderServicesApplication {

	public static void main(String[] args) {
		SpringApplication.run(DocumentUploaderServicesApplication.class, args);
	}

}
