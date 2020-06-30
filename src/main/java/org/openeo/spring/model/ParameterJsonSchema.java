package org.openeo.spring.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import org.openeo.spring.model.AnyOfarrayjsonSchema;
import org.openeo.spring.model.JsonSchema;
import org.openeo.spring.model.OneOfjsonSchemaTypeset;
import org.openeo.spring.model.Parameter;
import org.openeo.spring.model.ParameterJsonSchemaAllOf;
import org.openapitools.jackson.nullable.JsonNullable;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * ParameterJsonSchema
 */
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2020-06-30T15:12:47.411+02:00[Europe/Rome]")
public class ParameterJsonSchema   {
  @JsonProperty("type")
  private OneOfjsonSchemaTypeset type = null;

  @JsonProperty("subtype")
  private String subtype;

  @JsonProperty("pattern")
  private String pattern;

  @JsonProperty("enum")
  @Valid
  private List<Object> _enum = null;

  @JsonProperty("minimum")
  private BigDecimal minimum;

  @JsonProperty("maximum")
  private BigDecimal maximum;

  @JsonProperty("minItems")
  private BigDecimal minItems = 0d;

  @JsonProperty("maxItems")
  private BigDecimal maxItems;

  @JsonProperty("items")
  private AnyOfarrayjsonSchema items = null;

  @JsonProperty("parameters")
  @Valid
  private List<Parameter> parameters = null;

  public ParameterJsonSchema type(OneOfjsonSchemaTypeset type) {
    this.type = type;
    return this;
  }

  /**
   * Get type
   * @return type
  */
  @ApiModelProperty(value = "")

  @Valid

  public OneOfjsonSchemaTypeset getType() {
    return type;
  }

  public void setType(OneOfjsonSchemaTypeset type) {
    this.type = type;
  }

  public ParameterJsonSchema subtype(String subtype) {
    this.subtype = subtype;
    return this;
  }

  /**
   * Get subtype
   * @return subtype
  */
  @ApiModelProperty(value = "")


  public String getSubtype() {
    return subtype;
  }

  public void setSubtype(String subtype) {
    this.subtype = subtype;
  }

  public ParameterJsonSchema pattern(String pattern) {
    this.pattern = pattern;
    return this;
  }

  /**
   * Get pattern
   * @return pattern
  */
  @ApiModelProperty(value = "")


  public String getPattern() {
    return pattern;
  }

  public void setPattern(String pattern) {
    this.pattern = pattern;
  }

  public ParameterJsonSchema _enum(List<Object> _enum) {
    this._enum = _enum;
    return this;
  }

  public ParameterJsonSchema addEnumItem(Object _enumItem) {
    if (this._enum == null) {
      this._enum = new ArrayList<>();
    }
    this._enum.add(_enumItem);
    return this;
  }

  /**
   * Get _enum
   * @return _enum
  */
  @ApiModelProperty(value = "")


  public List<Object> getEnum() {
    return _enum;
  }

  public void setEnum(List<Object> _enum) {
    this._enum = _enum;
  }

  public ParameterJsonSchema minimum(BigDecimal minimum) {
    this.minimum = minimum;
    return this;
  }

  /**
   * Get minimum
   * @return minimum
  */
  @ApiModelProperty(value = "")

  @Valid

  public BigDecimal getMinimum() {
    return minimum;
  }

  public void setMinimum(BigDecimal minimum) {
    this.minimum = minimum;
  }

  public ParameterJsonSchema maximum(BigDecimal maximum) {
    this.maximum = maximum;
    return this;
  }

  /**
   * Get maximum
   * @return maximum
  */
  @ApiModelProperty(value = "")

  @Valid

  public BigDecimal getMaximum() {
    return maximum;
  }

  public void setMaximum(BigDecimal maximum) {
    this.maximum = maximum;
  }

  public ParameterJsonSchema minItems(BigDecimal minItems) {
    this.minItems = minItems;
    return this;
  }

