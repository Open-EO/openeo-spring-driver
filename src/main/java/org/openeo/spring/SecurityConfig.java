package org.openeo.spring;

import java.io.InputStream;

import javax.servlet.Filter;
import javax.servlet.ServletException;

import org.hibernate.Hibernate;
import org.keycloak.adapters.KeycloakConfigResolver;
import org.keycloak.adapters.KeycloakDeployment;
import org.keycloak.adapters.KeycloakDeploymentBuilder;
import org.keycloak.adapters.spi.HttpFacade;
//import org.keycloak.adapters.springboot.KeycloakSpringBootConfigResolver;
import org.keycloak.adapters.springsecurity.KeycloakConfiguration;
import org.keycloak.adapters.springsecurity.KeycloakSecurityComponents;
import org.keycloak.adapters.springsecurity.authentication.KeycloakAuthenticationProvider;
import org.keycloak.adapters.springsecurity.client.KeycloakClientRequestFactory;
import org.keycloak.adapters.springsecurity.client.KeycloakRestTemplate;
import org.keycloak.adapters.springsecurity.config.KeycloakWebSecurityConfigurerAdapter;
import org.keycloak.adapters.springsecurity.filter.KeycloakAuthenticatedActionsFilter;
import org.keycloak.adapters.springsecurity.filter.KeycloakAuthenticationProcessingFilter;
import org.keycloak.adapters.springsecurity.filter.KeycloakPreAuthActionsFilter;
import org.keycloak.adapters.springsecurity.filter.KeycloakSecurityContextRequestFilter;
import org.keycloak.adapters.springsecurity.management.HttpSessionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.HttpSecurityBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.mapping.SimpleAuthorityMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.session.RegisterSessionAuthenticationStrategy;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.security.web.header.HeaderWriterFilter;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;



@Configuration
@EnableWebSecurity(debug = true)
@ComponentScan(basePackageClasses = KeycloakSecurityComponents.class)
@KeycloakConfiguration
public class SecurityConfig extends KeycloakWebSecurityConfigurerAdapter {
	
	private final KeycloakClientRequestFactory keycloakClientRequestFactory;
	

	 public SecurityConfig(KeycloakClientRequestFactory keycloakClientRequestFactory) {
	        this.keycloakClientRequestFactory =  keycloakClientRequestFactory;

	        //to use principal and authentication together with @async
	        SecurityContextHolder.setStrategyName(SecurityContextHolder.MODE_INHERITABLETHREADLOCAL);
	    }
	 
	 @Bean
	    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
	    public KeycloakRestTemplate keycloakRestTemplate() {
	        return new KeycloakRestTemplate(keycloakClientRequestFactory);
	    }
	 
	 
		
	 /**
	     * registers the Keycloakauthenticationprovider in spring context
	     * and sets its mapping strategy for roles/authorities (mapping to spring seccurities' default ROLE_... for authorities ).
	     * @param auth SecurityBuilder to build authentications and add details like authproviders etc.
	     * @throws Exception
	     */
	    @Autowired
	    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
	        KeycloakAuthenticationProvider keyCloakAuthProvider = keycloakAuthenticationProvider();
	        keyCloakAuthProvider.setGrantedAuthoritiesMapper(new SimpleAuthorityMapper());
	        auth.authenticationProvider(keyCloakAuthProvider);
	    }
	 
	 	 	    
	    
