/**
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech) (5.0.0-SNAPSHOT).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */
package org.openeo.spring.api;

import org.openeo.spring.model.Error;
import org.openeo.spring.model.Process;
import org.openeo.spring.model.ProcessGraphWithMetadata;
import org.openeo.spring.model.UserDefinedProcessesResponse;
import io.swagger.annotations.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import javax.validation.constraints.*;
import java.util.List;
import java.util.Map;
import java.util.Optional;
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2020-06-30T15:12:47.411+02:00[Europe/Rome]")
@Validated
@Api(value = "process_graphs", description = "the process_graphs API")
public interface ProcessGraphsApi {

    default Optional<NativeWebRequest> getRequest() {
        return Optional.empty();
    }

    /**
     * DELETE /process_graphs/{process_graph_id} : Delete a user-defined process
     * Deletes the data related to this user-defined process, including its process graph.  Does NOT delete jobs or services that reference this user-defined process.
     *
     * @param processGraphId Unique identifier for a user-defined process. (required)
     * @return The user-defined process has been successfully deleted (status code 204)
     *         or The request can&#39;t be fulfilled due to an error on client-side, i.e. the request is invalid. The client should not repeat the request without modifications.  The response body SHOULD contain a JSON error object. MUST be any HTTP status code specified in [RFC 7231](https://tools.ietf.org/html/rfc7231#section-6.6). This request MUST respond with HTTP status codes 401 if authorization is required or 403 if the authorization failed or access is forbidden in general to the authenticated user. HTTP status code 404 should be used if the value of a path parameter is invalid.  See also: * [Error Handling](#section/API-Principles/Error-Handling) in the API in general. * [Common Error Codes](errors.json) (status code 400)
     *         or The request can&#39;t be fulfilled due to an error at the back-end. The error is never the client’s fault and therefore it is reasonable for the client to retry the exact same request that triggered this response.  The response body SHOULD contain a JSON error object. MUST be any HTTP status code specified in [RFC 7231](https://tools.ietf.org/html/rfc7231#section-6.6).  See also: * [Error Handling](#section/API-Principles/Error-Handling) in the API in general. * [Common Error Codes](errors.json) (status code 500)
     */
    @ApiOperation(value = "Delete a user-defined process", nickname = "deleteCustomProcess", notes = "Deletes the data related to this user-defined process, including its process graph.  Does NOT delete jobs or services that reference this user-defined process.", authorizations = {
        @Authorization(value = "Bearer")
    }, tags={ "User-Defined Processes", })
    @ApiResponses(value = { 
        @ApiResponse(code = 204, message = "The user-defined process has been successfully deleted"),
        @ApiResponse(code = 400, message = "The request can't be fulfilled due to an error on client-side, i.e. the request is invalid. The client should not repeat the request without modifications.  The response body SHOULD contain a JSON error object. MUST be any HTTP status code specified in [RFC 7231](https://tools.ietf.org/html/rfc7231#section-6.6). This request MUST respond with HTTP status codes 401 if authorization is required or 403 if the authorization failed or access is forbidden in general to the authenticated user. HTTP status code 404 should be used if the value of a path parameter is invalid.  See also: * [Error Handling](#section/API-Principles/Error-Handling) in the API in general. * [Common Error Codes](errors.json)", response = Error.class),
        @ApiResponse(code = 500, message = "The request can't be fulfilled due to an error at the back-end. The error is never the client’s fault and therefore it is reasonable for the client to retry the exact same request that triggered this response.  The response body SHOULD contain a JSON error object. MUST be any HTTP status code specified in [RFC 7231](https://tools.ietf.org/html/rfc7231#section-6.6).  See also: * [Error Handling](#section/API-Principles/Error-Handling) in the API in general. * [Common Error Codes](errors.json)", response = Error.class) })
    @RequestMapping(value = "/process_graphs/{process_graph_id}",
        produces = { "application/json" }, 
        method = RequestMethod.DELETE)
    default ResponseEntity<Void> deleteCustomProcess(@Pattern(regexp="^\\w+$") @ApiParam(value = "Unique identifier for a user-defined process.",required=true) @PathVariable("process_graph_id") String processGraphId) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }


    /**
     * GET /process_graphs/{process_graph_id} : Full metadata for a user-defined process
     * Returns all information about a user-defined process, including its process graph.
     *
     * @param processGraphId Unique identifier for a user-defined process. (required)
     * @return The user-defined process with process graph. (status code 200)
     *         or The request can&#39;t be fulfilled due to an error on client-side, i.e. the request is invalid. The client should not repeat the request without modifications.  The response body SHOULD contain a JSON error object. MUST be any HTTP status code specified in [RFC 7231](https://tools.ietf.org/html/rfc7231#section-6.6). This request MUST respond with HTTP status codes 401 if authorization is required or 403 if the authorization failed or access is forbidden in general to the authenticated user. HTTP status code 404 should be used if the value of a path parameter is invalid.  See also: * [Error Handling](#section/API-Principles/Error-Handling) in the API in general. * [Common Error Codes](errors.json) (status code 400)
     *         or The request can&#39;t be fulfilled due to an error at the back-end. The error is never the client’s fault and therefore it is reasonable for the client to retry the exact same request that triggered this response.  The response body SHOULD contain a JSON error object. MUST be any HTTP status code specified in [RFC 7231](https://tools.ietf.org/html/rfc7231#section-6.6).  See also: * [Error Handling](#section/API-Principles/Error-Handling) in the API in general. * [Common Error Codes](errors.json) (status code 500)
     */
    @ApiOperation(value = "Full metadata for a user-defined process", nickname = "describeCustomProcess", notes = "Returns all information about a user-defined process, including its process graph.", response = Process.class, authorizations = {
        @Authorization(value = "Bearer")
    }, tags={ "User-Defined Processes","Process Discovery", })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "The user-defined process with process graph.", response = Process.class),
        @ApiResponse(code = 400, message = "The request can't be fulfilled due to an error on client-side, i.e. the request is invalid. The client should not repeat the request without modifications.  The response body SHOULD contain a JSON error object. MUST be any HTTP status code specified in [RFC 7231](https://tools.ietf.org/html/rfc7231#section-6.6). This request MUST respond with HTTP status codes 401 if authorization is required or 403 if the authorization failed or access is forbidden in general to the authenticated user. HTTP status code 404 should be used if the value of a path parameter is invalid.  See also: * [Error Handling](#section/API-Principles/Error-Handling) in the API in general. * [Common Error Codes](errors.json)", response = Error.class),
        @ApiResponse(code = 500, message = "The request can't be fulfilled due to an error at the back-end. The error is never the client’s fault and therefore it is reasonable for the client to retry the exact same request that triggered this response.  The response body SHOULD contain a JSON error object. MUST be any HTTP status code specified in [RFC 7231](https://tools.ietf.org/html/rfc7231#section-6.6).  See also: * [Error Handling](#section/API-Principles/Error-Handling) in the API in general. * [Common Error Codes](errors.json)", response = Error.class) })
    @RequestMapping(value = "/process_graphs/{process_graph_id}",
        produces = { "application/json" }, 
        method = RequestMethod.GET)
    default ResponseEntity<Process> describeCustomProcess(@Pattern(regexp="^\\w+$") @ApiParam(value = "Unique identifier for a user-defined process.",required=true) @PathVariable("process_graph_id") String processGraphId) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }


    /**
     * GET /process_graphs : List all user-defined processes
     * This service lists all user-defined processes (process graphs) of the authenticated user that are stored at the back-end.  It is **strongly RECOMMENDED** to keep the response size small by omitting larger values from the objects in &#x60;processes&#x60; (e.g. the &#x60;exceptions&#x60;, &#x60;examples&#x60; and &#x60;links&#x60; properties). To get the full metadata for a secondary web service clients MUST request &#x60;GET /services/{service_id}&#x60;.
     *
     * @param limit This parameter enables pagination for the endpoint and specifies the maximum number of elements that arrays in the top-level object (e.g. jobs or log entries) are allowed to contain. The only exception is the &#x60;links&#x60; array, which MUST NOT be paginated as otherwise the pagination links may be missing ins responses. If the parameter is not provided or empty, all elements are returned.  Pagination is OPTIONAL and back-ends and clients may not support it. Therefore it MUST be implemented in a way that clients not supporting pagination get all resources regardless. Back-ends not supporting  pagination will return all resources.  If the response is paginated, the links array MUST be used to propagate the  links for pagination with pre-defined &#x60;rel&#x60; types. See the links array schema for supported &#x60;rel&#x60; types.  *Note:* Implementations can use all kind of pagination techniques, depending on what is supported best by their infrastructure. So it doesn&#39;t care whether it is page-based, offset-based or uses tokens for pagination. The clients will use whatever is specified in the links with the corresponding &#x60;rel&#x60; types. (optional)
     * @return JSON array with user-defined processes. (status code 200)
     *         or The request can&#39;t be fulfilled due to an error on client-side, i.e. the request is invalid. The client should not repeat the request without modifications.  The response body SHOULD contain a JSON error object. MUST be any HTTP status code specified in [RFC 7231](https://tools.ietf.org/html/rfc7231#section-6.6). This request MUST respond with HTTP status codes 401 if authorization is required or 403 if the authorization failed or access is forbidden in general to the authenticated user. HTTP status code 404 should be used if the value of a path parameter is invalid.  See also: * [Error Handling](#section/API-Principles/Error-Handling) in the API in general. * [Common Error Codes](errors.json) (status code 400)
     *         or The request can&#39;t be fulfilled due to an error at the back-end. The error is never the client’s fault and therefore it is reasonable for the client to retry the exact same request that triggered this response.  The response body SHOULD contain a JSON error object. MUST be any HTTP status code specified in [RFC 7231](https://tools.ietf.org/html/rfc7231#section-6.6).  See also: * [Error Handling](#section/API-Principles/Error-Handling) in the API in general. * [Common Error Codes](errors.json) (status code 500)
     */
    @ApiOperation(value = "List all user-defined processes", nickname = "listCustomProcesses", notes = "This service lists all user-defined processes (process graphs) of the authenticated user that are stored at the back-end.  It is **strongly RECOMMENDED** to keep the response size small by omitting larger values from the objects in `processes` (e.g. the `exceptions`, `examples` and `links` properties). To get the full metadata for a secondary web service clients MUST request `GET /services/{service_id}`.", response = UserDefinedProcessesResponse.class, authorizations = {
        @Authorization(value = "Bearer")
    }, tags={ "User-Defined Processes","Process Discovery", })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "JSON array with user-defined processes.", response = UserDefinedProcessesResponse.class),
        @ApiResponse(code = 400, message = "The request can't be fulfilled due to an error on client-side, i.e. the request is invalid. The client should not repeat the request without modifications.  The response body SHOULD contain a JSON error object. MUST be any HTTP status code specified in [RFC 7231](https://tools.ietf.org/html/rfc7231#section-6.6). This request MUST respond with HTTP status codes 401 if authorization is required or 403 if the authorization failed or access is forbidden in general to the authenticated user. HTTP status code 404 should be used if the value of a path parameter is invalid.  See also: * [Error Handling](#section/API-Principles/Error-Handling) in the API in general. * [Common Error Codes](errors.json)", response = Error.class),
        @ApiResponse(code = 500, message = "The request can't be fulfilled due to an error at the back-end. The error is never the client’s fault and therefore it is reasonable for the client to retry the exact same request that triggered this response.  The response body SHOULD contain a JSON error object. MUST be any HTTP status code specified in [RFC 7231](https://tools.ietf.org/html/rfc7231#section-6.6).  See also: * [Error Handling](#section/API-Principles/Error-Handling) in the API in general. * [Common Error Codes](errors.json)", response = Error.class) })
    @RequestMapping(value = "/process_graphs",
        produces = { "application/json" }, 
        method = RequestMethod.GET)
    default ResponseEntity<UserDefinedProcessesResponse> listCustomProcesses(@Min(1)@ApiParam(value = "This parameter enables pagination for the endpoint and specifies the maximum number of elements that arrays in the top-level object (e.g. jobs or log entries) are allowed to contain. The only exception is the `links` array, which MUST NOT be paginated as otherwise the pagination links may be missing ins responses. If the parameter is not provided or empty, all elements are returned.  Pagination is OPTIONAL and back-ends and clients may not support it. Therefore it MUST be implemented in a way that clients not supporting pagination get all resources regardless. Back-ends not supporting  pagination will return all resources.  If the response is paginated, the links array MUST be used to propagate the  links for pagination with pre-defined `rel` types. See the links array schema for supported `rel` types.  *Note:* Implementations can use all kind of pagination techniques, depending on what is supported best by their infrastructure. So it doesn't care whether it is page-based, offset-based or uses tokens for pagination. The clients will use whatever is specified in the links with the corresponding `rel` types.") @Valid @RequestParam(value = "limit", required = false) Integer limit) {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "\"{\\"processes\\":[{\\"id\\":\\"evi\\",\\"summary\\":\\"Enhanced Vegetation Index\\",\\"description\\":\\"Computes the Enhanced Vegetation Index (EVI). It is computed with the following formula: `2.5 * (NIR - RED) / (1 + NIR + 6*RED + -7.5*BLUE)`.\\",\\"parameters\\":[{\\"name\\":\\"red\\",\\"description\\":\\"Value from the red band.\\",\\"schema\\":{\\"type\\":\\"number\\"}},{\\"name\\":\\"blue\\",\\"description\\":\\"Value from the blue band.\\",\\"schema\\":{\\"type\\":\\"number\\"}},{\\"name\\":\\"nir\\",\\"description\\":\\"Value from the near infrared band.\\",\\"schema\\":{\\"type\\":\\"number\\"}}],\\"returns\\":{\\"description\\":\\"Computed EVI.\\",\\"schema\\":{\\"type\\":\\"number\\"}}},{\\"id\\":\\"ndsi\\",\\"summary\\":\\"Normalized-Difference Snow Index\\",\\"parameters\\":[{\\"name\\":\\"green\\",\\"description\\":\\"Value from the Visible Green (0.53 - 0.61 micrometers) band.\\",\\"schema\\":{\\"type\\":\\"number\\"}},{\\"name\\":\\"swir\\",\\"description\\":\\"Value from the Short Wave Infrared (1.55 - 1.75 micrometers) band.\\",\\"schema\\":{\\"type\\":\\"number\\"}}],\\"returns\\":{\\"schema\\":{\\"type\\":\\"number\\"}}},{\\"id\\":\\"my_custom_process\\"}],\\"links\\":[]}\"";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }


    /**
     * PUT /process_graphs/{process_graph_id} : Store a user-defined process
     * Stores a provided user-defined process with process graph that can be reused in other processes. If a process with the specified  &#x60;process_graph_id&#x60; exists, the process is fully replaced. The id can&#39;t be changed for stored user-defined processes.  The id must be unique for the authenticated user, including all pre-defined processes by the back-end.  Partially updating user-defined processes is not supported.  To simplify exchanging user-defined processes, the property &#x60;id&#x60; can be part of the request body. If the values don&#39;t match, the value for &#x60;id&#x60; gets replaced with the value from the &#x60;process_graph_id&#x60; parameter in the path.
     *
     * @param processGraphId Unique identifier for a user-defined process. (required)
     * @param processGraphWithMetadata Specifies the process graph with its meta data. (required)
     * @return The user-defined process has been stored successfully. (status code 200)
     *         or The request can&#39;t be fulfilled due to an error on client-side, i.e. the request is invalid. The client should not repeat the request without modifications.  The response body SHOULD contain a JSON error object. MUST be any HTTP status code specified in [RFC 7231](https://tools.ietf.org/html/rfc7231#section-6.6). This request MUST respond with HTTP status codes 401 if authorization is required or 403 if the authorization failed or access is forbidden in general to the authenticated user. HTTP status code 404 should be used if the value of a path parameter is invalid.  See also: * [Error Handling](#section/API-Principles/Error-Handling) in the API in general. * [Common Error Codes](errors.json) (status code 400)
     *         or The request can&#39;t be fulfilled due to an error at the back-end. The error is never the client’s fault and therefore it is reasonable for the client to retry the exact same request that triggered this response.  The response body SHOULD contain a JSON error object. MUST be any HTTP status code specified in [RFC 7231](https://tools.ietf.org/html/rfc7231#section-6.6).  See also: * [Error Handling](#section/API-Principles/Error-Handling) in the API in general. * [Common Error Codes](errors.json) (status code 500)
     */
    @ApiOperation(value = "Store a user-defined process", nickname = "storeCustomProcess", notes = "Stores a provided user-defined process with process graph that can be reused in other processes. If a process with the specified  `process_graph_id` exists, the process is fully replaced. The id can't be changed for stored user-defined processes.  The id must be unique for the authenticated user, including all pre-defined processes by the back-end.  Partially updating user-defined processes is not supported.  To simplify exchanging user-defined processes, the property `id` can be part of the request body. If the values don't match, the value for `id` gets replaced with the value from the `process_graph_id` parameter in the path.", authorizations = {
        @Authorization(value = "Bearer")
    }, tags={ "User-Defined Processes", })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "The user-defined process has been stored successfully."),
        @ApiResponse(code = 400, message = "The request can't be fulfilled due to an error on client-side, i.e. the request is invalid. The client should not repeat the request without modifications.  The response body SHOULD contain a JSON error object. MUST be any HTTP status code specified in [RFC 7231](https://tools.ietf.org/html/rfc7231#section-6.6). This request MUST respond with HTTP status codes 401 if authorization is required or 403 if the authorization failed or access is forbidden in general to the authenticated user. HTTP status code 404 should be used if the value of a path parameter is invalid.  See also: * [Error Handling](#section/API-Principles/Error-Handling) in the API in general. * [Common Error Codes](errors.json)", response = Error.class),
        @ApiResponse(code = 500, message = "The request can't be fulfilled due to an error at the back-end. The error is never the client’s fault and therefore it is reasonable for the client to retry the exact same request that triggered this response.  The response body SHOULD contain a JSON error object. MUST be any HTTP status code specified in [RFC 7231](https://tools.ietf.org/html/rfc7231#section-6.6).  See also: * [Error Handling](#section/API-Principles/Error-Handling) in the API in general. * [Common Error Codes](errors.json)", response = Error.class) })
    @RequestMapping(value = "/process_graphs/{process_graph_id}",
        produces = { "application/json" }, 
        consumes = { "application/json" },
        method = RequestMethod.PUT)
    default ResponseEntity<Void> storeCustomProcess(@Pattern(regexp="^\\w+$") @ApiParam(value = "Unique identifier for a user-defined process.",required=true) @PathVariable("process_graph_id") String processGraphId,@ApiParam(value = "Specifies the process graph with its meta data." ,required=true )  @Valid @RequestBody ProcessGraphWithMetadata processGraphWithMetadata) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

}
