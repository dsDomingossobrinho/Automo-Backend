# Integração SMS - Guia de Configuração

Este documento explica como configurar a integração de SMS com diferentes provedores.

## 🔧 **Twilio (Recomendado)**

### **1. Dependências (pom.xml)**
```xml
<dependency>
    <groupId>com.twilio.sdk</groupId>
    <artifactId>twilio</artifactId>
    <version>8.34.0</version>
</dependency>
```

### **2. Configuração (application.properties)**
```properties
# SMS Configuration - Twilio
sms.provider.enabled=true
sms.provider.name=twilio
sms.provider.account-sid=your-account-sid
sms.provider.auth-token=your-auth-token
sms.provider.phone-number=+1234567890
sms.provider.sender=AUTOMO
```

### **3. Implementação Real**
Substitua o método `sendSmsViaTwilio` em `SmsServiceImpl`:

```java
private void sendSmsViaTwilio(String phoneNumber, String message) {
    try {
        // Inicializar Twilio
        Twilio.init(accountSid, authToken);
        
        // Enviar SMS
        Message.creator(
            new PhoneNumber(phoneNumber),
            new PhoneNumber(twilioPhoneNumber),
            message
        ).create();
        
        log.info("SMS sent successfully via Twilio to: {}", phoneNumber);
    } catch (Exception e) {
        log.error("Failed to send SMS via Twilio to: {}", phoneNumber, e);
        throw new RuntimeException("Failed to send SMS via Twilio", e);
    }
}
```

## 📱 **AWS SNS**

### **1. Dependências (pom.xml)**
```xml
<dependency>
    <groupId>com.amazonaws</groupId>
    <artifactId>aws-java-sdk-sns</artifactId>
    <version>1.12.400</version>
</dependency>
```

### **2. Configuração (application.properties)**
```properties
# SMS Configuration - AWS SNS
sms.provider.enabled=true
sms.provider.name=aws-sns
aws.access-key-id=your-access-key
aws.secret-access-key=your-secret-key
aws.region=us-east-1
sms.provider.sender=AUTOMO
```

### **3. Implementação AWS SNS**
```java
@Service
public class AwsSnsService {
    
    @Value("${aws.access-key-id}")
    private String accessKey;
    
    @Value("${aws.secret-access-key}")
    private String secretKey;
    
    @Value("${aws.region}")
    private String region;
    
    private AmazonSNS snsClient;
    
    @PostConstruct
    public void init() {
        AWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);
        snsClient = AmazonSNSClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withRegion(region)
                .build();
    }
    
    public void sendSms(String phoneNumber, String message) {
        PublishRequest request = new PublishRequest()
                .withPhoneNumber(phoneNumber)
                .withMessage(message);
        
        snsClient.publish(request);
    }
}
```

## 🌍 **Vonage (Nexmo)**

### **1. Dependências (pom.xml)**
```xml
<dependency>
    <groupId>com.vonage</groupId>
    <artifactId>client</artifactId>
    <version>7.8.0</version>
</dependency>
```

### **2. Configuração (application.properties)**
```properties
# SMS Configuration - Vonage
sms.provider.enabled=true
sms.provider.name=vonage
vonage.api-key=your-api-key
vonage.api-secret=your-api-secret
sms.provider.sender=AUTOMO
```

### **3. Implementação Vonage**
```java
private void sendSmsViaVonage(String phoneNumber, String message) {
    try {
        VonageClient client = VonageClient.builder()
                .apiKey(vonageApiKey)
                .apiSecret(vonageApiSecret)
                .build();
        
        TextMessage textMessage = new TextMessage(senderName, phoneNumber, message);
        SmsSubmissionResponse response = client.getSmsClient().submitMessage(textMessage);
        
        log.info("SMS sent via Vonage to: {} with ID: {}", phoneNumber, response.getMessages().get(0).getId());
    } catch (Exception e) {
        log.error("Failed to send SMS via Vonage to: {}", phoneNumber, e);
        throw new RuntimeException("Failed to send SMS via Vonage", e);
    }
}
```

## 🔧 **Configuração Genérica**

