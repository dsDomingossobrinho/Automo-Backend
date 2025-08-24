package com.automo.auth.util;

import java.util.regex.Pattern;

public class ContactValidator {
    
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$"
    );
    
    private static final Pattern PHONE_PATTERN = Pattern.compile(
            "^[+]?[0-9\\s\\-\\(\\)]{7,15}$"
    );
    
    public static boolean isEmail(String contact) {
        return contact != null && EMAIL_PATTERN.matcher(contact.trim()).matches();
    }
    
    public static boolean isPhone(String contact) {
        if (contact == null) {
            return false;
        }
        
        String cleanContact = contact.replaceAll("[\\s\\-\\(\\)]", "");
        
        // Verifica se é um número de telefone válido
        return PHONE_PATTERN.matcher(contact.trim()).matches() && 
               cleanContact.matches("^[+]?[0-9]{7,15}$");
    }
    
    public static ContactType getContactType(String contact) {
        if (isEmail(contact)) {
            return ContactType.EMAIL;
        } else if (isPhone(contact)) {
            return ContactType.PHONE;
        } else {
            return ContactType.UNKNOWN;
        }
    }
    
    public enum ContactType {
        EMAIL,
        PHONE,
        UNKNOWN
    }
} 