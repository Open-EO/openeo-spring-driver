package org.openeo.spring.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.ArrayList;
import java.util.List;
import org.openeo.spring.model.Billing;
import org.openeo.spring.model.Endpoint;
import org.openeo.spring.model.Link;
import org.openapitools.jackson.nullable.JsonNullable;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * CapabilitiesResponse
 */
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2020-06-30T15:12:47.411+02:00[Europe/Rome]")
public class CapabilitiesResponse   {
  @JsonProperty("api_version")
  private String apiVersion;

  @JsonProperty("backend_version")
  private String backendVersion;

  @JsonProperty("stac_version")
  private String stacVersion;

  @JsonProperty("id")
  private String id;

  @JsonProperty("title")
  private String title;

  @JsonProperty("description")
  private String description;

  @JsonProperty("production")
  private Boolean production = false;

  @JsonProperty("endpoints")
  @Valid
  private List<Endpoint> endpoints = new ArrayList<>();

  @JsonProperty("billing")
  private Billing billing;

  @JsonProperty("links")
  @Valid
  private List<Link> links = new ArrayList<>();

  public CapabilitiesResponse apiVersion(String apiVersion) {
    this.apiVersion = apiVersion;
    return this;
  }

  /**
   * Version number of the openEO specification this back-end implements.
   * @return apiVersion
  */
  @ApiModelProperty(example = "1.0.1", required = true, value = "Version number of the openEO specification this back-end implements.")
  @NotNull


  public String getApiVersion() {
    return apiVersion;
  }

  public void setApiVersion(String apiVersion) {
    this.apiVersion = apiVersion;
  }

  public CapabilitiesResponse backendVersion(String backendVersion) {
    this.backendVersion = backendVersion;
    return this;
  }

  /**
   * Version number of the back-end implementation. Every change on back-end side MUST cause a change of the version number.
   * @return backendVersion
  */
  @ApiModelProperty(example = "1.1.2", required = true, value = "Version number of the back-end implementation. Every change on back-end side MUST cause a change of the version number.")
  @NotNull


  public String getBackendVersion() {
    return backendVersion;
  }

  public void setBackendVersion(String backendVersion) {
    this.backendVersion = backendVersion;
  }

  public CapabilitiesResponse stacVersion(String stacVersion) {
    this.stacVersion = stacVersion;
    return this;
  }

  /**
   * The [version of the STAC specification](https://github.com/radiantearth/stac-spec/releases), which MAY not be equal to the [STAC API version](#section/STAC). Supports versions 0.9.x and 1.x.x.
   * @return stacVersion
  */
  @ApiModelProperty(required = true, value = "The [version of the STAC specification](https://github.com/radiantearth/stac-spec/releases), which MAY not be equal to the [STAC API version](#section/STAC). Supports versions 0.9.x and 1.x.x.")
  @NotNull


  public String getStacVersion() {
    return stacVersion;
  }

  public void setStacVersion(String stacVersion) {
    this.stacVersion = stacVersion;
  }

  public CapabilitiesResponse id(String id) {
    this.id = id;
    return this;
  }

  /**
   * Identifier for the service. This field originates from STAC and is used as unique identifier for the STAC catalog available at `/collections`.
   * @return id
  */
  @ApiModelProperty(example = "cool-eo-cloud", required = true, value = "Identifier for the service. This field originates from STAC and is used as unique identifier for the STAC catalog available at `/collections`.")
  @NotNull


  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public CapabilitiesResponse title(String title) {
    this.title = title;
    return this;
  }

  /**
   * The name of the service.
   * @return title
  */
  @ApiModelProperty(example = "Cool EO Cloud", required = true, value = "The name of the service.")
  @NotNull


  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public CapabilitiesResponse description(String description) {
    this.description = description;
    return this;
  }

  /**
   * A description of the service, which allows the service provider to introduce the user to its service. [CommonMark 0.29](http://commonmark.org/) syntax MAY be used for rich text representation.
   * @return description
  */
  @ApiModelProperty(example = "This service is provided to you by [Cool EO Cloud Corp.](http://cool-eo-cloud-corp.com). It implements the full openEO API and allows to process a range of 999 EO data sets, including   * Sentinel 1/2/3 and 5 * Landsat 7/8  A free plan is available to test the service. For further information please contact our customer service at [support@cool-eo-cloud-corp.com](mailto:support@cool-eo-cloud-corp.com).", required = true, value = "A description of the service, which allows the service provider to introduce the user to its service. [CommonMark 0.29](http://commonmark.org/) syntax MAY be used for rich text representation.")
  @NotNull


  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public CapabilitiesResponse production(Boolean production) {
    this.production = production;
    return this;
  }

