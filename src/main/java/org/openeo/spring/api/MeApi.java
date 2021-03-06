/**
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech) (5.0.0-SNAPSHOT).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */
package org.openeo.spring.api;

import java.security.Principal;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.request.NativeWebRequest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2020-07-02T08:45:00.334+02:00[Europe/Rome]")
@Validated
public interface MeApi {

	default Optional<NativeWebRequest> getRequest() {
		return Optional.empty();
	}

	/**
	 * GET /me : Information about the authenticated user This endpoint always
	 * returns the user id and MAY return the disk quota available to the user. It
	 * MAY also return links related to user management and the user profile, e.g.
	 * where payments are handled or the user profile could be edited. For back-ends
	 * that involve accounting, this service MAY also return the currently available
	 * money or credits in the currency the back-end is working with. This endpoint
	 * MAY be extended to fulfil the specification of the [OpenID Connect UserInfo
	 * Endpoint](http://openid.net/specs/openid-connect-core-1_0.html#UserInfo).
	 *
	 * @return Information about the logged in user. (status code 200) or The
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
	@Operation(summary = "Information about the authenticated user", operationId = "describeAccount", description = "This endpoint always returns the user id and MAY return the disk quota available to the user. It MAY also return links related to user management and the user profile, e.g. where payments are handled or the user profile could be edited. For back-ends that involve accounting, this service MAY also return the currently available money or credits in the currency the back-end is working with. This endpoint MAY be extended to fulfil the specification of the [OpenID Connect UserInfo Endpoint](http://openid.net/specs/openid-connect-core-1_0.html#UserInfo).", security = {
			@SecurityRequirement(name = "bearer-key") })
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Information about the logged in user."),
			@ApiResponse(responseCode = "400", description = "The request can't be fulfilled due to an error on client-side, i.e. the request is invalid. The client should not repeat the request without modifications.  The response body SHOULD contain a JSON error object. MUST be any HTTP status code specified in [RFC 7231](https://tools.ietf.org/html/rfc7231#section-6.6). This request MUST respond with HTTP status codes 401 if authorization is required or 403 if the authorization failed or access is forbidden in general to the authenticated user. HTTP status code 404 should be used if the value of a path parameter is invalid.  See also: * [Error Handling](#section/API-Principles/Error-Handling) in the API in general. * [Common Error Codes](errors.json)"),
			@ApiResponse(responseCode = "500", description = "The request can't be fulfilled due to an error at the back-end. The error is never the client’s fault and therefore it is reasonable for the client to retry the exact same request that triggered this response.  The response body SHOULD contain a JSON error object. MUST be any HTTP status code specified in [RFC 7231](https://tools.ietf.org/html/rfc7231#section-6.6).  See also: * [Error Handling](#section/API-Principles/Error-Handling) in the API in general. * [Common Error Codes](errors.json)") })
	@RequestMapping(value = "/me", produces = { "application/json" }, method = RequestMethod.GET)
	default ResponseEntity<?> describeAccount(Principal principal) {
		getRequest().ifPresent(request -> {
			for (MediaType mediaType : MediaType.parseMediaTypes(request.getHeader("Accept"))) {
				if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
					String exampleString = "{ \"user_id\" : \"john_doe\", \"name\" : \"name\", \"links\" : [ { \"href\" : \"https://example.openeo.org/john_doe/payment/\", \"rel\" : \"payment\" }, { \"href\" : \"https://example.openeo.org/john_doe/edit/\", \"rel\" : \"edit-form\" }, { \"href\" : \"https://example.openeo.org/john_doe/\", \"rel\" : \"alternate\", \"type\" : \"text/html\", \"title\" : \"User profile\" }, { \"href\" : \"https://example.openeo.org/john_doe.vcf\", \"rel\" : \"alternate\", \"type\" : \"text/vcard\", \"title\" : \"vCard of John Doe\" } ], \"storage\" : { \"quota\" : 1073741824, \"free\" : 536870912 }, \"budget\" : 100 }";
					ApiUtil.setExampleResponse(request, "application/json", exampleString);
					break;
				}
			}
		});
		return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

	}

}
