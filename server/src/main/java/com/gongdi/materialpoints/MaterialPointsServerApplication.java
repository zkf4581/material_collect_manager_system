package com.gongdi.materialpoints;

import com.gongdi.materialpoints.config.FileStorageProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(FileStorageProperties.class)
public class MaterialPointsServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(MaterialPointsServerApplication.class, args);
	}

}
