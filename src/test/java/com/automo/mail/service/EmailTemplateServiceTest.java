package com.automo.mail.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests for EmailTemplateService")
class EmailTemplateServiceTest {

    @InjectMocks
    private EmailTemplateService emailTemplateService;

    private static final String MOCK_HTML_TEMPLATE = """
        <html>
        <body>
            <h1>Automo - Código de Verificação</h1>
            <img src="{{LOGO_BASE64}}" alt="Automo Logo" class="logo">
            <p>Seu código de verificação é:</p>
            <div class="otp-code">{{OTP_CODE}}</div>
        </body>
        </html>
        """;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(emailTemplateService, "logoBase64", "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mNk+M9QDwADhgGAWjR9awAAAABJRU5ErkJggg==");
    }

    @Test
    @DisplayName("Should load and process OTP email template successfully")
    void shouldLoadAndProcessOtpEmailTemplateSuccessfully() {
        try (MockedStatic<ClassPathResource> mockedResource = mockStatic(ClassPathResource.class)) {
            ClassPathResource mockResource = mock(ClassPathResource.class);
            InputStream mockInputStream = new ByteArrayInputStream(MOCK_HTML_TEMPLATE.getBytes());

            mockedResource.when(() -> new ClassPathResource(any(String.class))).thenReturn(mockResource);
            when(mockResource.getInputStream()).thenReturn(mockInputStream);

            String otpCode = "123456";
            String result = emailTemplateService.getOtpEmailTemplate(otpCode);

            assertNotNull(result);
            assertTrue(result.contains("123456"));
            assertTrue(result.contains("data:image/png;base64"));
            assertFalse(result.contains("{{OTP_CODE}}"));
            assertFalse(result.contains("{{LOGO_BASE64}}"));
        } catch (IOException e) {
            fail("Should not throw IOException");
        }
    }

    @Test
    @DisplayName("Should load and process forgot password OTP email template successfully")
    void shouldLoadAndProcessForgotPasswordOtpEmailTemplateSuccessfully() {
        try (MockedStatic<ClassPathResource> mockedResource = mockStatic(ClassPathResource.class)) {
            ClassPathResource mockResource = mock(ClassPathResource.class);
            InputStream mockInputStream = new ByteArrayInputStream(MOCK_HTML_TEMPLATE.getBytes());

            mockedResource.when(() -> new ClassPathResource(any(String.class))).thenReturn(mockResource);
            when(mockResource.getInputStream()).thenReturn(mockInputStream);

            String otpCode = "654321";
            String result = emailTemplateService.getForgotPasswordOtpEmailTemplate(otpCode);

            assertNotNull(result);
            assertTrue(result.contains("654321"));
            assertTrue(result.contains("data:image/png;base64"));
            assertFalse(result.contains("{{OTP_CODE}}"));
            assertFalse(result.contains("{{LOGO_BASE64}}"));
        } catch (IOException e) {
            fail("Should not throw IOException");
        }
    }

    @Test
    @DisplayName("Should load and process back office OTP email template successfully")
    void shouldLoadAndProcessBackOfficeOtpEmailTemplateSuccessfully() {
        try (MockedStatic<ClassPathResource> mockedResource = mockStatic(ClassPathResource.class)) {
            ClassPathResource mockResource = mock(ClassPathResource.class);
            InputStream mockInputStream = new ByteArrayInputStream(MOCK_HTML_TEMPLATE.getBytes());

            mockedResource.when(() -> new ClassPathResource(any(String.class))).thenReturn(mockResource);
            when(mockResource.getInputStream()).thenReturn(mockInputStream);

            String otpCode = "789012";
            String result = emailTemplateService.getBackOfficeOtpEmailTemplate(otpCode);

            assertNotNull(result);
            assertTrue(result.contains("789012"));
            assertTrue(result.contains("data:image/png;base64"));
            assertFalse(result.contains("{{OTP_CODE}}"));
            assertFalse(result.contains("{{LOGO_BASE64}}"));
        } catch (IOException e) {
            fail("Should not throw IOException");
        }
    }

    @Test
    @DisplayName("Should load and process user OTP email template successfully")
    void shouldLoadAndProcessUserOtpEmailTemplateSuccessfully() {
        try (MockedStatic<ClassPathResource> mockedResource = mockStatic(ClassPathResource.class)) {
            ClassPathResource mockResource = mock(ClassPathResource.class);
            InputStream mockInputStream = new ByteArrayInputStream(MOCK_HTML_TEMPLATE.getBytes());

            mockedResource.when(() -> new ClassPathResource(any(String.class))).thenReturn(mockResource);
            when(mockResource.getInputStream()).thenReturn(mockInputStream);

            String otpCode = "345678";
            String result = emailTemplateService.getUserOtpEmailTemplate(otpCode);

            assertNotNull(result);
            assertTrue(result.contains("345678"));
            assertTrue(result.contains("data:image/png;base64"));
            assertFalse(result.contains("{{OTP_CODE}}"));
            assertFalse(result.contains("{{LOGO_BASE64}}"));
        } catch (IOException e) {
            fail("Should not throw IOException");
        }
    }

    @Test
    @DisplayName("Should handle IOException when loading template")
    void shouldHandleIOExceptionWhenLoadingTemplate() {
        try (MockedStatic<ClassPathResource> mockedResource = mockStatic(ClassPathResource.class)) {
            ClassPathResource mockResource = mock(ClassPathResource.class);

            mockedResource.when(() -> new ClassPathResource(any(String.class))).thenReturn(mockResource);
            when(mockResource.getInputStream()).thenThrow(new IOException("File not found"));

            String otpCode = "123456";
            String result = emailTemplateService.getOtpEmailTemplate(otpCode);

            assertNull(result);
        } catch (IOException e) {
            // Expected behavior - method should handle IOException gracefully
        }
    }

    @Test
    @DisplayName("Should replace placeholders correctly in template")
    void shouldReplacePlaceholdersCorrectlyInTemplate() {
        try (MockedStatic<ClassPathResource> mockedResource = mockStatic(ClassPathResource.class)) {
            String templateWithMultiplePlaceholders = """
                <html>
                <body>
                    <img src="{{LOGO_BASE64}}" alt="Logo">
                    <p>Your OTP: {{OTP_CODE}}</p>
                    <p>Another OTP reference: {{OTP_CODE}}</p>
                    <img src="{{LOGO_BASE64}}" alt="Footer Logo">
                </body>
                </html>
                """;

            ClassPathResource mockResource = mock(ClassPathResource.class);
            InputStream mockInputStream = new ByteArrayInputStream(templateWithMultiplePlaceholders.getBytes());

            mockedResource.when(() -> new ClassPathResource(any(String.class))).thenReturn(mockResource);
            when(mockResource.getInputStream()).thenReturn(mockInputStream);

            String otpCode = "999888";
            String result = emailTemplateService.getOtpEmailTemplate(otpCode);

            assertNotNull(result);
            
            // Check that all OTP_CODE placeholders were replaced
            assertFalse(result.contains("{{OTP_CODE}}"));
            assertEquals(2, result.split("999888").length - 1); // Count occurrences

            // Check that all LOGO_BASE64 placeholders were replaced
            assertFalse(result.contains("{{LOGO_BASE64}}"));
            assertEquals(2, result.split("data:image/png;base64").length - 1); // Count occurrences
        } catch (IOException e) {
            fail("Should not throw IOException");
        }
    }

    @Test
    @DisplayName("Should handle empty OTP code")
    void shouldHandleEmptyOtpCode() {
        try (MockedStatic<ClassPathResource> mockedResource = mockStatic(ClassPathResource.class)) {
            ClassPathResource mockResource = mock(ClassPathResource.class);
            InputStream mockInputStream = new ByteArrayInputStream(MOCK_HTML_TEMPLATE.getBytes());

            mockedResource.when(() -> new ClassPathResource(any(String.class))).thenReturn(mockResource);
            when(mockResource.getInputStream()).thenReturn(mockInputStream);

            String result = emailTemplateService.getOtpEmailTemplate("");

            assertNotNull(result);
            assertTrue(result.contains("data:image/png;base64"));
            assertFalse(result.contains("{{OTP_CODE}}"));
            assertFalse(result.contains("{{LOGO_BASE64}}"));
        } catch (IOException e) {
            fail("Should not throw IOException");
        }
    }

    @Test
    @DisplayName("Should handle null OTP code gracefully")
    void shouldHandleNullOtpCodeGracefully() {
        try (MockedStatic<ClassPathResource> mockedResource = mockStatic(ClassPathResource.class)) {
            ClassPathResource mockResource = mock(ClassPathResource.class);
            InputStream mockInputStream = new ByteArrayInputStream(MOCK_HTML_TEMPLATE.getBytes());

            mockedResource.when(() -> new ClassPathResource(any(String.class))).thenReturn(mockResource);
            when(mockResource.getInputStream()).thenReturn(mockInputStream);

            String result = emailTemplateService.getOtpEmailTemplate(null);

            assertNotNull(result);
            assertTrue(result.contains("data:image/png;base64"));
            assertTrue(result.contains("null")); // null gets converted to string "null"
            assertFalse(result.contains("{{OTP_CODE}}"));
            assertFalse(result.contains("{{LOGO_BASE64}}"));
        } catch (IOException e) {
            fail("Should not throw IOException");
        }
    }
}