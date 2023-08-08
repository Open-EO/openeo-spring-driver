package org.openeo.spring.bearer;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.oauth2.server.resource.web.BearerTokenResolver;
import org.springframework.security.oauth2.server.resource.web.DefaultBearerTokenResolver;

import io.jsonwebtoken.JwtException;

/**
 * A JWT Bearer token resolver which allows for prefix(es) "/" in tokens.
 */
public class PrefixedBearerTokenResolver implements BearerTokenResolver {
    
    private final DefaultBearerTokenResolver innerResolver;
    private final String prefixStart;
    
    /**
     * Constructor with a required (start of) token prefix.
     * For instance, a {@code startsWith} value of "oidc/" will
     * make a "oidc/acme/qwerzYYYYYY" token accepted.
     * 
     * @param startsWith
     */
    public PrefixedBearerTokenResolver(String startsWith) {       
        if (null == startsWith) {
            startsWith = "";
        }        
        prefixStart = startsWith;
        innerResolver = new DefaultBearerTokenResolver();
    }
    
    /**
     * Constructor with no specific prefix required. 
     */
    public PrefixedBearerTokenResolver() {
        this(null);
    }

    /**
     * @return The configured (start of) the prefix (an empty string 
     * if any prefix allowed.
     * A token prefix is any string
     * before the last forwards slash in the token hash.
     */
    public String getPrefixStartsWith() {
        return prefixStart;
    }
    
    /**
     * Strips the prefixes from the token {@code "prefix_1\/[prefix_2\/][...]TOKEN"}.
     */
    @Override
    public String resolve(HttpServletRequest request) {
        String pureToken = null;
        String prefixedToken = innerResolver.resolve(request);
        
        if (null != prefixedToken) {
            if (!prefixedToken.startsWith(prefixStart)) {
                throw new JwtException(String.format(
                        "Invalid token prefix. Expected: '%s'. Token: %20s...",
                        prefixStart, prefixedToken));
            }
            pureToken = prefixedToken.substring(prefixedToken.lastIndexOf('/') + 1);
        }
        
        return pureToken;
    }
}
