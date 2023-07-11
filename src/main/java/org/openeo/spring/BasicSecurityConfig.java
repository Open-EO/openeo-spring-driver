package org.openeo.spring;

import static org.openeo.spring.GlobalSecurityManager.BASIC_AUTH_API_RESOURCE;
import static org.openeo.spring.GlobalSecurityManager.NOAUTH_API_RESOURCES;
import static org.openeo.spring.GlobalSecurityManager.OIDC_AUTH_API_RESOURCE;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

//@Profile(BasicSecurityFromFileConfig.PROFILE_ID) -> better use:
@ConditionalOnProperty(prefix="spring.security", value="enable-basic")
@Configuration
//@ComponentScan("org.openeo.spring.components")
@ImportResource({ "classpath:webSecurityConfig.xml" })
public class BasicSecurityConfig {
    
    /** Used to define a {@link Profile}. */
    public static final String PROFILE_ID = "BASIC_AUTH_FILE";
    
    /**
     * Requires login input on the basic-auth endpoint. 
     */
    @Bean
    public SecurityFilterChain loginFilterChain(HttpSecurity http) throws Exception {
        http
        .antMatcher(BASIC_AUTH_API_RESOURCE)
        .authorizeHttpRequests(authorize -> authorize
                .anyRequest().authenticated()
                )
        .httpBasic();
        //      .rememberMe(Customizer.withDefaults());

        return http.build();
    }
    
    /**
     * Requires authenticated user on all resources.
     * 
     * NOTE: resources to be ignored by the authorization service are
     * configured in {@link #webSecurityCustomizer()}.
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
                .antMatchers(NOAUTH_API_RESOURCES)
                .antMatchers(OIDC_AUTH_API_RESOURCE);
    }
    
    // to encode passwords, use Spring Boot CLI
    // $ spring encodepassword "password"
    // THIS IS CONFIGURED IN THE XML CONFIG FILE:
//    @Bean
//    public UserDetailsService userDetailsService() {
//        UserDetails user = User.builder()
//                .username("user")
//                .password("{bcrypt}$2a$10$X5wFBtLrL/kHcmrOGGTrGufsBX8CJ0WpQpF3pgeuxBB/H73BK1DW6")
//                .roles("USER")
//                .build();
//        UserDetails admin = User.builder()
//                .username("admin")
//                .password("{bcrypt}$2a$10$9BwjekB1vfhiuuHaFCGVOuL0WYUJzrCTz1sw3ZwA.KsU2s09H4uDS")
//                .roles("USER", "ADMIN")
//                .build();
//
//        return new InMemoryUserDetailsManager(user, admin);
//    }
    
    /**
     * Crypto-encoder for local storage of passwords.
     * 
     * NOTE: this has nothing to do with the basic-auth encoding
     * function used in HTTP headers (which shall be Base64 by 
     * <a href="https://www.rfc-editor.org/rfc/rfc7617.html">
     * Basic HTTP Authentication scheme standard</a>).
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        // it will detect the {id} of passwords (eg. {bcrypt}) and delegate
        return PasswordEncoderFactories.createDelegatingPasswordEncoder(); 
    }
    
    @Bean
    @Primary
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
    
    /** Overload for {@link #authenticationManager(AuthenticationConfiguration)}. */
//  public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
//      final AuthenticationConfiguration AC = http.getSharedObject(AuthenticationConfiguration.class);
//      return authenticationManager(AC);
//  }
  
  /*
   * (force my custom Authentication Provider here)
   * NO: automatically injected in the manager if it is a @Component
   */
//  @Bean
//  public AuthenticationManager authManager(HttpSecurity http) throws Exception {
//      AuthenticationManagerBuilder authenticationManagerBuilder = 
//          http.getSharedObject(AuthenticationManagerBuilder.class);
//      authenticationManagerBuilder.authenticationProvider(AP);
//      return authenticationManagerBuilder.build();
//  }
}