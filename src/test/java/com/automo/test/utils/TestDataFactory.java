package com.automo.test.utils;

import com.automo.accountType.entity.AccountType;
import com.automo.admin.entity.Admin;
import com.automo.agent.entity.Agent;
import com.automo.area.entity.Area;
import com.automo.auth.entity.Auth;
import com.automo.auth.entity.Otp;
import com.automo.country.entity.Country;
import com.automo.deal.entity.Deal;
import com.automo.identifier.entity.Identifier;
import com.automo.identifierType.entity.IdentifierType;
import com.automo.lead.entity.Lead;
import com.automo.leadType.entity.LeadType;
import com.automo.messageCount.entity.MessageCount;
import com.automo.notification.entity.Notification;
import com.automo.notificationType.entity.NotificationType;
import com.automo.organizationType.entity.OrganizationType;
import com.automo.payment.entity.Payment;
import com.automo.paymentType.entity.PaymentType;
import com.automo.product.entity.Product;
import com.automo.productCategory.entity.ProductCategory;
import com.automo.promotion.entity.Promotion;
import com.automo.province.entity.Province;
import com.automo.role.entity.Role;
import com.automo.state.entity.State;
import com.automo.subscription.entity.Subscription;
import com.automo.subscriptionPlan.entity.SubscriptionPlan;
import com.automo.user.entity.User;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Factory class para criar objetos de teste com dados válidos
 * para todas as entidades do sistema Automo.
 */
public class TestDataFactory {

    // =============================================================================
    // ENTIDADES BASE E LOOKUP
    // =============================================================================

    public static State createActiveState() {
        State state = new State();
        state.setState("ACTIVE");
        return state;
    }

    public static State createInactiveState() {
        State state = new State();
        state.setState("INACTIVE");
        return state;
    }

    public static State createEliminatedState() {
        State state = new State();
        state.setState("ELIMINATED");
        return state;
    }

    public static Role createUserRole() {
        Role role = new Role();
        role.setRole("USER");
        return role;
    }

    public static Role createAdminRole() {
        Role role = new Role();
        role.setRole("ADMIN");
        return role;
    }

    public static Role createAgentRole() {
        Role role = new Role();
        role.setRole("AGENT");
        return role;
    }

    public static AccountType createIndividualAccountType() {
        AccountType accountType = new AccountType();
        accountType.setType("INDIVIDUAL");
        accountType.setDescription("Individual account type");
        return accountType;
    }

    public static AccountType createCorporateAccountType() {
        AccountType accountType = new AccountType();
        accountType.setType("CORPORATE");
        accountType.setDescription("Corporate account type");
        return accountType;
    }

    public static AccountType createValidAccountType(String type, String description) {
        AccountType accountType = new AccountType();
        accountType.setType(type);
        accountType.setDescription(description);
        return accountType;
    }

    public static Country createPortugalCountry() {
        Country country = new Country();
        country.setCountry("Portugal");
        country.setNumberDigits(9);
        country.setIndicative("+351");
        country.setState(createActiveState());
        return country;
    }

    public static Country createSpainCountry() {
        Country country = new Country();
        country.setCountry("Spain");
        country.setNumberDigits(9);
        country.setIndicative("+34");
        country.setState(createActiveState());
        return country;
    }

    public static Country createBrazilCountry() {
        Country country = new Country();
        country.setCountry("Brazil");
        country.setNumberDigits(11);
        country.setIndicative("+55");
        country.setState(createActiveState());
        return country;
    }

    public static Province createLisbonProvince() {
        Province province = new Province();
        province.setProvince("Lisboa");
        province.setCountry(createPortugalCountry());
        province.setState(createActiveState());
        return province;
    }

    public static Province createLisbonProvince(Country country) {
        Province province = new Province();
        province.setProvince("Lisboa");
        province.setCountry(country);
        province.setState(createActiveState());
        return province;
    }

    public static Province createPortoProvince(Country country) {
        Province province = new Province();
        province.setProvince("Porto");
        province.setCountry(country);
        province.setState(createActiveState());
        return province;
    }

    public static Province createMadridProvince(Country country) {
        Province province = new Province();
        province.setProvince("Madrid");
        province.setCountry(country);
        province.setState(createActiveState());
        return province;
    }

