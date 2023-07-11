package org.openeo.spring;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.WebSecurityConfigurer;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

/**
 * In order to have multiple authentication mechanisms available
 * simultaneously we need a single {@link WebSecurityConfigurer}
 * where nested configurations are listed.
 * 
 * This class also dynamically enables/disables authentication
 * mechanisms based on the application properties.
 * 
 * Environment post-processors classes need to be registered in the 
 * {@code META-INF/spring.factories} file.
 * 
 * @see {@code spring.security.enable-basic}
 * @see {@code spring.security.enable-keycloak}
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true)
@Order(Ordered.LOWEST_PRECEDENCE)
public class GlobalSecurityConfig implements EnvironmentPostProcessor {
        
    @Value("${spring.security.enable-basic}")
    boolean enableBasicAuth;
    
    @Value("${spring.security.enable-keycloak}")
    boolean enableKeycloakAuth;    
    
    /**
     * Configured the application environment (e.g. activate profiles
     * to dynamically control which configurations/components get actually
     * processed by Spring Boot at runtime). 
     */
    @Override
    public void postProcessEnvironment(ConfigurableEnvironment env, SpringApplication app) {
        // FIXME @Value not processed yet / not called yet
        enableBasicAuth = env.getProperty("spring.security.enable-basic", Boolean.class);
        enableKeycloakAuth = env.getProperty("spring.security.enable-keycloak", Boolean.class);

        // just as an exercise: we use @ConditionalOnProperty anyway to control activation of beans
        if (enableBasicAuth) {
            env.addActiveProfile(BasicSecurityConfig.PROFILE_ID);
        }
        if (enableKeycloakAuth) {
            env.addActiveProfile(KeycloakSecurityConfig.PROFILE_ID);
        }
    }
    
    /**
     * Recommended authentication mechanism: OIDC/OAuth2 via Keycloak.
     */
    @Order(1)
    public static class RecommendedSecurityConfig extends KeycloakSecurityConfig {}

    /**
     * Optional "basic" (user/password) authentication mechanism.
     */
    @Order(2)
    public static class OptionalSecurityConfig extends BasicSecurityConfig {}
}
