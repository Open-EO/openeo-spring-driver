package org.openeo.spring.bearer;

import java.time.temporal.TemporalUnit;

import org.springframework.security.core.userdetails.UserDetails;

import io.jsonwebtoken.ClaimJwtException;

/**
 * Interface for a (bearer) token service.
 *
 * @see https://datatracker.ietf.org/doc/html/rfc6750
 */
public interface ITokenService {

    /**
     * Generates a token hash with default expiration time.
     * 
     * @param user the logged user.
     * @return the hashed token
     */
    String generateToken(UserDetails user);    

    /**
     * Generates a token with the given arbitrary expiration time.
     * 
     * @param user the logged user
     * @param expUnits how many units of time (*{@code uom}) from now to set the expiration
     * @param uom the unit of measure of {@code expUnits}
     * @return the hashed token
     */
    String generateToken(UserDetails user, int expUnits, TemporalUnit uom);

    /**
     * Parses a token and its claims.
     * 
     * @param token the received token.
     * @throws ClaimJwtException
     */
    UserDetails parseToken(String token) throws ClaimJwtException;
}