  /**
   * Specifies whether the implementation is ready to be used in production use (`true`) or not (`false`). Clients SHOULD only connect to non-production implementations if the user explicitly confirmed to use a non-production implementation. This flag is part of `GET /.well-known/openeo` and `GET /`. It must be used consistently in both endpoints.
   * @return production
  */
  @ApiModelProperty(value = "Specifies whether the implementation is ready to be used in production use (`true`) or not (`false`). Clients SHOULD only connect to non-production implementations if the user explicitly confirmed to use a non-production implementation. This flag is part of `GET /.well-known/openeo` and `GET /`. It must be used consistently in both endpoints.")


  public Boolean isProduction() {
    return production;
  }

  public void setProduction(Boolean production) {
    this.production = production;
  }

  public CapabilitiesResponse endpoints(List<Endpoint> endpoints) {
    this.endpoints = endpoints;
    return this;
  }

  public CapabilitiesResponse addEndpointsItem(Endpoint endpointsItem) {
    this.endpoints.add(endpointsItem);
    return this;
  }

  /**
   * Lists all supported endpoints. Supported are all endpoints, which are implemented, return a 2XX or 3XX HTTP status code and are fully compatible to the API specification.
   * @return endpoints
  */
  @ApiModelProperty(example = "[{\"path\":\"/collections\",\"methods\":[\"GET\"]},{\"path\":\"/collections/{collection_id}\",\"methods\":[\"GET\"]},{\"path\":\"/processes\",\"methods\":[\"GET\"]},{\"path\":\"/jobs\",\"methods\":[\"GET\",\"POST\"]},{\"path\":\"/jobs/{job_id}\",\"methods\":[\"GET\",\"DELETE\",\"PATCH\"]}]", required = true, value = "Lists all supported endpoints. Supported are all endpoints, which are implemented, return a 2XX or 3XX HTTP status code and are fully compatible to the API specification.")
  @NotNull

  @Valid

  public List<Endpoint> getEndpoints() {
    return endpoints;
  }

  public void setEndpoints(List<Endpoint> endpoints) {
    this.endpoints = endpoints;
  }

  public CapabilitiesResponse billing(Billing billing) {
    this.billing = billing;
    return this;
  }

  /**
   * Get billing
   * @return billing
  */
  @ApiModelProperty(value = "")

  @Valid

  public Billing getBilling() {
    return billing;
  }

  public void setBilling(Billing billing) {
    this.billing = billing;
  }

  public CapabilitiesResponse links(List<Link> links) {
    this.links = links;
    return this;
  }

  public CapabilitiesResponse addLinksItem(Link linksItem) {
    this.links.add(linksItem);
    return this;
  }

