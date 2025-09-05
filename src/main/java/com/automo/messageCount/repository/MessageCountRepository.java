package com.automo.messageCount.repository;

import com.automo.messageCount.entity.MessageCount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageCountRepository extends JpaRepository<MessageCount, Long> {
    
    List<MessageCount> findByLeadId(Long leadId);
    
    List<MessageCount> findByStateId(Long stateId);
    
    List<MessageCount> findByLeadIdAndStateId(Long leadId, Long stateId);
    
    // === MÉTODOS DE ESTATÍSTICAS ===
    
    /**
     * Conta o total de mensagens enviadas globalmente
     */
    @Query("SELECT COALESCE(SUM(mc.messageCount), 0) FROM MessageCount mc WHERE mc.state.state != 'ELIMINATED'")
    long countTotalMessagesGlobal();
    
    /**
     * Busca contagem de mensagens agrupada por usuário (através do lead)
     */
    @Query("""
        SELECT new com.automo.auth.dto.UserStatisticsResponse$UserMessageCount(
            l.identifier.userId,
            u.name,
            u.email,
            COALESCE(SUM(mc.messageCount), 0)
        )
        FROM MessageCount mc 
        JOIN mc.lead l 
        JOIN User u ON u.id = l.identifier.userId
        WHERE mc.state.state != 'ELIMINATED' 
        AND l.state.state != 'ELIMINATED'
        GROUP BY l.identifier.userId, u.name, u.email
        ORDER BY SUM(mc.messageCount) DESC
        """)
    List<com.automo.auth.dto.UserStatisticsResponse.UserMessageCount> findMessageCountByUser();
    
    // === MÉTODOS DE ESTATÍSTICAS DE AGENTE ===
    
    /**
     * Calcula a média de mensagens para captação de leads por agente
     */
    @Query("SELECT AVG(mc.messageCount) FROM MessageCount mc JOIN mc.lead l WHERE l.identifier.userId = :agentId AND mc.state.state != 'ELIMINATED'")
    Double calculateAverageMessagesPerLeadByAgent(@Param("agentId") Long agentId);
    
    /**
     * Calcula a média global de mensagens para captação de leads
     */
    @Query("SELECT AVG(mc.messageCount) FROM MessageCount mc WHERE mc.state.state != 'ELIMINATED'")
    Double calculateGlobalAverageMessagesPerLead();
    
    /**
     * Conta o total de mensagens ativas por agente
     */
    @Query("SELECT COALESCE(SUM(mc.messageCount), 0) FROM MessageCount mc JOIN mc.lead l WHERE l.identifier.userId = :agentId AND mc.state.state != 'ELIMINATED'")
    long countTotalMessagesByAgent(@Param("agentId") Long agentId);
} 