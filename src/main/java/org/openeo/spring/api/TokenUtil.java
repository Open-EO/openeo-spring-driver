package org.openeo.spring.api;

import java.security.Principal;

import org.keycloak.adapters.OidcKeycloakAccount;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.keycloak.representations.AccessToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;

/**
 * Utilities for fetching user sessions' access tokens.
 */
// TODOs unique method: getAccessToken(Principal p)
public class TokenUtil {
    
    private TokenUtil() {};
	
    /**
     * FEtches the Keycloak (KC) access token of a given user.
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
	public static String getBAAccessToken(Principal principal) {
	    if (null == principal) {
	        return null;
	    }
	    
	    String token = null;
	    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();//? which one?
	    // 1 authentication.getPrincipal() -> instanceof User
	    // 2 get username() then:
	    String givenUser = principal.getName();
	    String loggedUser = .toString();
	    User loggedUser = 
	            
	    if (givenUser.equals(loggedUser)) {
	        token = authentication.getName();
	    } else {
	        // TODO
	        // throw internal error exception? given user != logged user
	        // or just return null?
	    }
	    
	    return token;
	}
}
