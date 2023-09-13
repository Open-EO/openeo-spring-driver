package org.openeo.spring;

import java.io.IOException;
import java.util.Enumeration;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class ApiFilter extends OncePerRequestFilter {
	
	private final Logger log = LogManager.getLogger(ApiFilter.class);
	@Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) 
    		throws IOException, ServletException {
	    
        log.trace("Spring Security Filter Chain: {}", chain.getClass());
        	    
        HttpServletResponse res = response;
        HttpServletRequest req = request;
        log.debug("Filter: URL" + " called: "+req.getRequestURL().toString());
        
        Enumeration<String> headerEnum = req.getHeaderNames();        
        while(headerEnum.hasMoreElements()) {
        	String headerName = headerEnum.nextElement();
        	log.trace(headerName + " = " + req.getHeader(headerName));
        }
        
        String clientIp = req.getHeader("Origin");
        if(clientIp == null) {
            clientIp = req.getHeader("X-Forwarded-For");
            if(clientIp == null) {
                clientIp = request.getRemoteHost();
                log.debug("Got direct request from the following client: " + clientIp);
            } else {
                log.debug("Got proxy forwared request from the following client: " + clientIp);
            }
        } else {
            log.debug("Got request from the following js client: " + clientIp);
        }
        
        chain.doFilter(request, response);
    }
}