package org.openeo.spring.api;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.openeo.spring.model.Error;
import org.openeo.spring.model.Job;
import org.openeo.spring.model.Processes;
import org.openeo.spring.model.Process;
import org.openeo.spring.model.Collection;
import org.openeo.spring.model.Collections;
import org.openeo.spring.model.EngineTypes;
import org.openeo.spring.api.CollectionsApiController;
import org.openeo.spring.components.CollectionMap;
import org.openeo.spring.components.CollectionsMap;
import org.openeo.spring.components.JobScheduler;
import org.openeo.wcps.ConvenienceHelper;
import org.openeo.wcps.WCPSQueryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.NativeWebRequest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2020-07-02T08:45:00.334+02:00[Europe/Rome]")
@Component
@RestController
@RequestMapping("${openapi.openEO.base-path:}")
public class ResultApiController implements ResultApi {

	@Autowired
	private JobScheduler jobScheduler;
	
	@Autowired
	private CollectionsMap collectionsMap;
	
	@Autowired
	private CollectionMap  collectionMap;
	
	private final NativeWebRequest request;

	private final Logger log = LogManager.getLogger(ResultApiController.class);

	@Value("${org.openeo.wcps.endpoint}")
	private String wcpsEndpoint;

	@Value("${org.openeo.endpoint}")
	private String openEOEndpoint;

	@Value("${org.openeo.odc.endpoint}")
	private String odcEndpoint;

	@Value("${org.openeo.wcps.collections.list}")
	Resource collectionsFileWCPS;
	@Value("${org.openeo.odc.collections.list}")
	Resource collectionsFileODC;
	
	@Value("${org.openeo.wcps.processes.list}")
	Resource processesFileWCPS;
	@Value("${org.openeo.odc.processes.list}")
	Resource processesFileODC;
	
	@org.springframework.beans.factory.annotation.Autowired
	public ResultApiController(NativeWebRequest request) {
		this.request = request;
	}

	@Override
	public Optional<NativeWebRequest> getRequest() {
		return Optional.ofNullable(request);
	}

