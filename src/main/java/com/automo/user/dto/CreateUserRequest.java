package com.automo.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.List;

public record CreateUserRequest(
    // Dados de autenticação
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
    String password,
    
    @NotNull(message = "Tipo de conta é obrigatório")
    Long accountTypeId,
    
    @NotNull(message = "Estado é obrigatório")
    Long stateId,
    
    @NotNull(message = "Lista de roles é obrigatória")
    @Size(min = 1, message = "Pelo menos uma role deve ser fornecida")
    List<Long> roles,
    
    // Dados específicos do user
    @NotBlank(message = "Nome é obrigatório")
    String name,
    
    String img,
    
    String contacto,
    
    @NotNull(message = "ID do país é obrigatório")
    Long countryId,
    
    @NotNull(message = "ID do tipo de organização é obrigatório")
    Long organizationTypeId,
    
    Long provinceId
) {}
