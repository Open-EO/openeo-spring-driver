package org.openeo.spring.bearer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.ClaimJwtException;
import io.jsonwebtoken.JwtException;

/**
 * Filters that handles authorizations for incoming HTTP requests
 * based on a received JWT bearer token.
 * 
 * Basic HTTP authentication is assumed (basic// prefix is expected on the token).
 */
@Component
public class JWTAuthorizationFilter extends OncePerRequestFilter {
    
    @Autowired
    ITokenService tokenService;
    
    /** HTTP Bearer scheme id. */
    private static String BEARER_HEADER_PRE = "Bearer "; 
    
    /** Prefix of the HTTP authentication Bearer token header. */
    private static String TOKEN_PREFIX = "basic//";
    
    private static final Logger LOGGER = LogManager.getLogger(JWTAuthorizationFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        
        if (null != authorizationHeader) {
            if (authorizationHeaderIsBearer(authorizationHeader)) {
                if (authBearerHeaderIsInvalid(authorizationHeader)) {
                    throw new JwtException(String.format(
                            "Invalid authorization header. Expected: %s%sTOKEN",
                            BEARER_HEADER_PRE, TOKEN_PREFIX));
                } else {
                    UsernamePasswordAuthenticationToken auth = parseToken(authorizationHeader);
                    if (null != auth) {
                        SecurityContextHolder.getContext().setAuthentication(auth);
                    } else {
                        LOGGER.error("Invalid token received: authentication unsuccessful.");
                    }
                }
            }
        } else {
            LOGGER.debug("No \"Bearer\" token found in the request.");
        }
        
        // do not break the chain!
        filterChain.doFilter(request, response);
    }
    
    /** Tells whether the given HTTP "Authorization" header is a Bearer token. */ 
    private boolean authorizationHeaderIsBearer(String authorizationHeader) {
        return authorizationHeader != null &&
                authorizationHeader.startsWith(BEARER_HEADER_PRE);
    }
    
    /** Tells whether the given HTTP "Authorization" header follows the Bearer scheme. */ 
    private boolean authBearerHeaderIsInvalid(String authorizationHeader) {
        return authorizationHeader == null || (
                authorizationHeader.startsWith(BEARER_HEADER_PRE) &&
                !authorizationHeader.startsWith(BEARER_HEADER_PRE + TOKEN_PREFIX));
    }
    
    /** Deciphers a JWT bearer token attached to a given request header. */ 
    private UsernamePasswordAuthenticationToken parseToken(String authorizationHeader)
    throws ClaimJwtException {
        String prefixedToken = authorizationHeader.replace(BEARER_HEADER_PRE, "");
        String jwtToken = prefixedToken.replaceAll(TOKEN_PREFIX, "");
        
        UserDetails userPrincipal = tokenService.parseToken(jwtToken);
        UsernamePasswordAuthenticationToken auth = null;

        if (null != userPrincipal) {
            List<GrantedAuthority> authorities = new ArrayList<>();
            //        if (userPrincipal.isAdmin()) {
            //            authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN")); // FIXME String
            //        }
            auth = new UsernamePasswordAuthenticationToken(userPrincipal, null, authorities);
        }

        return auth;
    }
}