    public static Area createLisbonArea() {
        Area area = new Area();
        area.setArea("Lisboa Centro");
        area.setDescription("Central area of Lisboa");
        area.setState(createActiveState());
        return area;
    }

    public static Area createCascaisArea() {
        Area area = new Area();
        area.setArea("Cascais");
        area.setDescription("Coastal area near Lisboa");
        area.setState(createActiveState());
        return area;
    }

    public static Area createSintraArea() {
        Area area = new Area();
        area.setArea("Sintra");
        area.setDescription("Historic area with palaces");
        area.setState(createActiveState());
        return area;
    }

    // =============================================================================
    // ENTIDADES DE AUTENTICAÇÃO
    // =============================================================================

    public static Auth createValidAuth() {
        Auth auth = new Auth();
        auth.setEmail("test@automo.com");
        auth.setPassword("$2a$10$encrypted.password.hash");
        return auth;
    }

    public static Auth createValidAuth(String email) {
        Auth auth = new Auth();
        auth.setEmail(email);
        auth.setPassword("$2a$10$encrypted.password.hash");
        return auth;
    }

    public static User createValidUser(Auth auth, AccountType accountType, State state) {
        User user = new User();
        user.setName("Test User");
        user.setContact("912345678");
        user.setAuth(auth);
        user.setAccountType(accountType);
        user.setState(state);
        return user;
    }

    public static Admin createValidAdmin(Auth auth, State state) {
        Admin admin = new Admin();
        admin.setName("Test Admin");
        admin.setEmail("admin@automo.com");
        admin.setAuth(auth);
        admin.setState(state);
        return admin;
    }

    public static Agent createValidAgent(State state) {
        Agent agent = new Agent();
        agent.setName("Test Agent");
        agent.setDescription("Test agent description");
        agent.setLocation("Lisboa, Portugal");
        agent.setRestrictions("No restrictions");
        agent.setActivityFlow("Standard flow");
        agent.setState(state);
        return agent;
    }

    public static Otp createValidOtp(String contact) {
        Otp otp = new Otp();
        otp.setContact(contact);
        otp.setContactType("EMAIL");
        otp.setOtpCode("123456");
        otp.setPurpose("LOGIN");
        otp.setExpiresAt(LocalDateTime.now().plusMinutes(5));
        otp.setUsed(false);
        return otp;
    }
    
    public static Otp createValidOtpForPhone(String phone) {
        Otp otp = new Otp();
        otp.setContact(phone);
        otp.setContactType("PHONE");
        otp.setOtpCode("123456");
        otp.setPurpose("LOGIN");
        otp.setExpiresAt(LocalDateTime.now().plusMinutes(5));
        otp.setUsed(false);
        return otp;
    }
    
    public static Otp createOtpWithPurpose(String contact, String purpose) {
        Otp otp = new Otp();
        otp.setContact(contact);
        otp.setContactType("EMAIL");
        otp.setOtpCode("123456");
        otp.setPurpose(purpose);
        otp.setExpiresAt(LocalDateTime.now().plusMinutes(5));
        otp.setUsed(false);
        return otp;
    }
    
    public static Otp createExpiredOtp(String contact) {
        Otp otp = new Otp();
        otp.setContact(contact);
        otp.setContactType("EMAIL");
        otp.setOtpCode("123456");
        otp.setPurpose("LOGIN");
        otp.setExpiresAt(LocalDateTime.now().minusMinutes(5));
        otp.setUsed(false);
        return otp;
    }
    
    public static Otp createUsedOtp(String contact) {
        Otp otp = new Otp();
        otp.setContact(contact);
        otp.setContactType("EMAIL");
        otp.setOtpCode("123456");
        otp.setPurpose("LOGIN");
        otp.setExpiresAt(LocalDateTime.now().plusMinutes(5));
        otp.setUsed(true);
        return otp;
    }
    
    public static Otp createOtpWithCustomCode(String contact, String otpCode) {
        Otp otp = new Otp();
        otp.setContact(contact);
        otp.setContactType("EMAIL");
        otp.setOtpCode(otpCode);
        otp.setPurpose("LOGIN");
        otp.setExpiresAt(LocalDateTime.now().plusMinutes(5));
        otp.setUsed(false);
        return otp;
    }
    
