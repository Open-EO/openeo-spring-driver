package org.openeo.spring.security;

import static org.openeo.spring.security.GlobalSecurityConfig.BASIC_AUTH_API_RESOURCE;
import static org.openeo.spring.security.GlobalSecurityConfig.NOAUTH_API_RESOURCES;
import static org.openeo.spring.security.GlobalSecurityConfig.OIDC_AUTH_API_RESOURCE;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openeo.spring.bearer.PrefixedBearerTokenResolver;
import org.openeo.spring.components.FilterChainExceptionHandler;
import org.openeo.spring.keycloak.KeycloakLogoutHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.resource.OAuth2ResourceServerConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.oauth2.server.resource.web.BearerTokenResolver;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.authentication.session.RegisterSessionAuthenticationStrategy;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;

@Configuration
//@Profile(KeycloakSecurityConfig.PROFILE_ID) -> better use:
@ConditionalOnProperty(prefix="spring.security", value="enable-keycloak")
@PropertySource("classpath:keycloak.properties")
public class KeycloakSecurityConfig {

    /** Used to define a {@link Profile}. */
    public static final String PROFILE_ID = "KEYCLOAK_AUTH";
    
    /** The beginning of an OIDC JWT token prefix (full prefix depends on provider) */
    public static final String TOKEN_PREFIX_START = "oidc/"; // "oidc/ms/TOKEN"
    
    private static final Logger LOGGER = LogManager.getLogger(KeycloakSecurityConfig.class);

    @Autowired
    KeycloakLogoutHandler keycloakLogoutHandler;
    
    @Autowired
    FilterChainExceptionHandler filterChainExHandler;
    
    @Bean
    protected SessionAuthenticationStrategy sessionAuthenticationStrategy() {
        return new RegisterSessionAuthenticationStrategy(new SessionRegistryImpl());
    }

    /**
     * Requires login input on the basic-auth endpoint. 
     * @param http
     * @return
     * @throws Exception
     */
    @Bean
    public SecurityFilterChain kcLoginFilterChain(HttpSecurity http) throws Exception {
        http
        .antMatcher(OIDC_AUTH_API_RESOURCE)
        .oauth2Login(/*withDefaults()*/)
        .and()
        // disable session management (JSESSIONID cookies -> security risks)
        .sessionManagement()
        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        // logout
        .and()
        .logout(logout -> logout
                .addLogoutHandler(keycloakLogoutHandler)
                .logoutSuccessUrl("/")
        );
        
        LOGGER.info("Keycloak authentication security chain set.");

        return http.build();
    }

    @Bean
    public SecurityFilterChain kcSecurityFilterChain(HttpSecurity http) throws Exception {
        http
        .authorizeHttpRequests(authorize -> authorize
                .anyRequest().authenticated()
         )
        // disable session management (JSESSIONID cookies -> security risks)
        .sessionManagement()
        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        // JWT token
        .and()
        .oauth2ResourceServer(OAuth2ResourceServerConfigurer::jwt)
        // catch exceptions and resolve them to error for the client
        .addFilterBefore(filterChainExHandler, LogoutFilter.class);
        
        LOGGER.info("Keycloak authentication security chain set.");

        return http.build();
    }

    /**
     * Sets the resources that do not required security rules.
     */
    @Bean
    public WebSecurityCustomizer kcWebSecurityCustomizer() {
        return (web) -> web
                .ignoring()
                .antMatchers(NOAUTH_API_RESOURCES)
                .antMatchers(BASIC_AUTH_API_RESOURCE);
    }
    
    /**
     * Need to strip away the {@code oidc/ms/} prefix from the token before decoding it. 
     */
    @Bean
    BearerTokenResolver bearerTokenResolver() {
        return new PrefixedBearerTokenResolver(TOKEN_PREFIX_START);
    }
    
    /**
     * Overrides default API filters set up by Spring Boot auto-configuration.
     * Without this, OAuth2 login redirection specified in {@code application.properties} is processed. 
     * 
     * @see org.springframework.boot.autoconfigure.security.oauth2.client.servlet.OAuth2ClientAutoConfiguration#OAuth2ClientAutoConfiguration()
     * @see <a href="https://docs.spring.io/spring-security/reference/servlet/oauth2/login/core.html#oauth2login-override-boot-autoconfig">Overriding Spring Boot 2.x Auto-configuration</a>
     */
    @ConditionalOnProperty(prefix="spring.security", value="enable-keycloak", havingValue="false")
    @Configuration
    static class SuppressAutoConfigLogin {

        @Bean
        public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
            // NOOP, but avoid OAuth 2.0 Login auto-configuration 
            return http.build();
        }
    }
}
