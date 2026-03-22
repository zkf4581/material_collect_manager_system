package com.gongdi.materialpoints.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.file")
public record FileStorageProperties(
        String storageType,
        String baseDir
) {
}