    public static Otp createPasswordResetOtp(String contact) {
        Otp otp = new Otp();
        otp.setContact(contact);
        otp.setContactType("EMAIL");
        otp.setOtpCode("654321");
        otp.setPurpose("RESET_PASSWORD");
        otp.setExpiresAt(LocalDateTime.now().plusMinutes(5));
        otp.setUsed(false);
        return otp;
    }
    
    public static Otp createOtpWithContactType(String contact, String contactType, String purpose) {
        Otp otp = new Otp();
        otp.setContact(contact);
        otp.setContactType(contactType);
        otp.setOtpCode("789012");
        otp.setPurpose(purpose);
        otp.setExpiresAt(LocalDateTime.now().plusMinutes(5));
        otp.setUsed(false);
        return otp;
    }

    // =============================================================================
    // ENTIDADES DE IDENTIFICAÇÃO E TIPOS
    // =============================================================================

    public static IdentifierType createNifIdentifierType() {
        IdentifierType identifierType = new IdentifierType();
        identifierType.setType("NIF");
        identifierType.setDescription("Número de Identificação Fiscal");
        return identifierType;
    }

    public static IdentifierType createValidIdentifierType(String type, String description) {
        IdentifierType identifierType = new IdentifierType();
        identifierType.setType(type);
        identifierType.setDescription(description);
        return identifierType;
    }

    public static Identifier createValidIdentifier(Long userId, IdentifierType identifierType, State state) {
        Identifier identifier = new Identifier();
        identifier.setUserId(userId);
        identifier.setIdentifierType(identifierType);
        identifier.setState(state);
        return identifier;
    }

    public static com.automo.identifier.dto.IdentifierDto createValidIdentifierDto(Long userId, Long identifierTypeId, Long stateId) {
        return new com.automo.identifier.dto.IdentifierDto(userId, identifierTypeId, stateId);
    }

    public static com.automo.identifierType.dto.IdentifierTypeDto createValidIdentifierTypeDto(String type, String description) {
        return new com.automo.identifierType.dto.IdentifierTypeDto(type, description);
    }

    public static OrganizationType createPrivateOrganizationType() {
        OrganizationType organizationType = new OrganizationType();
        organizationType.setType("PRIVATE");
        organizationType.setDescription("Private organization type");
        return organizationType;
    }

    public static OrganizationType createValidOrganizationType(String type, String description) {
        OrganizationType organizationType = new OrganizationType();
        organizationType.setType(type);
        organizationType.setDescription(description);
        return organizationType;
    }

    public static PaymentType createBankTransferPaymentType() {
        PaymentType paymentType = new PaymentType();
        paymentType.setType("BANK_TRANSFER");
        paymentType.setDescription("Bank transfer payment method");
        return paymentType;
    }

    public static PaymentType createValidPaymentType(String type, String description) {
        PaymentType paymentType = new PaymentType();
        paymentType.setType(type);
        paymentType.setDescription(description);
        return paymentType;
    }

    public static NotificationType createEmailNotificationType() {
        NotificationType notificationType = new NotificationType();
        notificationType.setType("EMAIL");
        notificationType.setDescription("Email notification type");
        return notificationType;
    }

    public static NotificationType createSmsNotificationType() {
        NotificationType notificationType = new NotificationType();
        notificationType.setType("SMS");
        notificationType.setDescription("SMS notification type");
        return notificationType;
    }

    public static NotificationType createPushNotificationType() {
        NotificationType notificationType = new NotificationType();
        notificationType.setType("PUSH");
        notificationType.setDescription("Push notification type");
        return notificationType;
    }

    public static LeadType createCallLeadType() {
        LeadType leadType = new LeadType();
        leadType.setType("CALL");
        leadType.setDescription("Call-based lead type");
        return leadType;
    }

    public static LeadType createValidLeadType(String type, String description) {
        LeadType leadType = new LeadType();
        leadType.setType(type);
        leadType.setDescription(description);
        return leadType;
    }

    // =============================================================================
    // ENTIDADES DE PRODUTOS E CATEGORIAS
    // =============================================================================

