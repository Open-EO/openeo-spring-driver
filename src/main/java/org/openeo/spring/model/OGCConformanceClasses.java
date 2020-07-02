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
 * OGCConformanceClasses
 */
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2020-07-02T08:45:00.334+02:00[Europe/Rome]")
public class OGCConformanceClasses   {
  @JsonProperty("conformsTo")
  @Valid
  private List<URI> conformsTo = new ArrayList<>();

  public OGCConformanceClasses conformsTo(List<URI> conformsTo) {
    this.conformsTo = conformsTo;
    return this;
  }

  public OGCConformanceClasses addConformsToItem(URI conformsToItem) {
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
    OGCConformanceClasses ogCConformanceClasses = (OGCConformanceClasses) o;
    return Objects.equals(this.conformsTo, ogCConformanceClasses.conformsTo);
  }

  @Override
  public int hashCode() {
    return Objects.hash(conformsTo);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class OGCConformanceClasses {\n");
    
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

