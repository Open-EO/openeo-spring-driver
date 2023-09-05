package org.openeo.spring.bearer;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.NotImplementedException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.server.resource.BearerTokenAuthenticationToken;
import org.springframework.security.oauth2.server.resource.web.BearerTokenAuthenticationFilter;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Filter to be added in the HTTP basic security chain,
 * in order to manage the Bearer token generation and parsing.
 * 
 * @see BearerTokenAuthenticationFilter
 */
@Component
@ConditionalOnProperty(prefix="spring.security", value="enable-basic")
public class JWTAuthenticationFilter extends OncePerRequestFilter {
    
    @Autowired
    ITokenService tokenService;
    
    /** HTTP Bearer scheme id. */
    private static String BEARER_HEADER_PRE = "Bearer "; 
    
    /** Prefix of the HTTP authentication Basic authentication token header. */
    private static String BA_HEADER_PRE = "Basic ";
    
    private static final Logger LOGGER = LogManager.getLogger(JWTAuthenticationFilter.class);
    
    /**
     * Control the filter registration that Spring would otherwise automatically do.
     */
    @Bean
    public FilterRegistrationBean<JWTAuthenticationFilter> jwtAuthenticationRegistration(JWTAuthenticationFilter filter) {
        FilterRegistrationBean<JWTAuthenticationFilter> registration = new FilterRegistrationBean<>(filter);
        registration.setEnabled(false);
        return registration;
    }
    
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws IOException, ServletException {
        
        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        
        if (authorizationHeaderIsValid(authorizationHeader)) {
            Authentication authResult = SecurityContextHolder.getContext().getAuthentication(); 
            if (null != authResult) {
                onSuccessfulAuthentication(request, response, authResult);
            } else {
                LOGGER.debug("Unauthenticated user: NOOP.");
            }
        } else {
            LOGGER.debug("No valid \"Authorization\" header found.");
        }
        
        // do not break the chain!
        filterChain.doFilter(request, response);
    }
    
    /**
     * What to be done when a new successful authentication process has been completed.
     */
    protected void onSuccessfulAuthentication(HttpServletRequest request,
            HttpServletResponse response, Authentication authResult) {
        Object authPrincipal = authResult.getPrincipal();
        
        if (authPrincipal instanceof UserDetails) {
            UserDetails user = (UserDetails) authPrincipal;
            String token = tokenService.generateToken(user);
            
            // store the token in the session for later usage in the chain
            BearerTokenAuthenticationToken tokenAuth = new BearerTokenAuthenticationToken(token);
            tokenAuth.setDetails(authPrincipal);
            tokenAuth.setAuthenticated(true); // !
            SecurityContextHolder.getContext().setAuthentication(tokenAuth);

            // add the token in the response header
            response.addHeader(HttpHeaders.AUTHORIZATION,
                    String.format("%s%s", BEARER_HEADER_PRE, token));
            
            LOGGER.debug("JWT token added to the response's header: {}...", token);
            
        } else {
            throw new NotImplementedException(String.format(
                    "Authentication object not handled: %s", authPrincipal.getClass()));
        }
    }
    /**
     * The request header should be a Basic authentication request
     * Further requests from client shall have the "Bearer" prefix instead.
     */ 
    private boolean authorizationHeaderIsValid(String authorizationHeader) {
        return null != authorizationHeader 
                && authorizationHeader.startsWith(BA_HEADER_PRE);
    }
}
