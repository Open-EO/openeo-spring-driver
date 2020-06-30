/**
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech) (5.0.0-SNAPSHOT).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */
package org.openeo.spring.api;

import org.openeo.spring.model.Collection;
import org.openeo.spring.model.CollectionsResponse;
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
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2020-06-30T15:12:47.411+02:00[Europe/Rome]")
@Validated
@Api(value = "collections", description = "the collections API")
public interface CollectionsApi {

    default Optional<NativeWebRequest> getRequest() {
        return Optional.empty();
    }

    /**
     * GET /collections/{collection_id} : Full metadata for a specific dataset
     * Lists **all** information about a specific collection specified by the identifier &#x60;collection_id&#x60;.  This endpoint is compatible with [STAC 0.9.0](https://stacspec.org) and [OGC API - Features](http://docs.opengeospatial.org/is/17-069r3/17-069r3.html). [STAC API](https://github.com/radiantearth/stac-spec/tree/v0.9.0/api-spec) features / extensions and [STAC extensions](https://github.com/radiantearth/stac-spec/tree/v0.9.0/extensions) can be implemented in addition to what is documented here.
     *
     * @param collectionId Collection identifier (required)
     * @return JSON object with metadata about the collection. (status code 200)
     *         or The request can&#39;t be fulfilled due to an error on client-side, i.e. the request is invalid. The client should not repeat the request without modifications.  The response body SHOULD contain a JSON error object. MUST be any HTTP status code specified in [RFC 7231](https://tools.ietf.org/html/rfc7231#section-6.6). This request MUST respond with HTTP status codes 401 if authorization is required or 403 if the authorization failed or access is forbidden in general to the authenticated user. HTTP status code 404 should be used if the value of a path parameter is invalid.  See also: * [Error Handling](#section/API-Principles/Error-Handling) in the API in general. * [Common Error Codes](errors.json) (status code 400)
     *         or The request can&#39;t be fulfilled due to an error at the back-end. The error is never the client’s fault and therefore it is reasonable for the client to retry the exact same request that triggered this response.  The response body SHOULD contain a JSON error object. MUST be any HTTP status code specified in [RFC 7231](https://tools.ietf.org/html/rfc7231#section-6.6).  See also: * [Error Handling](#section/API-Principles/Error-Handling) in the API in general. * [Common Error Codes](errors.json) (status code 500)
     */
    @ApiOperation(value = "Full metadata for a specific dataset", nickname = "describeCollection", notes = "Lists **all** information about a specific collection specified by the identifier `collection_id`.  This endpoint is compatible with [STAC 0.9.0](https://stacspec.org) and [OGC API - Features](http://docs.opengeospatial.org/is/17-069r3/17-069r3.html). [STAC API](https://github.com/radiantearth/stac-spec/tree/v0.9.0/api-spec) features / extensions and [STAC extensions](https://github.com/radiantearth/stac-spec/tree/v0.9.0/extensions) can be implemented in addition to what is documented here.", response = Collection.class, authorizations = {
        @Authorization(value = "Bearer")
    }, tags={ "EO Data Discovery", })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "JSON object with metadata about the collection.", response = Collection.class),
        @ApiResponse(code = 400, message = "The request can't be fulfilled due to an error on client-side, i.e. the request is invalid. The client should not repeat the request without modifications.  The response body SHOULD contain a JSON error object. MUST be any HTTP status code specified in [RFC 7231](https://tools.ietf.org/html/rfc7231#section-6.6). This request MUST respond with HTTP status codes 401 if authorization is required or 403 if the authorization failed or access is forbidden in general to the authenticated user. HTTP status code 404 should be used if the value of a path parameter is invalid.  See also: * [Error Handling](#section/API-Principles/Error-Handling) in the API in general. * [Common Error Codes](errors.json)", response = Error.class),
        @ApiResponse(code = 500, message = "The request can't be fulfilled due to an error at the back-end. The error is never the client’s fault and therefore it is reasonable for the client to retry the exact same request that triggered this response.  The response body SHOULD contain a JSON error object. MUST be any HTTP status code specified in [RFC 7231](https://tools.ietf.org/html/rfc7231#section-6.6).  See also: * [Error Handling](#section/API-Principles/Error-Handling) in the API in general. * [Common Error Codes](errors.json)", response = Error.class) })
    @RequestMapping(value = "/collections/{collection_id}",
        produces = { "application/json" }, 
        method = RequestMethod.GET)
    default ResponseEntity<Collection> describeCollection(@Pattern(regexp="^[\\w\\-\\.~/]+$") @ApiParam(value = "Collection identifier",required=true) @PathVariable("collection_id") String collectionId) {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"stac_version\" : \"0.9.0\", \"stac_extensions\" : [ \"collection-assets\", \"datacube\", \"scientific\", \"version\" ], \"id\" : \"Sentinel-2\", \"title\" : \"Sentinel-2 MSI L2A\", \"description\" : \"Sentinel-2A is a wide-swath, high-resolution, multi-spectral imaging mission supporting Copernicus Land Monitoring studies.\", \"license\" : \"proprietary\", \"keywords\" : [ \"copernicus\", \"esa\", \"msi\", \"sentinel\" ], \"version\" : \"20200106A\", \"providers\" : [ { \"name\" : \"European Space Agency (ESA)\", \"roles\" : [ \"producer\", \"licensor\" ], \"url\" : \"https://sentinel.esa.int/web/sentinel/user-guides/sentinel-2-msi\" }, { \"name\" : \"Google\", \"roles\" : [ \"host\" ], \"url\" : \"https://developers.google.com/earth-engine/datasets/catalog/COPERNICUS_S2\" } ], \"extent\" : { \"spatial\" : { \"bbox\" : [ [ -180, -56, 180, 83 ] ] }, \"temporal\" : { \"interval\" : [ [ \"2015-06-23T00:00:00Z\", null ] ] } }, \"links\" : [ { \"rel\" : \"license\", \"href\" : \"https://scihub.copernicus.eu/twiki/pub/SciHubWebPortal/TermsConditions/Sentinel_Data_Terms_and_Conditions.pdf\", \"type\" : \"application/pdf\" }, { \"rel\" : \"about\", \"href\" : \"https://earth.esa.int/web/sentinel/user-guides/sentinel-2-msi/product-types/level-1c\", \"type\" : \"text/html\", \"title\" : \"ESA Sentinel-2 MSI Level-1C User Guide\" }, { \"rel\" : \"example\", \"href\" : \"https://example.openeo.org/api/collections/Sentinel-2/examples/true-color.json\", \"type\" : \"application/json\", \"title\" : \"Example Process for True-Color Visualization\" }, { \"rel\" : \"example\", \"href\" : \"https://example.openeo.org/api/collections/Sentinel-2/examples/ndvi.json\", \"type\" : \"application/json\", \"title\" : \"Example Process for NDVI Calculation and Visualization\" } ], \"cube:dimensions\" : { \"x\" : { \"type\" : \"spatial\", \"axis\" : \"x\", \"extent\" : [ -180, 180 ], \"reference_system\" : 4326 }, \"y\" : { \"type\" : \"spatial\", \"axis\" : \"y\", \"extent\" : [ -56, 83 ], \"reference_system\" : 4326 }, \"t\" : { \"type\" : \"temporal\", \"extent\" : [ \"2015-06-23T00:00:00Z\", null ], \"step\" : null }, \"bands\" : { \"type\" : \"bands\", \"values\" : [ \"B1\", \"B2\", \"B3\", \"B4\", \"B5\", \"B6\", \"B7\", \"B8\", \"B8A\", \"B9\", \"B10\", \"B11\", \"B12\" ] } }, \"sci:citation\" : \"Copernicus Sentinel data [Year]\", \"summaries\" : { \"constellation\" : [ \"Sentinel-2\" ], \"platform\" : [ \"Sentinel-2A\", \"Sentinel-2B\" ], \"instruments\" : [ \"MSI\" ], \"eo:cloud_cover\" : { \"min\" : 0, \"max\" : 75 }, \"sat:orbit_state\" : [ \"ascending\", \"descending\" ], \"eo:gsd\" : [ 10, 20, 60 ], \"eo:bands\" : [ { \"name\" : \"B1\", \"common_name\" : \"coastal\", \"center_wavelength\" : 0.4439, \"gsd\" : 60 }, { \"name\" : \"B2\", \"common_name\" : \"blue\", \"center_wavelength\" : 0.4966, \"gsd\" : 10 }, { \"name\" : \"B3\", \"common_name\" : \"green\", \"center_wavelength\" : 0.56, \"gsd\" : 10 }, { \"name\" : \"B4\", \"common_name\" : \"red\", \"center_wavelength\" : 0.6645, \"gsd\" : 10 }, { \"name\" : \"B5\", \"center_wavelength\" : 0.7039, \"gsd\" : 20 }, { \"name\" : \"B6\", \"center_wavelength\" : 0.7402, \"gsd\" : 20 }, { \"name\" : \"B7\", \"center_wavelength\" : 0.7825, \"gsd\" : 20 }, { \"name\" : \"B8\", \"common_name\" : \"nir\", \"center_wavelength\" : 0.8351, \"gsd\" : 10 }, { \"name\" : \"B8A\", \"common_name\" : \"nir08\", \"center_wavelength\" : 0.8648, \"gsd\" : 20 }, { \"name\" : \"B9\", \"common_name\" : \"nir09\", \"center_wavelength\" : 0.945, \"gsd\" : 60 }, { \"name\" : \"B10\", \"common_name\" : \"cirrus\", \"center_wavelength\" : 1.3735, \"gsd\" : 60 }, { \"name\" : \"B11\", \"common_name\" : \"swir16\", \"center_wavelength\" : 1.6137, \"gsd\" : 20 }, { \"name\" : \"B12\", \"common_name\" : \"swir22\", \"center_wavelength\" : 2.2024, \"gsd\" : 20 } ], \"proj:epsg\" : { \"min\" : 32601, \"max\" : 32660 } }, \"assets\" : { \"thumbnail\" : { \"href\" : \"https://example.openeo.org/api/collections/Sentinel-2/thumbnail.png\", \"type\" : \"image/png\", \"title\" : \"Preview\", \"roles\" : [ \"thumbnail\" ] }, \"inspire\" : { \"href\" : \"https://example.openeo.org/api/collections/Sentinel-2/inspire.xml\", \"type\" : \"application/xml\", \"title\" : \"INSPIRE metadata\", \"description\" : \"INSPIRE compliant XML metadata\", \"roles\" : [ \"metadata\" ] } } }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }


    /**
     * GET /collections : Basic metadata for all datasets
     * Lists available collections with at least the required information.  Back-ends can choose which optional information are not send, but should ensure the responses don&#39;t get overly large. To retrieve all information clients must make a request to &#x60;GET /collections/{collection_id}&#x60;.  This endpoint is compatible with [STAC 0.9.0](https://stacspec.org) and [OGC API - Features](http://docs.opengeospatial.org/is/17-069r3/17-069r3.html). [STAC API](https://github.com/radiantearth/stac-spec/tree/v0.9.0/api-spec) features / extensions and [STAC extensions](https://github.com/radiantearth/stac-spec/tree/v0.9.0/extensions) can be implemented in addition to what is documented here.
     *
     * @param limit This parameter enables pagination for the endpoint and specifies the maximum number of elements that arrays in the top-level object (e.g. jobs or log entries) are allowed to contain. The only exception is the &#x60;links&#x60; array, which MUST NOT be paginated as otherwise the pagination links may be missing ins responses. If the parameter is not provided or empty, all elements are returned.  Pagination is OPTIONAL and back-ends and clients may not support it. Therefore it MUST be implemented in a way that clients not supporting pagination get all resources regardless. Back-ends not supporting  pagination will return all resources.  If the response is paginated, the links array MUST be used to propagate the  links for pagination with pre-defined &#x60;rel&#x60; types. See the links array schema for supported &#x60;rel&#x60; types.  *Note:* Implementations can use all kind of pagination techniques, depending on what is supported best by their infrastructure. So it doesn&#39;t care whether it is page-based, offset-based or uses tokens for pagination. The clients will use whatever is specified in the links with the corresponding &#x60;rel&#x60; types. (optional)
     * @return Lists of collections and related links. (status code 200)
     *         or The request can&#39;t be fulfilled due to an error on client-side, i.e. the request is invalid. The client should not repeat the request without modifications.  The response body SHOULD contain a JSON error object. MUST be any HTTP status code specified in [RFC 7231](https://tools.ietf.org/html/rfc7231#section-6.6). This request MUST respond with HTTP status codes 401 if authorization is required or 403 if the authorization failed or access is forbidden in general to the authenticated user. HTTP status code 404 should be used if the value of a path parameter is invalid.  See also: * [Error Handling](#section/API-Principles/Error-Handling) in the API in general. * [Common Error Codes](errors.json) (status code 400)
     *         or The request can&#39;t be fulfilled due to an error at the back-end. The error is never the client’s fault and therefore it is reasonable for the client to retry the exact same request that triggered this response.  The response body SHOULD contain a JSON error object. MUST be any HTTP status code specified in [RFC 7231](https://tools.ietf.org/html/rfc7231#section-6.6).  See also: * [Error Handling](#section/API-Principles/Error-Handling) in the API in general. * [Common Error Codes](errors.json) (status code 500)
     */
    @ApiOperation(value = "Basic metadata for all datasets", nickname = "listCollections", notes = "Lists available collections with at least the required information.  Back-ends can choose which optional information are not send, but should ensure the responses don't get overly large. To retrieve all information clients must make a request to `GET /collections/{collection_id}`.  This endpoint is compatible with [STAC 0.9.0](https://stacspec.org) and [OGC API - Features](http://docs.opengeospatial.org/is/17-069r3/17-069r3.html). [STAC API](https://github.com/radiantearth/stac-spec/tree/v0.9.0/api-spec) features / extensions and [STAC extensions](https://github.com/radiantearth/stac-spec/tree/v0.9.0/extensions) can be implemented in addition to what is documented here.", response = CollectionsResponse.class, authorizations = {
        @Authorization(value = "Bearer")
    }, tags={ "EO Data Discovery", })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "Lists of collections and related links.", response = CollectionsResponse.class),
        @ApiResponse(code = 400, message = "The request can't be fulfilled due to an error on client-side, i.e. the request is invalid. The client should not repeat the request without modifications.  The response body SHOULD contain a JSON error object. MUST be any HTTP status code specified in [RFC 7231](https://tools.ietf.org/html/rfc7231#section-6.6). This request MUST respond with HTTP status codes 401 if authorization is required or 403 if the authorization failed or access is forbidden in general to the authenticated user. HTTP status code 404 should be used if the value of a path parameter is invalid.  See also: * [Error Handling](#section/API-Principles/Error-Handling) in the API in general. * [Common Error Codes](errors.json)", response = Error.class),
        @ApiResponse(code = 500, message = "The request can't be fulfilled due to an error at the back-end. The error is never the client’s fault and therefore it is reasonable for the client to retry the exact same request that triggered this response.  The response body SHOULD contain a JSON error object. MUST be any HTTP status code specified in [RFC 7231](https://tools.ietf.org/html/rfc7231#section-6.6).  See also: * [Error Handling](#section/API-Principles/Error-Handling) in the API in general. * [Common Error Codes](errors.json)", response = Error.class) })
    @RequestMapping(value = "/collections",
        produces = { "application/json" }, 
        method = RequestMethod.GET)
    default ResponseEntity<CollectionsResponse> listCollections(@Min(1)@ApiParam(value = "This parameter enables pagination for the endpoint and specifies the maximum number of elements that arrays in the top-level object (e.g. jobs or log entries) are allowed to contain. The only exception is the `links` array, which MUST NOT be paginated as otherwise the pagination links may be missing ins responses. If the parameter is not provided or empty, all elements are returned.  Pagination is OPTIONAL and back-ends and clients may not support it. Therefore it MUST be implemented in a way that clients not supporting pagination get all resources regardless. Back-ends not supporting  pagination will return all resources.  If the response is paginated, the links array MUST be used to propagate the  links for pagination with pre-defined `rel` types. See the links array schema for supported `rel` types.  *Note:* Implementations can use all kind of pagination techniques, depending on what is supported best by their infrastructure. So it doesn't care whether it is page-based, offset-based or uses tokens for pagination. The clients will use whatever is specified in the links with the corresponding `rel` types.") @Valid @RequestParam(value = "limit", required = false) Integer limit) {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "\"{\\"collections\\":[{\\"stac_version\\":\\"0.9.0\\",\\"id\\":\\"Sentinel-2A\\",\\"title\\":\\"Sentinel-2A MSI L1C\\",\\"description\\":\\"Sentinel-2A is a wide-swath, high-resolution, multi-spectral imaging mission supporting Copernicus Land Monitoring studies, including the monitoring of vegetation, soil and water cover, as well as observation of inland waterways and coastal areas.\\",\\"license\\":\\"proprietary\\",\\"extent\\":{\\"spatial\\":{\\"bbox\\":[[-180,-56,180,83]]},\\"temporal\\":{\\"interval\\":[[\\"2015-06-23T00:00:00Z\\",\\"2019-01-01T00:00:00Z\\"]]}},\\"keywords\\":[\\"copernicus\\",\\"esa\\",\\"msi\\",\\"sentinel\\"],\\"providers\\":[{\\"name\\":\\"European Space Agency (ESA)\\",\\"roles\\":[\\"producer\\",\\"licensor\\"],\\"url\\":\\"https://sentinel.esa.int/web/sentinel/user-guides/sentinel-2-msi\\"},{\\"name\\":\\"openEO\\",\\"roles\\":[\\"host\\"],\\"url\\":\\"https://developers.google.com/earth-engine/datasets/catalog/COPERNICUS_S2\\"}],\\"links\\":[{\\"rel\\":\\"license\\",\\"href\\":\\"https://scihub.copernicus.eu/twiki/pub/SciHubWebPortal/TermsConditions/Sentinel_Data_Terms_and_Conditions.pdf\\"}]},{\\"stac_version\\":\\"0.9.0\\",\\"id\\":\\"MOD09Q1\\",\\"title\\":\\"MODIS/Terra Surface Reflectance 8-Day L3 Global 250m SIN Grid V006\\",\\"description\\":\\"The MOD09Q1 Version 6 product provides an estimate of the surface spectral reflectance of Terra MODIS Bands 1-2 corrected for atmospheric conditions such as gasses, aerosols, and Rayleigh scattering. Provided along with the two 250 m MODIS bands is one additional layer, the Surface Reflectance QC 250 m band. For each pixel, a value is selected from all the acquisitions within the 8-day composite period. The criteria for the pixel choice include cloud and solar zenith. When several acquisitions meet the criteria the pixel with the minimum channel 3 (blue) value is used. Validation at stage 3 has been achieved for all MODIS Surface Reflectance products.\\",\\"license\\":\\"proprietary\\",\\"extent\\":{\\"spatial\\":{\\"bbox\\":[[-180,-90,180,90]]},\\"temporal\\":{\\"interval\\":[[\\"2000-02-01T00:00:00Z\\",null]]}},\\"links\\":[{\\"rel\\":\\"license\\",\\"href\\":\\"https://example.openeo.org/api/collections/MOD09Q1/license\\"}]}],\\"links\\":[{\\"rel\\":\\"alternate\\",\\"href\\":\\"https://example.openeo.org/csw\\",\\"title\\":\\"openEO catalog (OGC Catalogue Services 3.0)\\"}]}\"";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

}
