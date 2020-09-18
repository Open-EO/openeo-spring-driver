package org.openeo.spring.api;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

import org.openeo.spring.model.Capabilities;
import org.openeo.spring.model.Endpoint;
import org.openeo.spring.model.Endpoint.MethodsEnum;
import org.openeo.spring.model.Link;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.NativeWebRequest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2020-07-02T08:45:00.334+02:00[Europe/Rome]")
@Component
@RestController
@RequestMapping("${openapi.openEO.base-path:}")
public class DefaultApiController implements DefaultApi {

	private final NativeWebRequest request;

	@Value("${org.openeo.endpoint}")
	private String openEOEndpoint;

	@org.springframework.beans.factory.annotation.Autowired
	public DefaultApiController(NativeWebRequest request) {
		this.request = request;
	}

	@Override
	public Optional<NativeWebRequest> getRequest() {
		return Optional.ofNullable(request);
	}

	@Operation(summary = "Information about the back-end", operationId = "capabilities", description = "Returns general information about the back-end, including which version and endpoints of the openEO API are supported. May also include billing information.", tags = {
			"Capabilities", })
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Information about the API version and supported endpoints / features."),
			@ApiResponse(responseCode = "400", description = "The request can't be fulfilled due to an error on client-side, i.e. the request is invalid. The client should not repeat the request without modifications.  The response body SHOULD contain a JSON error object. MUST be any HTTP status code specified in [RFC 7231](https://tools.ietf.org/html/rfc7231#section-6.6). This request usually does not respond with HTTP status codes 401 and 403 due to missing authorization. HTTP status code 404 should be used if the value of a path parameter is invalid.  See also: * [Error Handling](#section/API-Principles/Error-Handling) in the API in general. * [Common Error Codes](errors.json)"),
			@ApiResponse(responseCode = "500", description = "The request can't be fulfilled due to an error at the back-end. The error is never the client’s fault and therefore it is reasonable for the client to retry the exact same request that triggered this response.  The response body SHOULD contain a JSON error object. MUST be any HTTP status code specified in [RFC 7231](https://tools.ietf.org/html/rfc7231#section-6.6).  See also: * [Error Handling](#section/API-Principles/Error-Handling) in the API in general. * [Common Error Codes](errors.json)") })
	@RequestMapping(value = "/", produces = { "application/json" }, method = RequestMethod.GET)
	public ResponseEntity<Capabilities> capabilities() {

		Capabilities capabilities = new Capabilities();

		capabilities.apiVersion("1.0.0");
		capabilities.backendVersion("Spring-Dev-1.0.0");
		capabilities.description(
				"The Eurac Research backend provides EO data available for processing using OGC WC(P)S and the open data cube");
		capabilities.title("Eurac Research - openEO - backend");
		capabilities.setId("Eurac_openEO");
		capabilities.setStacVersion("0.9.0");

		Endpoint capabilitiesEndPoint = new Endpoint();
		capabilitiesEndPoint.setPath("/");
		capabilitiesEndPoint.addMethodsItem(MethodsEnum.GET);
		capabilities.addEndpointsItem(capabilitiesEndPoint);

		Endpoint wellKnownEndPoint = new Endpoint();
		wellKnownEndPoint.setPath("/.well-known/openeo");
		wellKnownEndPoint.addMethodsItem(MethodsEnum.GET);
		capabilities.addEndpointsItem(wellKnownEndPoint);

		Endpoint resultEndPoint = new Endpoint();
		resultEndPoint.setPath("/result");
		resultEndPoint.addMethodsItem(MethodsEnum.POST);
		capabilities.addEndpointsItem(resultEndPoint);

		Endpoint collectionsEndpoint = new Endpoint();
		collectionsEndpoint.setPath("/collections");
		collectionsEndpoint.addMethodsItem(MethodsEnum.GET);
		capabilities.addEndpointsItem(collectionsEndpoint);

		Endpoint collectionIDEndpoint = new Endpoint();
		collectionIDEndpoint.setPath("/collections/{collection-id}");
		collectionIDEndpoint.addMethodsItem(MethodsEnum.GET);
		capabilities.addEndpointsItem(collectionIDEndpoint);

		Endpoint processesEndpoint = new Endpoint();
		processesEndpoint.setPath("/processes");
		processesEndpoint.addMethodsItem(MethodsEnum.GET);
		capabilities.addEndpointsItem(processesEndpoint);

		Link operatorUrl = new Link();
		try {
			operatorUrl.setHref(new URI("http://www.eurac.edu"));
		} catch (URISyntaxException e) {
		}
		operatorUrl.setTitle("Homepage of the service provider");
		operatorUrl.setType("text/html");
		operatorUrl.setRel("Eurac Research");
		capabilities.addLinksItem(operatorUrl);

		Link openEOUrl = new Link();
		try {
			openEOUrl.setHref(new URI(this.openEOEndpoint));
		} catch (URISyntaxException e) {
		}
		openEOUrl.setTitle("url to openeo api service");
		openEOUrl.setType("text/html");
		openEOUrl.setRel("self");
		capabilities.addLinksItem(openEOUrl);

		return new ResponseEntity<Capabilities>(capabilities, HttpStatus.OK);

	}

}
