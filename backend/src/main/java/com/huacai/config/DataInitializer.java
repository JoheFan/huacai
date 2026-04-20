package com.huacai.config;

import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataInitializer {

    @Bean
    public ApplicationRunner applicationRunner(AdminDataInitializationService adminDataInitializationService) {
        return args -> adminDataInitializationService.initializeAdminData();
    }
}
