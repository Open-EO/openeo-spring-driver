package org.openeo.spring.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import org.openapitools.jackson.nullable.JsonNullable;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * OGCConformanceResponse
 */
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2020-06-30T14:48:14.663+02:00[Europe/Rome]")
public class OGCConformanceResponse   {
  @JsonProperty("conformsTo")
  @Valid
  private List<URI> conformsTo = new ArrayList<>();

  public OGCConformanceResponse conformsTo(List<URI> conformsTo) {
    this.conformsTo = conformsTo;
    return this;
  }

  public OGCConformanceResponse addConformsToItem(URI conformsToItem) {
    this.conformsTo.add(conformsToItem);
    return this;
  }

  /**
   * Get conformsTo
   * @return conformsTo
  */
  @ApiModelProperty(required = true, value = "")
  @NotNull

  @Valid

  public List<URI> getConformsTo() {
    return conformsTo;
  }

  public void setConformsTo(List<URI> conformsTo) {
    this.conformsTo = conformsTo;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    OGCConformanceResponse ogCConformanceResponse = (OGCConformanceResponse) o;
    return Objects.equals(this.conformsTo, ogCConformanceResponse.conformsTo);
  }

  @Override
  public int hashCode() {
    return Objects.hash(conformsTo);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class OGCConformanceResponse {\n");
    
    sb.append("    conformsTo: ").append(toIndentedString(conformsTo)).append("\n");
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

