package com.synkro.workflow.observability;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ObservabilityConfig {

    @Bean
    public StructuredLogger structuredLogger(@Value("${spring.application.name}") String service,
                                             @Value("${app.version}") String version,
                                             @Value("${app.environment}") String environment) {
        return new StructuredLogger(service, version, environment, System.out);
    }
}
