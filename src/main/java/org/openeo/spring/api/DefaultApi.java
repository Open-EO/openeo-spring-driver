/**
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech) (5.0.0-SNAPSHOT).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */
package org.openeo.spring.api;

import org.openeo.spring.model.CapabilitiesResponse;
import org.openeo.spring.model.Error;
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
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2020-06-30T14:48:14.663+02:00[Europe/Rome]")
@Validated
@Api(value = "default", description = "the default API")
public interface DefaultApi {

    default Optional<NativeWebRequest> getRequest() {
        return Optional.empty();
    }

    /**
     * GET / : Information about the back-end
     * Returns general information about the back-end, including which version and endpoints of the openEO API are supported. May also include billing information.
     *
     * @return Information about the API version and supported endpoints / features. (status code 200)
     *         or The request can&#39;t be fulfilled due to an error on client-side, i.e. the request is invalid. The client should not repeat the request without modifications.  The response body SHOULD contain a JSON error object. MUST be any HTTP status code specified in [RFC 7231](https://tools.ietf.org/html/rfc7231#section-6.6). This request usually does not respond with HTTP status codes 401 and 403 due to missing authorization. HTTP status code 404 should be used if the value of a path parameter is invalid.  See also: * [Error Handling](#section/API-Principles/Error-Handling) in the API in general. * [Common Error Codes](errors.json) (status code 400)
     *         or The request can&#39;t be fulfilled due to an error at the back-end. The error is never the client’s fault and therefore it is reasonable for the client to retry the exact same request that triggered this response.  The response body SHOULD contain a JSON error object. MUST be any HTTP status code specified in [RFC 7231](https://tools.ietf.org/html/rfc7231#section-6.6).  See also: * [Error Handling](#section/API-Principles/Error-Handling) in the API in general. * [Common Error Codes](errors.json) (status code 500)
     */
    @ApiOperation(value = "Information about the back-end", nickname = "capabilities", notes = "Returns general information about the back-end, including which version and endpoints of the openEO API are supported. May also include billing information.", response = CapabilitiesResponse.class, tags={ "Capabilities", })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "Information about the API version and supported endpoints / features.", response = CapabilitiesResponse.class),
        @ApiResponse(code = 400, message = "The request can't be fulfilled due to an error on client-side, i.e. the request is invalid. The client should not repeat the request without modifications.  The response body SHOULD contain a JSON error object. MUST be any HTTP status code specified in [RFC 7231](https://tools.ietf.org/html/rfc7231#section-6.6). This request usually does not respond with HTTP status codes 401 and 403 due to missing authorization. HTTP status code 404 should be used if the value of a path parameter is invalid.  See also: * [Error Handling](#section/API-Principles/Error-Handling) in the API in general. * [Common Error Codes](errors.json)", response = Error.class),
        @ApiResponse(code = 500, message = "The request can't be fulfilled due to an error at the back-end. The error is never the client’s fault and therefore it is reasonable for the client to retry the exact same request that triggered this response.  The response body SHOULD contain a JSON error object. MUST be any HTTP status code specified in [RFC 7231](https://tools.ietf.org/html/rfc7231#section-6.6).  See also: * [Error Handling](#section/API-Principles/Error-Handling) in the API in general. * [Common Error Codes](errors.json)", response = Error.class) })
    @RequestMapping(value = "/",
        produces = { "application/json" }, 
        method = RequestMethod.GET)
    default ResponseEntity<CapabilitiesResponse> capabilities() {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"backend_version\" : \"1.1.2\", \"endpoints\" : [ { \"path\" : \"/collections\", \"methods\" : [ \"GET\" ] }, { \"path\" : \"/collections/{collection_id}\", \"methods\" : [ \"GET\" ] }, { \"path\" : \"/processes\", \"methods\" : [ \"GET\" ] }, { \"path\" : \"/jobs\", \"methods\" : [ \"GET\", \"POST\" ] }, { \"path\" : \"/jobs/{job_id}\", \"methods\" : [ \"GET\", \"DELETE\", \"PATCH\" ] } ], \"production\" : false, \"description\" : \"This service is provided to you by [Cool EO Cloud Corp.](http://cool-eo-cloud-corp.com). It implements the full openEO API and allows to process a range of 999 EO data sets, including \n\n* Sentinel 1/2/3 and 5\n* Landsat 7/8\n\nA free plan is available to test the service. For further information please contact our customer service at [support@cool-eo-cloud-corp.com](mailto:support@cool-eo-cloud-corp.com).\", \"links\" : [ { \"href\" : \"http://www.cool-cloud-corp.com\", \"rel\" : \"about\", \"type\" : \"text/html\", \"title\" : \"Homepage of the service provider\" }, { \"href\" : \"https://www.cool-cloud-corp.com/tos\", \"rel\" : \"terms-of-service\", \"type\" : \"text/html\", \"title\" : \"Terms of Service\" }, { \"href\" : \"https://www.cool-cloud-corp.com/privacy\", \"rel\" : \"privacy-policy\", \"type\" : \"text/html\", \"title\" : \"Privacy Policy\" }, { \"href\" : \"http://www.cool-cloud-corp.com/.well-known/openeo\", \"rel\" : \"version-history\", \"type\" : \"application/json\", \"title\" : \"List of supported openEO versions\" }, { \"href\" : \"http://www.cool-cloud-corp.com/api/v1.0/conformance\", \"rel\" : \"conformance\", \"type\" : \"application/json\", \"title\" : \"OGC Conformance Classes\" }, { \"href\" : \"http://www.cool-cloud-corp.com/api/v1.0/collections\", \"rel\" : \"data\", \"type\" : \"application/json\", \"title\" : \"List of Datasets\" } ], \"id\" : \"cool-eo-cloud\", \"api_version\" : \"1.0.1\", \"title\" : \"Cool EO Cloud\", \"billing\" : { \"plans\" : [ { \"name\" : \"free\", \"description\" : \"Free plan. Calculates one tile per second and a maximum amount of 100 tiles per hour.\", \"url\" : \"http://cool-cloud-corp.com/plans/free-plan\", \"paid\" : false }, { \"name\" : \"premium\", \"description\" : \"Premium plan. Calculates unlimited tiles and each calculated tile costs 0.003 USD.\", \"url\" : \"http://cool-cloud-corp.com/plans/premium-plan\", \"paid\" : true } ], \"currency\" : \"USD\", \"default_plan\" : \"free\" } }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

}
