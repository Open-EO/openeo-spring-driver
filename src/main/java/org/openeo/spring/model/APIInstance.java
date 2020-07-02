package org.openeo.spring.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.net.URI;
import org.openapitools.jackson.nullable.JsonNullable;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * APIInstance
 */
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2020-07-02T08:45:00.334+02:00[Europe/Rome]")
public class APIInstance   {
  @JsonProperty("url")
  private URI url;

  @JsonProperty("production")
  private Boolean production = false;

  @JsonProperty("api_version")
  private String apiVersion;

  public APIInstance url(URI url) {
    this.url = url;
    return this;
  }

  /**
   * *Absolute* URLs to the service.
   * @return url
  */
  @ApiModelProperty(example = "https://example.com/api/v1.0", required = true, value = "*Absolute* URLs to the service.")
  @NotNull

  @Valid

  public URI getUrl() {
    return url;
  }

  public void setUrl(URI url) {
    this.url = url;
  }

  public APIInstance production(Boolean production) {
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

  public APIInstance apiVersion(String apiVersion) {
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


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    APIInstance apIInstance = (APIInstance) o;
    return Objects.equals(this.url, apIInstance.url) &&
        Objects.equals(this.production, apIInstance.production) &&
        Objects.equals(this.apiVersion, apIInstance.apiVersion);
  }

  @Override
  public int hashCode() {
    return Objects.hash(url, production, apiVersion);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class APIInstance {\n");
    
    sb.append("    url: ").append(toIndentedString(url)).append("\n");
    sb.append("    production: ").append(toIndentedString(production)).append("\n");
    sb.append("    apiVersion: ").append(toIndentedString(apiVersion)).append("\n");
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

