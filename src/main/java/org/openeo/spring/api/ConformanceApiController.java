package org.openeo.spring.api;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

import org.openeo.spring.model.OGCConformanceClasses;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.NativeWebRequest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2020-07-02T08:45:00.334+02:00[Europe/Rome]")
@Controller
@RequestMapping("${openapi.openEO.base-path:}")
public class ConformanceApiController implements ConformanceApi {

    private final NativeWebRequest request;

    @org.springframework.beans.factory.annotation.Autowired
    public ConformanceApiController(NativeWebRequest request) {
        this.request = request;
    }

    @Override
    public Optional<NativeWebRequest> getRequest() {
        return Optional.ofNullable(request);       
    }
    
    /**
     * GET /conformance : OGC specifications this API conforms to
     * A list of all conformance classes specified in OGC standards that the server conforms to. This endpoint is only required if full compliance with OGC API standards is desired. Therefore, openEO back-ends may implement it for compatibility with OGC API clients and openEO clients don&#39;t need to request it.
     *
     * @return The URIs of all conformance classes supported by the server. (status code 200)
     *         or The request can&#39;t be fulfilled due to an error at the back-end. The error is never the client’s fault and therefore it is reasonable for the client to retry the exact same request that triggered this response.  The response body SHOULD contain a JSON error object. MUST be any HTTP status code specified in [RFC 7231](https://tools.ietf.org/html/rfc7231#section-6.6).  See also: * [Error Handling](#section/API-Principles/Error-Handling) in the API in general. * [Common Error Codes](errors.json) (status code 500)
     */
    @Operation(summary = "OGC specifications this API conforms to", operationId = "conformance", description = "A list of all conformance classes specified in OGC standards that the server conforms to. This endpoint is only required if full compliance with OGC API standards is desired. Therefore, openEO back-ends may implement it for compatibility with OGC API clients and openEO clients don't need to request it.", tags={ "Capabilities", })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "The URIs of all conformance classes supported by the server."),
        @ApiResponse(responseCode = "500", description = "The request can't be fulfilled due to an error at the back-end. The error is never the client’s fault and therefore it is reasonable for the client to retry the exact same request that triggered this response.  The response body SHOULD contain a JSON error object. MUST be any HTTP status code specified in [RFC 7231](https://tools.ietf.org/html/rfc7231#section-6.6).  See also: * [Error Handling](#section/API-Principles/Error-Handling) in the API in general. * [Common Error Codes](errors.json)") })
    @GetMapping(value = "/conformance", produces = { "application/json" } )
    public ResponseEntity<OGCConformanceClasses> conformance() {
        
    	OGCConformanceClasses conformance = new OGCConformanceClasses();
    	
    	try {
			conformance.addConformsToItem(new URI (""));
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
        return new ResponseEntity<OGCConformanceClasses>(conformance, HttpStatus.OK);

    }

}
