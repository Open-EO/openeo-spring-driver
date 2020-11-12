package org.openeo.spring.model;

import java.net.URI;
import java.util.Objects;

import javax.persistence.Embeddable;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * A link to another resource on the web. Bases on [RFC 5899](https://tools.ietf.org/html/rfc5988).
 */
@Embeddable
@ApiModel(description = "A link to another resource on the web. Bases on [RFC 5899](https://tools.ietf.org/html/rfc5988).")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2020-07-02T08:45:00.334+02:00[Europe/Rome]")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BandSummary   {
  @JsonProperty("rel")
  private String rel;

  @JsonProperty("href")
  private URI href;

  @JsonProperty("type")
  private String type;

  @JsonProperty("title")
  private String title;

  public BandSummary rel(String rel) {
    this.rel = rel;
    return this;
  }

  /**
   * Relationship between the current document and the linked document. SHOULD be a [registered link relation type](https://www.iana.org/assignments/link-relations/link-relations.xml) whenever feasible.
   * @return rel
  */
  @ApiModelProperty(example = "related", required = true, value = "Relationship between the current document and the linked document. SHOULD be a [registered link relation type](https://www.iana.org/assignments/link-relations/link-relations.xml) whenever feasible.")
  @NotNull


  public String getRel() {
    return rel;
  }

  public void setRel(String rel) {
    this.rel = rel;
  }

  public BandSummary href(URI href) {
    this.href = href;
    return this;
  }

  /**
   * The value MUST be a valid URL.
   * @return href
  */
  @ApiModelProperty(example = "https://example.openeo.org", required = true, value = "The value MUST be a valid URL.")
  @NotNull

  @Valid

  public URI getHref() {
    return href;
  }

  public void setHref(URI href) {
    this.href = href;
  }

  public BandSummary type(String type) {
    this.type = type;
    return this;
  }

  /**
   * The value MUST be a string that hints at the format used to represent data at the provided URI, preferably a media (MIME) type.
   * @return type
  */
  @ApiModelProperty(example = "text/html", value = "The value MUST be a string that hints at the format used to represent data at the provided URI, preferably a media (MIME) type.")


  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public BandSummary title(String title) {
    this.title = title;
    return this;
  }

  /**
   * Used as a human-readable label for a link.
   * @return title
  */
  @ApiModelProperty(example = "openEO", value = "Used as a human-readable label for a link.")


  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    BandSummary bandsummary = (BandSummary) o;
    return Objects.equals(this.rel, bandsummary.rel) &&
        Objects.equals(this.href, bandsummary.href) &&
        Objects.equals(this.type, bandsummary.type) &&
        Objects.equals(this.title, bandsummary.title);
  }

  @Override
  public int hashCode() {
    return Objects.hash(rel, href, type, title);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Link {\n");
    
    sb.append("    rel: ").append(toIndentedString(rel)).append("\n");
    sb.append("    href: ").append(toIndentedString(href)).append("\n");
    sb.append("    type: ").append(toIndentedString(type)).append("\n");
    sb.append("    title: ").append(toIndentedString(title)).append("\n");
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

