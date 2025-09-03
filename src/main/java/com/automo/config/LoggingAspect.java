package com.automo.config;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * Aspect para logging automático de métodos de serviço
 * Fornece logging consistente em toda a aplicação
 */
@Aspect
@Component
@Slf4j
public class LoggingAspect {

    /**
     * Pointcut para todos os métodos públicos dos services
     */
    @Pointcut("execution(public * com.automo.*.service.*ServiceImpl.*(..))")
    public void serviceLayer() {}

    /**
     * Pointcut para métodos de criação
     */
    @Pointcut("execution(* com.automo.*.service.*ServiceImpl.create*(..))")
    public void createMethods() {}

    /**
     * Pointcut para métodos de atualização
     */
    @Pointcut("execution(* com.automo.*.service.*ServiceImpl.update*(..))")
    public void updateMethods() {}

    /**
     * Pointcut para métodos de deleção
     */
    @Pointcut("execution(* com.automo.*.service.*ServiceImpl.delete*(..))")
    public void deleteMethods() {}

    /**
     * Pointcut para métodos de busca
     */
    @Pointcut("execution(* com.automo.*.service.*ServiceImpl.find*(..) || " +
              "execution(* com.automo.*.service.*ServiceImpl.get*(..))")
    public void findMethods() {}

    /**
     * Logging around service methods com tempo de execução
     */
    @Around("serviceLayer()")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        String className = joinPoint.getSignature().getDeclaringTypeName();
        String methodName = joinPoint.getSignature().getName();
        String fullMethodName = className + "." + methodName;

        try {
            log.debug("🔄 Executing: {} with args: {}", fullMethodName, Arrays.toString(joinPoint.getArgs()));
            
            Object result = joinPoint.proceed();
            
            long executionTime = System.currentTimeMillis() - startTime;
            
            if (executionTime > 1000) {
                log.warn("⚠️ Slow execution: {} took {}ms", fullMethodName, executionTime);
            } else {
                log.debug("✅ Completed: {} in {}ms", fullMethodName, executionTime);
            }
            
            return result;
            
        } catch (Exception ex) {
            long executionTime = System.currentTimeMillis() - startTime;
            log.error("❌ Failed: {} after {}ms. Error: {}", fullMethodName, executionTime, ex.getMessage(), ex);
            throw ex;
        }
    }

    /**
     * Logging específico para métodos de criação
     */
    @AfterReturning(pointcut = "createMethods()", returning = "result")
    public void logAfterCreate(JoinPoint joinPoint, Object result) {
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getSignature().getDeclaringTypeName().substring(
                joinPoint.getSignature().getDeclaringTypeName().lastIndexOf('.') + 1);
        
        log.info("➕ CREATED: {} executed successfully by {}", methodName, className);
        
        // Log do ID se o resultado tem método getId()
        try {
            if (result != null && result.getClass().getMethod("getId") != null) {
                Object id = result.getClass().getMethod("getId").invoke(result);
                log.info("📝 Entity created with ID: {}", id);
            }
        } catch (Exception e) {
            // Ignore if getId() method doesn't exist
        }
    }

    /**
     * Logging específico para métodos de atualização
     */
    @AfterReturning(pointcut = "updateMethods()", returning = "result")
    public void logAfterUpdate(JoinPoint joinPoint, Object result) {
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getSignature().getDeclaringTypeName().substring(
                joinPoint.getSignature().getDeclaringTypeName().lastIndexOf('.') + 1);
        
        Object[] args = joinPoint.getArgs();
        Object entityId = args.length > 0 ? args[0] : "unknown";
        
        log.info("✏️ UPDATED: {} executed successfully by {} for entity ID: {}", methodName, className, entityId);
    }

    /**
     * Logging específico para métodos de deleção
     */
    @AfterReturning(pointcut = "deleteMethods()")
    public void logAfterDelete(JoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getSignature().getDeclaringTypeName().substring(
                joinPoint.getSignature().getDeclaringTypeName().lastIndexOf('.') + 1);
        
        Object[] args = joinPoint.getArgs();
        Object entityId = args.length > 0 ? args[0] : "unknown";
        
        log.info("🗑️ DELETED: {} executed successfully by {} for entity ID: {}", methodName, className, entityId);
    }

    /**
     * Logging para queries lentas em métodos de busca
     */
    @Around("findMethods()")
    public Object logSlowQueries(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getSignature().getDeclaringTypeName().substring(
                joinPoint.getSignature().getDeclaringTypeName().lastIndexOf('.') + 1);

        try {
            Object result = joinPoint.proceed();
            long executionTime = System.currentTimeMillis() - startTime;
            
            // Log queries que demoram mais de 500ms como lentas
            if (executionTime > 500) {
                log.warn("🐌 SLOW QUERY: {}.{} took {}ms with args: {}", 
                        className, methodName, executionTime, Arrays.toString(joinPoint.getArgs()));
            }
            
            return result;
            
        } catch (Exception ex) {
            long executionTime = System.currentTimeMillis() - startTime;
            log.error("❌ QUERY FAILED: {}.{} failed after {}ms. Error: {}", 
                    className, methodName, executionTime, ex.getMessage());
            throw ex;
        }
    }

    /**
     * Logging de exceções em services
     */
    @AfterThrowing(pointcut = "serviceLayer()", throwing = "ex")
    public void logServiceExceptions(JoinPoint joinPoint, Throwable ex) {
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getSignature().getDeclaringTypeName().substring(
                joinPoint.getSignature().getDeclaringTypeName().lastIndexOf('.') + 1);
        
        log.error("💥 EXCEPTION in {}.{}: {} | Args: {}", 
                className, methodName, ex.getMessage(), Arrays.toString(joinPoint.getArgs()));
    }
}