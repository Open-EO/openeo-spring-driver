/**
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech) (5.0.0-SNAPSHOT).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */
package org.openeo.spring.api;

import java.util.Optional;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;

import org.openeo.spring.model.Error;
import org.openeo.spring.model.LogEntries;
import org.openeo.spring.model.SecondaryWebServices;
import org.openeo.spring.model.Service;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.NativeWebRequest;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Authorization;
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2020-07-02T08:45:00.334+02:00[Europe/Rome]")
@Validated
@Api(value = "services", description = "the services API")
public interface ServicesApi {

    default Optional<NativeWebRequest> getRequest() {
        return Optional.empty();
    }

    /**
     * POST /services : Publish a new service
     * Calling this endpoint will create a secondary web service such as WMTS, TMS or WCS. The underlying data is processes on-demand, but a process graph may simply access results from a batch job. Computations should be performed in the sense that it is only evaluated for the requested spatial / temporal extent and resolution.  **Note:** Costs incurred by shared secondary web services are usually paid by the owner, but this depends on the service type and whether it supports charging fees or not.
     *
     * @param storeSecondaryWebServiceRequest  (required)
     * @return The service has been created successfully. (status code 201)
     *         or The request can&#39;t be fulfilled due to an error on client-side, i.e. the request is invalid. The client should not repeat the request without modifications.  The response body SHOULD contain a JSON error object. MUST be any HTTP status code specified in [RFC 7231](https://tools.ietf.org/html/rfc7231#section-6.6). This request MUST respond with HTTP status codes 401 if authorization is required or 403 if the authorization failed or access is forbidden in general to the authenticated user. HTTP status code 404 should be used if the value of a path parameter is invalid.  See also: * [Error Handling](#section/API-Principles/Error-Handling) in the API in general. * [Common Error Codes](errors.json) (status code 400)
     *         or The request can&#39;t be fulfilled due to an error at the back-end. The error is never the client’s fault and therefore it is reasonable for the client to retry the exact same request that triggered this response.  The response body SHOULD contain a JSON error object. MUST be any HTTP status code specified in [RFC 7231](https://tools.ietf.org/html/rfc7231#section-6.6).  See also: * [Error Handling](#section/API-Principles/Error-Handling) in the API in general. * [Common Error Codes](errors.json) (status code 500)
     */
    @ApiOperation(value = "Publish a new service", nickname = "createService", notes = "Calling this endpoint will create a secondary web service such as WMTS, TMS or WCS. The underlying data is processes on-demand, but a process graph may simply access results from a batch job. Computations should be performed in the sense that it is only evaluated for the requested spatial / temporal extent and resolution.  **Note:** Costs incurred by shared secondary web services are usually paid by the owner, but this depends on the service type and whether it supports charging fees or not.", authorizations = {
        @Authorization(value = "Bearer")
    }, tags={ "Secondary Services", })
    @ApiResponses(value = { 
        @ApiResponse(code = 201, message = "The service has been created successfully."),
        @ApiResponse(code = 400, message = "The request can't be fulfilled due to an error on client-side, i.e. the request is invalid. The client should not repeat the request without modifications.  The response body SHOULD contain a JSON error object. MUST be any HTTP status code specified in [RFC 7231](https://tools.ietf.org/html/rfc7231#section-6.6). This request MUST respond with HTTP status codes 401 if authorization is required or 403 if the authorization failed or access is forbidden in general to the authenticated user. HTTP status code 404 should be used if the value of a path parameter is invalid.  See also: * [Error Handling](#section/API-Principles/Error-Handling) in the API in general. * [Common Error Codes](errors.json)", response = Error.class),
        @ApiResponse(code = 500, message = "The request can't be fulfilled due to an error at the back-end. The error is never the client’s fault and therefore it is reasonable for the client to retry the exact same request that triggered this response.  The response body SHOULD contain a JSON error object. MUST be any HTTP status code specified in [RFC 7231](https://tools.ietf.org/html/rfc7231#section-6.6).  See also: * [Error Handling](#section/API-Principles/Error-Handling) in the API in general. * [Common Error Codes](errors.json)", response = Error.class) })
    @RequestMapping(value = "/services",
        produces = { "application/json" }, 
        consumes = { "application/json" },
        method = RequestMethod.POST)
    default ResponseEntity<Void> createService(@ApiParam(value = "" ,required=true )  @Valid @RequestBody Service storeSecondaryWebServiceRequest) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }


    /**
     * GET /services/{service_id}/logs : Logs for a secondary service
     * Shows log entries for the secondary service, usually for debugging purposes. Back-ends can log any information that may be relevant for a user. Users can log information during data processing using respective processes such as &#x60;debug&#x60;. If requested consecutively while the secondary service is enabled, it is RECOMMENDED that clients use the offset parameter to get only the entries they have not received yet. While pagination itself is OPTIONAL, the &#x60;offset&#x60; parameter is REQUIRED to be implemented by back-ends.
     *
     * @param serviceId Unique secondary web service identifier. (required)
     * @param offset The last identifier (property &#x60;id&#x60; of a log entry) the client has received. If provided, the back-ends only sends the entries that occured after the specified identifier. If not provided or empty, start with the first entry. (optional)
     * @param limit This parameter enables pagination for the endpoint and specifies the maximum number of elements that arrays in the top-level object (e.g. jobs or log entries) are allowed to contain. The only exception is the &#x60;links&#x60; array, which MUST NOT be paginated as otherwise the pagination links may be missing ins responses. If the parameter is not provided or empty, all elements are returned.  Pagination is OPTIONAL and back-ends and clients may not support it. Therefore it MUST be implemented in a way that clients not supporting pagination get all resources regardless. Back-ends not supporting  pagination will return all resources.  If the response is paginated, the links array MUST be used to propagate the  links for pagination with pre-defined &#x60;rel&#x60; types. See the links array schema for supported &#x60;rel&#x60; types.  *Note:* Implementations can use all kind of pagination techniques, depending on what is supported best by their infrastructure. So it doesn&#39;t care whether it is page-based, offset-based or uses tokens for pagination. The clients will use whatever is specified in the links with the corresponding &#x60;rel&#x60; types. (optional)
     * @return Lists the requested log entries. (status code 200)
     *         or The request can&#39;t be fulfilled due to an error on client-side, i.e. the request is invalid. The client should not repeat the request without modifications.  The response body SHOULD contain a JSON error object. MUST be any HTTP status code specified in [RFC 7231](https://tools.ietf.org/html/rfc7231#section-6.6). This request MUST respond with HTTP status codes 401 if authorization is required or 403 if the authorization failed or access is forbidden in general to the authenticated user. HTTP status code 404 should be used if the value of a path parameter is invalid.  See also: * [Error Handling](#section/API-Principles/Error-Handling) in the API in general. * [Common Error Codes](errors.json) (status code 400)
     *         or The request can&#39;t be fulfilled due to an error at the back-end. The error is never the client’s fault and therefore it is reasonable for the client to retry the exact same request that triggered this response.  The response body SHOULD contain a JSON error object. MUST be any HTTP status code specified in [RFC 7231](https://tools.ietf.org/html/rfc7231#section-6.6).  See also: * [Error Handling](#section/API-Principles/Error-Handling) in the API in general. * [Common Error Codes](errors.json) (status code 500)
     */
    @ApiOperation(value = "Logs for a secondary service", nickname = "debugService", notes = "Shows log entries for the secondary service, usually for debugging purposes. Back-ends can log any information that may be relevant for a user. Users can log information during data processing using respective processes such as `debug`. If requested consecutively while the secondary service is enabled, it is RECOMMENDED that clients use the offset parameter to get only the entries they have not received yet. While pagination itself is OPTIONAL, the `offset` parameter is REQUIRED to be implemented by back-ends.", response = LogEntries.class, authorizations = {
        @Authorization(value = "Bearer")
    }, tags={ "Secondary Services", })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "Lists the requested log entries.", response = LogEntries.class),
        @ApiResponse(code = 400, message = "The request can't be fulfilled due to an error on client-side, i.e. the request is invalid. The client should not repeat the request without modifications.  The response body SHOULD contain a JSON error object. MUST be any HTTP status code specified in [RFC 7231](https://tools.ietf.org/html/rfc7231#section-6.6). This request MUST respond with HTTP status codes 401 if authorization is required or 403 if the authorization failed or access is forbidden in general to the authenticated user. HTTP status code 404 should be used if the value of a path parameter is invalid.  See also: * [Error Handling](#section/API-Principles/Error-Handling) in the API in general. * [Common Error Codes](errors.json)", response = Error.class),
        @ApiResponse(code = 500, message = "The request can't be fulfilled due to an error at the back-end. The error is never the client’s fault and therefore it is reasonable for the client to retry the exact same request that triggered this response.  The response body SHOULD contain a JSON error object. MUST be any HTTP status code specified in [RFC 7231](https://tools.ietf.org/html/rfc7231#section-6.6).  See also: * [Error Handling](#section/API-Principles/Error-Handling) in the API in general. * [Common Error Codes](errors.json)", response = Error.class) })
    @RequestMapping(value = "/services/{service_id}/logs",
        produces = { "application/json" }, 
        method = RequestMethod.GET)
    default ResponseEntity<LogEntries> debugService(@Pattern(regexp="^[\\w\\-\\.~]+$") @ApiParam(value = "Unique secondary web service identifier.",required=true) @PathVariable("service_id") String serviceId,@ApiParam(value = "The last identifier (property `id` of a log entry) the client has received. If provided, the back-ends only sends the entries that occured after the specified identifier. If not provided or empty, start with the first entry.") @Valid @RequestParam(value = "offset", required = false) String offset,@Min(1)@ApiParam(value = "This parameter enables pagination for the endpoint and specifies the maximum number of elements that arrays in the top-level object (e.g. jobs or log entries) are allowed to contain. The only exception is the `links` array, which MUST NOT be paginated as otherwise the pagination links may be missing ins responses. If the parameter is not provided or empty, all elements are returned.  Pagination is OPTIONAL and back-ends and clients may not support it. Therefore it MUST be implemented in a way that clients not supporting pagination get all resources regardless. Back-ends not supporting  pagination will return all resources.  If the response is paginated, the links array MUST be used to propagate the  links for pagination with pre-defined `rel` types. See the links array schema for supported `rel` types.  *Note:* Implementations can use all kind of pagination techniques, depending on what is supported best by their infrastructure. So it doesn't care whether it is page-based, offset-based or uses tokens for pagination. The clients will use whatever is specified in the links with the corresponding `rel` types.") @Valid @RequestParam(value = "limit", required = false) Integer limit) {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"links\" : [ { \"rel\" : \"related\", \"href\" : \"https://example.openeo.org\", \"type\" : \"text/html\", \"title\" : \"openEO\" }, { \"rel\" : \"related\", \"href\" : \"https://example.openeo.org\", \"type\" : \"text/html\", \"title\" : \"openEO\" } ], \"logs\" : [ { \"path\" : [ { \"process_id\" : \"run_udf\", \"parameter\" : \"udf\", \"node_id\" : \"runudf1\" }, { \"process_id\" : \"run_udf\", \"parameter\" : \"udf\", \"node_id\" : \"runudf1\" } ], \"code\" : \"SampleError\", \"data\" : \"\", \"level\" : \"error\", \"links\" : [ { \"href\" : \"https://example.openeo.org/docs/errors/SampleError\", \"rel\" : \"about\" } ], \"id\" : \"1\", \"message\" : \"Can't load the UDF file from the URL `https://example.com/invalid/file.txt`. Server responded with error 404.\" }, { \"path\" : [ { \"process_id\" : \"run_udf\", \"parameter\" : \"udf\", \"node_id\" : \"runudf1\" }, { \"process_id\" : \"run_udf\", \"parameter\" : \"udf\", \"node_id\" : \"runudf1\" } ], \"code\" : \"SampleError\", \"data\" : \"\", \"level\" : \"error\", \"links\" : [ { \"href\" : \"https://example.openeo.org/docs/errors/SampleError\", \"rel\" : \"about\" } ], \"id\" : \"1\", \"message\" : \"Can't load the UDF file from the URL `https://example.com/invalid/file.txt`. Server responded with error 404.\" } ] }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }


    /**
     * DELETE /services/{service_id} : Delete a service
     * Calling this endpoint will stop a given secondary web service to access result data.
     *
     * @param serviceId Unique secondary web service identifier. (required)
     * @return The service has been successfully deleted. (status code 204)
     *         or The request can&#39;t be fulfilled due to an error on client-side, i.e. the request is invalid. The client should not repeat the request without modifications.  The response body SHOULD contain a JSON error object. MUST be any HTTP status code specified in [RFC 7231](https://tools.ietf.org/html/rfc7231#section-6.6). This request MUST respond with HTTP status codes 401 if authorization is required or 403 if the authorization failed or access is forbidden in general to the authenticated user. HTTP status code 404 should be used if the value of a path parameter is invalid.  See also: * [Error Handling](#section/API-Principles/Error-Handling) in the API in general. * [Common Error Codes](errors.json) (status code 400)
     *         or The request can&#39;t be fulfilled due to an error at the back-end. The error is never the client’s fault and therefore it is reasonable for the client to retry the exact same request that triggered this response.  The response body SHOULD contain a JSON error object. MUST be any HTTP status code specified in [RFC 7231](https://tools.ietf.org/html/rfc7231#section-6.6).  See also: * [Error Handling](#section/API-Principles/Error-Handling) in the API in general. * [Common Error Codes](errors.json) (status code 500)
     */
    @ApiOperation(value = "Delete a service", nickname = "deleteService", notes = "Calling this endpoint will stop a given secondary web service to access result data.", authorizations = {
        @Authorization(value = "Bearer")
    }, tags={ "Secondary Services", })
    @ApiResponses(value = { 
        @ApiResponse(code = 204, message = "The service has been successfully deleted."),
        @ApiResponse(code = 400, message = "The request can't be fulfilled due to an error on client-side, i.e. the request is invalid. The client should not repeat the request without modifications.  The response body SHOULD contain a JSON error object. MUST be any HTTP status code specified in [RFC 7231](https://tools.ietf.org/html/rfc7231#section-6.6). This request MUST respond with HTTP status codes 401 if authorization is required or 403 if the authorization failed or access is forbidden in general to the authenticated user. HTTP status code 404 should be used if the value of a path parameter is invalid.  See also: * [Error Handling](#section/API-Principles/Error-Handling) in the API in general. * [Common Error Codes](errors.json)", response = Error.class),
        @ApiResponse(code = 500, message = "The request can't be fulfilled due to an error at the back-end. The error is never the client’s fault and therefore it is reasonable for the client to retry the exact same request that triggered this response.  The response body SHOULD contain a JSON error object. MUST be any HTTP status code specified in [RFC 7231](https://tools.ietf.org/html/rfc7231#section-6.6).  See also: * [Error Handling](#section/API-Principles/Error-Handling) in the API in general. * [Common Error Codes](errors.json)", response = Error.class) })
    @RequestMapping(value = "/services/{service_id}",
        produces = { "application/json" }, 
        method = RequestMethod.DELETE)
    default ResponseEntity<Void> deleteService(@Pattern(regexp="^[\\w\\-\\.~]+$") @ApiParam(value = "Unique secondary web service identifier.",required=true) @PathVariable("service_id") String serviceId) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }


    /**
     * GET /services/{service_id} : Full metadata for a service
     * Requests to this endpoint will return JSON description of the secondary web service.
     *
     * @param serviceId Unique secondary web service identifier. (required)
     * @return Details of the created service (status code 200)
     *         or The request can&#39;t be fulfilled due to an error on client-side, i.e. the request is invalid. The client should not repeat the request without modifications.  The response body SHOULD contain a JSON error object. MUST be any HTTP status code specified in [RFC 7231](https://tools.ietf.org/html/rfc7231#section-6.6). This request MUST respond with HTTP status codes 401 if authorization is required or 403 if the authorization failed or access is forbidden in general to the authenticated user. HTTP status code 404 should be used if the value of a path parameter is invalid.  See also: * [Error Handling](#section/API-Principles/Error-Handling) in the API in general. * [Common Error Codes](errors.json) (status code 400)
     *         or The request can&#39;t be fulfilled due to an error at the back-end. The error is never the client’s fault and therefore it is reasonable for the client to retry the exact same request that triggered this response.  The response body SHOULD contain a JSON error object. MUST be any HTTP status code specified in [RFC 7231](https://tools.ietf.org/html/rfc7231#section-6.6).  See also: * [Error Handling](#section/API-Principles/Error-Handling) in the API in general. * [Common Error Codes](errors.json) (status code 500)
     */
    @ApiOperation(value = "Full metadata for a service", nickname = "describeService", notes = "Requests to this endpoint will return JSON description of the secondary web service.", response = Service.class, authorizations = {
        @Authorization(value = "Bearer")
    }, tags={ "Secondary Services", })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "Details of the created service", response = Service.class),
        @ApiResponse(code = 400, message = "The request can't be fulfilled due to an error on client-side, i.e. the request is invalid. The client should not repeat the request without modifications.  The response body SHOULD contain a JSON error object. MUST be any HTTP status code specified in [RFC 7231](https://tools.ietf.org/html/rfc7231#section-6.6). This request MUST respond with HTTP status codes 401 if authorization is required or 403 if the authorization failed or access is forbidden in general to the authenticated user. HTTP status code 404 should be used if the value of a path parameter is invalid.  See also: * [Error Handling](#section/API-Principles/Error-Handling) in the API in general. * [Common Error Codes](errors.json)", response = Error.class),
        @ApiResponse(code = 500, message = "The request can't be fulfilled due to an error at the back-end. The error is never the client’s fault and therefore it is reasonable for the client to retry the exact same request that triggered this response.  The response body SHOULD contain a JSON error object. MUST be any HTTP status code specified in [RFC 7231](https://tools.ietf.org/html/rfc7231#section-6.6).  See also: * [Error Handling](#section/API-Principles/Error-Handling) in the API in general. * [Common Error Codes](errors.json)", response = Error.class) })
    @RequestMapping(value = "/services/{service_id}",
        produces = { "application/json" }, 
        method = RequestMethod.GET)
    default ResponseEntity<Service> describeService(@Pattern(regexp="^[\\w\\-\\.~]+$") @ApiParam(value = "Unique secondary web service identifier.",required=true) @PathVariable("service_id") String serviceId) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }


    /**
     * GET /services : List all web services
     * Requests to this endpoint will list all secondary web services submitted by a user.  It is **strongly RECOMMENDED** to keep the response size small by omitting all optional non-scalar values from objects in&#x60;services&#x60; (i.e. the &#x60;process&#x60;, &#x60;configuration&#x60; and &#x60;attributes&#x60; properties). To get the full metadata for a secondary web service clients MUST request &#x60;GET /services/{service_id}&#x60;.
     *
     * @param limit This parameter enables pagination for the endpoint and specifies the maximum number of elements that arrays in the top-level object (e.g. jobs or log entries) are allowed to contain. The only exception is the &#x60;links&#x60; array, which MUST NOT be paginated as otherwise the pagination links may be missing ins responses. If the parameter is not provided or empty, all elements are returned.  Pagination is OPTIONAL and back-ends and clients may not support it. Therefore it MUST be implemented in a way that clients not supporting pagination get all resources regardless. Back-ends not supporting  pagination will return all resources.  If the response is paginated, the links array MUST be used to propagate the  links for pagination with pre-defined &#x60;rel&#x60; types. See the links array schema for supported &#x60;rel&#x60; types.  *Note:* Implementations can use all kind of pagination techniques, depending on what is supported best by their infrastructure. So it doesn&#39;t care whether it is page-based, offset-based or uses tokens for pagination. The clients will use whatever is specified in the links with the corresponding &#x60;rel&#x60; types. (optional)
     * @return Array of secondary web service descriptions (status code 200)
     *         or The request can&#39;t be fulfilled due to an error on client-side, i.e. the request is invalid. The client should not repeat the request without modifications.  The response body SHOULD contain a JSON error object. MUST be any HTTP status code specified in [RFC 7231](https://tools.ietf.org/html/rfc7231#section-6.6). This request MUST respond with HTTP status codes 401 if authorization is required or 403 if the authorization failed or access is forbidden in general to the authenticated user. HTTP status code 404 should be used if the value of a path parameter is invalid.  See also: * [Error Handling](#section/API-Principles/Error-Handling) in the API in general. * [Common Error Codes](errors.json) (status code 400)
     *         or The request can&#39;t be fulfilled due to an error at the back-end. The error is never the client’s fault and therefore it is reasonable for the client to retry the exact same request that triggered this response.  The response body SHOULD contain a JSON error object. MUST be any HTTP status code specified in [RFC 7231](https://tools.ietf.org/html/rfc7231#section-6.6).  See also: * [Error Handling](#section/API-Principles/Error-Handling) in the API in general. * [Common Error Codes](errors.json) (status code 500)
     */
    @ApiOperation(value = "List all web services", nickname = "listServices", notes = "Requests to this endpoint will list all secondary web services submitted by a user.  It is **strongly RECOMMENDED** to keep the response size small by omitting all optional non-scalar values from objects in`services` (i.e. the `process`, `configuration` and `attributes` properties). To get the full metadata for a secondary web service clients MUST request `GET /services/{service_id}`.", response = SecondaryWebServices.class, authorizations = {
        @Authorization(value = "Bearer")
    }, tags={ "Secondary Services", })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "Array of secondary web service descriptions", response = SecondaryWebServices.class),
        @ApiResponse(code = 400, message = "The request can't be fulfilled due to an error on client-side, i.e. the request is invalid. The client should not repeat the request without modifications.  The response body SHOULD contain a JSON error object. MUST be any HTTP status code specified in [RFC 7231](https://tools.ietf.org/html/rfc7231#section-6.6). This request MUST respond with HTTP status codes 401 if authorization is required or 403 if the authorization failed or access is forbidden in general to the authenticated user. HTTP status code 404 should be used if the value of a path parameter is invalid.  See also: * [Error Handling](#section/API-Principles/Error-Handling) in the API in general. * [Common Error Codes](errors.json)", response = Error.class),
        @ApiResponse(code = 500, message = "The request can't be fulfilled due to an error at the back-end. The error is never the client’s fault and therefore it is reasonable for the client to retry the exact same request that triggered this response.  The response body SHOULD contain a JSON error object. MUST be any HTTP status code specified in [RFC 7231](https://tools.ietf.org/html/rfc7231#section-6.6).  See also: * [Error Handling](#section/API-Principles/Error-Handling) in the API in general. * [Common Error Codes](errors.json)", response = Error.class) })
    @RequestMapping(value = "/services",
        produces = { "application/json" }, 
        method = RequestMethod.GET)
    default ResponseEntity<SecondaryWebServices> listServices(@Min(1)@ApiParam(value = "This parameter enables pagination for the endpoint and specifies the maximum number of elements that arrays in the top-level object (e.g. jobs or log entries) are allowed to contain. The only exception is the `links` array, which MUST NOT be paginated as otherwise the pagination links may be missing ins responses. If the parameter is not provided or empty, all elements are returned.  Pagination is OPTIONAL and back-ends and clients may not support it. Therefore it MUST be implemented in a way that clients not supporting pagination get all resources regardless. Back-ends not supporting  pagination will return all resources.  If the response is paginated, the links array MUST be used to propagate the  links for pagination with pre-defined `rel` types. See the links array schema for supported `rel` types.  *Note:* Implementations can use all kind of pagination techniques, depending on what is supported best by their infrastructure. So it doesn't care whether it is page-based, offset-based or uses tokens for pagination. The clients will use whatever is specified in the links with the corresponding `rel` types.") @Valid @RequestParam(value = "limit", required = false) Integer limit) {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"links\" : [ { \"rel\" : \"related\", \"href\" : \"https://example.openeo.org\", \"type\" : \"text/html\", \"title\" : \"openEO\" }, { \"rel\" : \"related\", \"href\" : \"https://example.openeo.org\", \"type\" : \"text/html\", \"title\" : \"openEO\" } ], \"services\" : [ { \"costs\" : 12.98, \"process\" : { \"id\" : \"\" }, \"configuration\" : { \"version\" : \"1.3.0\" }, \"created\" : \"2017-01-01T09:32:12Z\", \"description\" : \"Deriving minimum NDVI measurements over pixel time series of Sentinel 2\", \"title\" : \"NDVI based on Sentinel 2\", \"type\" : \"wms\", \"url\" : \"https://example.openeo.org/wms/wms-a3cca9\", \"enabled\" : true, \"attributes\" : { \"layers\" : [ \"ndvi\", \"evi\" ] }, \"id\" : \"wms-a3cca9\", \"plan\" : \"free\", \"budget\" : 100 }, { \"costs\" : 12.98, \"process\" : { \"id\" : \"\" }, \"configuration\" : { \"version\" : \"1.3.0\" }, \"created\" : \"2017-01-01T09:32:12Z\", \"description\" : \"Deriving minimum NDVI measurements over pixel time series of Sentinel 2\", \"title\" : \"NDVI based on Sentinel 2\", \"type\" : \"wms\", \"url\" : \"https://example.openeo.org/wms/wms-a3cca9\", \"enabled\" : true, \"attributes\" : { \"layers\" : [ \"ndvi\", \"evi\" ] }, \"id\" : \"wms-a3cca9\", \"plan\" : \"free\", \"budget\" : 100 } ] }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }


    /**
     * PATCH /services/{service_id} : Modify a service
     * Calling this endpoint will change the specified secondary web service, but maintain its identifier. Changes can be grouped in a single request. To change the service type create a new service.
     *
     * @param serviceId Unique secondary web service identifier. (required)
     * @param updateSecondaryWebServiceRequest  (required)
     * @return Changes to the service were applied successfully. (status code 204)
     *         or The request can&#39;t be fulfilled due to an error on client-side, i.e. the request is invalid. The client should not repeat the request without modifications.  The response body SHOULD contain a JSON error object. MUST be any HTTP status code specified in [RFC 7231](https://tools.ietf.org/html/rfc7231#section-6.6). This request MUST respond with HTTP status codes 401 if authorization is required or 403 if the authorization failed or access is forbidden in general to the authenticated user. HTTP status code 404 should be used if the value of a path parameter is invalid.  See also: * [Error Handling](#section/API-Principles/Error-Handling) in the API in general. * [Common Error Codes](errors.json) (status code 400)
     *         or The request can&#39;t be fulfilled due to an error at the back-end. The error is never the client’s fault and therefore it is reasonable for the client to retry the exact same request that triggered this response.  The response body SHOULD contain a JSON error object. MUST be any HTTP status code specified in [RFC 7231](https://tools.ietf.org/html/rfc7231#section-6.6).  See also: * [Error Handling](#section/API-Principles/Error-Handling) in the API in general. * [Common Error Codes](errors.json) (status code 500)
     */
    @ApiOperation(value = "Modify a service", nickname = "updateService", notes = "Calling this endpoint will change the specified secondary web service, but maintain its identifier. Changes can be grouped in a single request. To change the service type create a new service.", authorizations = {
        @Authorization(value = "Bearer")
    }, tags={ "Secondary Services", })
    @ApiResponses(value = { 
        @ApiResponse(code = 204, message = "Changes to the service were applied successfully."),
        @ApiResponse(code = 400, message = "The request can't be fulfilled due to an error on client-side, i.e. the request is invalid. The client should not repeat the request without modifications.  The response body SHOULD contain a JSON error object. MUST be any HTTP status code specified in [RFC 7231](https://tools.ietf.org/html/rfc7231#section-6.6). This request MUST respond with HTTP status codes 401 if authorization is required or 403 if the authorization failed or access is forbidden in general to the authenticated user. HTTP status code 404 should be used if the value of a path parameter is invalid.  See also: * [Error Handling](#section/API-Principles/Error-Handling) in the API in general. * [Common Error Codes](errors.json)", response = Error.class),
        @ApiResponse(code = 500, message = "The request can't be fulfilled due to an error at the back-end. The error is never the client’s fault and therefore it is reasonable for the client to retry the exact same request that triggered this response.  The response body SHOULD contain a JSON error object. MUST be any HTTP status code specified in [RFC 7231](https://tools.ietf.org/html/rfc7231#section-6.6).  See also: * [Error Handling](#section/API-Principles/Error-Handling) in the API in general. * [Common Error Codes](errors.json)", response = Error.class) })
    @RequestMapping(value = "/services/{service_id}",
        produces = { "application/json" }, 
        consumes = { "application/json" },
        method = RequestMethod.PATCH)
    default ResponseEntity<Void> updateService(@Pattern(regexp="^[\\w\\-\\.~]+$") @ApiParam(value = "Unique secondary web service identifier.",required=true) @PathVariable("service_id") String serviceId,@ApiParam(value = "" ,required=true )  @Valid @RequestBody Service updateSecondaryWebServiceRequest) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

}
