/**
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech) (5.0.0-SNAPSHOT).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */
package org.openeo.spring.api;

import java.util.Optional;

import javax.validation.Valid;

import org.openeo.spring.model.Job;
import org.openeo.spring.model.Error;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.request.NativeWebRequest;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2020-07-02T08:45:00.334+02:00[Europe/Rome]")
@Validated
public interface ResultApi {

    default Optional<NativeWebRequest> getRequest() {
        return Optional.empty();
    }

    /**
     * POST /result : Process and download data synchronously
     * A user-defined process will be executed directly and the result will be downloaded in the format specified in the process graph. This endpoint can be used to generate small previews or test user-defined processes before starting a batch job. Timeouts on either client- or server-side are to be expected for complex computations. Back-ends MAY send the openEO error &#x60;ProcessGraphComplexity&#x60; immediately if the computation is expected to time out. Otherwise requests MAY time-out after a certain amount of time by sending openEO error &#x60;RequestTimeout&#x60;. A header named &#x60;OpenEO-Costs&#x60; MAY be sent with all responses, which MUST include the costs for processing and downloading the data. Additionally,  a link to a log file MAY be sent in the header.
     *
     * @param synchronousResultRequest  (required)
     * @return Result data in the requested output format (status code 200)
     *         or The request can&#39;t be fulfilled due to an error on client-side, i.e. the request is invalid. The client should not repeat the request without modifications.  The response body SHOULD contain a JSON error object. MUST be any HTTP status code specified in [RFC 7231](https://tools.ietf.org/html/rfc7231#section-6.6). This request MUST respond with HTTP status codes 401 if authorization is required or 403 if the authorization failed or access is forbidden in general to the authenticated user. HTTP status code 404 should be used if the value of a path parameter is invalid.  See also: * [Error Handling](#section/API-Principles/Error-Handling) in the API in general. * [Common Error Codes](errors.json) (status code 400)
     *         or The request can&#39;t be fulfilled due to an error at the back-end. The error is never the client’s fault and therefore it is reasonable for the client to retry the exact same request that triggered this response.  The response body SHOULD contain a JSON error object. MUST be any HTTP status code specified in [RFC 7231](https://tools.ietf.org/html/rfc7231#section-6.6).  See also: * [Error Handling](#section/API-Principles/Error-Handling) in the API in general. * [Common Error Codes](errors.json) (status code 500)
     */
    @Operation(summary = "Process and download data synchronously", operationId = "computeResult", description = "A user-defined process will be executed directly and the result will be downloaded in the format specified in the process graph. This endpoint can be used to generate small previews or test user-defined processes before starting a batch job. Timeouts on either client- or server-side are to be expected for complex computations. Back-ends MAY send the openEO error `ProcessGraphComplexity` immediately if the computation is expected to time out. Otherwise requests MAY time-out after a certain amount of time by sending openEO error `RequestTimeout`. A header named `OpenEO-Costs` MAY be sent with all responses, which MUST include the costs for processing and downloading the data. Additionally,  a link to a log file MAY be sent in the header.", tags={ "Data Processing", })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "Result data in the requested output format"),
        @ApiResponse(responseCode = "400", description = "The request can't be fulfilled due to an error on client-side, i.e. the request is invalid. The client should not repeat the request without modifications.  The response body SHOULD contain a JSON error object. MUST be any HTTP status code specified in [RFC 7231](https://tools.ietf.org/html/rfc7231#section-6.6). This request MUST respond with HTTP status codes 401 if authorization is required or 403 if the authorization failed or access is forbidden in general to the authenticated user. HTTP status code 404 should be used if the value of a path parameter is invalid.  See also: * [Error Handling](#section/API-Principles/Error-Handling) in the API in general. * [Common Error Codes](errors.json)"),
        @ApiResponse(responseCode = "500", description = "The request can't be fulfilled due to an error at the back-end. The error is never the client’s fault and therefore it is reasonable for the client to retry the exact same request that triggered this response.  The response body SHOULD contain a JSON error object. MUST be any HTTP status code specified in [RFC 7231](https://tools.ietf.org/html/rfc7231#section-6.6).  See also: * [Error Handling](#section/API-Principles/Error-Handling) in the API in general. * [Common Error Codes](errors.json)") })
    @RequestMapping(value = "/result",
        produces = { "application/json" }, 
        consumes = { "application/json" },
        method = RequestMethod.POST)
    default ResponseEntity<Void> computeResult(@Parameter(description = "" ,required=true )  @Valid @RequestBody Job synchronousResultRequest) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

}
