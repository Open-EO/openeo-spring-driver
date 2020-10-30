package org.openeo.spring.api;

import java.security.Principal;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.keycloak.representations.AccessToken;
import org.openeo.spring.model.Error;
import org.openeo.spring.model.UserData;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.NativeWebRequest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2020-07-02T08:45:00.334+02:00[Europe/Rome]")
@Controller
@RequestMapping("${openapi.openEO.base-path:}")
public class MeApiController implements MeApi {

    private final NativeWebRequest request;
    
    private final Logger log = LogManager.getLogger(MeApiController.class);

    @org.springframework.beans.factory.annotation.Autowired
    public MeApiController(NativeWebRequest request) {
        this.request = request;
    }

    @Override
    public Optional<NativeWebRequest> getRequest() {
        return Optional.ofNullable(request);
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
	@GetMapping(value = "/me", produces = { "application/json" })
	public ResponseEntity<?> describeAccount(Principal principal) {
		UserData userData = new UserData();
		KeycloakAuthenticationToken keycloakAuthenticationToken = (KeycloakAuthenticationToken) principal;
        AccessToken accessToken = keycloakAuthenticationToken.getAccount().getKeycloakSecurityContext().getToken();
		if(principal != null) {
			userData.setUserId(principal.getName());
			log.debug("registered user id: " + principal.getName());
			userData.setName(accessToken.getName());
			log.debug("registered user name: " + accessToken.getName());
		}else {
			Error error = new Error();
			error.setCode("500");
			error.setMessage("Security Principal is null, verification not possible!");
			return new ResponseEntity<Error>(error, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<UserData>(userData, HttpStatus.OK);

	}

}
