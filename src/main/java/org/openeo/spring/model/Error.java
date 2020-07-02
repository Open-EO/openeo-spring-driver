package org.openeo.spring.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.ArrayList;
import java.util.List;
import org.openeo.spring.model.Link;
import org.openapitools.jackson.nullable.JsonNullable;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * An error object declares additional information about a client-side or server-side error. See also: * [Error Handling](#section/API-Principles/Error-Handling) in the API in general. * [Common Error Codes](errors.json)
 */
@ApiModel(description = "An error object declares additional information about a client-side or server-side error. See also: * [Error Handling](#section/API-Principles/Error-Handling) in the API in general. * [Common Error Codes](errors.json)")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2020-07-02T08:31:05.442+02:00[Europe/Rome]")
public class Error   {
  @JsonProperty("id")
  private String id;

  @JsonProperty("code")
  private String code;

  @JsonProperty("message")
  private String message;

  @JsonProperty("links")
  @Valid
  private List<Link> links = null;

  public Error id(String id) {
    this.id = id;
    return this;
  }

  /**
   * A back-end may add a unique identifier to the error response to be able to log and track errors with further non-disclosable details. A client could communicate this id to a back-end provider to get further information.
   * @return id
  */
  @ApiModelProperty(example = "550e8400-e29b-11d4-a716-446655440000", value = "A back-end may add a unique identifier to the error response to be able to log and track errors with further non-disclosable details. A client could communicate this id to a back-end provider to get further information.")


  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public Error code(String code) {
    this.code = code;
    return this;
  }

  /**
   * The code is either one of the standardized error codes or a custom code, for example specified by a user in the `debug` process.
   * @return code
  */
  @ApiModelProperty(example = "SampleError", required = true, value = "The code is either one of the standardized error codes or a custom code, for example specified by a user in the `debug` process.")
  @NotNull


  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public Error message(String message) {
    this.message = message;
    return this;
  }

  /**
   * A message explaining what the client may need to change or what difficulties the server is facing.
   * @return message
  */
  @ApiModelProperty(example = "Parameter 'sample' is missing.", required = true, value = "A message explaining what the client may need to change or what difficulties the server is facing.")
  @NotNull


  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public Error links(List<Link> links) {
    this.links = links;
    return this;
  }

  public Error addLinksItem(Link linksItem) {
    if (this.links == null) {
      this.links = new ArrayList<>();
    }
    this.links.add(linksItem);
    return this;
  }

  /**
   * Links related to this log entry / error, e.g. to a resource that provides further explanations.  For relation types see the lists of [common relation types in openEO](#section/API-Principles/Web-Linking).
   * @return links
  */
  @ApiModelProperty(example = "[{\"href\":\"https://example.openeo.org/docs/errors/SampleError\",\"rel\":\"about\"}]", value = "Links related to this log entry / error, e.g. to a resource that provides further explanations.  For relation types see the lists of [common relation types in openEO](#section/API-Principles/Web-Linking).")

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
    Error error = (Error) o;
    return Objects.equals(this.id, error.id) &&
        Objects.equals(this.code, error.code) &&
        Objects.equals(this.message, error.message) &&
        Objects.equals(this.links, error.links);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, code, message, links);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Error {\n");
    
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    code: ").append(toIndentedString(code)).append("\n");
    sb.append("    message: ").append(toIndentedString(message)).append("\n");
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

