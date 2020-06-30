package org.openeo.spring.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.openapitools.jackson.nullable.JsonNullable;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * Data that is expected to be passed from another process.
 */
@ApiModel(description = "Data that is expected to be passed from another process.")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2020-06-30T14:48:14.663+02:00[Europe/Rome]")
public class ResultReference   {
  @JsonProperty("from_node")
  private String fromNode;

  public ResultReference fromNode(String fromNode) {
    this.fromNode = fromNode;
    return this;
  }

  /**
   * The ID of the node that data is expected to come from.
   * @return fromNode
  */
  @ApiModelProperty(required = true, value = "The ID of the node that data is expected to come from.")
  @NotNull


  public String getFromNode() {
    return fromNode;
  }

  public void setFromNode(String fromNode) {
    this.fromNode = fromNode;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ResultReference resultReference = (ResultReference) o;
    return Objects.equals(this.fromNode, resultReference.fromNode);
  }

  @Override
  public int hashCode() {
    return Objects.hash(fromNode);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ResultReference {\n");
    
    sb.append("    fromNode: ").append(toIndentedString(fromNode)).append("\n");
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

