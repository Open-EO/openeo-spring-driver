package org.openapitools.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.web.util.UriComponentsBuilder;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.paths.Paths;
import springfox.documentation.spring.web.paths.RelativePathProvider;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import javax.servlet.ServletContext;

@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2020-06-30T14:48:14.663+02:00[Europe/Rome]")
@Configuration
@EnableSwagger2
public class OpenAPIDocumentationConfig {

    ApiInfo apiInfo() {
        return new ApiInfoBuilder()
            .title("openEO API")
            .description("The openEO API specification for interoperable cloud-based processing of large Earth observation datasets.  # API Principles  ## Language  In the specification the key words “MUST”, “MUST NOT”, “REQUIRED”, “SHALL”, “SHALL NOT”, “SHOULD”, “SHOULD NOT”, “RECOMMENDED”, “MAY”, and “OPTIONAL” in this document are to be interpreted as described in [RFC 2119](http://tools.ietf.org/html/rfc2119).  ## Casing  Unless otherwise stated the API works **case sensitive**.  All names SHOULD be written in snake case, i.e. words are separated with one underscore character (_) and no spaces, with all letters lower-cased. Example: `hello_world`. This applies particularly to endpoints and JSON property names. HTTP header fields follow their respective casing conventions, e.g. `Content-Type` or `OpenEO-Costs`, despite being case-insensitive according to [RFC 7230](https://tools.ietf.org/html/rfc7230#section-3.2).  ## HTTP / REST  This uses [HTTP REST](https://en.wikipedia.org/wiki/Representational_state_transfer) [Level 2](https://martinfowler.com/articles/richardsonMaturityModel.html#level2) for communication between client and back-end server.  Public APIs MUST be available via HTTPS only.   Endpoints are made use meaningful HTTP verbs (e.g. GET, POST, PUT, PATCH, DELETE) whenever technically possible. If there is a need to transfer big chunks of data for a GET requests to the back-end, POST requests MAY be used as a replacement as they support to send data via request body. Unless otherwise stated, PATCH requests are only defined to work on direct (first-level) children of the full JSON object. Therefore, changing a property on a deeper level of the full JSON object always requires to send the whole JSON object defined by the first-level property.  Naming of endpoints follow the REST principles. Therefore, endpoints are centered around resources. Resource identifiers MUST be named with a noun in plural form except for single actions that can not be modelled with the regular HTTP verbs. Single actions MUST be single endpoints with a single HTTP verb (POST is RECOMMENDED) and no other endpoints beneath it.  ## JSON  The API uses JSON for request and response bodies whenever feasible. Services use JSON as the default encoding. Other encodings can be requested using [Content Negotiation](https://www.w3.org/Protocols/rfc2616/rfc2616-sec12.html). Clients and servers MUST NOT rely on the order in which properties appears in JSON. Collections usually don't include nested JSON objects if those information can be requested from the individual resources.  ## Web Linking  The API is designed in a way that to most entities (e.g. collections and processes) a set of links can be added. These can be alternate representations, e.g. data discovery via OGC WCS or OGC CSW, references to a license, references to actual raw data for downloading, detailed information about pre-processing and more. Clients should allow users to follow the links.  Whenever links are utilized in the API, the description explains which relation (`rel` property) types are commonly used. A [list of standardized link relations types is provided by IANA](https://www.iana.org/assignments/link-relations/link-relations.xhtml) and the API tries to align whenever feasible.  Some very common relation types - usually not mentioned explicitly in the description of `links` fields - are:  1. `self`: which allows link to the location that the resource can be (permanently) found online.This is particularly useful when the data is data is made available offline, so that the downstream user knows where the data has come from.  2. `alternate`: An alternative representation of the resource, may it be another metadata standard the data is available in or simply a human-readable version in HTML or PDF.  3. `about`: A resource that is related or further explains the resource, e.g. a user guide.  ## Error Handling  The success of requests MUST be indicated using [HTTP status codes](https://tools.ietf.org/html/rfc7231#section-6) according to [RFC 7231](https://tools.ietf.org/html/rfc7231).  If the API responds with a status code between 100 and 399 the back-end indicates that the request has been handled successfully.  In general an error is communicated with a status code between 400 and 599. Client errors are defined as a client passing invalid data to the service and the service *correctly* rejecting that data. Examples include invalid credentials, incorrect parameters, unknown versions, or similar. These are generally \"4xx\" HTTP error codes and are the result of a client passing incorrect or invalid data. Client errors do *not* contribute to overall API availability.   Server errors are defined as the server failing to correctly return in response to a valid client request. These are generally \"5xx\" HTTP error codes. Server errors *do* contribute to the overall API availability. Calls that fail due to rate limiting or quota failures MUST NOT count as server errors.   ### JSON error object  A JSON error object SHOULD be sent with all responses that have a status code between 400 and 599.  ``` json {   \"id\": \"936DA01F-9ABD-4D9D-80C7-02AF85C822A8\",   \"code\": \"SampleError\",   \"message\": \"A sample error message.\",   \"url\": \"https://example.openeo.org/docs/errors/SampleError\" } ```  Sending `code` and `message` is REQUIRED.   * A back-end MAY add a free-form `id` (unique identifier) to the error response to be able to log and track errors with further non-disclosable details. * The `code` is either one of the [standardized textual openEO error codes](errors.json) or a proprietary error code. * The `message` explains the reason the server is rejecting the request. For \"4xx\" error codes the message explains how the client needs to modify the request.    By default the message MUST be sent in English language. Content Negotiation is used to localize the error messages: If an `Accept-Language` header is sent by the client and a translation is available, the message should be translated accordingly and the `Content-Language` header must be present in the response. See \"[How to localize your API](http://apiux.com/2013/04/25/how-to-localize-your-api/)\" for more information. * `url` is an OPTIONAL attribute and contains a link to a resource that is explaining the error and potential solutions in-depth.  ### Standardized status codes  The openEO API usually uses the following HTTP status codes for successful requests:   - **200 OK**:   Indicates a successful request **with** a response body being sent. - **201 Created**   Indicates a successful request that successfully created a new resource. Sends a `Location` header to the newly created resource **without** a response body. - **202 Accepted**   Indicates a successful request that successfully queued the creation of a new resource, but it has not been created yet. The response is sent **without** a response body. - **204 No Content**:   Indicates a successful request **without** a response body being sent.  The openEO API has some commonly used HTTP status codes for failed requests:   - **400 Bad Request**:   The back-end responds with this error code whenever the error has its origin on client side and no other HTTP status code in the 400 range is suitable.  - **401 Unauthorized**:   The client did not provide any authentication details for a resource requiring authentication or the provided authentication details are not correct.  - **403 Forbidden**:   The client did provided correct authentication details, but the privileges/permissions of the provided credentials do not allow to request the resource.  - **404 Not Found**:   The resource specified by the path does not exist, i.e. one of the resources belonging to the specified identifiers are not available at the back-end.   *Note:* Unsupported endpoints MUST use HTTP status code 501.  - **500 Internal Server Error**:   The error has its origin on server side and no other status code in the 500 range is suitable.   If a HTTP status code in the 400 range is returned, the client SHOULD NOT repeat the request without modifications. For HTTP status code in the 500 range, the client MAY repeat the same request later.  All HTTP status codes defined in RFC 7231 in the 400 and 500 ranges can be used as openEO error code in addition to the most used status codes mentioned here. Responding with openEO error codes 400 and 500 SHOULD be avoided in favor of any more specific standardized or proprietary openEO error code.  ## Temporal data  Date, time, intervals and durations are formatted based on ISO 8601 or its profile [RFC 3339](https://www.ietf.org/rfc/rfc3339) whenever there is an appropriate encoding available in the standard. All temporal data are specified based on the Gregorian calendar.  # Authentication  The openEO API offers two forms of authentication by default: * OpenID Connect (recommended) at `GET /credentials/oidc` * Basic at `GET /credentials/basic`    After authentication with any of the methods listed above, the tokens obtained during the authentication workflows can be sent to protected endpoints in subsequent requests.  Further authentication methods MAY be added by back-ends.  <SecurityDefinitions />  # Cross-Origin Resource Sharing (CORS)  > Cross-origin resource sharing (CORS) is a mechanism that allows restricted resources [...] on a web page to be requested from another domain outside the domain from which the first resource was served. [...] > CORS defines a way in which a browser and server can interact to determine whether or not it is safe to allow the cross-origin request. It allows for more freedom and functionality than purely same-origin requests, but is more secure than simply allowing all cross-origin requests.  Source: [https://en.wikipedia.org/wiki/Cross-origin_resource_sharing](https://en.wikipedia.org/wiki/Cross-origin_resource_sharing)  openEO-based back-ends are usually hosted on a different domain / host than the client that is requesting data from the back-end. Therefore most requests to the back-end are blocked by all modern browsers. This leads to the problem that the JavaScript library and any browser-based application can't access back-ends. Therefore, all back-end providers SHOULD support CORS to enable browser-based applications to access back-ends. [CORS is a recommendation of the W3C organization](https://www.w3.org/TR/cors/). The following chapters will explain how back-end providers can implement CORS support.  ## OPTIONS method  All endpoints must respond to the `OPTIONS` HTTP method. This is a response for the preflight requests made by the browsers. It needs to respond with a status code of `204` and send the HTTP headers shown in the table below. No body needs to be provided.  | Name                             | Description                                                  | Example | | -------------------------------- | ------------------------------------------------------------ | ------- | | Access-Control-Allow-Origin      | Allowed origin for the request, including protocol, host and port. It is RECOMMENDED to return the value of the request's origin header. If no `Origin` is sent to the back-end CORS headers SHOULD NOT be sent at all. | `http://client.isp.com:80` | | Access-Control-Allow-Credentials | If authorization is implemented by the back-end the value MUST be `true`. | `true` | | Access-Control-Allow-Headers     | Comma-separated list of HTTP headers allowed to be send. MUST contain at least `Authorization` if authorization is implemented by the back-end. | ` Authorization, Content-Type` | | Access-Control-Allow-Methods     | Comma-separated list of HTTP methods allowed to be requested. Back-ends MUST list all implemented HTTP methods for the endpoint here. | `OPTIONS, GET, POST, PATCH, PUT, DELETE` | | Access-Control-Expose-Headers    | Some endpoints send non-safelisted HTTP response headers such as `OpenEO-Identifier` and `OpenEO-Costs`. All headers except `Cache-Control`, `Content-Language`, `Content-Type`, `Expires`, `Last-Modified` and `Pragma` must be listed in this header. Currently, the openEO API requires at least the following headers to be listed: `Location, OpenEO-Identifier, OpenEO-Costs`. | `Location, OpenEO-Identifier, OpenEO-Costs` | | Content-Type                     | SHOULD return the content type delivered by the request that the permission is requested for. | `application/json` |  ### Example request and response  Request:  ```http OPTIONS /api/v1/jobs HTTP/1.1 Host: openeo.cloudprovider.com Origin: http://client.org:8080 Access-Control-Request-Method: POST  Access-Control-Request-Headers: Authorization, Content-Type ```  Response:  ```http HTTP/1.1 204 No Content Access-Control-Allow-Origin: http://client.org:8080 Access-Control-Allow-Credentials: true Access-Control-Allow-Methods: OPTIONS, GET, POST, PATCH, PUT, DELETE Access-Control-Allow-Headers: Authorization, Content-Type Content-Type: application/json ```  ## CORS headers  The following headers MUST be included with every response:  | Name                             | Description                                                  | Example | | -------------------------------- | ------------------------------------------------------------ | ------- | | Access-Control-Allow-Origin      | Allowed origin for the request, including protocol, host and port. It is RECOMMENDED to return the value of the request's origin header. If no `Origin` is sent to the back-end CORS headers SHOULD NOT be sent at all. | `http://client.isp.com:80` | | Access-Control-Allow-Credentials | If authorization is implemented by the back-end the value MUST be `true`. | `true` | | Access-Control-Expose-Headers    | Some endpoints send non-safelisted HTTP response headers such as `OpenEO-Identifier` and `OpenEO-Costs`. All headers except `Cache-Control`, `Content-Language`, `Content-Type`, `Expires`, `Last-Modified` and `Pragma` must be listed in this header. Currently, the openEO API requires at least the following headers to be listed: `Location, OpenEO-Identifier, OpenEO-Costs`. | `Location, OpenEO-Identifier, OpenEO-Costs` |   **Tip**: Most server can send the required headers and the responses to the OPTIONS requests globally. Otherwise you may want to use a proxy server to add the headers and OPTIONS responses.  # Processes  A **process** is an operation that performs a specific task on a set of parameters and returns a result. An example is computing a statistical operation, such as mean or median, on selected EO data. A process is similar to a function or method in programming languages. In openEO, processes are used to build a chain of processes ([process graph](#section/Processes/Process-Graphs)), which can be applied to EO data to derive your own findings from the data.  A **pre-defined process** is a process provided by the *back-end*. There is a set of predefined processes by openEO to improve interoperability between back-ends. Back-ends SHOULD follow these specifications whenever possible. Not all processes need to be implemented by all back-ends. See the **[process reference](https://processes.openeo.org)** for pre-defined processes.  A **user-defined process** is a process defined by the *user*. It can directly be part of another process graph or be stored as custom process on a back-end. Internally it is a *process graph* with optional additional metadata.  A **process graph** chains specific process calls from the set of pre-defined and user-defined processes together. A process graph itself can be stored as a (user-defined) process again. Similarly to scripts in the context of programming, process graphs organize and automate the execution of one or more processes that could alternatively be executed individually. In a process graph, processes need to be specific, i.e. concrete values or \"placeholders\" for input parameters need to be specified. These values can be scalars, arrays, objects, references to parameters or previous computations or other process graphs.  ## Defining Processes  Back-ends and users MAY define new proprietary processes for their domain.   **Back-end providers** MUST follow the schema for predefined processes as in [`GET /processes`](/#tag/Process-Discovery) to define new processes. This includes:  * Choosing a intuitive and ideally unique name as process id, consisting of only letters (a-z), numbers and underscores. * Defining the parameters and their exact (JSON) schemes. * Specifying the return value of a process also with a (JSON) schema. * Providing examples or compliance tests. * Trying to make the process universally usable so that other back-end providers or openEO can adopt it.  **Users** MUST follow the schema for user-defined processes as in [`GET /process_graphs`](/#tag/User-Defined-Processes) to define new processes. This includes:  * Choosing a intuitive and ideally unique name as process id, consisting of only letters (a-z), numbers and underscores. * Defining the algorithm as a process graph. * Optionally, specifying the additional metadata for processes.  If new process are potentially useful for other back-ends the openEO consortium is happily accepting [pull requests](https://github.com/Open-EO/openeo-processes/pulls) to include them in the list of pre-defined processes.  ### Schemas  Each process parameter and the return values of a process define a schema that the value MUST comply to. The schemas are based on [JSON Schema draft-07](http://json-schema.org/).  Two custom keywords have been defined: * `subtype` for more fine-grained data-types than JSON Schema supports. * `parameters` to specify parameters that processes can pass to other process graphs.  ### Subtypes  JSON Schema allows to specify only a small set of native data types (string, boolean, number, integer, array, object, null). To support more fine grained data types, a custom [JSON Schema keyword](https://tools.ietf.org/html/draft-handrews-json-schema-01#section-6.4) has been defined: `subtype`. It works similarly as the JSON Schema keyword [`format`](https://tools.ietf.org/html/draft-handrews-json-schema-validation-01#section-7) and defines a number of subtypes for the native data types. These should be re-used in process schema definitions whenever suitable.  If a general data type such as `string` or `number` is used in a schema, all subtypes with the same parent data type can be passed, too. Clients should offer make passing subtypes as easy as passing a general data type. For example, a parameter accepting strings must also allow passing a string with subtype `date` and thus clients should encourage this by also providing a date-picker.  A [list of predefined subtypes](subtype-schemas.json) is available as JSON Schema.  ## Process Graphs  As defined above, a **process graph** is a chain of processes with explicit values for their parameters. Technically, a process graph is defined to be a graph of connected processes with exactly one node returning the final result:  ``` <ProcessGraph> := {   \"<ProcessNodeIdentifier>\": <ProcessNode>,   ... } ```  `<ProcessNodeIdentifier>` is a unique key within the process graph that is used to reference (the return value of) this process in arguments of other processes. The identifier is unique only strictly within itself, excluding any parent and child process graphs. Process node identifiers are also strictly scoped and can not be referenced from child or parent process graphs. Circular references are not allowed.  Note: We provide a non-binding [JSON Schema for basic process graph validation](assets/pg-schema.json).  ### Processes (Process Nodes)  A single node in a process graph (i.e. a specific instance of a process) is defined as follows:  ``` <ProcessNode> := {   \"process_id\": <string>,   \"description\": <string>,   \"arguments\": <Arguments>,   \"result\": true / false } ``` A process node MUST always contain key-value-pairs named `process_id` and `arguments`. It MAY contain a `description`.  One of the nodes in a map of processes (the final one) MUST have the `result` flag set to `true`, all the other nodes can omit it as the default value is `false`. Having such a node is important as multiple end nodes are possible, but in most use cases it is important to exactly specify the return value to be used by other processes. Each child process graph must also specify a result node similar to the \"main\" process graph.  `process_id` MUST be any of the pre-defined or user-defined process IDs, which are all listed at `GET /processes` and `GET /process_graphs`. An example is `load_collection` to retrieve data from a specific collection for processing.  ### Arguments  A process can have an arbitrary number of arguments. Their name and value are specified  in the process specification as an object of key-value pairs:  ``` <Arguments> := {   \"<ParameterName>\": <string|number|boolean|null|array|object|ResultReference|UserDefinedProcess|ParameterReference> } ```  **Notes:** - The specified data types are the native data types supported by JSON, except for `ResultReference`, `UserDefinedProcess` and `ParameterReference`. - Objects are not allowed to have keys with the following reserved names:      * `from_node`, except for objects of type `ResultReference`     * `process_graph`, except for objects of type `UserDefinedProcess`     * `from_parameter`, except for objects of type `ParameterReference`  - Arrays and objects can also contain a `ResultReference`, a `UserDefinedProcess` or a `ParameterReference`. So back-ends must *fully* traverse the process graphs, including all children.  ### Accessing results of other process nodes  A value of type `<ResultReference>` is an object with a key `from_node` and a `<ProcessNodeIdentifier>` as corresponding value:  ``` <ResultReference> := {   \"from_node\": \"<ProcessNodeIdentifier>\" } ```  This tells the back-end that the process expects the result (i.e. the return value) from another process node to be passed as argument. The `<ProcessNodeIdentifier>` is strictly scoped and can only reference nodes from within the same process graph, not child or parent process graphs.  ### User-defined process  A user-defined process in a process graph is a child process graph, to be evaluated as part of another process.  **Example**: You want to calculate the absolute value of each pixel in a data cube. This can be achieved in openeEO by executing the `apply` process and pass it a user-defined process as the \"operator\" to apply to each pixel. In this simple example, the \"child\" process graph defining the user-defined process consists of a single process `absolute`, but it can be arbitrairy complex in general.  A `<UserDefinedProcess>` argument must at least consist of an object with a key `process_graph`. Optionally, it can also be described with the same additional properties available for pre-defined processes such as an id, parameters, return values etc. When embedded in a process graph, these additional properties of a user-defined process are usually not used, except for validation purposes.  ``` <UserDefinedProcess> := {   \"process_graph\": <ProcessGraph>,   ... } ```  ### Accessing process parameters  A \"parent\" process that works with a user-defined process can make so called *process graph parameters* available to the \"child\" logic. Processes in the \"child\" process graph can access these parameters by passing a `ParameterReference` object as argument. It is an object with key `from_parameter` specifying the name of the process graph parameter:  ``` <ParameterReference> := {   \"from_parameter\": \"<ParameterReferenceName>\" } ```  The parameter names made available for `<ParameterReferenceName>` are defined and passed to the process graph by one of the parent entities. The parent could be a process (such as `apply` or `reduce_dimension`) or something else that executes a process graph (a secondary web service for example). If the parent is a process, the parameter are defined in the [`parameters` property](#section/Processes/Defining-Processes) of the corresponding JSON Schema.  In case of the example given above, the parameter `process` in the process [`apply`](https://processes.openeo.org/#apply) defines two process graph parameters: `x` (the value of each pixel that will be processed) and `context` (additional data passed through from the user). The process `absolute` expects an argument with the same name `x`. The process graph for the example would look as follows:  ``` {   \"process_id\": \"apply\",   \"arguments\": {     \"data\": {\"from_node\": \"loadcollection1\"}     \"process\": {       \"process_graph\": {         \"abs1\": {           \"process_id\": \"absolute\",           \"arguments\": {             \"x\": {\"from_parameter\": \"x\"}           },           \"result\": true         }       }     }   } } ```  `loadcollection1` would be a result from another process, which is not part of this example.  **Important:** `<ParameterReferenceName>` is less strictly scoped than `<ProcessNodeIdentifier>`. `<ParameterReferenceName>` can be any parameter from the process graph or any of its parents.  The value for the parameter must be resolved as follows: 1. In general the most specific parameter value is used. This means the parameter value is resolved starting from the current scope and then checking each parent for a suitable parameter value until a parameter values is found or the \"root\" process graph has been reached. 2. In case a parameter value is not available, the most unspecific default value from the process graph parameter definitions are used. For example, if default values are available for the root process graph and all children, the default value from the root process graph is used. 3. If no default values are available either, the error `ProcessParameterMissing` must be thrown.  ### Full example for an EVI computation  Deriving minimum EVI (Enhanced Vegetation Index) measurements over pixel time series of Sentinel 2 imagery. The main process graph in blue, child process graphs in yellow:  ![Graph with processing instructions](assets/pg-evi-example.png)  The process graph for the algorithm: [pg-evi-example.json](assets/pg-evi-example.json)  ## Data Processing  Processes can run in three different ways:  1. Results can be pre-computed by creating a ***batch job***. They are submitted to the back-end's processing system, but will remain inactive until explicitly put into the processing queue. They will run only once and store results after execution. Results can be downloaded. Batch jobs are typically time consuming and user interaction is not possible although log files are generated for them. This is the only mode that allows to get an estimate about time, volume and costs beforehand.  2. A more dynamic way of processing and accessing data is to create a **secondary web service**. They allow web-based access using different protocols such as [OGC WMS](http://www.opengeospatial.org/standards/wms) (Open Geospatial Consortium Web Map Service), [OGC WCS](http://www.opengeospatial.org/standards/wcs) (Web Coverage Service) or [XYZ tiles](https://wiki.openstreetmap.org/wiki/Slippy_map_tilenames). These protocols usually allow users to change the viewing extent or level of detail (zoom level). Therefore, computations often run *on demand* so that the requested data is calculated during the request. Back-ends should make sure to cache processed data to avoid additional/high costs and reduce waiting times for the user.  3. Processes can also be executed **on-demand** (i.e. synchronously). Results are delivered with the request itself and no job is created. Only lightweight computations, for example previews, should be executed using this approach as timeouts are to be expected for [long-polling HTTP requests](https://www.pubnub.com/blog/2014-12-01-http-long-polling/).  ### Validation  Process graph validation is a quite complex task. There's a [JSON schema](assets/pg-schema.json) for basic process graph validation. It checks the general structure of a process graph, but only checking against the schema is not fully validating a process graph. Note that this JSON Schema is probably good enough for a first version, but should be revised and improved for production. There are further steps to do:  1. Validate whether there's exactly one `result: true` per process graph. 2. Check whether the process names that are referenced in the field `process_id` are actually available. There's a custom format `process-id`, which can be used to check the value directly during validation against the JSON Schema. 3. Validate all arguments for each process against the JSON schemas that are specified in the corresponding process specifications. 4. Check whether the values specified for `from_node` have a corresponding node in the same process graph. 5. Validate whether the return value and the arguments requesting a return value with `from_node` are compatible. 7. Check the content of arrays and objects. These could include parameter and result references (`from_node`, `from_parameter` etc.).   ### Execution  To process the process graph on the back-end you need to go through all nodes/processes in the list and set for each node to which node it passes data and from which it expects data. In another iteration the back-end can find all start nodes for processing by checking for zero dependencies.  You can now start and execute the start nodes (in parallel, if possible). Results can be passed to the nodes that were identified beforehand. For each node that depends on multiple inputs you need to check whether all dependencies have already finished and only execute once the last dependency is ready.  Please be aware that the result node (`result` set to `true`) is not necessarily the last node that is executed. The author of the process graph may choose to set a non-end node to the result node!")
            .license("Apache 2.0")
            .licenseUrl("http://www.apache.org/licenses/LICENSE-2.0.html")
            .termsOfServiceUrl("https://openeo.org")
            .version("1.0.0-rc.2")
            .contact(new Contact("","", "openeo@list.tuwien.ac.at"))
            .build();
    }

    @Bean
    public Docket customImplementation(ServletContext servletContext, @Value("${openapi.openEO.base-path:/api/v1.0}") String basePath) {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                    .apis(RequestHandlerSelectors.basePackage("org.openeo.spring.api"))
                    .build()
                .pathProvider(new BasePathAwareRelativePathProvider(servletContext, basePath))
                .directModelSubstitute(java.time.LocalDate.class, java.sql.Date.class)
                .directModelSubstitute(java.time.OffsetDateTime.class, java.util.Date.class)
                .apiInfo(apiInfo());
    }

    class BasePathAwareRelativePathProvider extends RelativePathProvider {
        private String basePath;

        public BasePathAwareRelativePathProvider(ServletContext servletContext, String basePath) {
            super(servletContext);
            this.basePath = basePath;
        }

        @Override
        protected String applicationPath() {
            return  Paths.removeAdjacentForwardSlashes(UriComponentsBuilder.fromPath(super.applicationPath()).path(basePath).build().toString());
        }

        @Override
        public String getOperationPath(String operationPath) {
            UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromPath("/");
            return Paths.removeAdjacentForwardSlashes(
                    uriComponentsBuilder.path(operationPath.replaceFirst("^" + basePath, "")).build().toString());
        }
    }

}
