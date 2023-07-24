package org.openeo.spring;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.handler.SimpleUrlHandlerMapping;
import org.springframework.web.servlet.resource.ResourceHttpRequestHandler;

/**
 * Handler of the favicon.ico request.
 * 
 * TODO: Spring Boot should automatically serve static content in resources
 * classpath, but without this explicit handler nothing happens on {@code GET /favicon.ico}.
 */
@Configuration
public class FaviconConfiguration {
 
    @Bean
    public SimpleUrlHandlerMapping customFaviconHandlerMapping() {
        SimpleUrlHandlerMapping mapping = new SimpleUrlHandlerMapping();
        mapping.setOrder(Integer.MIN_VALUE);
        mapping.setUrlMap(Collections.singletonMap("/favicon.ico", faviconRequestHandler()));
        return mapping;
    }

    @Bean
    protected ResourceHttpRequestHandler faviconRequestHandler() {
        ResourceHttpRequestHandler requestHandler = new ResourceHttpRequestHandler();
        List<String> locations = Arrays.asList("classpath:static/");
        requestHandler.setLocationValues(locations);
        return requestHandler;
    }
}