    public static ProductCategory createCarProductCategory(State state) {
        ProductCategory category = new ProductCategory();
        category.setCategory("Cars");
        category.setDescription("Automobile vehicles");
        category.setState(state);
        return category;
    }

    public static ProductCategory createValidProductCategory(State state) {
        ProductCategory category = new ProductCategory();
        category.setCategory("Electronics");
        category.setDescription("Electronic devices and accessories");
        category.setState(state);
        return category;
    }

    public static ProductCategory createElectronicsProductCategory(State state) {
        ProductCategory category = new ProductCategory();
        category.setCategory("Electronics");
        category.setDescription("Electronic devices and accessories");
        category.setState(state);
        return category;
    }

    public static ProductCategory createBooksProductCategory(State state) {
        ProductCategory category = new ProductCategory();
        category.setCategory("Books");
        category.setDescription("Books and magazines");
        category.setState(state);
        return category;
    }

    public static ProductCategory createHomeGardenProductCategory(State state) {
        ProductCategory category = new ProductCategory();
        category.setCategory("Home & Garden");
        category.setDescription("Home improvement and garden items");
        category.setState(state);
        return category;
    }

    // =============================================================================
    // ENTIDADES DE PROMOÇÃO
    // =============================================================================

    public static Promotion createValidPromotion(State state) {
        Promotion promotion = new Promotion();
        promotion.setName("Summer Sale");
        promotion.setDiscountValue(new BigDecimal("20.00"));
        promotion.setCode("SUMMER20");
        promotion.setState(state);
        return promotion;
    }

    public static Promotion createSummerPromotion(State state) {
        Promotion promotion = new Promotion();
        promotion.setName("Summer Sale");
        promotion.setDiscountValue(new BigDecimal("20.00"));
        promotion.setCode("SUMMER20");
        promotion.setState(state);
        return promotion;
    }

    public static Promotion createBlackFridayPromotion(State state) {
        Promotion promotion = new Promotion();
        promotion.setName("Black Friday");
        promotion.setDiscountValue(new BigDecimal("50.00"));
        promotion.setCode("BF50");
        promotion.setState(state);
        return promotion;
    }

    public static Promotion createChristmasPromotion(State state) {
        Promotion promotion = new Promotion();
        promotion.setName("Christmas Sale");
        promotion.setDiscountValue(new BigDecimal("30.00"));
        promotion.setCode("XMAS30");
        promotion.setState(state);
        return promotion;
    }

    public static Promotion createCustomPromotion(String name, BigDecimal discountValue, String code, State state) {
        Promotion promotion = new Promotion();
        promotion.setName(name);
        promotion.setDiscountValue(discountValue);
        promotion.setCode(code);
        promotion.setState(state);
        return promotion;
    }

    public static Product createValidProduct(State state) {
        Product product = new Product();
        product.setName("BMW X5");
        product.setDescription("Luxury SUV");
        product.setPrice(new BigDecimal("50000.00"));
        product.setImg("bmw-x5.jpg");
        product.setState(state);
        return product;
    }

    public static SubscriptionPlan createBasicSubscriptionPlan(State state) {
        SubscriptionPlan plan = new SubscriptionPlan();
        plan.setName("Basic Plan");
        plan.setDescription("Basic subscription plan");
        plan.setPrice(new BigDecimal("29.99"));
        plan.setState(state);
        return plan;
    }

    public static SubscriptionPlan createPremiumSubscriptionPlan(State state) {
        SubscriptionPlan plan = new SubscriptionPlan();
        plan.setName("Premium Plan");
        plan.setDescription("Premium subscription plan with advanced features");
        plan.setPrice(new BigDecimal("79.99"));
        plan.setState(state);
        return plan;
    }

    public static Subscription createValidSubscription(User user, SubscriptionPlan plan, State state) {
        Subscription subscription = new Subscription();
        subscription.setUser(user);
        subscription.setPlan(plan);
        subscription.setPrice(plan.getPrice());
        subscription.setStartDate(LocalDate.now());
        subscription.setEndDate(LocalDate.now().plusMonths(1));
        subscription.setMessageCount(1000);
        subscription.setState(state);
        return subscription;
    }

