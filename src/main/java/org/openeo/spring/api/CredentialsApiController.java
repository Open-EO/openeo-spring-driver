package org.openeo.spring.api;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openeo.spring.model.DefaultOpenIDConnectClient;
import org.openeo.spring.model.Error;
import org.openeo.spring.model.OpenIDConnectProvider;
import org.openeo.spring.model.OpenIDConnectProviders;
import org.openeo.spring.model.DefaultOpenIDConnectClient.GrantTypesEnum;
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
    
    @Value("${org.openeo.oidc.configuration.endpoint}")
	private String oidcEndpoint;
    
    @Value("${keycloak.auth-server-url}")
    private String oidcProvider;
    
    @Value("${org.openeo.oidc.providers.list}")
	Resource oidcProvidersFile;

    @org.springframework.beans.factory.annotation.Autowired
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
    	ObjectMapper mapper = new ObjectMapper();

    	OpenIDConnectProviders providers;
		try {
			providers = mapper.readValue(oidcProvidersFile.getInputStream(), OpenIDConnectProviders.class);
		} catch (IOException e) {
			Error error = new Error();
			error.setCode("500");
			error.setMessage("The list of oidc providers is currently not available!");
			log.error("The list of oidc providers is currently not available!");
			return new ResponseEntity<Error>(error, HttpStatus.INTERNAL_SERVER_ERROR);
		}
   
    	return new ResponseEntity<OpenIDConnectProviders>(providers, HttpStatus.OK);
    }

}
