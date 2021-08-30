package org.openeo.spring.api;

import org.keycloak.AuthorizationContext;
import org.keycloak.KeycloakSecurityContext;
import java.util.List;

import org.keycloak.representations.AccessToken;
import org.keycloak.representations.idm.authorization.Permission;
import org.springframework.stereotype.Component;

/**
* <p>This is a simple facade to obtain information from authenticated users.
*/
@Component
public class Identity {
	
	

	private final KeycloakSecurityContext keycloakSecurityContext;
	
	public Identity() {
		this.keycloakSecurityContext = new KeycloakSecurityContext();
		
	}
    public Identity(KeycloakSecurityContext keycloakSecurityContext) {
        this.keycloakSecurityContext = keycloakSecurityContext;
    }
    
    
    /**
     * An example on how you can use the {@link AuthorizationContext} to check for permissions granted by Keycloak for a particular user.
     *
     * @param name the name of the resource
     * @return true if user has was granted with a permission for the given resource. Otherwise, false.
     */
    
    public boolean hasResourcePermission(String resourceName) {
    	return getAuthorizationContext().hasResourcePermission(resourceName);
    }
    
//    public boolean hasResourcePermission(String resourceName) {
//    	
//    	for (Permission permission: getPermissions()) {
//    		if (resourceName.equalsIgnoreCase(permission.getResourceName()) || resourceName.equalsIgnoreCase(permission.getResourceId())){
//    			return true;
//    		}
//   	}
//    	return false;
//    }
//      
    
    /**
     * An example on how you can use the {@link AuthorizationContext} to check for permissions granted by Keycloak for a particular scope.
     *
     * @param name the name of the scope
     * @return true if user has was granted with a permission for the given scope. Otherwise, false.
     */
    
    public boolean hasScopePermission(String scopeName) {
    	return getAuthorizationContext().hasScopePermission(scopeName);
    }
    
    
    /**
     * An example on how you can use the {@link AuthorizationContext} to check for permissions granted by Keycloak for a particular resource and scope.
     *
     * @param name the name of the scope and resource
     * @return true if user has was granted with a permission for the given scope. Otherwise, false.
     */
    
    public boolean hasPermission(String resourceName, String scopeName){
    	return getAuthorizationContext().hasPermission(resourceName, scopeName);
    }
    
    
    /**
     * An example on how you can use {@link KeycloakSecurityContext} to obtain information about user's identity.
     *
     * @return the user name
     */
    public String getName() {
        return keycloakSecurityContext.getIdToken().getPreferredUsername();
    }
	
    
    
    /**
     * An example on how you can use the {@link AuthorizationContext} to obtain all permissions granted for a particular user.
     *
     * @return
     */
    public List<Permission> getPermissions() {
        return getAuthorizationContext().getPermissions();
    }
	
    
    
    /**
     * Returns a {@link AuthorizationContext} instance holding all permissions granted for an user. The instance is build based on
     * the permissions returned by Keycloak. 
     * @return
     */
    private AuthorizationContext getAuthorizationContext() {
        return keycloakSecurityContext.getAuthorizationContext();
    }
    
    
    /**
     * check permission
     */    
    public boolean hasPermission(AccessToken accessToken, String resourceName, String scopeName) {
        if (accessToken==null || accessToken.getAuthorization() == null) {
            return false;
        }

        AccessToken.Authorization authorization = accessToken.getAuthorization();

        for (Permission permission : authorization.getPermissions()) {
        	System.out.println("Permisssssss:    " + permission);
            if (resourceName.equalsIgnoreCase(permission.getResourceName()) || resourceName.equalsIgnoreCase(permission.getResourceId())) {
                if (scopeName == null) {
                    return true;
                }

                if (permission.getScopes().contains(scopeName)) {
                    return true;
                }
            }
        }

        return false;
    }
    
    
    
    
    
    
    
    
     
}
