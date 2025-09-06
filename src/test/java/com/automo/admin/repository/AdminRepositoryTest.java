package com.automo.admin.repository;

import com.automo.admin.entity.Admin;
import com.automo.auth.entity.Auth;
import com.automo.state.entity.State;
import com.automo.test.utils.TestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("Tests for AdminRepository")
class AdminRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private AdminRepository adminRepository;

    private Auth testAuth1;
    private Auth testAuth2;
    private State activeState;
    private State eliminatedState;
    private Admin testAdmin1;
    private Admin testAdmin2;

    @BeforeEach
    void setUp() {
        testAuth1 = TestDataFactory.createValidAuth("admin1@automo.com");
        testAuth1 = entityManager.persistAndFlush(testAuth1);

        testAuth2 = TestDataFactory.createValidAuth("admin2@automo.com");
        testAuth2 = entityManager.persistAndFlush(testAuth2);

        activeState = TestDataFactory.createActiveState();
        activeState = entityManager.persistAndFlush(activeState);

        eliminatedState = TestDataFactory.createEliminatedState();
        eliminatedState = entityManager.persistAndFlush(eliminatedState);

        testAdmin1 = TestDataFactory.createValidAdmin(testAuth1, activeState);
        testAdmin1.setEmail("admin1@automo.com");
        testAdmin1.setName("Admin One");
        testAdmin1 = entityManager.persistAndFlush(testAdmin1);

        testAdmin2 = TestDataFactory.createValidAdmin(testAuth2, activeState);
        testAdmin2.setEmail("admin2@automo.com");
        testAdmin2.setName("Admin Two");
        testAdmin2 = entityManager.persistAndFlush(testAdmin2);

        entityManager.clear();
    }

    @Test
    @DisplayName("Should find admin by id successfully")
    void shouldFindAdminByIdSuccessfully() {
        Optional<Admin> found = adminRepository.findById(testAdmin1.getId());

        assertTrue(found.isPresent());
        assertEquals(testAdmin1.getName(), found.get().getName());
        assertEquals(testAdmin1.getEmail(), found.get().getEmail());
        assertEquals(testAdmin1.getAuth().getId(), found.get().getAuth().getId());
    }

    @Test
    @DisplayName("Should find admin by email")
    void shouldFindAdminByEmail() {
        Optional<Admin> found = adminRepository.findByEmail("admin1@automo.com");

        assertTrue(found.isPresent());
        assertEquals(testAdmin1.getName(), found.get().getName());
        assertEquals("admin1@automo.com", found.get().getEmail());
    }

    @Test
    @DisplayName("Should return empty when admin email not found")
    void shouldReturnEmptyWhenAdminEmailNotFound() {
        Optional<Admin> found = adminRepository.findByEmail("nonexistent@automo.com");

        assertFalse(found.isPresent());
    }

    @Test
    @DisplayName("Should find admin by auth id")
    void shouldFindAdminByAuthId() {
        Optional<Admin> found = adminRepository.findByAuthId(testAuth1.getId());

        assertTrue(found.isPresent());
        assertEquals(testAdmin1.getName(), found.get().getName());
        assertEquals(testAuth1.getId(), found.get().getAuth().getId());
    }

    @Test
    @DisplayName("Should find admins by state id")
    void shouldFindAdminsByStateId() {
        // Create admin with eliminated state
        Admin eliminatedAdmin = TestDataFactory.createValidAdmin(testAuth1, eliminatedState);
        eliminatedAdmin.setEmail("eliminated@automo.com");
        eliminatedAdmin.setName("Eliminated Admin");
        entityManager.persistAndFlush(eliminatedAdmin);
        entityManager.clear();

        List<Admin> activeAdmins = adminRepository.findByStateId(activeState.getId());
        List<Admin> eliminatedAdmins = adminRepository.findByStateId(eliminatedState.getId());

        assertEquals(2, activeAdmins.size());
        assertEquals(1, eliminatedAdmins.size());

        assertTrue(activeAdmins.stream().anyMatch(a -> a.getName().equals("Admin One")));
        assertTrue(activeAdmins.stream().anyMatch(a -> a.getName().equals("Admin Two")));
        assertTrue(eliminatedAdmins.stream().anyMatch(a -> a.getName().equals("Eliminated Admin")));
    }

    @Test
    @DisplayName("Should find admin by email with auth and state relationships")
    void shouldFindAdminByEmailWithAuthAndStateRelationships() {
        Optional<Admin> found = adminRepository.findByEmailWithAuthAndState("admin1@automo.com");

        assertTrue(found.isPresent());
        Admin admin = found.get();
        
        // Verify that auth and state are loaded (no lazy loading exceptions)
        assertNotNull(admin.getAuth());
        assertNotNull(admin.getState());
        assertEquals("admin1@automo.com", admin.getAuth().getEmail());
        assertEquals("ACTIVE", admin.getState().getState());
    }

    @Test
    @DisplayName("Should find admin by auth id with auth and state relationships")
    void shouldFindAdminByAuthIdWithAuthAndStateRelationships() {
        Optional<Admin> found = adminRepository.findByAuthIdWithAuthAndState(testAuth1.getId());

        assertTrue(found.isPresent());
        Admin admin = found.get();
        
        // Verify that auth and state are loaded (no lazy loading exceptions)
        assertNotNull(admin.getAuth());
        assertNotNull(admin.getState());
        assertEquals(testAuth1.getId(), admin.getAuth().getId());
        assertEquals(activeState.getId(), admin.getState().getId());
    }

    @Test
    @DisplayName("Should find all admins with auth and state relationships")
    void shouldFindAllAdminsWithAuthAndStateRelationships() {
        List<Admin> allAdmins = adminRepository.findAllWithAuthAndState();

        assertEquals(2, allAdmins.size());
        
        // Verify that auth and state are loaded for all admins
        for (Admin admin : allAdmins) {
            assertNotNull(admin.getAuth());
            assertNotNull(admin.getState());
            assertNotNull(admin.getAuth().getEmail());
            assertNotNull(admin.getState().getState());
        }
    }

    @Test
    @DisplayName("Should find admin by id with auth and state relationships")
    void shouldFindAdminByIdWithAuthAndStateRelationships() {
        Optional<Admin> found = adminRepository.findByIdWithAuthAndState(testAdmin1.getId());

        assertTrue(found.isPresent());
        Admin admin = found.get();
        
        // Verify that auth and state are loaded (no lazy loading exceptions)
        assertNotNull(admin.getAuth());
        assertNotNull(admin.getState());
        assertEquals(testAdmin1.getId(), admin.getId());
        assertEquals("admin1@automo.com", admin.getAuth().getEmail());
        assertEquals("ACTIVE", admin.getState().getState());
    }

    @Test
    @DisplayName("Should perform paginated search by criteria")
    void shouldPerformPaginatedSearchByCriteria() {
        // Create more admins for pagination testing
        Auth auth3 = TestDataFactory.createValidAuth("test@automo.com");
        auth3 = entityManager.persistAndFlush(auth3);
        
        Admin searchableAdmin = TestDataFactory.createValidAdmin(auth3, activeState);
        searchableAdmin.setEmail("searchable@automo.com");
        searchableAdmin.setName("Searchable Admin");
        entityManager.persistAndFlush(searchableAdmin);
        entityManager.clear();

        Pageable pageable = PageRequest.of(0, 10);
        
        // Test empty search (should return all)
        Page<Admin> allResults = adminRepository.findBySearchCriteria("", pageable);
        assertEquals(3, allResults.getTotalElements());

        // Test search by name
        Page<Admin> nameResults = adminRepository.findBySearchCriteria("Searchable", pageable);
        assertEquals(1, nameResults.getTotalElements());
        assertEquals("Searchable Admin", nameResults.getContent().get(0).getName());

        // Test search by email
        Page<Admin> emailResults = adminRepository.findBySearchCriteria("admin1", pageable);
        assertEquals(1, emailResults.getTotalElements());
        assertEquals("admin1@automo.com", emailResults.getContent().get(0).getEmail());
    }

    @Test
    @DisplayName("Should perform paginated search by state and criteria")
    void shouldPerformPaginatedSearchByStateAndCriteria() {
        // Create admin with eliminated state
        Auth eliminatedAuth = TestDataFactory.createValidAuth("eliminated@automo.com");
        eliminatedAuth = entityManager.persistAndFlush(eliminatedAuth);
        
        Admin eliminatedAdmin = TestDataFactory.createValidAdmin(eliminatedAuth, eliminatedState);
        eliminatedAdmin.setEmail("eliminated@automo.com");
        eliminatedAdmin.setName("Eliminated Admin");
        entityManager.persistAndFlush(eliminatedAdmin);
        entityManager.clear();

        Pageable pageable = PageRequest.of(0, 10);
        
        // Test search by state (active)
        Page<Admin> activeResults = adminRepository.findByStateIdAndSearchCriteria(
            activeState.getId(), "", pageable);
        assertEquals(2, activeResults.getTotalElements());

        // Test search by state (eliminated)
        Page<Admin> eliminatedResults = adminRepository.findByStateIdAndSearchCriteria(
            eliminatedState.getId(), "", pageable);
        assertEquals(1, eliminatedResults.getTotalElements());

        // Test search by state and name criteria
        Page<Admin> stateAndNameResults = adminRepository.findByStateIdAndSearchCriteria(
            eliminatedState.getId(), "Eliminated", pageable);
        assertEquals(1, stateAndNameResults.getTotalElements());
        assertEquals("Eliminated Admin", stateAndNameResults.getContent().get(0).getName());
    }

    @Test
    @DisplayName("Should save admin successfully")
    void shouldSaveAdminSuccessfully() {
        Auth newAuth = TestDataFactory.createValidAuth("newadmin@automo.com");
        newAuth = entityManager.persistAndFlush(newAuth);

        Admin newAdmin = TestDataFactory.createValidAdmin(newAuth, activeState);
        newAdmin.setEmail("newadmin@automo.com");
        newAdmin.setName("New Admin");
        newAdmin.setImg("profile.jpg");

        Admin savedAdmin = adminRepository.save(newAdmin);

        assertNotNull(savedAdmin.getId());
        assertEquals("New Admin", savedAdmin.getName());
        assertEquals("newadmin@automo.com", savedAdmin.getEmail());
        assertEquals("profile.jpg", savedAdmin.getImg());
        assertEquals(newAuth.getId(), savedAdmin.getAuth().getId());
        assertEquals(activeState.getId(), savedAdmin.getState().getId());
    }

    @Test
    @DisplayName("Should update admin successfully")
    void shouldUpdateAdminSuccessfully() {
        testAdmin1.setName("Updated Admin Name");
        testAdmin1.setEmail("updated@automo.com");
        testAdmin1.setImg("updated-profile.jpg");

        Admin updatedAdmin = adminRepository.save(testAdmin1);

        assertEquals("Updated Admin Name", updatedAdmin.getName());
        assertEquals("updated@automo.com", updatedAdmin.getEmail());
        assertEquals("updated-profile.jpg", updatedAdmin.getImg());
        assertEquals(testAdmin1.getId(), updatedAdmin.getId());
    }

    @Test
    @DisplayName("Should delete admin successfully")
    void shouldDeleteAdminSuccessfully() {
        Long adminId = testAdmin1.getId();

        adminRepository.delete(testAdmin1);
        entityManager.flush();

        Optional<Admin> deletedAdmin = adminRepository.findById(adminId);
        assertFalse(deletedAdmin.isPresent());
    }

    @Test
    @DisplayName("Should handle unique email constraint")
    void shouldHandleUniqueEmailConstraint() {
        Auth duplicateAuth = TestDataFactory.createValidAuth("duplicate@automo.com");
        duplicateAuth = entityManager.persistAndFlush(duplicateAuth);

        Admin admin1 = TestDataFactory.createValidAdmin(duplicateAuth, activeState);
        admin1.setEmail("duplicate@automo.com");
        admin1.setName("First Admin");
        adminRepository.save(admin1);
        entityManager.flush();

        Admin admin2 = TestDataFactory.createValidAdmin(duplicateAuth, activeState);
        admin2.setEmail("duplicate@automo.com"); // Same email
        admin2.setName("Second Admin");

        // This should throw an exception due to unique constraint on email
        assertThrows(Exception.class, () -> {
            adminRepository.save(admin2);
            entityManager.flush();
        });
    }

    @Test
    @DisplayName("Should return empty list when no admins exist for state")
    void shouldReturnEmptyListWhenNoAdminsExistForState() {
        State newState = new State();
        newState.setState("PENDING");
        newState = entityManager.persistAndFlush(newState);

        List<Admin> admins = adminRepository.findByStateId(newState.getId());

        assertTrue(admins.isEmpty());
    }

    @Test
    @DisplayName("Should handle cascade operations with auth entity")
    void shouldHandleCascadeOperationsWithAuthEntity() {
        // Verify that admin is properly linked to auth
        Optional<Admin> foundAdmin = adminRepository.findById(testAdmin1.getId());
        assertTrue(foundAdmin.isPresent());
        
        Auth linkedAuth = foundAdmin.get().getAuth();
        assertNotNull(linkedAuth);
        assertEquals(testAuth1.getEmail(), linkedAuth.getEmail());
    }

    @Test
    @DisplayName("Should persist and retrieve timestamps correctly")
    void shouldPersistAndRetrieveTimestampsCorrectly() {
        Optional<Admin> foundAdmin = adminRepository.findById(testAdmin1.getId());

        assertTrue(foundAdmin.isPresent());
        Admin admin = foundAdmin.get();
        
        assertNotNull(admin.getCreatedAt());
        assertNotNull(admin.getUpdatedAt());
        
        // Update the admin to test updatedAt
        admin.setName("Updated Name");
        Admin savedAdmin = adminRepository.save(admin);
        
        assertNotNull(savedAdmin.getUpdatedAt());
        assertTrue(savedAdmin.getUpdatedAt().isAfter(savedAdmin.getCreatedAt()) || 
                   savedAdmin.getUpdatedAt().isEqual(savedAdmin.getCreatedAt()));
    }

    @Test
    @DisplayName("Should handle case insensitive search")
    void shouldHandleCaseInsensitiveSearch() {
        Pageable pageable = PageRequest.of(0, 10);
        
        // Test case insensitive search
        Page<Admin> lowerResults = adminRepository.findBySearchCriteria("admin", pageable);
        Page<Admin> upperResults = adminRepository.findBySearchCriteria("ADMIN", pageable);
        Page<Admin> mixedResults = adminRepository.findBySearchCriteria("Admin", pageable);
        
        assertEquals(2, lowerResults.getTotalElements());
        assertEquals(2, upperResults.getTotalElements());
        assertEquals(2, mixedResults.getTotalElements());
    }

    @Test
    @DisplayName("Should search by username in auth entity")
    void shouldSearchByUsernameInAuthEntity() {
        // Update auth username for testing
        testAuth1.setUsername("test_username");
        entityManager.persistAndFlush(testAuth1);
        entityManager.clear();

        Pageable pageable = PageRequest.of(0, 10);
        
        Page<Admin> results = adminRepository.findBySearchCriteria("test_username", pageable);
        assertEquals(1, results.getTotalElements());
        assertEquals(testAdmin1.getId(), results.getContent().get(0).getId());
    }
}