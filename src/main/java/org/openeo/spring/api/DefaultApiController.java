package org.openeo.spring.api;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openeo.spring.conformance.OgcLinkRelation;
import org.openeo.spring.model.Billing;
import org.openeo.spring.model.BillingPlan;
import org.openeo.spring.model.Capabilities;
import org.openeo.spring.model.Endpoint;
import org.openeo.spring.model.Endpoint.MethodsEnum;
import org.openeo.spring.model.Link;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.ConfigurableEnvironment;
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

	public static final String IMPLEMENTED_OPENEO_API_VERSION = "1.0.0"; // TODO 1.1.0 ?
	public static final String IMPLEMENTED_STAC_VERSION = "0.9.0";

	private final NativeWebRequest request;

	private final Logger log = LogManager.getLogger(DefaultApiController.class);

	@Value("${org.openeo.public.endpoint}")
	private String openEOEndpoint;

	@Value("${org.openeo.wcps.provider.url}")
	private String providerUrl;

    @Value("${spring.security.enable-basic}")
    boolean enableBasicAuth;

    @Value("${spring.security.enable-keycloak}")
    boolean enableKeycloakAuth;

    @Autowired
    ConfigurableEnvironment env;

	@org.springframework.beans.factory.annotation.Autowired
	public DefaultApiController(NativeWebRequest request) {
		this.request = request;
	}

	@Override
	public Optional<NativeWebRequest> getRequest() {
		return Optional.ofNullable(request);
	}

	@Override
    @Operation(summary = "Information about the back-end", operationId = "capabilities", description = "Returns general information about the back-end, including which version and endpoints of the openEO API are supported. May also include billing information.", tags = {
			"Capabilities", })
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Information about the API version and supported endpoints / features."),
			@ApiResponse(responseCode = "400", description = "The request can't be fulfilled due to an error on client-side, i.e. the request is invalid. The client should not repeat the request without modifications.  The response body SHOULD contain a JSON error object. MUST be any HTTP status code specified in [RFC 7231](https://tools.ietf.org/html/rfc7231#section-6.6). This request usually does not respond with HTTP status codes 401 and 403 due to missing authorization. HTTP status code 404 should be used if the value of a path parameter is invalid.  See also: * [Error Handling](#section/API-Principles/Error-Handling) in the API in general. * [Common Error Codes](errors.json)"),
			@ApiResponse(responseCode = "500", description = "The request can't be fulfilled due to an error at the back-end. The error is never the client’s fault and therefore it is reasonable for the client to retry the exact same request that triggered this response.  The response body SHOULD contain a JSON error object. MUST be any HTTP status code specified in [RFC 7231](https://tools.ietf.org/html/rfc7231#section-6.6).  See also: * [Error Handling](#section/API-Principles/Error-Handling) in the API in general. * [Common Error Codes](errors.json)") })
	@RequestMapping(value = "/", produces = { "application/json" }, method = RequestMethod.GET)
	public ResponseEntity<Capabilities> capabilities() {

		Capabilities capabilities = new Capabilities();

	    /*
         * openEO/STAC implemented versions
         */
		capabilities.apiVersion(IMPLEMENTED_OPENEO_API_VERSION);
		capabilities.backendVersion("Spring-Dev-1.2.0-master");
		capabilities.description(
				"The Eurac Research backend provides EO data available for processing using OGC WC(P)S and the open data cube");
		capabilities.title("Eurac Research - openEO - backend");
		capabilities.setId("Eurac_openEO");
		capabilities.setStacVersion(IMPLEMENTED_STAC_VERSION);

	    /*
         * API endpoints
         */
		Endpoint capabilitiesEndPoint = new Endpoint();
		capabilitiesEndPoint.setPath("/");
		capabilitiesEndPoint.addMethodsItem(MethodsEnum.GET);
		capabilities.addEndpointsItem(capabilitiesEndPoint);

		Endpoint wellKnownEndPoint = new Endpoint();
		wellKnownEndPoint.setPath("/.well-known/openeo");
		wellKnownEndPoint.addMethodsItem(MethodsEnum.GET);
		capabilities.addEndpointsItem(wellKnownEndPoint);

		Endpoint meEndPoint = new Endpoint();
		meEndPoint.setPath("/me");
		meEndPoint.addMethodsItem(MethodsEnum.GET);
		capabilities.addEndpointsItem(meEndPoint);

		Endpoint resultEndPoint = new Endpoint();
		resultEndPoint.setPath("/result");
		resultEndPoint.addMethodsItem(MethodsEnum.POST);
		capabilities.addEndpointsItem(resultEndPoint);

		Endpoint collectionsEndpoint = new Endpoint();
		collectionsEndpoint.setPath("/collections");
		collectionsEndpoint.addMethodsItem(MethodsEnum.GET);
		capabilities.addEndpointsItem(collectionsEndpoint);

		Endpoint collectionIDEndpoint = new Endpoint();
		collectionIDEndpoint.setPath("/collections/{collection_id}");
		collectionIDEndpoint.addMethodsItem(MethodsEnum.GET);
		capabilities.addEndpointsItem(collectionIDEndpoint);

		Endpoint collectionCoverageEndpoint = new Endpoint();
		collectionCoverageEndpoint.setPath("/collections/{collection_id}/coverage");
		collectionCoverageEndpoint.addMethodsItem(MethodsEnum.GET);
		capabilities.addEndpointsItem(collectionCoverageEndpoint);

		Endpoint processesEndpoint = new Endpoint();
		processesEndpoint.setPath("/processes");
		processesEndpoint.addMethodsItem(MethodsEnum.GET);
		capabilities.addEndpointsItem(processesEndpoint);

		Endpoint fileFormatsEndpoint = new Endpoint();
		fileFormatsEndpoint.setPath("/file_formats");
		fileFormatsEndpoint.addMethodsItem(MethodsEnum.GET);
		capabilities.addEndpointsItem(fileFormatsEndpoint);

		Endpoint udfEndpoint = new Endpoint();
		udfEndpoint.setPath("/udf_runtimes");
		udfEndpoint.addMethodsItem(MethodsEnum.GET);
		capabilities.addEndpointsItem(udfEndpoint);

		Endpoint conformanceEndpoint = new Endpoint();
		conformanceEndpoint.setPath("/conformance");
		conformanceEndpoint.addMethodsItem(MethodsEnum.GET);
		capabilities.addEndpointsItem(conformanceEndpoint);

		if (enableBasicAuth) {
            Endpoint credentialsBasicEndpoint = new Endpoint();
            credentialsBasicEndpoint.setPath("/credentials/basic");
            credentialsBasicEndpoint.addMethodsItem(MethodsEnum.GET);
            capabilities.addEndpointsItem(credentialsBasicEndpoint);
        }

		if (enableKeycloakAuth) {
		    Endpoint credentialsOIDCEndpoint = new Endpoint();
		    credentialsOIDCEndpoint.setPath("/credentials/oidc");
		    credentialsOIDCEndpoint.addMethodsItem(MethodsEnum.GET);
		    capabilities.addEndpointsItem(credentialsOIDCEndpoint);
		}

		Endpoint jobsEndpoint = new Endpoint();
		jobsEndpoint.setPath("/jobs");
		jobsEndpoint.addMethodsItem(MethodsEnum.GET);
		jobsEndpoint.addMethodsItem(MethodsEnum.POST);
		capabilities.addEndpointsItem(jobsEndpoint);

		Endpoint jobEndpoint = new Endpoint();
		jobEndpoint.setPath("/jobs/{job_id}");
		jobEndpoint.addMethodsItem(MethodsEnum.GET);
		jobEndpoint.addMethodsItem(MethodsEnum.PATCH);
		jobEndpoint.addMethodsItem(MethodsEnum.DELETE);
		capabilities.addEndpointsItem(jobEndpoint);

		Endpoint jobDebugEndpoint = new Endpoint();
		jobDebugEndpoint.setPath("/jobs/{job_id}/logs");
		jobDebugEndpoint.addMethodsItem(MethodsEnum.GET);
		capabilities.addEndpointsItem(jobDebugEndpoint);

		Endpoint jobResultEndpoint = new Endpoint();
		jobResultEndpoint.setPath("/jobs/{job_id}/results");
		jobResultEndpoint.addMethodsItem(MethodsEnum.GET);
		jobResultEndpoint.addMethodsItem(MethodsEnum.POST);
		jobResultEndpoint.addMethodsItem(MethodsEnum.DELETE);
		capabilities.addEndpointsItem(jobResultEndpoint);

		Endpoint downloadEndpoint = new Endpoint();
		downloadEndpoint.setPath("/download/{file_name}");
		downloadEndpoint.addMethodsItem(MethodsEnum.GET);
		capabilities.addEndpointsItem(downloadEndpoint);

		/*
         * Billing info
         */
		Billing billing = new Billing();
		billing.setDefaultPlan("free");
		billing.setCurrency(null);
		BillingPlan billingPlan = new BillingPlan();
		billingPlan.setName("free");
		billingPlan.setDescription("free use of service for research purposes and testing");
		billingPlan.setPaid(false);
		try {
			billingPlan.setUrl(new URI(openEOEndpoint));
		} catch (URISyntaxException e1) {
			log.error("the url provided is not valid: " + openEOEndpoint);
		}
		billing.addPlansItem(billingPlan);
		capabilities.setBilling(billing);

		/*
         * HTML Home page link
         */
		Link operatorUrl = new Link();
		try {
			operatorUrl.setHref(new URI(providerUrl));
		} catch (URISyntaxException e) {
			log.error("the url provided is not valid: " + providerUrl);
		}
		operatorUrl.setTitle("Homepage of the Service Provider");
		operatorUrl.setType("text/html");
		operatorUrl.setRel("Eurac Research");
		capabilities.addLinksItem(operatorUrl);

		/*
         * Self link
         */
		Link openEOUrl = new Link();
		try {
			openEOUrl.setHref(new URI(this.openEOEndpoint));
		} catch (URISyntaxException e) {
			log.error("the url provided is not valid: " + openEOEndpoint);
		}
		openEOUrl.setTitle("URL to openEO API Service");
		openEOUrl.setType("application/json");
		openEOUrl.setRel("self");
		capabilities.addLinksItem(openEOUrl);

		/*
         * Well-Known link
         */
		String versionHistoryUrl = String.format("%s/.well-known/openeo", this.openEOEndpoint);
		Link versionHistoryLink = new Link();
		try {
			versionHistoryLink.setHref(new URI(versionHistoryUrl));
		} catch (URISyntaxException e) {
			log.error("the url provided is not valid: " + versionHistoryUrl);
		}
		versionHistoryLink.setTitle("Well-Known URL");
		versionHistoryLink.setType("application/json");
		versionHistoryLink.setRel("version-history");
		capabilities.addLinksItem(versionHistoryLink);

		/*
         * Collections link
         */
		String collDataUrl = String.format("%s/collections", this.openEOEndpoint);
		Link collDataLink = new Link();
		try {
			collDataLink.setHref(new URI(collDataUrl));
		} catch (URISyntaxException e) {
			log.error("the url provided is not valid: " + collDataUrl);
		}
		collDataLink.setTitle("Collections");
		collDataLink.setType("application/json");
		collDataLink.setRel("data");
		capabilities.addLinksItem(collDataLink);

		/*
		 * Conformance link
		 */
		String conformanceUrl = String.format("%s/conformance", openEOEndpoint);
		Link conformanceLink = new Link();
		try {
		    conformanceLink.setHref(new URI(conformanceUrl));
		} catch (URISyntaxException e) {
            log.error("the conformance URI is not valid: " + conformanceUrl);
        }
		conformanceLink.setTitle("Conformance Declaration as JSON");
		conformanceLink.setType("application/json");
		conformanceLink.setRel(OgcLinkRelation.CONFORMANCE.toString());
		capabilities.addLinksItem(conformanceLink);


		return new ResponseEntity<Capabilities>(capabilities, HttpStatus.OK);
	}

}
