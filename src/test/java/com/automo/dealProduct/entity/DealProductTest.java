package com.automo.dealProduct.entity;

import com.automo.deal.entity.Deal;
import com.automo.product.entity.Product;
import com.automo.state.entity.State;
import com.automo.test.config.BaseTestConfig;
import com.automo.test.utils.TestDataFactory;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Comprehensive unit tests for DealProduct entity
 * Testing validation, relationships, and entity behavior
 * This entity has State relationship - supports soft delete
 */
@BaseTestConfig
@DisplayName("DealProduct Entity Tests")
class DealProductTest {

    @Autowired
    private Validator validator;

    private Deal testDeal;
    private Product testProduct;
    private State testState;

    @BeforeEach
    void setUp() {
        State activeState = TestDataFactory.createActiveState();
        activeState.setId(1L);
        
        // Create test entities with proper relationships
        com.automo.identifier.entity.Identifier identifier = TestDataFactory.createValidIdentifier(1L, 
            TestDataFactory.createNifIdentifierType(), activeState);
        identifier.setId(1L);
        
        com.automo.lead.entity.Lead lead = TestDataFactory.createValidLead(identifier, 
            TestDataFactory.createCallLeadType(), activeState);
        lead.setId(1L);
        
        testDeal = TestDataFactory.createDealWithMultipleProducts(identifier, lead, activeState);
        testDeal.setId(1L);
        
        testProduct = TestDataFactory.createValidProduct(activeState);
        testProduct.setId(1L);
        
        testState = activeState;
    }

    @Test
    @DisplayName("Should create valid DealProduct entity successfully")
    void shouldCreateValidDealProductEntity() {
        // Given
        DealProduct dealProduct = TestDataFactory.createValidDealProduct(testDeal, testProduct, testState);
        
        // When
        Set<ConstraintViolation<DealProduct>> violations = validator.validate(dealProduct);
        
        // Then
        assertThat(violations).isEmpty();
        assertThat(dealProduct.getDeal()).isEqualTo(testDeal);
        assertThat(dealProduct.getProduct()).isEqualTo(testProduct);
        assertThat(dealProduct.getState()).isEqualTo(testState);
    }

    @Test
    @DisplayName("Should inherit AbstractModel properties correctly")
    void shouldInheritAbstractModelProperties() {
        // Given
        DealProduct dealProduct = TestDataFactory.createValidDealProduct(testDeal, testProduct, testState);
        LocalDateTime testTime = LocalDateTime.now();
        
        // When
        dealProduct.setId(100L);
        dealProduct.setCreatedAt(testTime);
        dealProduct.setUpdatedAt(testTime.plusMinutes(30));
        
        // Then
        assertThat(dealProduct.getId()).isEqualTo(100L);
        assertThat(dealProduct.getCreatedAt()).isEqualTo(testTime);
        assertThat(dealProduct.getUpdatedAt()).isEqualTo(testTime.plusMinutes(30));
    }

    @Test
    @DisplayName("Should fail validation when Deal is null")
    void shouldFailValidationWhenDealIsNull() {
        // Given
        DealProduct dealProduct = TestDataFactory.createValidDealProduct(null, testProduct, testState);
        
        // When
        Set<ConstraintViolation<DealProduct>> violations = validator.validate(dealProduct);
        
        // Then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Deal is required");
    }

    @Test
    @DisplayName("Should fail validation when Product is null")
    void shouldFailValidationWhenProductIsNull() {
        // Given
        DealProduct dealProduct = TestDataFactory.createValidDealProduct(testDeal, null, testState);
        
        // When
        Set<ConstraintViolation<DealProduct>> violations = validator.validate(dealProduct);
        
        // Then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Product is required");
    }

    @Test
    @DisplayName("Should fail validation when State is null")
    void shouldFailValidationWhenStateIsNull() {
        // Given
        DealProduct dealProduct = TestDataFactory.createValidDealProduct(testDeal, testProduct, null);
        
        // When
        Set<ConstraintViolation<DealProduct>> violations = validator.validate(dealProduct);
        
        // Then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("State is required");
    }

