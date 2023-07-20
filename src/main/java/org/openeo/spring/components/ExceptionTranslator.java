package org.openeo.spring.components;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openeo.spring.model.Error;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.HandlerExceptionResolver;

import io.jsonwebtoken.JwtException;

/**
 * Collection of exception handlers for the API.
 * 
 * Registered handlers are used by the {@link HandlerExceptionResolver} to resolve
 * the exceptions, given its type.
 *
 * @see FilterChainExceptionHandler
 */
@RestControllerAdvice
public class ExceptionTranslator {

    private static final Logger LOGGER = LogManager.getLogger(ExceptionTranslator.class);

    /**
     * Handling of an error in the validation of an incoming Bearer token:
     * expired token, invalid format, maliciously crafted tokens etc.
     */
    @ExceptionHandler(JwtException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ResponseEntity<Error> processBearerTokenValidationException(JwtException e) {
        LOGGER.error("JWT token exception caught: ", e);

        Error error = new Error();
        error.setCode("403");
        error.setMessage(e.getMessage());

        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(error);
    }
    
    /**
     * @deprecated All {@link AuthenticationException} thrown are handled by the
     *             {@link BasicAuthenticationFilter}.
     */
    @Deprecated
    @ExceptionHandler(AuthenticationException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ResponseEntity<Error> processAuthenticationException(AuthenticationException e) {
        LOGGER.error("Authentication exception caught: ", e);

        Error error = new Error();
        error.setCode("403");
        error.setMessage(e.getMessage());

        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(error);
    }
    
    /**
     * Last resort handler of all not-yet managed runtime error.
     */
    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<Error> processAllException(RuntimeException e) {
        LOGGER.error("Runtime exception caught: ", e);

        Error error = new Error();
        error.setCode("500");
        error.setMessage(e.getMessage());

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(error);
    }
}
