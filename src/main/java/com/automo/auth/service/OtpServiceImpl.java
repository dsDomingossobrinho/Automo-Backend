package com.automo.auth.service;

import com.automo.auth.entity.Otp;
import com.automo.auth.repository.OtpRepository;
import com.automo.auth.util.ContactValidator;
import com.automo.mail.service.EmailService;
import com.automo.sms.service.SmsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Slf4j
public class OtpServiceImpl implements OtpService {

    private final OtpRepository otpRepository;
    private final EmailService emailService;
    private final SmsService smsService;
    
    private static final int OTP_LENGTH = 6;
    private static final int OTP_EXPIRY_MINUTES = 5;

    @Override
    @Transactional
    public String generateAndSendOtp(String contact, String purpose) {
        // Detectar tipo de contato
        ContactValidator.ContactType contactType = ContactValidator.getContactType(contact);
        
        if (contactType == ContactValidator.ContactType.UNKNOWN) {
            throw new IllegalArgumentException("Invalid contact format. Must be email or phone number.");
        }
        
        // Gerar código OTP
        String otpCode = generateOtpCode();
        
        // Calcular expiração
        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(OTP_EXPIRY_MINUTES);
        
        // Marcar OTPs anteriores como usados
        otpRepository.markAllAsUsedByContactAndPurpose(contact, purpose);
        
        // Salvar novo OTP
        Otp otp = new Otp();
        otp.setContact(contact);
        otp.setContactType(contactType.name());
        otp.setOtpCode(otpCode);
        otp.setExpiresAt(expiresAt);
        otp.setPurpose(purpose);
        otp.setUsed(false);
        
        otpRepository.save(otp);
        
        // Enviar OTP baseado no tipo de contato
        if (contactType == ContactValidator.ContactType.EMAIL) {
            emailService.sendOtpEmail(contact, otpCode, purpose);
            log.info("OTP sent via EMAIL to: {} for purpose: {}", contact, purpose);
        } else if (contactType == ContactValidator.ContactType.PHONE) {
            smsService.sendOtpSms(contact, otpCode, purpose);
            log.info("OTP sent via SMS to: {} for purpose: {}", contact, purpose);
        }
        
        return otpCode;
    }

    @Override
    @Transactional
    public boolean verifyOtp(String contact, String otpCode, String purpose) {
        LocalDateTime now = LocalDateTime.now();
        
        var otpOptional = otpRepository.findByContactAndOtpCodeAndPurposeAndUsedFalseAndExpiresAtAfter(
                contact, otpCode, purpose, now);
        
        if (otpOptional.isPresent()) {
            Otp otp = otpOptional.get();
            otp.setUsed(true);
            otpRepository.save(otp);
            
            log.info("OTP verified successfully for: {} purpose: {}", contact, purpose);
            return true;
        }
        
        log.warn("OTP verification failed for: {} purpose: {}", contact, purpose);
        return false;
    }

    @Override
    @Transactional
    @Scheduled(fixedRate = 300000) // Executar a cada 5 minutos
    public void cleanupExpiredOtps() {
        LocalDateTime now = LocalDateTime.now();
        otpRepository.deleteByExpiresAtBefore(now);
        log.info("Expired OTPs cleaned up");
    }
    
    private String generateOtpCode() {
        Random random = new Random();
        StringBuilder otp = new StringBuilder();
        
        for (int i = 0; i < OTP_LENGTH; i++) {
            otp.append(random.nextInt(10));
        }
        
        return otp.toString();
    }
} 