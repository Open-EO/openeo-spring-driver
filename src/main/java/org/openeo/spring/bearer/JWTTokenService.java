package org.openeo.spring.bearer;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.Date;

import javax.crypto.SecretKey;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.keycloak.representations.AccessToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.server.resource.BearerTokenAuthenticationToken;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

/**
 * JWT token management service.
 */
//FIXME use org.springframework.security.core.token.TokenService ?
@Component
public class JWTTokenService implements ITokenService {
    
    @Value("${jwt.secret}")
    private String jwtSecret;
    
    @Value("${jwt.issuer}")
    private String jwtIssuer;
    
    @Value("${jwt.type}")
    private String jwtType;
    
    @Value("${jwt.audience}")
    private String jwtAudience;
    
    @Value("${jwt.exp-minutes}")
    private int jwtExpMinutes;
    
    @Autowired
    private UserDetailsService udService;
    
    /** Algorithm used to encode the token. */
    private static final SignatureAlgorithm SA = SignatureAlgorithm.HS512;
    
    private static final Logger LOGGER = LogManager.getLogger(JWTTokenService.class);

    @Override
    public String generateToken(UserDetails user) {
        return generateToken(user, jwtExpMinutes, ChronoUnit.MINUTES);
    }
    
    @Override
    public String generateToken(UserDetails user, int expUnits, TemporalUnit uom) {
        
        Instant expirationTime = Instant.now().plus(expUnits, uom);
        Date expirationDate = Date.from(expirationTime);

        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes());

        String compactTokenString = Jwts.builder()
//                .claim(ID_CLAIM, user.getId())
//                .claim(IS_ADMIN_CLAIM, user.isAdmin())
                .setIssuedAt(Date.from(Instant.now()))
                .setExpiration(expirationDate)
                .setSubject(user.getUsername())
                .signWith(key, SA)
                .setIssuer(jwtIssuer)
                .setAudience(jwtAudience)
                .setHeaderParam("typ", jwtType)
                .compact();

        return compactTokenString;
    }

    @Override
    public BearerTokenAuthenticationToken parseToken(String token) throws JwtException {
        
        byte[] secretBytes = jwtSecret.getBytes();
        BearerTokenAuthenticationToken auth = null;

        try {
            Jws<Claims> jwsClaims = Jwts.parserBuilder()
                    .setSigningKey(secretBytes)
                    .requireIssuer(jwtIssuer)
                    .requireAudience(jwtAudience)
                    .setAllowedClockSkewSeconds(0)
                    .build()
                    .parseClaimsJws(token);

            String username = jwsClaims.getBody().getSubject();
            //        Integer userId = jwsClaims.getBody().get(ID_CLAIM, Integer.class);
            //        boolean isAdmin = jwsClaims.getBody().get(IS_ADMIN_CLAIM, Boolean.class);
            if (null != username) {
                try {
                    UserDetails user = udService.loadUserByUsername(username);
                    auth = new BearerTokenAuthenticationToken(token);
                    auth.setDetails(user);
                    auth.setAuthenticated(true);
                } catch (UsernameNotFoundException e) {
                    throw new JwtException("No user found for " + username);
                }
            } else {
                throw new MalformedJwtException("No username in token subject.");
            }
        } catch (JwtException ex) { // TODO handle via registered runtime exceptions handler:
            //                ExpiredJwtException | UnsupportedJwtException |
            //                MalformedJwtException | SignatureException | IllegalArgumentException ex) {
            LOGGER.error("Illegal or expired token received.", ex);
            throw ex;
        }

        return auth;
    }
    
    @Override
    public AccessToken decodeToken(String tokenHash) throws JwtException {
        
        byte[] secretBytes = jwtSecret.getBytes();      
        AccessToken token = new AccessToken();

        try {
            Jws<Claims> jwsClaims = Jwts.parserBuilder()
                    .setSigningKey(secretBytes)
                    .requireIssuer(jwtIssuer)
                    .requireAudience(jwtAudience)
                    .setAllowedClockSkewSeconds(0)
                    .build()
                    .parseClaimsJws(tokenHash);
            
            Date issued = jwsClaims.getBody().getIssuedAt();
            Date expires = jwsClaims.getBody().getExpiration();
            long expSeconds = (expires.getTime() - issued.getTime()) / 1000;
            
            token.exp(expSeconds)
                 .addAudience(jwsClaims.getBody().getAudience())
                 .issuer(jwsClaims.getBody().getIssuer())
                 .id(tokenHash);
            
            token.setName(jwsClaims.getBody().getSubject());
            token.setPreferredUsername(token.getName());
            
        } catch (JwtException ex) {
            LOGGER.error("Illegal or expired token received.", ex);
            throw ex;
        }
        
        return token;
    }
    
    // JWT labels
    private static final String ID_CLAIM = "id";
    private static final String USER_CLAIM = "sub";
    private static final String IS_ADMIN_CLAIM = "admin";
}
