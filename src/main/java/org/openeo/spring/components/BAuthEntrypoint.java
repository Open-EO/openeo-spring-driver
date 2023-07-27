package org.openeo.spring.components;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.openeo.spring.api.ApiUtil;
import org.openeo.spring.model.Error;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.config.annotation.web.configurers.HttpBasicConfigurer;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint;
import org.springframework.stereotype.Component;

/**
 * Override the default behaviour of the Spring Basic Authentication
 * entrypoint.
 * @see HttpBasicConfigurer#authenticationEntryPoint(org.springframework.security.web.AuthenticationEntryPoint)
 */
@Component
public class BAuthEntrypoint extends BasicAuthenticationEntryPoint {
    
    /** Label for the "realm" set in {@code WWW-Authenticate} response header. */
    public static final String REALM_LABEL = "openEO";
    
    public BAuthEntrypoint() {
        super();
        setRealmName(REALM_LABEL);
    }
    
    /**
     * Sends a response where authentication is requested.
     * Default error attributes are overridden in {@link ErrorAttributes}. 
     */
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException authException) throws IOException {
        response.addHeader(HttpHeaders.WWW_AUTHENTICATE, "Basic realm=\"" + getRealmName() + "\"");
        
        ResponseEntity<Error> resp = ApiUtil.errorResponse(HttpStatus.UNAUTHORIZED);
        request.setAttribute(REALM_LABEL, resp); // [!]
        
        response.sendError(resp.getStatusCodeValue(), resp.toString());
    }

}
