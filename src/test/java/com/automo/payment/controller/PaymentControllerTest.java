package com.automo.payment.controller;

import com.automo.config.security.JwtUtils;
import com.automo.payment.dto.PaymentDto;
import com.automo.payment.entity.Payment;
import com.automo.payment.response.PaymentResponse;
import com.automo.payment.service.PaymentService;
import com.automo.test.config.BaseTestConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PaymentController.class)
@ActiveProfiles("test")
@DisplayName("Tests for PaymentController")
class PaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PaymentService paymentService;

    @MockBean
    private JwtUtils jwtUtils;

    @Autowired
    private ObjectMapper objectMapper;

    private PaymentDto paymentDto;
    private PaymentResponse paymentResponse;
    private Payment payment;

    @BeforeEach
    void setUp() {
        paymentDto = new PaymentDto(1L, 1L, new BigDecimal("100.00"));
        paymentResponse = new PaymentResponse(
            1L, 1L, 1L, new BigDecimal("100.00"), 
            "payment123.jpg", "receipt.jpg", 1L
        );
        
        payment = new Payment();
        payment.setId(1L);
        payment.setAmount(new BigDecimal("100.00"));
        payment.setImageFilename("payment123.jpg");
        payment.setOriginalFilename("receipt.jpg");
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Should create payment successfully")
    void shouldCreatePaymentSuccessfully() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
            "file", "receipt.jpg", "image/jpeg", "test image content".getBytes()
        );
        
        when(paymentService.createPayment(any(PaymentDto.class), any())).thenReturn(paymentResponse);

        mockMvc.perform(multipart("/payments")
                .file(file)
                .param("identifierId", "1")
                .param("paymentTypeId", "1")
                .param("amount", "100.00")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.identifierId").value(1))
                .andExpect(jsonPath("$.amount").value(100.00));
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Should get all payments successfully")
    void shouldGetAllPaymentsSuccessfully() throws Exception {
        PaymentResponse payment1 = new PaymentResponse(
            1L, 1L, 1L, new BigDecimal("100.00"), 
            "payment1.jpg", "receipt1.jpg", 1L
        );
        PaymentResponse payment2 = new PaymentResponse(
            2L, 2L, 1L, new BigDecimal("200.00"), 
            "payment2.jpg", "receipt2.jpg", 1L
        );
        
        List<PaymentResponse> payments = Arrays.asList(payment1, payment2);
        when(paymentService.getAllPayments()).thenReturn(payments);

        mockMvc.perform(get("/payments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].amount").value(100.00))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].amount").value(200.00));
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Should get payment by id successfully")
    void shouldGetPaymentByIdSuccessfully() throws Exception {
        when(paymentService.getPaymentByIdResponse(1L)).thenReturn(paymentResponse);

        mockMvc.perform(get("/payments/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.identifierId").value(1))
                .andExpect(jsonPath("$.amount").value(100.00))
                .andExpect(jsonPath("$.imageFilename").value("payment123.jpg"));
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Should update payment successfully")
    void shouldUpdatePaymentSuccessfully() throws Exception {
        when(paymentService.updatePayment(eq(1L), any(PaymentDto.class))).thenReturn(paymentResponse);

        mockMvc.perform(put("/payments/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(paymentDto))
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.amount").value(100.00));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should delete payment successfully")
    void shouldDeletePaymentSuccessfully() throws Exception {
        doNothing().when(paymentService).deletePayment(1L);

        mockMvc.perform(delete("/payments/1")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string("Payment deleted successfully"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should update payment state successfully")
    void shouldUpdatePaymentStateSuccessfully() throws Exception {
        when(paymentService.updatePaymentState(1L, 2L)).thenReturn(paymentResponse);

        mockMvc.perform(put("/payments/1/state/2")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should update payment type successfully")
    void shouldUpdatePaymentTypeSuccessfully() throws Exception {
        when(paymentService.updatePaymentType(1L, 2L)).thenReturn(paymentResponse);

        mockMvc.perform(put("/payments/1/type/2")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should update payment file successfully")
    void shouldUpdatePaymentFileSuccessfully() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
            "file", "new-receipt.jpg", "image/jpeg", "new test image content".getBytes()
        );
        
        PaymentResponse updatedResponse = new PaymentResponse(
            1L, 1L, 1L, new BigDecimal("100.00"), 
            "payment456.jpg", "new-receipt.jpg", 1L
        );
        
        when(paymentService.updatePaymentFile(eq(1L), any())).thenReturn(updatedResponse);

        mockMvc.perform(multipart("/payments/1/file")
                .file(file)
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.originalFilename").value("new-receipt.jpg"));
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Should get payments by state successfully")
    void shouldGetPaymentsByStateSuccessfully() throws Exception {
        List<PaymentResponse> payments = Arrays.asList(paymentResponse);
        when(paymentService.getPaymentsByState(1L)).thenReturn(payments);

        mockMvc.perform(get("/payments/state/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Should get payments by payment type successfully")
    void shouldGetPaymentsByPaymentTypeSuccessfully() throws Exception {
        List<PaymentResponse> payments = Arrays.asList(paymentResponse);
        when(paymentService.getPaymentsByPaymentType(1L)).thenReturn(payments);

        mockMvc.perform(get("/payments/type/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    @DisplayName("Should return 401 for unauthorized access")
    void shouldReturn401ForUnauthorizedAccess() throws Exception {
        mockMvc.perform(get("/payments"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Should return 400 for invalid multipart request")
    void shouldReturn400ForInvalidMultipartRequest() throws Exception {
        mockMvc.perform(multipart("/payments")
                .param("identifierId", "invalid")
                .with(csrf()))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Should return 403 for forbidden delete operation")
    void shouldReturn403ForForbiddenDeleteOperation() throws Exception {
        mockMvc.perform(delete("/payments/1")
                .with(csrf()))
                .andExpect(status().isForbidden());
    }
}