package org.openeo.spring.api;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringBufferInputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.Principal;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import javax.annotation.PostConstruct;
import javax.swing.event.EventListenerList;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;

import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;
import org.apache.tomcat.util.json.JSONParser;
import org.json.JSONArray;
import org.json.JSONObject;
import org.keycloak.representations.AccessToken;
import org.openeo.spring.components.JobScheduler;
import org.openeo.spring.dao.BatchJobResultDAO;
import org.openeo.spring.dao.JobDAO;
import org.openeo.spring.model.BatchJobEstimate;
import org.openeo.spring.model.BatchJobResult;
import org.openeo.spring.model.BatchJobs;
import org.openeo.spring.model.EngineTypes;
import org.openeo.spring.model.Error;
import org.openeo.spring.model.Job;
import org.openeo.spring.model.JobStates;
import org.openeo.spring.model.Link;
import org.openeo.spring.model.LogEntries;
import org.openeo.spring.model.LogEntry;
import org.openeo.spring.model.LogEntry.LevelEnum;
import org.openeo.spring.api.ResultApiController;
import org.openeo.wcps.ConvenienceHelper;
import org.openeo.wcps.events.JobEvent;
import org.openeo.wcps.events.JobEventListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.NativeWebRequest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.swagger.v3.core.util.Json;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2020-07-02T08:45:00.334+02:00[Europe/Rome]")
@RestController
@RequestMapping("${openapi.openEO.base-path:}")
public class JobsApiController implements JobsApi {

	private final NativeWebRequest request;

	private final Logger log = LogManager.getLogger(JobsApiController.class);

	private EventListenerList listenerList = new EventListenerList();

	@Value("${org.openeo.wcps.endpoint}")
	private String wcpsEndpoint;

	@Value("${org.openeo.endpoint}")
	private String openEOEndpoint;
	
	@Value("${org.openeo.public.endpoint}")
	private String openEOPublicEndpoint;

	@Value("${org.openeo.odc.endpoint}")
	private String odcEndpoint;

	@Value("${org.openeo.tmp.dir}")
	private String tmpDir;
	
	@Value("${co.elasticsearch.endpoint}")
	private String elasticSearchEndpoint;
	
	@Value("${co.elasticsearch.service.name}")
	private String serviceName;
	
	@Value("${co.elasticsearch.service.node.name}")
	private String serviceNodeName;

	@Autowired
	private JobScheduler jobScheduler;
	
	@Autowired
	private AuthzService authzService;
	
	@Autowired
	private ResultApiController resultApiController;

	JobDAO jobDAO;

	BatchJobResultDAO resultDAO;

	@Autowired
	public void setDao(JobDAO injectedJObDAO, BatchJobResultDAO injectResultDao) {
		jobDAO = injectedJObDAO;
		resultDAO = injectResultDao;
	}

	@org.springframework.beans.factory.annotation.Autowired
	public JobsApiController(NativeWebRequest request) {
		this.request = request;

	}

	@PostConstruct
	public void init() {
		this.addJobListener(jobScheduler);
	}

	@Override
	public Optional<NativeWebRequest> getRequest() {
		return Optional.ofNullable(request);
	}
	
	@Autowired
	public Identity identity;

