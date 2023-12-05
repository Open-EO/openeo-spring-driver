package org.openeo.spring.api;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.security.Principal;
import java.time.Duration;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.annotation.PostConstruct;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;

import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.keycloak.representations.AccessToken;
import org.openeo.spring.bearer.ITokenService;
import org.openeo.spring.bearer.TokenUtil;
import org.openeo.spring.components.CollectionMap;
import org.openeo.spring.components.CollectionsMap;
import org.openeo.spring.loaders.ICollectionsLoader;
import org.openeo.spring.loaders.ODCCollectionsLoader;
import org.openeo.spring.loaders.STACFileCollectionsLoader;
import org.openeo.spring.loaders.WCSCollectionsLoader;
import org.openeo.spring.model.Asset;
import org.openeo.spring.model.BatchJobResult;
import org.openeo.spring.model.Collection;
import org.openeo.spring.model.Collections;
import org.openeo.spring.model.EngineTypes;
import org.openeo.spring.model.Job;
import org.openeo.spring.model.JobStates;
import org.openeo.spring.model.Process;
import org.openeo.spring.model.Providers;
import org.openeo.wcps.ConvenienceHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.NativeWebRequest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2020-07-02T08:45:00.334+02:00[Europe/Rome]")
@RestController
@RequestMapping("${openapi.openEO.base-path:}")
public class CollectionsApiController implements CollectionsApi {

    /** Home directory used as root for cached catalogs. */
    public static final String CACHE_ROOT_DIR = System.getProperty("user.dir");
    
    @Autowired
    private JobsApiController jobsApiController;
    
    /** Whether to run both inter- and intra-catalogue parallelized loading. */
    @Value("${org.openeo.parallelizedHarvest}")
    private boolean parallelizedHarvest;

    @Value("${org.openeo.wcps.endpoint}")
    private String wcpsEndpoint;

    @Value("${org.openeo.wcps.endpoint.version}")
    private String wcpsVersion;

    @Value("${org.openeo.wcps.provider.name}")
    private String wcpsProviderName;

    @Value("${org.openeo.wcps.provider.type}")
    private String wcpsProviderType;

    @Value("${org.openeo.wcps.provider.url}")
    private String wcpsProviderUrl;

    @Value("${org.openeo.odc.collectionsEndpoint}")
    private String odcCollEndpoint;

    @Value("${org.openeo.odc.provider.name}")
    private String odcProviderName;

    @Value("${org.openeo.odc.provider.type}")
    private String odcProviderType;

    @Value("${org.openeo.odc.provider.url}")
    private String odcProviderUrl;

    @Value("${org.openeo.querycollectionsonstartup}")
    private boolean queryCollectionsOnStartup;

    @Value("${org.openeo.wcps.collections.list}")
    Resource collectionsFileWCPS;

    @Value("${org.openeo.odc.collections.list}")
    Resource collectionsFileODC;

    @Autowired
    private CollectionsMap collectionsMap;

    @Autowired
    private CollectionMap collectionMap;
    
	@Autowired(required = false)
	private ITokenService tokenService;

    private final NativeWebRequest request;

    private static final Logger log = LogManager.getLogger(CollectionsApiController.class);

    @org.springframework.beans.factory.annotation.Autowired
    public CollectionsApiController(NativeWebRequest request) {
        this.request = request;
    }

