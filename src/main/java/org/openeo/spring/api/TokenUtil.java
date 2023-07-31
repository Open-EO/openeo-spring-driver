package org.openeo.spring.api;

import java.security.Principal;

import org.keycloak.adapters.OidcKeycloakAccount;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.keycloak.representations.AccessToken;
import org.openeo.spring.bearer.ITokenService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.BearerTokenAuthenticationToken;

/**
 * Utilities for fetching user sessions' access tokens.
 */
public class TokenUtil {
    
    private TokenUtil() {};
    
    /**
     * Fetches the access/bearer token of a user.
     * @param principal the authenticated user
     * @param service the service that is used to encode/decode Bearer tokens
     *        (required for users on Basic Authentication, not required for OIDC) 
     */
    public static AccessToken getAccessToken(Principal principal, ITokenService service) {
        if (null == principal) {
            return null;
        }
        
        AccessToken token = null;
        
        if (principal instanceof KeycloakAuthenticationToken) {
            token = TokenUtil.getKCAccessToken((KeycloakAuthenticationToken) principal);
        }
        
        else if (principal instanceof UsernamePasswordAuthenticationToken) {
            token = TokenUtil.getBAAccessToken((UsernamePasswordAuthenticationToken) principal, service);
        }
        
        else if (principal instanceof BearerTokenAuthenticationToken) {
            token = TokenUtil.getBearerAccessToken((BearerTokenAuthenticationToken) principal, service);
        }
        
        return token;
    }
	
    /**
     * Fetches the Keycloak (KC) access token of a given user.
     * @param principal the user asking for the token
     * @return the access token; {@code null} when not found (or with {@code null} input)  
     */
	static AccessToken getKCAccessToken(KeycloakAuthenticationToken principal) {
	    if (null == principal) {
	        return null;
	    }
	    OidcKeycloakAccount account = principal.getAccount(); 
	    AccessToken token = account.getKeycloakSecurityContext().getToken();
	    
	    return token;
    }
	
	/**
	 * Fetches the Basic-Authentication (BA) access token of a given user. 
	 * @param principal the user asking for the token
	 * @param tokenService
	 * @return the access token; {@code null} when not found (or with {@code null} input)  
	 */
	static AccessToken getBAAccessToken(UsernamePasswordAuthenticationToken principal,
	        ITokenService tokenService) {
	    if (null == principal) {
	        return null;
	    }
	    if (null == tokenService) {
	        throw new InternalError("ITokenService required to fetch Bearer token.");
	    }    
	    
	    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	    AccessToken token = null;

        if (auth instanceof BearerTokenAuthenticationToken) {
            token = TokenUtil.getBearerAccessToken((BearerTokenAuthenticationToken) auth, tokenService);
//	    } else {
//	        throw new InternalError("Expected BearerTokenAuthenticationToken, got: " + auth.getClass()); 
	    }

	    return token;
	}
	
	/**
	 * FEtches and decodes a bearer token. 
	 * @param principal
	 * @param tokenService
	 * @return
	 */
	static AccessToken getBearerAccessToken(BearerTokenAuthenticationToken principal,
	        ITokenService tokenService) {

	    String tokenHash = null;
	    AccessToken token = null;

	    tokenHash = principal.getToken();                   
	    token = tokenService.decodeToken(tokenHash);

	    return token;
	}
}
