package com.automo.auth.dto;

public record UserManagementResponse(
    String message,
    boolean success,
    CompleteUserResponse updatedUser
) {
    public static UserManagementResponse success(String message, CompleteUserResponse user) {
        return new UserManagementResponse(message, true, user);
    }
    
    public static UserManagementResponse success(String message) {
        return new UserManagementResponse(message, true, null);
    }
    
    public static UserManagementResponse error(String message) {
        return new UserManagementResponse(message, false, null);
    }
}