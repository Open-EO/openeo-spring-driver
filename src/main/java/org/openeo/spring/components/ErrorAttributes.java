package org.openeo.spring.components;

import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openeo.spring.model.Error;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.WebRequest;

/**
 * Override Spring auto-configured white-label error structure.
 */
@Component
public class ErrorAttributes extends DefaultErrorAttributes {
    
    private static final Logger LOGGER = LogManager.getLogger(ErrorAttributes.class);

    @Override
    public Map<String, Object> getErrorAttributes(WebRequest webRequest, ErrorAttributeOptions options) {
        
        Map<String, Object> errorAttributes = super.getErrorAttributes(webRequest, options);
        ResponseEntity<Error> response = getAttribute(webRequest, BAuthEntrypoint.REALM_LABEL);
        
        if (null != response) {
            LOGGER.debug("Will change default attributes for error: {}", response.getStatusCode());
            
            Error error = response.getBody();
            errorAttributes.clear();

            // TODO extract attributes labels from Error annotations
            errorAttributes.put("id", error.getId());
            errorAttributes.put("code", error.getCode());
            errorAttributes.put("message", error.getMessage());
            errorAttributes.put("link", error.getLinks());
        }

        return errorAttributes;
    }
    
    @SuppressWarnings("unchecked")
    private <T> T getAttribute(RequestAttributes requestAttributes, String name) {
        return (T) requestAttributes.getAttribute(name, RequestAttributes.SCOPE_REQUEST);
    }
}
