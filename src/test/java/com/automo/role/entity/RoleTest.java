package com.automo.role.entity;

import com.automo.test.config.BaseTestConfig;
import com.automo.test.utils.TestDataFactory;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@BaseTestConfig
@DisplayName("Tests for Role Entity")
class RoleTest {

    @Autowired
    private Validator validator;

    @Test
    @DisplayName("Should create valid Role entity")
    void shouldCreateValidRoleEntity() {
        // Given
        Role role = TestDataFactory.createUserRole();
        
        // When
        Set<ConstraintViolation<Role>> violations = validator.validate(role);
        
        // Then
        assertTrue(violations.isEmpty());
        assertEquals("USER", role.getRole());
        assertNull(role.getDescription());
    }

    @Test
    @DisplayName("Should create valid Role with description")
    void shouldCreateValidRoleWithDescription() {
        // Given
        Role role = new Role();
        role.setRole("ADMIN");
        role.setDescription("Administrator role");
        
        // When
        Set<ConstraintViolation<Role>> violations = validator.validate(role);
        
        // Then
        assertTrue(violations.isEmpty());
        assertEquals("ADMIN", role.getRole());
        assertEquals("Administrator role", role.getDescription());
    }

    @Test
    @DisplayName("Should fail validation with null role")
    void shouldFailValidationWithNullRole() {
        // Given
        Role role = new Role();
        role.setRole(null);
        role.setDescription("Test description");
        
        // When
        Set<ConstraintViolation<Role>> violations = validator.validate(role);
        
        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> 
            v.getPropertyPath().toString().equals("role") && 
            v.getMessage().contains("Role name is required")));
    }

    @Test
    @DisplayName("Should fail validation with blank role")
    void shouldFailValidationWithBlankRole() {
        // Given
        Role role = new Role();
        role.setRole("");
        role.setDescription("Test description");
        
        // When
        Set<ConstraintViolation<Role>> violations = validator.validate(role);
        
        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> 
            v.getPropertyPath().toString().equals("role") && 
            v.getMessage().contains("Role name is required")));
    }

    @Test
    @DisplayName("Should fail validation with whitespace-only role")
    void shouldFailValidationWithWhitespaceOnlyRole() {
        // Given
        Role role = new Role();
        role.setRole("   ");
        role.setDescription("Test description");
        
        // When
        Set<ConstraintViolation<Role>> violations = validator.validate(role);
        
        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> 
            v.getPropertyPath().toString().equals("role") && 
            v.getMessage().contains("Role name is required")));
    }

    @Test
    @DisplayName("Should create Role with null description")
    void shouldCreateRoleWithNullDescription() {
        // Given
        Role role = new Role();
        role.setRole("MANAGER");
        role.setDescription(null);
        
        // When
        Set<ConstraintViolation<Role>> violations = validator.validate(role);
        
        // Then
        assertTrue(violations.isEmpty());
        assertEquals("MANAGER", role.getRole());
        assertNull(role.getDescription());
    }

    @Test
    @DisplayName("Should create Role with empty description")
    void shouldCreateRoleWithEmptyDescription() {
        // Given
        Role role = new Role();
        role.setRole("AGENT");
        role.setDescription("");
        
        // When
        Set<ConstraintViolation<Role>> violations = validator.validate(role);
        
        // Then
        assertTrue(violations.isEmpty());
        assertEquals("AGENT", role.getRole());
        assertEquals("", role.getDescription());
    }

    @Test
    @DisplayName("Should create different system roles")
    void shouldCreateDifferentSystemRoles() {
        // Given
        String[] validRoles = {
            "USER",
            "ADMIN", 
            "AGENT",
            "MANAGER",
            "SUPERVISOR",
            "OWNER",
            "GUEST",
            "MODERATOR"
        };
        
        String[] descriptions = {
            "Standard user role",
            "Administrator with full access",
            "Agent with limited access",
            "Manager role",
            "Supervisor role",
            "Owner role with all permissions",
            "Guest with read-only access",
            "Moderator role"
        };
        
        // When & Then
        for (int i = 0; i < validRoles.length; i++) {
            Role role = new Role();
            role.setRole(validRoles[i]);
            role.setDescription(descriptions[i]);
            
            Set<ConstraintViolation<Role>> violations = validator.validate(role);
            
            assertTrue(violations.isEmpty(), "Role " + validRoles[i] + " should be valid");
            assertEquals(validRoles[i], role.getRole());
            assertEquals(descriptions[i], role.getDescription());
        }
    }

    @Test
    @DisplayName("Should handle long role names")
    void shouldHandleLongRoleNames() {
        // Given
        String longRole = "VERY_LONG_ROLE_NAME_THAT_MIGHT_BE_USED_FOR_SPECIAL_BUSINESS_CONDITIONS";
        String longDescription = "This is a very long description that might be used to describe " +
                                "a complex role that requires detailed explanation about " +
                                "its purpose and usage in various business contexts and permissions.";
        
        Role role = new Role();
        role.setRole(longRole);
        role.setDescription(longDescription);
        
        // When
        Set<ConstraintViolation<Role>> violations = validator.validate(role);
        
        // Then
        assertTrue(violations.isEmpty());
        assertEquals(longRole, role.getRole());
        assertEquals(longDescription, role.getDescription());
    }

    @Test
    @DisplayName("Should handle special characters in role and description")
    void shouldHandleSpecialCharactersInRoleAndDescription() {
        // Given
        String roleWithSpecialChars = "ADMIN-SUPER_2024";
        String descriptionWithSpecialChars = "Super Administrator (2024) - Special Access & All Permissions!";
        
        Role role = new Role();
        role.setRole(roleWithSpecialChars);
        role.setDescription(descriptionWithSpecialChars);
        
        // When
        Set<ConstraintViolation<Role>> violations = validator.validate(role);
        
        // Then
        assertTrue(violations.isEmpty());
        assertEquals(roleWithSpecialChars, role.getRole());
        assertEquals(descriptionWithSpecialChars, role.getDescription());
    }

    @Test
    @DisplayName("Should handle numeric characters in role")
    void shouldHandleNumericCharactersInRole() {
        // Given
        String numericRole = "ROLE123";
        String description = "Numeric role identifier";
        
        Role role = new Role();
        role.setRole(numericRole);
        role.setDescription(description);
        
        // When
        Set<ConstraintViolation<Role>> violations = validator.validate(role);
        
        // Then
        assertTrue(violations.isEmpty());
        assertEquals(numericRole, role.getRole());
        assertEquals(description, role.getDescription());
    }

    @Test
    @DisplayName("Should test equals and hashCode methods")
    void shouldTestEqualsAndHashCodeMethods() {
        // Given
        Role role1 = TestDataFactory.createUserRole();
        Role role2 = TestDataFactory.createUserRole();
        role1.setId(1L);
        role2.setId(1L);
        
        // Then
        assertEquals(role1, role2);
        assertEquals(role1.hashCode(), role2.hashCode());
        
        // When different IDs
        role2.setId(2L);
        
        // Then
        assertNotEquals(role1, role2);
    }

    @Test
    @DisplayName("Should inherit AbstractModel properties")
    void shouldInheritAbstractModelProperties() {
        // Given
        Role role = TestDataFactory.createUserRole();
        
        // When
        role.setId(1L);
        
        // Then
        assertNotNull(role.getId());
        assertEquals(1L, role.getId());
        // Note: createdAt and updatedAt are set by JPA auditing in real scenarios
    }

    @Test
    @DisplayName("Should support case-sensitive role names")
    void shouldSupportCaseSensitiveRoleNames() {
        // Given
        Role upperCase = new Role();
        upperCase.setRole("ADMIN");
        upperCase.setDescription("Upper case");
        
        Role lowerCase = new Role();
        lowerCase.setRole("admin");
        lowerCase.setDescription("Lower case");
        
        Role mixedCase = new Role();
        mixedCase.setRole("Admin");
        mixedCase.setDescription("Mixed case");
        
        // When
        Set<ConstraintViolation<Role>> violations1 = validator.validate(upperCase);
        Set<ConstraintViolation<Role>> violations2 = validator.validate(lowerCase);
        Set<ConstraintViolation<Role>> violations3 = validator.validate(mixedCase);
        
        // Then
        assertTrue(violations1.isEmpty());
        assertTrue(violations2.isEmpty());
        assertTrue(violations3.isEmpty());
        
        assertEquals("ADMIN", upperCase.getRole());
        assertEquals("admin", lowerCase.getRole());
        assertEquals("Admin", mixedCase.getRole());
    }

    @Test
    @DisplayName("Should validate minimum requirements for business logic")
    void shouldValidateMinimumRequirementsForBusinessLogic() {
        // Given - Common roles used in business
        String[] businessRoles = {"USER", "ADMIN", "AGENT", "MANAGER"};
        
        // When & Then
        for (String roleName : businessRoles) {
            Role role = new Role();
            role.setRole(roleName);
            role.setDescription(roleName + " Description");
            
            Set<ConstraintViolation<Role>> violations = validator.validate(role);
            
            assertTrue(violations.isEmpty(), "Business role " + roleName + " should be valid");
            assertEquals(roleName, role.getRole());
        }
    }

    @Test
    @DisplayName("Should handle constructor variations")
    void shouldHandleConstructorVariations() {
        // Given & When - Default constructor
        Role defaultConstructor = new Role();
        defaultConstructor.setRole("TEST");
        defaultConstructor.setDescription("Test Description");
        
        // Given & When - All args constructor
        Role allArgsConstructor = new Role("TEST", "Test Description");
        
        // Then
        Set<ConstraintViolation<Role>> violations1 = validator.validate(defaultConstructor);
        Set<ConstraintViolation<Role>> violations2 = validator.validate(allArgsConstructor);
        
        assertTrue(violations1.isEmpty());
        assertTrue(violations2.isEmpty());
        
        assertEquals("TEST", defaultConstructor.getRole());
        assertEquals("Test Description", defaultConstructor.getDescription());
        assertEquals("TEST", allArgsConstructor.getRole());
        assertEquals("Test Description", allArgsConstructor.getDescription());
    }

    @Test
    @DisplayName("Should maintain immutability expectations for role")
    void shouldMaintainImmutabilityExpectationsForRole() {
        // Given
        Role role = TestDataFactory.createUserRole();
        String originalRole = role.getRole();
        String originalDescription = role.getDescription();
        
        // When - modifying the returned strings shouldn't affect the entity
        String modifiedRole = originalRole.toLowerCase();
        
        // Then
        assertNotEquals(modifiedRole, role.getRole());
        assertEquals("USER", role.getRole());
        assertNull(role.getDescription()); // Original doesn't have description
    }

    @Test
    @DisplayName("Should create standard system roles")
    void shouldCreateStandardSystemRoles() {
        // Given & When
        Role userRole = TestDataFactory.createUserRole();
        Role adminRole = TestDataFactory.createAdminRole();
        Role agentRole = TestDataFactory.createAgentRole();
        
        // Then
        Set<ConstraintViolation<Role>> violations1 = validator.validate(userRole);
        Set<ConstraintViolation<Role>> violations2 = validator.validate(adminRole);
        Set<ConstraintViolation<Role>> violations3 = validator.validate(agentRole);
        
        assertTrue(violations1.isEmpty());
        assertTrue(violations2.isEmpty());
        assertTrue(violations3.isEmpty());
        
        assertEquals("USER", userRole.getRole());
        assertEquals("ADMIN", adminRole.getRole());
        assertEquals("AGENT", agentRole.getRole());
    }

    @Test
    @DisplayName("Should enforce unique role constraint conceptually")
    void shouldEnforceUniqueRoleConstraintConceptually() {
        // Given - Two roles with same name (unique constraint will be enforced by DB)
        Role role1 = new Role();
        role1.setRole("DUPLICATE_ROLE");
        role1.setDescription("First role");
        
        Role role2 = new Role();
        role2.setRole("DUPLICATE_ROLE");
        role2.setDescription("Second role");
        
        // When
        Set<ConstraintViolation<Role>> violations1 = validator.validate(role1);
        Set<ConstraintViolation<Role>> violations2 = validator.validate(role2);
        
        // Then - Both pass validation (unique constraint is database-level)
        assertTrue(violations1.isEmpty());
        assertTrue(violations2.isEmpty());
        
        // Note: The unique constraint is enforced at the database level,
        // not at the entity validation level
        assertEquals("DUPLICATE_ROLE", role1.getRole());
        assertEquals("DUPLICATE_ROLE", role2.getRole());
    }
}