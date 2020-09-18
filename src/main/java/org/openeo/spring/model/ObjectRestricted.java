package org.openeo.spring.model;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModelProperty;

/**
 * ObjectRestricted
 */
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2020-07-02T08:45:00.334+02:00[Europe/Rome]")
public class ObjectRestricted   {
  @JsonProperty("from_parameter")
  private Object fromParameter = null;

  @JsonProperty("from_node")
  private Object fromNode = null;

  @JsonProperty("process_graph")
  private Object processGraph = null;

  public ObjectRestricted fromParameter(Object fromParameter) {
    this.fromParameter = fromParameter;
    return this;
  }

  /**
   * Get fromParameter
   * @return fromParameter
  */
  @ApiModelProperty(value = "")


  public Object getFromParameter() {
    return fromParameter;
  }

  public void setFromParameter(Object fromParameter) {
    this.fromParameter = fromParameter;
  }

  public ObjectRestricted fromNode(Object fromNode) {
    this.fromNode = fromNode;
    return this;
  }

  /**
   * Get fromNode
   * @return fromNode
  */
  @ApiModelProperty(value = "")


  public Object getFromNode() {
    return fromNode;
  }

  public void setFromNode(Object fromNode) {
    this.fromNode = fromNode;
  }

  public ObjectRestricted processGraph(Object processGraph) {
    this.processGraph = processGraph;
    return this;
  }

  /**
   * Get processGraph
   * @return processGraph
  */
  @ApiModelProperty(value = "")


  public Object getProcessGraph() {
    return processGraph;
  }

  public void setProcessGraph(Object processGraph) {
    this.processGraph = processGraph;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ObjectRestricted objectRestricted = (ObjectRestricted) o;
    return Objects.equals(this.fromParameter, objectRestricted.fromParameter) &&
        Objects.equals(this.fromNode, objectRestricted.fromNode) &&
        Objects.equals(this.processGraph, objectRestricted.processGraph);
  }

  @Override
  public int hashCode() {
    return Objects.hash(fromParameter, fromNode, processGraph);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ObjectRestricted {\n");
    
    sb.append("    fromParameter: ").append(toIndentedString(fromParameter)).append("\n");
    sb.append("    fromNode: ").append(toIndentedString(fromNode)).append("\n");
    sb.append("    processGraph: ").append(toIndentedString(processGraph)).append("\n");
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

