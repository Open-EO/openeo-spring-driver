package org.openeo.spring.api;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import org.openeo.spring.model.Processes;
import org.openeo.spring.model.Process;
import org.openeo.spring.model.ProcessParameter;
import org.openeo.spring.model.ProcessReturnValue;
import org.openeo.spring.model.DataTypeSchema;
import org.openeo.spring.model.EngineTypes;
import org.openeo.spring.model.Link;
import org.openeo.spring.model.ParameterSchema;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.NativeWebRequest;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2020-07-02T08:45:00.334+02:00[Europe/Rome]")
@RestController
@RequestMapping("${openapi.openEO.base-path:}")
public class ProcessesApiController implements ProcessesApi {
    private final NativeWebRequest request;    
    private Map<String, Process> processes = null;
	private Map<String, Link> links = null;
	private ObjectMapper mapper = null;
//	("classpath:processes.json")
	@Value("${org.openeo.wcps.processes.list}")
	Resource processesFileWCPS;
	@Value("${org.openeo.odc.processes.list}")
	Resource processesFileODC;
	
//	
	@Autowired
	ResourceLoader resourceLoader;
	
    @org.springframework.beans.factory.annotation.Autowired
    public ProcessesApiController(NativeWebRequest request) {
        this.request = request;
        
//        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
//        new ClassPathResource("processes.json").getInputStream();
//        Resource processResource = resourceLoader.getResource("classpath:processes.json");
//        Resource linksResource = resourceLoader.getResource("classpath:links.json");
        
//    	InputStream stream = processResource.getInputStream();
//    	InputStream linkstream = linksResource.getInputStream();
    	
//    	InputStream stream = classLoader.getResourceAsStream("processes.json");
//    	InputStream linkstream = classLoader.getResourceAsStream("links.json");
    	
//    	this.mapper = new ObjectMapper();
//    	this.processes = new HashMap<String, Process>();
//    	this.links = new HashMap<String, Link>();
//    	
//    	try {
//			Process[] processArray = this.mapper.readValue(stream, Process[].class);
//			Link[] linksArray = this.mapper.readValue(linkstream, Link[].class);
//			
//			for(int p = 0; p < linksArray.length; p++) {
//				this.links.put(linksArray[p].getRel(), linksArray[p]);				
////				log.debug("Found and stored process: " + linksArray[p].getRel());
//			}
//			
//			for(int p = 0; p < processArray.length; p++) {
//				this.processes.put(processArray[p].getId(), processArray[p]);				
////				log.debug("Found and stored process: " + processArray[p].getId());
//			}			
//		} catch (JsonParseException e) {
////			log.error("Error parsing json: " + e.getMessage());
//		} catch (JsonMappingException e) {
////			log.error("Error mapping json to java: " + e.getMessage());
//		} catch (IOException e) {
////			log.error("Error reading json file: " + e.getMessage());
//		}
    }