	/**
	 * POST /jobs : Create a new batch job Creates a new batch processing task (job)
	 * from one or more (chained) processes at the back-end. Processing the data
	 * doesn&#39;t start yet. The job status gets initialized as &#x60;created&#x60;
	 * by default.
	 * @param Principal 
	 * @param storeBatchJobRequest (required)
	 * @return The batch job has been created successfully. (status code 201) or The
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
	@Operation(summary = "Create a new batch job", operationId = "createJob", description = "Creates a new batch processing task (job) from one or more (chained) processes at the back-end.  Processing the data doesn't start yet. The job status gets initialized as `created` by default.", tags = {
			"Data Processing", "Batch Jobs", })
	@ApiResponses(value = {
			@ApiResponse(responseCode = "201", description = "The batch job has been created successfully."),
			@ApiResponse(responseCode = "400", description = "The request can't be fulfilled due to an error on client-side, i.e. the request is invalid. The client should not repeat the request without modifications.  The response body SHOULD contain a JSON error object. MUST be any HTTP status code specified in [RFC 7231](https://tools.ietf.org/html/rfc7231#section-6.6). This request MUST respond with HTTP status codes 401 if authorization is required or 403 if the authorization failed or access is forbidden in general to the authenticated user. HTTP status code 404 should be used if the value of a path parameter is invalid.  See also: * [Error Handling](#section/API-Principles/Error-Handling) in the API in general. * [Common Error Codes](errors.json)"),
			@ApiResponse(responseCode = "500", description = "The request can't be fulfilled due to an error at the back-end. The error is never the client’s fault and therefore it is reasonable for the client to retry the exact same request that triggered this response.  The response body SHOULD contain a JSON error object. MUST be any HTTP status code specified in [RFC 7231](https://tools.ietf.org/html/rfc7231#section-6.6).  See also: * [Error Handling](#section/API-Principles/Error-Handling) in the API in general. * [Common Error Codes](errors.json)") })
	@RequestMapping(value = "/jobs", produces = { "application/json" }, consumes = {
			"application/json" }, method = RequestMethod.POST)
	public ResponseEntity<?> createJob(@Parameter(description = "", required = true) @Valid @RequestBody Job job, Principal principal) {
		AccessToken token = null;
		if(principal != null) {
			token = TokenUtil.getAccessToken(principal);
			job.setOwnerPrincipal(token.getPreferredUsername());
			ThreadContext.put("userid", token.getPreferredUsername());
		}
		//TODO add validity check of the job using ValidationApiController
//    	UUID jobID = UUID.randomUUID();
//    	job.setId(jobID);
		
		job.setStatus(JobStates.CREATED);
		job.setPlan("free");
		job.setCreated(OffsetDateTime.now());
		job.setUpdated(OffsetDateTime.now());
		log.debug("received jobs POST request for new job with ID + " + job.getId());
		JSONObject processGraph = (JSONObject) job.getProcess().getProcessGraph();
	     
		
		Set<String> roles = new HashSet<>();
		Map<String, AccessToken.Access> resourceAccess = token.getResourceAccess();
		for (Map.Entry<String, AccessToken.Access> e : resourceAccess.entrySet()) {
			if (e.getValue().getRoles() != null){
				for(String r: e.getValue().getRoles()) {
					roles.add(r);					
				}			
			}		
		}
		

		boolean isEuracUser = roles.contains("eurac");
	
		Iterator<String> keys = processGraph.keys();
		boolean isCreateJobAllow= true;
		while(keys.hasNext()) {
			String key = keys.next();
			JSONObject processNode = (JSONObject) processGraph.get(key);
			String process_id = processNode.get("process_id").toString();
			if (process_id.equals("run_udf") && !isEuracUser) {
				isCreateJobAllow =false;				    
			}
		}

		try {
			EngineTypes resultEngine = null;
			resultEngine = resultApiController.checkGraphValidityAndEngine(processGraph);
			job.setEngine(resultEngine);
		} catch (Exception e) {
			Error error = new Error();
			error.setCode("500");
			error.setMessage(e.getMessage());
			log.error(error.getMessage());
			ThreadContext.clearMap();
			return new ResponseEntity<Error>(error, HttpStatus.INTERNAL_SERVER_ERROR);
		}		
		log.trace("Process Graph attached: " + processGraph.toString(4));
		log.info("Graph of job successfully parsed and job created with ID: " + job.getId());
		
		if (isCreateJobAllow) {
		jobDAO.save(job);
		
		ThreadContext.put("jobid", job.getId().toString());
		ObjectMapper mapper = new ObjectMapper();
		try {
			log.info("job saved to database: " + mapper.writeValueAsString(job));
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Job verifiedSave = jobDAO.findOne(job.getId());
		if (verifiedSave != null) {
			if(token != null) {
			
				authzService.createProtectedResource(job, token);
			}
//			WCPSQueryFactory wcpsFactory = new WCPSQueryFactory(processGraph);
			log.debug("verified retrieved job: " + verifiedSave.toString());
			URI jobUrl;
			try {
				jobUrl = new URI(openEOPublicEndpoint + "/jobs/" + job.getId().toString());
				ThreadContext.clearMap();
				return ResponseEntity.created(jobUrl).header("OpenEO-Identifier", job.getId().toString()).body(job);
			} catch (URISyntaxException e) {
				Error error = new Error();
				error.setCode("500");
				error.setMessage("The submitted job " + job.toString() + " has an invalid URI");
				log.error("The submitted job " + job.toString() + " has an invalid URI");
				ThreadContext.clearMap();
				return new ResponseEntity<Error>(error, HttpStatus.INTERNAL_SERVER_ERROR);
			}
			
		} else {
			Error error = new Error();
			error.setCode("500");
			error.setMessage("The submitted job " + job.toString() + " was not saved persistently");
			log.error("The submitted job " + job.toString() + " was not saved persistently");
			ThreadContext.clearMap();
			return new ResponseEntity<Error>(error, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		}
		else {
			Error error = new Error();
			error.setCode("401");
			error.setMessage("You are not authorized to create this job");
			log.error("You are not authorized to create this job");
			return new ResponseEntity<Error>(error, HttpStatus.UNAUTHORIZED); 
		}
		
	}

	/**
	 * GET /jobs/{job_id}/logs : Logs for a batch job Shows log entries for the
	 * batch job, usually for debugging purposes. Back-ends can log any information
	 * that may be relevant for a user. Users can log information during data
	 * processing using respective processes such as &#x60;debug&#x60;. If requested
	 * consecutively while a job is running, it is RECOMMENDED that clients use the
	 * offset parameter to get only the entries they have not received yet. While
	 * pagination itself is OPTIONAL, the &#x60;offset&#x60; parameter is REQUIRED
	 * to be implemented by back-ends.
	 *
	 * @param jobId  Unique job identifier. (required)
	 * @param offset The last identifier (property &#x60;id&#x60; of a log entry)
	 *               the client has received. If provided, the back-ends only sends
	 *               the entries that occured after the specified identifier. If not
	 *               provided or empty, start with the first entry. (optional)
	 * @param limit  This parameter enables pagination for the endpoint and
	 *               specifies the maximum number of elements that arrays in the
	 *               top-level object (e.g. jobs or log entries) are allowed to
	 *               contain. The only exception is the &#x60;links&#x60; array,
	 *               which MUST NOT be paginated as otherwise the pagination links
	 *               may be missing ins responses. If the parameter is not provided
	 *               or empty, all elements are returned. Pagination is OPTIONAL and
	 *               back-ends and clients may not support it. Therefore it MUST be
	 *               implemented in a way that clients not supporting pagination get
	 *               all resources regardless. Back-ends not supporting pagination
	 *               will return all resources. If the response is paginated, the
	 *               links array MUST be used to propagate the links for pagination
	 *               with pre-defined &#x60;rel&#x60; types. See the links array
	 *               schema for supported &#x60;rel&#x60; types. *Note:*
	 *               Implementations can use all kind of pagination techniques,
	 *               depending on what is supported best by their infrastructure. So
	 *               it doesn&#39;t care whether it is page-based, offset-based or
	 *               uses tokens for pagination. The clients will use whatever is
	 *               specified in the links with the corresponding &#x60;rel&#x60;
	 *               types. (optional)
	 * @return Lists the requested log entries. (status code 200) or The request
	 *         can&#39;t be fulfilled due to an error on client-side, i.e. the
	 *         request is invalid. The client should not repeat the request without
	 *         modifications. The response body SHOULD contain a JSON error object.
	 *         MUST be any HTTP status code specified in [RFC
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
	@Operation(summary = "Logs for a batch job", operationId = "debugJob", description = "Shows log entries for the batch job, usually for debugging purposes.  Back-ends can log any information that may be relevant for a user. Users can log information during data processing using respective processes such as `debug`.  If requested consecutively while a job is running, it is RECOMMENDED that clients use the offset parameter to get only the entries they have not received yet.  While pagination itself is OPTIONAL, the `offset` parameter is REQUIRED to be implemented by back-ends.", tags = {
			"Data Processing", "Batch Jobs", })
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Lists the requested log entries."),
			@ApiResponse(responseCode = "400", description = "The request can't be fulfilled due to an error on client-side, i.e. the request is invalid. The client should not repeat the request without modifications.  The response body SHOULD contain a JSON error object. MUST be any HTTP status code specified in [RFC 7231](https://tools.ietf.org/html/rfc7231#section-6.6). This request MUST respond with HTTP status codes 401 if authorization is required or 403 if the authorization failed or access is forbidden in general to the authenticated user. HTTP status code 404 should be used if the value of a path parameter is invalid.  See also: * [Error Handling](#section/API-Principles/Error-Handling) in the API in general. * [Common Error Codes](errors.json)"),
			@ApiResponse(responseCode = "500", description = "The request can't be fulfilled due to an error at the back-end. The error is never the client’s fault and therefore it is reasonable for the client to retry the exact same request that triggered this response.  The response body SHOULD contain a JSON error object. MUST be any HTTP status code specified in [RFC 7231](https://tools.ietf.org/html/rfc7231#section-6.6).  See also: * [Error Handling](#section/API-Principles/Error-Handling) in the API in general. * [Common Error Codes](errors.json)") })
	@RequestMapping(value = "/jobs/{job_id}/logs", produces = { "application/json" }, method = RequestMethod.GET)
	public ResponseEntity<?> debugJob(
			@Pattern(regexp = "^[\\w\\-\\.~]+$") @Parameter(description = "Unique job identifier.", required = true) @PathVariable("job_id") String jobId,
			@Parameter(description = "The last identifier (property `id` of a log entry) the client has received. If provided, the back-ends only sends the entries that occured after the specified identifier. If not provided or empty, start with the first entry.") @Valid @RequestParam(value = "offset", required = false) String offset,
			@Min(1) @Parameter(description = "This parameter enables pagination for the endpoint and specifies the maximum number of elements that arrays in the top-level object (e.g. jobs or log entries) are allowed to contain. The only exception is the `links` array, which MUST NOT be paginated as otherwise the pagination links may be missing ins responses. If the parameter is not provided or empty, all elements are returned.  Pagination is OPTIONAL and back-ends and clients may not support it. Therefore it MUST be implemented in a way that clients not supporting pagination get all resources regardless. Back-ends not supporting  pagination will return all resources.  If the response is paginated, the links array MUST be used to propagate the  links for pagination with pre-defined `rel` types. See the links array schema for supported `rel` types.  *Note:* Implementations can use all kind of pagination techniques, depending on what is supported best by their infrastructure. So it doesn't care whether it is page-based, offset-based or uses tokens for pagination. The clients will use whatever is specified in the links with the corresponding `rel` types.") @Valid @RequestParam(value = "limit", required = false) Integer limit) {
		LogEntries logEntries = new LogEntries();
		//TODO describe query
		String elasticSearchQuery = "filebeat-7.13.3-2021.07.13-000001/_search";
		try {
			//TODO query elastic search endpoint here for all log information regarding a job queued for processing.
			URL url = new URL(elasticSearchEndpoint + "/" + elasticSearchQuery);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setDoOutput(true);
			conn.setRequestProperty("Content-Type", "application/json");
			StringBuilder queryString = new StringBuilder();
			//TODO insert elastic search parameters from application.properties through resource loader here
			queryString.append("{");
			queryString.append("    \"query\":{");
			queryString.append("            \"bool\":{");
			queryString.append("                    \"filter\":[");
			queryString.append("                            {\"term\": {\"service.name\":\"openeo\"}},");
			queryString.append("                            {\"term\": {\"service.node.name\":\"alex_dev\"}},");
			queryString.append("                            {\"term\": {\"jobid\":\"" + jobId + "\"}}");
			queryString.append("                            ]");
			queryString.append("                    }");
			queryString.append("            },");
			queryString.append("    \"fields\":[");
			queryString.append("            \"@timestamp\",");
			queryString.append("            \"message\",");
			queryString.append("            \"log.level\",");
			queryString.append("            \"log.logger\"");
			queryString.append("    ],");
			queryString.append("    \"_source\": false");
			queryString.append("}");
			OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream());
			out.write(queryString.toString());
			out.close();
			InputStream errorStream = conn.getErrorStream();
			if (errorStream != null) {
				BufferedReader reader = new BufferedReader(new InputStreamReader(errorStream));
				StringBuilder errorMessage = new StringBuilder();
				String line;
				while ((line = reader.readLine()) != null) {
					log.error(line);
					errorMessage.append(line);
					errorMessage.append(System.getProperty("line.separator"));
				}
				log.error("An error when accessing logs from elastic stac: " + errorMessage.toString());
				Error error = new Error();
				error.setCode("500");
				error.setMessage("An error when accessing logs from elastic stac: " + errorMessage.toString());
				return new ResponseEntity<Error>(error, HttpStatus.INTERNAL_SERVER_ERROR);
			}else {
				ByteArrayOutputStream result = new ByteArrayOutputStream();
				byte[] buffer = new byte[1024];
				 for (int length; (length = conn.getInputStream().read(buffer)) != -1; ) {
				     result.write(buffer, 0, length);
				 }
				log.trace(result.toString());
				//TODO create log result as json mappable object in domain model and map directly using annotations.
				//automatic parsing as below is currently failing...
				JSONObject logResult = new JSONObject(result.toString());
				JSONArray results =  logResult.getJSONObject("hits").getJSONArray("hits");
				results.forEach(item -> {
					JSONObject logEntryItem = (JSONObject) item;
					JSONObject logInfoFields =  logEntryItem.getJSONObject("fields");
					log.trace(logEntryItem.toString());
					log.trace(logInfoFields.toString());
					LogEntry logEntry = new LogEntry();
					String logLevel = logInfoFields.getJSONArray("log.level").getString(0);
					logEntry.setLevel(LevelEnum.valueOf(logLevel.toUpperCase()));
					logEntry.setMessage(logInfoFields.getJSONArray("message").getString(0));
					logEntry.setId(logEntryItem.getString("_id"));
					logEntries.addLogsItem(logEntry);
				});
			}
		}catch(Exception e) {
			log.error("An error when accessing logs from elastic stac: " + e.getMessage());
			StringBuilder builder = new StringBuilder(e.getMessage());
			for (StackTraceElement element : e.getStackTrace()) {
				builder.append(element.toString() + "\n");
			}
			log.error(builder.toString());
			Error error = new Error();
			error.setCode("500");
			error.setMessage("An error when accessing logs from elastic stac: " + builder.toString());
			return new ResponseEntity<Error>(error, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		//TODO implement logEntry and add to logEntries list and return result with pagination links
		return new ResponseEntity<LogEntries>(logEntries, HttpStatus.OK);

	}

	/**
	 * DELETE /jobs/{job_id} : Delete a batch job Deletes all data related to this
	 * job. Computations are stopped and computed results are deleted. This job
	 * won&#39;t generate additional costs for processing.
	 *
	 * @param jobId Unique job identifier. (required)
	 * @return The job has been successfully deleted. (status code 204) or The
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
	@Operation(summary = "Delete a batch job", operationId = "deleteJob", description = "Deletes all data related to this job. Computations are stopped and computed results are deleted. This job won't generate additional costs for processing.", tags = {
			"Data Processing", "Batch Jobs", })
	@ApiResponses(value = { @ApiResponse(responseCode = "204", description = "The job has been successfully deleted."),
			@ApiResponse(responseCode = "400", description = "The request can't be fulfilled due to an error on client-side, i.e. the request is invalid. The client should not repeat the request without modifications.  The response body SHOULD contain a JSON error object. MUST be any HTTP status code specified in [RFC 7231](https://tools.ietf.org/html/rfc7231#section-6.6). This request MUST respond with HTTP status codes 401 if authorization is required or 403 if the authorization failed or access is forbidden in general to the authenticated user. HTTP status code 404 should be used if the value of a path parameter is invalid.  See also: * [Error Handling](#section/API-Principles/Error-Handling) in the API in general. * [Common Error Codes](errors.json)"),
			@ApiResponse(responseCode = "500", description = "The request can't be fulfilled due to an error at the back-end. The error is never the client’s fault and therefore it is reasonable for the client to retry the exact same request that triggered this response.  The response body SHOULD contain a JSON error object. MUST be any HTTP status code specified in [RFC 7231](https://tools.ietf.org/html/rfc7231#section-6.6).  See also: * [Error Handling](#section/API-Principles/Error-Handling) in the API in general. * [Common Error Codes](errors.json)") })
	@RequestMapping(value = "/jobs/{job_id}", produces = { "application/json" }, method = RequestMethod.DELETE)
	public ResponseEntity<?> deleteJob(
			@Pattern(regexp = "^[\\w\\-\\.~]+$") @Parameter(description = "Unique job identifier.", required = true) @PathVariable("job_id") String jobId) {
		Job job = jobDAO.findOne(UUID.fromString(jobId));
		if (job != null) {
			BatchJobResult jobResult = resultDAO.findOne(UUID.fromString(jobId));
			if(jobResult != null) {
				log.debug("The job result " + jobId + " was detected.");
				File jobResults = new File(tmpDir + jobId);
				if(jobResults.exists()) {
					log.debug("Directory of job results has been found: " + jobResults.getAbsolutePath());
					for(File file: jobResults.listFiles()) {
						log.debug("The following result will be deleted: " + file.getName());
						file.delete();
					}
					jobResults.delete();
					log.debug("All persistent files have been successfully deleted for job with id: " + jobId);
				}
				resultDAO.delete(jobResult);
				log.debug("The job result " + jobId + " was successfully deleted.");
			}
			jobDAO.delete(job);
			log.debug("The job " + jobId + " was successfully deleted.");
			authzService.deleteProtectedResource(job);
			log.debug("The job " + jobId + " was successfully deleted from Keycloak.");
			return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
		} else {
			Error error = new Error();
			error.setCode("400");
			error.setMessage("The requested job " + jobId + " could not be found.");
			log.error("The requested job " + jobId + " could not be found.");
			return new ResponseEntity<Error>(error, HttpStatus.BAD_REQUEST);
		}

	}

	/**
	 * GET /jobs/{job_id} : Full metadata for a batch job Returns all information
	 * about a submitted batch job.
	 *
	 * @param jobId Unique job identifier. (required)
	 * @return Full job information. (status code 200) or The request can&#39;t be
	 *         fulfilled due to an error on client-side, i.e. the request is
	 *         invalid. The client should not repeat the request without
	 *         modifications. The response body SHOULD contain a JSON error object.
	 *         MUST be any HTTP status code specified in [RFC
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
	@Operation(summary = "Full metadata for a batch job", operationId = "describeJob", description = "Returns all information about a submitted batch job.", tags = {
			"Data Processing", "Batch Jobs", })
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Full job information."),
			@ApiResponse(responseCode = "400", description = "The request can't be fulfilled due to an error on client-side, i.e. the request is invalid. The client should not repeat the request without modifications.  The response body SHOULD contain a JSON error object. MUST be any HTTP status code specified in [RFC 7231](https://tools.ietf.org/html/rfc7231#section-6.6). This request MUST respond with HTTP status codes 401 if authorization is required or 403 if the authorization failed or access is forbidden in general to the authenticated user. HTTP status code 404 should be used if the value of a path parameter is invalid.  See also: * [Error Handling](#section/API-Principles/Error-Handling) in the API in general. * [Common Error Codes](errors.json)"),
			@ApiResponse(responseCode = "500", description = "The request can't be fulfilled due to an error at the back-end. The error is never the client’s fault and therefore it is reasonable for the client to retry the exact same request that triggered this response.  The response body SHOULD contain a JSON error object. MUST be any HTTP status code specified in [RFC 7231](https://tools.ietf.org/html/rfc7231#section-6.6).  See also: * [Error Handling](#section/API-Principles/Error-Handling) in the API in general. * [Common Error Codes](errors.json)") })
	@RequestMapping(value = "/jobs/{job_id}", produces = { "application/json" }, method = RequestMethod.GET)
	public ResponseEntity<?> describeJob(
			@Pattern(regexp = "^[\\w\\-\\.~]+$") @Parameter(description = "Unique job identifier.", required = true) @PathVariable("job_id") String jobId) {
		ThreadContext.put("jobid", jobId);
		Job job = jobDAO.findOne(UUID.fromString(jobId));
		if (job != null) {
			log.debug("The job " + jobId + " was successfully requested.");
			log.trace(job.toString());
			ThreadContext.clearMap();
			return new ResponseEntity<Job>(job, HttpStatus.OK);
		} else {
			Error error = new Error();
			error.setCode("400");
			error.setMessage("The requested job " + jobId + " could not be found.");
			ThreadContext.clearMap();
			return new ResponseEntity<Error>(error, HttpStatus.BAD_REQUEST);
		}
	}

	/**
	 * GET /jobs/{job_id}/estimate : Get an estimate for a batch job Clients can ask
	 * for an estimate for a batch job. Back-ends can decide to either calculate the
	 * duration, the costs, the size or a combination of them. This MUST be the
	 * upper limit of the incurring costs. Clients can be charged less than
	 * specified, but never more. Back-end providers MAY specify an expiry time for
	 * the estimate. Starting to process data afterwards MAY be charged at a higher
	 * cost. Costs MAY NOT include downloading costs. This can be indicated with the
	 * &#x60;downloads_included&#x60; flag.
	 *
	 * @param jobId Unique job identifier. (required)
	 * @return The estimated costs with regard to money, processing time and storage
	 *         capacity. At least one of &#x60;costs&#x60;, &#x60;duration&#x60; or
	 *         &#x60;size&#x60; MUST be provided. (status code 200) or The request
	 *         can&#39;t be fulfilled due to an error on client-side, i.e. the
	 *         request is invalid. The client should not repeat the request without
	 *         modifications. The response body SHOULD contain a JSON error object.
	 *         MUST be any HTTP status code specified in [RFC
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
	@Operation(summary = "Get an estimate for a batch job", operationId = "estimateJob", description = "Clients can ask for an estimate for a batch job. Back-ends can decide to either calculate the duration, the costs, the size or a combination of them. This MUST be the upper limit of the incurring costs. Clients can be charged less than specified, but never more. Back-end providers MAY specify an expiry time for the estimate. Starting to process data afterwards MAY be charged at a higher cost. Costs MAY NOT include downloading costs. This can be indicated with the `downloads_included` flag.", tags = {
			"Data Processing", "Batch Jobs", })
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "The estimated costs with regard to money, processing time and storage capacity. At least one of `costs`, `duration` or `size` MUST be provided."),
			@ApiResponse(responseCode = "400", description = "The request can't be fulfilled due to an error on client-side, i.e. the request is invalid. The client should not repeat the request without modifications.  The response body SHOULD contain a JSON error object. MUST be any HTTP status code specified in [RFC 7231](https://tools.ietf.org/html/rfc7231#section-6.6). This request MUST respond with HTTP status codes 401 if authorization is required or 403 if the authorization failed or access is forbidden in general to the authenticated user. HTTP status code 404 should be used if the value of a path parameter is invalid.  See also: * [Error Handling](#section/API-Principles/Error-Handling) in the API in general. * [Common Error Codes](errors.json)"),
			@ApiResponse(responseCode = "500", description = "The request can't be fulfilled due to an error at the back-end. The error is never the client’s fault and therefore it is reasonable for the client to retry the exact same request that triggered this response.  The response body SHOULD contain a JSON error object. MUST be any HTTP status code specified in [RFC 7231](https://tools.ietf.org/html/rfc7231#section-6.6).  See also: * [Error Handling](#section/API-Principles/Error-Handling) in the API in general. * [Common Error Codes](errors.json)") })
	@RequestMapping(value = "/jobs/{job_id}/estimate", produces = { "application/json" }, method = RequestMethod.GET)
	public ResponseEntity<BatchJobEstimate> estimateJob(
			@Pattern(regexp = "^[\\w\\-\\.~]+$") @Parameter(description = "Unique job identifier.", required = true) @PathVariable("job_id") String jobId) {
		getRequest().ifPresent(request -> {
			for (MediaType mediaType : MediaType.parseMediaTypes(request.getHeader("Accept"))) {
				if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
					String exampleString = "{ \"duration\" : \"P1Y2M10DT2H30M\", \"costs\" : 12.98, \"expires\" : \"2020-11-01T00:00:00Z\", \"size\" : 157286400, \"downloads_included\" : 5 }";
					ApiUtil.setExampleResponse(request, "application/json", exampleString);
					break;
				}
			}
		});
		return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

	}

	/**
	 * GET /jobs : List all batch jobs Requests to this endpoint will list all batch
	 * jobs submitted by a user. It is **strongly RECOMMENDED** to keep the response
	 * size small by omitting all optional non-scalar values from objects in
	 * &#x60;jobs&#x60; (i.e. the &#x60;process&#x60; property). To get the full
	 * metadata for a job clients MUST request &#x60;GET /jobs/{job_id}&#x60;.
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
	 * @return Array of job descriptions (status code 200) or The request can&#39;t
	 *         be fulfilled due to an error on client-side, i.e. the request is
	 *         invalid. The client should not repeat the request without
	 *         modifications. The response body SHOULD contain a JSON error object.
	 *         MUST be any HTTP status code specified in [RFC
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
	@Operation(summary = "List all batch jobs", operationId = "listJobs", description = "Requests to this endpoint will list all batch jobs submitted by a user.  It is **strongly RECOMMENDED** to keep the response size small by omitting all optional non-scalar values from objects in `jobs` (i.e. the `process` property). To get the full metadata for a job clients MUST request `GET /jobs/{job_id}`.", tags = {
			"Data Processing", "Batch Jobs", })
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Array of job descriptions"),
			@ApiResponse(responseCode = "400", description = "The request can't be fulfilled due to an error on client-side, i.e. the request is invalid. The client should not repeat the request without modifications.  The response body SHOULD contain a JSON error object. MUST be any HTTP status code specified in [RFC 7231](https://tools.ietf.org/html/rfc7231#section-6.6). This request MUST respond with HTTP status codes 401 if authorization is required or 403 if the authorization failed or access is forbidden in general to the authenticated user. HTTP status code 404 should be used if the value of a path parameter is invalid.  See also: * [Error Handling](#section/API-Principles/Error-Handling) in the API in general. * [Common Error Codes](errors.json)"),
			@ApiResponse(responseCode = "500", description = "The request can't be fulfilled due to an error at the back-end. The error is never the client’s fault and therefore it is reasonable for the client to retry the exact same request that triggered this response.  The response body SHOULD contain a JSON error object. MUST be any HTTP status code specified in [RFC 7231](https://tools.ietf.org/html/rfc7231#section-6.6).  See also: * [Error Handling](#section/API-Principles/Error-Handling) in the API in general. * [Common Error Codes](errors.json)") })
	@GetMapping(value = "/jobs", produces = { "application/json" })
	public ResponseEntity<?> listJobs(
			@Min(1) @Parameter(description = "This parameter enables pagination for the endpoint and specifies the maximum number of elements that arrays in the top-level object (e.g. jobs or log entries) are allowed to contain. The only exception is the `links` array, which MUST NOT be paginated as otherwise the pagination links may be missing ins responses. If the parameter is not provided or empty, all elements are returned.  Pagination is OPTIONAL and back-ends and clients may not support it. Therefore it MUST be implemented in a way that clients not supporting pagination get all resources regardless. Back-ends not supporting  pagination will return all resources.  If the response is paginated, the links array MUST be used to propagate the  links for pagination with pre-defined `rel` types. See the links array schema for supported `rel` types.  *Note:* Implementations can use all kind of pagination techniques, depending on what is supported best by their infrastructure. So it doesn't care whether it is page-based, offset-based or uses tokens for pagination. The clients will use whatever is specified in the links with the corresponding `rel` types.") @Valid @RequestParam(value = "limit", required = false) Integer limit, Principal principal) {
		BatchJobs batchJobs = new BatchJobs();
		//I added///
				AccessToken token = TokenUtil.getAccessToken(principal); 
				for (Job job : jobDAO.findWithOwner(token.getPreferredUsername())) {
					batchJobs.addJobsItem(job);
				
				}
//		for (Job job : jobDAO.findAll()) {
//			batchJobs.addJobsItem(job);
//			
//		}
		Link linkToJob = new Link();
		linkToJob.setTitle("next");
		linkToJob.setRel("next");
		try {
			linkToJob.setHref(new URI(openEOPublicEndpoint + "/jobs"));
		} catch (URISyntaxException e) {}
		batchJobs.addLinksItem(linkToJob);
		if (!batchJobs.getJobs().isEmpty()) {
			return new ResponseEntity<BatchJobs>(batchJobs, HttpStatus.OK);
		} else {
			Error error = new Error();
			error.setCode("400");
			error.setMessage("No jobs are available at the moment!");
			return new ResponseEntity<Error>(error, HttpStatus.BAD_REQUEST);
		}
	}

	/**
	 * GET /jobs/{job_id}/results : Download results for a completed batch job After
	 * finishing processing, this request will provide signed URLs to the processed
	 * files of the batch job with some additional metadata. The response is a [STAC
	 * Item (version
	 * 0.9.0)](https://github.com/radiantearth/stac-spec/tree/v0.9.0/item-spec) if
	 * it has spatial and temporal references included. URL signing is a way to
	 * protect files from unauthorized access with a key in the URL instead of HTTP
	 * header based authorization. The URL signing key is similar to a password and
	 * its inclusion in the URL allows to download files using simple GET requests
	 * supported by a wide range of programs, e.g. web browsers or download
	 * managers. Back-ends are responsible to generate the URL signing keys and to
	 * manage their appropriate expiration. The back-end MAY indicate an expiration
	 * time by setting the &#x60;expires&#x60; property. If processing has not
	 * finished yet requests to this endpoint MUST be rejected with openEO error
	 * &#x60;JobNotFinished&#x60;.
	 *
	 * @param jobId Unique job identifier. (required)
	 * @return Valid download links have been returned. The download links
	 *         doesn&#39;t necessarily need to be located under the API base url.
	 *         (status code 200) or The request can&#39;t be fulfilled as the batch
	 *         job failed. This request will deliver the last error message that was
	 *         produced by the batch job. This HTTP code MUST be sent only when the
	 *         job &#x60;status&#x60; is &#x60;error&#x60;. (status code 424) or The
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
	@Operation(summary = "Download results for a completed batch job", operationId = "listResults", description = "After finishing processing, this request will provide signed URLs to the processed files of the batch job with some additional metadata. The response is a [STAC Item (version 0.9.0)](https://github.com/radiantearth/stac-spec/tree/v0.9.0/item-spec) if it has spatial and temporal references included.  URL signing is a way to protect files from unauthorized access with a key in the URL instead of HTTP header based authorization. The URL signing key is similar to a password and its inclusion in the URL allows to download files using simple GET requests supported by a wide range of programs, e.g. web browsers or download managers. Back-ends are responsible to generate the URL signing keys and to manage their appropriate expiration. The back-end MAY indicate an expiration time by setting the `expires` property.  If processing has not finished yet requests to this endpoint MUST be rejected with openEO error `JobNotFinished`.", tags = {
			"Data Processing", "Batch Jobs", })
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Valid download links have been returned. The download links doesn't necessarily need to be located under the API base url."),
			@ApiResponse(responseCode = "424", description = "The request can't be fulfilled as the batch job failed. This request will deliver the last error message that was produced by the batch job.  This HTTP code MUST be sent only when the job `status` is `error`."),
			@ApiResponse(responseCode = "400", description = "The request can't be fulfilled due to an error on client-side, i.e. the request is invalid. The client should not repeat the request without modifications.  The response body SHOULD contain a JSON error object. MUST be any HTTP status code specified in [RFC 7231](https://tools.ietf.org/html/rfc7231#section-6.6). This request MUST respond with HTTP status codes 401 if authorization is required or 403 if the authorization failed or access is forbidden in general to the authenticated user. HTTP status code 404 should be used if the value of a path parameter is invalid.  See also: * [Error Handling](#section/API-Principles/Error-Handling) in the API in general. * [Common Error Codes](errors.json)"),
			@ApiResponse(responseCode = "500", description = "The request can't be fulfilled due to an error at the back-end. The error is never the client’s fault and therefore it is reasonable for the client to retry the exact same request that triggered this response.  The response body SHOULD contain a JSON error object. MUST be any HTTP status code specified in [RFC 7231](https://tools.ietf.org/html/rfc7231#section-6.6).  See also: * [Error Handling](#section/API-Principles/Error-Handling) in the API in general. * [Common Error Codes](errors.json)") })
	@RequestMapping(value = "/jobs/{job_id}/results", produces = { "application/json",
			"application/geo+json" }, method = RequestMethod.GET)
	public ResponseEntity<?> listResults(
			@Pattern(regexp = "^[\\w\\-\\.~]+$") @Parameter(description = "Unique job identifier.", required = true) @PathVariable("job_id") String jobId) {
		BatchJobResult result = resultDAO.findOne(UUID.fromString(jobId));
		if (result != null) {
			log.trace(result.toString());
			return new ResponseEntity<BatchJobResult>(result, HttpStatus.OK);
		} else {
			Error error = new Error();
			error.setCode("400");
			error.setMessage("The requested job " + jobId + " could not be found.");
			return new ResponseEntity<Error>(error, HttpStatus.BAD_REQUEST);
		}

	}

	@Operation(summary = "Download data for given file", operationId = "downloadAsset", description = "Download asset as a result from a successfully executed process graph.", tags = {
			"Data Access", })
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Result data in the requested output format"),
			@ApiResponse(responseCode = "400", description = "The request can't be fulfilled due to an error on client-side, i.e. the request is invalid. The client should not repeat the request without modifications.  The response body SHOULD contain a JSON error object. MUST be any HTTP status code specified in [RFC 7231](https://tools.ietf.org/html/rfc7231#section-6.6). This request MUST respond with HTTP status codes 401 if authorization is required or 403 if the authorization failed or access is forbidden in general to the authenticated user. HTTP status code 404 should be used if the value of a path parameter is invalid.  See also: * [Error Handling](#section/API-Principles/Error-Handling) in the API in general. * [Common Error Codes](errors.json)"),
			@ApiResponse(responseCode = "500", description = "The request can't be fulfilled due to an error at the back-end. The error is never the client’s fault and therefore it is reasonable for the client to retry the exact same request that triggered this response.  The response body SHOULD contain a JSON error object. MUST be any HTTP status code specified in [RFC 7231](https://tools.ietf.org/html/rfc7231#section-6.6).  See also: * [Error Handling](#section/API-Principles/Error-Handling) in the API in general. * [Common Error Codes](errors.json)") })
	@GetMapping(value = "/download/{job_id}/{file_name}", produces = { "*" })
	public ResponseEntity<?> downloadResult(
			@Parameter(description = "Id of job that has created the result", required = true) @PathVariable("job_id") String jobID,
			@Parameter(description = "name of file of result", required = true) @PathVariable("file_name") String fileName) {
		ThreadContext.put("jobid", jobID);
		byte[] response = null;
		log.debug("job-id: " + jobID);
		log.debug("file-name: " + fileName);
		try {
			String mime = ConvenienceHelper.getMimeTypeFromRasName(fileName.substring(fileName.indexOf(".") + 1));
			log.debug("File download was requested:" + fileName + " of mime type: " + mime);
			File userFile = new File(tmpDir + jobID + "/" + fileName);
			log.debug(userFile.getAbsolutePath());
			response = IOUtils.toByteArray(new FileInputStream(userFile));
			log.debug("File found and converted in bytes for download");
			//Content-Disposition: inline; filename="myfile.txt"
			ThreadContext.clearMap();
			return ResponseEntity.ok().header("Content-Disposition", "inline; filename=\"" + fileName + "\"").contentType(MediaType.parseMediaType(mime)).body(response);
		} catch (FileNotFoundException e) {
			log.error("File not found:" + fileName);
			StringBuilder builder = new StringBuilder();
			for (StackTraceElement element : e.getStackTrace()) {
				builder.append(element.toString() + "\n");
			}
			log.error(builder.toString());
			Error error = new Error();
			error.setCode("400");
			error.setMessage("The requested file " + fileName + " could not be found.");
			ThreadContext.clearMap();
			return new ResponseEntity<Error>(error, HttpStatus.BAD_REQUEST);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			log.error("IOEXception error");
			StringBuilder builder = new StringBuilder();
			for (StackTraceElement element : e.getStackTrace()) {
				builder.append(element.toString() + "\n");
			}
			log.error(builder.toString());
			Error error = new Error();
			error.setCode("500");
			error.setMessage("IOEXception error " + builder.toString());
			ThreadContext.clearMap();
			return new ResponseEntity<Error>(error, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * POST /jobs/{job_id}/results : Start processing a batch job Adds a batch job
	 * to the processing queue to compute the results. The result will be stored in
	 * the format specified in the process graph. To specify the format use a
	 * process such as &#x60;save_result&#x60;. This endpoint has no effect if the
	 * job status is already &#x60;queued&#x60; or &#x60;running&#x60;. In
	 * particular, it doesn&#39;t restart a running job. Processing MUST be canceled
	 * before to restart it. The job status is set to &#x60;queued&#x60;, if
	 * processing doesn&#39;t start instantly. * Once the processing starts the
	 * status is set to &#x60;running&#x60;. * Once the data is available to
	 * download the status is set to &#x60;finished&#x60;. * Whenever an error
	 * occurs during processing, the status must be set to &#x60;error&#x60;.
	 *
	 * @param jobId Unique job identifier. (required)
	 * @return The creation of the resource has been queued successfully. (status
	 *         code 202) or The request can&#39;t be fulfilled due to an error on
	 *         client-side, i.e. the request is invalid. The client should not
	 *         repeat the request without modifications. The response body SHOULD
	 *         contain a JSON error object. MUST be any HTTP status code specified
	 *         in [RFC 7231](https://tools.ietf.org/html/rfc7231#section-6.6). This
	 *         request MUST respond with HTTP status codes 401 if authorization is
	 *         required or 403 if the authorization failed or access is forbidden in
	 *         general to the authenticated user. HTTP status code 404 should be
	 *         used if the value of a path parameter is invalid. See also: * [Error
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
	@Operation(summary = "Start processing a batch job", operationId = "startJob", description = "Adds a batch job to the processing queue to compute the results.  The result will be stored in the format specified in the process graph. To specify the format use a process such as `save_result`.  This endpoint has no effect if the job status is already `queued` or `running`. In particular, it doesn't restart a running job. Processing MUST be canceled before to restart it.  The job status is set to `queued`, if processing doesn't start instantly. * Once the processing starts the status is set to `running`.   * Once the data is available to download the status is set to `finished`.      * Whenever an error occurs during processing, the status must be set to `error`.", tags = {
			"Data Processing", "Batch Jobs", })
	@ApiResponses(value = {
			@ApiResponse(responseCode = "202", description = "The creation of the resource has been queued successfully."),
			@ApiResponse(responseCode = "400", description = "The request can't be fulfilled due to an error on client-side, i.e. the request is invalid. The client should not repeat the request without modifications.  The response body SHOULD contain a JSON error object. MUST be any HTTP status code specified in [RFC 7231](https://tools.ietf.org/html/rfc7231#section-6.6). This request MUST respond with HTTP status codes 401 if authorization is required or 403 if the authorization failed or access is forbidden in general to the authenticated user. HTTP status code 404 should be used if the value of a path parameter is invalid.  See also: * [Error Handling](#section/API-Principles/Error-Handling) in the API in general. * [Common Error Codes](errors.json)"),
			@ApiResponse(responseCode = "500", description = "The request can't be fulfilled due to an error at the back-end. The error is never the client’s fault and therefore it is reasonable for the client to retry the exact same request that triggered this response.  The response body SHOULD contain a JSON error object. MUST be any HTTP status code specified in [RFC 7231](https://tools.ietf.org/html/rfc7231#section-6.6).  See also: * [Error Handling](#section/API-Principles/Error-Handling) in the API in general. * [Common Error Codes](errors.json)") })
	@RequestMapping(value = "/jobs/{job_id}/results", produces = { "application/json" }, method = RequestMethod.POST)
	public ResponseEntity<?> startJob(
			@Pattern(regexp = "^[\\w\\-\\.~]+$") @Parameter(description = "Unique job identifier.", required = true) @PathVariable("job_id") String jobId) {
		ThreadContext.put("jobid", jobId);
		Job job = jobDAO.findOne(UUID.fromString(jobId));
		if (job != null) {
			if (job.getStatus() == JobStates.FINISHED) {
				Error error = new Error();
				error.setCode("400");
				error.setMessage("The requested job " + jobId + " has been finished and can't be restarted. Please create a new job.");
				log.error(error.getMessage());
				ThreadContext.clearMap();
				return new ResponseEntity<Error>(error, HttpStatus.BAD_REQUEST);
			}
			if (job.getStatus() == JobStates.QUEUED)  {
				Error error = new Error();
				error.setCode("400");
				error.setMessage("The requested job " + jobId + " is queued and can't be restarted before finishing.");
				log.error(error.getMessage());
				ThreadContext.clearMap();
				return new ResponseEntity<Error>(error, HttpStatus.BAD_REQUEST);
			}
			if (job.getStatus() == JobStates.RUNNING)  {
				Error error = new Error();
				error.setCode("400");
				error.setMessage("The requested job " + jobId + " is running and can't be restarted before finishing.");
				log.error(error.getMessage());
				ThreadContext.clearMap();
				return new ResponseEntity<Error>(error, HttpStatus.BAD_REQUEST);
			}
			job.setStatus(JobStates.QUEUED);
			job.setUpdated(OffsetDateTime.now());
			jobDAO.update(job);
			this.fireJobQueuedEvent(job.getId());
			ThreadContext.clearMap();
			return new ResponseEntity<Job>(HttpStatus.ACCEPTED);
		} else {
			Error error = new Error();
			error.setCode("400");
			error.setMessage("The requested job " + jobId + " could not be found.");
			log.error("The requested job " + jobId + " could not be found.");
			ThreadContext.clearMap();
			return new ResponseEntity<Error>(error, HttpStatus.BAD_REQUEST);
		}

	}

	/**
	 * DELETE /jobs/{job_id}/results : Cancel processing a batch job Cancels all
	 * related computations for this job at the back-end. It will stop generating
	 * additional costs for processing. A subset of processed results may be
	 * available for downloading depending on the state of the job as it was
	 * canceled. Finished results MUST NOT be deleted until the job is deleted or
	 * job processing is started again. This endpoint only has an effect if the job
	 * status is &#x60;queued&#x60; or &#x60;running&#x60;. The job status is set to
	 * &#x60;canceled&#x60; if the status was &#x60;running&#x60; beforehand and
	 * partial or preliminary results are available to be downloaded. Otherwise the
	 * status is set to &#x60;created&#x60;.
	 *
	 * @param jobId Unique job identifier. (required)
	 * @return Processing the job has been successfully canceled. (status code 204)
	 *         or The request can&#39;t be fulfilled due to an error on client-side,
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
	@Operation(summary = "Cancel processing a batch job", operationId = "stopJob", description = "Cancels all related computations for this job at the back-end. It will stop generating additional costs for processing.  A subset of processed results may be available for downloading depending on the state of the job as it was canceled. Finished results MUST NOT be deleted until the job is deleted or job processing is started again.  This endpoint only has an effect if the job status is `queued` or `running`.  The job status is set to `canceled` if the status was `running` beforehand and partial or preliminary results are available to be downloaded. Otherwise the status is set to `created`. ", tags = {
			"Data Processing", "Batch Jobs", })
	@ApiResponses(value = {
			@ApiResponse(responseCode = "204", description = "Processing the job has been successfully canceled."),
			@ApiResponse(responseCode = "400", description = "The request can't be fulfilled due to an error on client-side, i.e. the request is invalid. The client should not repeat the request without modifications.  The response body SHOULD contain a JSON error object. MUST be any HTTP status code specified in [RFC 7231](https://tools.ietf.org/html/rfc7231#section-6.6). This request MUST respond with HTTP status codes 401 if authorization is required or 403 if the authorization failed or access is forbidden in general to the authenticated user. HTTP status code 404 should be used if the value of a path parameter is invalid.  See also: * [Error Handling](#section/API-Principles/Error-Handling) in the API in general. * [Common Error Codes](errors.json)"),
			@ApiResponse(responseCode = "500", description = "The request can't be fulfilled due to an error at the back-end. The error is never the client’s fault and therefore it is reasonable for the client to retry the exact same request that triggered this response.  The response body SHOULD contain a JSON error object. MUST be any HTTP status code specified in [RFC 7231](https://tools.ietf.org/html/rfc7231#section-6.6).  See also: * [Error Handling](#section/API-Principles/Error-Handling) in the API in general. * [Common Error Codes](errors.json)") })
	@RequestMapping(value = "/jobs/{job_id}/results", produces = { "application/json" }, method = RequestMethod.DELETE)
	public ResponseEntity<Void> stopJob(
			@Pattern(regexp = "^[\\w\\-\\.~]+$") @Parameter(description = "Unique job identifier.", required = true) @PathVariable("job_id") String jobId) {
		return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

	}

	/**
	 * PATCH /jobs/{job_id} : Modify a batch job Modifies an existing job at the
	 * back-end but maintains the identifier. Changes can be grouped in a single
	 * request. Jobs can only be modified when the job is not queued or running.
	 * Otherwise requests to this endpoint MUST be rejected with openEO error
	 * &#x60;JobLocked&#x60;.
	 *
	 * @param jobId                 Unique job identifier. (required)
	 * @param updateBatchJobRequest (required)
	 * @return Changes to the job applied successfully. (status code 204) or The
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
	@Operation(summary = "Modify a batch job", operationId = "updateJob", description = "Modifies an existing job at the back-end but maintains the identifier. Changes can be grouped in a single request.  Jobs can only be modified when the job is not queued or running. Otherwise requests to this endpoint MUST be rejected with openEO error `JobLocked`.", tags = {
			"Data Processing", "Batch Jobs", })
	@ApiResponses(value = {
			@ApiResponse(responseCode = "204", description = "Changes to the job applied successfully."),
			@ApiResponse(responseCode = "400", description = "The request can't be fulfilled due to an error on client-side, i.e. the request is invalid. The client should not repeat the request without modifications.  The response body SHOULD contain a JSON error object. MUST be any HTTP status code specified in [RFC 7231](https://tools.ietf.org/html/rfc7231#section-6.6). This request MUST respond with HTTP status codes 401 if authorization is required or 403 if the authorization failed or access is forbidden in general to the authenticated user. HTTP status code 404 should be used if the value of a path parameter is invalid.  See also: * [Error Handling](#section/API-Principles/Error-Handling) in the API in general. * [Common Error Codes](errors.json)"),
			@ApiResponse(responseCode = "500", description = "The request can't be fulfilled due to an error at the back-end. The error is never the client’s fault and therefore it is reasonable for the client to retry the exact same request that triggered this response.  The response body SHOULD contain a JSON error object. MUST be any HTTP status code specified in [RFC 7231](https://tools.ietf.org/html/rfc7231#section-6.6).  See also: * [Error Handling](#section/API-Principles/Error-Handling) in the API in general. * [Common Error Codes](errors.json)") })
	@RequestMapping(value = "/jobs/{job_id}", produces = { "application/json" }, consumes = {
			"application/json" }, method = RequestMethod.PATCH)
	public ResponseEntity<?> updateJob(
			@Pattern(regexp = "^[\\w\\-\\.~]+$") @Parameter(description = "Unique job identifier.", required = true) @PathVariable("job_id") String jobId,
			@Parameter(description = "", required = true) @Valid @RequestBody Job updateBatchJobRequest) {
		ThreadContext.put("jobid", jobId);
		Job job = jobDAO.findOne(UUID.fromString(jobId));
		if (job != null) {
			if (job.getStatus()==JobStates.QUEUED || job.getStatus()==JobStates.RUNNING) {
					Error error = new Error();
					error.setCode("400");
					error.setMessage("JobLocked: The requested job " + jobId + " is queued or running, not possible to update it now.");
					ThreadContext.clearMap();
					return new ResponseEntity<Error>(error, HttpStatus.FORBIDDEN);
				}
			if(updateBatchJobRequest.getEngine() != null) {
				job.setEngine(updateBatchJobRequest.getEngine());
			}
			if(updateBatchJobRequest.getProcess() != null) {
				job.setProcess(updateBatchJobRequest.getProcess());
			}
			if(updateBatchJobRequest.getBudget() != null) {
				job.setBudget(updateBatchJobRequest.getBudget());
			}
			if(updateBatchJobRequest.getPlan() != null) {
				job.setPlan(updateBatchJobRequest.getPlan());
			}
			if(updateBatchJobRequest.getDescription() != null) {
				job.setDescription(updateBatchJobRequest.getDescription());
			}
			if(updateBatchJobRequest.getTitle() != null) {
				job.setTitle(updateBatchJobRequest.getTitle());
			}
			job.setUpdated(OffsetDateTime.now());
			jobDAO.update(job);
			ThreadContext.clearMap();
			return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
		} else {
			Error error = new Error();
			error.setCode("400");
			error.setMessage("The requested job " + jobId + " could not be found.");
			ThreadContext.clearMap();
			return new ResponseEntity<Error>(error, HttpStatus.BAD_REQUEST);
		}

	}

	public void addJobListener(JobEventListener listener) {
		try {
			listenerList.add(JobEventListener.class, listener);
			log.debug("JobEventListener successfully added to listenerList!");
		} catch (Exception e) {
			log.error("No Event available: " + e.getMessage());
			StringBuilder builder = new StringBuilder();
			for (StackTraceElement element : e.getStackTrace()) {
				builder.append(element.toString() + "\n");
			}
			log.error(builder.toString());
		}
	}

	private void fireJobQueuedEvent(UUID jobId) {
		ThreadContext.put("jobid", jobId.toString());
		Object[] listeners = listenerList.getListenerList();
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == JobEventListener.class) {
				JobEvent jobEvent = new JobEvent(this, jobId);
				((JobEventListener) listeners[i + 1]).jobQueued(jobEvent);
			}
		}
		log.debug("Job Queue Event fired for job: " + jobId);
		ThreadContext.clearMap();
	}

}
