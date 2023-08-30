package org.openeo.spring.bearer;

import java.security.Principal;

import org.keycloak.representations.AccessToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.BearerTokenAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

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
        
        if (principal instanceof JwtAuthenticationToken) {
            token = TokenUtil.getKCAccessToken((JwtAuthenticationToken) principal);
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
	static AccessToken getKCAccessToken(JwtAuthenticationToken principal) {
	    if (null == principal) {
	        return null;
	    }
	    
	    AccessToken token = new AccessToken();
	    
	    // TODO is it always a Jwt object?
	    Jwt jwt = (Jwt) principal.getPrincipal();

	    token.id(jwt.getId());
//	    token.setName(jwt.getSubject()); -> gives the token hash
	    token.setName(jwt.getClaimAsString("name"));
	    token.setAccessTokenHash(jwt.getTokenValue());
	    
	    if (null != jwt.getAudience()) {
	        jwt.getAudience().forEach(
	                (x) -> token.addAudience(x));
	    }
	    
	    if (null != jwt.getIssuedAt()) {
	        token.iat(jwt.getIssuedAt().toEpochMilli());
	    }
	    token.exp(jwt.getExpiresAt().toEpochMilli());
	    
	    // other claims
	    jwt.getClaims().forEach((
	            k,v) -> token.setOtherClaims(k, v)
	    );
	    // more needed ? certficates etc. just a format conversion, we might abandon AccessToken class
	    
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
	    
	    AccessToken token = null;
	    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	    
	    if (null != auth) {
	        if (auth instanceof BearerTokenAuthenticationToken) {
	            token = TokenUtil.getBearerAccessToken((BearerTokenAuthenticationToken) auth, tokenService);
	        } else if (!auth.isAuthenticated()) {
	            // we reach this point e.g. with mock MVC users in tests
	            throw new InternalError("Could not fetch token from " + auth.getClass()); 
	        }
	    }

	    return token;
	}
	
	/**
	 * Fetches and decodes a bearer token. 
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