    @Override
    public Optional<NativeWebRequest> getRequest() {
        return Optional.ofNullable(request);
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
    @Override
	@Operation(summary = "Supported predefined processes", operationId = "listProcesses", description = "The request asks the back-end for available predefined processes and returns detailed process descriptions, including parameters and return values.", tags={ "Process Discovery", })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "Formal specification describing the supported predefined processes."),
        @ApiResponse(responseCode = "400", description = "The request can't be fulfilled due to an error on client-side, i.e. the request is invalid. The client should not repeat the request without modifications.  The response body SHOULD contain a JSON error object. MUST be any HTTP status code specified in [RFC 7231](https://tools.ietf.org/html/rfc7231#section-6.6). This request MUST respond with HTTP status codes 401 if authorization is required or 403 if the authorization failed or access is forbidden in general to the authenticated user. HTTP status code 404 should be used if the value of a path parameter is invalid.  See also: * [Error Handling](#section/API-Principles/Error-Handling) in the API in general. * [Common Error Codes](errors.json)"),
        @ApiResponse(responseCode = "500", description = "The request can't be fulfilled due to an error at the back-end. The error is never the client’s fault and therefore it is reasonable for the client to retry the exact same request that triggered this response.  The response body SHOULD contain a JSON error object. MUST be any HTTP status code specified in [RFC 7231](https://tools.ietf.org/html/rfc7231#section-6.6).  See also: * [Error Handling](#section/API-Principles/Error-Handling) in the API in general. * [Common Error Codes](errors.json)") })
    @GetMapping(value = "/processes", produces = { "application/json" })
    
    public ResponseEntity<Processes> listProcesses(@Min(1)@Parameter(description = "This parameter enables pagination for the endpoint and specifies the maximum number of elements that arrays in the top-level object (e.g. jobs or log entries) are allowed to contain. The only exception is the `links` array, which MUST NOT be paginated as otherwise the pagination links may be missing ins responses. If the parameter is not provided or empty, all elements are returned.  Pagination is OPTIONAL and back-ends and clients may not support it. Therefore it MUST be implemented in a way that clients not supporting pagination get all resources regardless. Back-ends not supporting  pagination will return all resources.  If the response is paginated, the links array MUST be used to propagate the  links for pagination with pre-defined `rel` types. See the links array schema for supported `rel` types.  *Note:* Implementations can use all kind of pagination techniques, depending on what is supported best by their infrastructure. So it doesn't care whether it is page-based, offset-based or uses tokens for pagination. The clients will use whatever is specified in the links with the corresponding `rel` types.") @Valid @RequestParam(value = "limit", required = false) Integer limit) {
    	ObjectMapper mapper = new ObjectMapper();
    	Processes processesList = new Processes();
    	HashMap<String, Process> processMap = new HashMap<String, Process>();
		try {
			Processes processesListWCPS = mapper.readValue(processesFileWCPS.getInputStream(), Processes.class);
			Processes processesListODC = mapper.readValue(processesFileODC.getInputStream(), Processes.class);
			for(Process processElement: processesListWCPS.getProcesses()) {
				processElement.addEngine(EngineTypes.WCPS);
				processMap.put(processElement.getId(),processElement);
				//processesList.addProcessesItem(processElement);
			}
			for(Process processElement: processesListODC.getProcesses()) {
				Process currentProcess = processMap.get(processElement.getId());
				if (currentProcess == null) {
					processElement.addEngine(EngineTypes.ODC_DASK);
					processMap.put(processElement.getId(),processElement);					
				}
				else {
					processMap.get(processElement.getId()).addEngine(EngineTypes.ODC_DASK);
				}
				//processesList.addProcessesItem(processElement);
			}
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
//    	
////    	for(String key : this.processes.keySet()){
////			Process process = this.processes.get(key);
////			Process processDef = new Process();	    	
////	    	processDef.setId(process.getId());
////			processDef.setSummary(process.getSummary());
////			processDef.setDescription(process.getDescription());
////			processDef.setParameters(process.getParameters());
////			processDef.setCategories(process.getCategories());
////			processDef.setReturns(process.getReturns());
////			processesList.addProcessesItem(processDef);
////		}
//    	
//    	
//    	
//    	Process process1 = new Process ();
//    	process1.setId("load_collection");
//    	process1.setSummary("Load a collection");
//    	process1.setDescription("Loads a collection from the current back-end by its id and returns it as processable data cube. The data that is added to the data cube can be restricted with the additional `spatial_extent`, `temporal_extent`, `bands` and `properties`.\\n\\n**Remarks:**\\n\\n* The bands (and all dimensions that specify nominal dimension labels) are expected to be ordered as specified in the metadata if the `bands` parameter is set to `null`.\\n* If no additional parameter is specified this would imply that the whole data set is expected to be loaded. Due to the large size of many data sets this is not recommended and may be optimized by back-ends to only load the data that is actually required after evaluating subsequent processes such as filters. This means that the pixel values should be processed only after the data has been limited to the required extents and as a consequence also to a manageable size.");
//    	List<String> categories1 = new ArrayList<String>();
//    	categories1.add("cubes");
//    	categories1.add("import");
//    	process1.setCategories(categories1);
//    	ProcessParameter parametersItem1ID =  new ProcessParameter();
//    	ProcessParameter parametersItem1SpatExt =  new ProcessParameter();
//    	ProcessParameter parametersItem1TempExt =  new ProcessParameter();
////    	ParameterSchema schemaParameter1ID = new ParameterSchema();
////    	ParameterSchema schemaParameter1SpatExt = new ParameterSchema();
//    	parametersItem1ID.setName("id");
//    	parametersItem1SpatExt.setName("spatial_extent");
//    	parametersItem1TempExt.setName("temporal_extent");
//    	parametersItem1ID.setDescription("The collection id.");
//    	parametersItem1SpatExt.setDescription("Limits the data to load from the collection to the specified bounding box or polygons.\\n\\nThe process puts a pixel into the data cube if the point at the pixel center intersects with the bounding box or any of the polygons (as defined in the Simple Features standard by the OGC).\\n\\nThe GeoJSON can be one of the following GeoJSON types:\\n\\n* A `Polygon` geometry,\\n* a `GeometryCollection` containing Polygons,\\n* a `Feature` with a `Polygon` geometry or\\n* a `FeatureCollection` containing `Feature`s with a `Polygon` geometry.\\n\\nSet this parameter to `null` to set no limit for the spatial extent. Be careful with this when loading large datasets!");
//    	parametersItem1TempExt.setDescription("Limits the data to load from the collection to the specified left-closed temporal interval. Applies to all temporal dimensions. The interval has to be specified as an array with exactly two elements:\\n\\n1. The first element is the start of the temporal interval. The specified instance in time is **included** in the interval.\\n2. The second element is the end of the temporal interval. The specified instance in time is **excluded** from the interval.\\n\\nThe specified temporal strings follow [RFC 3339](https://tools.ietf.org/html/rfc3339). Also supports open intervals by setting one of the boundaries to `null`, but never both.\\n\\nSet this parameter to `null` to set no limit for the spatial extent. Be careful with this when loading large datasets!");
////    	parametersItem1ID.setSchema(schemaParameter1ID);
////    	parametersItem1SpatExt.setSchema(schemaParameter1SpatExt);
//    	process1.addParametersItem(parametersItem1ID);
//    	process1.addParametersItem(parametersItem1SpatExt);
//    	process1.addParametersItem(parametersItem1TempExt);
//    	
//    	ProcessReturnValue returns1 = new ProcessReturnValue();
//    	DataTypeSchema schemaReturn1 = new DataTypeSchema();
//    	returns1.setDescription("A data cube for further processing. The dimensions and dimension properties (name, type, labels, reference system and resolution) correspond to the collection's metadata, but the dimension labels are restricted as specified in the parameters.");
//    	
////    	returns1.setSchema(schemaReturn1);
//    	process1.setReturns(returns1);
//    	
//    	
//    	Process process2 = new Process ();
//    	process2.setId("run_udf");
//    	process2.setSummary("Run an UDF");
//    	process2.setDescription("Runs an UDF in one of the supported runtime environments.\\n\\nThe process can either:\\n\\n1. load and run a locally stored UDF from a file in the workspace of the authenticated user. The path to the UDF file must be relative to the root directory of the user's workspace.\\n2. fetch and run a remotely stored and published UDF by absolute URI, for example from [openEO Hub](https://hub.openeo.org)).\\n3. run the source code specified inline as string.\\n\\nThe loaded UDF can be executed in several processes such as ``aggregate_spatial()``, ``apply()``, ``apply_dimension()`` and ``reduce_dimension()``. In this case an array is passed instead of a raster data cube. The user must ensure that the data is properly passed as an array so that the UDF can make sense of it.");
//    	List<String> categories2 = new ArrayList<String>();
//    	categories2.add("cubes");
//    	categories2.add("import");
//    	categories2.add("udf");
//    	process2.setCategories(categories2);
//    	ProcessParameter parametersItem2Data =  new ProcessParameter();
//    	ProcessParameter parametersItem2UDF =  new ProcessParameter();
//    	ProcessParameter parametersItem2Runtime =  new ProcessParameter();
//
//    	parametersItem2Data.setName("data");
//    	parametersItem2UDF.setName("udf");
//    	parametersItem2Runtime.setName("runtime");
//    	parametersItem2Data.setDescription("The data to be passed to the UDF as array or raster data cube.");    	
//    	parametersItem2UDF.setDescription("Either source code, an absolute URL or a path to an UDF script.");
//    	parametersItem2Runtime.setDescription("An UDF runtime identifier available at the back-end.");
//    	process2.addParametersItem(parametersItem2Data);
//    	process2.addParametersItem(parametersItem2UDF);
//    	process2.addParametersItem(parametersItem2Runtime);
//    	
//    	ProcessReturnValue returns2 = new ProcessReturnValue();
//    	DataTypeSchema schemaReturn2 = new DataTypeSchema();
//    	
//    	returns2.setDescription("The data processed by the UDF.\\n\\n* Returns a raster data cube, if a raster data cube is passed for `data`. Details on the dimensions and dimension properties (name, type, labels, reference system and resolution) depend on the UDF.\\n* If an array is passed for `data`, the returned value can be of any data type, but is exactly what the UDF returns.");
//    	process2.setReturns(returns2);
//    	
//    	
//    	Process process3 = new Process ();
//    	process3.setId("save_result");
//    	process3.setSummary("Save processed data to storage");
//    	process3.setDescription("Saves processed data to the local user workspace / data store of the authenticated user. This process aims to be compatible to GDAL/OGR formats and options. STAC-compatible metadata should be stored with the processed data.\\n\\nCalling this process may be rejected by back-ends in the context of secondary web services.");
//    	List<String> categories3 = new ArrayList<String>();
//    	categories3.add("cubes");
//    	categories3.add("export");
//    	process3.setCategories(categories3);
//    	ProcessParameter parametersItem3Data =  new ProcessParameter();
//    	ProcessParameter parametersItem3Format =  new ProcessParameter();
//
//    	parametersItem3Data.setName("data");
//    	parametersItem3Format.setName("format");
//    	parametersItem3Data.setDescription("The data to save.");
//    	parametersItem3Format.setDescription("The file format to save to. It must be one of the values that the server reports as supported output file formats, which usually correspond to the short GDAL/OGR codes. If the format is not suitable for storing the underlying data structure, a `FormatUnsuitable` exception will be thrown. This parameter is *case insensitive*.");
//    	process3.addParametersItem(parametersItem3Data);
//    	process3.addParametersItem(parametersItem3Format);
//    	
//    	ProcessReturnValue returns3 = new ProcessReturnValue();
//    	DataTypeSchema schemaReturn3 = new DataTypeSchema();
//    	
//    	returns2.setDescription("false` if saving failed, `true` otherwise.");
//    	process2.setReturns(returns3);
//    	
//    	
//    	processesList.addProcessesItem(process1);
//    	processesList.addProcessesItem(process2);
//    	processesList.addProcessesItem(process3);
    	
    	
//    	getRequest().ifPresent(request -> {
//            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
//                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
//                	String exampleString = "{ \"processes\" : [ \"{}\", \"{}\" ], \"links\" : [ { \"rel\" : \"related\", \"href\" : \"https://example.openeo.org\", \"type\" : \"text/html\", \"title\" : \"openEO\" }, { \"rel\" : \"related\", \"href\" : \"https://example.openeo.org\", \"type\" : \"text/html\", \"title\" : \"openEO\" } ] }";
//                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
//                    
//                }
//            }
//        });
		for (Process proc: processMap.values()) {
			processesList.addProcessesItem(proc);
		}
        return new ResponseEntity<Processes>(processesList, HttpStatus.OK);

    }
    
}
