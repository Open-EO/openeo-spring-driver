package org.openeo.spring.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.ArrayList;
import java.util.List;
import org.openeo.spring.model.Link;
import org.openapitools.jackson.nullable.JsonNullable;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * UdfRuntime
 */
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2020-07-02T08:45:00.334+02:00[Europe/Rome]")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "type", visible = true)
@JsonSubTypes({
  @JsonSubTypes.Type(value = UdfProgrammingLanguage.class, name = "language"),
  @JsonSubTypes.Type(value = UdfDocker.class, name = "docker"),
})

public class UdfRuntime   {
  @JsonProperty("title")
  private String title;

  @JsonProperty("description")
  private String description;

  @JsonProperty("type")
  private String type;

  @JsonProperty("default")
  private String _default;

  @JsonProperty("links")
  @Valid
  private List<Link> links = null;

  public UdfRuntime title(String title) {
    this.title = title;
    return this;
  }

  /**
   * A human-readable short title to be displayed to users **in addition** to the names specified in the keys. This property is only for better user experience so that users can understand the names better. Example titles could be `GeoTiff` for the key `GTiff` (for file formats) or `OGC Web Map Service` for the key `WMS` (for service types). The title MUST NOT be used in communication (e.g. in process graphs), although clients MAY translate the titles into the corresponding names.
   * @return title
  */
  @ApiModelProperty(value = "A human-readable short title to be displayed to users **in addition** to the names specified in the keys. This property is only for better user experience so that users can understand the names better. Example titles could be `GeoTiff` for the key `GTiff` (for file formats) or `OGC Web Map Service` for the key `WMS` (for service types). The title MUST NOT be used in communication (e.g. in process graphs), although clients MAY translate the titles into the corresponding names.")


  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public UdfRuntime description(String description) {
    this.description = description;
    return this;
  }

  /**
   * Detailed description to explain the entity.  [CommonMark 0.29](http://commonmark.org/) syntax MAY be used for rich text representation.
   * @return description
  */
  @ApiModelProperty(value = "Detailed description to explain the entity.  [CommonMark 0.29](http://commonmark.org/) syntax MAY be used for rich text representation.")


  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public UdfRuntime type(String type) {
    this.type = type;
    return this;
  }

  /**
   * The type of the UDF runtime.  Pre-defined types are: * `language` for Programming Languages and * `docker` for Docker Containers.  The types can potentially be extended by back-ends.
   * @return type
  */
  @ApiModelProperty(required = true, value = "The type of the UDF runtime.  Pre-defined types are: * `language` for Programming Languages and * `docker` for Docker Containers.  The types can potentially be extended by back-ends.")
  @NotNull


  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public UdfRuntime _default(String _default) {
    this._default = _default;
    return this;
  }

  /**
   * Get _default
   * @return _default
  */
  @ApiModelProperty(value = "")


  public String getDefault() {
    return _default;
  }

  public void setDefault(String _default) {
    this._default = _default;
  }

  public UdfRuntime links(List<Link> links) {
    this.links = links;
    return this;
  }

  public UdfRuntime addLinksItem(Link linksItem) {
    if (this.links == null) {
      this.links = new ArrayList<>();
    }
    this.links.add(linksItem);
    return this;
  }

  /**
   * Links related to this runtime, e.g. external documentation.  It is highly RECOMMENDED to provide at least links with the following `rel` (relation) types:  1. `about`: A resource that further explains the runtime, e.g. a user guide or the documentation.  For additional relation types see also the lists of [common relation types in openEO](#section/API-Principles/Web-Linking).
   * @return links
  */
  @ApiModelProperty(value = "Links related to this runtime, e.g. external documentation.  It is highly RECOMMENDED to provide at least links with the following `rel` (relation) types:  1. `about`: A resource that further explains the runtime, e.g. a user guide or the documentation.  For additional relation types see also the lists of [common relation types in openEO](#section/API-Principles/Web-Linking).")

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
    UdfRuntime udfRuntime = (UdfRuntime) o;
    return Objects.equals(this.title, udfRuntime.title) &&
        Objects.equals(this.description, udfRuntime.description) &&
        Objects.equals(this.type, udfRuntime.type) &&
        Objects.equals(this._default, udfRuntime._default) &&
        Objects.equals(this.links, udfRuntime.links);
  }

  @Override
  public int hashCode() {
    return Objects.hash(title, description, type, _default, links);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class UdfRuntime {\n");
    
    sb.append("    title: ").append(toIndentedString(title)).append("\n");
    sb.append("    description: ").append(toIndentedString(description)).append("\n");
    sb.append("    type: ").append(toIndentedString(type)).append("\n");
    sb.append("    _default: ").append(toIndentedString(_default)).append("\n");
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

