package org.openeo.spring;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.keycloak.adapters.springsecurity.filter.KeycloakAuthenticationProcessingFilter;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;

public class OpenEOKeycloakAuthenticationProcessingFilter extends KeycloakAuthenticationProcessingFilter {

	private final Logger log = LogManager.getLogger(OpenEOKeycloakAuthenticationProcessingFilter.class);

	public OpenEOKeycloakAuthenticationProcessingFilter(AuthenticationManager authenticationManager) {
		super(authenticationManager);
	}

	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
			throws AuthenticationException, IOException, ServletException {

		log.debug("OpenEOKeycloakAuthenticationProcessingFilter...");

		ModifyAuthTokenHeaderRequestWrapper tokenWrapper = new ModifyAuthTokenHeaderRequestWrapper(request);
		String token = request.getHeader(AUTHORIZATION_HEADER);		
		if(token != null && token.contains("/")) {
			log.trace("The following token was present: " + token);
			token = "Bearer " + token.substring(token.lastIndexOf("/")+1);
			log.trace("The token was change to: " + token);
			tokenWrapper.addHeader(AUTHORIZATION_HEADER, token);
		}
		if (SecurityContextHolder.getContext().getAuthentication() != null
				&& SecurityContextHolder.getContext().getAuthentication().isAuthenticated()) {
			log.debug("OpenEOKeycloakAuthenticationProcessingFilter. We are already authenticated");
			return SecurityContextHolder.getContext().getAuthentication();
		}

		return super.attemptAuthentication(tokenWrapper, response);
	}

}