  /**
   * Links related to this service, e.g. the homepage of the service provider or the terms of service.  It is highly RECOMMENDED to provide links with the following `rel` (relation) types:  1. `version-history`: A link back to the Well-Known URL (see `/.well-known/openeo`) to allow clients to work on the most recent version.  2. `terms-of-service`: A link to the terms of service. If a back-end provides a link to the terms of service, the clients MUST provide a way to read the terms of service and only connect to the back-end after the user agreed to them. The user interface MUST be designed in a way that the terms of service are not agreed to by default, i.e. the user MUST explicitly agree to them.  3. `privacy-policy`: A link to the privacy policy (GDPR). If a back-end provides a link to a privacy policy, the clients MUST provide a way to read the privacy policy and only connect to the back-end after the user agreed to them. The user interface MUST be designed in a way that the privacy policy is not agreed to by default, i.e. the user MUST explicitly agree to them.  4. `service-desc` or `service-doc`: A link to the API definition. Use `service-desc` for machine-readable API definition and  `service-doc` for human-readable API definition. Required if full OGC API compatibility is desired.  5. `conformance`: A link to the Conformance declaration (see `/conformance`).  Required if full OGC API compatibility is desired.  6. `data`: A link to the collections (see `/collections`). Required if full OGC API compatibility is desired.  For additional relation types see also the lists of [common relation types in openEO](#section/API-Principles/Web-Linking).
   * @return links
  */
  @ApiModelProperty(example = "[{\"href\":\"http://www.cool-cloud-corp.com\",\"rel\":\"about\",\"type\":\"text/html\",\"title\":\"Homepage of the service provider\"},{\"href\":\"https://www.cool-cloud-corp.com/tos\",\"rel\":\"terms-of-service\",\"type\":\"text/html\",\"title\":\"Terms of Service\"},{\"href\":\"https://www.cool-cloud-corp.com/privacy\",\"rel\":\"privacy-policy\",\"type\":\"text/html\",\"title\":\"Privacy Policy\"},{\"href\":\"http://www.cool-cloud-corp.com/.well-known/openeo\",\"rel\":\"version-history\",\"type\":\"application/json\",\"title\":\"List of supported openEO versions\"},{\"href\":\"http://www.cool-cloud-corp.com/api/v1.0/conformance\",\"rel\":\"conformance\",\"type\":\"application/json\",\"title\":\"OGC Conformance Classes\"},{\"href\":\"http://www.cool-cloud-corp.com/api/v1.0/collections\",\"rel\":\"data\",\"type\":\"application/json\",\"title\":\"List of Datasets\"}]", required = true, value = "Links related to this service, e.g. the homepage of the service provider or the terms of service.  It is highly RECOMMENDED to provide links with the following `rel` (relation) types:  1. `version-history`: A link back to the Well-Known URL (see `/.well-known/openeo`) to allow clients to work on the most recent version.  2. `terms-of-service`: A link to the terms of service. If a back-end provides a link to the terms of service, the clients MUST provide a way to read the terms of service and only connect to the back-end after the user agreed to them. The user interface MUST be designed in a way that the terms of service are not agreed to by default, i.e. the user MUST explicitly agree to them.  3. `privacy-policy`: A link to the privacy policy (GDPR). If a back-end provides a link to a privacy policy, the clients MUST provide a way to read the privacy policy and only connect to the back-end after the user agreed to them. The user interface MUST be designed in a way that the privacy policy is not agreed to by default, i.e. the user MUST explicitly agree to them.  4. `service-desc` or `service-doc`: A link to the API definition. Use `service-desc` for machine-readable API definition and  `service-doc` for human-readable API definition. Required if full OGC API compatibility is desired.  5. `conformance`: A link to the Conformance declaration (see `/conformance`).  Required if full OGC API compatibility is desired.  6. `data`: A link to the collections (see `/collections`). Required if full OGC API compatibility is desired.  For additional relation types see also the lists of [common relation types in openEO](#section/API-Principles/Web-Linking).")
  @NotNull

  @Valid

  public List<Link> getLinks() {
    return links;
  }

  public void setLinks(List<Link> links) {
    this.links = links;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CapabilitiesResponse capabilitiesResponse = (CapabilitiesResponse) o;
    return Objects.equals(this.apiVersion, capabilitiesResponse.apiVersion) &&
        Objects.equals(this.backendVersion, capabilitiesResponse.backendVersion) &&
        Objects.equals(this.stacVersion, capabilitiesResponse.stacVersion) &&
        Objects.equals(this.id, capabilitiesResponse.id) &&
        Objects.equals(this.title, capabilitiesResponse.title) &&
        Objects.equals(this.description, capabilitiesResponse.description) &&
        Objects.equals(this.production, capabilitiesResponse.production) &&
        Objects.equals(this.endpoints, capabilitiesResponse.endpoints) &&
        Objects.equals(this.billing, capabilitiesResponse.billing) &&
        Objects.equals(this.links, capabilitiesResponse.links);
  }

  @Override
  public int hashCode() {
    return Objects.hash(apiVersion, backendVersion, stacVersion, id, title, description, production, endpoints, billing, links);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class CapabilitiesResponse {\n");
    
    sb.append("    apiVersion: ").append(toIndentedString(apiVersion)).append("\n");
    sb.append("    backendVersion: ").append(toIndentedString(backendVersion)).append("\n");
    sb.append("    stacVersion: ").append(toIndentedString(stacVersion)).append("\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    title: ").append(toIndentedString(title)).append("\n");
    sb.append("    description: ").append(toIndentedString(description)).append("\n");
    sb.append("    production: ").append(toIndentedString(production)).append("\n");
    sb.append("    endpoints: ").append(toIndentedString(endpoints)).append("\n");
    sb.append("    billing: ").append(toIndentedString(billing)).append("\n");
    sb.append("    links: ").append(toIndentedString(links)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(java.lang.Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}

