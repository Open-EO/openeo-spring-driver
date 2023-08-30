package org.openeo.spring.api;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.openeo.spring.model.Error;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.context.request.NativeWebRequest;

/**
 * Shared utilities for API controllers. 
 */
public class ApiUtil {
    
    public static void setExampleResponse(NativeWebRequest req, String contentType, String example) {
        try {
            HttpServletResponse res = req.getNativeResponse(HttpServletResponse.class);
            res.setCharacterEncoding("UTF-8");
            res.addHeader(HttpHeaders.CONTENT_TYPE, contentType);
            res.getWriter().print(example);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    /**
     * Crafts an error response.
     * @see Error
     */
    public static ResponseEntity<Error> errorResponse(HttpStatus code, String message)
            throws HttpServerErrorException {
        if (!code.isError()) {
            throw new HttpServerErrorException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    String.format("%d is not an error code.", code.value()));
        }
        
        Error error = new Error();
        error.setCode(String.valueOf(code.value()));
        error.setMessage(message);
        
        return ResponseEntity
                .status(code)
                .header(HttpHeaders.CONTENT_TYPE, "application/json")
                .body(error);
    }
    
    /**
     * Overload with default message.
     * @see HttpStatus#getReasonPhrase()
     */
    public static ResponseEntity<Error> errorResponse(HttpStatus code) {
        return errorResponse(code, code.getReasonPhrase());
    }
}
