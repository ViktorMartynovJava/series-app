package com.seriesapp.config;

import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MetricsConfig {

    @Bean
    public CommandLineRunner configureCommonTags(
            MeterRegistry registry,
            @Value("${spring.application.name:seriesapp}") String applicationName) {
        return args -> registry.config().commonTags("application", applicationName);
    }
}