package org.openeo.spring.api;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openeo.spring.model.DefaultOpenIDConnectClient;
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
    public ResponseEntity<OpenIDConnectProviders>  authenticateOidc() {
    	ObjectMapper mapper = new ObjectMapper();
    	OpenIDConnectProviders providers = new OpenIDConnectProviders();
    	OpenIDConnectProvider provider = new OpenIDConnectProvider();
    	DefaultOpenIDConnectClient defaultClient =  new DefaultOpenIDConnectClient();
    	defaultClient.addGrantTypesItem(GrantTypesEnum.AUTHORIZATION_CODE_PKCE);
    	defaultClient.addGrantTypesItem(GrantTypesEnum.REFRESH_TOKEN);
    	defaultClient.id("openEO_TEST");    	
    	provider.setId("Eurac_EDP_Keycloak");
    	provider.addScopesItem("email");
    	provider.addScopesItem("profile");
    	provider.addScopesItem("roles");
    	provider.addScopesItem("web-origins");
    	provider.addScopesItem("address");
    	provider.addScopesItem("microprofile-jwt");
    	provider.addScopesItem("offline_access");
    	provider.addScopesItem("phone");
    	try {
    		defaultClient.addRedirectUrlsItem(new URI("https://editor.openeo.org/"));
	    	defaultClient.addRedirectUrlsItem(new URI("http://localhost:1410/*"));
	    	defaultClient.addRedirectUrlsItem(new URI("https://10.8.244.204:9443/editor"));
	    	defaultClient.addRedirectUrlsItem(new URI("https://10.8.244.194:8443/*"));
			provider.setIssuer(new URI(oidcEndpoint));
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	provider.addDefaultClientsItem(defaultClient);
    	provider.setTitle("Eurac EDP Keycloak");
    	provider.setDescription("Keycloak server linking to the eurac active directory. This service can be used with Eurac and general MS accounts");
    	providers.addProvidersItem(provider);
    	
    	return new ResponseEntity<OpenIDConnectProviders>(providers, HttpStatus.OK);
    }

}
