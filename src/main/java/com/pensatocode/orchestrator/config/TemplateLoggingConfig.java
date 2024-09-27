package com.pensatocode.orchestrator.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;

import java.util.Arrays;

@Configuration
public class TemplateLoggingConfig {

    private static final Logger logger = LoggerFactory.getLogger(TemplateLoggingConfig.class);

    @Bean
    public CommandLineRunner logTemplateLocations(ResourcePatternResolver resourcePatternResolver) {
        return args -> {
            logger.info("Logging template locations:");
            Resource[] resources = resourcePatternResolver.getResources("classpath*:/templates/**/*.html");
            Arrays.stream(resources).forEach(resource -> {
                try {
                    logger.info("Found template: " + resource.getURI());
                } catch (Exception e) {
                    logger.error("Error logging template location", e);
                }
            });
        };
    }
}
