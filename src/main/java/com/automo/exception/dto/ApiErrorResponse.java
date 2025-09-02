package com.automo.exception.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Map;

@Getter
@Setter
@Builder
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiErrorResponse {
    
    private String errorCode;
    private String message;
    private int status;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime timestamp;
    
    private String path;
    private Map<String, String> fieldErrors;
    
    public ApiErrorResponse(String errorCode, String message, int status) {
        this.errorCode = errorCode;
        this.message = message;
        this.status = status;
        this.timestamp = LocalDateTime.now();
    }
    
    public ApiErrorResponse(String errorCode, String message, int status, String path) {
        this(errorCode, message, status);
        this.path = path;
    }
    
    public ApiErrorResponse(String errorCode, String message, int status, String path, Map<String, String> fieldErrors) {
        this(errorCode, message, status, path);
        this.fieldErrors = fieldErrors;
    }
}