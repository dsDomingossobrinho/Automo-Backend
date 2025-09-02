package com.automo.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public CacheManager cacheManager() {
        ConcurrentMapCacheManager cacheManager = new ConcurrentMapCacheManager();
        
        // Define cache names for different entities
        cacheManager.setCacheNames(Arrays.asList(
                "roles",
                "accountTypes", 
                "countries",
                "provinces",
                "states",
                "areas",
                "organizationTypes",
                "identifierTypes",
                "paymentTypes",
                "notificationTypes",
                "leadTypes",
                "productCategories"
        ));
        
        // Allow creation of caches on the fly if not predefined
        cacheManager.setAllowNullValues(false);
        
        return cacheManager;
    }
}