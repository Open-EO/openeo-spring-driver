package org.openeo.spring.api;

import java.util.HashSet;

import org.keycloak.authorization.client.AuthzClient;
import org.keycloak.authorization.client.resource.ProtectedResource;
import org.keycloak.representations.idm.authorization.ResourceOwnerRepresentation;
import org.keycloak.representations.idm.authorization.ResourceRepresentation;
import org.keycloak.representations.idm.authorization.ScopeRepresentation;
import org.openeo.spring.model.Job;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import java.util.List;



@Component
public class AuthzService {
	
    static final String SCOPE_VIEW = "urn:openEO:scopes:view";
    static final String SCOPE_VIEW_DETAIL = "urn:openEO:scopes:view-detail";
    static final String SCOPE_DELETE = "urn:openEO:scopes:delete";
    
    
    public ResponseEntity<?> createProtectedResource(Job job) {
    	
    	try {
    		//create a list and add defined keycloak's scopes in it
            HashSet<ScopeRepresentation> scopes = new HashSet<>();
            
            scopes.add(new ScopeRepresentation(SCOPE_VIEW));
            scopes.add(new ScopeRepresentation(SCOPE_VIEW_DETAIL));
            scopes.add(new ScopeRepresentation(SCOPE_DELETE));
            
          //consider each job as a resource and create it in the keycloak
            ResourceRepresentation resourceRepresentation = new ResourceRepresentation(job.getTitle(), scopes,
                    "/jobs/" + job.getId(), "urn:openEO:resources:jobs");
            
            
          //set resource Owner
            ResourceOwnerRepresentation resourceOwner = new ResourceOwnerRepresentation();
//            resourceOwner.setId(picture.getOwner().getId());
            resourceOwner.setName(job.getOwnerPrincipal());
            
          //set Owner in keycloak
            resourceRepresentation.setOwner(resourceOwner);
            resourceRepresentation.setOwnerManagedAccess(true);
            
            
            //create a new instance based on the configuration defined in keycloak.json
            AuthzClient authzClient = AuthzClient.create();
            
            ProtectedResource resourceClient = authzClient.protection().resource();
            ResourceRepresentation existingResource = resourceClient.findByName(resourceRepresentation.getName());
            
            if (existingResource != null) {
                resourceClient.delete(existingResource.getId());
            }
            
            //create a resource on the server
            resourceClient.create(resourceRepresentation);
           
	
    	}catch (Exception e) {
                throw new RuntimeException("Could not register protected resource", e);		
    	}
    	  	
    	return new ResponseEntity<>(HttpStatus.OK);   	
    }
    
    
    
    
    public ResponseEntity<?> deleteProtectedResource(Job job) {
        String uri = "/jobs/" + job.getId();

        try {
            AuthzClient authzClient = AuthzClient.create();
            ProtectedResource protectedResource = authzClient.protection().resource();
            List<ResourceRepresentation> search = protectedResource.findByUri(uri);

            if (search.isEmpty()) {
                throw new RuntimeException("Could not find protected resource with URI [" + uri + "]");
            }

            protectedResource.delete(search.get(0).getId());

        } catch (Exception e) {
            throw new RuntimeException("Could not search protected resource", e);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }
	

}
