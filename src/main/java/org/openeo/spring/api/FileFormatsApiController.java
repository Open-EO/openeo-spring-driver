package org.openeo.spring.api;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.openeo.spring.model.FileFormat;
import org.openeo.spring.model.FileFormats;
import org.openeo.spring.model.Link;
import org.openeo.spring.model.Process;
import org.openeo.spring.model.Processes;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.NativeWebRequest;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2020-07-02T08:45:00.334+02:00[Europe/Rome]")
@RestController
@RequestMapping("${openapi.openEO.base-path:}")
public class FileFormatsApiController implements FileFormatsApi {
    private final NativeWebRequest request;
    
    private Map<String, FileFormat> fileFormats = null;	
	private ObjectMapper mapper = null;
	@Value("classpath:output_formats_endpoint.json")
	Resource fileFormatsFile;

    @org.springframework.beans.factory.annotation.Autowired
    public FileFormatsApiController(NativeWebRequest request) {
        this.request = request;
        
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
    	InputStream stream = classLoader.getResourceAsStream("output_formats_endpoint.json");
    	
    	this.mapper = new ObjectMapper();
    	this.fileFormats = new HashMap<String, FileFormat>();
    	
    	try {
    		FileFormat[] formatsArray = this.mapper.readValue(stream, FileFormat[].class);
			
			for(int p = 0; p < formatsArray.length; p++) {
				this.fileFormats.put(formatsArray[p].getTitle(), formatsArray[p]);				
//				log.debug("Found and stored process: " + processArray[p].getId());
			}			
		} catch (JsonParseException e) {
//			log.error("Error parsing json: " + e.getMessage());
		} catch (JsonMappingException e) {
//			log.error("Error mapping json to java: " + e.getMessage());
		} catch (IOException e) {
//			log.error("Error reading json file: " + e.getMessage());
		}
    }

    @Override
    public Optional<NativeWebRequest> getRequest() {
        return Optional.ofNullable(request);
    }
    
