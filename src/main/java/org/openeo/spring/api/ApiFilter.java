package org.openeo.spring.api;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Component
@Order(1)
public class ApiFilter implements Filter {
	
	private final Logger log = LogManager.getLogger(ApiFilter.class);
	@Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) 
    		throws IOException, ServletException {
        HttpServletResponse res = (HttpServletResponse) response;
        HttpServletRequest req = (HttpServletRequest) request;
        log.debug("Filter: URL" + " called: "+req.getRequestURL().toString());
        String clientIp = req.getHeader("Origin");
        if(clientIp == null) {
        	clientIp = req.getHeader("X-Forwarded-For");
        	if(clientIp == null) {
	        	clientIp = request.getRemoteHost();
	        	log.debug("Got direct request from the following client: " + clientIp);
        	}else {
        		log.debug("Got proxy forwared request from the following client: " + clientIp);
        	}
        }else {
        	log.debug("Got request from the following js client: " + clientIp);
        }        
        res.addHeader("Access-Control-Allow-Origin", clientIp);
        res.addHeader("Access-Control-Allow-Methods", "OPTIONS, GET, POST, DELETE, PUT, PATCH");
        res.addHeader("Access-Control-Allow-Headers", "origin, content-type, accept, authorization");
        res.addHeader("Access-Control-Allow-Credentials", "true");
        res.addHeader("Access-Control-Expose-Headers", "Location, OpenEO-Identifier, OpenEO-Costs");
        chain.doFilter(request, response);
    }

    public void destroy() {}

    public void init(FilterConfig filterConfig) throws ServletException {}
}