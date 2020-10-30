package org.openeo.spring.api;

import java.security.Principal;
import org.keycloak.KeycloakPrincipal;
import org.keycloak.representations.AccessToken;

public class TokenUtil {
	
	public static AccessToken getAccessToken(Principal principal) {
        KeycloakPrincipal kcPrincipal = (KeycloakPrincipal) principal;
        return kcPrincipal.getKeycloakSecurityContext().getToken();
    }
}
