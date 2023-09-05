package org.openeo.spring.keycloak;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.security.oauth2.client.servlet.OAuth2ClientAutoConfiguration;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySource;

/**
 * This component disables Sprint Boot OAuth2 clients auto-configurations
 * whenever Keycloak authentication is also disabled.
 */
@Order(Ordered.LOWEST_PRECEDENCE)
public class KeycloakAutoconfigPostProcessor implements EnvironmentPostProcessor {
    
    public static final String SPRING_EXCLUDE_PROPERTY = "spring.autoconfigure.exclude";
    public static final String SPRING_EXCLUDE_CLASS = OAuth2ClientAutoConfiguration.class.getCanonicalName();
    
    private static final String PROPERTY_SOURCE_NAME = "overwriteProperties";
    private static final Logger LOGGER = LogManager.getLogger(KeycloakAutoconfigPostProcessor.class);

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment env, SpringApplication app) {
        
        boolean enableKeycloakAuth = env.getProperty("spring.security.enable-keycloak", Boolean.class);
        
        if (!enableKeycloakAuth) {
            String excludes = env.getProperty(SPRING_EXCLUDE_PROPERTY);
            
            if (null == excludes || excludes.isBlank()) {
                excludes = SPRING_EXCLUDE_CLASS;
            } else {
                excludes += "," + SPRING_EXCLUDE_CLASS;
            }
            
            // disable OAuth2 client auto-configuration, programmatically:
            Map<String, Object> map = new HashMap<>();
            map.put(SPRING_EXCLUDE_PROPERTY, excludes);
            
            addOrReplace(env.getPropertySources(), map);
            LOGGER.debug("Spring Boot OAuth2 client auto-configuration disabled.");
        }
    }

    /** Adds/overrides a property in the environment. */
    private void addOrReplace(MutablePropertySources propertySources,
            Map<String, Object> map) {
        MapPropertySource target = null;
        if (propertySources.contains(PROPERTY_SOURCE_NAME)) {
            PropertySource<?> source = propertySources.get(PROPERTY_SOURCE_NAME);
            if (source instanceof MapPropertySource) {
                target = (MapPropertySource) source;
                for (String key : map.keySet()) {
                    if (!target.containsProperty(key)) {
                        target.getSource().put(key, map.get(key));
                    }
                }
            }
        }
        if (target == null) {
            target = new MapPropertySource(PROPERTY_SOURCE_NAME, map);
        }
        if (!propertySources.contains(PROPERTY_SOURCE_NAME)) {
            propertySources.addFirst(target); // first -> overrides existing
        }
    }
}
