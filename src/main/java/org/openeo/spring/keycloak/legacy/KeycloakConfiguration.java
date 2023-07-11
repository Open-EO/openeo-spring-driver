package org.openeo.spring.keycloak.legacy;

import java.io.InputStream;

import org.keycloak.adapters.KeycloakConfigResolver;
import org.keycloak.adapters.KeycloakDeployment;
import org.keycloak.adapters.KeycloakDeploymentBuilder;
import org.keycloak.adapters.spi.HttpFacade;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Provides the configuation resolver to the Keycloak auth module. 
 */
// Separate file to avoid circular deps with Spring Boot 2.6.x
// https://stackoverflow.com/questions/70207564/spring-boot-2-6-regression-how-can-i-fix-keycloak-circular-dependency-in-adapte
@ConditionalOnExpression(value = "false")
@Configuration
@Deprecated
public class KeycloakConfiguration {
    
    @Value("${spring.security.keycloak.conf-file}")
    private String keycloakConfFile;

    //      /**
    //       * Sets keycloaks config resolver to use springs application.properties instead of keycloak.json (which is standard)
    //       * @return
    //       */
    //  @Bean
    //  public KeycloakConfigResolver KeycloakConfigResolver() {
    //      return new KeycloakSpringBootConfigResolver();
    //  }
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

                String path = "/" + keycloakConfFile;
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
}
