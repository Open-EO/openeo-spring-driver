package org.openeo.spring;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import javax.servlet.Filter;

public class TokenInterceptorFilter implements Filter {
	
	private final Logger log = LogManager.getLogger(TokenInterceptorFilter.class);

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		
		HttpServletResponse res = (HttpServletResponse) response;
        HttpServletRequest req = (HttpServletRequest) request;
		String token = req.getHeader("Authorization");
		log.debug("Tokeninterceptor is currently filtering!");
		if(token != null) {
			log.trace("The following token was present: " + token);
			token = "Bearer " + token.substring(token.lastIndexOf("/")+1);
			res.setHeader("Authorization", token);
			log.trace("The token was change to: " + token);
		}else {
			log.trace("No token found!");
		}
		
		filterChain.doFilter(req, res);	
		

	}

}
