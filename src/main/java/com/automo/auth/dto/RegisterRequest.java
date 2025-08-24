package com.automo.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record RegisterRequest(
    @Email(message = "Formato de email inválido")
    @NotBlank(message = "Email é obrigatório")
    String email,
    
    @NotBlank(message = "Contato é obrigatório")
    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Formato de contato inválido")
    String contact,
    
    @NotBlank(message = "Username é obrigatório")
    @Pattern(regexp = "^[a-zA-Z0-9_]{3,20}$", message = "Username deve ter entre 3 e 20 caracteres, apenas letras, números e underscore")
    String username,
    
    @NotBlank(message = "Senha é obrigatória")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d@$!%*?&]{8,}$", 
             message = "Senha deve ter pelo menos 8 caracteres, incluindo maiúscula, minúscula e número")
    String password
) {} 