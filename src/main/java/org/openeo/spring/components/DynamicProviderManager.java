package org.openeo.spring.components;

import java.util.Collections;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;

/**
 * Authentication manager that dynamically filters the available
 * authentication providers based on the application configuration
 * (properties).
 */
//@Component
public class DynamicProviderManager extends ProviderManager {
    
    private boolean enabled;
    
    public DynamicProviderManager(AuthenticationProvider... providers) {
        super(providers);
    }
    
//    public DynamicProviderManager(AuthenticationManager parent) {
//        super(Collections.emptyList(), parent);
//        setEnabled(true);
//    }
    
    public void setEnabled(boolean b) {
        this.enabled = b;        
    }

    public boolean getEnabled() {
        return enabled;
    }
    
    // enable @value properties injection
    @Bean
    public static PropertySourcesPlaceholderConfigurer placeholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    @Override
    public List<AuthenticationProvider> getProviders() {
        return getEnabled() ? super.getProviders() : Collections.emptyList();        
    }
}