    @PostConstruct
    public void init() {

        Instant start = Instant.now();

        ExecutorService exec = Executors.newFixedThreadPool(
                parallelizedHarvest ? 2 : 1,
                new CustomizableThreadFactory("CollectionsLoaders-"));

        ICollectionsLoader wcsLoader = null;
        Future<Collections> wcsCatalog = null;

        ICollectionsLoader odcLoader = null;
        Future<Collections> odcCatalog = null;

        try {
            if (queryCollectionsOnStartup) {

                boolean hasWcpsEndpoint = !wcpsEndpoint.isEmpty();
                boolean hasOdcEndpoint = !odcCollEndpoint.isEmpty();

                // WC(P)S collections loader
                if (hasWcpsEndpoint) {
                    wcsLoader = WCSCollectionsLoader.Builder
                            .of(wcpsEndpoint)
                            .version(wcpsVersion)
                            .provider(new Providers()
                                    .name(wcpsProviderName)
                                    .roles(wcpsProviderType)
                                    .url(new URI(wcpsProviderUrl)))
                            .cache(collectionsFileWCPS)
                            .parallelism(parallelizedHarvest ? -1 : 1)
                            .build();
                }

                // ODC collections loader
                if (hasOdcEndpoint) {
                    odcLoader = ODCCollectionsLoader.Builder
                            .of(odcCollEndpoint)
                            .cache(collectionsFileODC)
                            .build();
                }

                if (!hasWcpsEndpoint && !hasOdcEndpoint) {
                    log.error("No STAC endpoint was specified.");
                    // TODO: what to do here? Throw exception?
                }

            } else {
                // cached STAC-WCS catalogue loader
                wcsLoader = STACFileCollectionsLoader.Builder
                        .of(collectionsFileWCPS)
                        .engine(EngineTypes.WCPS)
                        .build();

                // cached STAC-ODC catalogue loader
                odcLoader = STACFileCollectionsLoader.Builder
                        .of(collectionsFileODC)
                        .engine(EngineTypes.ODC_DASK)
                        .build();
            }
        } catch (URISyntaxException e) {
            log.error("Invalid URI provided.", e);
        }

        if (null != wcsLoader) {
            wcsCatalog = exec.submit(wcsLoader);
        }

        if (null != odcLoader) {
            odcCatalog = exec.submit(odcLoader);
        }

        // collect harvested collections
        try {
            if (null != wcsCatalog) {
                collectionsMap.put(wcsLoader.getEngineType(), wcsCatalog.get());
            }
            if (null != odcCatalog) {
                collectionsMap.put(odcLoader.getEngineType(), odcCatalog.get());
            }
        } catch (InterruptedException | ExecutionException e) {
            log.error("Error while loading catalog.", e);
        }

        log.debug(collectionsMap.keySet());

        // { collID -> coll  } mapping
        for (EngineTypes type : collectionsMap.keySet()) {
            collectionsMap.get(type).getCollections()
                .forEach(coll -> collectionMap.put(coll.getId(), coll));
        }

        // profiling
        Instant end = Instant.now();
        long ds = Duration.between(start, end).toMillis() / 1000; // TODO from Java 9: .toSeconds()
        long dms = Duration.between(start, end).minusSeconds(ds).toMillis();

        log.printf(Level.INFO, "Catalogues harvesting done (%d.%03d s).", ds, dms);
    }

    @Override
    public Optional<NativeWebRequest> getRequest() {
        return Optional.ofNullable(request);
    }

