package org.openeo.spring.api;

import java.util.Optional;
import java.util.UUID;

import javax.validation.Valid;
import javax.validation.constraints.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.SessionFactory;
import org.openeo.spring.dao.ProcessDAO;
import org.openeo.spring.model.Process;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.NativeWebRequest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2020-07-02T08:45:00.334+02:00[Europe/Rome]")
@RestController
@RequestMapping("${openapi.openEO.base-path:}")
public class ProcessGraphsApiController implements ProcessGraphsApi {

	private final NativeWebRequest request;

	private final Logger log = LogManager.getLogger(ProcessGraphsApiController.class);

	@Autowired
	private SessionFactory sessionFactory;

	ProcessDAO processDAO;

	@Autowired
	public void setDao(ProcessDAO injectedDAO) {
		processDAO = injectedDAO;
	}

	@org.springframework.beans.factory.annotation.Autowired
	public ProcessGraphsApiController(NativeWebRequest request) {
		this.request = request;
	}

	@Override
	public Optional<NativeWebRequest> getRequest() {
		return Optional.ofNullable(request);
	}

	@Operation(summary = "Store a user-defined process", operationId = "storeCustomProcess", description = "Stores a provided user-defined process with process graph that can be reused in other processes. If a process with the specified  `process_graph_id` exists, the process is fully replaced. The id can't be changed for stored user-defined processes.  The id must be unique for the authenticated user, including all pre-defined processes by the back-end.  Partially updating user-defined processes is not supported.  To simplify exchanging user-defined processes, the property `id` can be part of the request body. If the values don't match, the value for `id` gets replaced with the value from the `process_graph_id` parameter in the path.", tags = {
			"User-Defined Processes" })
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "The user-defined process has been stored successfully."),
			@ApiResponse(responseCode = "400", description = "The request can't be fulfilled due to an error on client-side, i.e. the request is invalid. The client should not repeat the request without modifications.  The response body SHOULD contain a JSON error object. MUST be any HTTP status code specified in [RFC 7231](https://tools.ietf.org/html/rfc7231#section-6.6). This request MUST respond with HTTP status codes 401 if authorization is required or 403 if the authorization failed or access is forbidden in general to the authenticated user. HTTP status code 404 should be used if the value of a path parameter is invalid.  See also: * [Error Handling](#section/API-Principles/Error-Handling) in the API in general. * [Common Error Codes](errors.json)"),
			@ApiResponse(responseCode = "500", description = "The request can't be fulfilled due to an error at the back-end. The error is never the client’s fault and therefore it is reasonable for the client to retry the exact same request that triggered this response.  The response body SHOULD contain a JSON error object. MUST be any HTTP status code specified in [RFC 7231](https://tools.ietf.org/html/rfc7231#section-6.6).  See also: * [Error Handling](#section/API-Principles/Error-Handling) in the API in general. * [Common Error Codes](errors.json)") })
	@PutMapping(value = "/process_graphs/{process_graph_id}", produces = { "application/json" }, consumes = { "application/json" })
	@Override
	public ResponseEntity storeCustomProcess(
			@Pattern(regexp = "^\\w+$") @Parameter(description = "Unique identifier for a user-defined process.", required = true) @PathVariable("process_graph_id") String processGraphId,
			@Parameter(description = "Specifies the process graph with its meta data.", required = true) @Valid @RequestBody Process processWithGraph) {
		if (processGraphId == null) {
			processGraphId = UUID.randomUUID().toString();
		}
		processWithGraph.setId(processGraphId);
		log.debug("Process attached: " + processWithGraph.toString());
		this.processDAO.save(processWithGraph);
		return new ResponseEntity<Process>(processWithGraph, HttpStatus.OK);

	}

}
