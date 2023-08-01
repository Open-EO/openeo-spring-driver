package org.openeo.spring.security;

import org.apache.commons.lang3.NotImplementedException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.context.annotation.Configuration;
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
@EnableGlobalMethodSecurity(
        securedEnabled = true, 
        prePostEnabled = true) // -> @PreAuthorize annotations on controller methods
public class GlobalSecurityConfig implements EnvironmentPostProcessor {
        
    @Value("${spring.security.enable-basic}")
    boolean enableBasicAuth;
    
    @Value("${spring.security.enable-keycloak}")
    boolean enableKeycloakAuth;
    
    /** API resources that do not require authentication. */
    public static final String[] NOAUTH_API_RESOURCES = new String[] {
            "/",
            "/favicon.ico",
            "/conformance",
            "/file_formats",
            "/.well-known/**"};
    
    public static final String BASIC_AUTH_API_RESOURCE = "/credentials/basic";
    public static final String OIDC_AUTH_API_RESOURCE = "/credentials/oidc";
    
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
        
        if (!enableBasicAuth && !enableKeycloakAuth) {
            throw new InternalError("Enable at least one security provider.");
        }
        
        // TODO make both providers working together fine
        // @see TestBasicAuthentication_OIDCEnabled class
        if (enableBasicAuth && enableKeycloakAuth) {
            throw new NotImplementedException("Maximum 1 security agent is allowed.");
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
