package org.openeo.spring.api;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.validation.constraints.Pattern;

import org.gdal.osr.CoordinateTransformation;
import org.gdal.osr.SpatialReference;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.Namespace;
import org.jdom2.input.SAXBuilder;
import org.openeo.spring.model.Collection;
import org.openeo.spring.model.CollectionExtent;
import org.openeo.spring.model.CollectionSpatialExtent;
import org.openeo.spring.model.CollectionSummaryStats;
import org.openeo.spring.model.CollectionTemporalExtent;
import org.openeo.spring.model.Dimension;
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
    	    	
    	URL url;
    	Collection currentCollection = new Collection();
    	
    	try {    		
    		currentCollection.setId(collectionId);
    		currentCollection.setStacVersion("0.9.0");
			url = new URL("http://saocompute.eurac.edu/rasdaman/ows" + "?SERVICE=WCS&VERSION=2.0.1&REQUEST=DescribeCoverage&COVERAGEID=" + collectionId);

			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			SAXBuilder builder = new SAXBuilder();
			Document capabilititesDoc = (Document) builder.build(conn.getInputStream());
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
			
			List<Element> bandsList = null;
			Boolean bandsMeta = false;
			try {
			bandsList = metadataElement.getChild("bands", gmlNS).getChildren();
			bandsMeta = true;
		    }catch(Exception e) {
//			log.warn("Error in parsing bands :" + e.getMessage());
		    }
			List<Element> bandsListSwe = rootNode.getChild("CoverageDescription", defaultNS).getChild("rangeType", gmlNS).getChild("DataRecord", sweNS).getChildren("field", sweNS);
			
			//metadataObj = new JSONObject(metadataString1);
			//String metadataString2 = metadataString1.replaceAll("\\n","");
			//String metadataString3 = metadataString2.replaceAll("\"\"","\"");
			//metadataObj = new JSONObject(metadataString3);
			//JSONArray slices = metadataObj.getJSONArray("slices");
			
			String srsDescription = boundingBoxElement.getAttributeValue("srsName");
			try {
				srsDescription = srsDescription.substring(srsDescription.indexOf("EPSG"), srsDescription.indexOf("&")).replace("/0/", ":");
				srsDescription = srsDescription.replaceAll("EPSG:","");
				
			}catch(StringIndexOutOfBoundsException e) {
				srsDescription = srsDescription.substring(srsDescription.indexOf("EPSG")).replace("/0/", ":");
				srsDescription = srsDescription.replaceAll("EPSG:","");							
			}			
			
//          JSONObject extentCollection = new JSONObject();			
//			JSONArray spatialExtent = new JSONArray();
//			JSONArray temporalExtent =  new JSONArray();
			
			SpatialReference src = new SpatialReference();
			src.ImportFromEPSG(Integer.parseInt(srsDescription));

			SpatialReference dst = new SpatialReference();
			dst.ImportFromEPSG(4326);
			
			String[] minValues = boundingBoxElement.getChildText("lowerCorner", gmlNS).split(" ");
			String[] maxValues = boundingBoxElement.getChildText("upperCorner", gmlNS).split(" ");			
			
			CoordinateTransformation tx = new CoordinateTransformation(src, dst);
		    
		    String[] axis = boundingBoxElement.getAttribute("axisLabels").getValue().split(" ");
//		    int xIndex = 0;
//		    int yIndex = 0;
//		    JSONObject[] dimObjects = new JSONObject[axis.length+1];
		    
//          JSONArray bandArray = new JSONArray();
					    
//			dimObjects[0] = new JSONObject();
//			dimObjects[0].put("type", "bands");
//			dimObjects[0].put("axis", "spectral");
//			JSONArray bandValues = new JSONArray();
//			log.debug("number of bands found: " + bandsListSwe.size());
//			if (bandsMeta) {
//				try {
//					for(int c = 0; c < bandsList.size(); c++) {
////						JSONObject product = new JSONObject();
//						String bandWave = null;
//						Element band = bandsList.get(c);
//						String bandId = band.getName();
//
//						try {
//							bandWave = band.getChildText("WAVELENGTH");
//						}catch(Exception e) {
////							log.warn("Error in parsing band wave-lenght:" + e.getMessage());
//						}
//						try {
//							product.put("common_name", band.getChildText("common_name"));
//						}catch(Exception e) {
//							log.warn("Error in parsing band common name:" + e.getMessage());
//						}				
//						product.put("name", bandId);
//						product.put("center_wavelength", bandWave);
//						bandValues.put(bandId);
//						try {
//							product.put("gsd", band.getChildText("gsd"));
//						}catch(Exception e) {
//							log.warn("Error in parsing band gsd:" + e.getMessage());
//						}
//						bandArray.put(product);
//					}
//				}catch(Exception e) {
//					log.warn("Error in parsing bands :" + e.getMessage());
//				}
//			}
//			else {
//				for(int c = 0; c < bandsListSwe.size(); c++) {
//					JSONObject product = new JSONObject();
//					String bandId = bandsListSwe.get(c).getAttributeValue("name");					
//					product.put("name", bandId);					
//					bandValues.put(bandId);					
//					bandArray.put(product);
//				}
//			}
			
//			try {
//				dimObjects[0].put("values", bandValues);
//		    }catch(Exception e) {
//		    	log.warn("Error in Band values :" + e.getMessage());
//		    }
		    
//		    for(int a = 0; a < axis.length; a++) {
//		    	log.debug(axis[a]);
//				if(axis[a].equals("E") || axis[a].equals("X") || axis[a].equals("Long")){
//					xIndex=a;
//					dimObjects[1] = new JSONObject();
//					dimObjects[1].put("axis", axis[a]);
//					dimObjects[1].put("type", "spatial");
//					dimObjects[1].put("reference_system", Long.parseLong(srsDescription));
//				}
//				if(axis[a].equals("N") || axis[a].equals("Y") || axis[a].equals("Lat")){
//					yIndex=a;
//					dimObjects[2] = new JSONObject();
//					dimObjects[2].put("axis", axis[a]);
//					dimObjects[2].put("type", "spatial");
//					dimObjects[2].put("reference_system", Long.parseLong(srsDescription));
//				}
//				if(axis[a].equals("DATE")  || axis[a].equals("TIME") || axis[a].equals("ANSI") || axis[a].equals("Time") || axis[a].equals("Date") || axis[a].equals("time") || axis[a].equals("ansi") || axis[a].equals("date") || axis[a].equals("unix")){
//					temporalExtent.put(minValues[a].replaceAll("\"", ""));
//					temporalExtent.put(maxValues[a].replaceAll("\"", ""));
//					dimObjects[3] = new JSONObject();
//					dimObjects[3].put("axis", axis[a]);
//					dimObjects[3].put("type", "temporal");
//					dimObjects[3].put("extent", temporalExtent);
//					dimObjects[3].put("step", JSONObject.NULL);
//				}
//		    }
		    
//			log.debug(srsDescription);
			
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
//			spatialExtent.put(c1[1]);
//			spatialExtent.put(c1[0]);
//			spatialExtent.put(c2[1]);
//			spatialExtent.put(c2[0]);
			
//			JSONArray xExtent = new JSONArray();
//			xExtent.put(c1[1]);
//			xExtent.put(c2[1]);
//			dimObjects[1].put("extent", xExtent);
//			JSONArray yExtent = new JSONArray();
//			yExtent.put(c1[0]);
//			yExtent.put(c2[0]);
//			dimObjects[2].put("extent", yExtent);
			
			CollectionExtent extent = new CollectionExtent();
			CollectionSpatialExtent spatialExtent = new CollectionSpatialExtent();
			List<List<BigDecimal>> bbox = new ArrayList<List<BigDecimal>>();
			List<BigDecimal> bbox1 = new ArrayList<BigDecimal>();
			
			bbox1.add(new BigDecimal(c1[1]));
			bbox1.add(new BigDecimal(c1[0]));
			bbox1.add(new BigDecimal(c2[1]));
			bbox1.add(new BigDecimal(c2[0]));
			bbox.add(bbox1);
			spatialExtent.setBbox(bbox);
			extent.setSpatial(spatialExtent);			
			
//			
			int k = 0;
			for(int a = 0; a < axis.length; a++) {
//		    	log.debug(axis[a]);
				String timeAxis = axis[a].toUpperCase();
				if(timeAxis.equals("DATE") || timeAxis.equals("TIME") || timeAxis.equals("ANSI") || timeAxis.equals("UNIX"))
				{
					k = a;
					break;
				}
			}
			
			String startTime = minValues[k].replaceAll("\"", "");
			String endTime = maxValues[k].replaceAll("\"", "");			
			
			CollectionTemporalExtent temporalExtent = new CollectionTemporalExtent();
			List<List<OffsetDateTime>> interval = new ArrayList<List<OffsetDateTime>>();
			List<OffsetDateTime> interval1 = new ArrayList<OffsetDateTime>();			
			
			interval1.add(OffsetDateTime.parse(startTime));
			interval1.add(OffsetDateTime.parse(endTime));
			interval.add(interval1);
			temporalExtent.setInterval(interval);
			extent.setTemporal(temporalExtent);
			
			currentCollection.setExtent(extent);
			
//			JSONArray links = new JSONArray();
//			
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
			
			String title = null;
			String description = null;
			try {
			title = metadataElement.getChildText("Project", gmlNS);
			currentCollection.setTitle(title);
		    }catch(Exception e) {
//		    	log.warn("Error in parsing Project Name :" + e.getMessage());
		    }
		    try {
			description = metadataElement.getChildText("Title", gmlNS);
			currentCollection.setDescription(description);
            }catch(Exception e) {
//            	log.warn("Error in parsing Title :" + e.getMessage());
	        }
		    
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
//								
////			JSONObject coverage = new JSONObject();
//			
//			coverage.put("stac_version", "0.6.2");
//			coverage.put("id", collectionId);
//			coverage.put("title", title);
//			coverage.put("description", description);
//			coverage.put("license", "CC-BY-4.0");
//			coverage.put("keywords", keywords);
//			coverage.put("providers", provider1);
//			coverage.put("links", links);
//			extentCollection.put("spatial", spatialExtent);
//			extentCollection.put("temporal", temporalExtent);
//			coverage.put("extent", extentCollection);
//			coverage.put("properties", properties);
//			coverage.put("other_properties", other_properties);

			return  new ResponseEntity<Collection>(currentCollection, HttpStatus.OK);
		
    }
    
//    	catch (MalformedURLException e) {
////			log.error("An error occured while describing coverage from WCPS endpoint: " + e.getMessage());
//			StringBuilder builder = new StringBuilder();
//			for( StackTraceElement element: e.getStackTrace()) {
//				builder.append(element.toString()+"\n");
//			}
////			log.error(builder.toString());
//			return Response.serverError()
//					.entity(new ApiResponseMessage(ApiResponseMessage.ERROR,
//							"An error occured while describing coverage from WCPS endpoint: " + e.getMessage()))
//					.build();
//		} 
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
