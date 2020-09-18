package org.openeo.spring.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.openapitools.jackson.nullable.JsonNullable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * An log message that communicates information about the processed data.
 */
@ApiModel(description = "An log message that communicates information about the processed data.")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2020-07-02T08:45:00.334+02:00[Europe/Rome]")
public class LogEntry   {
  @JsonProperty("id")
  private String id;

  @JsonProperty("code")
  private String code;

  /**
   * The severity level of the log entry.  The order of the levels is as follows (from high to low severity): `error`, `warning`, `info`, `debug`.  The level `error` usually stops processing the data.
   */
  public enum LevelEnum {
    ERROR("error"),
    
    WARNING("warning"),
    
    INFO("info"),
    
    DEBUG("debug");

    private String value;

    LevelEnum(String value) {
      this.value = value;
    }

    @JsonValue
    public String getValue() {
      return value;
    }

    @Override
    public String toString() {
      return String.valueOf(value);
    }

    @JsonCreator
    public static LevelEnum fromValue(String value) {
      for (LevelEnum b : LevelEnum.values()) {
        if (b.value.equals(value)) {
          return b;
        }
      }
      throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }
  }

  @JsonProperty("level")
  private LevelEnum level;

  @JsonProperty("message")
  private String message;

  @JsonProperty("data")
  private JsonNullable<Object> data = JsonNullable.undefined();

  @JsonProperty("path")
  @Valid
  private List<LogEntryPath> path = new ArrayList<>();

  @JsonProperty("links")
  @Valid
  private List<Link> links = null;

  public LogEntry id(String id) {
    this.id = id;
    return this;
  }

  /**
   * An unique identifier for the log message, could simply be an incrementing number.
   * @return id
  */
  @ApiModelProperty(example = "1", required = true, value = "An unique identifier for the log message, could simply be an incrementing number.")
  @NotNull


  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public LogEntry code(String code) {
    this.code = code;
    return this;
  }

  /**
   * The code is either one of the standardized error codes or a custom code, for example specified by a user in the `debug` process.
   * @return code
  */
  @ApiModelProperty(example = "SampleError", value = "The code is either one of the standardized error codes or a custom code, for example specified by a user in the `debug` process.")


  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public LogEntry level(LevelEnum level) {
    this.level = level;
    return this;
  }

  /**
   * The severity level of the log entry.  The order of the levels is as follows (from high to low severity): `error`, `warning`, `info`, `debug`.  The level `error` usually stops processing the data.
   * @return level
  */
  @ApiModelProperty(example = "error", required = true, value = "The severity level of the log entry.  The order of the levels is as follows (from high to low severity): `error`, `warning`, `info`, `debug`.  The level `error` usually stops processing the data.")
  @NotNull


  public LevelEnum getLevel() {
    return level;
  }

  public void setLevel(LevelEnum level) {
    this.level = level;
  }

  public LogEntry message(String message) {
    this.message = message;
    return this;
  }

  /**
   * A message explaining the log entry.
   * @return message
  */
  @ApiModelProperty(example = "Can't load the UDF file from the URL `https://example.com/invalid/file.txt`. Server responded with error 404.", required = true, value = "A message explaining the log entry.")
  @NotNull


  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public LogEntry data(Object data) {
    this.data = JsonNullable.of(data);
    return this;
  }

  /**
   * Data of any type. It is the back-ends task to decide how to best present passed data to a user.  For example, a raster-cube passed to the `debug` should return the metadata similar to the collection metadata, including `cube:dimensions`.
   * @return data
  */
  @ApiModelProperty(value = "Data of any type. It is the back-ends task to decide how to best present passed data to a user.  For example, a raster-cube passed to the `debug` should return the metadata similar to the collection metadata, including `cube:dimensions`.")


  public JsonNullable<Object> getData() {
    return data;
  }

  public void setData(JsonNullable<Object> data) {
    this.data = data;
  }

  public LogEntry path(List<LogEntryPath> path) {
    this.path = path;
    return this;
  }

  public LogEntry addPathItem(LogEntryPath pathItem) {
    this.path.add(pathItem);
    return this;
  }

  /**
   * Describes where the log entry has occurred.  The first element of the array is the process that has triggered the log entry, the second element is the parent of the process that has triggered the log entry, etc. This pattern is followed until the root of the process graph.
   * @return path
  */
  @ApiModelProperty(required = true, value = "Describes where the log entry has occurred.  The first element of the array is the process that has triggered the log entry, the second element is the parent of the process that has triggered the log entry, etc. This pattern is followed until the root of the process graph.")
  @NotNull

  @Valid

  public List<LogEntryPath> getPath() {
    return path;
  }

  public void setPath(List<LogEntryPath> path) {
    this.path = path;
  }

  public LogEntry links(List<Link> links) {
    this.links = links;
    return this;
  }

  public LogEntry addLinksItem(Link linksItem) {
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
    LogEntry logEntry = (LogEntry) o;
    return Objects.equals(this.id, logEntry.id) &&
        Objects.equals(this.code, logEntry.code) &&
        Objects.equals(this.level, logEntry.level) &&
        Objects.equals(this.message, logEntry.message) &&
        Objects.equals(this.data, logEntry.data) &&
        Objects.equals(this.path, logEntry.path) &&
        Objects.equals(this.links, logEntry.links);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, code, level, message, data, path, links);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class LogEntry {\n");
    
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    code: ").append(toIndentedString(code)).append("\n");
    sb.append("    level: ").append(toIndentedString(level)).append("\n");
    sb.append("    message: ").append(toIndentedString(message)).append("\n");
    sb.append("    data: ").append(toIndentedString(data)).append("\n");
    sb.append("    path: ").append(toIndentedString(path)).append("\n");
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