    public static Subscription createValidSubscription(User user, SubscriptionPlan plan, LocalDate startDate, LocalDate endDate, State state) {
        Subscription subscription = new Subscription();
        subscription.setUser(user);
        subscription.setPlan(plan);
        subscription.setPrice(plan.getPrice());
        subscription.setStartDate(startDate);
        subscription.setEndDate(endDate);
        subscription.setMessageCount(1000);
        subscription.setState(state);
        return subscription;
    }

    // =============================================================================
    // ENTIDADES DE LEADS E DEALS
    // =============================================================================

    public static Lead createValidLead(Identifier identifier, LeadType leadType, State state) {
        Lead lead = new Lead();
        lead.setIdentifier(identifier);
        lead.setName("Test Lead");
        lead.setEmail("test@example.com");
        lead.setContact("912345678");
        lead.setZone("Test Zone");
        lead.setLeadType(leadType);
        lead.setState(state);
        return lead;
    }

    public static Deal createValidDeal(Identifier identifier, Lead lead, State state) {
        Deal deal = new Deal();
        deal.setIdentifier(identifier);
        deal.setLead(lead);
        deal.setTotal(new BigDecimal("45000.00"));
        deal.setMessageCount(3);
        deal.setState(state);
        return deal;
    }

    public static MessageCount createValidMessageCount(Lead lead, State state) {
        MessageCount messageCount = new MessageCount();
        messageCount.setLead(lead);
        messageCount.setMessageCount(5L);
        messageCount.setState(state);
        return messageCount;
    }

    // =============================================================================
    // ENTIDADES DE PAGAMENTO E NOTIFICAÇÃO
    // =============================================================================

    public static Payment createValidPayment(Identifier identifier, PaymentType paymentType, State state) {
        Payment payment = new Payment();
        payment.setIdentifier(identifier);
        payment.setPaymentType(paymentType);
        payment.setAmount(new BigDecimal("100.00"));
        payment.setImageFilename(UUID.randomUUID().toString() + ".jpg");
        payment.setOriginalFilename("receipt.jpg");
        payment.setState(state);
        return payment;
    }

    public static Notification createValidNotification(Identifier sender, Identifier receiver, State state) {
        Notification notification = new Notification();
        notification.setSender(sender);
        notification.setReceiver(receiver);
        notification.setUrlRedirect("https://example.com/test");
        notification.setState(state);
        return notification;
    }

    public static Notification createValidNotification(Identifier sender, Identifier receiver, String urlRedirect, State state) {
        Notification notification = new Notification();
        notification.setSender(sender);
        notification.setReceiver(receiver);
        notification.setUrlRedirect(urlRedirect);
        notification.setState(state);
        return notification;
    }

    // =============================================================================
    // MÉTODOS UTILITÁRIOS
    // =============================================================================

    /**
     * Cria um email único para testes
     */
    public static String createUniqueEmail() {
        return "test-" + UUID.randomUUID().toString().substring(0, 8) + "@automo.com";
    }

    /**
     * Cria um telefone único para testes
     */
    public static String createUniquePhone() {
        return "91" + String.format("%07d", (int) (Math.random() * 10000000));
    }

    /**
     * Cria um valor monetário aleatório para testes
     */
    public static BigDecimal createRandomPrice() {
        return new BigDecimal(Math.random() * 100000).setScale(2, BigDecimal.ROUND_HALF_UP);
    }

    // =============================================================================
    // SUBSCRIPTION AND SUBSCRIPTION PLAN DTOs
    // =============================================================================

    public static com.automo.subscription.dto.SubscriptionDto createValidSubscriptionDto(Long userId, Long planId, Long stateId) {
        return new com.automo.subscription.dto.SubscriptionDto(
                userId,
                planId,
                null, // promotionId
                new BigDecimal("29.99"),
                LocalDate.now(),
                LocalDate.now().plusMonths(1),
                1000,
                stateId
        );
    }

    public static com.automo.subscriptionPlan.dto.SubscriptionPlanDto createValidSubscriptionPlanDto(Long stateId) {
        return new com.automo.subscriptionPlan.dto.SubscriptionPlanDto(
                "Basic Plan",
                new BigDecimal("29.99"),
                "Basic subscription plan for testing",
                stateId
        );
    }