    /**
     * GET /file_formats : Supported file formats
     * The request will ask the back-end for supported input and output file formats. *Input* file formats specify which file a back-end can *read* from. *Output* file formats specify which file a back-end can *write* to.  The response to this request is an object listing all available input and output file formats separately with their parameters and additional data. This endpoint does not include the supported secondary web services.  **Note**: Format names and parameters MUST be fully aligned with the GDAL codes if available, see [GDAL Raster Formats](http://www.gdal.org/formats_list.html) and [OGR Vector Formats](http://www.gdal.org/ogr_formats.html). It is OPTIONAL to support all output format parameters supported by GDAL. Some file formats not available through GDAL may be defined centrally for openEO. Custom file formats or parameters MAY be defined.  The format descriptions must describe how the file formats relate to  data cubes. Input file formats must describe how the files have to be structured to be transformed into data cubes. Output file formats must describe how the data cubes are stored at the back-end and how the  resulting file structure looks like.  Back-ends MUST NOT support aliases, for example it is not allowed to support &#x60;geotiff&#x60; instead of &#x60;gtiff&#x60;. Nevertheless, openEO Clients MAY translate user input input for convenience (e.g. translate &#x60;geotiff&#x60; to &#x60;gtiff&#x60;). Also, for a better user experience the back-end can specify a &#x60;title&#x60;.  Format names are allowed to be *case insensitive* throughout the API.
     *
     * @return An object with containing all input and output format separately.  For each property &#x60;input&#x60; and &#x60;output&#x60; an object is defined where the  file format names are the property keys and the property values are objects that define a title, supported parameters and related links. (status code 200)
     *         or The request can&#39;t be fulfilled due to an error on client-side, i.e. the request is invalid. The client should not repeat the request without modifications.  The response body SHOULD contain a JSON error object. MUST be any HTTP status code specified in [RFC 7231](https://tools.ietf.org/html/rfc7231#section-6.6). This request usually does not respond with HTTP status codes 401 and 403 due to missing authorization. HTTP status code 404 should be used if the value of a path parameter is invalid.  See also: * [Error Handling](#section/API-Principles/Error-Handling) in the API in general. * [Common Error Codes](errors.json) (status code 400)
     *         or The request can&#39;t be fulfilled due to an error at the back-end. The error is never the client’s fault and therefore it is reasonable for the client to retry the exact same request that triggered this response.  The response body SHOULD contain a JSON error object. MUST be any HTTP status code specified in [RFC 7231](https://tools.ietf.org/html/rfc7231#section-6.6).  See also: * [Error Handling](#section/API-Principles/Error-Handling) in the API in general. * [Common Error Codes](errors.json) (status code 500)
     */
    @Override
    @Operation(summary = "Supported file formats", operationId = "listFileTypes", description = "The request will ask the back-end for supported input and output file formats. *Input* file formats specify which file a back-end can *read* from. *Output* file formats specify which file a back-end can *write* to.  The response to this request is an object listing all available input and output file formats separately with their parameters and additional data. This endpoint does not include the supported secondary web services.  **Note**: Format names and parameters MUST be fully aligned with the GDAL codes if available, see [GDAL Raster Formats](http://www.gdal.org/formats_list.html) and [OGR Vector Formats](http://www.gdal.org/ogr_formats.html). It is OPTIONAL to support all output format parameters supported by GDAL. Some file formats not available through GDAL may be defined centrally for openEO. Custom file formats or parameters MAY be defined.  The format descriptions must describe how the file formats relate to  data cubes. Input file formats must describe how the files have to be structured to be transformed into data cubes. Output file formats must describe how the data cubes are stored at the back-end and how the  resulting file structure looks like.  Back-ends MUST NOT support aliases, for example it is not allowed to support `geotiff` instead of `gtiff`. Nevertheless, openEO Clients MAY translate user input input for convenience (e.g. translate `geotiff` to `gtiff`). Also, for a better user experience the back-end can specify a `title`.  Format names are allowed to be *case insensitive* throughout the API."
    		, tags={ "Capabilities","Data Processing", })
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "An object with containing all input and output format separately.  For each property `input` and `output` an object is defined where the  file format names are the property keys and the property values are objects that define a title, supported parameters and related links."),
        @ApiResponse(responseCode = "400", description = "The request can't be fulfilled due to an error on client-side, i.e. the request is invalid. The client should not repeat the request without modifications.  The response body SHOULD contain a JSON error object. MUST be any HTTP status code specified in [RFC 7231](https://tools.ietf.org/html/rfc7231#section-6.6). This request usually does not respond with HTTP status codes 401 and 403 due to missing authorization. HTTP status code 404 should be used if the value of a path parameter is invalid.  See also: * [Error Handling](#section/API-Principles/Error-Handling) in the API in general. * [Common Error Codes](errors.json)"),
        @ApiResponse(responseCode = "500", description = "The request can't be fulfilled due to an error at the back-end. The error is never the client’s fault and therefore it is reasonable for the client to retry the exact same request that triggered this response.  The response body SHOULD contain a JSON error object. MUST be any HTTP status code specified in [RFC 7231](https://tools.ietf.org/html/rfc7231#section-6.6).  See also: * [Error Handling](#section/API-Principles/Error-Handling) in the API in general. * [Common Error Codes](errors.json)") })
    @GetMapping(value = "/file_formats", produces = { "application/json" })
    
    public ResponseEntity<FileFormats> listFileTypes() {
    	ObjectMapper mapper = new ObjectMapper();
    	FileFormats fileFormatsList = null;
    	
		try {
			fileFormatsList = mapper.readValue(fileFormatsFile.getFile(), FileFormats.class);
		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return new ResponseEntity<FileFormats>(fileFormatsList, HttpStatus.OK);
    }
}
