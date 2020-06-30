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
 * LogEntryPath
 */
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2020-06-30T15:12:47.411+02:00[Europe/Rome]")
public class LogEntryPath   {
  @JsonProperty("node_id")
  private String nodeId;

  @JsonProperty("process_id")
  private String processId;

  @JsonProperty("parameter")
  private JsonNullable<String> parameter = JsonNullable.undefined();

  public LogEntryPath nodeId(String nodeId) {
    this.nodeId = nodeId;
    return this;
  }

  /**
   * Get nodeId
   * @return nodeId
  */
  @ApiModelProperty(example = "runudf1", required = true, value = "")
  @NotNull


  public String getNodeId() {
    return nodeId;
  }

  public void setNodeId(String nodeId) {
    this.nodeId = nodeId;
  }

  public LogEntryPath processId(String processId) {
    this.processId = processId;
    return this;
  }

  /**
   * Get processId
   * @return processId
  */
  @ApiModelProperty(example = "run_udf", value = "")


  public String getProcessId() {
    return processId;
  }

  public void setProcessId(String processId) {
    this.processId = processId;
  }

  public LogEntryPath parameter(String parameter) {
    this.parameter = JsonNullable.of(parameter);
    return this;
  }

  /**
   * Get parameter
   * @return parameter
  */
  @ApiModelProperty(example = "udf", value = "")


  public JsonNullable<String> getParameter() {
    return parameter;
  }

  public void setParameter(JsonNullable<String> parameter) {
    this.parameter = parameter;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    LogEntryPath logEntryPath = (LogEntryPath) o;
    return Objects.equals(this.nodeId, logEntryPath.nodeId) &&
        Objects.equals(this.processId, logEntryPath.processId) &&
        Objects.equals(this.parameter, logEntryPath.parameter);
  }

  @Override
  public int hashCode() {
    return Objects.hash(nodeId, processId, parameter);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class LogEntryPath {\n");
    
    sb.append("    nodeId: ").append(toIndentedString(nodeId)).append("\n");
    sb.append("    processId: ").append(toIndentedString(processId)).append("\n");
    sb.append("    parameter: ").append(toIndentedString(parameter)).append("\n");
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

