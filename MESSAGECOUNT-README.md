# MessageCount - Contagem de Mensagens para o Negócio

## 📋 **Descrição**

A entidade `MessageCount` é responsável por gerenciar a contagem de mensagens associadas a cada lead/negócio. Esta entidade permite rastrear quantas mensagens foram trocadas com cada lead, facilitando o controle de comunicação e follow-up.

## 🏗️ **Estrutura da Entidade**

### **Campos Principais:**
- **`id`**: Identificador único (herdado de `AbstractModel`)
- **`lead`**: Relacionamento com a entidade `Lead` (obrigatório)
- **`messageCount`**: Quantidade de mensagens (obrigatório, sempre positivo)
- **`state`**: Estado da contagem (obrigatório)
- **`createdAt`**: Data de criação (herdado de `AbstractModel`)
- **`updatedAt`**: Data de atualização (herdado de `AbstractModel`)

### **Relacionamentos:**
- **`@ManyToOne`** com `Lead`: Cada contagem está associada a um lead específico
- **`@ManyToOne`** com `State`: Controle de estado (ACTIVE, INACTIVE, etc.)

## 🔧 **Arquivos Criados**

### **1. Entity (`MessageCount.java`)**
```java
@Entity
@Table(name = "message_counts")
public class MessageCount extends AbstractModel {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lead_id", nullable = false)
    private Lead lead;
    
    @Column(name = "message_count", nullable = false)
    @Positive(message = "Message count must be positive")
    private Integer messageCount;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "state_id", nullable = false)
    private State state;
}
```

### **2. DTO (`MessageCountDto.java`)**
```java
public record MessageCountDto(
    @NotNull(message = "ID do lead é obrigatório")
    Long leadId,
    
    @NotNull(message = "Quantidade de mensagens é obrigatória")
    @Positive(message = "Quantidade de mensagens deve ser positiva")
    Integer messageCount,
    
    @NotNull(message = "ID do estado é obrigatório")
    Long stateId
) {}
```

### **3. Response (`MessageCountResponse.java`)**
```java
public record MessageCountResponse(
    Long id,
    Long leadId,
    String leadName,
    Integer messageCount,
    Long stateId,
    String stateName,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}
```

### **4. Repository (`MessageCountRepository.java`)**
```java
@Repository
public interface MessageCountRepository extends JpaRepository<MessageCount, Long> {
    List<MessageCount> findByLeadId(Long leadId);
    List<MessageCount> findByStateId(Long stateId);
    List<MessageCount> findByLeadIdAndStateId(Long leadId, Long stateId);
}
```

### **5. Service (`MessageCountService.java` & `MessageCountServiceImpl.java`)**
- **CRUD completo**: Create, Read, Update, Delete
- **Filtros por Lead**: Buscar contagens por lead específico
- **Filtros por Estado**: Buscar contagens por estado
- **Validações**: Verificação de existência de Lead e State

### **6. Controller (`MessageCountController.java`)**
- **Endpoints REST**: GET, POST, PUT, DELETE
- **Filtros**: Por lead e por estado
- **Documentação Swagger**: Anotações OpenAPI completas

## 📊 **Estrutura da Tabela**

```sql
CREATE TABLE message_counts (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    lead_id BIGINT NOT NULL,
    message_count INTEGER NOT NULL CHECK (message_count > 0),
    state_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (lead_id) REFERENCES leads(id),
    FOREIGN KEY (state_id) REFERENCES states(id)
);

-- Índices para performance
CREATE INDEX idx_message_counts_lead_id ON message_counts(lead_id);
CREATE INDEX idx_message_counts_state_id ON message_counts(state_id);
```

## 🚀 **Endpoints da API**

### **CRUD Básico:**
- **`GET /message-counts`** - Listar todas as contagens
- **`GET /message-counts/{id}`** - Buscar contagem por ID
- **`POST /message-counts`** - Criar nova contagem
- **`PUT /message-counts/{id}`** - Atualizar contagem existente
- **`DELETE /message-counts/{id}`** - Excluir contagem

### **Filtros Específicos:**
- **`GET /message-counts/lead/{leadId}`** - Contagens por lead específico
- **`GET /message-counts/state/{stateId}`** - Contagens por estado específico

## 📝 **Exemplos de Uso**

### **1. Criar Contagem de Mensagens:**
```json
POST /message-counts
{
    "leadId": 1,
    "messageCount": 15,
    "stateId": 1
}
```

**Resposta:**
```json
{
    "id": 1,
    "leadId": 1,
    "leadName": "João Silva",
    "messageCount": 15,
    "stateId": 1,
    "stateName": "ACTIVE",
    "createdAt": "2024-01-15T10:30:00",
    "updatedAt": "2024-01-15T10:30:00"
}
```

### **2. Buscar Contagens por Lead:**
```json
GET /message-counts/lead/1
```

**Resposta:**
```json
[
    {
        "id": 1,
        "leadId": 1,
        "leadName": "João Silva",
        "messageCount": 15,
        "stateId": 1,
        "stateName": "ACTIVE",
        "createdAt": "2024-01-15T10:30:00",
        "updatedAt": "2024-01-15T10:30:00"
    }
]
```

### **3. Atualizar Contagem:**
```json
PUT /message-counts/1
{
    "leadId": 1,
    "messageCount": 20,
    "stateId": 1
}
```

## 🔍 **Casos de Uso**

### **1. Rastreamento de Comunicação:**
- Contar mensagens trocadas com cada lead
- Identificar leads com mais interação
- Acompanhar evolução da comunicação

### **2. Relatórios de Performance:**
- Leads com maior engajamento
- Eficiência da comunicação por vendedor
- Análise de conversão por volume de mensagens

### **3. Gestão de Follow-up:**
- Identificar leads que precisam de mais atenção
- Planejar estratégias de comunicação
- Otimizar recursos de vendas

## ⚠️ **Validações e Regras de Negócio**

### **Validações de Entrada:**
- **`leadId`**: Obrigatório, deve existir na tabela `leads`
- **`messageCount`**: Obrigatório, deve ser positivo (> 0)
- **`stateId`**: Obrigatório, deve existir na tabela `states`

### **Regras de Negócio:**
- Uma contagem sempre está associada a um lead
- A quantidade de mensagens nunca pode ser negativa
- Cada contagem tem um estado para controle de ciclo de vida

## 🔗 **Relacionamentos**

### **Com Lead:**
- **Tipo**: Many-to-One
- **Cardinalidade**: Muitas contagens para um lead
- **Uso**: Rastrear comunicação com leads específicos

### **Com State:**
- **Tipo**: Many-to-One
- **Cardinalidade**: Muitas contagens para um estado
- **Uso**: Controle de ciclo de vida das contagens

## 📈 **Benefícios da Implementação**

1. **Rastreabilidade**: Controle completo da comunicação com leads
2. **Análise de Dados**: Insights sobre engajamento e performance
3. **Gestão de Vendas**: Otimização de estratégias de follow-up
4. **Relatórios**: Dados estruturados para tomada de decisão
5. **Integração**: Sistema completo com outras entidades do negócio

## 🎯 **Próximos Passos Sugeridos**

1. **Integração com Sistema de Mensagens**: Conectar com entidade de mensagens reais
2. **Automatização**: Incrementar contador automaticamente
3. **Dashboard**: Interface para visualização de métricas
4. **Alertas**: Notificações para leads com baixo engajamento
5. **Analytics**: Relatórios avançados de performance

---

**Entidade MessageCount criada com sucesso!** 🎉

Agora você tem um sistema completo para rastrear e gerenciar a contagem de mensagens por lead/negócio. 