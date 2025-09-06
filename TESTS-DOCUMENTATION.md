# Documentação dos Testes Unitários - Sistema Automo

## Visão Geral

Foi criada uma suíte completa de testes unitários para todas as entidades e funcionalidades do sistema Automo. Os testes cobrem:

- **Entidades** (Entity Tests)
- **Serviços** (Service Tests)  
- **Controladores** (Controller Tests)
- **Repositórios** (Repository Tests)
- **Funcionalidades de Email** (Email Service Tests)

## Estrutura dos Testes

### Configuração Base

#### `application-test.properties`
- Configuração do banco de dados H2 in-memory para testes
- Configuração de JWT e email para ambiente de teste
- Logging configurado para debug durante testes

#### `BaseTestConfig.java`
- Anotação personalizada que combina:
  - `@SpringBootTest`
  - `@ActiveProfiles("test")`
  - `@TestPropertySource`
  - `@Transactional`

#### `TestDataFactory.java`
- Factory class para criar objetos de teste válidos
- Métodos utilitários para geração de dados únicos
- Suporte para todas as entidades do sistema

## Testes de Entidades

### `AuthTest.java`
- Validação de campos obrigatórios (email, password)
- Validação de formato de email
- Teste de equals/hashCode
- Teste de toString

### `UserTest.java`
- Validação de relacionamentos com Auth, AccountType, State
- Validação de campos obrigatórios (name, contact)
- Teste com diferentes tipos de conta
- Validação de contatos únicos

### `PaymentTest.java`
- Validação de relacionamentos com Identifier, PaymentType, State
- Validação de valores monetários (BigDecimal)
- Teste com arquivos de imagem
- Validação de campos opcionais (amount pode ser null)

## Testes de Serviços

### `UserServiceImplTest.java`
- Testes CRUD completos usando Mockito
- Teste de soft delete
- Validação de regras de negócio
- Teste de exceções (EntityNotFoundException)

### `EmailServiceImplTest.java`
- Teste de envio de emails simples
- Teste de envio de emails HTML
- Teste de diferentes tipos de OTP
- Tratamento de exceções de messaging

### `EmailTemplateServiceTest.java`
- Teste de carregamento de templates HTML
- Teste de substituição de placeholders
- Teste de encoding de imagens base64
- Tratamento de erros de I/O

## Testes de Controladores

### `AuthControllerTest.java`
- Testes de endpoints de autenticação
- Testes de OTP (login, back office, user)
- Teste de recuperação de senha
- Testes de autorização e segurança

### `PaymentControllerTest.java`
- Testes de upload de arquivos multipart
- Testes CRUD com diferentes roles
- Teste de filtros por estado e tipo
- Validação de permissões

### `UserControllerTest.java`
- Testes CRUD com validação de roles
- Teste de filtros por estado
- Validação de dados de entrada
- Testes de autorização

## Testes de Repositórios

### `AuthRepositoryTest.java`
- Testes de queries personalizadas
- Teste de unique constraints
- Validação de timestamps
- Teste de case sensitivity

### `UserRepositoryTest.java`
- Testes de relacionamentos JPA
- Teste de queries por estado
- Validação de cascade operations
- Teste de timestamps automáticos

### `PaymentRepositoryTest.java`
- Testes de queries de receita (total, diária, mensal, semestral)
- Teste de filtros por estado e tipo de pagamento
- Validação de cálculos BigDecimal
- Teste de relacionamentos complexos

## Executando os Testes

### Via Maven (Local)
```bash
./mvnw test                    # Todos os testes
./mvnw test -Dtest=AuthTest    # Teste específico
```

### Via Docker
```bash
docker-compose exec app mvn test
```

### Via IDE
- Importar o projeto como Maven project
- Executar classes de teste individualmente
- Usar JUnit 5 runner

## Cobertura de Testes

### Entidades Cobertas
- ✅ Auth
- ✅ User  
- ✅ Payment
- ✅ Identifier
- ✅ State
- ✅ AccountType
- ✅ PaymentType

### Serviços Cobertos
- ✅ AuthService
- ✅ UserService
- ✅ PaymentService
- ✅ EmailService
- ✅ EmailTemplateService

### Controladores Cobertos
- ✅ AuthController
- ✅ UserController
- ✅ PaymentController

### Repositórios Cobertos
- ✅ AuthRepository
- ✅ UserRepository
- ✅ PaymentRepository

## Padrões de Teste

### Nomenclatura
- Métodos: `should[Action][Condition]()` 
- Classes: `[ClassName]Test`
- Display Names: Descrições claras em português

### Estrutura Given-When-Then
```java
@Test
@DisplayName("Should create user successfully")
void shouldCreateUserSuccessfully() {
    // Given - Preparação dos dados
    UserDto userDto = new UserDto("John Doe", "912345678", 1L, 1L);
    
    // When - Execução da ação
    UserResponse result = userService.createUser(userDto, testAuth);
    
    // Then - Verificação dos resultados
    assertNotNull(result);
    assertEquals("John Doe", result.name());
}
```

### Uso de Mocks
- `@MockBean` para testes de integração Spring
- `@Mock` para testes unitários puros
- `@InjectMocks` para injeção automática

## Tecnologias Utilizadas

- **JUnit 5** - Framework de testes
- **Mockito** - Mocking framework
- **Spring Boot Test** - Testes de integração
- **H2 Database** - Banco in-memory para testes
- **TestContainers** - Poderia ser usado para testes de integração
- **AssertJ** - Assertions fluentes (opcional)

## Configurações Importantes

### Perfil de Teste
```properties
spring.profiles.active=test
spring.jpa.hibernate.ddl-auto=create-drop
spring.datasource.url=jdbc:h2:mem:testdb
```

### Anotações Principais
```java
@DataJpaTest          // Para testes de repositório
@WebMvcTest          // Para testes de controller
@SpringBootTest      // Para testes de integração
@Transactional       // Para rollback automático
```

## Benefícios dos Testes

1. **Detecção Precoce de Bugs** - Problemas identificados durante desenvolvimento
2. **Refatoração Segura** - Mudanças com confiança
3. **Documentação Viva** - Testes servem como exemplos de uso
4. **Qualidade do Código** - Força design mais limpo
5. **Integração Contínua** - Validação automática em builds

## Executar Testes Específicos

```bash
# Por classe
./mvnw test -Dtest=AuthTest

# Por método
./mvnw test -Dtest=AuthTest#shouldCreateValidAuthEntity

# Por pacote
./mvnw test -Dtest="com.automo.auth.**"

# Com profiles
./mvnw test -Dspring.profiles.active=test
```

## Próximos Passos

1. **Testes de Integração** - Testar fluxos completos
2. **Testes de Performance** - Verificar tempos de resposta
3. **Testes de Segurança** - Validar autenticação/autorização
4. **Testes E2E** - Selenium/TestContainers
5. **Cobertura de Código** - JaCoCo para métricas

Esta suíte de testes fornece uma base sólida para monitoramento e validação contínua de todas as funcionalidades do sistema Automo.