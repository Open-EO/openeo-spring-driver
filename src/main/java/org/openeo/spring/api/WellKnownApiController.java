package org.openeo.spring.api;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

import org.openeo.spring.model.APIInstance;
import org.openeo.spring.model.Error;
import org.openeo.spring.model.WellKnownDiscovery;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.NativeWebRequest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2020-07-02T08:45:00.334+02:00[Europe/Rome]")
@RestController
@RequestMapping("${openapi.openEO.base-path:}")
public class WellKnownApiController implements WellKnownApi {

	private final NativeWebRequest request;

	@Value("${org.openeo.public.endpoint}")
	private String openEOPublicEndpoint;

	@org.springframework.beans.factory.annotation.Autowired
	public WellKnownApiController(NativeWebRequest request) {
		this.request = request;
	}

	@Override
	public Optional<NativeWebRequest> getRequest() {
		return Optional.ofNullable(request);
	}

	@Operation(summary = "Supported openEO versions", operationId = "connect", description = "Well-Known URI (see [RFC 5785](https://tools.ietf.org/html/rfc5785)) for openEO, listing all implemented openEO versions supported by the service provider.  This allows a client to easily identify the most recent openEO implementation it supports. By default, a client SHOULD connect to the most recent production-ready version it supports. If not available, the most recent supported version of *all* versions SHOULD be connected to. Clients MAY let users choose to connect to versions that are not production-ready or outdated. The most recent version is determined by comparing the version numbers according to rules from [Semantic Versioning](https://semver.org/), especially [§11](https://semver.org/#spec-item-11). Any pair of API versions in this list MUST NOT be equal according to Semantic Versioning.  The Well-Known URI is the entry point for clients and users, so make sure it is permanent and easy to use and remember. Clients MUST NOT require the well-known path (`./well-known/openeo`) in the URL that is specified by a user to connect to the back-end. A client MUST request `https://example.com/.well-known/openeo` if a user tries to connect to `https://example.com`. If the request to the well-known URI fails, the client SHOULD try to request the capabilities at `/` from `https://example.com`.  **This URI MUST NOT be versioned as the other endpoints.** If your API is available at `https://example.com/api/v1.0`, the Well-Known URI SHOULD be located at `https://example.com/.well-known/openeo` and the URI users connect to SHOULD be `https://example.com`.  Clients MAY get additional information (e.g. title or description) about a back-end from the most recent version that has the `production` flag set to `true`.", tags = {
			"Capabilities", })
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "List of all available API instances, each with URL and the implemented openEO API version."),
			@ApiResponse(responseCode = "400", description = "The request can't be fulfilled due to an error on client-side, i.e. the request is invalid. The client should not repeat the request without modifications.  The response body SHOULD contain a JSON error object. MUST be any HTTP status code specified in [RFC 7231](https://tools.ietf.org/html/rfc7231#section-6.6). This request usually does not respond with HTTP status codes 401 and 403 due to missing authorization. HTTP status code 404 should be used if the value of a path parameter is invalid.  See also: * [Error Handling](#section/API-Principles/Error-Handling) in the API in general. * [Common Error Codes](errors.json)"),
			@ApiResponse(responseCode = "500", description = "The request can't be fulfilled due to an error at the back-end. The error is never the client’s fault and therefore it is reasonable for the client to retry the exact same request that triggered this response.  The response body SHOULD contain a JSON error object. MUST be any HTTP status code specified in [RFC 7231](https://tools.ietf.org/html/rfc7231#section-6.6).  See also: * [Error Handling](#section/API-Principles/Error-Handling) in the API in general. * [Common Error Codes](errors.json)") })
	@RequestMapping(value = "/.well-known/openeo", produces = { "application/json" }, method = RequestMethod.GET)
	public ResponseEntity connect() {
		WellKnownDiscovery wellKnownDiscovery = new WellKnownDiscovery();

		APIInstance apiInstance = new APIInstance();
		apiInstance.apiVersion("1.0.0");
		apiInstance.setProduction(false);
		try {
			apiInstance.setUrl(new URI(openEOPublicEndpoint));
		} catch (URISyntaxException e) {
			Error error = new Error();
			error.setCode("500");
			error.setMessage("The api endpoint uri was not correctly set: " + e.getMessage());
			return new ResponseEntity<Error>(error, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		wellKnownDiscovery.addVersionsItem(apiInstance);

		return new ResponseEntity<WellKnownDiscovery>(wellKnownDiscovery, HttpStatus.OK);

	}

}