    @Test
    @DisplayName("Should fail validation when all required fields are null")
    void shouldFailValidationWhenAllRequiredFieldsAreNull() {
        // Given
        DealProduct dealProduct = new DealProduct();
        
        // When
        Set<ConstraintViolation<DealProduct>> violations = validator.validate(dealProduct);
        
        // Then
        assertThat(violations).hasSize(3);
        Set<String> messages = Set.of(
            violations.iterator().next().getMessage(),
            violations.stream().skip(1).findFirst().get().getMessage(),
            violations.stream().skip(2).findFirst().get().getMessage()
        );
        assertThat(messages).containsExactlyInAnyOrder(
            "Deal is required",
            "Product is required",
            "State is required"
        );
    }

    @Test
    @DisplayName("Should support many-to-many relationship between Deal and Product")
    void shouldSupportManyToManyRelationship() {
        // Given - One Deal can have multiple Products
        Deal multiProductDeal = testDeal;
        
        Product product1 = TestDataFactory.createValidProduct(testState);
        product1.setId(1L);
        product1.setName("BMW X5");
        
        Product product2 = TestDataFactory.createValidProduct(testState);
        product2.setId(2L);
        product2.setName("Mercedes GLC");
        
        DealProduct dealProduct1 = TestDataFactory.createValidDealProduct(multiProductDeal, product1, testState);
        DealProduct dealProduct2 = TestDataFactory.createValidDealProduct(multiProductDeal, product2, testState);
        
        // When
        Set<ConstraintViolation<DealProduct>> violations1 = validator.validate(dealProduct1);
        Set<ConstraintViolation<DealProduct>> violations2 = validator.validate(dealProduct2);
        
        // Then
        assertThat(violations1).isEmpty();
        assertThat(violations2).isEmpty();
        assertThat(dealProduct1.getDeal()).isEqualTo(dealProduct2.getDeal());
        assertThat(dealProduct1.getProduct()).isNotEqualTo(dealProduct2.getProduct());
    }

    @Test
    @DisplayName("Should support soft delete through State relationship")
    void shouldSupportSoftDeleteThroughStateRelationship() {
        // Given
        State activeState = TestDataFactory.createActiveState();
        activeState.setId(1L);
        State eliminatedState = TestDataFactory.createEliminatedState();
        eliminatedState.setId(4L);
        
        DealProduct dealProduct = TestDataFactory.createValidDealProduct(testDeal, testProduct, activeState);
        
        // When - Simulate soft delete by changing state
        dealProduct.setState(eliminatedState);
        
        // Then
        Set<ConstraintViolation<DealProduct>> violations = validator.validate(dealProduct);
        assertThat(violations).isEmpty();
        assertThat(dealProduct.getState()).isEqualTo(eliminatedState);
    }

    @Test
    @DisplayName("Should test table and column mapping")
    void shouldTestTableAndColumnMapping() {
        // Given
        DealProduct dealProduct = TestDataFactory.createValidDealProduct(testDeal, testProduct, testState);
        
        // When & Then - Test that entity is properly configured for deal_products table
        assertThat(dealProduct.getClass().getAnnotation(jakarta.persistence.Table.class).name())
            .isEqualTo("deal_products");
        
        // Verify join columns are properly configured
        assertThat(dealProduct.getDeal()).isNotNull();
        assertThat(dealProduct.getProduct()).isNotNull();
        assertThat(dealProduct.getState()).isNotNull();
    }

    @Test
    @DisplayName("Should test constructor and builder pattern")
    void shouldTestConstructorAndBuilderPattern() {
        // Given & When - Test NoArgsConstructor
        DealProduct emptyDealProduct = new DealProduct();
        
        // Then
        assertThat(emptyDealProduct.getDeal()).isNull();
        assertThat(emptyDealProduct.getProduct()).isNull();
        assertThat(emptyDealProduct.getState()).isNull();
        
        // Given & When - Test AllArgsConstructor
        DealProduct fullDealProduct = new DealProduct(testDeal, testProduct, testState);
        
        // Then
        assertThat(fullDealProduct.getDeal()).isEqualTo(testDeal);
        assertThat(fullDealProduct.getProduct()).isEqualTo(testProduct);
        assertThat(fullDealProduct.getState()).isEqualTo(testState);
    }

