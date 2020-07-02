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
 * ProgrammingLanguageLibrary
 */
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2020-07-02T08:31:05.442+02:00[Europe/Rome]")
public class ProgrammingLanguageLibrary   {
  @JsonProperty("version")
  private String version;

  @JsonProperty("deprecated")
  private Boolean deprecated = false;

  @JsonProperty("links")
  @Valid
  private List<Link> links = null;

  public ProgrammingLanguageLibrary version(String version) {
    this.version = version;
    return this;
  }

  /**
   * Version number of the library.
   * @return version
  */
  @ApiModelProperty(required = true, value = "Version number of the library.")
  @NotNull


  public String getVersion() {
    return version;
  }

  public void setVersion(String version) {
    this.version = version;
  }

  public ProgrammingLanguageLibrary deprecated(Boolean deprecated) {
    this.deprecated = deprecated;
    return this;
  }

  /**
   * Specifies that the library is deprecated with the potential to be removed in any of the next versions. It should be transitioned out of usage as soon as possible and users should refrain from using it in new implementations.  A link with relation type `latest-version` SHOULD be added to the `links` and MUST refer to the library or library version that can be used instead.
   * @return deprecated
  */
  @ApiModelProperty(value = "Specifies that the library is deprecated with the potential to be removed in any of the next versions. It should be transitioned out of usage as soon as possible and users should refrain from using it in new implementations.  A link with relation type `latest-version` SHOULD be added to the `links` and MUST refer to the library or library version that can be used instead.")


  public Boolean isDeprecated() {
    return deprecated;
  }

  public void setDeprecated(Boolean deprecated) {
    this.deprecated = deprecated;
  }

  public ProgrammingLanguageLibrary links(List<Link> links) {
    this.links = links;
    return this;
  }

  public ProgrammingLanguageLibrary addLinksItem(Link linksItem) {
    if (this.links == null) {
      this.links = new ArrayList<>();
    }
    this.links.add(linksItem);
    return this;
  }

  /**
   * Additional links related to this library, e.g. external documentation for this library.  It is highly RECOMMENDED to provide links with the following `rel` (relation) types:  1. `about`: A resource that further explains the library, e.g. a user guide or the documentation.  2. `latest-version`: If a library has been marked as deprecated, a link should point to either a new library replacing the deprecated library or a latest version of the library available at the back-end.  For additional relation types see also the lists of [common relation types in openEO](#section/API-Principles/Web-Linking).
   * @return links
  */
  @ApiModelProperty(value = "Additional links related to this library, e.g. external documentation for this library.  It is highly RECOMMENDED to provide links with the following `rel` (relation) types:  1. `about`: A resource that further explains the library, e.g. a user guide or the documentation.  2. `latest-version`: If a library has been marked as deprecated, a link should point to either a new library replacing the deprecated library or a latest version of the library available at the back-end.  For additional relation types see also the lists of [common relation types in openEO](#section/API-Principles/Web-Linking).")

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
    ProgrammingLanguageLibrary programmingLanguageLibrary = (ProgrammingLanguageLibrary) o;
    return Objects.equals(this.version, programmingLanguageLibrary.version) &&
        Objects.equals(this.deprecated, programmingLanguageLibrary.deprecated) &&
        Objects.equals(this.links, programmingLanguageLibrary.links);
  }

  @Override
  public int hashCode() {
    return Objects.hash(version, deprecated, links);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ProgrammingLanguageLibrary {\n");
    
    sb.append("    version: ").append(toIndentedString(version)).append("\n");
    sb.append("    deprecated: ").append(toIndentedString(deprecated)).append("\n");
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