    /**
     * GET /collections : Basic metadata for all datasets Lists available
     * collections with at least the required information. It is **strongly
     * RECOMMENDED** to keep the response size small by omitting larger optional
     * values from the objects in &#x60;collections&#x60; (e.g. the
     * &#x60;summaries&#x60; and &#x60;cube:dimensions&#x60; properties). To get the
     * full metadata for a collection clients MUST request &#x60;GET
     * /collections/{collection_id}&#x60;. This endpoint is compatible with [STAC
     * 0.9.0](https://stacspec.org) and [OGC API -
     * Features](http://docs.opengeospatial.org/is/17-069r3/17-069r3.html). [STAC
     * API](https://github.com/radiantearth/stac-spec/tree/v0.9.0/api-spec) features
     * / extensions and [STAC
     * extensions](https://github.com/radiantearth/stac-spec/tree/v0.9.0/extensions)
     * can be implemented in addition to what is documented here.
     *
     * @param limit This parameter enables pagination for the endpoint and specifies
     *              the maximum number of elements that arrays in the top-level
     *              object (e.g. jobs or log entries) are allowed to contain. The
     *              only exception is the &#x60;links&#x60; array, which MUST NOT be
     *              paginated as otherwise the pagination links may be missing ins
     *              responses. If the parameter is not provided or empty, all
     *              elements are returned. Pagination is OPTIONAL and back-ends and
     *              clients may not support it. Therefore it MUST be implemented in
     *              a way that clients not supporting pagination get all resources
     *              regardless. Back-ends not supporting pagination will return all
     *              resources. If the response is paginated, the links array MUST be
     *              used to propagate the links for pagination with pre-defined
     *              &#x60;rel&#x60; types. See the links array schema for supported
     *              &#x60;rel&#x60; types. *Note:* Implementations can use all kind
     *              of pagination techniques, depending on what is supported best by
     *              their infrastructure. So it doesn&#39;t care whether it is
     *              page-based, offset-based or uses tokens for pagination. The
     *              clients will use whatever is specified in the links with the
     *              corresponding &#x60;rel&#x60; types. (optional)
     * @return Lists of collections and related links. (status code 200) or The
     *         request can&#39;t be fulfilled due to an error on client-side, i.e.
     *         the request is invalid. The client should not repeat the request
     *         without modifications. The response body SHOULD contain a JSON error
     *         object. MUST be any HTTP status code specified in [RFC
     *         7231](https://tools.ietf.org/html/rfc7231#section-6.6). This request
     *         MUST respond with HTTP status codes 401 if authorization is required
     *         or 403 if the authorization failed or access is forbidden in general
     *         to the authenticated user. HTTP status code 404 should be used if the
     *         value of a path parameter is invalid. See also: * [Error
     *         Handling](#section/API-Principles/Error-Handling) in the API in
     *         general. * [Common Error Codes](errors.json) (status code 400) or The
     *         request can&#39;t be fulfilled due to an error at the back-end. The
     *         error is never the client’s fault and therefore it is reasonable for
     *         the client to retry the exact same request that triggered this
     *         response. The response body SHOULD contain a JSON error object. MUST
     *         be any HTTP status code specified in [RFC
     *         7231](https://tools.ietf.org/html/rfc7231#section-6.6). See also: *
     *         [Error Handling](#section/API-Principles/Error-Handling) in the API
     *         in general. * [Common Error Codes](errors.json) (status code 500)
     */
    @Operation(summary = "Basic metadata for all datasets", operationId = "listCollections", description = "Lists available collections with at least the required information.  It is **strongly RECOMMENDED** to keep the response size small by omitting larger optional values from the objects in `collections` (e.g. the `summaries` and `cube:dimensions` properties). To get the full metadata for a collection clients MUST request `GET /collections/{collection_id}`.  This endpoint is compatible with [STAC 0.9.0](https://stacspec.org) and [OGC API - Features](http://docs.opengeospatial.org/is/17-069r3/17-069r3.html). [STAC API](https://github.com/radiantearth/stac-spec/tree/v0.9.0/api-spec) features / extensions and [STAC extensions](https://github.com/radiantearth/stac-spec/tree/v0.9.0/extensions) can be implemented in addition to what is documented here.")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Lists of collections and related links."),
            @ApiResponse(responseCode = "400", description = "The request can't be fulfilled due to an error on client-side, i.e. the request is invalid. The client should not repeat the request without modifications.  The response body SHOULD contain a JSON error object. MUST be any HTTP status code specified in [RFC 7231](https://tools.ietf.org/html/rfc7231#section-6.6). This request MUST respond with HTTP status codes 401 if authorization is required or 403 if the authorization failed or access is forbidden in general to the authenticated user. HTTP status code 404 should be used if the value of a path parameter is invalid.  See also: * [Error Handling](#section/API-Principles/Error-Handling) in the API in general. * [Common Error Codes](errors.json)"),
            @ApiResponse(responseCode = "500", description = "The request can't be fulfilled due to an error at the back-end. The error is never the client’s fault and therefore it is reasonable for the client to retry the exact same request that triggered this response.  The response body SHOULD contain a JSON error object. MUST be any HTTP status code specified in [RFC 7231](https://tools.ietf.org/html/rfc7231#section-6.6).  See also: * [Error Handling](#section/API-Principles/Error-Handling) in the API in general. * [Common Error Codes](errors.json)") })
    @GetMapping(value = "/collections", produces = { "application/json" })
    @Override
    public ResponseEntity<Collections> listCollections(
            @Min(1) @Parameter(name = "This parameter enables pagination for the endpoint and specifies the maximum number of elements that arrays in the top-level object (e.g. jobs or log entries) are allowed to contain. The only exception is the `links` array, which MUST NOT be paginated as otherwise the pagination links may be missing ins responses. If the parameter is not provided or empty, all elements are returned.  Pagination is OPTIONAL and back-ends and clients may not support it. Therefore it MUST be implemented in a way that clients not supporting pagination get all resources regardless. Back-ends not supporting  pagination will return all resources.  If the response is paginated, the links array MUST be used to propagate the  links for pagination with pre-defined `rel` types. See the links array schema for supported `rel` types.  *Note:* Implementations can use all kind of pagination techniques, depending on what is supported best by their infrastructure. So it doesn't care whether it is page-based, offset-based or uses tokens for pagination. The clients will use whatever is specified in the links with the corresponding `rel` types.") @Valid @RequestParam(value = "limit", required = false) Integer limit) {
        Collections collectionsList = new Collections();

        for (EngineTypes type : collectionsMap.keySet()) {
            for (Collection currentCollection : collectionsMap.get(type).getCollections()) {
                collectionsList.addCollectionsItem(currentCollection);
            }
        }

        return new ResponseEntity<Collections>(collectionsList, HttpStatus.OK);
    }

    /**
     * GET /collections/{collection_id} : Full metadata for a specific dataset Lists
     * **all** information about a specific collection specified by the identifier
     * &#x60;collection_id&#x60;. This endpoint is compatible with [STAC
     * 0.9.0](https://stacspec.org) and [OGC API -
     * Features](http://docs.opengeospatial.org/is/17-069r3/17-069r3.html). [STAC
     * API](https://github.com/radiantearth/stac-spec/tree/v0.9.0/api-spec) features
     * / extensions and [STAC
     * extensions](https://github.com/radiantearth/stac-spec/tree/v0.9.0/extensions)
     * can be implemented in addition to what is documented here.
     *
     * @param collectionId Collection identifier (required)
     * @return JSON object with the full collection metadata. (status code 200) or
     *         The request can&#39;t be fulfilled due to an error on client-side,
     *         i.e. the request is invalid. The client should not repeat the request
     *         without modifications. The response body SHOULD contain a JSON error
     *         object. MUST be any HTTP status code specified in [RFC
     *         7231](https://tools.ietf.org/html/rfc7231#section-6.6). This request
     *         MUST respond with HTTP status codes 401 if authorization is required
     *         or 403 if the authorization failed or access is forbidden in general
     *         to the authenticated user. HTTP status code 404 should be used if the
     *         value of a path parameter is invalid. See also: * [Error
     *         Handling](#section/API-Principles/Error-Handling) in the API in
     *         general. * [Common Error Codes](errors.json) (status code 400) or The
     *         request can&#39;t be fulfilled due to an error at the back-end. The
     *         error is never the client’s fault and therefore it is reasonable for
     *         the client to retry the exact same request that triggered this
     *         response. The response body SHOULD contain a JSON error object. MUST
     *         be any HTTP status code specified in [RFC
     *         7231](https://tools.ietf.org/html/rfc7231#section-6.6). See also: *
     *         [Error Handling](#section/API-Principles/Error-Handling) in the API
     *         in general. * [Common Error Codes](errors.json) (status code 500)
     */

    @Operation(summary = "Full metadata for a specific dataset", operationId = "describeCollecion", description = "Lists **all** information about a specific collection specified by the identifier `collection_id`.  This endpoint is compatible with [STAC 0.9.0](https://stacspec.org) and [OGC API - Features](http://docs.opengeospatial.org/is/17-069r3/17-069r3.html). [STAC API](https://github.com/radiantearth/stac-spec/tree/v0.9.0/api-spec) features / extensions and [STAC extensions](https://github.com/radiantearth/stac-spec/tree/v0.9.0/extensions) can be implemented in addition to what is documented here.", tags = {
            "EO Data Discovery", })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "JSON object with the full collection metadata."),
            @ApiResponse(responseCode = "400", description = "The request can't be fulfilled due to an error on client-side, i.e. the request is invalid. The client should not repeat the request without modifications.  The response body SHOULD contain a JSON error object. MUST be any HTTP status code specified in [RFC 7231](https://tools.ietf.org/html/rfc7231#section-6.6). This request MUST respond with HTTP status codes 401 if authorization is required or 403 if the authorization failed or access is forbidden in general to the authenticated user. HTTP status code 404 should be used if the value of a path parameter is invalid.  See also: * [Error Handling](#section/API-Principles/Error-Handling) in the API in general. * [Common Error Codes](errors.json)"),
            @ApiResponse(responseCode = "500", description = "The request can't be fulfilled due to an error at the back-end. The error is never the client’s fault and therefore it is reasonable for the client to retry the exact same request that triggered this response.  The response body SHOULD contain a JSON error object. MUST be any HTTP status code specified in [RFC 7231](https://tools.ietf.org/html/rfc7231#section-6.6).  See also: * [Error Handling](#section/API-Principles/Error-Handling) in the API in general. * [Common Error Codes](errors.json)") })
    @GetMapping(value = "/collections/{collection_id}", produces = { "application/json" })
    @Override
    public ResponseEntity<Collection> describeCollection(
            @Pattern(regexp = "^[\\w\\-\\.~/]+$") @Parameter(name = "Collection identifier", required = true) @PathVariable("collection_id") String collectionId,
            Principal principal) {

        //    	log.debug("The following user is authenticated: " + principal.getName());
        //    	Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        //    	if (!(authentication instanceof AnonymousAuthenticationToken)) {
        //    	    String currentUserName = authentication.getName();
        //    	    log.debug("The following user is authenticated: " + currentUserName);
        //    	}else {
        //    		log.warn("The current user is not authenticated!");
        //    	}

        Collection currentCollection = collectionMap.get(collectionId);
        if (currentCollection != null) {
            return new ResponseEntity<Collection>(currentCollection, HttpStatus.OK);
        }
        return new ResponseEntity<Collection>(HttpStatus.NOT_FOUND);
    }