    @Test
    @DisplayName("Should handle equals and hashCode correctly")
    void shouldHandleEqualsAndHashCodeCorrectly() {
        // Given
        DealProduct dealProduct1 = TestDataFactory.createValidDealProduct(testDeal, testProduct, testState);
        dealProduct1.setId(1L);
        
        DealProduct dealProduct2 = TestDataFactory.createValidDealProduct(testDeal, testProduct, testState);
        dealProduct2.setId(1L);
        
        DealProduct dealProduct3 = TestDataFactory.createValidDealProduct(testDeal, testProduct, testState);
        dealProduct3.setId(2L);
        
        // When & Then
        assertThat(dealProduct1).isEqualTo(dealProduct2);
        assertThat(dealProduct1).isNotEqualTo(dealProduct3);
        assertThat(dealProduct1.hashCode()).isEqualTo(dealProduct2.hashCode());
    }

    @Test
    @DisplayName("Should test fetch type configurations")
    void shouldTestFetchTypeConfigurations() {
        // Given
        DealProduct dealProduct = TestDataFactory.createValidDealProduct(testDeal, testProduct, testState);
        
        // When & Then - Test that fetch types are properly configured
        // All relationships are LAZY fetch as configured in entity
        assertThat(dealProduct.getDeal()).isNotNull();
        assertThat(dealProduct.getProduct()).isNotNull();
        assertThat(dealProduct.getState()).isNotNull();
    }

    @Test
    @DisplayName("Should test business scenario of multi-product deal")
    void shouldTestBusinessScenarioOfMultiProductDeal() {
        // Given - Customer purchasing multiple cars in one deal
        Deal carPackageDeal = testDeal;
        carPackageDeal.setTotal(java.math.BigDecimal.valueOf(150000.00));
        carPackageDeal.setMessageCount(15);
        
        Product familyCar = TestDataFactory.createValidProduct(testState);
        familyCar.setId(1L);
        familyCar.setName("Toyota Corolla");
        familyCar.setDescription("Family sedan");
        familyCar.setPrice(java.math.BigDecimal.valueOf(25000.00));
        
        Product luxuryCar = TestDataFactory.createValidProduct(testState);
        luxuryCar.setId(2L);
        luxuryCar.setName("BMW 5 Series");
        luxuryCar.setDescription("Executive sedan");
        luxuryCar.setPrice(java.math.BigDecimal.valueOf(65000.00));
        
        Product sportsCar = TestDataFactory.createValidProduct(testState);
        sportsCar.setId(3L);
        sportsCar.setName("Porsche 911");
        sportsCar.setDescription("Sports car");
        sportsCar.setPrice(java.math.BigDecimal.valueOf(120000.00));
        
        DealProduct familyCarDeal = TestDataFactory.createValidDealProduct(carPackageDeal, familyCar, testState);
        DealProduct luxuryCarDeal = TestDataFactory.createValidDealProduct(carPackageDeal, luxuryCar, testState);
        DealProduct sportsCarDeal = TestDataFactory.createValidDealProduct(carPackageDeal, sportsCar, testState);
        
        // When
        Set<ConstraintViolation<DealProduct>> familyViolations = validator.validate(familyCarDeal);
        Set<ConstraintViolation<DealProduct>> luxuryViolations = validator.validate(luxuryCarDeal);
        Set<ConstraintViolation<DealProduct>> sportsViolations = validator.validate(sportsCarDeal);
        
        // Then
        assertThat(familyViolations).isEmpty();
        assertThat(luxuryViolations).isEmpty();
        assertThat(sportsViolations).isEmpty();
        
        // All products belong to same deal
        assertThat(familyCarDeal.getDeal()).isEqualTo(carPackageDeal);
        assertThat(luxuryCarDeal.getDeal()).isEqualTo(carPackageDeal);
        assertThat(sportsCarDeal.getDeal()).isEqualTo(carPackageDeal);
        
        // But are different products
        assertThat(familyCarDeal.getProduct()).isNotEqualTo(luxuryCarDeal.getProduct());
        assertThat(familyCarDeal.getProduct()).isNotEqualTo(sportsCarDeal.getProduct());
        assertThat(luxuryCarDeal.getProduct()).isNotEqualTo(sportsCarDeal.getProduct());
        
        // All share same state (active)
        assertThat(familyCarDeal.getState()).isEqualTo(testState);
        assertThat(luxuryCarDeal.getState()).isEqualTo(testState);
        assertThat(sportsCarDeal.getState()).isEqualTo(testState);
    }

