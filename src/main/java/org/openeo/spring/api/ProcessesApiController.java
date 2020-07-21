package org.openeo.spring.api;

import java.util.Optional;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;

import org.openeo.spring.model.Collection;
import org.openeo.spring.model.Error;
import org.openeo.spring.model.Processes;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.NativeWebRequest;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.Authorization;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2020-07-02T08:45:00.334+02:00[Europe/Rome]")
@RestController
@RequestMapping("${openapi.openEO.base-path:}")
public class ProcessesApiController implements ProcessesApi {
    private final NativeWebRequest request;

    @org.springframework.beans.factory.annotation.Autowired
    public ProcessesApiController(NativeWebRequest request) {
        this.request = request;
    }

    @Override
    public Optional<NativeWebRequest> getRequest() {
        return Optional.ofNullable(request);
    }
    
    /**
     * GET /processes : Supported predefined processes
     * The request asks the back-end for available predefined processes and returns detailed process descriptions, including parameters and return values.
     *
     * @param limit This parameter enables pagination for the endpoint and specifies the maximum number of elements that arrays in the top-level object (e.g. jobs or log entries) are allowed to contain. The only exception is the &#x60;links&#x60; array, which MUST NOT be paginated as otherwise the pagination links may be missing ins responses. If the parameter is not provided or empty, all elements are returned.  Pagination is OPTIONAL and back-ends and clients may not support it. Therefore it MUST be implemented in a way that clients not supporting pagination get all resources regardless. Back-ends not supporting  pagination will return all resources.  If the response is paginated, the links array MUST be used to propagate the  links for pagination with pre-defined &#x60;rel&#x60; types. See the links array schema for supported &#x60;rel&#x60; types.  *Note:* Implementations can use all kind of pagination techniques, depending on what is supported best by their infrastructure. So it doesn&#39;t care whether it is page-based, offset-based or uses tokens for pagination. The clients will use whatever is specified in the links with the corresponding &#x60;rel&#x60; types. (optional)
     * @return Formal specification describing the supported predefined processes. (status code 200)
     *         or The request can&#39;t be fulfilled due to an error on client-side, i.e. the request is invalid. The client should not repeat the request without modifications.  The response body SHOULD contain a JSON error object. MUST be any HTTP status code specified in [RFC 7231](https://tools.ietf.org/html/rfc7231#section-6.6). This request MUST respond with HTTP status codes 401 if authorization is required or 403 if the authorization failed or access is forbidden in general to the authenticated user. HTTP status code 404 should be used if the value of a path parameter is invalid.  See also: * [Error Handling](#section/API-Principles/Error-Handling) in the API in general. * [Common Error Codes](errors.json) (status code 400)
     *         or The request can&#39;t be fulfilled due to an error at the back-end. The error is never the client’s fault and therefore it is reasonable for the client to retry the exact same request that triggered this response.  The response body SHOULD contain a JSON error object. MUST be any HTTP status code specified in [RFC 7231](https://tools.ietf.org/html/rfc7231#section-6.6).  See also: * [Error Handling](#section/API-Principles/Error-Handling) in the API in general. * [Common Error Codes](errors.json) (status code 500)
     */
    @Operation(summary = "Supported predefined processes", operationId = "listProcesses", description = "The request asks the back-end for available predefined processes and returns detailed process descriptions, including parameters and return values.", tags={ "Process Discovery", })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "Formal specification describing the supported predefined processes."),
        @ApiResponse(responseCode = "400", description = "The request can't be fulfilled due to an error on client-side, i.e. the request is invalid. The client should not repeat the request without modifications.  The response body SHOULD contain a JSON error object. MUST be any HTTP status code specified in [RFC 7231](https://tools.ietf.org/html/rfc7231#section-6.6). This request MUST respond with HTTP status codes 401 if authorization is required or 403 if the authorization failed or access is forbidden in general to the authenticated user. HTTP status code 404 should be used if the value of a path parameter is invalid.  See also: * [Error Handling](#section/API-Principles/Error-Handling) in the API in general. * [Common Error Codes](errors.json)"),
        @ApiResponse(responseCode = "500", description = "The request can't be fulfilled due to an error at the back-end. The error is never the client’s fault and therefore it is reasonable for the client to retry the exact same request that triggered this response.  The response body SHOULD contain a JSON error object. MUST be any HTTP status code specified in [RFC 7231](https://tools.ietf.org/html/rfc7231#section-6.6).  See also: * [Error Handling](#section/API-Principles/Error-Handling) in the API in general. * [Common Error Codes](errors.json)") })
    @GetMapping(value = "/processes", produces = { "application/json" })
    public ResponseEntity<Processes> listProcesses(@Min(1)@ApiParam(value = "This parameter enables pagination for the endpoint and specifies the maximum number of elements that arrays in the top-level object (e.g. jobs or log entries) are allowed to contain. The only exception is the `links` array, which MUST NOT be paginated as otherwise the pagination links may be missing ins responses. If the parameter is not provided or empty, all elements are returned.  Pagination is OPTIONAL and back-ends and clients may not support it. Therefore it MUST be implemented in a way that clients not supporting pagination get all resources regardless. Back-ends not supporting  pagination will return all resources.  If the response is paginated, the links array MUST be used to propagate the  links for pagination with pre-defined `rel` types. See the links array schema for supported `rel` types.  *Note:* Implementations can use all kind of pagination techniques, depending on what is supported best by their infrastructure. So it doesn't care whether it is page-based, offset-based or uses tokens for pagination. The clients will use whatever is specified in the links with the corresponding `rel` types.") @Valid @RequestParam(value = "limit", required = false) Integer limit) {

    	Processes processesList = new Processes();
    	
//    	getRequest().ifPresent(request -> {
//            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
//                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
//                	String exampleString = "{ \"processes\" : [ \"{}\", \"{}\" ], \"links\" : [ { \"rel\" : \"related\", \"href\" : \"https://example.openeo.org\", \"type\" : \"text/html\", \"title\" : \"openEO\" }, { \"rel\" : \"related\", \"href\" : \"https://example.openeo.org\", \"type\" : \"text/html\", \"title\" : \"openEO\" } ] }";
//                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
//                    
//                }
//            }
//        });
        return new ResponseEntity<Processes>(processesList, HttpStatus.OK);

    }
    
}
