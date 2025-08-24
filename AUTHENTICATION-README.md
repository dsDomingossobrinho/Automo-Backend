# Sistema de Autenticação com OTP - Automo Backend

Este projeto implementa um sistema de autenticação em duas etapas usando códigos OTP (One-Time Password) enviados por **email** ou **SMS**, dependendo do tipo de contato fornecido.

## 🔐 **Fluxo de Autenticação**

### **1. Solicitação de OTP**
```
POST /auth/login/request-otp
{
    "emailOrContact": "user@example.com",  // ou "+244912345678"
    "password": "userpassword"
}
```

**Resposta (Email):**
```json
{
    "token": null,
    "message": "OTP sent to your email. Please check and enter the code.",
    "requiresOtp": true
}
```

**Resposta (SMS):**
```json
{
    "token": null,
    "message": "OTP sent to your phone. Please check and enter the code.",
    "requiresOtp": true
}
```

### **2. Verificação de OTP**
```
POST /auth/login/verify-otp
{
    "contact": "user@example.com",  // ou "+244912345678"
    "otpCode": "123456"
}
```

**Resposta:**
```json
{
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "message": "Authentication successful",
    "requiresOtp": false
}
```

## 📧 **Endpoints Disponíveis**

### **Autenticação Geral**
- `POST /auth/login/request-otp` - Solicitar OTP
- `POST /auth/login/verify-otp` - Verificar OTP e autenticar

### **Back Office (account_type_id = 1)**
- `POST /auth/login/backoffice/request-otp` - Solicitar OTP para back office
- `POST /auth/login/backoffice/verify-otp` - Verificar OTP e autenticar back office

### **Usuários Regulares (account_type_id = 2)**
- `POST /auth/login/user/request-otp` - Solicitar OTP para usuário regular
- `POST /auth/login/user/verify-otp` - Verificar OTP e autenticar usuário regular

### **Registro**
- `POST /auth/register` - Registrar novo usuário (gera token diretamente)

## ⚙️ **Configurações**

### **Email (application.properties)**
```properties
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=automo@gmail.com
spring.mail.password=your-app-password
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
spring.mail.properties.mail.smtp.starttls.required=true
```

### **SMS (application.properties)**
```properties
sms.provider.enabled=false  # true para ativar SMS
sms.provider.api-key=your-twilio-api-key
sms.provider.sender=AUTOMO
```

### **OTP Settings**
- **Tamanho**: 6 dígitos
- **Expiração**: 5 minutos
- **Limpeza**: Automática a cada 5 minutos
- **Detecção automática**: Email vs Telefone

## 🔧 **Componentes do Sistema**

### **1. Entidade OTP**
```java
@Entity
@Table(name = "otps")
public class Otp extends AbstractModel {
    private String contact;     // Email ou telefone
    private String contactType; // EMAIL ou PHONE
    private String otpCode;
    private LocalDateTime expiresAt;
    private boolean used;
    private String purpose;
}
```

### **2. Detector de Contato**
- **Validação automática** de email vs telefone
- **Regex patterns** para detecção precisa
- **Suporte a formatos**: +244912345678, (244) 912-345-678, etc.

### **3. Serviço de Email**
- Envio de emails com códigos OTP
- Templates personalizados para diferentes propósitos
- Logs de envio e erros

### **4. Serviço de SMS**
- Envio de SMS com códigos OTP
- Integração com provedores (Twilio, AWS SNS, etc.)
- Modo simulado para desenvolvimento

### **5. Serviço de OTP**
- Geração de códigos aleatórios
- Validação de códigos
- **Detecção automática** do tipo de contato
- Limpeza automática de códigos expirados
- Prevenção de reutilização

## 🚀 **Como Usar**

### **Exemplo de Autenticação Completa**

```bash
# 1. Solicitar OTP (Email)
curl -X POST http://localhost:8080/auth/login/request-otp \
  -H "Content-Type: application/json" \
  -d '{
    "emailOrContact": "user@example.com",
    "password": "userpassword"
  }'

# 1. Solicitar OTP (Telefone)
curl -X POST http://localhost:8080/auth/login/request-otp \
  -H "Content-Type: application/json" \
  -d '{
    "emailOrContact": "+244912345678",
    "password": "userpassword"
  }'

# 2. Verificar OTP (código recebido por email ou SMS)
curl -X POST http://localhost:8080/auth/login/verify-otp \
  -H "Content-Type: application/json" \
  -d '{
    "contact": "user@example.com",
    "otpCode": "123456"
  }'
```

## 🔒 **Segurança**

- **OTPs únicos**: Cada código só pode ser usado uma vez
- **Expiração**: Códigos expiram em 5 minutos
- **Limpeza automática**: Códigos expirados são removidos automaticamente
- **Validação de propósito**: Cada OTP tem um propósito específico (LOGIN, LOGIN_BACKOFFICE, LOGIN_USER)
- **Detecção automática**: Sistema identifica automaticamente email vs telefone
- **Múltiplos canais**: Email e SMS para maior segurança

## 📝 **Logs**

O sistema registra todas as operações:
- Geração e envio de OTPs
- Tentativas de verificação
- Limpeza de códigos expirados
- Erros de envio de email

## ⚠️ **Importante**

1. **Configure o email** antes de usar o sistema
2. **Configure o SMS** se quiser suporte a telefones
3. **Altere as credenciais** de email/SMS para produção
4. **Monitore os logs** para identificar problemas
5. **Teste ambos os fluxos** (email e SMS) antes de colocar em produção

## 🔍 **Troubleshooting**

### **OTP não recebido**
- Verifique as configurações de email/SMS
- Confirme se o contato está correto
- Verifique logs de erro
- Para SMS: confirme se `sms.provider.enabled=true`

### **OTP inválido**
- Código expirou (5 minutos)
- Código já foi usado
- Contato incorreto na verificação
- Formato de telefone inválido

### **Erro de autenticação**
- Verifique se o usuário existe
- Confirme se a senha está correta
- Verifique se o tipo de conta está correto 