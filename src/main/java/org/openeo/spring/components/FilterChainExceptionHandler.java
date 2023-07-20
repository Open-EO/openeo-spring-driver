package org.openeo.spring.components;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

/**
 * The overarching exception handler in the HTTP chain of filters.
 * 
 * This filter should be placed before other custom security,
 * authentication or authorization filters so that the new logic
 * is safely placed inside a trz-catch block.
 */
@Component
public class FilterChainExceptionHandler extends OncePerRequestFilter {

    private final Logger LOGGER = LogManager.getLogger(FilterChainExceptionHandler.class);

    @Autowired
    @Qualifier("handlerExceptionResolver")
    private HandlerExceptionResolver resolver;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        try {
            filterChain.doFilter(request, response);
        } catch (BadCredentialsException bex) {
            LOGGER.error("TESTING", bex);
            resolver.resolveException(request, response, null, bex);
            
        } catch (Exception e) {
            LOGGER.error("Spring Security Filter Chain Exception:", e);
            resolver.resolveException(request, response, null, e); // --> to ExceptionTranslators
        }
    }
}