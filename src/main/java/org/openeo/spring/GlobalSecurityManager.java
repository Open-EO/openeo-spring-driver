package org.openeo.spring;

/**
 * Global authentication and authorization manager.
 * 
 * TODO docs
 */
public /*final*/ class GlobalSecurityManager {
    
    /** API resources that do not require authentication. */
    public static final String[] NOAUTH_API_RESOURCES = new String[] {
            "/",
            "/conformance",
            "/file_formats",
            "/.well-known/**"};
    
    public static final String BASIC_AUTH_API_RESOURCE = "/credentials/basic";
    public static final String OIDC_AUTH_API_RESOURCE = "/credentials/oidc";

}