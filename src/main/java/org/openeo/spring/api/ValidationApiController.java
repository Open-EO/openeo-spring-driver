package org.openeo.spring.api;

import java.util.Optional;

import javax.validation.Valid;

import org.openeo.spring.model.Error;
import org.openeo.spring.model.ValidationResult;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.request.NativeWebRequest;

//TODO update dependencies to V3
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Authorization;
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2020-07-02T08:45:00.334+02:00[Europe/Rome]")
@Controller
@RequestMapping("${openapi.openEO.base-path:/api/v1.0}")
public class ValidationApiController implements ValidationApi {

    private final NativeWebRequest request;

    @org.springframework.beans.factory.annotation.Autowired
    public ValidationApiController(NativeWebRequest request) {
        this.request = request;
    }

    @Override
    public Optional<NativeWebRequest> getRequest() {
        return Optional.ofNullable(request);
    }

    @ApiOperation(value = "Validate a user-defined process (graph)", nickname = "validateCustomProcess", notes = "Validates a user-defined process without executing it. A user-defined process is considered valid unless the `errors` array in the response contains at least one error.  Checks whether the user-defined process is schematically correct and the processes are supported by the back-end. It MUST also checks the parameter values against the schema, but checking whether the values are adequate in the context of data is OPTIONAL. For example, a non-existing band name may get rejected only by a few back-ends.  Errors that usually occur during processing MAY NOT get reported, e.g. if a referenced file is accessible at the time of execution.  Back-ends can either report all errors at once or stop the validation once they found the first error.   Please note that a validation always returns with HTTP status code 200. Error codes in the 4xx and 5xx ranges MUST be returned only when the general validation request is invalid (e.g. server is busy or properties in the request body are missing), but never if an error was found during validation of the user-defined process (e.g. an unsupported process).", response = ValidationResult.class, authorizations = {
        @Authorization(value = "Bearer")
    }, tags={ "User-Defined Processes", })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "Returns the validation result as a list of errors. An empty list indicates a successful validation.", response = ValidationResult.class),
        @ApiResponse(code = 400, message = "The request can't be fulfilled due to an error on client-side, i.e. the request is invalid. The client should not repeat the request without modifications.  The response body SHOULD contain a JSON error object. MUST be any HTTP status code specified in [RFC 7231](https://tools.ietf.org/html/rfc7231#section-6.6). This request MUST respond with HTTP status codes 401 if authorization is required or 403 if the authorization failed or access is forbidden in general to the authenticated user. HTTP status code 404 should be used if the value of a path parameter is invalid.  See also: * [Error Handling](#section/API-Principles/Error-Handling) in the API in general. * [Common Error Codes](errors.json)", response = Error.class),
        @ApiResponse(code = 500, message = "The request can't be fulfilled due to an error at the back-end. The error is never the client’s fault and therefore it is reasonable for the client to retry the exact same request that triggered this response.  The response body SHOULD contain a JSON error object. MUST be any HTTP status code specified in [RFC 7231](https://tools.ietf.org/html/rfc7231#section-6.6).  See also: * [Error Handling](#section/API-Principles/Error-Handling) in the API in general. * [Common Error Codes](errors.json)", response = Error.class) })
    @RequestMapping(value = "/validation",
        produces = { "application/json" }, 
        consumes = { "application/json" },
        method = RequestMethod.POST)
    public ResponseEntity<ValidationResult> validateCustomProcess(@ApiParam(value = "Specifies the user-defined process to be validated." ,required=true )  @Valid @RequestBody Process processGraphWithMetadata) {
        //TODO implement validation check
    	
    	
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }
    
}