    // =============================================================================
    // GEOGRAPHIC ENTITY DTOs
    // =============================================================================

    public static com.automo.country.dto.CountryDto createValidCountryDto(Long stateId) {
        return new com.automo.country.dto.CountryDto(
                "Portugal",
                9,
                "+351",
                stateId
        );
    }

    public static com.automo.country.dto.CountryDto createValidCountryDto(String countryName, Integer numberDigits, String indicative, Long stateId) {
        return new com.automo.country.dto.CountryDto(
                countryName,
                numberDigits,
                indicative,
                stateId
        );
    }

    public static com.automo.province.dto.ProvinceDto createValidProvinceDto(Long countryId, Long stateId) {
        return new com.automo.province.dto.ProvinceDto(
                "Lisboa",
                countryId,
                stateId
        );
    }

    public static com.automo.province.dto.ProvinceDto createValidProvinceDto(String provinceName, Long countryId, Long stateId) {
        return new com.automo.province.dto.ProvinceDto(
                provinceName,
                countryId,
                stateId
        );
    }

    public static com.automo.area.dto.AreaDto createValidAreaDto(Long stateId) {
        return new com.automo.area.dto.AreaDto(
                "Lisboa Centro",
                "Central area of Lisboa",
                stateId
        );
    }

    public static com.automo.area.dto.AreaDto createValidAreaDto(String areaName, String description, Long stateId) {
        return new com.automo.area.dto.AreaDto(
                areaName,
                description,
                stateId
        );
    }

    // =============================================================================
    // PRODUCT CATEGORY AND PROMOTION DTOs
    // =============================================================================

    public static com.automo.productCategory.dto.ProductCategoryDto createValidProductCategoryDto(Long stateId) {
        return new com.automo.productCategory.dto.ProductCategoryDto(
                "Electronics",
                "Electronic devices and accessories",
                stateId
        );
    }

    public static com.automo.productCategory.dto.ProductCategoryDto createValidProductCategoryDto(String category, String description, Long stateId) {
        return new com.automo.productCategory.dto.ProductCategoryDto(
                category,
                description,
                stateId
        );
    }

    public static com.automo.promotion.dto.PromotionDto createValidPromotionDto(Long stateId) {
        return new com.automo.promotion.dto.PromotionDto(
                "Summer Sale",
                new BigDecimal("20.00"),
                "SUMMER20",
                stateId
        );
    }

    public static com.automo.promotion.dto.PromotionDto createValidPromotionDto(String name, BigDecimal discountValue, String code, Long stateId) {
        return new com.automo.promotion.dto.PromotionDto(
                name,
                discountValue,
                code,
                stateId
        );
    }

    // =============================================================================
    // AUXILIARY MODULE DTOs
    // =============================================================================

    public static com.automo.state.dto.StateDto createValidStateDto(String state, String description) {
        return new com.automo.state.dto.StateDto(state, description);
    }

    public static com.automo.role.dto.RoleDto createValidRoleDto(String role, String description) {
        return new com.automo.role.dto.RoleDto(role, description);
    }

    public static com.automo.accountType.dto.AccountTypeDto createValidAccountTypeDto(String type, String description) {
        return new com.automo.accountType.dto.AccountTypeDto(type, description);
    }

    public static com.automo.paymentType.dto.PaymentTypeDto createValidPaymentTypeDto(String type, String description) {
        return new com.automo.paymentType.dto.PaymentTypeDto(type, description);
    }

    public static com.automo.organizationType.dto.OrganizationTypeDto createValidOrganizationTypeDto(String type, String description) {
        return new com.automo.organizationType.dto.OrganizationTypeDto(type, description);
    }

    public static com.automo.leadType.dto.LeadTypeDto createValidLeadTypeDto(String type, String description) {
        return new com.automo.leadType.dto.LeadTypeDto(type, description);
    }

    // =============================================================================
    // RELATIONSHIP ENTITIES FACTORY METHODS
    // =============================================================================

