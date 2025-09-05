package com.automo.payment.service;

import com.automo.config.FileStorageConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileStorageServiceImpl implements FileStorageService {

    private final FileStorageConfig fileStorageConfig;

    @Override
    public String storePaymentFile(MultipartFile file) {
        try {
            if (file.isEmpty()) {
                throw new RuntimeException("Failed to store empty file");
            }

            if (!isValidFileType(file)) {
                throw new RuntimeException("Invalid file type. Allowed types: " + 
                    Arrays.toString(fileStorageConfig.getAllowedExtensions()));
            }

            if (!isValidFileSize(file)) {
                throw new RuntimeException("File size exceeds maximum limit of " + 
                    fileStorageConfig.getMaxFileSize() + " bytes");
            }

            String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());
            String fileExtension = getFileExtension(originalFilename);
            String uniqueFilename = UUID.randomUUID().toString() + "." + fileExtension;

            Path uploadPath = Paths.get(fileStorageConfig.getUploadDir(), fileStorageConfig.getPaymentsDir());
            
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            Path targetLocation = uploadPath.resolve(uniqueFilename);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            log.info("File stored successfully: {}", uniqueFilename);
            return uniqueFilename;

        } catch (IOException ex) {
            log.error("Failed to store file", ex);
            throw new RuntimeException("Failed to store file", ex);
        }
    }

    @Override
    public boolean deletePaymentFile(String filename) {
        try {
            if (filename == null || filename.trim().isEmpty()) {
                return false;
            }

            Path filePath = Paths.get(fileStorageConfig.getUploadDir(), fileStorageConfig.getPaymentsDir(), filename);
            boolean deleted = Files.deleteIfExists(filePath);
            
            if (deleted) {
                log.info("File deleted successfully: {}", filename);
            } else {
                log.warn("File not found for deletion: {}", filename);
            }
            
            return deleted;
        } catch (IOException ex) {
            log.error("Failed to delete file: {}", filename, ex);
            return false;
        }
    }

    @Override
    public String getPaymentFilePath(String filename) {
        if (filename == null || filename.trim().isEmpty()) {
            return null;
        }
        
        Path filePath = Paths.get(fileStorageConfig.getUploadDir(), fileStorageConfig.getPaymentsDir(), filename);
        return filePath.toString();
    }

    @Override
    public boolean isValidFileType(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            return false;
        }

        String fileExtension = getFileExtension(originalFilename).toLowerCase();
        return Arrays.asList(fileStorageConfig.getAllowedExtensions()).contains(fileExtension);
    }

    @Override
    public boolean isValidFileSize(MultipartFile file) {
        return file.getSize() <= fileStorageConfig.getMaxFileSize();
    }

    @Override
    public String storeProductImage(MultipartFile file) {
        try {
            if (file.isEmpty()) {
                throw new RuntimeException("Failed to store empty file");
            }

            if (!isValidImageType(file)) {
                throw new RuntimeException("Invalid image type. Allowed types: " + 
                    Arrays.toString(fileStorageConfig.getAllowedImageExtensions()));
            }

            if (!isValidFileSize(file)) {
                throw new RuntimeException("File size exceeds maximum limit of " + 
                    fileStorageConfig.getMaxFileSize() + " bytes");
            }

            String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());
            String fileExtension = getFileExtension(originalFilename);
            String uniqueFilename = UUID.randomUUID().toString() + "." + fileExtension;

            Path uploadPath = Paths.get(fileStorageConfig.getUploadDir(), fileStorageConfig.getProductsDir());
            
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            Path targetLocation = uploadPath.resolve(uniqueFilename);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            log.info("Product image stored successfully: {}", uniqueFilename);
            return uniqueFilename;

        } catch (IOException ex) {
            log.error("Failed to store product image", ex);
            throw new RuntimeException("Failed to store product image", ex);
        }
    }

    @Override
    public boolean deleteProductImage(String filename) {
        try {
            if (filename == null || filename.trim().isEmpty()) {
                return false;
            }

            Path filePath = Paths.get(fileStorageConfig.getUploadDir(), fileStorageConfig.getProductsDir(), filename);
            boolean deleted = Files.deleteIfExists(filePath);
            
            if (deleted) {
                log.info("Product image deleted successfully: {}", filename);
            } else {
                log.warn("Product image not found for deletion: {}", filename);
            }
            
            return deleted;
        } catch (IOException ex) {
            log.error("Failed to delete product image: {}", filename, ex);
            return false;
        }
    }

    @Override
    public String getProductImagePath(String filename) {
        if (filename == null || filename.trim().isEmpty()) {
            return null;
        }
        
        Path filePath = Paths.get(fileStorageConfig.getUploadDir(), fileStorageConfig.getProductsDir(), filename);
        return filePath.toString();
    }

    @Override
    public boolean isValidImageType(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            return false;
        }

        String fileExtension = getFileExtension(originalFilename).toLowerCase();
        return Arrays.asList(fileStorageConfig.getAllowedImageExtensions()).contains(fileExtension);
    }

    private String getFileExtension(String filename) {
        if (filename == null || filename.lastIndexOf(".") == -1) {
            return "";
        }
        return filename.substring(filename.lastIndexOf(".") + 1);
    }
}