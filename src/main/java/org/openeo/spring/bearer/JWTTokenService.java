package org.openeo.spring.bearer;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.Date;

import javax.crypto.SecretKey;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
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
    public UserDetails parseToken(String token) throws JwtException {
        
        byte[] secretBytes = jwtSecret.getBytes();
        UserDetails user = null;

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
                user = udService.loadUserByUsername(username);
            }
        } catch (JwtException ex) { // TODO handle via registered runtime exceptions handler:
//                ExpiredJwtException | UnsupportedJwtException |
//                MalformedJwtException | SignatureException | IllegalArgumentException ex) {
            LOGGER.error("Illegal or expired token received.", ex);
            throw ex;
        }

        return user;
    }
    
    // JWT labels
    private static final String ID_CLAIM = "id";
    private static final String USER_CLAIM = "sub";
    private static final String IS_ADMIN_CLAIM = "admin";
}
