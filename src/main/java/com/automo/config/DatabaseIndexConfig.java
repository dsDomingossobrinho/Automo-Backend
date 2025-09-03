package com.automo.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Statement;

/**
 * Configuração para criação de índices de performance no banco de dados
 * Executa apenas uma vez na inicialização da aplicação
 */
@Configuration
public class DatabaseIndexConfig {

    @Bean
    @Order(1000) // Executa após outras inicializações
    @SuppressWarnings("unused") // args parameter required by CommandLineRunner interface
    CommandLineRunner createDatabaseIndexes(DataSource dataSource) {
        return args -> { // args parameter required by CommandLineRunner interface
            try (Connection connection = dataSource.getConnection();
                 Statement statement = connection.createStatement()) {
                
                // Índices para tabela Auth (mais crítica para performance)
                createIndexIfNotExists(statement, "idx_auth_email", "auth", "email");
                createIndexIfNotExists(statement, "idx_auth_username", "auth", "username");
                createIndexIfNotExists(statement, "idx_auth_state_id", "auth", "state_id");
                createIndexIfNotExists(statement, "idx_auth_account_type_id", "auth", "account_type_id");
                
                // Índices para tabela Users
                createIndexIfNotExists(statement, "idx_users_email", "users", "email");
                createIndexIfNotExists(statement, "idx_users_auth_id", "users", "auth_id");
                createIndexIfNotExists(statement, "idx_users_state_id", "users", "state_id");
                createIndexIfNotExists(statement, "idx_users_country_id", "users", "country_id");
                
                // Índices para tabela Admins
                createIndexIfNotExists(statement, "idx_admins_email", "admins", "email");
                createIndexIfNotExists(statement, "idx_admins_auth_id", "admins", "auth_id");
                createIndexIfNotExists(statement, "idx_admins_state_id", "admins", "state_id");
                
                // Índices para tabela AuthRoles (queries frequentes)
                createIndexIfNotExists(statement, "idx_auth_roles_auth_id", "auth_roles", "auth_id");
                createIndexIfNotExists(statement, "idx_auth_roles_role_id", "auth_roles", "role_id");
                createIndexIfNotExists(statement, "idx_auth_roles_state_id", "auth_roles", "state_id");
                createCompositeIndexIfNotExists(statement, "idx_auth_roles_auth_role", "auth_roles", "auth_id, role_id");
                
                // Índices para tabela States (lookups frequentes)
                createIndexIfNotExists(statement, "idx_states_state", "states", "state");
                
                // Índices para tabela Products
                createIndexIfNotExists(statement, "idx_products_state_id", "products", "state_id");
                createIndexIfNotExists(statement, "idx_products_category_id", "product_category_id", "product_category_id");
                
                // Índices para tabela Leads (alta volumetria)
                createIndexIfNotExists(statement, "idx_leads_state_id", "leads", "state_id");
                createIndexIfNotExists(statement, "idx_leads_agent_id", "leads", "agent_id");
                createIndexIfNotExists(statement, "idx_leads_user_id", "leads", "user_id");
                createIndexIfNotExists(statement, "idx_leads_created_at", "leads", "created_at");
                
                // Índices para tabela Deals
                createIndexIfNotExists(statement, "idx_deals_state_id", "deals", "state_id");
                createIndexIfNotExists(statement, "idx_deals_agent_id", "deals", "agent_id");
                createIndexIfNotExists(statement, "idx_deals_user_id", "deals", "user_id");
                createIndexIfNotExists(statement, "idx_deals_created_at", "deals", "created_at");
                
                // Índices para tabela Payments
                createIndexIfNotExists(statement, "idx_payments_state_id", "payments", "state_id");
                createIndexIfNotExists(statement, "idx_payments_user_id", "payments", "user_id");
                createIndexIfNotExists(statement, "idx_payments_created_at", "payments", "created_at");
                
                // Índices para tabela Notifications
                createIndexIfNotExists(statement, "idx_notifications_user_id", "notifications", "user_id");
                createIndexIfNotExists(statement, "idx_notifications_state_id", "notifications", "state_id");
                createIndexIfNotExists(statement, "idx_notifications_created_at", "notifications", "created_at");
                
                // Índices compostos para queries complexas
                createCompositeIndexIfNotExists(statement, "idx_auth_email_state", "auth", "email, state_id");
                createCompositeIndexIfNotExists(statement, "idx_users_state_country", "users", "state_id, country_id");
                createCompositeIndexIfNotExists(statement, "idx_leads_agent_state_date", "leads", "agent_id, state_id, created_at");
                
                System.out.println("✅ Database performance indexes created successfully");
                
            } catch (Exception e) {
                System.err.println("❌ Error creating database indexes: " + e.getMessage());
                // Não falha a inicialização se os índices não puderem ser criados
                e.printStackTrace();
            }
        };
    }
    
    private void createIndexIfNotExists(Statement statement, String indexName, String tableName, String columnName) {
        try {
            String sql = String.format(
                "CREATE INDEX IF NOT EXISTS %s ON %s (%s)",
                indexName, tableName, columnName
            );
            statement.execute(sql);
            System.out.println("Index created: " + indexName);
        } catch (Exception e) {
            System.err.println("Failed to create index " + indexName + ": " + e.getMessage());
        }
    }
    
    private void createCompositeIndexIfNotExists(Statement statement, String indexName, String tableName, String columns) {
        try {
            String sql = String.format(
                "CREATE INDEX IF NOT EXISTS %s ON %s (%s)",
                indexName, tableName, columns
            );
            statement.execute(sql);
            System.out.println("Composite index created: " + indexName);
        } catch (Exception e) {
            System.err.println("Failed to create composite index " + indexName + ": " + e.getMessage());
        }
    }
}