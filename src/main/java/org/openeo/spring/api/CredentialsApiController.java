package org.openeo.spring.api;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
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

    @org.springframework.beans.factory.annotation.Autowired
    public CredentialsApiController(NativeWebRequest request) {
        this.request = request;
    }

    @Override
    public Optional<NativeWebRequest> getRequest() {
        return Optional.ofNullable(request);
    }
    
    @GetMapping(value = "/credentials/oidc")
    @Override
    public RedirectView authenticateOidc(RedirectAttributes attributes) {
    	attributes.addAttribute("attribute", "redirectWithRedirectView");
        return new RedirectView(oidcEndpoint);
    }

}