### **SmsServiceImpl Atualizado**
```java
@Service
@RequiredArgsConstructor
@Slf4j
public class SmsServiceImpl implements SmsService {

    @Value("${sms.provider.enabled:false}")
    private boolean smsEnabled;
    
    @Value("${sms.provider.name:twilio}")
    private String providerName;
    
    // Twilio
    @Value("${sms.provider.account-sid:}")
    private String accountSid;
    
    @Value("${sms.provider.auth-token:}")
    private String authToken;
    
    @Value("${sms.provider.phone-number:}")
    private String twilioPhoneNumber;
    
    // AWS SNS
    @Value("${aws.access-key-id:}")
    private String awsAccessKey;
    
    @Value("${aws.secret-access-key:}")
    private String awsSecretKey;
    
    @Value("${aws.region:us-east-1}")
    private String awsRegion;
    
    @Value("${sms.provider.sender:AUTOMO}")
    private String senderName;

    @Override
    public void sendSimpleSms(String phoneNumber, String message) {
        if (!smsEnabled) {
            log.warn("SMS service is disabled. Would send to {}: {}", phoneNumber, message);
            return;
        }
        
        try {
            switch (providerName.toLowerCase()) {
                case "twilio":
                    sendSmsViaTwilio(phoneNumber, message);
                    break;
                case "aws-sns":
                    sendSmsViaAwsSns(phoneNumber, message);
                    break;
                case "vonage":
                    sendSmsViaVonage(phoneNumber, message);
                    break;
                default:
                    log.warn("Unknown SMS provider: {}. Using simulation mode.", providerName);
                    simulateSmsDelivery(phoneNumber, message);
            }
            
            log.info("SMS sent successfully to: {} via {}", phoneNumber, providerName);
        } catch (Exception e) {
            log.error("Failed to send SMS to: {} via {}", phoneNumber, providerName, e);
            throw new RuntimeException("Failed to send SMS", e);
        }
    }
    
    private void simulateSmsDelivery(String phoneNumber, String message) {
        log.info("SIMULATED SMS SEND:");
        log.info("Provider: {}", providerName);
        log.info("To: {}", phoneNumber);
        log.info("Message: {}", message);
        log.info("Sender: {}", senderName);
    }
}
```

## 📋 **Checklist de Configuração**

### **Twilio**
- [ ] Criar conta no Twilio
- [ ] Obter Account SID e Auth Token
- [ ] Comprar número de telefone
- [ ] Configurar webhook (opcional)
- [ ] Testar envio

### **AWS SNS**
- [ ] Criar conta AWS
- [ ] Configurar IAM user com permissões SNS
- [ ] Obter Access Key e Secret Key
- [ ] Configurar região
- [ ] Testar envio

### **Vonage**
- [ ] Criar conta no Vonage
- [ ] Obter API Key e Secret
- [ ] Configurar sender ID
- [ ] Testar envio

## 🧪 **Testes**

### **Teste de Integração**
```java
@Test
public void testSmsIntegration() {
    // Configurar provider
    smsService.sendSimpleSms("+244912345678", "Teste de integração SMS");
    
    // Verificar logs
    // Confirmar recebimento no telefone
}
```

### **Teste de Formato de Telefone**
```java
@Test
public void testPhoneFormats() {
    String[] validFormats = {
        "+244912345678",
        "+1234567890",
        "+351912345678"
    };
    
    for (String phone : validFormats) {
        assertTrue(ContactValidator.isPhone(phone));
    }
}
```

## 🚨 **Considerações de Produção**

1. **Custos**: SMS tem custo por mensagem
2. **Rate Limiting**: Implementar limitação de tentativas
3. **Logs**: Não logar números de telefone completos
4. **Compliance**: Verificar regulamentações locais
5. **Fallback**: Ter backup para quando SMS falhar
6. **Monitoramento**: Alertas para falhas de envio

## 📊 **Monitoramento**

```java
@Component
public class SmsMetrics {
    
    private final MeterRegistry meterRegistry;
    
    public void recordSmsSent(String provider) {
        Counter.builder("sms.sent")
                .tag("provider", provider)
                .register(meterRegistry)
                .increment();
    }
    
    public void recordSmsError(String provider, String error) {
        Counter.builder("sms.error")
                .tag("provider", provider)
                .tag("error", error)
                .register(meterRegistry)
                .increment();
    }
}
``` 