//	    /**
//	     * Sets keycloaks config resolver to use springs application.properties instead of keycloak.json (which is standard)
//	     * @return
//	     */
//	@Bean
//	public KeycloakConfigResolver KeycloakConfigResolver() {
//	    return new KeycloakSpringBootConfigResolver();
//	}
//	 
    @Bean
    public KeycloakConfigResolver keycloakConfigResolver() {
        return new KeycloakConfigResolver() {

            private KeycloakDeployment keycloakDeployment;

            @Override
            public KeycloakDeployment resolve(HttpFacade.Request facade) {
                if (keycloakDeployment != null) {
                    return keycloakDeployment;
                }

                String path = "/keycloak.json";
                InputStream configInputStream = getClass().getResourceAsStream(path);

                if (configInputStream == null) {
                    throw new RuntimeException("Could not load Keycloak deployment info: " + path);
                } else {
                    keycloakDeployment = KeycloakDeploymentBuilder.build(configInputStream);
                }

                return keycloakDeployment;
            }
        };
    }
	    
	    /**
	     * define the session auth strategy so that no session is created
	     * @return concrete implementation of session authentication strategy
	     */	    
	    @Bean
		@Override
		protected SessionAuthenticationStrategy sessionAuthenticationStrategy() {
			return new RegisterSessionAuthenticationStrategy(new SessionRegistryImpl());
		}
	    
	    
	    /**
	     * define the actual constraints of the app.
	     * @param http
	     * @throws Exception
	     */
	    
		
		@Bean
	    @Primary
	    @Override
	    protected KeycloakAuthenticationProcessingFilter keycloakAuthenticationProcessingFilter() throws Exception {
	        KeycloakAuthenticationProcessingFilter filter = new OpenEOKeycloakAuthenticationProcessingFilter(authenticationManagerBean());
	        filter.setSessionAuthenticationStrategy(sessionAuthenticationStrategy());
	        return filter;
	        }
		
  
		/**
	     * Avoid Bean redefinition
	     */
//	    @Bean
//	    public FilterRegistrationBean keycloakAuthenticationProcessingFilterRegistrationBean(
//	            KeycloakAuthenticationProcessingFilter filter) {
//	        FilterRegistrationBean registrationBean = new FilterRegistrationBean(filter);
//	        registrationBean.setEnabled(false);
//	        return registrationBean;
//	    }
	   

	    @Bean
	    public FilterRegistrationBean keycloakPreAuthActionsFilterRegistrationBean(
	            KeycloakPreAuthActionsFilter filter) {
	        FilterRegistrationBean registrationBean = new FilterRegistrationBean(filter);
	        registrationBean.setEnabled(false);
	        return registrationBean;
	    }
	       

	    @Bean
	    public FilterRegistrationBean keycloakAuthenticatedActionsFilterBean(
	            KeycloakAuthenticatedActionsFilter filter) {
	        FilterRegistrationBean registrationBean = new FilterRegistrationBean(filter);
	        registrationBean.setEnabled(false);
	        return registrationBean;
	    }

	    @Bean
	    public FilterRegistrationBean keycloakSecurityContextRequestFilterBean(
	            KeycloakSecurityContextRequestFilter filter) {
	        FilterRegistrationBean registrationBean = new FilterRegistrationBean(filter);
	        registrationBean.setEnabled(false);
	        return registrationBean;
	    }

	    @Bean
	    @Override
	    @ConditionalOnMissingBean(HttpSessionManager.class)
	    protected HttpSessionManager httpSessionManager() {
	        return new HttpSessionManager();
	    }
	    

	    
	    @Override
		protected void configure(HttpSecurity http) throws Exception {
			super.configure(http);
			http.
			csrf().
			disable().
			authorizeRequests().
			//antMatchers("/**").
			//permitAll().
			
			antMatchers("/collections").hasAnyRole("eurac", "public").
			antMatchers("/collections/{collection_id}").hasAnyRole("eurac", "public").	
//			antMatchers("/jobs/*").hasAnyRole("eurac", "public").
//			antMatchers("/services/*").hasAnyRole("eurac", "public").
//			antMatchers("/files/*").hasAnyRole("eurac", "public").
//			antMatchers("/me").hasAnyRole("eurac", "public").
//			antMatchers("/process_graphs/*").hasAnyRole("eurac", "public").
//			antMatchers("/result").hasAnyRole("eurac", "public").
			
			//***we haven't decided about their authentication**//
			
			//antMatchers("//service_types").hasAnyRole("eurac", "public").
			//antMatchers("/file_formats").hasAnyRole("eurac", "public").
			//antMatchers("/udf_runtimes").hasAnyRole("eurac", "public").
			//antMatchers("/processes").hasAnyRole("eurac", "public").
			//antMatchers("/validation").hasAnyRole("eurac", "public").
			anyRequest().
			permitAll();
			http.headers().frameOptions().disable();

		}
	    
}







	

