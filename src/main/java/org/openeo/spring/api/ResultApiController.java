package org.openeo.spring.api;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Optional;

import javax.validation.Valid;

import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.openeo.spring.model.Error;
import org.openeo.spring.model.Job;
import org.openeo.wcps.ConvenienceHelper;
import org.openeo.wcps.WCPSQueryFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.NativeWebRequest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import springfox.documentation.spring.web.scanners.MediaTypeReader;

@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2020-07-02T08:45:00.334+02:00[Europe/Rome]")
@Component
@RestController
@RequestMapping("${openapi.openEO.base-path:}")
public class ResultApiController implements ResultApi {

	private final NativeWebRequest request;

	private final Logger log = LogManager.getLogger(ResultApiController.class);

	@Value("${org.openeo.wcps.endpoint}")
	private String wcpsEndpoint;

	@Value("${org.openeo.endpoint}")
	private String openEOEndpoint;

	@Value("${org.openeo.odc.endpoint}")
	private String odcEndpoint;

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
	public ResponseEntity computeResult(@Parameter(description = "", required = true) @Valid @RequestBody Job job) {
		String backend = job.getProcess().getDescription();
		JSONObject processGraphJSON = (JSONObject) job.getProcess().getProcessGraph();
		if (backend != null && backend.contains("ODC")) {
			JSONObject process = new JSONObject();
			process.put("id", "ODC-graph");
			process.put("process_graph", processGraphJSON);
			URL url;
			try {
				url = new URL(odcEndpoint + "/graph/" + "test");
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				conn.setRequestMethod("POST");
				conn.setRequestProperty("Content-Type", "application/json; utf-8");
				conn.setDoOutput(true);
				log.debug("graph object send to ODC server: " + process.toString());
				try (OutputStream os = conn.getOutputStream()) {
					byte[] requestBody = process.toString().getBytes("utf-8");
					os.write(requestBody, 0, requestBody.length);
				}
				InputStream is = conn.getInputStream();
				String mime = URLConnection.guessContentTypeFromStream(is);
				log.debug("Mime type on ODC response guessed to be: " + mime);
				byte[] response = IOUtils.toByteArray(is);
				log.info("Job successfully executed: " + job.toString());
				return ResponseEntity.ok().contentType(MediaType.parseMediaType(mime)).body(response);
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
		} else {
			WCPSQueryFactory wcpsFactory = null;
			wcpsFactory = new WCPSQueryFactory(processGraphJSON, openEOEndpoint, wcpsEndpoint);
			URL url;
			try {
				url = new URL(wcpsEndpoint + "?SERVICE=WCS" + "&VERSION=2.0.1" + "&REQUEST=ProcessCoverages" + "&QUERY="
						+ URLEncoder.encode(wcpsFactory.getWCPSString(), "UTF-8").replace("+", "%20"));
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				conn.setRequestMethod("GET");
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
		return new ResponseEntity<Error>(error, HttpStatus.INTERNAL_SERVER_ERROR);

	}

}