  /**
   * Get minItems
   * minimum: 0
   * @return minItems
  */
  @ApiModelProperty(value = "")

  @Valid
@DecimalMin("0")
  public BigDecimal getMinItems() {
    return minItems;
  }

  public void setMinItems(BigDecimal minItems) {
    this.minItems = minItems;
  }

  public ParameterJsonSchema maxItems(BigDecimal maxItems) {
    this.maxItems = maxItems;
    return this;
  }

  /**
   * Get maxItems
   * minimum: 0
   * @return maxItems
  */
  @ApiModelProperty(value = "")

  @Valid
@DecimalMin("0")
  public BigDecimal getMaxItems() {
    return maxItems;
  }

  public void setMaxItems(BigDecimal maxItems) {
    this.maxItems = maxItems;
  }

  public ParameterJsonSchema items(AnyOfarrayjsonSchema items) {
    this.items = items;
    return this;
  }

  /**
   * Get items
   * @return items
  */
  @ApiModelProperty(value = "")

  @Valid

  public AnyOfarrayjsonSchema getItems() {
    return items;
  }

  public void setItems(AnyOfarrayjsonSchema items) {
    this.items = items;
  }

  public ParameterJsonSchema parameters(List<Parameter> parameters) {
    this.parameters = parameters;
    return this;
  }

  public ParameterJsonSchema addParametersItem(Parameter parametersItem) {
    if (this.parameters == null) {
      this.parameters = new ArrayList<>();
    }
    this.parameters.add(parametersItem);
    return this;
  }

  /**
   * A list of parameters passed to the child process graph.  The order in the array corresponds to the parameter order to be used in clients that don't support named parameters.
   * @return parameters
  */
  @ApiModelProperty(value = "A list of parameters passed to the child process graph.  The order in the array corresponds to the parameter order to be used in clients that don't support named parameters.")

  @Valid

  public List<Parameter> getParameters() {
    return parameters;
  }

  public void setParameters(List<Parameter> parameters) {
    this.parameters = parameters;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ParameterJsonSchema parameterJsonSchema = (ParameterJsonSchema) o;
    return Objects.equals(this.type, parameterJsonSchema.type) &&
        Objects.equals(this.subtype, parameterJsonSchema.subtype) &&
        Objects.equals(this.pattern, parameterJsonSchema.pattern) &&
        Objects.equals(this._enum, parameterJsonSchema._enum) &&
        Objects.equals(this.minimum, parameterJsonSchema.minimum) &&
        Objects.equals(this.maximum, parameterJsonSchema.maximum) &&
        Objects.equals(this.minItems, parameterJsonSchema.minItems) &&
        Objects.equals(this.maxItems, parameterJsonSchema.maxItems) &&
        Objects.equals(this.items, parameterJsonSchema.items) &&
        Objects.equals(this.parameters, parameterJsonSchema.parameters);
  }

  @Override
  public int hashCode() {
    return Objects.hash(type, subtype, pattern, _enum, minimum, maximum, minItems, maxItems, items, parameters);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ParameterJsonSchema {\n");
    
    sb.append("    type: ").append(toIndentedString(type)).append("\n");
    sb.append("    subtype: ").append(toIndentedString(subtype)).append("\n");
    sb.append("    pattern: ").append(toIndentedString(pattern)).append("\n");
    sb.append("    _enum: ").append(toIndentedString(_enum)).append("\n");
    sb.append("    minimum: ").append(toIndentedString(minimum)).append("\n");
    sb.append("    maximum: ").append(toIndentedString(maximum)).append("\n");
    sb.append("    minItems: ").append(toIndentedString(minItems)).append("\n");
    sb.append("    maxItems: ").append(toIndentedString(maxItems)).append("\n");
    sb.append("    items: ").append(toIndentedString(items)).append("\n");
    sb.append("    parameters: ").append(toIndentedString(parameters)).append("\n");
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

