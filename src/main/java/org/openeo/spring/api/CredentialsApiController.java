package org.openeo.spring.api;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

import org.openeo.spring.model.Link;
import org.openeo.spring.model.OpenIDProvider;
import org.openeo.spring.model.OpenIDProviders;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2020-07-02T08:45:00.334+02:00[Europe/Rome]")
@Controller
@RequestMapping("${openapi.openEO.base-path:}")
public class CredentialsApiController implements CredentialsApi {

    private final NativeWebRequest request;
    
    @Value("${org.openeo.oidc.configuration.endpoint}")
	private String oidcEndpoint;
    
    @Value("{keycloak.auth-server-url}")
    private String oidcProvider;

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
    public ResponseEntity<OpenIDProviders>  authenticateOidc() {
    	OpenIDProviders providers = new OpenIDProviders();
    	OpenIDProvider provider = new OpenIDProvider();
    	provider.setId("Eurac_EDP_Keycloak");
    	try {
			provider.setIssuer(new URI(oidcEndpoint));
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	Link providerUrl = new Link();
		try {
			providerUrl.setHref(new URI(oidcProvider));
		} catch (URISyntaxException e) {
		}
		provider.addLinksItem(providerUrl);
    	provider.setTitle("Eurac EDP Keycloak");
    	provider.setDescription("Keycloak server linking to the eurac active directory. This service can be used with Eurac and general MS accounts");
    	providers.addProvidersItem(provider);
    	
    	return new ResponseEntity<OpenIDProviders>(providers, HttpStatus.OK);
    }

}
