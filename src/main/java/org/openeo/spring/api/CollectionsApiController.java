package org.openeo.spring.api;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.Principal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;
import org.openapitools.jackson.nullable.JsonNullable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.gdal.osr.CoordinateTransformation;
import org.gdal.osr.SpatialReference;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.Namespace;
import org.jdom2.input.SAXBuilder;
import org.json.JSONObject;
import org.openeo.spring.model.Collection;
import org.openeo.spring.model.CollectionExtent;
import org.openeo.spring.model.CollectionSpatialExtent;
import org.openeo.spring.model.CollectionSummaryStats;
import org.openeo.spring.model.CollectionTemporalExtent;
import org.openeo.spring.model.Collections;
import org.openeo.spring.model.Dimension;
import org.openeo.spring.model.Dimension.TypeEnum;
import org.openeo.spring.model.DimensionBands;
import org.openeo.spring.model.DimensionSpatial;
import org.openeo.spring.model.DimensionSpatial.AxisEnum;
import org.openeo.spring.model.DimensionTemporal;
import org.openeo.spring.model.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
    
    private final Logger log = LogManager.getLogger(CollectionsApiController.class);

    @org.springframework.beans.factory.annotation.Autowired
    public CollectionsApiController(NativeWebRequest request) {
        this.request = request;
    }

    @Override
    public Optional<NativeWebRequest> getRequest() {
        return Optional.ofNullable(request);
    }
    
    /**
     * GET /collections : Basic metadata for all datasets
     * Lists available collections with at least the required information.  It is **strongly RECOMMENDED** to keep the response size small by omitting larger optional values from the objects in &#x60;collections&#x60; (e.g. the &#x60;summaries&#x60; and &#x60;cube:dimensions&#x60; properties). To get the full metadata for a collection clients MUST request &#x60;GET /collections/{collection_id}&#x60;.  This endpoint is compatible with [STAC 0.9.0](https://stacspec.org) and [OGC API - Features](http://docs.opengeospatial.org/is/17-069r3/17-069r3.html). [STAC API](https://github.com/radiantearth/stac-spec/tree/v0.9.0/api-spec) features / extensions and [STAC extensions](https://github.com/radiantearth/stac-spec/tree/v0.9.0/extensions) can be implemented in addition to what is documented here.
     *
     * @param limit This parameter enables pagination for the endpoint and specifies the maximum number of elements that arrays in the top-level object (e.g. jobs or log entries) are allowed to contain. The only exception is the &#x60;links&#x60; array, which MUST NOT be paginated as otherwise the pagination links may be missing ins responses. If the parameter is not provided or empty, all elements are returned.  Pagination is OPTIONAL and back-ends and clients may not support it. Therefore it MUST be implemented in a way that clients not supporting pagination get all resources regardless. Back-ends not supporting  pagination will return all resources.  If the response is paginated, the links array MUST be used to propagate the  links for pagination with pre-defined &#x60;rel&#x60; types. See the links array schema for supported &#x60;rel&#x60; types.  *Note:* Implementations can use all kind of pagination techniques, depending on what is supported best by their infrastructure. So it doesn&#39;t care whether it is page-based, offset-based or uses tokens for pagination. The clients will use whatever is specified in the links with the corresponding &#x60;rel&#x60; types. (optional)
     * @return Lists of collections and related links. (status code 200)
     *         or The request can&#39;t be fulfilled due to an error on client-side, i.e. the request is invalid. The client should not repeat the request without modifications.  The response body SHOULD contain a JSON error object. MUST be any HTTP status code specified in [RFC 7231](https://tools.ietf.org/html/rfc7231#section-6.6). This request MUST respond with HTTP status codes 401 if authorization is required or 403 if the authorization failed or access is forbidden in general to the authenticated user. HTTP status code 404 should be used if the value of a path parameter is invalid.  See also: * [Error Handling](#section/API-Principles/Error-Handling) in the API in general. * [Common Error Codes](errors.json) (status code 400)
     *         or The request can&#39;t be fulfilled due to an error at the back-end. The error is never the client’s fault and therefore it is reasonable for the client to retry the exact same request that triggered this response.  The response body SHOULD contain a JSON error object. MUST be any HTTP status code specified in [RFC 7231](https://tools.ietf.org/html/rfc7231#section-6.6).  See also: * [Error Handling](#section/API-Principles/Error-Handling) in the API in general. * [Common Error Codes](errors.json) (status code 500)
     */
    @Operation(summary = "Basic metadata for all datasets", operationId = "listCollections", description = "Lists available collections with at least the required information.  It is **strongly RECOMMENDED** to keep the response size small by omitting larger optional values from the objects in `collections` (e.g. the `summaries` and `cube:dimensions` properties). To get the full metadata for a collection clients MUST request `GET /collections/{collection_id}`.  This endpoint is compatible with [STAC 0.9.0](https://stacspec.org) and [OGC API - Features](http://docs.opengeospatial.org/is/17-069r3/17-069r3.html). [STAC API](https://github.com/radiantearth/stac-spec/tree/v0.9.0/api-spec) features / extensions and [STAC extensions](https://github.com/radiantearth/stac-spec/tree/v0.9.0/extensions) can be implemented in addition to what is documented here.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lists of collections and related links."),
        @ApiResponse(responseCode = "400", description = "The request can't be fulfilled due to an error on client-side, i.e. the request is invalid. The client should not repeat the request without modifications.  The response body SHOULD contain a JSON error object. MUST be any HTTP status code specified in [RFC 7231](https://tools.ietf.org/html/rfc7231#section-6.6). This request MUST respond with HTTP status codes 401 if authorization is required or 403 if the authorization failed or access is forbidden in general to the authenticated user. HTTP status code 404 should be used if the value of a path parameter is invalid.  See also: * [Error Handling](#section/API-Principles/Error-Handling) in the API in general. * [Common Error Codes](errors.json)"),
        @ApiResponse(responseCode = "500", description = "The request can't be fulfilled due to an error at the back-end. The error is never the client’s fault and therefore it is reasonable for the client to retry the exact same request that triggered this response.  The response body SHOULD contain a JSON error object. MUST be any HTTP status code specified in [RFC 7231](https://tools.ietf.org/html/rfc7231#section-6.6).  See also: * [Error Handling](#section/API-Principles/Error-Handling) in the API in general. * [Common Error Codes](errors.json)") })
    @GetMapping(value = "/collections", produces = { "application/json" })
    @Override
    public ResponseEntity<Collections> listCollections(@Min(1)@Parameter(name = "This parameter enables pagination for the endpoint and specifies the maximum number of elements that arrays in the top-level object (e.g. jobs or log entries) are allowed to contain. The only exception is the `links` array, which MUST NOT be paginated as otherwise the pagination links may be missing ins responses. If the parameter is not provided or empty, all elements are returned.  Pagination is OPTIONAL and back-ends and clients may not support it. Therefore it MUST be implemented in a way that clients not supporting pagination get all resources regardless. Back-ends not supporting  pagination will return all resources.  If the response is paginated, the links array MUST be used to propagate the  links for pagination with pre-defined `rel` types. See the links array schema for supported `rel` types.  *Note:* Implementations can use all kind of pagination techniques, depending on what is supported best by their infrastructure. So it doesn't care whether it is page-based, offset-based or uses tokens for pagination. The clients will use whatever is specified in the links with the corresponding `rel` types.") @Valid @RequestParam(value = "limit", required = false) Integer limit) {
    	Collections collectionsList = new Collections();
    	
    	try {
			URL url;
			url = new URL("http://saocompute.eurac.edu/rasdaman/ows" + "?SERVICE=WCS&VERSION=2.0.1&REQUEST=GetCapabilities");
    	
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			SAXBuilder builder = new SAXBuilder();
			Document capabilititesDoc = builder.build(conn.getInputStream());
			Element rootNodeCollectionsList = capabilititesDoc.getRootElement();
			Namespace defaultNSCollectionsList = rootNodeCollectionsList.getNamespace();
//			log.debug("root node info: " + rootNode.getName());
			List<Element> coverageList = rootNodeCollectionsList.getChildren("Contents", defaultNSCollectionsList).get(0).getChildren("CoverageSummary", defaultNSCollectionsList);
			
			for(int collection = 0; collection < coverageList.size(); collection++) {				
				Collection currentCollection = new Collection();
				Element coverage = coverageList.get(collection);
//				log.debug("root node info: " + coverage.getName() + ":" + coverage.getChildText("CoverageId", defaultNS));		
				String coverageID = coverage.getChildText("CoverageId", defaultNSCollectionsList);
				currentCollection.setId(coverageID);				
				currentCollection.setStacVersion("0.9.0");
				URL urlCollections = new URL("http://saocompute.eurac.edu/rasdaman/ows"
						+ "?SERVICE=WCS&VERSION=2.0.1&REQUEST=DescribeCoverage&COVERAGEID=" + coverageID);
				HttpURLConnection connCollections = (HttpURLConnection) urlCollections.openConnection();
				connCollections.setRequestMethod("GET");
				SAXBuilder builderInt = new SAXBuilder();
				Document capabilititesDocCollections = builderInt.build(connCollections.getInputStream());
				List<Namespace> namespacesCollections = capabilititesDocCollections.getNamespacesIntroduced();

				Element rootNode = capabilititesDocCollections.getRootElement();
				Namespace defaultNS = rootNode.getNamespace();
				Namespace gmlNS = null;
				Namespace sweNS = null;
				Namespace gmlCovNS =  null;
				Namespace gmlrgridNS = null;
				for (int n = 0; n < namespacesCollections.size(); n++) {
					Namespace current = namespacesCollections.get(n);
					if(current.getPrefix().equals("swe")) {
						sweNS = current;
					}
					if(current.getPrefix().equals("gmlcov")) {
						gmlCovNS = current;
					}
					if(current.getPrefix().equals("gml")) {
						gmlNS = current;
					}
					if(current.getPrefix().equals("gmlrgrid")) {
						gmlrgridNS = current;
					}
				}			
//				log.debug("root node info: " + rootNode.getName());		
						
				Element coverageDescElement = rootNode.getChild("CoverageDescription", defaultNS);
				Element boundedByElement = coverageDescElement.getChild("boundedBy", gmlNS);
				Element boundingBoxElement = boundedByElement.getChild("Envelope", gmlNS);
				Element metadataElement = null;
				try {
				metadataElement = rootNode.getChild("CoverageDescription", defaultNS).getChild("metadata", gmlNS).getChild("Extension", gmlNS).getChild("covMetadata", gmlNS);
			    }catch(Exception e) {
//				log.warn("Error in parsing bands :" + e.getMessage());
			    }
				
//				metadataObj = new JSONObject(metadataString1);
//				String metadataString2 = metadataString1.replaceAll("\\n","");
//				String metadataString3 = metadataString2.replaceAll("\"\"","\"");
//				metadataObj = new JSONObject(metadataString3);
//				JSONArray slices = metadataObj.getJSONArray("slices");

				String srsDescription = boundingBoxElement.getAttributeValue("srsName");
				if (srsDescription.contains("EPSG")) {
				try {
					srsDescription = srsDescription.substring(srsDescription.indexOf("EPSG"), srsDescription.indexOf("&")).replace("/0/", ":");
					srsDescription = srsDescription.replaceAll("EPSG:","");

				}catch(StringIndexOutOfBoundsException e) {
					srsDescription = srsDescription.substring(srsDescription.indexOf("EPSG")).replace("/0/", ":");
					srsDescription = srsDescription.replaceAll("EPSG:","");							
				}

				SpatialReference src = new SpatialReference();
				src.ImportFromEPSG(Integer.parseInt(srsDescription));

				SpatialReference dst = new SpatialReference();
				dst.ImportFromEPSG(4326);

				String[] minValues = boundingBoxElement.getChildText("lowerCorner", gmlNS).split(" ");
				String[] maxValues = boundingBoxElement.getChildText("upperCorner", gmlNS).split(" ");			

				CoordinateTransformation tx = new CoordinateTransformation(src, dst);

				String[] axis = boundingBoxElement.getAttribute("axisLabels").getValue().split(" ");
				int xIndex = 0;
				int yIndex = 0;

				Map<String, Dimension> cubeColonDimensions = new HashMap<String, Dimension>();
				DimensionBands dimensionbands = new DimensionBands();
				dimensionbands.setType(TypeEnum.BANDS);
//				log.debug("number of bands found: " + bandsListSwe.size());			
				DimensionSpatial dimensionXspatial = new DimensionSpatial();
				dimensionXspatial.setType(TypeEnum.SPATIAL);
				DimensionSpatial dimensionYspatial = new DimensionSpatial();
				dimensionYspatial.setType(TypeEnum.SPATIAL);
				DimensionTemporal dimensionTemporal = new DimensionTemporal();
				dimensionTemporal.setType(TypeEnum.TEMPORAL);
				
				List<Element> bandsList = null;
				List<Element> bandsListSwe = null;
				Boolean bandsMeta = false;
				try {
					bandsList = metadataElement.getChild("bands", gmlNS).getChildren();
					bandsMeta = true;
				}catch(Exception e) {
//			        log.warn("Error in parsing bands :" + e.getMessage());
				}
				try {
					bandsListSwe = rootNode.getChild("CoverageDescription", defaultNS).getChild("rangeType", gmlNS).getChild("DataRecord", sweNS).getChildren("field", sweNS);
				}catch(Exception e) {
//					log.warn("Error in parsing bands List :" + e.getMessage());
				}
				if (bandsMeta) {
					try {
						for(int c = 0; c < bandsList.size(); c++) {
							String bandWave = null;
							String bandCommonName = null;
							String bandGSD = null;
							Element band = bandsList.get(c);
							String bandId = band.getName();
							dimensionbands.addValuesItem(bandId);
							try {
								bandWave = band.getChildText("WAVELENGTH");
							}catch(Exception e) {
//								log.warn("Error in parsing band wave-lenght:" + e.getMessage());
							}
							try {
								bandCommonName = band.getChildText("common_name");
							}catch(Exception e) {
//								log.warn("Error in parsing band common name:" + e.getMessage());
							}
							try {
								bandGSD = band.getChildText("gsd");
							}catch(Exception e) {
//								log.warn("Error in parsing band gsd:" + e.getMessage());
							}
						}
					}catch(Exception e) {
//						log.warn("Error in parsing bands :" + e.getMessage());
					}
				}
				else {
					for(int c = 0; c < bandsListSwe.size(); c++) {
						String bandId = bandsListSwe.get(c).getAttributeValue("name");
						dimensionbands.addValuesItem(bandId);
					}
				}
				cubeColonDimensions.put("bands", dimensionbands);
				currentCollection.setCubeColonDimensions(cubeColonDimensions);			

				double[] c1 = null;
				double[] c2 = null;
				int j = 0;

				for(int a = 0; a < axis.length; a++) {
//				    log.debug(axis[a]);
					if(axis[a].equals("E") || axis[a].equals("X") || axis[a].equals("Long") || axis[a].equals("N") || axis[a].equals("Y") || axis[a].equals("Lat")){
						j = a;
						break;
					}
				}
//				log.debug(j);

				c1 = tx.TransformPoint(Double.parseDouble(minValues[j]), Double.parseDouble(minValues[j+1]));
				c2 = tx.TransformPoint(Double.parseDouble(maxValues[j]), Double.parseDouble(maxValues[j+1]));

				String startTime = null;
				String endTime = null;
				for(int a = 0; a < axis.length; a++) {
//				    log.debug(axis[a]);
					if(axis[a].equals("E") || axis[a].equals("X") || axis[a].equals("Long")){
						xIndex=a;
						dimensionXspatial.setReferenceSystem(srsDescription);
						dimensionXspatial.setAxis(AxisEnum.X);
						List<BigDecimal> xExtent = new ArrayList<BigDecimal>();
						xExtent.add(0, new BigDecimal(c1[1]));
						xExtent.add(1, new BigDecimal(c2[1]));
						dimensionXspatial.setExtent(xExtent);
						cubeColonDimensions.put(axis[a], dimensionXspatial);
					}
					if(axis[a].equals("N") || axis[a].equals("Y") || axis[a].equals("Lat")){
						yIndex=a;
						dimensionYspatial.setReferenceSystem(srsDescription);
						dimensionYspatial.setAxis(AxisEnum.Y);
						List<BigDecimal> yExtent = new ArrayList<BigDecimal>();
						yExtent.add(0, new BigDecimal(c1[0]));
						yExtent.add(1, new BigDecimal(c2[0]));
						dimensionYspatial.setExtent(yExtent);
						cubeColonDimensions.put(axis[a], dimensionYspatial);
					}
					if(axis[a].equals("DATE")  || axis[a].equals("TIME") || axis[a].equals("ANSI") || axis[a].equals("Time") || axis[a].equals("Date") || axis[a].equals("time") || axis[a].equals("ansi") || axis[a].equals("date") || axis[a].equals("unix")){
						List<String> temporalExtent = new ArrayList<String>();
						temporalExtent.add(0, minValues[a].replaceAll("\"", ""));
						temporalExtent.add(1, maxValues[a].replaceAll("\"", ""));
						startTime = minValues[a].replaceAll("\"", "");
						endTime = maxValues[a].replaceAll("\"", "");
						dimensionTemporal.setExtent(temporalExtent);
						String[] taxis = null;
						try {
							List<Element> tList = rootNode.getChild("CoverageDescription", defaultNS).getChild("domainSet", gmlNS).getChild("RectifiedGrid", gmlNS).getChildren("offsetVector", gmlNS);
							taxis = tList.get(a).getValue().split(" ");
							dimensionTemporal.setStep(JsonNullable.of(taxis[0]));
					    }catch(Exception e) {
					    	log.warn("Irregular Axis :" + e.getMessage());
					    	dimensionTemporal.setStep(JsonNullable.of(null));
					    }
						cubeColonDimensions.put(axis[a], dimensionTemporal);
					}
				}		    
//				log.debug(srsDescription);

				CollectionExtent extent = new CollectionExtent();
				CollectionSpatialExtent spatialExtent = new CollectionSpatialExtent();
				List<List<BigDecimal>> bbox = new ArrayList<List<BigDecimal>>();
				List<BigDecimal> bbox1 = new ArrayList<BigDecimal>();
				CollectionTemporalExtent temporalExtent = new CollectionTemporalExtent();
				List<List<OffsetDateTime>> interval = new ArrayList<List<OffsetDateTime>>();
				List<OffsetDateTime> interval1 = new ArrayList<OffsetDateTime>();

				bbox1.add(new BigDecimal(c1[1]));
				bbox1.add(new BigDecimal(c1[0]));
				bbox1.add(new BigDecimal(c2[1]));
				bbox1.add(new BigDecimal(c2[0]));
				bbox.add(bbox1);
				spatialExtent.setBbox(bbox);
				extent.setSpatial(spatialExtent);			

//				int k = 0;
//				for(int a = 0; a < axis.length; a++) {
//					//				    	log.debug(axis[a]);
//					String timeAxis = axis[a].toUpperCase();
//					if(timeAxis.equals("DATE") || timeAxis.equals("TIME") || timeAxis.equals("ANSI") || timeAxis.equals("UNIX"))
//					{
//						k = a;
//						break;
//					}
//				}							

				try {
					interval1.add(OffsetDateTime.parse(startTime));
					interval1.add(OffsetDateTime.parse(endTime));
					interval.add(interval1);
					temporalExtent.setInterval(interval);
					extent.setTemporal(temporalExtent);
				}catch(Exception e) {
				}
				currentCollection.setExtent(extent);
				}
				
				else {
					srsDescription = "0";

					String[] minValues = boundingBoxElement.getChildText("lowerCorner", gmlNS).split(" ");
					String[] maxValues = boundingBoxElement.getChildText("upperCorner", gmlNS).split(" ");
					String[] axis = boundingBoxElement.getAttribute("axisLabels").getValue().split(" ");
					int xIndex = 0;
					int yIndex = 0;

					Map<String, Dimension> cubeColonDimensions = new HashMap<String, Dimension>();
					DimensionBands dimensionbands = new DimensionBands();
					dimensionbands.setType(TypeEnum.BANDS);
//					log.debug("number of bands found: " + bandsListSwe.size());			
					DimensionSpatial dimensionXspatial = new DimensionSpatial();
					dimensionXspatial.setType(TypeEnum.SPATIAL);
					DimensionSpatial dimensionYspatial = new DimensionSpatial();
					dimensionYspatial.setType(TypeEnum.SPATIAL);
					DimensionTemporal dimensionTemporal = new DimensionTemporal();
					dimensionTemporal.setType(TypeEnum.TEMPORAL);
					
					List<Element> bandsList = null;
					List<Element> bandsListSwe = null;
					Boolean bandsMeta = false;
					try {
						bandsList = metadataElement.getChild("bands", gmlNS).getChildren();
						bandsMeta = true;
					}catch(Exception e) {
//				        log.warn("Error in parsing bands :" + e.getMessage());
					}
					try {
						bandsListSwe = rootNode.getChild("CoverageDescription", defaultNS).getChild("rangeType", gmlNS).getChild("DataRecord", sweNS).getChildren("field", sweNS);
					}catch(Exception e) {
//						log.warn("Error in parsing bands List :" + e.getMessage());
					}
					if (bandsMeta) {
						try {
							for(int c = 0; c < bandsList.size(); c++) {
								String bandWave = null;
								String bandCommonName = null;
								String bandGSD = null;
								Element band = bandsList.get(c);
								String bandId = band.getName();
								dimensionbands.addValuesItem(bandId);
								try {
									bandWave = band.getChildText("WAVELENGTH");
								}catch(Exception e) {
//									log.warn("Error in parsing band wave-lenght:" + e.getMessage());
								}
								try {
									bandCommonName = band.getChildText("common_name");
								}catch(Exception e) {
//									log.warn("Error in parsing band common name:" + e.getMessage());
								}
								try {
									bandGSD = band.getChildText("gsd");
								}catch(Exception e) {
//									log.warn("Error in parsing band gsd:" + e.getMessage());
								}
							}
						}catch(Exception e) {
//							log.warn("Error in parsing bands :" + e.getMessage());
						}
					}
					else {
						for(int c = 0; c < bandsListSwe.size(); c++) {
							String bandId = bandsListSwe.get(c).getAttributeValue("name");
							dimensionbands.addValuesItem(bandId);
						}
					}
					cubeColonDimensions.put("bands", dimensionbands);
					currentCollection.setCubeColonDimensions(cubeColonDimensions);

					int j = 0;

					for(int a = 0; a < axis.length; a++) {
//					    log.debug(axis[a]);
						if(axis[a].equals("i") || axis[a].equals("j")){
							j = a;
							break;
						}
					}
//					log.debug(j);
					
					String startTime = null;
					String endTime = null;
					for(int a = 0; a < axis.length; a++) {
//					    log.debug(axis[a]);
						if(axis[a].equals("i")){
							xIndex=a;
							dimensionXspatial.setReferenceSystem(srsDescription);
							dimensionXspatial.setAxis(AxisEnum.X);
							List<BigDecimal> xExtent = new ArrayList<BigDecimal>();
							xExtent.add(0, new BigDecimal(minValues[j]));
							xExtent.add(1, new BigDecimal(maxValues[j]));
							dimensionXspatial.setExtent(xExtent);
							cubeColonDimensions.put(axis[a], dimensionXspatial);
						}
						if(axis[a].equals("j")){
							yIndex=a;
							dimensionYspatial.setReferenceSystem(srsDescription);
							dimensionYspatial.setAxis(AxisEnum.Y);
							List<BigDecimal> yExtent = new ArrayList<BigDecimal>();
							yExtent.add(0, new BigDecimal(minValues[j+1]));
							yExtent.add(1, new BigDecimal(minValues[j+1]));
							dimensionYspatial.setExtent(yExtent);
							cubeColonDimensions.put(axis[a], dimensionYspatial);
						}
						if(axis[a].equals("DATE")  || axis[a].equals("TIME") || axis[a].equals("ANSI") || axis[a].equals("Time") || axis[a].equals("Date") || axis[a].equals("time") || axis[a].equals("ansi") || axis[a].equals("date") || axis[a].equals("unix")){
							List<String> temporalExtent = new ArrayList<String>();
							temporalExtent.add(0, minValues[a].replaceAll("\"", ""));
							temporalExtent.add(1, maxValues[a].replaceAll("\"", ""));
							startTime = minValues[a].replaceAll("\"", "");
							endTime = maxValues[a].replaceAll("\"", "");
							dimensionTemporal.setExtent(temporalExtent);
							String[] taxis = null;
							try {
								List<Element> tList = rootNode.getChild("CoverageDescription", defaultNS).getChild("domainSet", gmlNS).getChild("RectifiedGrid", gmlNS).getChildren("offsetVector", gmlNS);
								taxis = tList.get(a).getValue().split(" ");
								dimensionTemporal.setStep(JsonNullable.of(taxis[0]));
						    }catch(Exception e) {
						    	log.warn("Irregular Axis :" + e.getMessage());
						    	dimensionTemporal.setStep(JsonNullable.of(null));
						    }
							cubeColonDimensions.put(axis[a], dimensionTemporal);
						}
					}

					CollectionExtent extent = new CollectionExtent();
					CollectionSpatialExtent spatialExtent = new CollectionSpatialExtent();
					List<List<BigDecimal>> bbox = new ArrayList<List<BigDecimal>>();
					List<BigDecimal> bbox1 = new ArrayList<BigDecimal>();
					CollectionTemporalExtent temporalExtent = new CollectionTemporalExtent();
					List<List<OffsetDateTime>> interval = new ArrayList<List<OffsetDateTime>>();
					List<OffsetDateTime> interval1 = new ArrayList<OffsetDateTime>();

					bbox1.add(new BigDecimal(minValues[j]));
					bbox1.add(new BigDecimal(minValues[j+1]));
					bbox1.add(new BigDecimal(maxValues[j]));
					bbox1.add(new BigDecimal(maxValues[j+1]));
					bbox.add(bbox1);
					spatialExtent.setBbox(bbox);
					extent.setSpatial(spatialExtent);

//					int k = 0;
//					for(int a = 0; a < axis.length; a++) {
//						//				    	log.debug(axis[a]);
//						String timeAxis = axis[a].toUpperCase();
//						if(timeAxis.equals("DATE") || timeAxis.equals("TIME") || timeAxis.equals("ANSI") || timeAxis.equals("UNIX"))
//						{
//							k = a;
//							break;
//						}
//					}							

					try {
						interval1.add(OffsetDateTime.parse(startTime));
						interval1.add(OffsetDateTime.parse(endTime));
						interval.add(interval1);
						temporalExtent.setInterval(interval);
						extent.setTemporal(temporalExtent);
					}catch(Exception e) {
					}
					currentCollection.setExtent(extent);
					}

				Link link1 = new Link();
				List<Link> links = new ArrayList<Link>();
				link1.setRel("license");
				link1.setType("text/html");
				try {
					link1.setHref(new URI ("https://creativecommons.org/licenses/by/4.0/"));
				} catch (URISyntaxException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				links.add(0, link1);
				currentCollection.setLinks(links);

				String title = null;
				String description = null;
				try {
					title = metadataElement.getChildText("Title", gmlNS);
					currentCollection.setTitle(title);
				}catch(Exception e) {
//				    	log.warn("Error in parsing Project Name :" + e.getMessage());
				}
				try {
					description = metadataElement.getChildText("Description", gmlNS);
					currentCollection.setDescription(description);
				}catch(Exception e) {
//		            	log.warn("Error in parsing Title :" + e.getMessage());
				}

			collectionsList.addCollectionsItem(currentCollection);
			}
			Link linkItems = new Link();
			linkItems.setRel("alternate");
			try {
				linkItems.setHref(new URI ("http://saocompute.eurac.edu/rasdaman/ows"));
			} catch (URISyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			linkItems.setTitle("openEO STAC Catalog (STAC Version 0.9.0)");
			collectionsList.addLinksItem(linkItems);


//			getRequest().ifPresent(request -> {
//				for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
//					if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
//
//
//						String exampleString = "{ \"collections\" : [ { \"extent\" : { \"spatial\" : { \"bbox\" : [ [ -180, -90, 180, 90 ], [ -180, -90, 180, 90 ] ] }, \"temporal\" : { \"interval\" : [ [ \"2011-11-11T12:22:11Z\", null ], [ \"2011-11-11T12:22:11Z\", null ] ] } }, \"stac_version\" : \"stac_version\", \"keywords\" : [ \"keywords\", \"keywords\" ], \"deprecated\" : false, \"description\" : \"description\", \"cube:dimensions\" : { \"key\" : \"\" }, \"title\" : \"title\", \"version\" : \"version\", \"license\" : \"Apache-2.0\", \"assets\" : { \"key\" : { \"roles\" : [ \"data\" ], \"description\" : \"description\", \"href\" : \"href\", \"title\" : \"title\", \"type\" : \"image/tiff; application=geotiff\" } }, \"links\" : [ { \"rel\" : \"related\", \"href\" : \"https://example.openeo.org\", \"type\" : \"text/html\", \"title\" : \"openEO\" }, { \"rel\" : \"related\", \"href\" : \"https://example.openeo.org\", \"type\" : \"text/html\", \"title\" : \"openEO\" } ], \"id\" : \"Sentinel-2A\", \"stac_extensions\" : [ \"\", \"\" ], \"providers\" : [ \"{}\", \"{}\" ], \"summaries\" : { \"key\" : \"\" } }, { \"extent\" : { \"spatial\" : { \"bbox\" : [ [ -180, -90, 180, 90 ], [ -180, -90, 180, 90 ] ] }, \"temporal\" : { \"interval\" : [ [ \"2011-11-11T12:22:11Z\", null ], [ \"2011-11-11T12:22:11Z\", null ] ] } }, \"stac_version\" : \"stac_version\", \"keywords\" : [ \"keywords\", \"keywords\" ], \"deprecated\" : false, \"description\" : \"description\", \"cube:dimensions\" : { \"key\" : \"\" }, \"title\" : \"title\", \"version\" : \"version\", \"license\" : \"Apache-2.0\", \"assets\" : { \"key\" : { \"roles\" : [ \"data\" ], \"description\" : \"description\", \"href\" : \"href\", \"title\" : \"title\", \"type\" : \"image/tiff; application=geotiff\" } }, \"links\" : [ { \"rel\" : \"related\", \"href\" : \"https://example.openeo.org\", \"type\" : \"text/html\", \"title\" : \"openEO\" }, { \"rel\" : \"related\", \"href\" : \"https://example.openeo.org\", \"type\" : \"text/html\", \"title\" : \"openEO\" } ], \"id\" : \"Sentinel-2A\", \"stac_extensions\" : [ \"\", \"\" ], \"providers\" : [ \"{}\", \"{}\" ], \"summaries\" : { \"key\" : \"\" } } ], \"links\" : [ { \"rel\" : \"related\", \"href\" : \"https://example.openeo.org\", \"type\" : \"text/html\", \"title\" : \"openEO\" }, { \"rel\" : \"related\", \"href\" : \"https://example.openeo.org\", \"type\" : \"text/html\", \"title\" : \"openEO\" } ] }";
//						ApiUtil.setExampleResponse(request, "application/json", exampleString);
//						break;
//					}
//				}
//			});
    	
    	return new ResponseEntity<Collections>(collectionsList, HttpStatus.OK);
    	}
    	
    	catch (MalformedURLException e) {
//			log.error("An error occured while requesting capabilities from WCPS endpoint: " + e.getMessage());
//			for( StackTraceElement element: e.getStackTrace()) {
//				log.error(element.toString());
//			}
//			return Response.serverError()
//					.entity(new ApiResponseMessage(ApiResponseMessage.ERROR,
//							"An error occured while requesting capabilities from WCPS endpoint: " + e.getMessage()))
//					.build();
			return  new ResponseEntity<Collections>(collectionsList, HttpStatus.BAD_REQUEST);
		} 
    	
    	catch (IOException e) {
//			log.error("An error occured while requesting capabilities from WCPS endpoint: " + e.getMessage());
//			for( StackTraceElement element: e.getStackTrace()) {
//				log.error(element.toString());
//			}
//			return Response.serverError()
//					.entity(new ApiResponseMessage(ApiResponseMessage.ERROR,
//							"An error occured while requesting capabilities from WCPS endpoint: " + e.getMessage()))
//					.build();
			return  new ResponseEntity<Collections>(collectionsList, HttpStatus.BAD_REQUEST);
		} 
    	
    	catch (JDOMException e) {
//			log.error("An error occured while requesting capabilities from WCPS endpoint: " + e.getMessage());
//			for( StackTraceElement element: e.getStackTrace()) {
//				log.error(element.toString());
//			}
//			return Response.serverError()
//					.entity(new ApiResponseMessage(ApiResponseMessage.ERROR,
//							"An error occured while requesting capabilities from WCPS endpoint: " + e.getMessage())).build();
			return  new ResponseEntity<Collections>(collectionsList, HttpStatus.BAD_REQUEST);
		}        
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
    public ResponseEntity<Collection> describeCollection(@Pattern(regexp="^[\\w\\-\\.~/]+$") @Parameter(name = "Collection identifier",required=true) @PathVariable("collection_id") String collectionId, Principal principal) {
    	    	
//    	log.debug("The following user is authenticated: " + principal.getName());
//    	Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//    	if (!(authentication instanceof AnonymousAuthenticationToken)) {
//    	    String currentUserName = authentication.getName();
//    	    log.debug("The following user is authenticated: " + currentUserName);
//    	}else {
//    		log.warn("The current user is not authenticated!");
//    	}
    	
    	URL url;
    	Collection currentCollection = new Collection();
    	try {
    		currentCollection.setId(collectionId);
    		currentCollection.setStacVersion("0.9.0");
			url = new URL("http://saocompute.eurac.edu/rasdaman/ows" + "?SERVICE=WCS&VERSION=2.0.1&REQUEST=DescribeCoverage&COVERAGEID=" + collectionId);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			SAXBuilder builder = new SAXBuilder();
			Document capabilititesDoc = builder.build(conn.getInputStream());
			List<Namespace> namespaces = capabilititesDoc.getNamespacesIntroduced();
			Element rootNode = capabilititesDoc.getRootElement();
			Namespace defaultNS = rootNode.getNamespace();
			Namespace gmlNS = null;
			Namespace sweNS = null;
			Namespace gmlCovNS =  null;
			Namespace gmlrgridNS = null;
			for (int n = 0; n < namespaces.size(); n++) {
				Namespace current = namespaces.get(n);
				if(current.getPrefix().equals("swe")) {
					sweNS = current;
				}
				if(current.getPrefix().equals("gmlcov")) {
					gmlCovNS = current;
				}
				if(current.getPrefix().equals("gml")) {
					gmlNS = current;
				}
				if(current.getPrefix().equals("gmlrgrid")) {
					gmlrgridNS = current;
				}
			}
//			log.debug("root node info: " + rootNode.getName());
					
			Element coverageDescElement = rootNode.getChild("CoverageDescription", defaultNS);
			Element boundedByElement = coverageDescElement.getChild("boundedBy", gmlNS);
			Element boundingBoxElement = boundedByElement.getChild("Envelope", gmlNS);
			Element metadataElement = null;
			try {
			metadataElement = rootNode.getChild("CoverageDescription", defaultNS).getChild("metadata", gmlNS).getChild("Extension", gmlNS).getChild("covMetadata", gmlNS);
		    }catch(Exception e) {
//			log.warn("Error in parsing bands :" + e.getMessage());
		    }
			
//			metadataObj = new JSONObject(metadataString1);
//			String metadataString2 = metadataString1.replaceAll("\\n","");
//			String metadataString3 = metadataString2.replaceAll("\"\"","\"");
//			metadataObj = new JSONObject(metadataString3);
//			JSONArray slices = metadataObj.getJSONArray("slices");
			
			String srsDescription = boundingBoxElement.getAttributeValue("srsName");
			if (srsDescription.contains("EPSG")) {
			try {
				srsDescription = srsDescription.substring(srsDescription.indexOf("EPSG"), srsDescription.indexOf("&")).replace("/0/", ":");
				srsDescription = srsDescription.replaceAll("EPSG:","");
				
			}catch(StringIndexOutOfBoundsException e) {
				srsDescription = srsDescription.substring(srsDescription.indexOf("EPSG")).replace("/0/", ":");
				srsDescription = srsDescription.replaceAll("EPSG:","");							
			}			
			
			SpatialReference src = new SpatialReference();
			src.ImportFromEPSG(Integer.parseInt(srsDescription));

			SpatialReference dst = new SpatialReference();
			dst.ImportFromEPSG(4326);
			
			String[] minValues = boundingBoxElement.getChildText("lowerCorner", gmlNS).split(" ");
			String[] maxValues = boundingBoxElement.getChildText("upperCorner", gmlNS).split(" ");			
			
			CoordinateTransformation tx = new CoordinateTransformation(src, dst);
		    
		    String[] axis = boundingBoxElement.getAttribute("axisLabels").getValue().split(" ");
		    int xIndex = 0;
		    int yIndex = 0;
			
		    Map<String, Dimension> cubeColonDimensions = new HashMap<String, Dimension>();
			DimensionBands dimensionbands = new DimensionBands();
			dimensionbands.setType(TypeEnum.BANDS);
//			log.debug("number of bands found: " + bandsListSwe.size());			
			DimensionSpatial dimensionXspatial = new DimensionSpatial();
			dimensionXspatial.setType(TypeEnum.SPATIAL);
			DimensionSpatial dimensionYspatial = new DimensionSpatial();
			dimensionYspatial.setType(TypeEnum.SPATIAL);
			DimensionTemporal dimensionTemporal = new DimensionTemporal();
			dimensionTemporal.setType(TypeEnum.TEMPORAL);
			
			List<Element> bandsList = null;
			List<Element> bandsListSwe = null;
			Boolean bandsMeta = false;
			try {
				bandsList = metadataElement.getChild("bands", gmlNS).getChildren();
				bandsMeta = true;
			}catch(Exception e) {
				//			log.warn("Error in parsing bands :" + e.getMessage());
			}
			try {
				bandsListSwe = rootNode.getChild("CoverageDescription", defaultNS).getChild("rangeType", gmlNS).getChild("DataRecord", sweNS).getChildren("field", sweNS);
			}catch(Exception e) {
				//					log.warn("Error in parsing bands List :" + e.getMessage());
			}
			if (bandsMeta) {
				try {
					for(int c = 0; c < bandsList.size(); c++) {
						String bandWave = null;
						String bandCommonName = null;
						String bandGSD = null;
						Element band = bandsList.get(c);
						String bandId = band.getName();
						dimensionbands.addValuesItem(bandId);
						try {
							bandWave = band.getChildText("WAVELENGTH");
						}catch(Exception e) {
//							log.warn("Error in parsing band wave-lenght:" + e.getMessage());
						}
						try {
							bandCommonName = band.getChildText("common_name");
						}catch(Exception e) {
//							log.warn("Error in parsing band common name:" + e.getMessage());
						}
						try {
							bandGSD = band.getChildText("gsd");
						}catch(Exception e) {
//							log.warn("Error in parsing band gsd:" + e.getMessage());
						}
						Map<String,CollectionSummaryStats[]> summaries = new HashMap<String,CollectionSummaryStats[]>();
						CollectionSummaryStats gsd = new CollectionSummaryStats();
						CollectionSummaryStats[] eoGsd = {gsd};
//						summaries.put("eo:bands", eoGsd);
//						currentCollection.setSummaries(summaries);
//						currentCollection.putSummariesItem("eo:gsd", eoGsd);
					}
				}catch(Exception e) {
//					log.warn("Error in parsing bands :" + e.getMessage());
				}
			}
			else {
				for(int c = 0; c < bandsListSwe.size(); c++) {
					String bandId = bandsListSwe.get(c).getAttributeValue("name");
					dimensionbands.addValuesItem(bandId);
				}
			}
			cubeColonDimensions.put("bands", dimensionbands);
			currentCollection.setCubeColonDimensions(cubeColonDimensions);			
			
			double[] c1 = null;
			double[] c2 = null;
			int j = 0;
			
			for(int a = 0; a < axis.length; a++) {
//		    	log.debug(axis[a]);
				if(axis[a].equals("E") || axis[a].equals("X") || axis[a].equals("Long") || axis[a].equals("N") || axis[a].equals("Y") || axis[a].equals("Lat")){
					j = a;
					break;
				}
			}
//			log.debug(j);
			
			c1 = tx.TransformPoint(Double.parseDouble(minValues[j]), Double.parseDouble(minValues[j+1]));
			c2 = tx.TransformPoint(Double.parseDouble(maxValues[j]), Double.parseDouble(maxValues[j+1]));
			
			String startTime = null;
			String endTime = null;
			
			for(int a = 0; a < axis.length; a++) {
//		    	log.debug(axis[a]);
				if(axis[a].equals("E") || axis[a].equals("X") || axis[a].equals("Long")){
					xIndex=a;
					dimensionXspatial.setReferenceSystem(srsDescription);
					dimensionXspatial.setAxis(AxisEnum.X);
					List<BigDecimal> xExtent = new ArrayList<BigDecimal>();
					xExtent.add(0, new BigDecimal(c1[1]));
					xExtent.add(1, new BigDecimal(c2[1]));
					dimensionXspatial.setExtent(xExtent);
					cubeColonDimensions.put(axis[a], dimensionXspatial);
				}
				if(axis[a].equals("N") || axis[a].equals("Y") || axis[a].equals("Lat")){
					yIndex=a;
					dimensionYspatial.setReferenceSystem(srsDescription);
					dimensionYspatial.setAxis(AxisEnum.Y);
					List<BigDecimal> yExtent = new ArrayList<BigDecimal>();
					yExtent.add(0, new BigDecimal(c1[0]));
					yExtent.add(1, new BigDecimal(c2[0]));
					dimensionYspatial.setExtent(yExtent);
					cubeColonDimensions.put(axis[a], dimensionYspatial);
				}
				if(axis[a].equals("DATE")  || axis[a].equals("TIME") || axis[a].equals("ANSI") || axis[a].equals("Time") || axis[a].equals("Date") || axis[a].equals("time") || axis[a].equals("ansi") || axis[a].equals("date") || axis[a].equals("unix")){
					List<String> temporalExtent = new ArrayList<String>();
					temporalExtent.add(0, minValues[a].replaceAll("\"", ""));
					temporalExtent.add(1, maxValues[a].replaceAll("\"", ""));
					startTime = minValues[a].replaceAll("\"", "");
					endTime = maxValues[a].replaceAll("\"", "");
					dimensionTemporal.setExtent(temporalExtent);
					String[] taxis = null;
					try {
						List<Element> tList = rootNode.getChild("CoverageDescription", defaultNS).getChild("domainSet", gmlNS).getChild("RectifiedGrid", gmlNS).getChildren("offsetVector", gmlNS);
						taxis = tList.get(a).getValue().split(" ");
						dimensionTemporal.setStep(JsonNullable.of(taxis[0]));
				    }catch(Exception e) {
				    	log.warn("Irregular Axis :" + e.getMessage());
				    	dimensionTemporal.setStep(JsonNullable.of(null));
				    }
					cubeColonDimensions.put(axis[a], dimensionTemporal);
				}
		    }
//			log.debug(srsDescription);
			
			CollectionExtent extent = new CollectionExtent();
			CollectionSpatialExtent spatialExtent = new CollectionSpatialExtent();
			List<List<BigDecimal>> bbox = new ArrayList<List<BigDecimal>>();
			List<BigDecimal> bbox1 = new ArrayList<BigDecimal>();
			CollectionTemporalExtent temporalExtent = new CollectionTemporalExtent();
			List<List<OffsetDateTime>> interval = new ArrayList<List<OffsetDateTime>>();
			List<OffsetDateTime> interval1 = new ArrayList<OffsetDateTime>();
			bbox1.add(new BigDecimal(c1[1]));
			bbox1.add(new BigDecimal(c1[0]));
			bbox1.add(new BigDecimal(c2[1]));
			bbox1.add(new BigDecimal(c2[0]));
			bbox.add(bbox1);
			spatialExtent.setBbox(bbox);
			extent.setSpatial(spatialExtent);			
			
//			int k = 0;
//			for(int a = 0; a < axis.length; a++) {
//		    	log.debug(axis[a]);
//				String timeAxis = axis[a].toUpperCase();
//				if(timeAxis.equals("DATE") || timeAxis.equals("TIME") || timeAxis.equals("ANSI") || timeAxis.equals("UNIX"))
//				{
//					k = a;
//					break;
//				}
//			}
			
			try {
				interval1.add(OffsetDateTime.parse(startTime));
				interval1.add(OffsetDateTime.parse(endTime));
				interval.add(interval1);
				temporalExtent.setInterval(interval);
				extent.setTemporal(temporalExtent);
			}catch(Exception e) {
			}
			currentCollection.setExtent(extent);
			}
			
			else {
				srsDescription = "0";				
				
				String[] minValues = boundingBoxElement.getChildText("lowerCorner", gmlNS).split(" ");
				String[] maxValues = boundingBoxElement.getChildText("upperCorner", gmlNS).split(" ");							    
			    String[] axis = boundingBoxElement.getAttribute("axisLabels").getValue().split(" ");
			    int xIndex = 0;
			    int yIndex = 0;
				
			    Map<String, Dimension> cubeColonDimensions = new HashMap<String, Dimension>();
				DimensionBands dimensionbands = new DimensionBands();
				dimensionbands.setType(TypeEnum.BANDS);
//				log.debug("number of bands found: " + bandsListSwe.size());			
				DimensionSpatial dimensionXspatial = new DimensionSpatial();
				dimensionXspatial.setType(TypeEnum.SPATIAL);
				DimensionSpatial dimensionYspatial = new DimensionSpatial();
				dimensionYspatial.setType(TypeEnum.SPATIAL);
				DimensionTemporal dimensionTemporal = new DimensionTemporal();
				dimensionTemporal.setType(TypeEnum.TEMPORAL);
				
				List<Element> bandsList = null;
				List<Element> bandsListSwe = null;
				Boolean bandsMeta = false;
				try {
					bandsList = metadataElement.getChild("bands", gmlNS).getChildren();
					bandsMeta = true;
				}catch(Exception e) {
					//			log.warn("Error in parsing bands :" + e.getMessage());
				}
				try {
					bandsListSwe = rootNode.getChild("CoverageDescription", defaultNS).getChild("rangeType", gmlNS).getChild("DataRecord", sweNS).getChildren("field", sweNS);
				}catch(Exception e) {
					//					log.warn("Error in parsing bands List :" + e.getMessage());
				}
				if (bandsMeta) {
					try {
						for(int c = 0; c < bandsList.size(); c++) {
							String bandWave = null;
							String bandCommonName = null;
							String bandGSD = null;
							Element band = bandsList.get(c);
							String bandId = band.getName();
							dimensionbands.addValuesItem(bandId);
							try {
								bandWave = band.getChildText("WAVELENGTH");
							}catch(Exception e) {
//								log.warn("Error in parsing band wave-lenght:" + e.getMessage());
							}
							try {
								bandCommonName = band.getChildText("common_name");
							}catch(Exception e) {
//								log.warn("Error in parsing band common name:" + e.getMessage());
							}
							try {
								bandGSD = band.getChildText("gsd");
							}catch(Exception e) {
//								log.warn("Error in parsing band gsd:" + e.getMessage());
							}
						}
					}catch(Exception e) {
//						log.warn("Error in parsing bands :" + e.getMessage());
					}
				}
				else {
					for(int c = 0; c < bandsListSwe.size(); c++) {
						String bandId = bandsListSwe.get(c).getAttributeValue("name");
						dimensionbands.addValuesItem(bandId);
					}
				}
				cubeColonDimensions.put("bands", dimensionbands);
				currentCollection.setCubeColonDimensions(cubeColonDimensions);
				int j = 0;
				
				for(int a = 0; a < axis.length; a++) {
//			    	log.debug(axis[a]);
					if(axis[a].equals("i") || axis[a].equals("j")){
						j = a;
						break;
					}
				}
//				log.debug(j);
				String startTime = null;
				String endTime = null;
				
				for(int a = 0; a < axis.length; a++) {
//			    	log.debug(axis[a]);
					if(axis[a].equals("i")){
						xIndex=a;
						dimensionXspatial.setReferenceSystem(srsDescription);
						dimensionXspatial.setAxis(AxisEnum.X);
						List<BigDecimal> xExtent = new ArrayList<BigDecimal>();
						xExtent.add(0, new BigDecimal(minValues[j]));
						xExtent.add(1, new BigDecimal(maxValues[j]));
						dimensionXspatial.setExtent(xExtent);
						cubeColonDimensions.put(axis[a], dimensionXspatial);
					}
					if(axis[a].equals("j")){
						yIndex=a;
						dimensionYspatial.setReferenceSystem(srsDescription);
						dimensionYspatial.setAxis(AxisEnum.Y);
						List<BigDecimal> yExtent = new ArrayList<BigDecimal>();
						yExtent.add(0, new BigDecimal(minValues[j+1]));
						yExtent.add(1, new BigDecimal(maxValues[j+1]));
						dimensionYspatial.setExtent(yExtent);
						cubeColonDimensions.put(axis[a], dimensionYspatial);
					}
					if(axis[a].equals("DATE")  || axis[a].equals("TIME") || axis[a].equals("ANSI") || axis[a].equals("Time") || axis[a].equals("Date") || axis[a].equals("time") || axis[a].equals("ansi") || axis[a].equals("date") || axis[a].equals("unix")){
						List<String> temporalExtent = new ArrayList<String>();
						temporalExtent.add(0, minValues[a].replaceAll("\"", ""));
						temporalExtent.add(1, maxValues[a].replaceAll("\"", ""));
						startTime = minValues[a].replaceAll("\"", "");
						endTime = maxValues[a].replaceAll("\"", "");
						dimensionTemporal.setExtent(temporalExtent);
						String[] taxis = null;
						try {
							List<Element> tList = rootNode.getChild("CoverageDescription", defaultNS).getChild("domainSet", gmlNS).getChild("RectifiedGrid", gmlNS).getChildren("offsetVector", gmlNS);
							taxis = tList.get(a).getValue().split(" ");
							dimensionTemporal.setStep(JsonNullable.of(taxis[0]));
					    }catch(Exception e) {
					    	log.warn("Irregular Axis :" + e.getMessage());
					    	dimensionTemporal.setStep(JsonNullable.of(null));
					    }						
						cubeColonDimensions.put(axis[a], dimensionTemporal);
					}
			    }
//				log.debug(srsDescription);
				
				CollectionExtent extent = new CollectionExtent();
				CollectionSpatialExtent spatialExtent = new CollectionSpatialExtent();
				List<List<BigDecimal>> bbox = new ArrayList<List<BigDecimal>>();
				List<BigDecimal> bbox1 = new ArrayList<BigDecimal>();
				CollectionTemporalExtent temporalExtent = new CollectionTemporalExtent();
				List<List<OffsetDateTime>> interval = new ArrayList<List<OffsetDateTime>>();
				List<OffsetDateTime> interval1 = new ArrayList<OffsetDateTime>();
				bbox1.add(new BigDecimal(minValues[j]));
				bbox1.add(new BigDecimal(minValues[j+1]));
				bbox1.add(new BigDecimal(maxValues[j]));
				bbox1.add(new BigDecimal(maxValues[j+1]));
				bbox.add(bbox1);
				spatialExtent.setBbox(bbox);
				extent.setSpatial(spatialExtent);
				
//				int k = 0;
//				for(int a = 0; a < axis.length; a++) {
//			    	log.debug(axis[a]);
//					String timeAxis = axis[a].toUpperCase();
//					if(timeAxis.equals("DATE") || timeAxis.equals("TIME") || timeAxis.equals("ANSI") || timeAxis.equals("UNIX"))
//					{
//						k = a;
//						break;
//					}
//				}
				
				try {							
					interval1.add(OffsetDateTime.parse(startTime));
					interval1.add(OffsetDateTime.parse(endTime));
					interval.add(interval1);
					temporalExtent.setInterval(interval);
					extent.setTemporal(temporalExtent);
				}catch(Exception e) {
				}
				currentCollection.setExtent(extent);
			}			
			
			Link link1 = new Link();
			List<Link> links = new ArrayList<Link>();
			link1.setRel("license");
			link1.setType("text/html");
			try {
				link1.setHref(new URI ("https://creativecommons.org/licenses/by/4.0/"));
			} catch (URISyntaxException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			links.add(0, link1);
			currentCollection.setLinks(links);
			
			String title = null;
			String description = null;
			try {
			title = metadataElement.getChildText("Title", gmlNS);
			currentCollection.setTitle(title);
		    }catch(Exception e) {
//		    	log.warn("Error in parsing Project Name :" + e.getMessage());
		    }
		    try {
			description = metadataElement.getChildText("Description", gmlNS);
			currentCollection.setDescription(description);
            }catch(Exception e) {
//            	log.warn("Error in parsing Title :" + e.getMessage());
	        }
		       
			
//			JSONArray links = new JSONArray();			
//			JSONObject linkSelf = new JSONObject();
//			linkSelf.put("href", ConvenienceHelper.readProperties("openeo-endpoint") + "/collections/" + collectionId);
//			linkSelf.put("rel", "self");
//			
//			JSONObject linkAlternate = new JSONObject();
//			linkAlternate.put("href", ConvenienceHelper.readProperties("wcps-endpoint") + "?SERVICE=WCS&VERSION=2.0.1&REQUEST=DescribeCoverage&COVERAGEID=" + collectionId);
//			linkAlternate.put("rel", "alternate");
//			
//			JSONObject linkLicense = new JSONObject();
//			linkLicense.put("href", "https://creativecommons.org/licenses/by/4.0/");
//			linkLicense.put("rel", "license");
//			
//			JSONObject linkAbout = new JSONObject();
//			linkAbout.put("href", ConvenienceHelper.readProperties("wcps-endpoint") + "?SERVICE=WCS&VERSION=2.0.1&REQUEST=DescribeCoverage&COVERAGEID=" + collectionId);
//			linkAbout.put("title", ConvenienceHelper.readProperties("wcps-endpoint") + "?SERVICE=WCS&VERSION=2.0.1&REQUEST=DescribeCoverage&COVERAGEID=" + collectionId);
//			linkAbout.put("rel", "about");
//			
//			links.put(linkSelf);
//			links.put(linkAlternate);
//			links.put(linkLicense);
//			links.put(linkAbout);
		
			//String keyword1 = metadataObj.getString("Project");
			
			//String providerName = metadataObj.getString("Creator");		
			
			//String role1 = metadataObj.getString("Roles");
			
			
//			JSONArray provider1 = new JSONArray();
//			JSONObject provider1Info = new JSONObject();
//			provider1Info.put("name", "Eurac Research - Institute for Earth Observation");
//			provider1Info.put("roles", roles1);
//			provider1Info.put("url", "http://www.eurac.edu");
//			provider1.put(provider1Info);			
//			
//			JSONObject properties = new JSONObject();
//			JSONObject other_properties = new JSONObject();
//			
//		
//			JSONArray epsg_values = new JSONArray();
//			epsg_values.put(Double.parseDouble(srsDescription));
//			
//			epsgvalues.put("values", epsg_values);
//			
//			JSONArray platform_values = new JSONArray();
//			platform_values.put("Sentinel-2A");
//			platform_values.put("Sentinel-2B");
//			pltfrmvalues.put("values", platform_values);			
		    
//			JSONArray cloud_cover = new JSONArray();
//			JSONObject cloud_cover_extent = new JSONObject();			
//			JSONObject cube_dimensions = new JSONObject();
			
//			try {
//			for(JSONObject dim: dimObjects) {
//				cube_dimensions.put(dim.getString("axis"), dim);
//			}
//			}catch(Exception e) {
//				log.warn("Error in parsing Band Values :" + e.getMessage());
//			}
//			
//			List<Element> slices = null;
//			try {
//				slices = metadataElement.getChild("slices", gmlNS).getChildren();
//		    }catch(Exception e) {
//		    	log.warn("Error in parsing metadata slices:" + e.getMessage());
//		    }
//			
//			Element props = null;
//			try {
//				props = slices.get(0);
//		    }catch(Exception e) {
//		    	log.warn("Error in parsing metadata slice :" + e.getMessage());
//		    }
//			try {
//				properties.put("sci:citation", props.getChildText("CITATION"));
//			}catch(Exception e) {
//				log.warn("Error in parsing Constellation:" + e.getMessage());
//			}
//
//			try {
//				properties.put("eo:constellation", props.getChildText("CONSTELLATON"));
//			}catch(Exception e) {
//				log.warn("Error in parsing Constellation:" + e.getMessage());
//			}				
//
//			try {				
//				properties.put("eo:instrument", props.getChildText("INSTRUMENT"));
//			}catch(Exception e) {
//				log.warn("Error in parsing Instrument:" + e.getMessage());
//			}
//
//			JSONArray cloudCovArray = new JSONArray();
//			
//			try {
//			for(int c = 0; c < slices.size(); c++) {
//				try {					
//					double cloudCov = Double.parseDouble(slices.get(c).getChildText("CLOUD_COVERAGE_ASSESSMENT"));
//					cloudCovArray.put(cloudCov);					
//				}catch(Exception e) {
////					log.warn("Error in parsing Cloud Coverage:" + e.getMessage());
//				}				
//			}
//		    }catch(Exception e) {
////		    	log.warn("Error in parsing metadata slice :" + e.getMessage());
//		    }
//			
//			double maxCCValue = 0;
//			double minCCValue = 0;	
//			Boolean cloudCoverFlag = false;
//			try {
//				maxCCValue = cloudCovArray.getDouble(0);
//				minCCValue = cloudCovArray.getDouble(0);
//				cloudCoverFlag = true;
//		    }catch(Exception e) {
////		    	log.warn("Error in parsing cloud cover Extents :" + e.getMessage());
//		    }
//			
//			try {
//				for(int i=1;i < cloudCovArray.length();i++){
//					if(cloudCovArray.getDouble(i) > maxCCValue){
//						maxCCValue = cloudCovArray.getDouble(i); 
//					}
//					if(cloudCovArray.getDouble(i) < minCCValue){
//						minCCValue = cloudCovArray.getDouble(i);
//					}
//				}
//		    }catch(Exception e) {
////		    	log.error("Error in parsing cloud cover array :" + e.getMessage());
//		    }
//			
//			cloud_cover.put(minCCValue);
//			cloud_cover.put(maxCCValue);
//			cloud_cover_extent.put("extent", cloud_cover);
//			if (cloudCoverFlag) {
//			other_properties.put("eo:cloud_cover", cloud_cover_extent);
//			}
//			
//			properties.put("cube:dimensions", cube_dimensions);
//			properties.put("eo:epsg", Double.parseDouble(srsDescription));			
//			properties.put("eo:bands", bandArray);
//						
//			other_properties.put("eo:platform", pltfrmvalues);
//			other_properties.put("eo:epsg", epsgvalues);			

			return  new ResponseEntity<Collection>(currentCollection, HttpStatus.OK);		
            }
    
    	catch (MalformedURLException e) {
//			log.error("An error occured while describing coverage from WCPS endpoint: " + e.getMessage());
//			StringBuilder builder = new StringBuilder();
//			for( StackTraceElement element: e.getStackTrace()) {
//				builder.append(element.toString()+"\n");
//			}
//			log.error(builder.toString());
//			return Response.serverError()
//					.entity(new ApiResponseMessage(ApiResponseMessage.ERROR,
//							"An error occured while describing coverage from WCPS endpoint: " + e.getMessage()))
//					.build();
    		return  new ResponseEntity<Collection>(currentCollection, HttpStatus.BAD_REQUEST);
		} 
    	catch (IOException e) {
//			log.error("An error occured while describing coverage from WCPS endpoint: " + e.getMessage());
//			StringBuilder builder = new StringBuilder();
//			for( StackTraceElement element: e.getStackTrace()) {
//				builder.append(element.toString()+"\n");
//			}
//			log.error(builder.toString());
//			return Response.serverError()
//					.entity(new ApiResponseMessage(ApiResponseMessage.ERROR,
//							"An error occured while describing coverage from WCPS endpoint: " + e.getMessage()))
//					.build();
		    return  new ResponseEntity<Collection>(currentCollection, HttpStatus.BAD_REQUEST);
    		}
    	    
    	catch (JDOMException e) {
//			log.error("An error occured while requesting capabilities from WCPS endpoint: " + e.getMessage());
//			StringBuilder builder = new StringBuilder();
//			for( StackTraceElement element: e.getStackTrace()) {
//				builder.append(element.toString()+"\n");
//			}
//			log.error(builder.toString());
//			return Response.serverError()
//					.entity(new ApiResponseMessage(ApiResponseMessage.ERROR,
//							"An error occured while requesting capabilities from WCPS endpoint: " + e.getMessage())).build();
    		return  new ResponseEntity<Collection>(currentCollection, HttpStatus.BAD_REQUEST);
    	}

    }
    
}
