package org.openeo.spring.token;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
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
    
    /** Algorithm used to encode the token. */
    private static final SignatureAlgorithm SA = SignatureAlgorithm.HS512;

    @Override
    public String generateToken(UserDetails user) {
        
        Instant expirationTime = Instant.now().plus(jwtExpMinutes, ChronoUnit.MINUTES);
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
    public UserDetails parseToken(String token) {
        byte[] secretBytes = jwtSecret.getBytes();

        Jws<Claims> jwsClaims = Jwts.parserBuilder()
                .setSigningKey(secretBytes)
                .build()
                .parseClaimsJws(token);

        String username = jwsClaims.getBody().getSubject();
//        Integer userId = jwsClaims.getBody().get(ID_CLAIM, Integer.class);
//        boolean isAdmin = jwsClaims.getBody().get(IS_ADMIN_CLAIM, Boolean.class);

        return User.builder().username(username).build();
    }
    
    // JWT labels
    private static final String ID_CLAIM = "id";
    private static final String USER_CLAIM = "sub";
    private static final String IS_ADMIN_CLAIM = "admin";
}
