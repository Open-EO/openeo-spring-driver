/**
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech) (5.0.0-SNAPSHOT).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */
package org.openeo.spring.api;

import org.openeo.spring.model.Error;
import org.springframework.core.io.Resource;
import org.openeo.spring.model.WorkspaceFilesListResponse;
import io.swagger.annotations.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import javax.validation.constraints.*;
import java.util.List;
import java.util.Map;
import java.util.Optional;
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2020-06-30T14:48:14.663+02:00[Europe/Rome]")
@Validated
@Api(value = "files", description = "the files API")
public interface FilesApi {

    default Optional<NativeWebRequest> getRequest() {
        return Optional.empty();
    }

    /**
     * DELETE /files/{path} : Delete a file from the workspace
     * This service deletes an existing user-uploaded file specified by its path. Resulting empty folders MUST be deleted automatically.  Back-ends MAY support deleting folders including its files and sub-folders. If not supported by the back-end a &#x60;FileOperationUnsupported&#x60; error MUST be sent as response.
     *
     * @param path Path of the file, relative to the user&#39;s root directory.    **Note:** Folder and file names in the path MUST be url-encoded. The path separator &#x60;/&#x60; and the file extension separator &#x60;.&#x60; MUST NOT be url-encoded. This may be shown incorrectly in rendered versions due to [OpenAPI 3 not supporting path parameters which contain slashes](https://github.com/OAI/OpenAPI-Specification/issues/892). (required)
     * @return The file has been successfully deleted at the back-end. (status code 204)
     *         or The request can&#39;t be fulfilled due to an error on client-side, i.e. the request is invalid. The client should not repeat the request without modifications.  The response body SHOULD contain a JSON error object. MUST be any HTTP status code specified in [RFC 7231](https://tools.ietf.org/html/rfc7231#section-6.6). This request MUST respond with HTTP status codes 401 if authorization is required or 403 if the authorization failed or access is forbidden in general to the authenticated user. HTTP status code 404 should be used if the value of a path parameter is invalid.  See also: * [Error Handling](#section/API-Principles/Error-Handling) in the API in general. * [Common Error Codes](errors.json) (status code 400)
     *         or The request can&#39;t be fulfilled due to an error at the back-end. The error is never the client’s fault and therefore it is reasonable for the client to retry the exact same request that triggered this response.  The response body SHOULD contain a JSON error object. MUST be any HTTP status code specified in [RFC 7231](https://tools.ietf.org/html/rfc7231#section-6.6).  See also: * [Error Handling](#section/API-Principles/Error-Handling) in the API in general. * [Common Error Codes](errors.json) (status code 500)
     */
    @ApiOperation(value = "Delete a file from the workspace", nickname = "deleteFile", notes = "This service deletes an existing user-uploaded file specified by its path. Resulting empty folders MUST be deleted automatically.  Back-ends MAY support deleting folders including its files and sub-folders. If not supported by the back-end a `FileOperationUnsupported` error MUST be sent as response.", authorizations = {
        @Authorization(value = "Bearer")
    }, tags={ "File Storage", })
    @ApiResponses(value = { 
        @ApiResponse(code = 204, message = "The file has been successfully deleted at the back-end."),
        @ApiResponse(code = 400, message = "The request can't be fulfilled due to an error on client-side, i.e. the request is invalid. The client should not repeat the request without modifications.  The response body SHOULD contain a JSON error object. MUST be any HTTP status code specified in [RFC 7231](https://tools.ietf.org/html/rfc7231#section-6.6). This request MUST respond with HTTP status codes 401 if authorization is required or 403 if the authorization failed or access is forbidden in general to the authenticated user. HTTP status code 404 should be used if the value of a path parameter is invalid.  See also: * [Error Handling](#section/API-Principles/Error-Handling) in the API in general. * [Common Error Codes](errors.json)", response = Error.class),
        @ApiResponse(code = 500, message = "The request can't be fulfilled due to an error at the back-end. The error is never the client’s fault and therefore it is reasonable for the client to retry the exact same request that triggered this response.  The response body SHOULD contain a JSON error object. MUST be any HTTP status code specified in [RFC 7231](https://tools.ietf.org/html/rfc7231#section-6.6).  See also: * [Error Handling](#section/API-Principles/Error-Handling) in the API in general. * [Common Error Codes](errors.json)", response = Error.class) })
    @RequestMapping(value = "/files/{path}",
        produces = { "application/json" }, 
        method = RequestMethod.DELETE)
    default ResponseEntity<Void> deleteFile(@ApiParam(value = "Path of the file, relative to the user's root directory.    **Note:** Folder and file names in the path MUST be url-encoded. The path separator `/` and the file extension separator `.` MUST NOT be url-encoded. This may be shown incorrectly in rendered versions due to [OpenAPI 3 not supporting path parameters which contain slashes](https://github.com/OAI/OpenAPI-Specification/issues/892).",required=true) @PathVariable("path") String path) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }


    /**
     * GET /files/{path} : Download a file from the workspace
     * This service downloads a user files identified by its path relative to the user&#39;s root directory. If a folder is specified as path a &#x60;FileOperationUnsupported&#x60; error MUST be sent as response.
     *
     * @param path Path of the file, relative to the user&#39;s root directory.    **Note:** Folder and file names in the path MUST be url-encoded. The path separator &#x60;/&#x60; and the file extension separator &#x60;.&#x60; MUST NOT be url-encoded. This may be shown incorrectly in rendered versions due to [OpenAPI 3 not supporting path parameters which contain slashes](https://github.com/OAI/OpenAPI-Specification/issues/892). (required)
     * @return A file from the workspace. (status code 200)
     *         or The request can&#39;t be fulfilled due to an error on client-side, i.e. the request is invalid. The client should not repeat the request without modifications.  The response body SHOULD contain a JSON error object. MUST be any HTTP status code specified in [RFC 7231](https://tools.ietf.org/html/rfc7231#section-6.6). This request MUST respond with HTTP status codes 401 if authorization is required or 403 if the authorization failed or access is forbidden in general to the authenticated user. HTTP status code 404 should be used if the value of a path parameter is invalid.  See also: * [Error Handling](#section/API-Principles/Error-Handling) in the API in general. * [Common Error Codes](errors.json) (status code 400)
     *         or The request can&#39;t be fulfilled due to an error at the back-end. The error is never the client’s fault and therefore it is reasonable for the client to retry the exact same request that triggered this response.  The response body SHOULD contain a JSON error object. MUST be any HTTP status code specified in [RFC 7231](https://tools.ietf.org/html/rfc7231#section-6.6).  See also: * [Error Handling](#section/API-Principles/Error-Handling) in the API in general. * [Common Error Codes](errors.json) (status code 500)
     */
    @ApiOperation(value = "Download a file from the workspace", nickname = "downloadFile", notes = "This service downloads a user files identified by its path relative to the user's root directory. If a folder is specified as path a `FileOperationUnsupported` error MUST be sent as response.", response = Resource.class, authorizations = {
        @Authorization(value = "Bearer")
    }, tags={ "File Storage", })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "A file from the workspace.", response = Resource.class),
        @ApiResponse(code = 400, message = "The request can't be fulfilled due to an error on client-side, i.e. the request is invalid. The client should not repeat the request without modifications.  The response body SHOULD contain a JSON error object. MUST be any HTTP status code specified in [RFC 7231](https://tools.ietf.org/html/rfc7231#section-6.6). This request MUST respond with HTTP status codes 401 if authorization is required or 403 if the authorization failed or access is forbidden in general to the authenticated user. HTTP status code 404 should be used if the value of a path parameter is invalid.  See also: * [Error Handling](#section/API-Principles/Error-Handling) in the API in general. * [Common Error Codes](errors.json)", response = Error.class),
        @ApiResponse(code = 500, message = "The request can't be fulfilled due to an error at the back-end. The error is never the client’s fault and therefore it is reasonable for the client to retry the exact same request that triggered this response.  The response body SHOULD contain a JSON error object. MUST be any HTTP status code specified in [RFC 7231](https://tools.ietf.org/html/rfc7231#section-6.6).  See also: * [Error Handling](#section/API-Principles/Error-Handling) in the API in general. * [Common Error Codes](errors.json)", response = Error.class) })
    @RequestMapping(value = "/files/{path}",
        produces = { "application/octet-stream", "application/json" }, 
        method = RequestMethod.GET)
    default ResponseEntity<Resource> downloadFile(@ApiParam(value = "Path of the file, relative to the user's root directory.    **Note:** Folder and file names in the path MUST be url-encoded. The path separator `/` and the file extension separator `.` MUST NOT be url-encoded. This may be shown incorrectly in rendered versions due to [OpenAPI 3 not supporting path parameters which contain slashes](https://github.com/OAI/OpenAPI-Specification/issues/892).",required=true) @PathVariable("path") String path) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }


    /**
     * GET /files : List all files in the workspace
     * This service lists all user-uploaded files that are stored at the back-end.
     *
     * @param limit This parameter enables pagination for the endpoint and specifies the maximum number of elements that arrays in the top-level object (e.g. jobs or log entries) are allowed to contain. The only exception is the &#x60;links&#x60; array, which MUST NOT be paginated as otherwise the pagination links may be missing ins responses. If the parameter is not provided or empty, all elements are returned.  Pagination is OPTIONAL and back-ends and clients may not support it. Therefore it MUST be implemented in a way that clients not supporting pagination get all resources regardless. Back-ends not supporting  pagination will return all resources.  If the response is paginated, the links array MUST be used to propagate the  links for pagination with pre-defined &#x60;rel&#x60; types. See the links array schema for supported &#x60;rel&#x60; types.  *Note:* Implementations can use all kind of pagination techniques, depending on what is supported best by their infrastructure. So it doesn&#39;t care whether it is page-based, offset-based or uses tokens for pagination. The clients will use whatever is specified in the links with the corresponding &#x60;rel&#x60; types. (optional)
     * @return Flattened file tree with path relative to the user&#39;s root directory and some basic properties such as the file size and the timestamp of the last modification. All properties except the name are optional. Folders MUST NOT be listed separately so each element in the list MUST be a downloadable file. (status code 200)
     *         or The request can&#39;t be fulfilled due to an error on client-side, i.e. the request is invalid. The client should not repeat the request without modifications.  The response body SHOULD contain a JSON error object. MUST be any HTTP status code specified in [RFC 7231](https://tools.ietf.org/html/rfc7231#section-6.6). This request MUST respond with HTTP status codes 401 if authorization is required or 403 if the authorization failed or access is forbidden in general to the authenticated user. HTTP status code 404 should be used if the value of a path parameter is invalid.  See also: * [Error Handling](#section/API-Principles/Error-Handling) in the API in general. * [Common Error Codes](errors.json) (status code 400)
     *         or The request can&#39;t be fulfilled due to an error at the back-end. The error is never the client’s fault and therefore it is reasonable for the client to retry the exact same request that triggered this response.  The response body SHOULD contain a JSON error object. MUST be any HTTP status code specified in [RFC 7231](https://tools.ietf.org/html/rfc7231#section-6.6).  See also: * [Error Handling](#section/API-Principles/Error-Handling) in the API in general. * [Common Error Codes](errors.json) (status code 500)
     */
    @ApiOperation(value = "List all files in the workspace", nickname = "listFiles", notes = "This service lists all user-uploaded files that are stored at the back-end.", response = WorkspaceFilesListResponse.class, authorizations = {
        @Authorization(value = "Bearer")
    }, tags={ "File Storage", })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "Flattened file tree with path relative to the user's root directory and some basic properties such as the file size and the timestamp of the last modification. All properties except the name are optional. Folders MUST NOT be listed separately so each element in the list MUST be a downloadable file.", response = WorkspaceFilesListResponse.class),
        @ApiResponse(code = 400, message = "The request can't be fulfilled due to an error on client-side, i.e. the request is invalid. The client should not repeat the request without modifications.  The response body SHOULD contain a JSON error object. MUST be any HTTP status code specified in [RFC 7231](https://tools.ietf.org/html/rfc7231#section-6.6). This request MUST respond with HTTP status codes 401 if authorization is required or 403 if the authorization failed or access is forbidden in general to the authenticated user. HTTP status code 404 should be used if the value of a path parameter is invalid.  See also: * [Error Handling](#section/API-Principles/Error-Handling) in the API in general. * [Common Error Codes](errors.json)", response = Error.class),
        @ApiResponse(code = 500, message = "The request can't be fulfilled due to an error at the back-end. The error is never the client’s fault and therefore it is reasonable for the client to retry the exact same request that triggered this response.  The response body SHOULD contain a JSON error object. MUST be any HTTP status code specified in [RFC 7231](https://tools.ietf.org/html/rfc7231#section-6.6).  See also: * [Error Handling](#section/API-Principles/Error-Handling) in the API in general. * [Common Error Codes](errors.json)", response = Error.class) })
    @RequestMapping(value = "/files",
        produces = { "application/json" }, 
        method = RequestMethod.GET)
    default ResponseEntity<WorkspaceFilesListResponse> listFiles(@Min(1)@ApiParam(value = "This parameter enables pagination for the endpoint and specifies the maximum number of elements that arrays in the top-level object (e.g. jobs or log entries) are allowed to contain. The only exception is the `links` array, which MUST NOT be paginated as otherwise the pagination links may be missing ins responses. If the parameter is not provided or empty, all elements are returned.  Pagination is OPTIONAL and back-ends and clients may not support it. Therefore it MUST be implemented in a way that clients not supporting pagination get all resources regardless. Back-ends not supporting  pagination will return all resources.  If the response is paginated, the links array MUST be used to propagate the  links for pagination with pre-defined `rel` types. See the links array schema for supported `rel` types.  *Note:* Implementations can use all kind of pagination techniques, depending on what is supported best by their infrastructure. So it doesn't care whether it is page-based, offset-based or uses tokens for pagination. The clients will use whatever is specified in the links with the corresponding `rel` types.") @Valid @RequestParam(value = "limit", required = false) Integer limit) {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "\"{\\"files\\":[{\\"path\\":\\"test.txt\\",\\"size\\":182,\\"modified\\":\\"2015-10-20T17:22:10Z\\"},{\\"path\\":\\"test.tif\\",\\"size\\":183142,\\"modified\\":\\"2017-01-01T09:36:18Z\\"},{\\"path\\":\\"Sentinel2/S2A_MSIL1C_20170819T082011_N0205_R121_T34KGD_20170819T084427.zip\\",\\"size\\":4183353142,\\"modified\\":\\"2018-01-03T10:55:29Z\\"}],\\"links\\":[]}\"";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }


    /**
     * PUT /files/{path} : Upload a file to the workspace
     * This service uploads a new or updates an existing file at a given path.  Folders are created once required by a file upload. Empty folders can&#39;t be created.
     *
     * @param path Path of the file, relative to the user&#39;s root directory.    **Note:** Folder and file names in the path MUST be url-encoded. The path separator &#x60;/&#x60; and the file extension separator &#x60;.&#x60; MUST NOT be url-encoded. This may be shown incorrectly in rendered versions due to [OpenAPI 3 not supporting path parameters which contain slashes](https://github.com/OAI/OpenAPI-Specification/issues/892). (required)
     * @param body  (required)
     * @return The file has been uploaded successfully. (status code 200)
     *         or The request can&#39;t be fulfilled due to an error on client-side, i.e. the request is invalid. The client should not repeat the request without modifications.  The response body SHOULD contain a JSON error object. MUST be any HTTP status code specified in [RFC 7231](https://tools.ietf.org/html/rfc7231#section-6.6). This request MUST respond with HTTP status codes 401 if authorization is required or 403 if the authorization failed or access is forbidden in general to the authenticated user. HTTP status code 404 should be used if the value of a path parameter is invalid.  See also: * [Error Handling](#section/API-Principles/Error-Handling) in the API in general. * [Common Error Codes](errors.json) (status code 400)
     *         or The request can&#39;t be fulfilled due to an error at the back-end. The error is never the client’s fault and therefore it is reasonable for the client to retry the exact same request that triggered this response.  The response body SHOULD contain a JSON error object. MUST be any HTTP status code specified in [RFC 7231](https://tools.ietf.org/html/rfc7231#section-6.6).  See also: * [Error Handling](#section/API-Principles/Error-Handling) in the API in general. * [Common Error Codes](errors.json) (status code 500)
     */
    @ApiOperation(value = "Upload a file to the workspace", nickname = "uploadFile", notes = "This service uploads a new or updates an existing file at a given path.  Folders are created once required by a file upload. Empty folders can't be created.", response = Resource.class, authorizations = {
        @Authorization(value = "Bearer")
    }, tags={ "File Storage", })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "The file has been uploaded successfully.", response = Resource.class),
        @ApiResponse(code = 400, message = "The request can't be fulfilled due to an error on client-side, i.e. the request is invalid. The client should not repeat the request without modifications.  The response body SHOULD contain a JSON error object. MUST be any HTTP status code specified in [RFC 7231](https://tools.ietf.org/html/rfc7231#section-6.6). This request MUST respond with HTTP status codes 401 if authorization is required or 403 if the authorization failed or access is forbidden in general to the authenticated user. HTTP status code 404 should be used if the value of a path parameter is invalid.  See also: * [Error Handling](#section/API-Principles/Error-Handling) in the API in general. * [Common Error Codes](errors.json)", response = Error.class),
        @ApiResponse(code = 500, message = "The request can't be fulfilled due to an error at the back-end. The error is never the client’s fault and therefore it is reasonable for the client to retry the exact same request that triggered this response.  The response body SHOULD contain a JSON error object. MUST be any HTTP status code specified in [RFC 7231](https://tools.ietf.org/html/rfc7231#section-6.6).  See also: * [Error Handling](#section/API-Principles/Error-Handling) in the API in general. * [Common Error Codes](errors.json)", response = Error.class) })
    @RequestMapping(value = "/files/{path}",
        produces = { "application/json" }, 
        consumes = { "application/octet-stream" },
        method = RequestMethod.PUT)
    default ResponseEntity<Resource> uploadFile(@ApiParam(value = "Path of the file, relative to the user's root directory.    **Note:** Folder and file names in the path MUST be url-encoded. The path separator `/` and the file extension separator `.` MUST NOT be url-encoded. This may be shown incorrectly in rendered versions due to [OpenAPI 3 not supporting path parameters which contain slashes](https://github.com/OAI/OpenAPI-Specification/issues/892).",required=true) @PathVariable("path") String path,@ApiParam(value = "" ,required=true )  @Valid @RequestBody Resource body) {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"path\" : \"folder/file.txt\", \"size\" : 1024, \"modified\" : \"2018-01-03T10:55:29Z\" }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

}
