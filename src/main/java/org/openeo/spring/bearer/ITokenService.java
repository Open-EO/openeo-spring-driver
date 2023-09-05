package org.openeo.spring.bearer;

import java.time.temporal.TemporalUnit;

import org.keycloak.representations.AccessToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.server.resource.BearerTokenAuthenticationToken;

import io.jsonwebtoken.ClaimJwtException;
import io.jsonwebtoken.JwtException;

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
     * Parses a token and its claims, and integrates the user details.
     * 
     * @param token the received token.
     * @return the authentication object this token refers to
     * @throws ClaimJwtException
     * @see {@link SecurityContextHolder#setContext(org.springframework.security.core.context.SecurityContext)}
     */
    BearerTokenAuthenticationToken parseToken(String token) throws ClaimJwtException;

    /**
     * Decode a token hash to a Java object, assuming we are the issuers.
     * 
     * @param tokenHash
     * @return the decoded token.
     * @throws JwtException
     */
    AccessToken decodeToken(String tokenHash) throws JwtException;
}
