package org.openeo.spring.api;

import java.util.Optional;

import javax.validation.constraints.Pattern;

import org.openeo.spring.model.Collection;
import org.openeo.spring.model.Error;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.NativeWebRequest;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2020-07-02T08:45:00.334+02:00[Europe/Rome]")
@RestController
@RequestMapping("${openapi.openEO.base-path:}")
public class CollectionsApiController implements CollectionsApi {
    private final NativeWebRequest request;

    @org.springframework.beans.factory.annotation.Autowired
    public CollectionsApiController(NativeWebRequest request) {
        this.request = request;
    }

    @Override
    public Optional<NativeWebRequest> getRequest() {
        return Optional.ofNullable(request);
    }
    
    /**
     * GET /collections/{collection_id} : Full metadata for a specific dataset
     * Lists **all** information about a specific collection specified by the identifier &#x60;collection_id&#x60;.  This endpoint is compatible with [STAC 0.9.0](https://stacspec.org) and [OGC API - Features](http://docs.opengeospatial.org/is/17-069r3/17-069r3.html). [STAC API](https://github.com/radiantearth/stac-spec/tree/v0.9.0/api-spec) features / extensions and [STAC extensions](https://github.com/radiantearth/stac-spec/tree/v0.9.0/extensions) can be implemented in addition to what is documented here.
     *
     * @param collectionId Collection identifier (required)
     * @return JSON object with the full collection metadata. (status code 200)
     *         or The request can&#39;t be fulfilled due to an error on client-side, i.e. the request is invalid. The client should not repeat the request without modifications.  The response body SHOULD contain a JSON error object. MUST be any HTTP status code specified in [RFC 7231](https://tools.ietf.org/html/rfc7231#section-6.6). This request MUST respond with HTTP status codes 401 if authorization is required or 403 if the authorization failed or access is forbidden in general to the authenticated user. HTTP status code 404 should be used if the value of a path parameter is invalid.  See also: * [Error Handling](#section/API-Principles/Error-Handling) in the API in general. * [Common Error Codes](errors.json) (status code 400)
     *         or The request can&#39;t be fulfilled due to an error at the back-end. The error is never the client’s fault and therefore it is reasonable for the client to retry the exact same request that triggered this response.  The response body SHOULD contain a JSON error object. MUST be any HTTP status code specified in [RFC 7231](https://tools.ietf.org/html/rfc7231#section-6.6).  See also: * [Error Handling](#section/API-Principles/Error-Handling) in the API in general. * [Common Error Codes](errors.json) (status code 500)
     */
    @Operation(summary = "Full metadata for a specific dataset", operationId = "describeCollecion", description = "Lists **all** information about a specific collection specified by the identifier `collection_id`.  This endpoint is compatible with [STAC 0.9.0](https://stacspec.org) and [OGC API - Features](http://docs.opengeospatial.org/is/17-069r3/17-069r3.html). [STAC API](https://github.com/radiantearth/stac-spec/tree/v0.9.0/api-spec) features / extensions and [STAC extensions](https://github.com/radiantearth/stac-spec/tree/v0.9.0/extensions) can be implemented in addition to what is documented here.", tags={ "EO Data Discovery", })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "JSON object with the full collection metadata."),
        @ApiResponse(responseCode = "400", description = "The request can't be fulfilled due to an error on client-side, i.e. the request is invalid. The client should not repeat the request without modifications.  The response body SHOULD contain a JSON error object. MUST be any HTTP status code specified in [RFC 7231](https://tools.ietf.org/html/rfc7231#section-6.6). This request MUST respond with HTTP status codes 401 if authorization is required or 403 if the authorization failed or access is forbidden in general to the authenticated user. HTTP status code 404 should be used if the value of a path parameter is invalid.  See also: * [Error Handling](#section/API-Principles/Error-Handling) in the API in general. * [Common Error Codes](errors.json)"),
        @ApiResponse(responseCode = "500", description = "The request can't be fulfilled due to an error at the back-end. The error is never the client’s fault and therefore it is reasonable for the client to retry the exact same request that triggered this response.  The response body SHOULD contain a JSON error object. MUST be any HTTP status code specified in [RFC 7231](https://tools.ietf.org/html/rfc7231#section-6.6).  See also: * [Error Handling](#section/API-Principles/Error-Handling) in the API in general. * [Common Error Codes](errors.json)") })
    @GetMapping(value = "/collections/{collection_id}", produces = { "application/json" })
    @Override
    public ResponseEntity<Collection> describeCollection(@Pattern(regexp="^[\\w\\-\\.~/]+$") @Parameter(name = "Collection identifier",required=true) @PathVariable("collection_id") String collectionId) {
    	Collection currentCollection = new Collection();
    	currentCollection.setId(collectionId);
    	//TODO get description from rasdaman and add it here to the collection object
    	currentCollection.setDescription("my description");
    	
        return  new ResponseEntity<Collection>(HttpStatus.OK);

    }
    

}
