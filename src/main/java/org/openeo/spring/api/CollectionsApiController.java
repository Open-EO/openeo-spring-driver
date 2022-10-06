package org.openeo.spring.api;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.security.Principal;
import java.time.OffsetDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.gdal.osr.CoordinateTransformation;
import org.gdal.osr.SpatialReference;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.Namespace;
import org.jdom2.input.SAXBuilder;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.openeo.spring.components.CollectionMap;
import org.openeo.spring.components.CollectionsMap;
import org.openeo.spring.model.Asset;
import org.openeo.spring.model.BandSummary;
import org.openeo.spring.model.Collection;
import org.openeo.spring.model.CollectionExtent;
import org.openeo.spring.model.CollectionSpatialExtent;
import org.openeo.spring.model.CollectionSummaries;
import org.openeo.spring.model.CollectionSummaryStats;
import org.openeo.spring.model.CollectionTemporalExtent;
import org.openeo.spring.model.Collections;
import org.openeo.spring.model.Dimension;
import org.openeo.spring.model.Dimension.TypeEnum;
import org.openeo.spring.model.DimensionBands;
import org.openeo.spring.model.DimensionOther;
import org.openeo.spring.model.DimensionSpatial;
import org.openeo.spring.model.DimensionSpatial.AxisEnum;
import org.openeo.spring.model.DimensionTemporal;
import org.openeo.spring.model.EngineTypes;
import org.openeo.spring.model.HasUnit;
import org.openeo.spring.model.Link;
import org.openeo.spring.model.Providers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.NativeWebRequest;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2020-07-02T08:45:00.334+02:00[Europe/Rome]")
@RestController
@RequestMapping("${openapi.openEO.base-path:}")
public class CollectionsApiController implements CollectionsApi {

	/** STAC spec. version used. */
	// FIXME this should be dictated by the model implemented
	public static final String STAC_VERSION = DefaultApiController.DEFAULT_STAC_VERSION;

	/**
	 * The STAC extensions used in the catalog.
	 *
	 * @see <a href="https://github.com/stac-extensions">STAC extensions GitHub repositories</a>
	 */
	public static final List<String> STAC_EXTENSIONS = Arrays.asList(
	        "https://stac-extensions.github.io/datacube/v1.0.0/schema.json", // datacube:
	        "https://stac-extensions.github.io/eo/v1.0.0/schema.json", // eo:
	        "https://stac-extensions.github.io/scientific/v1.0.0/schema.json" // sci:
	);

	/** The version assigned to a collection when not stated otherwise in the input. */
	public static final String DEFAULT_COLL_VERSION = "v1";

	/** Label of the dimension of type "bands" where to put variables. */
	// NOTE: datacube STAC extension v2 adds "variables": we should use that to store GML rangeType fields
	// https://github.com/stac-extensions/datacube/blob/main/examples/item.json
	public static final String BANDS_DIM = "bands";

	/** Characters set assumed in remote catalogs. */
	private static final Charset UTF8 = Charset.forName("UTF-8");

	/** Home directory used as root for cached catalogs. */
	private static final String USER_DIR = System.getProperty("user.dir");

	private final NativeWebRequest request;

	@Value("${org.openeo.wcps.endpoint}")
	private String wcpsEndpoint;

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

	private final Logger log = LogManager.getLogger(CollectionsApiController.class);

	@org.springframework.beans.factory.annotation.Autowired
	public CollectionsApiController(NativeWebRequest request) {
		this.request = request;
	}

