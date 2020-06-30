/**
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech) (5.0.0-SNAPSHOT).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */
package org.openeo.spring.api;

import org.openeo.spring.model.Error;
import org.openeo.spring.model.OGCConformanceResponse;
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
@Api(value = "conformance", description = "the conformance API")
public interface ConformanceApi {

    default Optional<NativeWebRequest> getRequest() {
        return Optional.empty();
    }

    /**
     * GET /conformance : OGC specifications this API conforms to
     * A list of all conformance classes specified in OGC standards that the server conforms to. This endpoint is only required if full compliance with OGC API standards is desired. Therefore, openEO back-ends may implement it for compatibility with OGC API clients and openEO clients don&#39;t need to request it.
     *
     * @return The URIs of all conformance classes supported by the server. (status code 200)
     *         or The request can&#39;t be fulfilled due to an error at the back-end. The error is never the client’s fault and therefore it is reasonable for the client to retry the exact same request that triggered this response.  The response body SHOULD contain a JSON error object. MUST be any HTTP status code specified in [RFC 7231](https://tools.ietf.org/html/rfc7231#section-6.6).  See also: * [Error Handling](#section/API-Principles/Error-Handling) in the API in general. * [Common Error Codes](errors.json) (status code 500)
     */
    @ApiOperation(value = "OGC specifications this API conforms to", nickname = "conformance", notes = "A list of all conformance classes specified in OGC standards that the server conforms to. This endpoint is only required if full compliance with OGC API standards is desired. Therefore, openEO back-ends may implement it for compatibility with OGC API clients and openEO clients don't need to request it.", response = OGCConformanceResponse.class, tags={ "Capabilities", })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "The URIs of all conformance classes supported by the server.", response = OGCConformanceResponse.class),
        @ApiResponse(code = 500, message = "The request can't be fulfilled due to an error at the back-end. The error is never the client’s fault and therefore it is reasonable for the client to retry the exact same request that triggered this response.  The response body SHOULD contain a JSON error object. MUST be any HTTP status code specified in [RFC 7231](https://tools.ietf.org/html/rfc7231#section-6.6).  See also: * [Error Handling](#section/API-Principles/Error-Handling) in the API in general. * [Common Error Codes](errors.json)", response = Error.class) })
    @RequestMapping(value = "/conformance",
        produces = { "application/json" }, 
        method = RequestMethod.GET)
    default ResponseEntity<OGCConformanceResponse> conformance() {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"conformsTo\" : [ \"http://www.opengis.net/spec/ogcapi-features-1/1.0/conf/core\", \"http://www.opengis.net/spec/ogcapi-features-1/1.0/conf/core\" ] }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

}
