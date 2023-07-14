package org.openeo.spring.token;

import org.springframework.security.core.userdetails.UserDetails;

/**
 * Interface for a (bearer) token service.
 *
 * @see https://datatracker.ietf.org/doc/html/rfc6750
 */
public interface ITokenService {

    /**
     * Generates a token hash.
     * 
     * @param user the logged user.
     */
    String generateToken(UserDetails user);

    /**
     * Parses a token.
     * 
     * @param token the received token. 
     */
    UserDetails parseToken(String token);
    
    //String extractToken(HttpRequest or HttpResponse) "Authorization: Bearer basic//$TOKEN"
}
