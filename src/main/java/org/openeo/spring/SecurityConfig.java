package org.openeo.spring;

import java.io.InputStream;

import org.keycloak.adapters.KeycloakConfigResolver;
import org.keycloak.adapters.KeycloakDeployment;
import org.keycloak.adapters.KeycloakDeploymentBuilder;
import org.keycloak.adapters.spi.HttpFacade;
import org.keycloak.adapters.springsecurity.authentication.KeycloakAuthenticationProvider;
import org.keycloak.adapters.springsecurity.client.KeycloakClientRequestFactory;
import org.keycloak.adapters.springsecurity.client.KeycloakRestTemplate;
import org.keycloak.adapters.springsecurity.config.KeycloakWebSecurityConfigurerAdapter;
import org.keycloak.adapters.springsecurity.filter.KeycloakAuthenticatedActionsFilter;
import org.keycloak.adapters.springsecurity.filter.KeycloakAuthenticationProcessingFilter;
import org.keycloak.adapters.springsecurity.filter.KeycloakPreAuthActionsFilter;
import org.keycloak.adapters.springsecurity.filter.KeycloakSecurityContextRequestFilter;
import org.keycloak.adapters.springsecurity.management.HttpSessionManager;
import org.openeo.spring.keycloak.OpenEOKeycloakAuthenticationProcessingFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.authority.mapping.SimpleAuthorityMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.web.authentication.session.RegisterSessionAuthenticationStrategy;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;

//@Configuration
//@EnableWebSecurity(debug = false)
//@ComponentScan(basePackageClasses = KeycloakSecurityComponents.class)
//@KeycloakConfiguration

/* ERROR: org.springframework.beans.factory.BeanCreationException
--------------------------------------------------------------

org/springframework/security/config/annotation/web/configuration/WebSecurityConfiguration.class
  ..cannot define bean with name 'springSecurityFilterChain', because:
  "Found WebSecurityConfigurerAdapter as well as SecurityFilterChain. Please select just one."

--> Keycloak configuration still provides a WebSecurityConfigurerAdapter: needs to be migrated?
"In Spring Security 5.7.0-M2 we deprecated the WebSecurityConfigurerAdapter" [1]


  [1] https://spring.io/blog/2022/02/21/spring-security-without-the-websecurityconfigureradapter 
 */
public class SecurityConfig extends KeycloakWebSecurityConfigurerAdapter {

    public static final String EURAC_ROLE = "eurac";

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
//			antMatchers("/**").
//			permitAll();

//			antMatchers("/collections").hasAnyRole(EURAC_ROLE, "public").
//			antMatchers("/collections/{collection_id}").hasAnyRole(EURAC_ROLE, "public").
//			antMatchers("/jobs/*").hasAnyRole(EURAC_ROLE, "public").
//			antMatchers("/services/*").hasAnyRole(EURAC_ROLE, "public").
//			antMatchers("/files/*").hasAnyRole(EURAC_ROLE, "public").
//			antMatchers("/me").hasAnyRole(EURAC_ROLE, "public").
//			antMatchers("/process_graphs/*").hasAnyRole(EURAC_ROLE, "public").
//			antMatchers("/result").hasAnyRole(EURAC_ROLE, "public").

			//***we haven't decided about their authentication**//

			//antMatchers("//service_types").hasAnyRole(EURAC_ROLE, "public").
			//antMatchers("/file_formats").hasAnyRole(EURAC_ROLE, "public").
			//antMatchers("/udf_runtimes").hasAnyRole(EURAC_ROLE, "public").
			//antMatchers("/processes").hasAnyRole(EURAC_ROLE, "public").
			//antMatchers("/validation").hasAnyRole(EURAC_ROLE, "public").
			anyRequest().
			permitAll();
			http.headers().frameOptions().disable();

		}

}









