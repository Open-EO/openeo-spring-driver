package org.openeo.spring.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.openeo.spring.model.BaseParameter;
import org.openeo.spring.model.DataTypeSchema;
import org.openeo.spring.model.ParameterAllOf;
import org.openapitools.jackson.nullable.JsonNullable;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * Parameter
 */
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2020-06-30T14:48:14.663+02:00[Europe/Rome]")
public class Parameter   {
  @JsonProperty("name")
  private String name;

  @JsonProperty("description")
  private String description;

  @JsonProperty("optional")
  private Boolean optional = false;

  @JsonProperty("deprecated")
  private Boolean deprecated = false;

  @JsonProperty("experimental")
  private Boolean experimental = false;

  @JsonProperty("default")
  private JsonNullable<Object> _default = JsonNullable.undefined();

  @JsonProperty("schema")
  private DataTypeSchema schema = null;

  public Parameter name(String name) {
    this.name = name;
    return this;
  }

  /**
   * A unique name for the parameter.   It is RECOMMENDED to use [snake case](https://en.wikipedia.org/wiki/Snake_case) (e.g. `window_size` or `scale_factor`).
   * @return name
  */
  @ApiModelProperty(required = true, value = "A unique name for the parameter.   It is RECOMMENDED to use [snake case](https://en.wikipedia.org/wiki/Snake_case) (e.g. `window_size` or `scale_factor`).")
  @NotNull

@Pattern(regexp="^\\w+$") 
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Parameter description(String description) {
    this.description = description;
    return this;
  }

  /**
   * Detailed description to explain the entity.  [CommonMark 0.29](http://commonmark.org/) syntax MAY be used for rich text representation. In addition to the CommonMark syntax, clients can convert process IDs that are formatted as in the following example into links instead of code blocks: ``` ``process_id()`` ```
   * @return description
  */
  @ApiModelProperty(required = true, value = "Detailed description to explain the entity.  [CommonMark 0.29](http://commonmark.org/) syntax MAY be used for rich text representation. In addition to the CommonMark syntax, clients can convert process IDs that are formatted as in the following example into links instead of code blocks: ``` ``process_id()`` ```")
  @NotNull


  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public Parameter optional(Boolean optional) {
    this.optional = optional;
    return this;
  }

  /**
   * Determines whether this parameter is optional to be specified.
   * @return optional
  */
  @ApiModelProperty(value = "Determines whether this parameter is optional to be specified.")


  public Boolean isOptional() {
    return optional;
  }

  public void setOptional(Boolean optional) {
    this.optional = optional;
  }

  public Parameter deprecated(Boolean deprecated) {
    this.deprecated = deprecated;
    return this;
  }

  /**
   * Specifies that the process or parameter is deprecated with the potential to be removed in any of the next versions. It should be transitioned out of usage as soon as possible and users should refrain from using it in new implementations.  For processes a link with relation type `latest-version` SHOULD be added to the `links` and MUST refer to the process that can be used instead.
   * @return deprecated
  */
  @ApiModelProperty(value = "Specifies that the process or parameter is deprecated with the potential to be removed in any of the next versions. It should be transitioned out of usage as soon as possible and users should refrain from using it in new implementations.  For processes a link with relation type `latest-version` SHOULD be added to the `links` and MUST refer to the process that can be used instead.")


  public Boolean isDeprecated() {
    return deprecated;
  }

  public void setDeprecated(Boolean deprecated) {
    this.deprecated = deprecated;
  }

  public Parameter experimental(Boolean experimental) {
    this.experimental = experimental;
    return this;
  }

  /**
   * Declares the process or parameter to be experimental, which means that it is likely to change or may produce unpredictable behaviour. Users should refrain from using it in production,  but still feel encouraged to try it out and give feedback.
   * @return experimental
  */
  @ApiModelProperty(value = "Declares the process or parameter to be experimental, which means that it is likely to change or may produce unpredictable behaviour. Users should refrain from using it in production,  but still feel encouraged to try it out and give feedback.")


  public Boolean isExperimental() {
    return experimental;
  }

  public void setExperimental(Boolean experimental) {
    this.experimental = experimental;
  }

  public Parameter _default(Object _default) {
    this._default = JsonNullable.of(_default);
    return this;
  }

  /**
   * The default value for this parameter. Required parameters SHOULD NOT specify a default value. Optional parameters SHOULD always specify a default value.
   * @return _default
  */
  @ApiModelProperty(value = "The default value for this parameter. Required parameters SHOULD NOT specify a default value. Optional parameters SHOULD always specify a default value.")


  public JsonNullable<Object> getDefault() {
    return _default;
  }

  public void setDefault(JsonNullable<Object> _default) {
    this._default = _default;
  }

  public Parameter schema(DataTypeSchema schema) {
    this.schema = schema;
    return this;
  }

  /**
   * Get schema
   * @return schema
  */
  @ApiModelProperty(required = true, value = "")
  @NotNull

  @Valid

  public DataTypeSchema getSchema() {
    return schema;
  }

  public void setSchema(DataTypeSchema schema) {
    this.schema = schema;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Parameter parameter = (Parameter) o;
    return Objects.equals(this.name, parameter.name) &&
        Objects.equals(this.description, parameter.description) &&
        Objects.equals(this.optional, parameter.optional) &&
        Objects.equals(this.deprecated, parameter.deprecated) &&
        Objects.equals(this.experimental, parameter.experimental) &&
        Objects.equals(this._default, parameter._default) &&
        Objects.equals(this.schema, parameter.schema);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, description, optional, deprecated, experimental, _default, schema);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Parameter {\n");
    
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    description: ").append(toIndentedString(description)).append("\n");
    sb.append("    optional: ").append(toIndentedString(optional)).append("\n");
    sb.append("    deprecated: ").append(toIndentedString(deprecated)).append("\n");
    sb.append("    experimental: ").append(toIndentedString(experimental)).append("\n");
    sb.append("    _default: ").append(toIndentedString(_default)).append("\n");
    sb.append("    schema: ").append(toIndentedString(schema)).append("\n");
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

