package org.openeo.spring.security;

import java.io.IOException;

import org.openeo.spring.OpenAPI2SpringBoot;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.CorsUtils;
import org.springframework.web.cors.DefaultCorsProcessor;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * Centralized CORS (Cross-Origin Resource Sharing) configuration.
 * 
 * A CORS filter is manually registered in all security chains
 * in the {@link OpenAPI2SpringBoot} class.
 * The {@link #corsFilter()} method exposes it also as a bean
 * in the application context. 
 */
@Configuration
public class CorsConfig {
    
    /** Factory method. */
    @Bean // -> to share configuration in case of Bean-based CORS (HttpScurity.cors())
    public static CorsFilter corsFilter() {
        CorsFilter corsFilter = new CorsFilter(corsConfigurationSource());
        corsFilter.setCorsProcessor(new CorsProcessor());
        return corsFilter;
    }
    
    @Bean
    static CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        configuration.addAllowedOrigin("*");
//        configuration.setAllowCredentials(true); > incompatible with "*" above
        
        configuration.addAllowedMethod(HttpMethod.OPTIONS);
        configuration.addAllowedMethod(HttpMethod.GET);
        configuration.addAllowedMethod(HttpMethod.PATCH);
        configuration.addAllowedMethod(HttpMethod.POST);
        configuration.addAllowedMethod(HttpMethod.PUT);
        configuration.addAllowedMethod(HttpMethod.DELETE);
        
        configuration.addAllowedHeader(HttpHeaders.ACCEPT);
        configuration.addAllowedHeader(HttpHeaders.AUTHORIZATION);
        configuration.addAllowedHeader(HttpHeaders.CONTENT_TYPE);
        configuration.addAllowedHeader(HttpHeaders.ORIGIN);
        
        configuration.addExposedHeader("Location, OpenEO-Identifier, OpenEO-Costs, Link");
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        
        return source;
    }
    
    /**
     * The CORS processor, where the HTTP response headers are crafted.
     */
    static class CorsProcessor extends DefaultCorsProcessor {
        
        @Override
        protected boolean handleInternal(ServerHttpRequest request, ServerHttpResponse response,
                CorsConfiguration config, boolean preFlightRequest) throws IOException {
            
            boolean bOut = super.handleInternal(request, response, config, preFlightRequest);
            
            // set 204 on empty preflight responses            
            if (CorsProcessor.isPreFlightRequest(request)) {
                response.setStatusCode(HttpStatus.NO_CONTENT);
            }
            
            return bOut;
        }
        
        /** Detects a CORS pre-flight request. */
        static boolean isPreFlightRequest(ServerHttpRequest request) {
            if (null == request) {
                return false;
            }
            
            if (request instanceof ServletServerHttpRequest) {
                ServletServerHttpRequest ssReq = (ServletServerHttpRequest) request;
                return CorsUtils.isPreFlightRequest(ssReq.getServletRequest());
            }
            
            return (HttpMethod.OPTIONS.equals(request.getMethod()) &&
                    request.getHeaders().getOrigin() != null &&
                    request.getHeaders().getAccessControlRequestMethod() != null);
        }
    }
}