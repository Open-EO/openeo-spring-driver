/**
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech) (5.0.0-SNAPSHOT).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */
package org.openeo.spring.api;

import org.openeo.spring.model.Error;
import org.openeo.spring.model.ProcessesResponse;
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
@Api(value = "processes", description = "the processes API")
public interface ProcessesApi {

    default Optional<NativeWebRequest> getRequest() {
        return Optional.empty();
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
    @ApiOperation(value = "Supported predefined processes", nickname = "listProcesses", notes = "The request asks the back-end for available predefined processes and returns detailed process descriptions, including parameters and return values.", response = ProcessesResponse.class, authorizations = {
        @Authorization(value = "Bearer")
    }, tags={ "Process Discovery", })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "Formal specification describing the supported predefined processes.", response = ProcessesResponse.class),
        @ApiResponse(code = 400, message = "The request can't be fulfilled due to an error on client-side, i.e. the request is invalid. The client should not repeat the request without modifications.  The response body SHOULD contain a JSON error object. MUST be any HTTP status code specified in [RFC 7231](https://tools.ietf.org/html/rfc7231#section-6.6). This request MUST respond with HTTP status codes 401 if authorization is required or 403 if the authorization failed or access is forbidden in general to the authenticated user. HTTP status code 404 should be used if the value of a path parameter is invalid.  See also: * [Error Handling](#section/API-Principles/Error-Handling) in the API in general. * [Common Error Codes](errors.json)", response = Error.class),
        @ApiResponse(code = 500, message = "The request can't be fulfilled due to an error at the back-end. The error is never the client’s fault and therefore it is reasonable for the client to retry the exact same request that triggered this response.  The response body SHOULD contain a JSON error object. MUST be any HTTP status code specified in [RFC 7231](https://tools.ietf.org/html/rfc7231#section-6.6).  See also: * [Error Handling](#section/API-Principles/Error-Handling) in the API in general. * [Common Error Codes](errors.json)", response = Error.class) })
    @RequestMapping(value = "/processes",
        produces = { "application/json" }, 
        method = RequestMethod.GET)
    default ResponseEntity<ProcessesResponse> listProcesses(@Min(1)@ApiParam(value = "This parameter enables pagination for the endpoint and specifies the maximum number of elements that arrays in the top-level object (e.g. jobs or log entries) are allowed to contain. The only exception is the `links` array, which MUST NOT be paginated as otherwise the pagination links may be missing ins responses. If the parameter is not provided or empty, all elements are returned.  Pagination is OPTIONAL and back-ends and clients may not support it. Therefore it MUST be implemented in a way that clients not supporting pagination get all resources regardless. Back-ends not supporting  pagination will return all resources.  If the response is paginated, the links array MUST be used to propagate the  links for pagination with pre-defined `rel` types. See the links array schema for supported `rel` types.  *Note:* Implementations can use all kind of pagination techniques, depending on what is supported best by their infrastructure. So it doesn't care whether it is page-based, offset-based or uses tokens for pagination. The clients will use whatever is specified in the links with the corresponding `rel` types.") @Valid @RequestParam(value = "limit", required = false) Integer limit) {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "\"{\\"processes\\":[{\\"id\\":\\"apply\\",\\"summary\\":\\"Apply a process to each pixel\\",\\"description\\":\\"Applies a *unary* process to each pixel value in the data cube (i.e. a local operation). A unary process takes a single value and returns a single value, for example ``abs()`` or ``linear_scale_range()``.\\",\\"categories\\":[\\"cubes\\"],\\"parameters\\":[{\\"name\\":\\"data\\",\\"description\\":\\"A data cube.\\",\\"schema\\":{\\"type\\":\\"object\\",\\"subtype\\":\\"raster-cube\\"}},{\\"name\\":\\"process\\",\\"description\\":\\"A unary process to be applied on each value, may consist of multiple sub-processes.\\",\\"schema\\":{\\"type\\":\\"object\\",\\"subtype\\":\\"process-graph\\",\\"parameters\\":[{\\"name\\":\\"x\\",\\"description\\":\\"The value to process.\\",\\"schema\\":{\\"description\\":\\"Any data type.\\"}}]}}],\\"returns\\":{\\"description\\":\\"A data cube with the newly computed values. The resolution, cardinality and the number of dimensions are the same as for the original data cube.\\",\\"schema\\":{\\"type\\":\\"object\\",\\"subtype\\":\\"raster-cube\\"}}},{\\"id\\":\\"multiply\\",\\"summary\\":\\"Multiplication of two numbers\\",\\"description\\":\\"Multiplies the two numbers `x` and `y` (*x * y*) and returns the computed product.\\n\\nNo-data values are taken into account so that `null` is returned if any element is such a value.\\n\\nThe computations follow [IEEE Standard 754](https://ieeexplore.ieee.org/document/8766229) whenever the processing environment supports it.\\",\\"categories\\":[\\"math\\"],\\"parameters\\":[{\\"name\\":\\"x\\",\\"description\\":\\"The multiplier.\\",\\"schema\\":{\\"type\\":[\\"number\\",\\"null\\"]}},{\\"name\\":\\"y\\",\\"description\\":\\"The multiplicand.\\",\\"schema\\":{\\"type\\":[\\"number\\",\\"null\\"]}}],\\"returns\\":{\\"description\\":\\"The computed product of the two numbers.\\",\\"schema\\":{\\"type\\":[\\"number\\",\\"null\\"]}},\\"exceptions\\":{\\"MultiplicandMissing\\":{\\"message\\":\\"Multiplication requires at least two numbers.\\"}},\\"examples\\":[{\\"arguments\\":{\\"x\\":5,\\"y\\":2.5},\\"returns\\":12.5},{\\"arguments\\":{\\"x\\":-2,\\"y\\":-4},\\"returns\\":8},{\\"arguments\\":{\\"x\\":1,\\"y\\":null},\\"returns\\":null}],\\"links\\":[{\\"rel\\":\\"about\\",\\"href\\":\\"http://mathworld.wolfram.com/Product.html\\",\\"title\\":\\"Product explained by Wolfram MathWorld\\"},{\\"rel\\":\\"about\\",\\"href\\":\\"https://ieeexplore.ieee.org/document/8766229\\",\\"title\\":\\"IEEE Standard 754-2019 for Floating-Point Arithmetic\\"}]}],\\"links\\":[{\\"rel\\":\\"alternate\\",\\"href\\":\\"https://provider.com/processes\\",\\"type\\":\\"text/html\\",\\"title\\":\\"HTML version of the processes\\"}]}\"";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

}
