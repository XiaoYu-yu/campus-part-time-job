package com.cangqiong.takeaway.config;

import com.cangqiong.takeaway.config.properties.CorsProperties;
import com.cangqiong.takeaway.config.properties.JwtProperties;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class ProductionReadinessValidator {

    @Autowired
    private Environment environment;

    @Autowired
    private JwtProperties jwtProperties;

    @Autowired
    private CorsProperties corsProperties;

    @PostConstruct
    public void validate() {
        boolean prodActive = Arrays.stream(environment.getActiveProfiles())
                .anyMatch("prod"::equalsIgnoreCase);
        if (!prodActive) {
            return;
        }

        String secret = jwtProperties.getSecret();
        if (secret == null || secret.isBlank()) {
            throw new IllegalStateException("Production JWT secret is required. Please set the JWT_SECRET environment variable before startup.");
        }
        if (secret.length() < 32) {
            throw new IllegalStateException("Production JWT secret must be at least 32 characters long.");
        }
        if (corsProperties.getAllowedOrigins() == null || corsProperties.getAllowedOrigins().isEmpty()) {
            throw new IllegalStateException("Production CORS origins are required. Please set APP_CORS_ALLOWED_ORIGINS before startup.");
        }
    }
}