/**
 * GET /collections/{collection_id}/coverage : Full metadata for a specific dataset Lists
 * **all** information about a specific collection specified by the identifier
 * &#x60;collection_id&#x60;. This endpoint is compatible with [STAC
 * 0.9.0](https://stacspec.org) and [OGC API -
 * Features](http://docs.opengeospatial.org/is/17-069r3/17-069r3.html). [STAC
 * API](https://github.com/radiantearth/stac-spec/tree/v0.9.0/api-spec) features
 * / extensions and [STAC
 * extensions](https://github.com/radiantearth/stac-spec/tree/v0.9.0/extensions)
 * can be implemented in addition to what is documented here.
 *
 * @param collectionId Collection identifier (required)
 * @return JSON object with the full collection metadata. (status code 200) or
 *         The request can&#39;t be fulfilled due to an error on client-side,
 *         i.e. the request is invalid. The client should not repeat the request
 *         without modifications. The response body SHOULD contain a JSON error
 *         object. MUST be any HTTP status code specified in [RFC
 *         7231](https://tools.ietf.org/html/rfc7231#section-6.6). This request
 *         MUST respond with HTTP status codes 401 if authorization is required
 *         or 403 if the authorization failed or access is forbidden in general
 *         to the authenticated user. HTTP status code 404 should be used if the
 *         value of a path parameter is invalid. See also: * [Error
 *         Handling](#section/API-Principles/Error-Handling) in the API in
 *         general. * [Common Error Codes](errors.json) (status code 400) or The
 *         request can&#39;t be fulfilled due to an error at the back-end. The
 *         error is never the client’s fault and therefore it is reasonable for
 *         the client to retry the exact same request that triggered this
 *         response. The response body SHOULD contain a JSON error object. MUST
 *         be any HTTP status code specified in [RFC
 *         7231](https://tools.ietf.org/html/rfc7231#section-6.6). See also: *
 *         [Error Handling](#section/API-Principles/Error-Handling) in the API
 *         in general. * [Common Error Codes](errors.json) (status code 500)
 */