	@Operation(summary = "Process and download data synchronously", operationId = "computeResult", description = "A user-defined process will be executed directly and the result will be downloaded in the format specified in the process graph. This endpoint can be used to generate small previews or test user-defined processes before starting a batch job. Timeouts on either client- or server-side are to be expected for complex computations. Back-ends MAY send the openEO error `ProcessGraphComplexity` immediately if the computation is expected to time out. Otherwise requests MAY time-out after a certain amount of time by sending openEO error `RequestTimeout`. A header named `OpenEO-Costs` MAY be sent with all responses, which MUST include the costs for processing and downloading the data. Additionally,  a link to a log file MAY be sent in the header.", tags = {
			"Data Processing", })
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Result data in the requested output format"),
			@ApiResponse(responseCode = "400", description = "The request can't be fulfilled due to an error on client-side, i.e. the request is invalid. The client should not repeat the request without modifications.  The response body SHOULD contain a JSON error object. MUST be any HTTP status code specified in [RFC 7231](https://tools.ietf.org/html/rfc7231#section-6.6). This request MUST respond with HTTP status codes 401 if authorization is required or 403 if the authorization failed or access is forbidden in general to the authenticated user. HTTP status code 404 should be used if the value of a path parameter is invalid.  See also: * [Error Handling](#section/API-Principles/Error-Handling) in the API in general. * [Common Error Codes](errors.json)"),
			@ApiResponse(responseCode = "500", description = "The request can't be fulfilled due to an error at the back-end. The error is never the client’s fault and therefore it is reasonable for the client to retry the exact same request that triggered this response.  The response body SHOULD contain a JSON error object. MUST be any HTTP status code specified in [RFC 7231](https://tools.ietf.org/html/rfc7231#section-6.6).  See also: * [Error Handling](#section/API-Principles/Error-Handling) in the API in general. * [Common Error Codes](errors.json)") })
	@RequestMapping(value = "/result", produces = { "*" }, consumes = {
			"application/json" }, method = RequestMethod.POST)
	public ResponseEntity<?> computeResult(@Parameter(description = "", required = true) @Valid @RequestBody Job job) {
		JSONObject processGraphJSON = (JSONObject) job.getProcess().getProcessGraph();
		EngineTypes resultEngine = null;
		try {
			resultEngine = checkGraphValidityAndEngine(processGraphJSON);
		} catch (Exception e) {
			Error error = new Error();
			error.setCode("500");
			error.setMessage(e.getMessage());
			log.error(error.getMessage());
			return new ResponseEntity<Error>(error, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		if (resultEngine == EngineTypes.ODC_DASK) {
			JSONObject process = new JSONObject();
			process.put("id", "ODC-graph");
			process.put("process_graph", processGraphJSON);
			URL url;
			HttpURLConnection conn;
			try {
				url = new URL(odcEndpoint);
				conn = (HttpURLConnection) url.openConnection();
				conn.setRequestMethod("POST");
				conn.setRequestProperty("Content-Type", "application/json; utf-8");
				conn.setDoOutput(true);
			} catch (Exception e) {
				addStackTraceAndErrorToLog(e);
				Error error = new Error();
				error.setCode("500");
				error.setMessage("Not possible to establish connection with ODC Endpoint!");
				log.error(error.getMessage());
				return new ResponseEntity<Error>(error, HttpStatus.INTERNAL_SERVER_ERROR);
			}
			try {
				log.debug("graph object send to ODC server: " + process.toString());
				try (OutputStream os = conn.getOutputStream()) {
					byte[] requestBody = process.toString().getBytes("utf-8");
					os.write(requestBody, 0, requestBody.length);
				}
				InputStream is = conn.getInputStream();
			    String mime = conn.getContentType();
				//String mime = URLConnection.guessContentTypeFromStream(is);
				log.debug("Mime type on ODC response guessed to be: " + mime);
				byte[] response = IOUtils.toByteArray(is);
				log.info("Job successfully executed: " + job.toString());
				if (mime == null) {
					return ResponseEntity.ok().contentType(MediaType.parseMediaType("application/octet-stream")).body(response);
				}
				return ResponseEntity.ok().contentType(MediaType.parseMediaType(mime)).body(response);
			} catch (Exception e) {
				StringBuilder importProcessLogger = new StringBuilder();
				BufferedReader importProcessLogErrorReader = new BufferedReader(
				new InputStreamReader(conn.getErrorStream()));
				String line;
				try{
					while ((line = importProcessLogErrorReader.readLine()) != null) {
						importProcessLogger.append(line + "\n");
					}
					log.error(importProcessLogger);
					Error error = new Error();
					error.setCode("500");
					error.setMessage(importProcessLogger.toString());
					return new ResponseEntity<Error>(error, HttpStatus.INTERNAL_SERVER_ERROR);
				}
				catch (Exception e1){
					addStackTraceAndErrorToLog(e1);
				}
				addStackTraceAndErrorToLog(e);
			}
		} else if(resultEngine == EngineTypes.WCPS) {
			WCPSQueryFactory wcpsFactory = null;
			wcpsFactory = new WCPSQueryFactory(processGraphJSON, openEOEndpoint, wcpsEndpoint, collectionMap);
			URL url;
			try {
				url = new URL(wcpsEndpoint + "?SERVICE=WCS" + "&VERSION=2.0.1" + "&REQUEST=ProcessCoverages" + "&QUERY="
						+ URLEncoder.encode(wcpsFactory.getWCPSString(), "UTF-8").replace("+", "%20"));
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				conn.setRequestMethod("GET");
				//TODO pipe error stream message from rasdaman into error return here
				byte[] response = IOUtils.toByteArray(conn.getInputStream());
				log.info("Job successfully executed: " + job.toString());
//				.contentType(new MediaType(ConvenienceHelper.getMimeTypeFromRasName(wcpsFactory.getOutputFormat())))
				return ResponseEntity.ok()
						.contentType(MediaType.parseMediaType(ConvenienceHelper.getMimeTypeFromRasName(wcpsFactory.getOutputFormat())))
						.body(response);
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();

			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		Error error = new Error();
		error.setCode("500");
		error.setMessage("The submitted job " + job.toString() + " was not executed!");
		log.error(error.getMessage());
		return new ResponseEntity<Error>(error, HttpStatus.INTERNAL_SERVER_ERROR);

	}
	
	public EngineTypes checkGraphValidityAndEngine(JSONObject processGraphJSON) throws Exception {
		//TODO Check to which engine we need to send the job
		String collectionID = new String();
	
		List<JSONObject> loadCollectionNodes = jobScheduler.getProcessNode("load_collection",processGraphJSON);
		
		boolean containsSameEngineCollections = false;
		EngineTypes selectedEngineType = null;
		
		if(!loadCollectionNodes.isEmpty()){
			// The following loop checks if the collections requested in the load_collection calls are provided by the same engine and tells us which to use
			for (EngineTypes enType: EngineTypes.values()) {
				for (JSONObject loadCollectionNode: loadCollectionNodes) {
					collectionID = loadCollectionNode.getJSONObject("arguments").get("id").toString(); // The collection id requested in the process graph
		
					Collections engineCollections = collectionsMap.get(enType); // All the collections offered by this engine type
					Collection collection = null;
					
					for (Collection coll: engineCollections.getCollections()) {
						if (coll.getId().equals(collectionID)) {
							collection = coll; // We found the requested collection in the current engine of the loop
							break;
						}
					}
					if (collection == null) {
						containsSameEngineCollections = false;
						break;
					}
					else {
						selectedEngineType = enType;
						containsSameEngineCollections = true;
					}
				}
				if (containsSameEngineCollections) {
					break; // We don't need to check anything else, we found that the required collections are in the current engine/back-end
				}
			}
		checkProcessesAvailability(processGraphJSON,selectedEngineType);
		}
		else {
			throw new Exception("The submitted job contains no load_collection process!");
		}
		if(!containsSameEngineCollections){
			throw new Exception("The submitted job contains collections from two different engines, not supported!");
		}
		return selectedEngineType;
	}
	
	boolean checkProcessesAvailability(JSONObject processGraphJSON, EngineTypes engine) throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		Processes processesAvailableList = new Processes();
		if(engine==EngineTypes.ODC_DASK) {
			try {
				processesAvailableList = mapper.readValue(processesFileODC.getInputStream(), Processes.class);
			} catch (Exception e) {
				addStackTraceAndErrorToLog(e);
			}
		}
		else if(engine==EngineTypes.WCPS){
			try {
				processesAvailableList = mapper.readValue(processesFileWCPS.getInputStream(), Processes.class);
			} catch (Exception e) {
				addStackTraceAndErrorToLog(e);
			}
		}
		else {
			throw new Exception("Selected engine " + engine.toString() + " not found!");
		}
		List<String> processesList = new ArrayList<>();
		List<String> missingProcessesList = new ArrayList<>();
		for (Process proc: processesAvailableList.getProcesses()) {
			processesList.add(proc.getId());
		}
		for (String processNodeKey : processGraphJSON.keySet()) {
			JSONObject processNode = processGraphJSON.getJSONObject(processNodeKey);
			String processID = processNode.getString("process_id");
			if(processID.equals("apply")) {
				//We need to check the availability of the processes called in the sub process graph
				checkProcessesAvailability(processNode.getJSONObject("arguments").getJSONObject("process").getJSONObject("process_graph"),engine);
			}
			if(processID.equals("reduce_dimension")) {
				//We need to check the availability of the processes called in the sub process graph
				checkProcessesAvailability(processNode.getJSONObject("arguments").getJSONObject("reducer").getJSONObject("process_graph"),engine);
			}
			if (!processesList.contains(processID)) {
				missingProcessesList.add(processID);
			}
			// If process_id not in current engine processes list return/raise error
		}
		log.info(missingProcessesList);
		if (missingProcessesList.isEmpty()==false) {
			throw new Exception("Selected collection from " + engine.toString() + ". Processes " + missingProcessesList.toString()+ " not found in " + engine.toString() + " Processes! Can't run the provided process graph.");
			}
		return true;
	}
	
	
	private void addStackTraceAndErrorToLog(Exception e) {
		log.error(e.getMessage());
		StringBuilder builder = new StringBuilder();
		for (StackTraceElement element : e.getStackTrace()) {
			builder.append(element.toString() + "\n");
		}
		log.error(builder.toString());
	}

}