	@PostConstruct
	public void init() {
		// TODO parallelize
		if (queryCollectionsOnStartup) {
			collectionsMap.put(EngineTypes.WCPS, loadWcpsCollections());
			collectionsMap.put(EngineTypes.ODC_DASK, loadOdcCollections());
		} else {
			collectionsMap.put(EngineTypes.WCPS, loadCollectionsFromFile(collectionsFileWCPS));
			collectionsMap.put(EngineTypes.ODC_DASK, loadCollectionsFromFile(collectionsFileODC));
		}

		log.info(collectionsMap.keySet());

		for (EngineTypes type : collectionsMap.keySet()) {
			for (Collection currentCollection : collectionsMap.get(type).getCollections()) {
				collectionMap.put(currentCollection.getId(), currentCollection);
			}
		}
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

	/** Reads all chars from an input stream onto a string. */
	private static String readAll(Reader rd) throws IOException {
		StringBuilder sb = new StringBuilder();
		int cp;
		while ((cp = rd.read()) != -1) {
			sb.append((char) cp);
		}
		return sb.toString();
	}

	/**
	 * Parses the content of a document to JSON.
	 *
	 * @param url     the URL of the JSON document
	 * @param charset the characters encoding of the remote document
	 * @return the global {@code JSONObject} object representing the unmarshalled document;
	 *         {@code null} if the document cannot not be fetch or is invalid.
	 */
	private JSONObject readJsonFromUrl(String url, Charset charset) {

		JSONObject json = null;

		log.debug("Trying to read JSON from '{}'", url);

		try (InputStream is = new URL(url).openStream()) {

			BufferedReader rd = new BufferedReader(
					new InputStreamReader(is, charset)
					);
			String jsonText = readAll(rd);
//			log.debug(jsonText);
			json = new JSONObject(jsonText);

		} catch (IOException ioe) {
			log.error("Error while parsing JSON from {}", url, ioe);
		} catch (JSONException je) {
			log.error("Error while parsing JSON from {}", url, je);
		}

		return json;
	}

	/**
	 * Override method with default UTF-8 character encoding.
	 *
	 * @param url     the URL of the JSON document
	 * @see #readJsonFromUrl(String, Charset)
	 */
	private JSONObject readJsonFromUrl(String url) {
		return readJsonFromUrl(url, UTF8);
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

		URL url;
		Collection currentCollection = collectionMap.get(collectionId);
		if (currentCollection != null) {
			return new ResponseEntity<Collection>(currentCollection, HttpStatus.OK);
		}
		return new ResponseEntity<Collection>(HttpStatus.NOT_FOUND);

	}

	/**
	 * Loads collections from the configured OGC-WC(P)S endpoint.
	 *
	 * @see CollectionsApiController#wcpsEndpoint
	 */
	private Collections loadWcpsCollections() {

		Collections collectionsList = new Collections();
		InputStream wcpsInputStream;
		URL urlWCPS;
		HttpURLConnection conn;

		try {
			urlWCPS = new URL(wcpsEndpoint + "?SERVICE=WCS&VERSION=2.0.1&REQUEST=GetCapabilities");
			conn = (HttpURLConnection) urlWCPS.openConnection();
			conn.setRequestMethod("GET");
			wcpsInputStream = conn.getInputStream();
		} catch (MalformedURLException mue) {
			log.error("Malformed rasdaman endpoint.", mue);
			return collectionsList;
		} catch (IOException ioe) {
			log.error("Could not fetch rasdaman collections.", ioe);
			return collectionsList;
		}

		SAXBuilder builder = new SAXBuilder();
		Document capabilititesDoc;
		try {
			capabilititesDoc = builder.build(wcpsInputStream);
		} catch (JDOMException e) {
			log.error("Invalid rasdaman WCS capabilities document.", e);
			return collectionsList;
		} catch (IOException e) {
			log.error("Error while parsing rasdaman WCS capabilities document.", e);
			return collectionsList;
		}

		Element rootNodeCollectionsList = capabilititesDoc.getRootElement();
		Namespace defaultNSCollectionsList = rootNodeCollectionsList.getNamespace();
		log.trace("root node info: " + rootNodeCollectionsList.getName());
		List<Element> coverageList = rootNodeCollectionsList.getChildren("Contents", defaultNSCollectionsList).get(0)
				.getChildren("CoverageSummary", defaultNSCollectionsList);

		/*
		 * go through each WCS coverage and convert it to STAC collection:
		 */
		// TODO factor out to helper file all sub-tasks
		COLLECTIONS:
        for (int collection = 0; collection < coverageList.size(); collection++) {

			Collection currentCollection = new Collection();
			currentCollection.setEngine(EngineTypes.WCPS);

			Element coverage = coverageList.get(collection);
			String coverageID = coverage.getChildText("CoverageId", defaultNSCollectionsList);

			log.trace("coverage info: {}:{}", coverage.getName(), coverageID);

			currentCollection.setId(coverageID);
			currentCollection.setStacVersion(STAC_VERSION);

			URL urlCollections;
			HttpURLConnection connCollections;
			InputStream currentCollectionInputStream;
			try {
				urlCollections = new URL(wcpsEndpoint
						+ "?SERVICE=WCS"
						+ "&VERSION=2.0.1"
						+ "&REQUEST=DescribeCoverage"
						+ "&COVERAGEID=" + coverageID);
				connCollections = (HttpURLConnection) urlCollections.openConnection();
				connCollections.setRequestMethod("GET");
				currentCollectionInputStream = connCollections.getInputStream();

			} catch (MalformedURLException mue) {
				log.error("Malformed rasdaman coverage URL.", mue);
				return collectionsList;
			} catch (IOException ioe) {
				log.error("Could not fetch metadata of rasdaman coverage '{}'.", coverageID, ioe);
				return collectionsList;
			}

			SAXBuilder builderInt = new SAXBuilder();
			Document capabilititesDocInt;
			try {
				capabilititesDocInt = builderInt.build(currentCollectionInputStream);
			} catch (JDOMException e) {
				log.error("Invalid rasdaman '{}' coverage document.", coverageID, e);
				return collectionsList;
			} catch (IOException e) {
				log.error("Error while parsing rasdaman '{}' coverage document.", coverageID, e);
				return collectionsList;
			}

			List<Namespace> namespaces = capabilititesDocInt.getNamespacesIntroduced();
			Element rootNode = capabilititesDocInt.getRootElement();
			Namespace defaultNS = rootNode.getNamespace();
			Namespace gmlNS = null;
			Namespace sweNS = null;
			Namespace rasdamanNS = null;
			Namespace gmlcovNS = null;
//			Namespace gmlrgridNS = null;

			for (int n = 0; n < namespaces.size(); n++) {
				Namespace current = namespaces.get(n);
				if (current.getPrefix().equals("swe")) {
					sweNS = current;
				} else if (current.getPrefix().equals("gml")) {
					gmlNS = current;
				} else if (current.getPrefix().equals("rasdaman")) {
                    rasdamanNS = current;
				} else if (current.getPrefix().equals("gmlcov")) {
					gmlcovNS = current;
//				} else if (current.getPrefix().equals("gmlrgrid")) {
//					gmlrgridNS = current;
				}
			}

			log.debug("root node info: {}", rootNode.getName());

			// bbox
			Element coverageDescElement = rootNode.getChild("CoverageDescription", defaultNS);
			Element boundedByElement = coverageDescElement.getChild("boundedBy", gmlNS);
			Element boundingBoxElement = boundedByElement.getChild("Envelope", gmlNS);

			// bbox corners
			String[] minValues = boundingBoxElement.getChildText("lowerCorner", gmlNS).split(" ");
			String[] maxValues = boundingBoxElement.getChildText("upperCorner", gmlNS).split(" ");

			// (C)CRS axes labels
			String axisLabelsAttr = boundingBoxElement.getAttribute("axisLabels").getValue();
			List<String> axisLabels = Arrays.asList(axisLabelsAttr.split(" "));

			// (C)CRS axes UoMs
			String uomLabelsAttr = boundingBoxElement.getAttribute("uomLabels").getValue();
			List<String> uomLabels = Arrays.asList(uomLabelsAttr.split(" "));

			// handy lists to keep track of axes related to spatial dimensions:
			List<String> spatialAxisLabels = new ArrayList<>();
			Map<String, CSAxisOrientation> spatialAxis2Orientation= new LinkedHashMap<>();
			List<DimensionSpatial> spatialDims = new ArrayList<>();
			boolean hasSpatialCrs = false;

			// time extent
			List<String> timeAxisLabels = new ArrayList<>();
			List<DimensionTemporal> timeDims = new ArrayList<>();
			boolean hasTimeCrs = false;

			// overall extent
			CollectionExtent collectionExtent = new CollectionExtent();

			// extra metadata
			Element metadataElement = null;
			try {
				metadataElement = rootNode
						.getChild("CoverageDescription", defaultNS)
						.getChild("metadata", gmlcovNS)
						.getChild("Extension", gmlcovNS)
						.getChild("covMetadata", rasdamanNS);
			} catch (Exception e) {
				log.warn("Error in parsing bands.", e);
				// continue COLLECTION; ? skip or ignore?
			}

			/*
			 * inner GRID
			 */
			String gridType = null; // RectifiedGrid? ReferenceableGrid? etc.
			try {
				gridType = rootNode
						.getChild("CoverageDescription", defaultNS)
						.getChild("domainSet", gmlNS)
						.getChildren().get(0)
						.getName();
				log.debug("Grid type found: " + gridType);
			} catch (Exception e) {
				log.warn("Error while fetching grid type: {}", e.getMessage());
				continue COLLECTIONS;
			}

			String[] gridDims = null;
			try {
				gridDims = rootNode
						.getChild("CoverageDescription", defaultNS)
						.getChild("domainSet", gmlNS)
						.getChild(gridType, gmlNS)
						.getChild("limits", gmlNS)
						.getChild("GridEnvelope", gmlNS)
						.getChildText("high", gmlNS)
						.split(" ");
			} catch (Exception e) {
				log.warn("Error in parsing grid dimensions: {}", e.getMessage());
				continue COLLECTIONS;
			}

			if (axisLabels.size() != gridDims.length) {
				log.warn("{}: unsupported coverage type (grid and CRS dimensions do not match).", coverageID);
				continue COLLECTIONS;
			}

			/*
			 * Domain Set: DIMENSIONS
			 */
			Map<String, Dimension> cubeDimensions = new HashMap<>();

			// CRS of the whole coverage (it might be compound space/time/other)
			String crsUri = boundingBoxElement.getAttributeValue("srsName");

			// Extract the single CRSs
			List<String> singleCrsUris = new ArrayList<>();
			if (crsUri.contains("/crs-compound")) {
				String[] splitted = crsUri.split("(&)?\\d=");
				if (splitted.length == 0) {
					log.error("Unrecognized compound CRS uri: {}", crsUri);
					continue COLLECTIONS;
				} else if (splitted.length == 1) {
					log.warn("Compound CRS contains only one CRS: {}", crsUri);
					singleCrsUris.add(crsUri);
				} else {
					singleCrsUris.addAll(Arrays.stream(splitted).skip(1).toList());
					log.debug("Extracted {} single CRSs from: {}", (splitted.length - 1), crsUri);
				}
			} else {
				singleCrsUris.add(crsUri);
				log.debug("Coverage has single CRS: {}", crsUri);
			}

			CollectionSummaryStats epsg = new CollectionSummaryStats();
			CoordinateTransformation toWgs84 = null;
			Map<Integer, JsonNode> epsgCode2Json = new LinkedHashMap<>();
			Map<String, Integer> axisLabel2EpsgCode = new LinkedHashMap<>();

			// find the spatial EPSG and set the transform to WGS84:
			for (String uri : singleCrsUris) {
				if (uri.contains("/crs/EPSG")) {
					// check there are no multiple spatial CRSs (unsupported case):
					if (hasSpatialCrs) {
						log.error("Multiple spatial CRSs found: {}", crsUri);
						continue COLLECTIONS;
					}

					int epsgCode;
					try {
						String epsgCodeStr = uri.substring(uri.lastIndexOf("/")+1);
						epsgCode = Integer.parseInt(epsgCodeStr);
						epsg.setMin(epsgCode);
						epsg.setMax(epsgCode);
						log.debug("Spatial CRS found: EPSG:" + epsgCode);

					} catch (NumberFormatException e) {
						log.error("Unrecognized EPSG code in : " + uri);
						continue COLLECTIONS;
					}

					// TODO see WGS84 static end of class
					final SpatialReference WGS84 = new SpatialReference();
					WGS84.ImportFromEPSG(EPSG_WGS84);

					SpatialReference src = new SpatialReference();
					src.ImportFromEPSG(epsgCode);
					toWgs84 = new CoordinateTransformation(src, WGS84);

					// load JSONs to reach axes abbreviations (they match WCS coverage axes labels)
					JsonNode srcJson = epsgCode2Json.get(epsgCode);

					if (null == srcJson) {
						try {
							// srcGml = src.ExportToXML(/*GML_DIALECT (ignored)*/);
							String[] strOut = { new String() };
							src.ExportToPROJJSON(strOut);

							ObjectMapper mapper = new ObjectMapper();
							srcJson = mapper.readTree(strOut[0]);
							epsgCode2Json.put(epsgCode, srcJson);

						} catch (JsonProcessingException e) {
							log.error("Irregular JSON format for EPSG:{}", epsgCode, e);
							continue COLLECTIONS;
						}
					}

					int N = src.GetAxesCount();

					for (int i = 0; i < N; ++i) {
						String label = null;
						String csType = null;

						for (CsType cst : EnumSet.of(CsType.PROJCS, CsType.GEOGCS)) {
							label = src.GetAxisName(cst.name(), i);
							csType = cst.name();
							if (null != label) {
								break;
							}
						}

						if (null == label) {
							log.error("Unknown CRS type: {}", src.GetName());
//							log.error("Unknown CRS type: EPSG:{}", epsgCode);// FIXME: install GDAL 3.x
							continue COLLECTIONS;
						}

						int aoindex = src.GetAxisOrientation(csType, i);
						CSAxisOrientation ao = CSAxisOrientation.of(aoindex);

						// get axis abbreviation
						String abbrev = CollectionsApiController.getAxisAbbrev(srcJson, i);
						if (null == abbrev) {
							log.error("Unsupported JSON structure for EPSG:{}:\n{}", epsgCode, srcJson);
							continue COLLECTIONS;
						}

						spatialAxisLabels.add(abbrev);
						spatialAxis2Orientation.put(abbrev, ao);
						axisLabel2EpsgCode.put(abbrev, epsgCode);

						log.debug("spatial axis found: {} (\"{}\", {})", label, abbrev, ao);

						// guard
						if (!axisLabels.contains(abbrev)) {
							log.error("Spatial axis '{}' not found in coverage definition.", label);
							// skip or lenient?
						}
					}
					hasSpatialCrs = true;
				}
			}

			// go through each dimension, and identify their type

			for (String label : axisLabels) {
				log.trace("{}:{} axis", coverageID, label);
				int index = axisLabels.indexOf(label);

				String uom = uomLabels.get(index);

				/*
				 * spatial axis
				 */
				if (spatialAxisLabels.contains(label)) {
					// guard
					if (!hasSpatialCrs) {
						log.error("Internal coverage parsing error: '{}' deemed as spatial.", label);
						continue COLLECTIONS;
					}

					DimensionSpatial dim = new DimensionSpatial();
					dim.setType(TypeEnum.SPATIAL); // FIXME this should be implicit

//					int gridDim = Integer.parseInt(gridDims[index]) + 1;
					int epsgCode = axisLabel2EpsgCode.get(label);
					dim.setReferenceSystem(epsgCode);

					// axis type: easting/northing/z ?
					CSAxisOrientation csOrientation = spatialAxis2Orientation.get(label);
					AxisEnum orientation = CollectionsApiController.getAxisType(csOrientation);
					if (null == orientation) {
						log.error("No orientation inferred from axis '{}'.", label);
						continue COLLECTIONS;
					}
					log.trace(" axis '{}' -> {}", label, orientation);
					dim.setAxis(orientation);

					// set native extent
					List<BigDecimal> extent = Arrays.asList(
							BigDecimal.valueOf(Double.parseDouble(minValues[index])),
							BigDecimal.valueOf(Double.parseDouble(maxValues[index])));
					dim.setExtent(extent);

					// unit
					dim.setUnit(uom);

					cubeDimensions.put(label, dim);
					spatialDims.add(dim);
				}

				/*
				 * temporal axis
				 */
				// TODO import XML definition from SECORE and check:
				//   i) UoM is temporal?
				//   ii) type is TemporalCRS
				else if (TEMPORAL_AXIS_LABELS.contains(label)) {
					/*
					 * NOTE: temporal axis can either be a quoted timestamp (eg.
					 * "2022-12-03T09:00Z") or a time index whose meaning is encoded in the
					 * correspondent CRS, and which we recognize because of its label. (!)
					 */
					String minT = minValues[index].replaceAll("\"", "");
					String maxT = maxValues[index].replaceAll("\"", "");

					try {
						OffsetDateTime.parse(minT);
						hasTimeCrs = true;
						timeAxisLabels.add(label);
					} catch (DateTimeParseException e) {
						log.trace("Time axis is not expressed as timestamp: {}", minValues[index]); // nevermind
					}

					if (hasTimeCrs) { // "2022-12-03T09:00Z"
						DimensionTemporal dim = new DimensionTemporal();
						dim.setType(TypeEnum.TEMPORAL);
						List<String> extent = Arrays.asList(minT, maxT);
						dim.setExtent(extent);
						cubeDimensions.put(label, dim);
						timeDims.add(dim);
					} else { // time-encoded numeric coordinate
						DimensionOther dim = new DimensionOther();
						dim.setType(TypeEnum.TEMPORAL);
						List<BigDecimal> extent = Arrays.asList(
								BigDecimal.valueOf(Double.parseDouble(minT)),
								BigDecimal.valueOf(Double.parseDouble(maxT)));
						dim.setExtent(extent);
						cubeDimensions.put(label, dim);
					}

					/*
					 * arbitrary non-spatial/non-temporal axis
					 */
				} else {
					// check STAC API on this: types of axes
					DimensionOther dim = new DimensionOther();
					dim.setType(TypeEnum.OTHER);

					//
					try {
						// extent shall be numeric
						// but in alternative it can have values
						// https://github.com/stac-extensions/datacube#additional-dimension-object
						List<BigDecimal> extent = Arrays.asList(
								BigDecimal.valueOf(Double.parseDouble(minValues[index])),
								BigDecimal.valueOf(Double.parseDouble(maxValues[index])));
						dim.setExtent(extent);

						// unit
						dim.setUnit(uom);

						cubeDimensions.put(label, dim);

					} catch (NumberFormatException e) {
						log.error("Unsupported extent for dimension '{}'.", label, e);
						continue COLLECTIONS;
					}
				}
			}

			/*
			 * Range Set: BANDS
			 */
			List<Element> bandsMetadataList = null;
			List<Element> bandsListSwe = null;

			// hack to save some SWE metadata in eo:bands summary later on
			Map<String, String> band2SweDescr = new LinkedHashMap<>();

	        DimensionBands dimensionBands = new DimensionBands();
	        dimensionBands.setType(TypeEnum.BANDS);

	        try {
	            bandsListSwe = rootNode
	                    .getChild("CoverageDescription", defaultNS)
	                    .getChild("rangeType", gmlcovNS)
	                    .getChild("DataRecord", sweNS)
	                    .getChildren("field", sweNS);
	        } catch (Exception e) {
	            log.error("Error in parsing bands definition.", e);
	            continue COLLECTIONS;
	        }

            for (Element band : bandsListSwe) {
                String bandId = band.getAttributeValue("name");
                dimensionBands.addValuesItem(bandId);
                // TODO where does the Dimension "Bands" come from?
                // will try to put bands metadata in the eo:bands summaries.

                Element quantity = band.getChild("Quantity", sweNS);
                if (null == quantity) {
                    log.warn("No SWE quantity element in range variable {}.", bandId);

                } else {
                    String sweLabel = quantity.getChildText("label", sweNS);
                    // ...

                    String sweDescr = quantity.getChildText("description", sweNS);
                    if (null != sweDescr) {
                        band2SweDescr.put(bandId, sweDescr);
                    }
                }

                // uom code ...
                // nilValues ...
                // constraint ...
            }
			cubeDimensions.put(BANDS_DIM, dimensionBands);

			// ok cube:dimensions
			currentCollection.setCubeColonDimensions(cubeDimensions);

			/*
			 * Overall spatio-temporal extent
			 *
			 * (spatial bbox shall be WGS84:)
			 *  https://github.com/radiantearth/stac-spec/blob/master/collection-spec/collection-spec.md#spatial-extent-object
			 */
			if (hasSpatialCrs) {

				final int ndims = spatialAxisLabels.size(); // either 2D or 3D

				List<Integer> indexes = spatialAxisLabels.stream()
						.map(x -> axisLabels.indexOf(x))
						.toList();

				double[] lowerCorner = indexes.stream()
						.map(i -> Double.parseDouble(minValues[i]))
						.mapToDouble(Double::doubleValue)
						.toArray();
				double[] upperCorner = indexes.stream()
						.map(i -> Double.parseDouble(maxValues[i]))
						.mapToDouble(Double::doubleValue)
						.toArray();

				// TransformPoint requires 3D coords:
				// https://gdal.org/java/org/gdal/osr/CoordinateTransformation.html
				double[] llWgs84 = Arrays.copyOf(lowerCorner, 3);
				double[] urWgs84 = Arrays.copyOf(upperCorner, 3);

				toWgs84.TransformPoint(llWgs84);
				toWgs84.TransformPoint(urWgs84);

				log.trace("{}D WGS84 extent: {} -> {}", ndims,
						Arrays.toString(llWgs84),
						Arrays.toString(urWgs84));

				CollectionSpatialExtent spatialExtent = new CollectionSpatialExtent();
				List<List<BigDecimal>> bbox = new ArrayList<>();

				for (int i=0; i<ndims; ++i) {
					List<BigDecimal> coord = Arrays.asList(
							BigDecimal.valueOf(llWgs84[i]),
							BigDecimal.valueOf(urWgs84[i]));
					bbox.add(coord);
				}

				spatialExtent.setBbox(bbox);
				collectionExtent.setSpatial(spatialExtent);
			}

			if (hasTimeCrs) {
				CollectionTemporalExtent temporalExtent = new CollectionTemporalExtent();
				List<List<OffsetDateTime>> intervals = new ArrayList<>();

				// 1+ time dimensions:
				for (DimensionTemporal dim : timeDims) {
					String minT = dim.getExtent().get(0);
					String maxT = dim.getExtent().get(1);

					List<OffsetDateTime> interval = new ArrayList<>();
					try {
						//STAC requires format: https://www.rfc-editor.org/rfc/rfc3339#section-5.6
						//We use: java.time.format.DateTimeFormatter.ISO_OFFSET_DATE_TIME.
						interval.add(OffsetDateTime.parse(minT));
						interval.add(OffsetDateTime.parse(maxT));

					} catch (DateTimeParseException e) {
						log.warn("Error parsing time extent: {}:{}", minT, maxT);
						interval.add(null); // FIXME should we be lenient?
						interval.add(null); //
					}

					log.debug("Time interval : " + interval);

					intervals.add(interval);
				}
				temporalExtent.setInterval(intervals);
				collectionExtent.setTemporal(temporalExtent);
			}

			// set the computed spatio-temporal extent:
			currentCollection.setExtent(collectionExtent);

			// FIXME should not be hardcoded here
			currentCollection.setVersion(DEFAULT_COLL_VERSION);

			/*
			 * license
			 */
			List<Link> links = new ArrayList<>();

			// licence link
			String licenseLink = metadataElement.getChildText("License_Link", gmlNS);
			if (null != licenseLink) {
				Link link = new Link();
				try {
					link.setHref(new URI(licenseLink));
					link.setRel(LinkRelType.LICENCE.toString());
					link.setTitle("License Link");

					String linkType = metadataElement.getChildText("License_Link_Type", gmlNS);
					if (null != linkType) {
						link.setType(linkType);
					}
					links.add(link);
				} catch (URISyntaxException e) {
					log.error("Error invalid licence of {}", coverageID, e);
					continue COLLECTIONS;
				}
			}

			// about link
			String aboutLink = metadataElement.getChildText("About_Link");
            if (null != aboutLink) {
                Link link = new Link();
                try {
                    link.setHref(new URI(aboutLink));
                    link.setRel(LinkRelType.ABOUT.toString());
                    link.setTitle("About Link");

                    String linkType = metadataElement.getChildText("About_Link_Type", gmlNS);
                    if (null != linkType) {
                        link.setType(linkType);
                    }

                    links.add(link);
                } catch (URISyntaxException e) {
                    log.error("Error invalid licence of {}", coverageID, e);
                    continue COLLECTIONS;
                }
            }

			currentCollection.setLinks(links);

			/*
			 * licence
			 */
			String license = metadataElement.getChildText("License", gmlNS);
			if (license == null) {
				license = "No License Information Available";
			}
			currentCollection.setLicense(license);

			/*
			 * Other metadata
			 */
			String title = metadataElement.getChildText("Title", gmlNS);
			if (title == null) {
				title = "No Title Available";
			}
			currentCollection.setTitle(title);

			String citation = metadataElement.getChildText("Citation", gmlNS);
			currentCollection.setCitation(citation);

			String description = metadataElement.getChildText("Description", gmlNS);
			if (description == null) {
				description = "No Description Available";
			}
			currentCollection.setDescription(description);

			String tempStep = metadataElement.getChildText("Temporal_Step", gmlNS);
			if (null != tempStep) {
				if (!hasTimeCrs) {
					log.warn("Temporal step provided but time axis not found in coverage {}.", coverageID);

					// fetch other dimension with temporal type:
					List<DimensionOther> otherTimeDims = cubeDimensions.values().stream()
							.filter(dim -> TypeEnum.TEMPORAL.equals(dim.getType()))
							.filter(dim -> dim instanceof DimensionOther)
							.map(dim -> (DimensionOther) dim)
							.toList();

					for (DimensionOther dim : otherTimeDims) {
						dim.setStep(tempStep);
					}
				} else {
					DimensionTemporal dim = timeDims.get(0);
					dim.setStep(tempStep);

					if (timeDims.size() != 1) {
						log.warn("Multiple time dimension. Assume temporal step is for '{}'", dim);
					}
				}
			}

			/*
			 * keywords
			 */
			List<String> keywords = new ArrayList<>();

			String keywordsText = metadataElement.getChildText("Keywords", gmlNS);
			if (null != keywordsText) {
				keywords.addAll(Arrays.asList(keywordsText.split(", ")));
				log.debug("Keywords : {}", keywords);
			} else {
				keywords.add("No keywords Available");
			}

			currentCollection.setKeywords(keywords);

			/*
			 * STAC extensions
			 */
			Set<String> stacExtensions = new HashSet<>();
			stacExtensions.addAll(STAC_EXTENSIONS);
			currentCollection.setStacExtensions(stacExtensions);

			/*
			 * Data providers
			 */
			List<Providers> providers = new ArrayList<>();

			// force THIS provider
			Providers thisProvider = new Providers();
			try {
				thisProvider.setName(wcpsProviderName);
				thisProvider.setRoles(Arrays.asList(wcpsProviderType));
				thisProvider.setUrl(new URI(wcpsProviderUrl));
			} catch (URISyntaxException e) {
				log.error("Invalid local provider configuration. ", e);
				continue COLLECTIONS;
			}
			providers.add(thisProvider);

			// import source providers
			int prvi = 1;
			boolean done = false;
			while (!done) {
				List<String> roles = new ArrayList<>();
				Providers provider = new Providers();

				String name = metadataElement.getChildText("Provider%d_Name".formatted(prvi), gmlNS);
				String role = metadataElement.getChildText("Provider%d_Roles".formatted(prvi), gmlNS);
				String link = metadataElement.getChildText("Provider%d_Link".formatted(prvi), gmlNS);

				if (null == name) {
					done = true; // no more providers
				} else {
					try {
						provider.setName(name);
						roles.add(role);
						provider.setRoles(roles);
						provider.setUrl(new URI(link));
						providers.add(provider);
					} catch (URISyntaxException e) {
						log.error("Invalid provider link. ", e);
						continue COLLECTIONS;
					}
				}
				++prvi;
			}

			currentCollection.setProviders(providers);

			/*
			 * summaries
			 */
			CollectionSummaries summaries = new CollectionSummaries();

			Set<String> platforms = new LinkedHashSet<>();
			String platform = metadataElement.getChildText("Platform", gmlNS);
			if (null != platform) {
				platforms.add(platform);
			}

			CollectionSummaryStats cloudCover = new CollectionSummaryStats();
			JSONArray cloudCovArray = new JSONArray();

			/*
			 * axes metadata
			 */
			Element axesMetadata = metadataElement.getChild("axes", gmlNS);
			if (null != axesMetadata) {
				for (Element axis : axesMetadata.getChildren()) {
					String axisLabel = axis.getName();
					Dimension dim = cubeDimensions.get(axisLabel);

					if (null == dim) {
						log.warn("Axis '{}' not found in domain set.", axisLabel);
						continue; // skip axis metadata
					}

					// description -> description
					String descr = axis.getChildText("description");
					if (null != descr) {
						dim.setDescription(descr);
					}

					// standard_name / long_name ?
					// ...

					// units -> unit
					String units = axis.getChildText("units");
					if (null != units && HasUnit.is(dim)) {
						HasUnit unitDim = (HasUnit) dim;
						unitDim.setUnit(units);
						log.debug("Overwrite {} dimension unit of measure with '{}'.", axisLabel, units);
					}

					// other arbitrary fields ... ?
					// ...
				}
			}

			/*
			 * slices detail
			 */
			Element slicesEl = metadataElement.getChild("slices", gmlNS);
			if (null != slicesEl) {
				for (Element slice : slicesEl.getChildren()) {
					// spacecraft
					String spacecraft = slice.getChildText("DATATAKE_1_SPACECRAFT_NAME");
					if (null != spacecraft) {
						platforms.add(spacecraft);
					}
					// cloud-coverage
					String cloudCovStr = slice.getChildText("CLOUD_COVERAGE_ASSESSMENT");
					if (null != cloudCovStr) {
						try {
							double cloudCov = Double.parseDouble(cloudCovStr);
							cloudCovArray.put(cloudCov);
						} catch (NumberFormatException e) {
							log.error("Invalid cloud-coverage value found: '{}'", cloudCovStr, e);
						}
					}
					// boundedBy
					// ... -> single slices' extent could be added to the list of cube's extents
				}
			}

			List<String> constellations = new ArrayList<>();
			String constellation = metadataElement.getChildText("Constellation", gmlNS);
			if (null != constellation) {
				constellations.add(constellation);
			}

			List<String> instruments = new ArrayList<>();
			String instrument = metadataElement.getChildText("Instruments", gmlNS);
			if (null != instrument) {
				instruments.add(instrument);
			}

//		try {
//			rows = Integer.parseInt(metadataElement.getChildText("Rows", gmlNS));
//		}catch(Exception e) {
//			log.warn("Error in parsing Rows:" + e.getMessage());
//		}
//
//		try {
//			columns = Integer.parseInt(metadataElement.getChildText("Columns", gmlNS));
//		}catch(Exception e) {
//			log.warn("Error in parsing Columns:" + e.getMessage());
//		}

			/*
			 * cloud coverage
			 */
			double maxCCValue = 0;
			double minCCValue = 0;
			boolean cloudCoverFlag = false;

			// FIXME optimize cloud-cover min/max extraction:

			if (!cloudCovArray.isEmpty()) {
				try {
					maxCCValue = cloudCovArray.getDouble(0);
					minCCValue = cloudCovArray.getDouble(0);
					cloudCoverFlag = true;

				} catch (JSONException e) {
					log.warn("Error in parsing cloud cover Extents :" + e.getMessage());
				}
			}

			if (cloudCoverFlag) {
				for (int i = 1; i < cloudCovArray.length(); i++) {
					double ccov = cloudCovArray.getDouble(i);
					if (ccov > maxCCValue) {
						maxCCValue = ccov;
					}
					if (ccov < minCCValue) {
						minCCValue = ccov;
					}
				}
			}

			cloudCover.setMin(minCCValue);
			cloudCover.setMax(maxCCValue);

			/*
			 * bands
			 */
			List<BandSummary> bandsSummaries = new ArrayList<>();
			Set<Double> gsd = new LinkedHashSet<>();
			boolean hasBandsMetadata = false;

            Element bandsMetadata = metadataElement.getChild("bands", gmlNS);
            if (null != bandsMetadata) {
                bandsMetadataList = bandsMetadata.getChildren();
                hasBandsMetadata = true;
            }

			if (hasBandsMetadata) {
				try {
					// TODO here parsing is lenient: is that ok?
				    // TODO where to put metadata that are not in the eo:bands schema?
				    // https://github.com/stac-extensions/eo
					for (Element band : bandsMetadataList) {

						BandSummary bandsSummary = new BandSummary();
						String bandWave = "0";
						String bandCommonName = "No Band Common Name found";
						String bandGSD = "0";
						String bandId = "No Band Name found";

						bandId = band.getName();
						bandsSummary.setName(bandId);

						// (hack)
						// fetch SWE:description + metadata:long_name (arbitrary..) to describe the band
						String sweDescr = band2SweDescr.get(bandId);
						String longName = band.getChildText("long_name");
						if (null != longName && longName.equals(sweDescr)) {
						    longName = null;
						}
						if (null != sweDescr || null != longName) {
		                    String bandDescr = String.format("%s%s%s",
		                            (null == longName) ? "" : String.format("[%s]", longName),
                                    (null != sweDescr && null != longName) ? " - " : "",
		                            (null == sweDescr) ? "" : sweDescr );
						    bandsSummary.setDescription(bandDescr);
						}

						if (!dimensionBands.containsValue(bandId)) {
						    log.warn("{} band has metadata found, but is not listed in coverage's range.", bandId);
						    continue; // skip metadata
						}

						bandGSD = band.getChildText("gsd");
						if (null != bandGSD) {
							try {
								gsd.add(Double.parseDouble(bandGSD));
								bandsSummary.setGsd(Double.parseDouble(bandGSD));
							} catch (NumberFormatException e) {
								log.warn("Error in parsing band gsd:" + e.getMessage());
							}
						}

						bandCommonName = band.getChildText("common_name");
						if (null != bandCommonName) {
							bandsSummary.setCommonname(bandCommonName);
						}

						bandWave = band.getChildText("wavelength");
						if (null != bandWave) {
							try {
								double w = Double.parseDouble(bandWave);
								bandsSummary.setCenterwavelength(w);
							} catch (NumberFormatException e) {
								log.warn("Error in parsing band wave-lenght:" + e.getMessage());
							}
						}

						bandsSummaries.add(bandsSummary);
					}
				} catch (Exception e) {
					log.warn("Error in parsing bands :" + e.getMessage());
				}
			} else {
			    // simple summaries: just the band label
			    // (but there is more data in SWE quantities that is ignored)
				for (Element band : bandsListSwe) {
					BandSummary bandsSummary = new BandSummary();
					String bandId = band.getAttributeValue("name");
					bandsSummary.setName(bandId);
					bandsSummaries.add(bandsSummary);
				}
			}

			summaries.setPlatform(platforms.stream().toList());
			summaries.setConstellation(constellations);
			summaries.setInstruments(instruments);
			summaries.setCloudCover(cloudCover);
			summaries.setGsd(gsd.stream().toList());
//			summaries.setRows(rows);
//			summaries.setColumns(columns);
//		    summaries.setEpsg(epsg);
			summaries.setBands(bandsSummaries);
			currentCollection.setSummaries(summaries);

			Map<String, Asset> assets = new HashMap<String, Asset>();
			currentCollection.setAssets(assets);

			collectionsList.addCollectionsItem(currentCollection);
		}
		// ~~ END OF COLLECTIONS LOOP

		// cache catalog to JSON file:
		new Thread(() -> {
			ObjectMapper mapper = new ObjectMapper();
			mapper.enable(SerializationFeature.INDENT_OUTPUT);
			mapper.registerModule(new JavaTimeModule());
			mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

			try {
				log.info(collectionsFileWCPS.getFilename());
				File collectionsFile = new File(USER_DIR + "/" + collectionsFileWCPS.getFilename());
				if (!collectionsFile.exists()) {
					collectionsFile.createNewFile();
				}
				mapper.writeValue(collectionsFile, collectionsList);

			} catch (JsonGenerationException | JsonMappingException jse) {
				log.error("JSON error while serializing the WCPS catalog.", jse);

			} catch (IOException ioe) {
				log.error("I/O error while serializing the WCPS catalog to file.", ioe);
			}
		}).start();

		return collectionsList;
	}

	/**
	 * Loads collections from the OpenDataCube endpoint.
	 *
	 * @see CollectionsApiController#odcCollEndpoint
	 */
	private Collections loadOdcCollections() {

		Collections collectionsList = new Collections();
		JSONObject odcSTACMetdata =  readJsonFromUrl(odcCollEndpoint);

		if (odcSTACMetdata != null) {

			// JSONObject odcCollections = odcSTACMetdata.getJSONObject("collections");
			final String odcSTACMetdataStr = odcSTACMetdata.toString(4);

			ObjectMapper mapper = new ObjectMapper();
			mapper.registerModule(new JavaTimeModule());
			mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

			// parallelize writing of catalog to disk
			new Thread(() -> {
				File collectionsFile = new File(USER_DIR + "/" + collectionsFileODC.getFilename());

				if (!collectionsFile.exists()) {
					try {
						collectionsFile.createNewFile();
						try (FileWriter file = new FileWriter(collectionsFile)) {
							file.write(odcSTACMetdataStr);
						}
					} catch (IOException e) {
						log.error("Error while serializing ODC catalog to disk.", e);
					}
				}
			}).start();

			try {
				collectionsList = mapper.readValue(odcSTACMetdataStr, Collections.class);
				//collectionsList = mapper.readValue(collectionsFile, Collections.class);
			} catch (JsonProcessingException e) {
				log.error("Error while unmarshalling remote catalog '{}'", odcCollEndpoint, e);
			}
		}
		return collectionsList;
	}

	/** Loads openEO collections from a cached JSON catalog. */
	private Collections loadCollectionsFromFile(Resource collectionResource) {
		Collections collectionsList = null;

		ObjectMapper mapper = new ObjectMapper();
		mapper.registerModule(new JavaTimeModule());
		mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

		String filePath = USER_DIR + "/" + collectionResource.getFilename();

		try {
			File collectionsFile = new File(filePath);
			collectionsList = mapper.readValue(collectionsFile, Collections.class);
		} catch (IOException e) {
			log.error("Error while unmarshalling '{}'.", filePath, e);
		}
		return collectionsList;
	}

	// consts
	private static final int EPSG_WGS84 = 4326;
	private static enum CsType {
		PROJCS, GEOGCS;
	}
	// FIXME GDAL library UnsatisfiedLinkError:
//	private /*static*/ /*final*/ SpatialReference WGS84 = new SpatialReference();
//	/*static*/ {
//		WGS84.ImportFromEPSG(EPSG_WGS84);
//	}

	// heuristic to identify axis type
	private static final List<String> TEMPORAL_AXIS_LABELS = Arrays.asList(
			"DATE", "Date", "date",
			"TIME", "Time", "time",
			"ANSI", "Ansi", "ansi",
			"UNIX", "Unix", "unix",
			"YEAR", "Year", "year",
			"t");

	/*
	 * EPSG database: what are the axis labels in there?
	 */

//	epsg=# \d epsg_coordinateaxis
//	            Table "public.epsg_coordinateaxis"
//	Column          |         Type          | Collation | Nullable | Default
//	-------------------------+-----------------------+-----------+----------+---------
//	coord_sys_code          | integer               |           | not null |
//	coord_axis_name_code    | integer               |           | not null |
//	coord_axis_orientation  | character varying(24) |           | not null |
//	coord_axis_abbreviation | character varying(24) |           | not null |
//	uom_code                | integer               |           | not null |
//	coord_axis_order        | smallint              |           | not null |
//	Indexes:
//	    "pk_coordinateaxis" PRIMARY KEY, btree (coord_sys_code, coord_axis_name_code)
//
//  #
//	# Which axis orientation types are there?
//  #
//	epsg=# SELECT DISTINCT coord_axis_orientation AS o FROM epsg_coordinateaxis ORDER BY o;
//	 Geocentre > equator/90dE      #
//	 Geocentre > equator/PM        # Geocentric CRSs
//	 Geocentre > north pole        #
//	 North along 0 deg East
//	 North along 130 deg West
//	 North along 140 deg East
//	 North along 160 deg East
//	 North along 70 deg East
//	 North along 90 deg East
//	 South along 180 deg East
//	 South along 90 deg East
//	 down
//	 east
//	 east south east
//	 east-south-east
//	 north
//	 north north east
//	 north-east
//	 north-north-east
//	 north-west
//	 south
//	 up
//	 west
//
//  #
//	# Which axes have either north or south orientation?
//  #
//    epsg=# SELECT DISTINCT coord_axis_abbreviation AS abbrv
//	  FROM epsg_coordinateaxis
//	  WHERE (coord_axis_orientation ILIKE 'north%'
//	     OR  coord_axis_orientation ILIKE 'south%')
//	    AND  coord_axis_orientation NOT ILIKE 'Geocentre%'
//	  ORDER BY abbrv;
//	 E   # <----- !
//	 J
//	 Lat
//	 N
// 	 N(Y)
//	 P
//	 X  # <----- !
//	 Y
//	 e  # <----- !
//	 n
//	 x  # <----- !
//
	private static final List<String> Y_AXIS_LABELS = Arrays.asList(
			"J", "Lat", "n", "N", "N(Y)", "P", "Y");

//  #
//  # Which axes have either east or west orientation?
//  #
//	epsg=# SELECT DISTINCT coord_axis_abbreviation AS abbrv
//	  FROM epsg_coordinateaxis
//	  WHERE (coord_axis_orientation ILIKE 'west%'
//	     OR  coord_axis_orientation ILIKE 'east%')
//	    AND  coord_axis_orientation NOT ILIKE 'Geocentre%'
//	  ORDER BY abbrv;
//	 E
//	 E(X)
//	 I
//	 Long
//	 M
//	 W
//	 X
//	 Y  # <----- !
//	 y  # <----- !
//
	private static final List<String> X_AXIS_LABELS = Arrays.asList(
			"I", "Long", "E", "E(X)", "M", "W", "X");

//  #
//  # Which axes have either vertical (z) orientation?
//  #
//	epsg=# SELECT DISTINCT coord_axis_abbreviation AS abbrv
//	  FROM epsg_coordinateaxis
//	  WHERE coord_axis_orientation = 'up'
//	     OR coord_axis_orientation = 'down'
//       OR coord_axis_orientation = 'Geocentre > north pole'
//	  ORDER BY abbrv;
//	 D
//	 H
//	 R
//   Z
//	 h
//
	private static final List<String> Z_AXIS_LABELS = Arrays.asList("D", "H", "R", "Z", "h");

	/**
	 * Infers the type of a spatial axis from its label (abbreviation).
	 *
	 * @param axisLabel the abbreviation of an axis
	 * @return the inferred axis orientation/type; {@code null} if no suitable
	 *         orientation is found.
	 * @deprecated use the more precise {@link #getAxisType(CSAxisOrientation)}.
	 */
	private static AxisEnum getAxisTypeHeu(String axisLabel) {
		AxisEnum type = null;

		if (X_AXIS_LABELS.contains(axisLabel)) {
			type = AxisEnum.X;

		} else if (Y_AXIS_LABELS.contains(axisLabel)) {
			type = AxisEnum.Y;

		} else if (Z_AXIS_LABELS.contains(axisLabel)) {
			type = AxisEnum.Z;
		}

		return type;
	}

	/**
	 * Returns the type of axis given its original orientation.
	 *
	 * @param axisOrientation
	 * @return the inferred axis type, {@code null} if no match found
	 * ({@link CSAxisOrientation#OAO_Other}).
	 */
	private static AxisEnum getAxisType(CSAxisOrientation axisOrientation) {
		AxisEnum type = null;

		if (CS_H_AXES.contains(axisOrientation)) {
			type = AxisEnum.X;

		} else if (CS_V_AXES.contains(axisOrientation)) {
			type = AxisEnum.Y;

		} else if (CS_Z_AXES.contains(axisOrientation)) {
			type = AxisEnum.Z;
		}

		return type;
	}

	/**
	 * Fetches the abbreviation of the {@code i}-th axis of a given
	 * coordinate reference system.
	 *
	 * @param crs  Parsed JSON of a CRS definition
	 * @param i    0-based index of the axis in the CRS
	 * @return the "abbreviation" attribute of the {@code i}-th axis
	 *         in the {@code crs} (base CRS is ignored here);
	 *         {@code null} if the element is not found.
	 */
	private static final String getAxisAbbrev(JsonNode crs, int i) {
		String abbrev = null;
		try {
			JsonNode csNode = crs.get(JSON_CS);
			JsonNode axNodes = csNode.get(JSON_CS_AXIS);
			JsonNode abbrNode = axNodes.get(i).get(JSON_CS_AXIS_ABBREVIATION);
			abbrev = abbrNode.toString().replace("\"", "");

		} catch (NullPointerException e) {
			// unsupported JSON structure
		}

		return abbrev;
	}

	/** Key of the coordinate system element in a CRS JSON. */
	private static final String JSON_CS = "coordinate_system";

	/** Key of the coordinate system axis element in a CRS JSON. */
	private static final String JSON_CS_AXIS = "axis";

	/** Key of the abbreviation of a coordinate system axis in a CRS JSON. */
	private static final String JSON_CS_AXIS_ABBREVIATION = "abbreviation";

	/**
	 * GDAL axis orientations (corresponds to CS_AxisOrientationEnum).
	 *
	 * Taken from {@code OGRAxisOrientation} enum in {@code ogr_srs_api.h}.
	 *
	 * @see <a href="https://gdal.org/java/org/gdal/osr/SpatialReference.html#GetAxisOrientation(java.lang.String,int)">Class SpatialReference</a>
	 */
	private static enum CSAxisOrientation {
		OAO_Other, /**< Other */
		OAO_North, /**< North */
		OAO_South, /**< South */
		OAO_East,  /**< East */
		OAO_West,  /**< West */
		OAO_Up,    /**< Up (to space) */
		OAO_Down;  /**< Down (to Earth center) */

		static CSAxisOrientation of(int index) {
			return values()[index];
		}
	}

	/** Types of GDAL axes orientations to be considered as horizontal/X spatial axes. */
	private static final EnumSet<CSAxisOrientation> CS_H_AXES = EnumSet.of(
			CSAxisOrientation.OAO_East,
			CSAxisOrientation.OAO_West);

	/** Types of GDAL axes orientations to be considered as horizontal/Y spatial axes. */
	private static final EnumSet<CSAxisOrientation> CS_V_AXES = EnumSet.of(
			CSAxisOrientation.OAO_North,
			CSAxisOrientation.OAO_South);

	/** Types of GDAL axes orientations to be considered as vertical/Z spatial axes. */
	private static final EnumSet<CSAxisOrientation> CS_Z_AXES = EnumSet.of(
			CSAxisOrientation.OAO_Up,
			CSAxisOrientation.OAO_Down);
}
