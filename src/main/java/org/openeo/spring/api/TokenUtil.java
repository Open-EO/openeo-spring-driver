package org.openeo.spring.api;

import java.security.Principal;

import org.keycloak.adapters.OidcKeycloakAccount;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.keycloak.representations.AccessToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.BearerTokenAuthenticationToken;

/**
 * Utilities for fetching user sessions' access tokens.
 */
// TODOs unique method: getAccessToken(Principal p)
public class TokenUtil {
    
    private TokenUtil() {};
	
    /**
     * Fetches the Keycloak (KC) access token of a given user.
     * @param principal the user asking for the token
     * @return the access token; {@code null} when not found (or with {@code null} input)  
     */
	public static AccessToken getKCAccessToken(Principal principal) {
	    if (null == principal) {
	        return null;
	    }
	    if (!(principal instanceof KeycloakAuthenticationToken)) {
	        return null;
	    }
	    
	    KeycloakAuthenticationToken kcPrincipal = (KeycloakAuthenticationToken) principal;
	    OidcKeycloakAccount account = kcPrincipal.getAccount(); 
	    AccessToken token = account.getKeycloakSecurityContext().getToken();
	    
	    return token;
    }
	
	/**
	 * Fetches the Basic-Authentication (BA) access token of a given user. 
	 * @param principal the user asking for the token
	 * @return the access token; {@code null} when not found (or with {@code null} input)  
	 */
	public static String getCurrentBAAccessToken(Principal principal) {
	    if (null == principal) {
	        return null;
	    }
	    
	    String givenUsername = principal.getName();
	    String token = null;
	    
	    Authentication auth = SecurityContextHolder.getContext().getAuthentication();//? which one?

        if (auth instanceof BearerTokenAuthenticationToken) {
            BearerTokenAuthenticationToken tokenAuth = (BearerTokenAuthenticationToken) auth;
	        String loggedUsername = tokenAuth.getName();

	        if (givenUsername.equals(loggedUsername)) {
	                token = tokenAuth.getToken();
	        } else {
	            // TODO
	            // throw internal error exception? given user != logged user
	            // or just return null?
	        }
	    } else {
	        // TODO as above 
	    }

	    return token;
	}
}
