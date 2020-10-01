package org.openeo.spring.api;

import java.util.Map;
import java.util.HashMap;
import java.util.Optional;
import java.util.List;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import org.openeo.spring.model.Link;

import org.openeo.spring.model.UdfRuntime;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.NativeWebRequest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2020-07-02T08:45:00.334+02:00[Europe/Rome]")
@RestController
@RequestMapping("${openapi.openEO.base-path:}")
public class UdfRuntimesApiController implements UdfRuntimesApi {

    private final NativeWebRequest request;

    @org.springframework.beans.factory.annotation.Autowired
    public UdfRuntimesApiController(NativeWebRequest request) {
        this.request = request;
    }

    @Override
    public Optional<NativeWebRequest> getRequest() {
        return Optional.ofNullable(request);
    }
    
    /**
     * GET /udf_runtimes : Supported UDF runtimes
     * Returns the supported runtimes for user-defined functions (UDFs), which includes either the programming languages including version numbers and available libraries including version numbers or docker containers.
     *
     * @return Description of UDF runtime support (status code 200)
     *         or The request can&#39;t be fulfilled due to an error on client-side, i.e. the request is invalid. The client should not repeat the request without modifications.  The response body SHOULD contain a JSON error object. MUST be any HTTP status code specified in [RFC 7231](https://tools.ietf.org/html/rfc7231#section-6.6). This request MUST respond with HTTP status codes 401 if authorization is required or 403 if the authorization failed or access is forbidden in general to the authenticated user. HTTP status code 404 should be used if the value of a path parameter is invalid.  See also: * [Error Handling](#section/API-Principles/Error-Handling) in the API in general. * [Common Error Codes](errors.json) (status code 400)
     *         or The request can&#39;t be fulfilled due to an error at the back-end. The error is never the client’s fault and therefore it is reasonable for the client to retry the exact same request that triggered this response.  The response body SHOULD contain a JSON error object. MUST be any HTTP status code specified in [RFC 7231](https://tools.ietf.org/html/rfc7231#section-6.6).  See also: * [Error Handling](#section/API-Principles/Error-Handling) in the API in general. * [Common Error Codes](errors.json) (status code 500)
     */
    @Operation(summary = "Supported UDF runtimes", operationId = "listUdfRuntimes", description = "Returns the supported runtimes for user-defined functions (UDFs), which includes either the programming languages including version numbers and available libraries including version numbers or docker containers.", tags={ "Capabilities", })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "Description of UDF runtime support"),
        @ApiResponse(responseCode = "400", description = "The request can't be fulfilled due to an error on client-side, i.e. the request is invalid. The client should not repeat the request without modifications.  The response body SHOULD contain a JSON error object. MUST be any HTTP status code specified in [RFC 7231](https://tools.ietf.org/html/rfc7231#section-6.6). This request MUST respond with HTTP status codes 401 if authorization is required or 403 if the authorization failed or access is forbidden in general to the authenticated user. HTTP status code 404 should be used if the value of a path parameter is invalid.  See also: * [Error Handling](#section/API-Principles/Error-Handling) in the API in general. * [Common Error Codes](errors.json)"),
        @ApiResponse(responseCode = "500", description = "The request can't be fulfilled due to an error at the back-end. The error is never the client’s fault and therefore it is reasonable for the client to retry the exact same request that triggered this response.  The response body SHOULD contain a JSON error object. MUST be any HTTP status code specified in [RFC 7231](https://tools.ietf.org/html/rfc7231#section-6.6).  See also: * [Error Handling](#section/API-Principles/Error-Handling) in the API in general. * [Common Error Codes](errors.json)") })
    @GetMapping(value = "/udf_runtimes", produces = { "application/json" })
    public ResponseEntity<Map<String, UdfRuntime>> listUdfRuntimes() {
    	Map<String, UdfRuntime> udfRuntimes = new HashMap<String, UdfRuntime>();
    	
    	
    	UdfRuntime udfPython = new UdfRuntime();
    	List<Link> linksPython = new ArrayList<Link>();
    	Link link1 = new Link();
    	URI url;
		try {
			url = new URI("https://github.com/Open-EO/openeo-udf");
			link1.setHref(url);
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		link1.setRel("about");
    	udfPython.setTitle("Python UDF Environment");
    	udfPython.setType("docker");
    	udfPython.setDescription("Python programming language");
    	udfPython.setDefault("3.6.8");
    	linksPython.add(link1);
    	udfPython.setLinks(linksPython);
    	udfRuntimes.put("python", udfPython);
    	
    	
    	UdfRuntime udfR = new UdfRuntime();
    	udfR.setTitle("R UDF Environment");
    	udfR.setType("docker");
    	udfR.setDefault("3.6.1");
    	udfR.setDescription("R programming language with `Rcpp` and `rmarkdown` extensions installed.");
    	
    	udfRuntimes.put("R", udfR);
    	
        return new ResponseEntity<Map<String, UdfRuntime>>(udfRuntimes, HttpStatus.OK);

    	
    }

}