    /**
     * Creates a valid AuthRoles relationship entity
     */
    public static com.automo.authRoles.entity.AuthRoles createValidAuthRoles(Auth auth, Role role, State state) {
        com.automo.authRoles.entity.AuthRoles authRoles = new com.automo.authRoles.entity.AuthRoles();
        authRoles.setAuth(auth);
        authRoles.setRole(role);
        authRoles.setState(state);
        return authRoles;
    }

    /**
     * Creates a valid AgentAreas relationship entity
     */
    public static com.automo.agentAreas.entity.AgentAreas createValidAgentAreas(Agent agent, Area area, State state) {
        com.automo.agentAreas.entity.AgentAreas agentAreas = new com.automo.agentAreas.entity.AgentAreas();
        agentAreas.setAgent(agent);
        agentAreas.setArea(area);
        agentAreas.setState(state);
        return agentAreas;
    }

    /**
     * Creates a valid AgentProduct relationship entity (no state - physical delete)
     */
    public static com.automo.agentProduct.entity.AgentProduct createValidAgentProduct(Agent agent, Product product) {
        com.automo.agentProduct.entity.AgentProduct agentProduct = new com.automo.agentProduct.entity.AgentProduct();
        agentProduct.setAgent(agent);
        agentProduct.setProduct(product);
        return agentProduct;
    }

    /**
     * Creates a valid DealProduct relationship entity
     */
    public static com.automo.dealProduct.entity.DealProduct createValidDealProduct(Deal deal, Product product, State state) {
        com.automo.dealProduct.entity.DealProduct dealProduct = new com.automo.dealProduct.entity.DealProduct();
        dealProduct.setDeal(deal);
        dealProduct.setProduct(product);
        dealProduct.setState(state);
        return dealProduct;
    }

    /**
     * Creates a valid MessageCount entity (updated version - already exists above but enhanced)
     */
    public static MessageCount createValidMessageCount(Lead lead, Integer messageCount, State state) {
        MessageCount messageCountEntity = new MessageCount();
        messageCountEntity.setLead(lead);
        messageCountEntity.setMessageCount(messageCount);
        messageCountEntity.setState(state);
        return messageCountEntity;
    }

    // =============================================================================
    // RELATIONSHIP ENTITIES DTO FACTORY METHODS
    // =============================================================================

    /**
     * Creates a valid AuthRolesDto
     */
    public static com.automo.authRoles.dto.AuthRolesDto createValidAuthRolesDto(Long authId, Long roleId, Long stateId) {
        return new com.automo.authRoles.dto.AuthRolesDto(authId, roleId, stateId);
    }

    /**
     * Creates a valid AgentAreasDto
     */
    public static com.automo.agentAreas.dto.AgentAreasDto createValidAgentAreasDto(Long agentId, Long areaId, Long stateId) {
        return new com.automo.agentAreas.dto.AgentAreasDto(agentId, areaId, stateId);
    }

    /**
     * Creates a valid AgentProductDto (no state field)
     */
    public static com.automo.agentProduct.dto.AgentProductDto createValidAgentProductDto(Long agentId, Long productId) {
        return new com.automo.agentProduct.dto.AgentProductDto(agentId, productId);
    }

    /**
     * Creates a valid DealProductDto
     */
    public static com.automo.dealProduct.dto.DealProductDto createValidDealProductDto(Long dealId, Long productId, Long stateId) {
        return new com.automo.dealProduct.dto.DealProductDto(dealId, productId, stateId);
    }

    /**
     * Creates a valid MessageCountDto
     */
    public static com.automo.messageCount.dto.MessageCountDto createValidMessageCountDto(Long leadId, Integer messageCount, Long stateId) {
        return new com.automo.messageCount.dto.MessageCountDto(leadId, messageCount, stateId);
    }

    // =============================================================================
    // ASSOCIATED CONTACT AND EMAIL ENTITIES
    // =============================================================================

    /**
     * Creates a valid AssociatedContact entity
     */
    public static com.automo.associatedContact.entity.AssociatedContact createValidAssociatedContact(Identifier identifier, State state) {
        com.automo.associatedContact.entity.AssociatedContact associatedContact = new com.automo.associatedContact.entity.AssociatedContact();
        associatedContact.setIdentifier(identifier);
        associatedContact.setContact("912345678");
        associatedContact.setState(state);
        return associatedContact;
    }