@Operation(summary = "Full metadata for a specific dataset", operationId = "describeCollecion", description = "Lists **all** information about a specific collection specified by the identifier `collection_id`.  This endpoint is compatible with [STAC 0.9.0](https://stacspec.org) and [OGC API - Features](http://docs.opengeospatial.org/is/17-069r3/17-069r3.html). [STAC API](https://github.com/radiantearth/stac-spec/tree/v0.9.0/api-spec) features / extensions and [STAC extensions](https://github.com/radiantearth/stac-spec/tree/v0.9.0/extensions) can be implemented in addition to what is documented here.", tags = {
        "EO Data Discovery", })
@ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "JSON object with the full collection metadata."),
        @ApiResponse(responseCode = "400", description = "The request can't be fulfilled due to an error on client-side, i.e. the request is invalid. The client should not repeat the request without modifications.  The response body SHOULD contain a JSON error object. MUST be any HTTP status code specified in [RFC 7231](https://tools.ietf.org/html/rfc7231#section-6.6). This request MUST respond with HTTP status codes 401 if authorization is required or 403 if the authorization failed or access is forbidden in general to the authenticated user. HTTP status code 404 should be used if the value of a path parameter is invalid.  See also: * [Error Handling](#section/API-Principles/Error-Handling) in the API in general. * [Common Error Codes](errors.json)"),
        @ApiResponse(responseCode = "500", description = "The request can't be fulfilled due to an error at the back-end. The error is never the client’s fault and therefore it is reasonable for the client to retry the exact same request that triggered this response.  The response body SHOULD contain a JSON error object. MUST be any HTTP status code specified in [RFC 7231](https://tools.ietf.org/html/rfc7231#section-6.6).  See also: * [Error Handling](#section/API-Principles/Error-Handling) in the API in general. * [Common Error Codes](errors.json)") })
