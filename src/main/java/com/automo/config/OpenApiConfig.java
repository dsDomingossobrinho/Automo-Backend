package com.automo.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Configuration
@Slf4j
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        try {
            log.info("Configurando OpenAPI customizado...");
            
            OpenAPI openAPI = new OpenAPI()
                    .info(new Info()
                            .title("Automo API")
                            .description("API de gerenciamento do sistema Automo")
                            .version("1.0.0")
                            .contact(new Contact()
                                    .name("Automo Team")
                                    .email("support@automo.com")
                                    .url("https://automo.com"))
                            .license(new License()
                                    .name("MIT License")
                                    .url("https://opensource.org/licenses/MIT")))
                    .tags(getTagsInAlphabeticalOrder());
            
            log.info("OpenAPI configurado com sucesso!");
            return openAPI;
            
        } catch (Exception e) {
            log.error("Erro ao configurar OpenAPI: {}", e.getMessage(), e);
            
            // Retornar configuração mínima em caso de erro
            return new OpenAPI()
                    .info(new Info()
                            .title("Automo API")
                            .description("API de gerenciamento do sistema Automo")
                            .version("1.0.0"));
        }
    }

    private List<Tag> getTagsInAlphabeticalOrder() {
        List<Tag> tags = new ArrayList<>();
        
        // Todas as tags em ordem alfabética pura
        tags.add(new Tag().name("Authentication").description("Authentication management APIs"));
        tags.add(new Tag().name("Account Types").description("Account type management APIs"));
        tags.add(new Tag().name("Admins").description("Admin management APIs"));
        tags.add(new Tag().name("Agent Areas").description("Agent Areas management endpoints"));
        tags.add(new Tag().name("Agent Products").description("Agent Product management APIs"));
        tags.add(new Tag().name("Agents").description("Agent management APIs"));
        tags.add(new Tag().name("Areas").description("Area management APIs"));
        tags.add(new Tag().name("Associated Contacts").description("Associated Contact management APIs"));
        tags.add(new Tag().name("Associated Emails").description("Associated Email management APIs"));
        tags.add(new Tag().name("Auth Roles").description("Auth Roles management APIs"));
        tags.add(new Tag().name("Countries").description("Country management APIs"));
        tags.add(new Tag().name("Deal Products").description("Deal Product management APIs"));
        tags.add(new Tag().name("Deals").description("Deal management APIs"));
        tags.add(new Tag().name("Identifiers").description("Identifier management APIs"));
        tags.add(new Tag().name("Identifier Types").description("Identifier type management APIs"));
        tags.add(new Tag().name("Lead Types").description("Lead type management APIs"));
        tags.add(new Tag().name("Leads").description("Lead management APIs"));
        tags.add(new Tag().name("Message Counts").description("Message count management APIs"));
        tags.add(new Tag().name("Notification Types").description("Notification type management APIs"));
        tags.add(new Tag().name("Notifications").description("Notification management APIs"));
        tags.add(new Tag().name("Organization Types").description("Organization type management APIs"));
        tags.add(new Tag().name("Payment Types").description("Payment type management APIs"));
        tags.add(new Tag().name("Payments").description("Payment management APIs"));
        tags.add(new Tag().name("Product Categories").description("Product category management APIs"));
        tags.add(new Tag().name("Products").description("Product management APIs"));
        tags.add(new Tag().name("Promotions").description("Promotion management APIs"));
        tags.add(new Tag().name("Provinces").description("Province management APIs"));
        tags.add(new Tag().name("Roles").description("Role management APIs"));
        tags.add(new Tag().name("States").description("State management APIs"));
        tags.add(new Tag().name("Subscription Plans").description("Subscription plan management APIs"));
        tags.add(new Tag().name("Subscriptions").description("Subscription management APIs"));
        tags.add(new Tag().name("Test").description("Test endpoints to verify SpringDoc functionality"));
        tags.add(new Tag().name("Users").description("User management APIs"));
        
        return tags;
    }
}