    /**
     * Creates a valid AssociatedContact entity with custom contact
     */
    public static com.automo.associatedContact.entity.AssociatedContact createValidAssociatedContact(Identifier identifier, String contact, State state) {
        com.automo.associatedContact.entity.AssociatedContact associatedContact = new com.automo.associatedContact.entity.AssociatedContact();
        associatedContact.setIdentifier(identifier);
        associatedContact.setContact(contact);
        associatedContact.setState(state);
        return associatedContact;
    }

    /**
     * Creates a valid AssociatedContactDto
     */
    public static com.automo.associatedContact.dto.AssociatedContactDto createValidAssociatedContactDto(Long identifierId, Long stateId) {
        return new com.automo.associatedContact.dto.AssociatedContactDto(
                identifierId,
                "912345678",
                stateId
        );
    }

    /**
     * Creates a valid AssociatedContactDto with custom contact
     */
    public static com.automo.associatedContact.dto.AssociatedContactDto createValidAssociatedContactDto(Long identifierId, String contact, Long stateId) {
        return new com.automo.associatedContact.dto.AssociatedContactDto(
                identifierId,
                contact,
                stateId
        );
    }

    /**
     * Creates a valid AssociatedEmail entity
     */
    public static com.automo.associatedEmail.entity.AssociatedEmail createValidAssociatedEmail(Identifier identifier, State state) {
        com.automo.associatedEmail.entity.AssociatedEmail associatedEmail = new com.automo.associatedEmail.entity.AssociatedEmail();
        associatedEmail.setIdentifier(identifier);
        associatedEmail.setEmail("test@automo.com");
        associatedEmail.setState(state);
        return associatedEmail;
    }

    /**
     * Creates a valid AssociatedEmail entity with custom email
     */
    public static com.automo.associatedEmail.entity.AssociatedEmail createValidAssociatedEmail(Identifier identifier, String email, State state) {
        com.automo.associatedEmail.entity.AssociatedEmail associatedEmail = new com.automo.associatedEmail.entity.AssociatedEmail();
        associatedEmail.setIdentifier(identifier);
        associatedEmail.setEmail(email);
        associatedEmail.setState(state);
        return associatedEmail;
    }

    /**
     * Creates a valid AssociatedEmailDto
     */
    public static com.automo.associatedEmail.dto.AssociatedEmailDto createValidAssociatedEmailDto(Long identifierId, Long stateId) {
        return new com.automo.associatedEmail.dto.AssociatedEmailDto(
                identifierId,
                "test@automo.com",
                stateId
        );
    }

    /**
     * Creates a valid AssociatedEmailDto with custom email
     */
    public static com.automo.associatedEmail.dto.AssociatedEmailDto createValidAssociatedEmailDto(Long identifierId, String email, Long stateId) {
        return new com.automo.associatedEmail.dto.AssociatedEmailDto(
                identifierId,
                email,
                stateId
        );
    }

    // =============================================================================
    // COMPLEX RELATIONSHIP SCENARIOS FOR TESTING
    // =============================================================================

    /**
     * Creates a complete Auth with multiple roles scenario for testing
     */
    public static Auth createAuthWithMultipleRoles(State activeState) {
        Auth auth = createValidAuth("multirole@automo.com");
        auth.setState(activeState);
        return auth;
    }

    /**
     * Creates a complete Agent with multiple areas and products scenario for testing
     */
    public static Agent createAgentWithMultipleAssignments(State activeState) {
        Agent agent = createValidAgent(activeState);
        agent.setName("Multi-Assignment Agent");
        agent.setDescription("Agent with multiple area and product assignments");
        return agent;
    }

    /**
     * Creates a Deal with multiple products scenario for testing
     */
    public static Deal createDealWithMultipleProducts(Identifier identifier, Lead lead, State activeState) {
        Deal deal = createValidDeal(identifier, lead, activeState);
        deal.setTotal(new BigDecimal("125000.00"));
        deal.setMessageCount(10);
        return deal;
    }

    /**
     * Creates multiple MessageCount records for the same lead (testing message tracking)
     */
    public static MessageCount createMessageCountForLead(Lead lead, Integer count, State state) {
        return createValidMessageCount(lead, count, state);
    }
}