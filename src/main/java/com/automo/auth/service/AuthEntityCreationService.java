package com.automo.auth.service;

import com.automo.auth.entity.Auth;
import com.automo.accountType.service.AccountTypeService;
import com.automo.state.entity.State;
import com.automo.identifier.service.IdentifierService;
import com.automo.authRoles.service.AuthRolesService;
import com.automo.role.service.RoleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Serviço centralizado para criação de entidades que precisam de Auth associado
 * Elimina duplicação de código entre UserServiceImpl e AdminServiceImpl
 */
@Service
@Slf4j
public class AuthEntityCreationService {

    private final AuthService authService;
    private final AccountTypeService accountTypeService;
    private final PasswordEncoder passwordEncoder;
    private final IdentifierService identifierService;
    private final AuthRolesService authRolesService;
    private final RoleService roleService;

    public AuthEntityCreationService(@Lazy AuthService authService,
                                    AccountTypeService accountTypeService,
                                    PasswordEncoder passwordEncoder,
                                    IdentifierService identifierService,
                                    AuthRolesService authRolesService,
                                    RoleService roleService) {
        this.authService = authService;
        this.accountTypeService = accountTypeService;
        this.passwordEncoder = passwordEncoder;
        this.identifierService = identifierService;
        this.authRolesService = authRolesService;
        this.roleService = roleService;
    }

    /**
     * Cria uma entidade Auth completa com username único, identifiers e roles
     * @param email Email do usuário
     * @param name Nome para gerar username único
     * @param password Senha em texto plano (será encodada)
     * @param contact Contato/telefone
     * @param accountTypeId ID do tipo de conta
     * @param state Estado da entidade
     * @param entityType Tipo da entidade ("USER", "ADMIN", etc.)
     * @param defaultRoleName Nome da role padrão ("USER", "ADMIN", etc.)
     * @return Auth criado e salvo com todas as associações
     */
    @Transactional
    public Auth createAuthForEntity(String email, String name, String password, String contact, 
                                   Long accountTypeId, State state, String entityType, String defaultRoleName) {
        log.info("Iniciando criação de Auth para entidade tipo: {} com email: {}", entityType, email);
        
        // 1. Gerar username único
        String uniqueUsername = authService.generateUniqueUsername(name);
        log.debug("Username único gerado: {}", uniqueUsername);
        
        // 2. Criar e configurar Auth
        Auth auth = new Auth();
        auth.setEmail(email);
        auth.setUsername(uniqueUsername);
        auth.setPassword(passwordEncoder.encode(password));
        auth.setContact(contact);
        auth.setAccountType(accountTypeService.findById(accountTypeId));
        auth.setState(state);
        
        // 3. Salvar Auth
        Auth savedAuth = authService.save(auth);
        log.debug("Auth criado com sucesso. ID: {}, Username: {}", savedAuth.getId(), savedAuth.getUsername());
        
        // 4. Criar Identifier para a entidade
        try {
            identifierService.createIdentifierForEntity(savedAuth.getId(), entityType, state.getId());
            log.debug("Identifier criado para {} ID: {}", entityType, savedAuth.getId());
        } catch (Exception e) {
            log.warn("Erro ao criar Identifier para {} ID: {}. Erro: {}", entityType, savedAuth.getId(), e.getMessage());
        }
        
        // 5. Atribuir role padrão
        try {
            com.automo.role.entity.Role defaultRole = roleService.findByRole(defaultRoleName);
            authRolesService.createAuthRolesWithEntities(savedAuth, defaultRole, state);
            log.debug("Role '{}' atribuída para Auth ID: {}", defaultRoleName, savedAuth.getId());
        } catch (Exception e) {
            log.warn("Erro ao atribuir role '{}' para {} ID: {}. Erro: {}", 
                    defaultRoleName, entityType, savedAuth.getId(), e.getMessage());
            // Não falha a operação se não conseguir atribuir role
        }
        
        log.info("Auth criado com sucesso para {} - ID: {}, Email: {}, Username: {}", 
                entityType, savedAuth.getId(), savedAuth.getEmail(), savedAuth.getUsername());
        
        return savedAuth;
    }
    
    /**
     * Atualiza Auth existente mantendo integridade
     * @param existingAuth Auth existente para atualizar
     * @param email Novo email
     * @param password Nova senha (pode ser null/vazio para não alterar)
     * @param contact Novo contato
     * @param accountTypeId Novo tipo de conta
     * @param state Novo estado
     * @return Auth atualizado
     */
    @Transactional
    public Auth updateAuthForEntity(Auth existingAuth, String email, String password, 
                                   String contact, Long accountTypeId, State state) {
        log.info("Atualizando Auth ID: {} para email: {}", existingAuth.getId(), email);
        
        existingAuth.setEmail(email);
        
        // Só atualiza senha se fornecida
        if (password != null && !password.trim().isEmpty()) {
            existingAuth.setPassword(passwordEncoder.encode(password));
            log.debug("Senha atualizada para Auth ID: {}", existingAuth.getId());
        }
        
        existingAuth.setContact(contact);
        existingAuth.setAccountType(accountTypeService.findById(accountTypeId));
        existingAuth.setState(state);
        
        Auth updatedAuth = authService.save(existingAuth);
        log.info("Auth ID: {} atualizado com sucesso", updatedAuth.getId());
        
        return updatedAuth;
    }
}