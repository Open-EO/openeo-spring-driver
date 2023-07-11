package org.openeo.spring;

import static org.openeo.spring.GlobalSecurityManager.NOAUTH_API_RESOURCES;
import static org.openeo.spring.GlobalSecurityManager.OIDC_AUTH_API_RESOURCE;

import org.openeo.spring.keycloak.KeycloakLogoutHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.resource.OAuth2ResourceServerConfigurer;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.session.RegisterSessionAuthenticationStrategy;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;

//@Profile(KeycloakSecurityConfig.PROFILE_ID) -> better use:
@ConditionalOnProperty(prefix="spring.security", value="enable-keycloak")
@Configuration
public class KeycloakSecurityConfig {

    /** Used to define a {@link Profile}. */
    public static final String PROFILE_ID = "KEYCLOAK_AUTH";

    @Autowired
    private KeycloakLogoutHandler keycloakLogoutHandler;

//    KeycloakSecurityConfig(KeycloakLogoutHandler keycloakLogoutHandler) {
//        this.keycloakLogoutHandler = keycloakLogoutHandler;
//    }    
    
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
    public SecurityFilterChain loginFilterChain(HttpSecurity http) throws Exception {
        http
        .antMatcher(OIDC_AUTH_API_RESOURCE)
        .oauth2Login()
        .and()
        .logout()
        .addLogoutHandler(keycloakLogoutHandler)
        .logoutSuccessUrl("/");

        http.oauth2ResourceServer(OAuth2ResourceServerConfigurer::jwt);

        return http.build();
    }

    /**
     * Requires authenticated user on all resources.
     * 
     * NOTE: resources to be ignored by the authorization service are
     * configured in {@link #webSecurityCustomizer()}.
//     *  
     * @param http
     * @return
     * @throws Exception
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
        .authorizeHttpRequests(authorize -> authorize
                .anyRequest().authenticated()
                );
            
        return http.build();
    }

    /**
     * Sets the resources that do not required security rules.
     */
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web
                .ignoring()
                .antMatchers(NOAUTH_API_RESOURCES);
//                .antMatchers(BASIC_AUTH_API_RESOURCE);
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
