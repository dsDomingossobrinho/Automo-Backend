package com.automo.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import lombok.Getter;
import lombok.Setter;

@Configuration
@ConfigurationProperties(prefix = "file.storage")
@Getter
@Setter
public class FileStorageConfig {
    
    private String uploadDir = "src/main/resources/static/storage";
    private String paymentsDir = "payments";
    private String productsDir = "products";
    private long maxFileSize = 5242880; // 5MB
    private String[] allowedExtensions = {"jpg", "jpeg", "png", "pdf", "doc", "docx"};
    private String[] allowedImageExtensions = {"jpg", "jpeg", "png"};
}