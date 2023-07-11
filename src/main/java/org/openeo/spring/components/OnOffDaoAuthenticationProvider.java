package org.openeo.spring.components;

import org.openeo.spring.BasicSecurityConfig;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;

/**
 * DAO authentication provider that can be enabled/disabled at runtime.
 * 
 * Deprecated in favor of annotation-based runtime conditional activations.
 * @see ConditionalOnProperty
 */  
//@Component
@Deprecated
public class OnOffDaoAuthenticationProvider extends DaoAuthenticationProvider {
    
    
    /** Switch to enable/disable this authentication provider at runtime. */ 
    private boolean enabled;
    
    public OnOffDaoAuthenticationProvider(BasicSecurityConfig config) {
        super();
//        this.setUserDetailsService(config.userDetailsService());
        this.setPasswordEncoder(config.passwordEncoder());
        
        // from: org.springframework.security.config.annotation.authentication.configuration.InitializeUserDetailsBeanManagerConfigurer
//        UserDetailsPasswordService passwordManager = getBeanOrNull(UserDetailsPasswordService.class);
        
//        provider.setUserDetailsPasswordService(passwordManager);
        
//        setEnabled(config.getEnabled()); // better use @ConditionalOnProperty annotation on the config
    }
    
    /**
     * Tells whether this provider is enabled (or disabled).
     */
    public boolean isEnabled() {
        return enabled;
    }
    
    public void setEnabled(boolean b) {
        this.enabled = b;
    }
    
    @Override
    public boolean supports(Class<?> authentication) {
        boolean out = false;
        if (isEnabled() || true) { // FIXME testing whether manipulating filterchains is enough to disable
            out = super.supports(authentication);
        }
        return out;
    }
}
