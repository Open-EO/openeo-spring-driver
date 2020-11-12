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
  @JsonProperty("name")
  private String name;

  @JsonProperty("common_name")
  private String commonname;

  @JsonProperty("center_wavelength")
  private double centerwavelength;

  @JsonProperty("gsd")
  private double gsd;

  public BandSummary name(String name) {
    this.name = name;
    return this;
  }

  /**
   * Relationship between the current document and the linked document. SHOULD be a [registered link relation type](https://www.iana.org/assignments/link-relations/link-relations.xml) whenever feasible.
   * @return rel
  */
  @ApiModelProperty(example = "name", required = true, value = "Relationship between the current document and the linked document. SHOULD be a [registered link relation type](https://www.iana.org/assignments/link-relations/link-relations.xml) whenever feasible.")
  @NotNull


  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public BandSummary commonname(String commonname) {
    this.commonname = commonname;
    return this;
  }

  /**
   * The value MUST be a valid URL.
   * @return href
  */
  @ApiModelProperty(example = "Red", required = true, value = "The value MUST be a valid common name.")
  @NotNull

  @Valid

  public String getCommonname() {
    return commonname;
  }

  public void setCommonname(String commonname) {
    this.commonname = commonname;
  }

  public BandSummary centerwavelength(double centerwavelength) {
    this.centerwavelength = centerwavelength;
    return this;
  }

  /**
   * The value MUST be a string that hints at the format used to represent data at the provided URI, preferably a media (MIME) type.
   * @return type
  */
  @ApiModelProperty(example = "0.773", value = "The value MUST be a value that hints at the format used to represent data at the provided URI, preferably a media (MIME) type.")


  public double getCenterwavelength() {
    return centerwavelength;
  }

  public void setCenterwavelength(double centerwavelength) {
    this.centerwavelength = centerwavelength;
  }

  public BandSummary gsd(double gsd) {
    this.gsd = gsd;
    return this;
  }

  /**
   * Used as a human-readable label for a link.
   * @return title
  */
  @ApiModelProperty(example = "10.11", value = "The value MUST be a valid Number.")


  public double getGsd() {
    return gsd;
  }

  public void setGsd(double gsd) {
    this.gsd = gsd;
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
    return Objects.equals(this.name, bandsummary.name) &&
        Objects.equals(this.commonname, bandsummary.commonname) &&
        Objects.equals(this.centerwavelength, bandsummary.centerwavelength) &&
        Objects.equals(this.gsd, bandsummary.gsd);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, commonname, centerwavelength, gsd);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Link {\n");
    
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    common_name: ").append(toIndentedString(commonname)).append("\n");
    sb.append("    center_wavelength: ").append(toIndentedString(centerwavelength)).append("\n");
    sb.append("    gsd: ").append(toIndentedString(gsd)).append("\n");
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

