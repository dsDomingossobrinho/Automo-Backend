package com.automo.test.config;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.lang.annotation.*;

/**
 * Anotação base para configurar testes unitários e de integração
 * no projeto Automo. Configura automaticamente:
 * 
 * - Profile de teste
 * - Base de dados H2 em memória
 * - Transações com rollback
 * - Propriedades de teste
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.properties")
@Transactional
public @interface BaseTestConfig {
    /**
     * Define se o contexto web deve ser carregado
     */
    SpringBootTest.WebEnvironment webEnvironment() default SpringBootTest.WebEnvironment.MOCK;
}