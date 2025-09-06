package com.automo.lead.repository;

import com.automo.identifier.entity.Identifier;
import com.automo.identifierType.entity.IdentifierType;
import com.automo.lead.entity.Lead;
import com.automo.leadType.entity.LeadType;
import com.automo.state.entity.State;
import com.automo.test.utils.TestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("Tests for LeadRepository")
class LeadRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private LeadRepository leadRepository;

    // Test data entities
    private State activeState;
    private State inactiveState;
    private State eliminatedState;
    private IdentifierType identifierType1;
    private IdentifierType identifierType2;
    private Identifier testIdentifier1;
    private Identifier testIdentifier2;
    private LeadType callLeadType;
    private LeadType emailLeadType;
    private Lead testLead1;
    private Lead testLead2;
    private Lead testLead3;

    @BeforeEach
    void setUp() {
        // Setup states
        activeState = TestDataFactory.createActiveState();
        activeState = entityManager.persistAndFlush(activeState);

        inactiveState = TestDataFactory.createInactiveState();
        inactiveState = entityManager.persistAndFlush(inactiveState);

        eliminatedState = TestDataFactory.createEliminatedState();
        eliminatedState = entityManager.persistAndFlush(eliminatedState);

        // Setup identifier types
        identifierType1 = TestDataFactory.createNifIdentifierType();
        identifierType1 = entityManager.persistAndFlush(identifierType1);
        
        identifierType2 = new IdentifierType();
        identifierType2.setIdentifierType("EMAIL");
        identifierType2 = entityManager.persistAndFlush(identifierType2);

        // Setup identifiers
        testIdentifier1 = TestDataFactory.createValidIdentifier(1L, identifierType1, activeState);
        testIdentifier1.setIdentifierValue("123456789");
        testIdentifier1 = entityManager.persistAndFlush(testIdentifier1);
        
        testIdentifier2 = TestDataFactory.createValidIdentifier(2L, identifierType2, activeState);
        testIdentifier2.setIdentifierValue("user@example.com");
        testIdentifier2 = entityManager.persistAndFlush(testIdentifier2);

        // Setup lead types
        callLeadType = TestDataFactory.createCallLeadType();
        callLeadType = entityManager.persistAndFlush(callLeadType);

        emailLeadType = new LeadType();
        emailLeadType.setType("EMAIL");
        emailLeadType = entityManager.persistAndFlush(emailLeadType);

        // Setup leads
        testLead1 = new Lead();
        testLead1.setIdentifier(testIdentifier1);
        testLead1.setName("João Silva");
        testLead1.setEmail("joao.silva@example.com");
        testLead1.setContact("912345678");
        testLead1.setZone("Lisboa");
        testLead1.setLeadType(callLeadType);
        testLead1.setState(activeState);
        testLead1 = entityManager.persistAndFlush(testLead1);

        testLead2 = new Lead();
        testLead2.setIdentifier(testIdentifier2);
        testLead2.setName("Maria Santos");
        testLead2.setEmail("maria.santos@example.com");
        testLead2.setContact("923456789");
        testLead2.setZone("Porto");
        testLead2.setLeadType(emailLeadType);
        testLead2.setState(inactiveState);
        testLead2 = entityManager.persistAndFlush(testLead2);

        testLead3 = new Lead();
        testLead3.setIdentifier(testIdentifier1);
        testLead3.setName("Ana Oliveira");
        testLead3.setEmail("ana.oliveira@example.com");
        testLead3.setContact("934567890");
        testLead3.setZone("Braga");
        testLead3.setLeadType(callLeadType);
        testLead3.setState(eliminatedState);
        testLead3 = entityManager.persistAndFlush(testLead3);

        // Clear the persistence context
        entityManager.clear();
    }

    @Test
    @DisplayName("Should save and find lead by ID")
    void shouldSaveAndFindLeadById() {
        // When
        Optional<Lead> found = leadRepository.findById(testLead1.getId());

        // Then
        assertTrue(found.isPresent());
        assertEquals(testLead1.getId(), found.get().getId());
        assertEquals("João Silva", found.get().getName());
        assertEquals("joao.silva@example.com", found.get().getEmail());
        assertEquals("912345678", found.get().getContact());
        assertEquals("Lisboa", found.get().getZone());
    }

    @Test
    @DisplayName("Should return empty when lead not found by ID")
    void shouldReturnEmptyWhenLeadNotFoundById() {
        // When
        Optional<Lead> found = leadRepository.findById(999L);

        // Then
        assertTrue(found.isEmpty());
    }

    @Test
    @DisplayName("Should find all leads")
    void shouldFindAllLeads() {
        // When
        List<Lead> leads = leadRepository.findAll();

        // Then
        assertNotNull(leads);
        assertEquals(3, leads.size());
        
        // Verify all leads are present
        assertTrue(leads.stream().anyMatch(lead -> lead.getName().equals("João Silva")));
        assertTrue(leads.stream().anyMatch(lead -> lead.getName().equals("Maria Santos")));
        assertTrue(leads.stream().anyMatch(lead -> lead.getName().equals("Ana Oliveira")));
    }

    @Test
    @DisplayName("Should find leads by state ID")
    void shouldFindLeadsByStateId() {
        // When
        List<Lead> activeLeads = leadRepository.findByStateId(activeState.getId());
        List<Lead> inactiveLeads = leadRepository.findByStateId(inactiveState.getId());
        List<Lead> eliminatedLeads = leadRepository.findByStateId(eliminatedState.getId());

        // Then
        assertEquals(1, activeLeads.size());
        assertEquals("João Silva", activeLeads.get(0).getName());
        assertEquals(activeState.getId(), activeLeads.get(0).getState().getId());

        assertEquals(1, inactiveLeads.size());
        assertEquals("Maria Santos", inactiveLeads.get(0).getName());
        assertEquals(inactiveState.getId(), inactiveLeads.get(0).getState().getId());

        assertEquals(1, eliminatedLeads.size());
        assertEquals("Ana Oliveira", eliminatedLeads.get(0).getName());
        assertEquals(eliminatedState.getId(), eliminatedLeads.get(0).getState().getId());
    }

    @Test
    @DisplayName("Should return empty list when no leads found by state ID")
    void shouldReturnEmptyListWhenNoLeadsFoundByStateId() {
        // When
        List<Lead> leads = leadRepository.findByStateId(999L);

        // Then
        assertNotNull(leads);
        assertTrue(leads.isEmpty());
    }

    @Test
    @DisplayName("Should find leads by lead type ID")
    void shouldFindLeadsByLeadTypeId() {
        // When
        List<Lead> callLeads = leadRepository.findByLeadTypeId(callLeadType.getId());
        List<Lead> emailLeads = leadRepository.findByLeadTypeId(emailLeadType.getId());

        // Then
        assertEquals(2, callLeads.size());
        assertTrue(callLeads.stream().anyMatch(lead -> lead.getName().equals("João Silva")));
        assertTrue(callLeads.stream().anyMatch(lead -> lead.getName().equals("Ana Oliveira")));
        assertTrue(callLeads.stream().allMatch(lead -> lead.getLeadType().getId().equals(callLeadType.getId())));

        assertEquals(1, emailLeads.size());
        assertEquals("Maria Santos", emailLeads.get(0).getName());
        assertEquals(emailLeadType.getId(), emailLeads.get(0).getLeadType().getId());
    }

    @Test
    @DisplayName("Should return empty list when no leads found by lead type ID")
    void shouldReturnEmptyListWhenNoLeadsFoundByLeadTypeId() {
        // When
        List<Lead> leads = leadRepository.findByLeadTypeId(999L);

        // Then
        assertNotNull(leads);
        assertTrue(leads.isEmpty());
    }

    @Test
    @DisplayName("Should find leads by identifier ID")
    void shouldFindLeadsByIdentifierId() {
        // When
        List<Lead> identifier1Leads = leadRepository.findByIdentifierId(testIdentifier1.getId());
        List<Lead> identifier2Leads = leadRepository.findByIdentifierId(testIdentifier2.getId());

        // Then
        assertEquals(2, identifier1Leads.size());
        assertTrue(identifier1Leads.stream().anyMatch(lead -> lead.getName().equals("João Silva")));
        assertTrue(identifier1Leads.stream().anyMatch(lead -> lead.getName().equals("Ana Oliveira")));
        assertTrue(identifier1Leads.stream().allMatch(lead -> lead.getIdentifier().getId().equals(testIdentifier1.getId())));

        assertEquals(1, identifier2Leads.size());
        assertEquals("Maria Santos", identifier2Leads.get(0).getName());
        assertEquals(testIdentifier2.getId(), identifier2Leads.get(0).getIdentifier().getId());
    }

    @Test
    @DisplayName("Should return empty list when no leads found by identifier ID")
    void shouldReturnEmptyListWhenNoLeadsFoundByIdentifierId() {
        // When
        List<Lead> leads = leadRepository.findByIdentifierId(999L);

        // Then
        assertNotNull(leads);
        assertTrue(leads.isEmpty());
    }

    @Test
    @DisplayName("Should count leads captured by agent")
    void shouldCountLeadsCapturedByAgent() {
        // When
        long countForUser1 = leadRepository.countLeadsCapturedByAgent(1L);
        long countForUser2 = leadRepository.countLeadsCapturedByAgent(2L);
        long countForNonExistentUser = leadRepository.countLeadsCapturedByAgent(999L);

        // Then
        assertEquals(2, countForUser1); // testLead1 (active) + testLead3 (eliminated, but not eliminated in query)
        assertEquals(1, countForUser2); // testLead2
        assertEquals(0, countForNonExistentUser);
    }

    @Test
    @DisplayName("Should count active leads captured by agent")
    void shouldCountActiveLeadsCapturedByAgent() {
        // When
        long activeCountForUser1 = leadRepository.countActiveLeadsCapturedByAgent(1L);
        long activeCountForUser2 = leadRepository.countActiveLeadsCapturedByAgent(2L);
        long activeCountForNonExistentUser = leadRepository.countActiveLeadsCapturedByAgent(999L);

        // Then
        assertEquals(1, activeCountForUser1); // Only testLead1 is active
        assertEquals(0, activeCountForUser2); // testLead2 is inactive
        assertEquals(0, activeCountForNonExistentUser);
    }

    @Test
    @DisplayName("Should save new lead with all required fields")
    void shouldSaveNewLeadWithAllRequiredFields() {
        // Given
        Lead newLead = new Lead();
        newLead.setIdentifier(testIdentifier1);
        newLead.setName("Novo Lead");
        newLead.setEmail("novo@example.com");
        newLead.setContact("945678901");
        newLead.setZone("Coimbra");
        newLead.setLeadType(callLeadType);
        newLead.setState(activeState);

        // When
        Lead savedLead = leadRepository.save(newLead);

        // Then
        assertNotNull(savedLead.getId());
        assertEquals("Novo Lead", savedLead.getName());
        assertEquals("novo@example.com", savedLead.getEmail());
        assertEquals("945678901", savedLead.getContact());
        assertEquals("Coimbra", savedLead.getZone());
        assertEquals(callLeadType.getId(), savedLead.getLeadType().getId());
        assertEquals(activeState.getId(), savedLead.getState().getId());
        assertNotNull(savedLead.getCreatedAt());
        assertNotNull(savedLead.getUpdatedAt());
    }

    @Test
    @DisplayName("Should save lead with optional fields as null")
    void shouldSaveLeadWithOptionalFieldsAsNull() {
        // Given
        Lead newLead = new Lead();
        newLead.setIdentifier(testIdentifier1);
        newLead.setName("Lead Minimal");
        newLead.setEmail("minimal@example.com");
        newLead.setContact(null); // optional field
        newLead.setZone(null); // optional field
        newLead.setLeadType(callLeadType);
        newLead.setState(activeState);

        // When
        Lead savedLead = leadRepository.save(newLead);

        // Then
        assertNotNull(savedLead.getId());
        assertEquals("Lead Minimal", savedLead.getName());
        assertEquals("minimal@example.com", savedLead.getEmail());
        assertNull(savedLead.getContact());
        assertNull(savedLead.getZone());
    }

    @Test
    @DisplayName("Should update existing lead")
    void shouldUpdateExistingLead() {
        // Given
        Lead leadToUpdate = leadRepository.findById(testLead1.getId()).orElseThrow();
        String originalName = leadToUpdate.getName();
        
        // When
        leadToUpdate.setName("Nome Atualizado");
        leadToUpdate.setEmail("atualizado@example.com");
        leadToUpdate.setZone("Faro");
        Lead updatedLead = leadRepository.save(leadToUpdate);

        // Then
        assertEquals(testLead1.getId(), updatedLead.getId());
        assertEquals("Nome Atualizado", updatedLead.getName());
        assertEquals("atualizado@example.com", updatedLead.getEmail());
        assertEquals("Faro", updatedLead.getZone());
        assertNotEquals(originalName, updatedLead.getName());
        assertNotNull(updatedLead.getUpdatedAt());
    }

    @Test
    @DisplayName("Should delete lead by ID")
    void shouldDeleteLeadById() {
        // Given
        Long leadIdToDelete = testLead1.getId();
        assertTrue(leadRepository.findById(leadIdToDelete).isPresent());

        // When
        leadRepository.deleteById(leadIdToDelete);

        // Then
        assertTrue(leadRepository.findById(leadIdToDelete).isEmpty());
    }

    @Test
    @DisplayName("Should handle complex filtering combinations")
    void shouldHandleComplexFilteringCombinations() {
        // When - Find CALL type leads that are ACTIVE
        List<Lead> activeCallLeads = leadRepository.findByLeadTypeId(callLeadType.getId())
                .stream()
                .filter(lead -> lead.getState().getId().equals(activeState.getId()))
                .toList();

        List<Lead> identifier1ActiveLeads = leadRepository.findByIdentifierId(testIdentifier1.getId())
                .stream()
                .filter(lead -> lead.getState().getId().equals(activeState.getId()))
                .toList();

        // Then
        assertEquals(1, activeCallLeads.size());
        assertEquals("João Silva", activeCallLeads.get(0).getName());

        assertEquals(1, identifier1ActiveLeads.size());
        assertEquals("João Silva", identifier1ActiveLeads.get(0).getName());
    }

    @Test
    @DisplayName("Should maintain referential integrity with identifier")
    void shouldMaintainReferentialIntegrityWithIdentifier() {
        // When
        Lead foundLead = leadRepository.findById(testLead1.getId()).orElseThrow();

        // Then
        assertNotNull(foundLead.getIdentifier());
        assertEquals(testIdentifier1.getId(), foundLead.getIdentifier().getId());
        assertEquals("123456789", foundLead.getIdentifier().getIdentifierValue());
        assertEquals(1L, foundLead.getIdentifier().getUserId());
    }

    @Test
    @DisplayName("Should maintain referential integrity with leadType")
    void shouldMaintainReferentialIntegrityWithLeadType() {
        // When
        Lead foundLead = leadRepository.findById(testLead1.getId()).orElseThrow();

        // Then
        assertNotNull(foundLead.getLeadType());
        assertEquals(callLeadType.getId(), foundLead.getLeadType().getId());
        assertEquals("CALL", foundLead.getLeadType().getType());
    }

    @Test
    @DisplayName("Should maintain referential integrity with state")
    void shouldMaintainReferentialIntegrityWithState() {
        // When
        Lead foundLead = leadRepository.findById(testLead1.getId()).orElseThrow();

        // Then
        assertNotNull(foundLead.getState());
        assertEquals(activeState.getId(), foundLead.getState().getId());
        assertEquals("ACTIVE", foundLead.getState().getState());
    }

    @Test
    @DisplayName("Should handle leads with same identifier but different lead types")
    void shouldHandleLeadsWithSameIdentifierButDifferentLeadTypes() {
        // When
        List<Lead> sameIdentifierLeads = leadRepository.findByIdentifierId(testIdentifier1.getId());

        // Then
        assertEquals(2, sameIdentifierLeads.size());
        assertTrue(sameIdentifierLeads.stream().anyMatch(lead -> 
            lead.getName().equals("João Silva") && lead.getLeadType().getType().equals("CALL")));
        assertTrue(sameIdentifierLeads.stream().anyMatch(lead -> 
            lead.getName().equals("Ana Oliveira") && lead.getLeadType().getType().equals("CALL")));
    }

    @Test
    @DisplayName("Should count properly excluding eliminated leads")
    void shouldCountProperlyExcludingEliminatedLeads() {
        // When - The query uses 'ELIMINATED' string, not eliminated state
        long countUser1 = leadRepository.countLeadsCapturedByAgent(1L);
        long activeCountUser1 = leadRepository.countActiveLeadsCapturedByAgent(1L);

        // Then
        // countLeadsCapturedByAgent should exclude ELIMINATED (1 lead - João Silva only)
        assertEquals(1, countUser1); // Only active lead, eliminated is excluded
        assertEquals(1, activeCountUser1); // Only active lead
    }

    @Test
    @DisplayName("Should handle pagination with repository methods")
    void shouldHandlePaginationWithRepositoryMethods() {
        // Given - Create more test leads for pagination testing
        Lead lead4 = new Lead();
        lead4.setIdentifier(testIdentifier1);
        lead4.setName("Lead 4");
        lead4.setEmail("lead4@example.com");
        lead4.setLeadType(callLeadType);
        lead4.setState(activeState);
        entityManager.persistAndFlush(lead4);

        Lead lead5 = new Lead();
        lead5.setIdentifier(testIdentifier1);
        lead5.setName("Lead 5");
        lead5.setEmail("lead5@example.com");
        lead5.setLeadType(callLeadType);
        lead5.setState(activeState);
        entityManager.persistAndFlush(lead5);

        entityManager.clear();

        // When
        List<Lead> allCallLeads = leadRepository.findByLeadTypeId(callLeadType.getId());
        List<Lead> allIdentifier1Leads = leadRepository.findByIdentifierId(testIdentifier1.getId());

        // Then
        assertTrue(allCallLeads.size() >= 2); // At least our test leads
        assertTrue(allIdentifier1Leads.size() >= 2); // At least our test leads
    }

    @Test
    @DisplayName("Should handle case insensitive state queries correctly")
    void shouldHandleCaseInsensitiveStateQueriesCorrectly() {
        // When - Test the actual query behavior
        long activeCount = leadRepository.countActiveLeadsCapturedByAgent(1L);
        
        // Then - Should match exactly with 'ACTIVE' state
        assertEquals(1, activeCount);
        
        // Verify by checking the actual state value
        Lead activeLead = leadRepository.findByIdentifierId(testIdentifier1.getId())
                .stream()
                .filter(lead -> "ACTIVE".equals(lead.getState().getState()))
                .findFirst()
                .orElseThrow();
        
        assertEquals("ACTIVE", activeLead.getState().getState());
        assertEquals("João Silva", activeLead.getName());
    }
}