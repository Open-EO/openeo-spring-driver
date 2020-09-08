package org.openeo.spring.api;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;

import org.openeo.spring.model.Collection;
import org.openeo.spring.model.Error;
import org.openeo.spring.model.Processes;
import org.openeo.spring.model.Process;
import org.openeo.spring.model.Link;
import org.openeo.spring.model.ProcessParameter;
import org.openeo.spring.model.ProcessReturnValue;
import org.openeo.spring.model.ParameterSchema;
import org.openeo.spring.model.DataTypeSchema;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.NativeWebRequest;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.Authorization;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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

    @org.springframework.beans.factory.annotation.Autowired
    public ProcessesApiController(NativeWebRequest request) {
        this.request = request;
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
    @Operation(summary = "Supported predefined processes", operationId = "listProcesses", description = "The request asks the back-end for available predefined processes and returns detailed process descriptions, including parameters and return values.", tags={ "Process Discovery", })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "Formal specification describing the supported predefined processes."),
        @ApiResponse(responseCode = "400", description = "The request can't be fulfilled due to an error on client-side, i.e. the request is invalid. The client should not repeat the request without modifications.  The response body SHOULD contain a JSON error object. MUST be any HTTP status code specified in [RFC 7231](https://tools.ietf.org/html/rfc7231#section-6.6). This request MUST respond with HTTP status codes 401 if authorization is required or 403 if the authorization failed or access is forbidden in general to the authenticated user. HTTP status code 404 should be used if the value of a path parameter is invalid.  See also: * [Error Handling](#section/API-Principles/Error-Handling) in the API in general. * [Common Error Codes](errors.json)"),
        @ApiResponse(responseCode = "500", description = "The request can't be fulfilled due to an error at the back-end. The error is never the client’s fault and therefore it is reasonable for the client to retry the exact same request that triggered this response.  The response body SHOULD contain a JSON error object. MUST be any HTTP status code specified in [RFC 7231](https://tools.ietf.org/html/rfc7231#section-6.6).  See also: * [Error Handling](#section/API-Principles/Error-Handling) in the API in general. * [Common Error Codes](errors.json)") })
    @GetMapping(value = "/processes", produces = { "application/json" })
    
    public ResponseEntity<Processes> listProcesses(@Min(1)@ApiParam(value = "This parameter enables pagination for the endpoint and specifies the maximum number of elements that arrays in the top-level object (e.g. jobs or log entries) are allowed to contain. The only exception is the `links` array, which MUST NOT be paginated as otherwise the pagination links may be missing ins responses. If the parameter is not provided or empty, all elements are returned.  Pagination is OPTIONAL and back-ends and clients may not support it. Therefore it MUST be implemented in a way that clients not supporting pagination get all resources regardless. Back-ends not supporting  pagination will return all resources.  If the response is paginated, the links array MUST be used to propagate the  links for pagination with pre-defined `rel` types. See the links array schema for supported `rel` types.  *Note:* Implementations can use all kind of pagination techniques, depending on what is supported best by their infrastructure. So it doesn't care whether it is page-based, offset-based or uses tokens for pagination. The clients will use whatever is specified in the links with the corresponding `rel` types.") @Valid @RequestParam(value = "limit", required = false) Integer limit) {
    	ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
    	InputStream stream = classLoader.getResourceAsStream("processes.json");
    	InputStream linkstream = classLoader.getResourceAsStream("links.json");
    	Processes processesList = new Processes();
    	this.mapper = new ObjectMapper();
    	this.processes = new HashMap<String, Process>();
    	
    	try {
			Process[] processArray = this.mapper.readValue(stream, Process[].class);
			Link[] linksArray = this.mapper.readValue(linkstream, Link[].class);
			
			for(int p = 0; p < linksArray.length; p++) {
				this.links.put(linksArray[p].getRel(), linksArray[p]);				
//				log.debug("Found and stored process: " + linksArray[p].getRel());
			}
			
			for(int p = 0; p < processArray.length; p++) {
				this.processes.put(processArray[p].getId(), processArray[p]);				
//				log.debug("Found and stored process: " + processArray[p].getId());
			}			
		} catch (JsonParseException e) {
//			log.error("Error parsing json: " + e.getMessage());
		} catch (JsonMappingException e) {
//			log.error("Error mapping json to java: " + e.getMessage());
		} catch (IOException e) {
//			log.error("Error reading json file: " + e.getMessage());
		}
    	
    	for(String key : this.processes.keySet()){
			Process process = this.processes.get(key);
			Process processDef = new Process();	    	
	    	process.setId(process.getId());
			processDef.setSummary(process.getSummary());
			processDef.setDescription(process.getDescription());
			processDef.setParameters(process.getParameters());
			processDef.setCategories(process.getCategories());
			processDef.setReturns(process.getReturns());
			processesList.addProcessesItem(processDef);
		}
    	
//    	Processes processesList = new Processes();
//    	Process processCount = new Process ();
//    	processCount.setId("count");
//    	processCount.setSummary("Count the number of elements");
//    	processCount.setDescription("Gives the number of elements in an array that matches the specified condition.\\n\\n**Remarks:**\\n\\n* Counts the number of valid elements by default (`condition` is set to `null`). A valid element is every element for which ``is_valid()`` returns `true`.\\n* To count all elements in a list set the `condition` parameter to boolean `true`.");
//    	List<String> categoriesCount = new ArrayList<String>();
//    	categoriesCount.add("arrays");
//    	categoriesCount.add("reducer");
//    	processCount.setCategories(categoriesCount);
//    	ProcessParameter parametersItemCountDATA =  new ProcessParameter();
//    	ProcessParameter parametersItemCountCONDITION =  new ProcessParameter();
//    	ParameterSchema schemaParameterCountDATA = new ParameterSchema();
//    	ParameterSchema schemaParameterCountCONDITION = new ParameterSchema();
//    	parametersItemCountDATA.setName("data");
//    	parametersItemCountCONDITION.setName("y");
//    	parametersItemCountDATA.setDescription("An array with elements of any data type.");
//    	parametersItemCountCONDITION.setDescription("A condition consists of one ore more processes, which in the end return a boolean value. It is evaluated against each element in the array. An element is counted only if the condition returns `true`. Defaults to count valid elements in a list (see ``is_valid()``). Setting this parameter to boolean `true` counts all elements in the list.");
//    	
//    	parametersItemCountDATA.setSchema(schemaParameterCountDATA);
//    	parametersItemCountCONDITION.setSchema(schemaParameterCountCONDITION);
//    	processCount.addParametersItem(parametersItemCountDATA);
//    	processCount.addParametersItem(parametersItemCountCONDITION);
//    	
//    	ProcessReturnValue returnsCount = new ProcessReturnValue();
//    	DataTypeSchema schemaReturnCount = new DataTypeSchema();
//    	returnsCount.setDescription("The counted number of elements.");
//    	
//    	returnsCount.setSchema(schemaReturnCount);
//    	processCount.setReturns(returnsCount);
//    	
//    	processesList.addProcessesItem(processCount);
    	
    	
//    	getRequest().ifPresent(request -> {
//            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
//                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
//                	String exampleString = "{ \"processes\" : [ \"{}\", \"{}\" ], \"links\" : [ { \"rel\" : \"related\", \"href\" : \"https://example.openeo.org\", \"type\" : \"text/html\", \"title\" : \"openEO\" }, { \"rel\" : \"related\", \"href\" : \"https://example.openeo.org\", \"type\" : \"text/html\", \"title\" : \"openEO\" } ] }";
//                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
//                    
//                }
//            }
//        });
        return new ResponseEntity<Processes>(processesList, HttpStatus.OK);

    }
    
}
