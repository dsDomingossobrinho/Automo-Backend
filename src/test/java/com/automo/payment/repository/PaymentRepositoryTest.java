package com.automo.payment.repository;

import com.automo.identifier.entity.Identifier;
import com.automo.identifierType.entity.IdentifierType;
import com.automo.payment.entity.Payment;
import com.automo.paymentType.entity.PaymentType;
import com.automo.state.entity.State;
import com.automo.test.config.BaseTestConfig;
import com.automo.test.utils.TestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("Tests for PaymentRepository")
class PaymentRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private PaymentRepository paymentRepository;

    private State activeState;
    private State approvedState;
    private IdentifierType identifierType;
    private Identifier identifier;
    private PaymentType paymentType;
    private Payment testPayment;

    @BeforeEach
    void setUp() {
        activeState = TestDataFactory.createActiveState();
        activeState = entityManager.persistAndFlush(activeState);

        approvedState = new State();
        approvedState.setState("APPROVED PAYMENT");
        approvedState = entityManager.persistAndFlush(approvedState);

        identifierType = TestDataFactory.createNifIdentifierType();
        identifierType = entityManager.persistAndFlush(identifierType);

        identifier = TestDataFactory.createValidIdentifier(1L, identifierType, activeState);
        identifier = entityManager.persistAndFlush(identifier);

        paymentType = TestDataFactory.createBankTransferPaymentType();
        paymentType = entityManager.persistAndFlush(paymentType);

        testPayment = TestDataFactory.createValidPayment(identifier, paymentType, activeState);
        testPayment = entityManager.persistAndFlush(testPayment);

        entityManager.clear();
    }

    @Test
    @DisplayName("Should find payment by id successfully")
    void shouldFindPaymentByIdSuccessfully() {
        Optional<Payment> found = paymentRepository.findById(testPayment.getId());

        assertTrue(found.isPresent());
        assertEquals(testPayment.getAmount(), found.get().getAmount());
        assertEquals(testPayment.getImageFilename(), found.get().getImageFilename());
        assertEquals(testPayment.getOriginalFilename(), found.get().getOriginalFilename());
    }

    @Test
    @DisplayName("Should find payments by state id")
    void shouldFindPaymentsByStateId() {
        // Create another payment with the same state
        Payment anotherPayment = TestDataFactory.createValidPayment(identifier, paymentType, activeState);
        anotherPayment.setAmount(new BigDecimal("200.00"));
        entityManager.persistAndFlush(anotherPayment);

        // Create payment with different state
        Payment approvedPayment = TestDataFactory.createValidPayment(identifier, paymentType, approvedState);
        approvedPayment.setAmount(new BigDecimal("300.00"));
        entityManager.persistAndFlush(approvedPayment);

        entityManager.clear();

        List<Payment> activePayments = paymentRepository.findByStateId(activeState.getId());
        List<Payment> approvedPayments = paymentRepository.findByStateId(approvedState.getId());

        assertEquals(2, activePayments.size());
        assertEquals(1, approvedPayments.size());

        assertTrue(activePayments.stream().anyMatch(p -> p.getAmount().equals(new BigDecimal("100.00"))));
        assertTrue(activePayments.stream().anyMatch(p -> p.getAmount().equals(new BigDecimal("200.00"))));
        assertTrue(approvedPayments.stream().anyMatch(p -> p.getAmount().equals(new BigDecimal("300.00"))));
    }

    @Test
    @DisplayName("Should find payments by payment type id")
    void shouldFindPaymentsByPaymentTypeId() {
        // Create another payment type
        PaymentType creditCardType = new PaymentType();
        creditCardType.setPaymentType("CREDIT_CARD");
        creditCardType = entityManager.persistAndFlush(creditCardType);

        // Create payment with credit card type
        Payment creditCardPayment = TestDataFactory.createValidPayment(identifier, creditCardType, activeState);
        creditCardPayment.setAmount(new BigDecimal("150.00"));
        entityManager.persistAndFlush(creditCardPayment);

        entityManager.clear();

        List<Payment> bankTransferPayments = paymentRepository.findByPaymentTypeId(paymentType.getId());
        List<Payment> creditCardPayments = paymentRepository.findByPaymentTypeId(creditCardType.getId());

        assertEquals(1, bankTransferPayments.size());
        assertEquals(1, creditCardPayments.size());

        assertEquals(new BigDecimal("100.00"), bankTransferPayments.get(0).getAmount());
        assertEquals(new BigDecimal("150.00"), creditCardPayments.get(0).getAmount());
    }

    @Test
    @DisplayName("Should calculate total revenue correctly")
    void shouldCalculateTotalRevenueCorrectly() {
        // Create additional payments
        Payment payment2 = TestDataFactory.createValidPayment(identifier, paymentType, activeState);
        payment2.setAmount(new BigDecimal("250.50"));
        entityManager.persistAndFlush(payment2);

        Payment payment3 = TestDataFactory.createValidPayment(identifier, paymentType, activeState);
        payment3.setAmount(new BigDecimal("75.25"));
        entityManager.persistAndFlush(payment3);

        entityManager.flush();
        entityManager.clear();

        BigDecimal totalRevenue = paymentRepository.getTotalRevenue();

        // Expected: 100.00 + 250.50 + 75.25 = 425.75
        assertEquals(new BigDecimal("425.75"), totalRevenue);
    }

    @Test
    @DisplayName("Should calculate daily revenue correctly")
    void shouldCalculateDailyRevenueCorrectly() {
        // Create payment with specific date (today)
        LocalDateTime today = LocalDateTime.now().withHour(12).withMinute(0).withSecond(0).withNano(0);
        
        Payment todayPayment = TestDataFactory.createValidPayment(identifier, paymentType, activeState);
        todayPayment.setAmount(new BigDecimal("500.00"));
        // We can't directly set createdAt in tests, so we'll rely on automatic timestamps
        entityManager.persistAndFlush(todayPayment);

        entityManager.flush();
        entityManager.clear();

        BigDecimal dailyRevenue = paymentRepository.getDailyRevenue();

        assertNotNull(dailyRevenue);
        assertTrue(dailyRevenue.compareTo(BigDecimal.ZERO) > 0);
    }

    @Test
    @DisplayName("Should calculate monthly revenue correctly")
    void shouldCalculateMonthlyRevenueCorrectly() {
        // Create additional payment for this month
        Payment monthlyPayment = TestDataFactory.createValidPayment(identifier, paymentType, activeState);
        monthlyPayment.setAmount(new BigDecimal("300.00"));
        entityManager.persistAndFlush(monthlyPayment);

        entityManager.flush();
        entityManager.clear();

        BigDecimal monthlyRevenue = paymentRepository.getMonthlyRevenue();

        assertNotNull(monthlyRevenue);
        assertTrue(monthlyRevenue.compareTo(BigDecimal.ZERO) > 0);
    }

    @Test
    @DisplayName("Should calculate semester revenue correctly")
    void shouldCalculateSemesterRevenueCorrectly() {
        // Create additional payment for this semester
        Payment semesterPayment = TestDataFactory.createValidPayment(identifier, paymentType, activeState);
        semesterPayment.setAmount(new BigDecimal("400.00"));
        entityManager.persistAndFlush(semesterPayment);

        entityManager.flush();
        entityManager.clear();

        BigDecimal semesterRevenue = paymentRepository.getSemesterRevenue();

        assertNotNull(semesterRevenue);
        assertTrue(semesterRevenue.compareTo(BigDecimal.ZERO) > 0);
    }

    @Test
    @DisplayName("Should save payment successfully")
    void shouldSavePaymentSuccessfully() {
        Payment newPayment = TestDataFactory.createValidPayment(identifier, paymentType, activeState);
        newPayment.setAmount(new BigDecimal("999.99"));
        newPayment.setImageFilename("new-payment.jpg");
        newPayment.setOriginalFilename("new-receipt.jpg");

        Payment savedPayment = paymentRepository.save(newPayment);

        assertNotNull(savedPayment.getId());
        assertEquals(new BigDecimal("999.99"), savedPayment.getAmount());
        assertEquals("new-payment.jpg", savedPayment.getImageFilename());
        assertEquals("new-receipt.jpg", savedPayment.getOriginalFilename());
        assertEquals(identifier.getId(), savedPayment.getIdentifier().getId());
        assertEquals(paymentType.getId(), savedPayment.getPaymentType().getId());
        assertEquals(activeState.getId(), savedPayment.getState().getId());
    }

    @Test
    @DisplayName("Should update payment successfully")
    void shouldUpdatePaymentSuccessfully() {
        testPayment.setAmount(new BigDecimal("updated.amount"));
        testPayment.setImageFilename("updated-filename.jpg");

        Payment updatedPayment = paymentRepository.save(testPayment);

        assertEquals(new BigDecimal("updated.amount"), updatedPayment.getAmount());
        assertEquals("updated-filename.jpg", updatedPayment.getImageFilename());
        assertEquals(testPayment.getId(), updatedPayment.getId());
    }

    @Test
    @DisplayName("Should delete payment successfully")
    void shouldDeletePaymentSuccessfully() {
        Long paymentId = testPayment.getId();

        paymentRepository.delete(testPayment);
        entityManager.flush();

        Optional<Payment> deletedPayment = paymentRepository.findById(paymentId);
        assertFalse(deletedPayment.isPresent());
    }

    @Test
    @DisplayName("Should find all payments")
    void shouldFindAllPayments() {
        // Create additional payments
        Payment payment2 = TestDataFactory.createValidPayment(identifier, paymentType, activeState);
        payment2.setAmount(new BigDecimal("222.22"));
        entityManager.persistAndFlush(payment2);

        Payment payment3 = TestDataFactory.createValidPayment(identifier, paymentType, approvedState);
        payment3.setAmount(new BigDecimal("333.33"));
        entityManager.persistAndFlush(payment3);

        entityManager.clear();

        List<Payment> allPayments = paymentRepository.findAll();

        assertEquals(3, allPayments.size());
        assertTrue(allPayments.stream().anyMatch(p -> p.getAmount().equals(new BigDecimal("100.00"))));
        assertTrue(allPayments.stream().anyMatch(p -> p.getAmount().equals(new BigDecimal("222.22"))));
        assertTrue(allPayments.stream().anyMatch(p -> p.getAmount().equals(new BigDecimal("333.33"))));
    }

    @Test
    @DisplayName("Should return empty list when no payments exist for state")
    void shouldReturnEmptyListWhenNoPaymentsExistForState() {
        State newState = new State();
        newState.setState("PENDING");
        newState = entityManager.persistAndFlush(newState);

        List<Payment> payments = paymentRepository.findByStateId(newState.getId());

        assertTrue(payments.isEmpty());
    }

    @Test
    @DisplayName("Should return zero when no payments exist for revenue calculations")
    void shouldReturnZeroWhenNoPaymentsExistForRevenueCalculations() {
        // Delete all payments
        paymentRepository.deleteAll();
        entityManager.flush();

        BigDecimal totalRevenue = paymentRepository.getTotalRevenue();
        
        // Should return null or zero when no payments exist
        assertTrue(totalRevenue == null || totalRevenue.equals(BigDecimal.ZERO));
    }

    @Test
    @DisplayName("Should persist and retrieve timestamps correctly")
    void shouldPersistAndRetrieveTimestampsCorrectly() {
        Optional<Payment> foundPayment = paymentRepository.findById(testPayment.getId());

        assertTrue(foundPayment.isPresent());
        Payment payment = foundPayment.get();
        
        assertNotNull(payment.getCreatedAt());
        assertNotNull(payment.getUpdatedAt());
        
        // Update the payment to test updatedAt
        payment.setAmount(new BigDecimal("updated.amount"));
        Payment savedPayment = paymentRepository.save(payment);
        
        assertNotNull(savedPayment.getUpdatedAt());
        assertTrue(savedPayment.getUpdatedAt().isAfter(savedPayment.getCreatedAt()) || 
                   savedPayment.getUpdatedAt().isEqual(savedPayment.getCreatedAt()));
    }
}