package org.openeo.spring.components;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.openeo.spring.api.ApiUtil;
import org.openeo.spring.model.Error;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.error.ErrorAttributeOptions.Include;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;

/**
 * Unit testing of the customized error attributes
 * in the Spring Boot application (which do not get (easily) 
 * wired in in mock MVC integration tests) 
 */
public class TestErrorAttributes {

    private ErrorAttributes errorAttributes;
    private MockHttpServletRequest request;

    @BeforeEach
    public void setup() {
        errorAttributes = new ErrorAttributes();
        request = new MockHttpServletRequest();
    }
    
    @ParameterizedTest
    @EnumSource(value = HttpStatus.class, names =  {
            "BAD_REQUEST", "UNAUTHORIZED", "FORBIDDEN",
            "INTERNAL_SERVER_ERROR", "NOT_IMPLEMENTED", "BAD_GATEWAY"})
    void mvcError(HttpStatus errCode) {
        
        String errMsg = "Massive Black Hole";
        ResponseEntity<Error> error = ApiUtil.errorResponse(errCode, errMsg);
        request.setAttribute(BAuthEntrypoint.REALM_LABEL, error);
                
        RuntimeException ex = new RuntimeException("Test Exception");
        ModelAndView modelAndView = errorAttributes.resolveException(request, null, null, ex);
        
        WebRequest webRequest = new ServletWebRequest(this.request);
        Map<String, Object> attributes = errorAttributes.getErrorAttributes(webRequest,
                ErrorAttributeOptions.of(Include.MESSAGE));
        
        assertEquals(errorAttributes.getError(webRequest), ex);
        
        assertThat(attributes.size()).isEqualTo(4);
        assertTrue(attributes.containsKey("id"));
        assertTrue(attributes.containsKey("code"));
        assertTrue(attributes.containsKey("message"));
        assertTrue(attributes.containsKey("link"));
        
        assertEquals(attributes.get("code"), String.valueOf(errCode.value()));
        assertEquals(attributes.get("message"), errMsg);
        
        assertThat(modelAndView).isNull();
    }
    
}
