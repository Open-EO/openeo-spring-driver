package org.openeo.spring.api;

import static org.junit.jupiter.api.Assertions.assertFalse;

import java.io.IOException;
import java.security.Principal;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openeo.spring.model.Error;
import org.openeo.spring.model.HTTPBasicAccessToken;
import org.openeo.spring.model.OpenIDConnectProviders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.NativeWebRequest;

import com.fasterxml.jackson.databind.ObjectMapper;

@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2020-07-02T08:45:00.334+02:00[Europe/Rome]")
@Controller
@RequestMapping("${openapi.openEO.base-path:}")
public class CredentialsApiController implements CredentialsApi {

    private final NativeWebRequest request;
    
    private final Logger log = LogManager.getLogger(CredentialsApiController.class);
   
    @Value("${org.openeo.oidc.providers.list}")
	private Resource oidcProvidersFile;

    @Value("${spring.security.enable-basic}")
    boolean enableBasicAuth;
    
    @Value("${spring.security.enable-keycloak}")
    boolean enableKeycloakAuth;

    @Autowired
    public CredentialsApiController(NativeWebRequest request) {
        this.request = request;
    }
    
    @Override
    public Optional<NativeWebRequest> getRequest() {
        return Optional.ofNullable(request);
    }
    
    @GetMapping(value = "/credentials/oidc", produces = { "application/json" })
    @Override
    public ResponseEntity<?>  authenticateOidc() {
        ResponseEntity<?> resp;    	
    	
		try {
		    OpenIDConnectProviders providers = new OpenIDConnectProviders();
		    if (enableKeycloakAuth) {		        
		        log.debug(oidcProvidersFile.getFilename());
		        ObjectMapper mapper = new ObjectMapper();
		        providers = mapper.readValue(oidcProvidersFile.getInputStream(), OpenIDConnectProviders.class);
		        assertFalse(providers.getProviders().isEmpty());
		        resp = ResponseEntity.ok(providers);
		    } else {
		        log.debug("OIDC authentication is disabled.");
		        resp = ApiUtil.errorResponse(HttpStatus.NOT_IMPLEMENTED,
		                "OIDC authentication is not enabled.");
		    }
		} catch (IOException e) {
			log.error("The list of oidc providers is currently not available! " + e.getMessage());
			StringBuilder builder = new StringBuilder();
			for (StackTraceElement element : e.getStackTrace()) {
				builder.append(element.toString() + "\n");
			}
			log.error(builder.toString());
			resp = ApiUtil.errorResponse(HttpStatus.INTERNAL_SERVER_ERROR,
			        "The list of oidc providers is currently not available.");
		}
   
    	return resp;
    }

    @GetMapping(value = "/credentials/basic", produces = { "application/json" })
    @Override
    // FIXME handle errors elsewhere and keep HTTPBasicAccessToken response type?
    public ResponseEntity</*HTTPBasicAccessToken*/?> authenticateBasic() {
        ResponseEntity<?> resp;
        
        if (enableBasicAuth) {
            Principal principal = request.getUserPrincipal();
            
            if (null != principal) {
                String username = principal.getName();
                String token = TokenUtil.getCurrentBAAccessToken(request.getUserPrincipal());
                log.debug("Access token for user {}: {}", username, token);
                resp = ResponseEntity
                        .ok(new HTTPBasicAccessToken()
                        .accessToken(token));
                
            } else {
                resp = ApiUtil.errorResponse(HttpStatus.UNAUTHORIZED,
                        "Basic Authentication header required.");
            }
        } else {
            resp = ApiUtil.errorResponse(HttpStatus.NOT_IMPLEMENTED,
                    "Basic authentication mechanism not supported by the server.");
        }
        
        return resp;

        /**TODO**/
        // token: https://github.com/Open-EO/openeo-wcps-driver/tree/master/src/main/java/eu/openeo/backend/auth/filter
        // also: https://github.com/Open-EO/openeo-wcps-driver/blob/master/src/main/java/eu/openeo/api/impl/CredentialsApiServiceImpl.java
    }
}
