package com.automo.payment.service;

import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {
    
    String storePaymentFile(MultipartFile file);
    
    boolean deletePaymentFile(String filename);
    
    String getPaymentFilePath(String filename);
    
    String storeProductImage(MultipartFile file);
    
    boolean deleteProductImage(String filename);
    
    String getProductImagePath(String filename);
    
    boolean isValidImageType(MultipartFile file);
    
    boolean isValidFileType(MultipartFile file);
    
    boolean isValidFileSize(MultipartFile file);
}