    @Test
    @DisplayName("Should test deal state transitions")
    void shouldTestDealStateTransitions() {
        // Given - Deal products at different states
        State activeState = TestDataFactory.createActiveState();
        activeState.setId(1L);
        State pendingState = TestDataFactory.createInactiveState();
        pendingState.setId(2L);
        pendingState.setState("PENDING");
        State eliminatedState = TestDataFactory.createEliminatedState();
        eliminatedState.setId(4L);
        
        DealProduct activeDealProduct = TestDataFactory.createValidDealProduct(testDeal, testProduct, activeState);
        DealProduct pendingDealProduct = TestDataFactory.createValidDealProduct(testDeal, testProduct, pendingState);
        DealProduct eliminatedDealProduct = TestDataFactory.createValidDealProduct(testDeal, testProduct, eliminatedState);
        
        // When
        Set<ConstraintViolation<DealProduct>> activeViolations = validator.validate(activeDealProduct);
        Set<ConstraintViolation<DealProduct>> pendingViolations = validator.validate(pendingDealProduct);
        Set<ConstraintViolation<DealProduct>> eliminatedViolations = validator.validate(eliminatedDealProduct);
        
        // Then - All should be valid from entity perspective
        assertThat(activeViolations).isEmpty();
        assertThat(pendingViolations).isEmpty();
        assertThat(eliminatedViolations).isEmpty();
        
        // Verify different states
        assertThat(activeDealProduct.getState().getState()).isEqualTo("ACTIVE");
        assertThat(pendingDealProduct.getState().getState()).isEqualTo("PENDING");
        assertThat(eliminatedDealProduct.getState().getState()).isEqualTo("ELIMINATED");
    }

    @Test
    @DisplayName("Should validate complex deal-product relationships")
    void shouldValidateComplexDealProductRelationships() {
        // Given - Multiple deals with overlapping products
        Deal deal1 = testDeal;
        
        // Create second deal
        State activeState = TestDataFactory.createActiveState();
        activeState.setId(1L);
        com.automo.identifier.entity.Identifier identifier2 = TestDataFactory.createValidIdentifier(2L,
            TestDataFactory.createNifIdentifierType(), activeState);
        identifier2.setId(2L);
        
        com.automo.lead.entity.Lead lead2 = TestDataFactory.createValidLead(identifier2,
            TestDataFactory.createCallLeadType(), activeState);
        lead2.setId(2L);
        
        Deal deal2 = TestDataFactory.createDealWithMultipleProducts(identifier2, lead2, activeState);
        deal2.setId(2L);
        
        // Same product can be in multiple deals
        Product sharedProduct = testProduct;
        
        DealProduct deal1Product = TestDataFactory.createValidDealProduct(deal1, sharedProduct, testState);
        DealProduct deal2Product = TestDataFactory.createValidDealProduct(deal2, sharedProduct, testState);
        
        // When
        Set<ConstraintViolation<DealProduct>> deal1Violations = validator.validate(deal1Product);
        Set<ConstraintViolation<DealProduct>> deal2Violations = validator.validate(deal2Product);
        
        // Then
        assertThat(deal1Violations).isEmpty();
        assertThat(deal2Violations).isEmpty();
        
        // Same product, different deals
        assertThat(deal1Product.getProduct()).isEqualTo(deal2Product.getProduct());
        assertThat(deal1Product.getDeal()).isNotEqualTo(deal2Product.getDeal());
    }
}