@GetMapping(value = "/collections/{collection_id}/coverage", produces = { "*" })
@Override
public ResponseEntity<?> getCoverage(@Pattern(regexp="^[\\w\\-\\.~/]+$") @Parameter(name = "Collection identifier",required=true) @PathVariable("collection_id") String collectionId,
	    @RequestParam(name = "subset") Optional<String> subsetList,
	    @RequestParam(name = "bbox") Optional<List<Double>> bboxList,
	    @RequestParam(name = "datetime") Optional<String> temporalString, 
	    @RequestParam(name = "f") Optional<String> requestedFileFormat,
	    @RequestParam(name = "properties") Optional<List<String>> requestedProperties, 
	    Principal principal){
	if (principal == null) {
		return ApiUtil.errorResponse(HttpStatus.FORBIDDEN,"Please authenticate!");
	}

    // TODO: scale-axis -> spatial dimension -> map to openEO resample_spatial. Temporal and other -> return NOT_IMPLEMENTED
    // TODO: if only one slice provided (subset or datetime), translate into reduce_dimension + mean.
    
//	String username = new String();
	try{
		principal.getName();
	} catch(Exception e){
		log.error("Can't get name from principal. Trying to continue.");
	}
	AccessToken token = TokenUtil.getAccessToken(principal, tokenService);
//	if (null != token) {
//	    username = token.getName();
//	}
//    URL url;

	// Parse requested media type from request header
	String requestedFileFormatHeader = null;
	if (getRequest().isPresent()){
		Iterator<String> headerNames = request.getHeaderNames();
		if (headerNames.hasNext()) {
			String header = null;
			for (Iterator<String> iter = headerNames; iter.hasNext();) {
			    header =  iter.next();
			    log.debug("Header Name:" + header + "   " + "Header Value:" + request.getHeader(header));
			    if (header.toString().equals("accept")) {
			    	if (request.getHeader(header).toString().contains("netcdf")) {
			    		requestedFileFormatHeader = "netcdf";
			    	}
			    	else if (request.getHeader(header).toString().contains("tif")) {
			    		requestedFileFormatHeader = "geotiff";
			    	}
			    }
			}
		}
	}
	
	//We enforce the bbox and datetime parameters only if we want to return the data and not only the metadata
	//Enum: "png" "geotiff" "netcdf" "json" "covjson" "html" 
	String fileFormat = "netcdf";
	if (requestedFileFormat.isPresent()){
		fileFormat = requestedFileFormat.get();
	}
	// The file format from the request header has higher priority
	if (requestedFileFormatHeader != null) {
		fileFormat = requestedFileFormatHeader;
	}
	
	// Return an error if bbox or datetime are specified together as subset
//	if ((bboxList.isPresent() || temporalString.isPresent()) && (subsetList.isPresent())) {
//		return ApiUtil.errorResponse(HttpStatus.FORBIDDEN,"Please provide either bbox and datetime parameters, or subset. Can't parse both at the same time!");
//	}
	if (subsetList.isPresent()) {
		return ApiUtil.errorResponse(HttpStatus.FORBIDDEN,"Subset parameter not yet supported!");
	}
	// Return an error if no spatial and temporal subsets are specified
	if ((bboxList.isEmpty() || temporalString.isEmpty()) && (fileFormat=="geotiff" || fileFormat=="netcdf")) {
		return ApiUtil.errorResponse(HttpStatus.FORBIDDEN,"Please provide the bbox and datetime parameters, otherwise the file would be too large!");
	}

	// properties (bands) mapping
	// TODO: properties can be integers. I need to map them to their label
	String[] properties = null;
	if (requestedProperties.isPresent()){
		properties = requestedProperties.get().toArray(String[]::new);
	}
//	subset=Lon(9.8382568359375,11.2554931640625),Lat(43.5882568359375,45.0054931640625),time(%22023-10-01T0:00:00Z%22)

	
	// spatial and temporal extent parsing
    Map<String,Double> spatialExtent = new LinkedHashMap<>();
    spatialExtent.put("west",bboxList.get().get(0));
    spatialExtent.put("east",bboxList.get().get(2));
    spatialExtent.put("north",bboxList.get().get(3));
    spatialExtent.put("south",bboxList.get().get(1));
    
    // check that the provided spatial extent is available in the collection, otherwise return an error
	Collection collection = collectionMap.get(collectionId);
	
	double westlower = collection.getExtent().getSpatial().getBbox().get(0).get(0).doubleValue();
	double eastupper = collection.getExtent().getSpatial().getBbox().get(0).get(2).doubleValue();
	double southlower = collection.getExtent().getSpatial().getBbox().get(0).get(1).doubleValue();
	double northupper = collection.getExtent().getSpatial().getBbox().get(0).get(3).doubleValue();

	boolean westOutOfBounds = false;
	boolean eastOutOfBounds = false;
	boolean northOutOfBounds = false;
	boolean southOutOfBounds = false;

	if (spatialExtent.get("west") < westlower) {
		westOutOfBounds = true;
	}
	if (spatialExtent.get("east") < eastupper) {
		eastOutOfBounds = true;
	}
	if (spatialExtent.get("north") < northupper) {
		northOutOfBounds = true;
	}
	if (spatialExtent.get("south") < southlower) {
		southOutOfBounds = true;
	}

	if (westOutOfBounds && eastOutOfBounds) {
		return ApiUtil.errorResponse(HttpStatus.FORBIDDEN,"The provided spatial extent is out of bounds for the current collection!");
	}
	if (northOutOfBounds && southOutOfBounds) {
		return ApiUtil.errorResponse(HttpStatus.FORBIDDEN,"The provided spatial extent is out of bounds for the current collection!");
	}

    String[] temporalExtent = new String[2];
    String[] temporalExtentSplit = new String[2];
//    String startDate = new String();
//    String endDate = new String();
    
    // datetime parameter specs: https://docs.ogc.org/DRAFTS/20-024.html#datetime-parameter-requirements
    
    temporalExtentSplit = temporalString.get().split("/");
    if(temporalExtentSplit.length==1) {
    	temporalExtent[0] = temporalExtentSplit[0];
    	temporalExtent[1] = null;
    }
    else {
    	temporalExtent[0] = temporalExtentSplit[0];
	    if (temporalExtentSplit[1]==".."){
	    	temporalExtent[1] = null;
	    } else {
	    	temporalExtent[1] = temporalExtentSplit[1];
	    }
    }

    //1. Mapping of Collection to CIS JSON METADATA SCHEMA, coverageByDomainAndRange
    //2. Create job object for the download:
    //2.1 Translate to openEO Process Graph with load_collection + save_result
    //3. Submit job to jobsApiController
    //4. Get /jobs/job_id/result from JobsApiController and add link to output JSON RangeSet
    Map<String, Object> loadSaveProcessGraph = new LinkedHashMap<>();
    Map<String, Object> loadNode = new LinkedHashMap<>();
    Map<String, Object> loadArguments = new LinkedHashMap<>();
    Map<String, Object> saveNode = new LinkedHashMap<>();
    Map<String, Object> saveArguments = new LinkedHashMap<>();
    Map<String, Object> saveArgumentsInner = new LinkedHashMap<>();

    loadArguments.put("id",collectionId);
    loadArguments.put("spatial_extent",spatialExtent);
    loadArguments.put("temporal_extent",temporalExtent);
    if (properties!=null) {
        loadArguments.put("bands",properties);
    }
    else {
    	loadArguments.put("bands",null);
    }
    loadNode.put("arguments",loadArguments);
    loadNode.put("process_id","load_collection");

    loadSaveProcessGraph.put("load1",loadNode);

    saveArguments.put("format",fileFormat);
    saveArgumentsInner.put("from_node","load1");
    saveArguments.put("data",saveArgumentsInner);
    
    saveNode.put("arguments",saveArguments);
    saveNode.put("process_id","save_result");
    saveNode.put("result",true);

    loadSaveProcessGraph.put("save1",saveNode);
    
    Process process = new Process();
	process.setProcessGraph(loadSaveProcessGraph);
	
    Job job = new Job();
    BatchJobResult jobResult = new BatchJobResult();
    job.setProcess(process);
	job.setTitle("OGC-Coverage-"+fileFormat+"-"+OffsetDateTime.now().toString());
    ResponseEntity<?> response = jobsApiController.createJob(job, principal);
    
    job = (Job) response.getBody();
    response = jobsApiController.startJob(job.getId().toString());
    boolean jobFinishedSuccessfully = false;
    for (int i = 0; i < 150; i++) {
	    try {
	    	  Thread.sleep(2000);
	    	  ResponseEntity<?> jobDescription = jobsApiController.describeJob(job.getId().toString());
	    	  job = (Job) jobDescription.getBody();
	    	  log.debug(job.getStatus());
	    	  if (job.getStatus()==JobStates.FINISHED) {
	    		  jobFinishedSuccessfully = true;
	    		  break;
	    	  }
	    	  else if (job.getStatus()==JobStates.ERROR) {
	    		  jobFinishedSuccessfully = false;
	    		  break;
	    	  }
	    	} catch (InterruptedException e) {
	    	  Thread.currentThread().interrupt();
	    	}
    }
    if (!jobFinishedSuccessfully) {
    	return ApiUtil.errorResponse(HttpStatus.INTERNAL_SERVER_ERROR,"The request did not finished within the limit of 5 minutes or failed.");
    }
    
    ResponseEntity<?> jobListResults = jobsApiController.listResults(job.getId().toString());
	jobResult = (BatchJobResult) jobListResults.getBody();
	Map<String, Asset> resultAssets = jobResult.getAssets();
	String resultKey = new String();
    for (Map.Entry<String, Asset> entry : resultAssets.entrySet()) {
        System.out.println(entry.getKey() + ":" + entry.getValue());
        if (entry.getKey().contains("result")){
        	resultKey = entry.getKey();
        }
    }
	Asset outputAsset = resultAssets.get(resultKey);
	String outputFilePath = outputAsset.getHref();
    try {
    	log.debug("Result path "+outputFilePath);
    	File outputFile = new File(outputFilePath);
    	String mime = URLConnection.guessContentTypeFromName(outputFile.getName());
    	if (mime == null) {
    		try {
    			mime = ConvenienceHelper.getMimeFromFilename(outputFile.getName());
    		}
    		catch (Exception e1){
    			log.error(e1);
    		}
    	}
    	log.debug("Guessed mime type: "+mime);

    	URL url = new URL(outputFilePath);
    	InputStream is = null;
    	byte[] outputFileBytes = null;
    	is = url.openStream();
    	outputFileBytes = IOUtils.toByteArray(is);
    	if (is != null) {
    		is.close();
    	}
		if (mime == null) {
			return ResponseEntity.ok().contentType(MediaType.parseMediaType("application/octet-stream")).body(outputFileBytes);
		}
		return ResponseEntity.ok().contentType(MediaType.parseMediaType(mime)).body(outputFileBytes);
    	} 
    catch (IOException e) {
    		log.error("Result file not found", e);
    		return ApiUtil.errorResponse(HttpStatus.INTERNAL_SERVER_ERROR,"Result file not found.");
    	}
    
//    {
//    	  "process_graph": {
//    	    "load1": {
//    	      "process_id": "load_collection",
//    	      "arguments": {
//    	        "id": "s2_l2a",
//    	        "spatial_extent": null,
//    	        "temporal_extent": null,
//    	        "bands": null
//    	      }
//    	    },
//    	    "save2": {
//    	      "process_id": "save_result",
//    	      "arguments": {
//    	        "format": "NETCDF",
//    	        "data": {
//    	          "from_node": "load1"
//    	        }
//    	      },
//    	      "result": true
//    	    }
//    	  },
//    	  "parameters": []
//    	}
}
}
