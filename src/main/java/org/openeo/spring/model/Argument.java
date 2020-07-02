package org.openeo.spring.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import org.openapitools.jackson.nullable.JsonNullable;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * Describes a general argument for various entities.
 */
@ApiModel(description = "Describes a general argument for various entities.")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2020-07-02T08:31:05.442+02:00[Europe/Rome]")
public class Argument   {
  /**
   * The type is the expected data type for the content of the parameter. `null` is allowed for all types. If no type is specified, any type is allowed to be passed.
   */
  public enum TypeEnum {
    STRING("string"),
    
    NUMBER("number"),
    
    INTEGER("integer"),
    
    BOOLEAN("boolean"),
    
    ARRAY("array"),
    
    OBJECT("object");

    private String value;

    TypeEnum(String value) {
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
    public static TypeEnum fromValue(String value) {
      for (TypeEnum b : TypeEnum.values()) {
        if (b.value.equals(value)) {
          return b;
        }
      }
      throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }
  }

  @JsonProperty("type")
  private TypeEnum type;

  @JsonProperty("description")
  private String description;

  @JsonProperty("required")
  private Boolean required = false;

  @JsonProperty("default")
  private JsonNullable<Object> _default = JsonNullable.undefined();

  @JsonProperty("minimum")
  private BigDecimal minimum;

  @JsonProperty("maximum")
  private BigDecimal maximum;

  @JsonProperty("enum")
  @Valid
  private List<Object> _enum = null;

  @JsonProperty("example")
  private JsonNullable<Object> example = JsonNullable.undefined();

  public Argument type(TypeEnum type) {
    this.type = type;
    return this;
  }

  /**
   * The type is the expected data type for the content of the parameter. `null` is allowed for all types. If no type is specified, any type is allowed to be passed.
   * @return type
  */
  @ApiModelProperty(value = "The type is the expected data type for the content of the parameter. `null` is allowed for all types. If no type is specified, any type is allowed to be passed.")


  public TypeEnum getType() {
    return type;
  }

  public void setType(TypeEnum type) {
    this.type = type;
  }

  public Argument description(String description) {
    this.description = description;
    return this;
  }

  /**
   * A brief description of the argument.
   * @return description
  */
  @ApiModelProperty(required = true, value = "A brief description of the argument.")
  @NotNull


  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public Argument required(Boolean required) {
    this.required = required;
    return this;
  }

  /**
   * Determines whether this argument is mandatory.
   * @return required
  */
  @ApiModelProperty(value = "Determines whether this argument is mandatory.")


  public Boolean isRequired() {
    return required;
  }

  public void setRequired(Boolean required) {
    this.required = required;
  }

  public Argument _default(Object _default) {
    this._default = JsonNullable.of(_default);
    return this;
  }

  /**
   * The default value represents what would be assumed by the consumer of the input as the value of the argument if none is provided. The value MUST conform to the defined type for the argument defined at the same level. For example, if type is string, then default can be \"foo\" but cannot be 1.
   * @return _default
  */
  @ApiModelProperty(value = "The default value represents what would be assumed by the consumer of the input as the value of the argument if none is provided. The value MUST conform to the defined type for the argument defined at the same level. For example, if type is string, then default can be \"foo\" but cannot be 1.")


  public JsonNullable<Object> getDefault() {
    return _default;
  }

  public void setDefault(JsonNullable<Object> _default) {
    this._default = _default;
  }

  public Argument minimum(BigDecimal minimum) {
    this.minimum = minimum;
    return this;
  }

  /**
   * Minimum value allowed for numeric arguments.
   * @return minimum
  */
  @ApiModelProperty(value = "Minimum value allowed for numeric arguments.")

  @Valid

  public BigDecimal getMinimum() {
    return minimum;
  }

  public void setMinimum(BigDecimal minimum) {
    this.minimum = minimum;
  }

  public Argument maximum(BigDecimal maximum) {
    this.maximum = maximum;
    return this;
  }

  /**
   * Maximum value allowed for numeric arguments.
   * @return maximum
  */
  @ApiModelProperty(value = "Maximum value allowed for numeric arguments.")

  @Valid

  public BigDecimal getMaximum() {
    return maximum;
  }

  public void setMaximum(BigDecimal maximum) {
    this.maximum = maximum;
  }

  public Argument _enum(List<Object> _enum) {
    this._enum = _enum;
    return this;
  }

  public Argument addEnumItem(Object _enumItem) {
    if (this._enum == null) {
      this._enum = new ArrayList<>();
    }
    this._enum.add(_enumItem);
    return this;
  }

  /**
   * List of allowed values for this argument. To represent examples that cannot be naturally represented in JSON, a string value can be used to contain the example with escaping where necessary.
   * @return _enum
  */
  @ApiModelProperty(value = "List of allowed values for this argument. To represent examples that cannot be naturally represented in JSON, a string value can be used to contain the example with escaping where necessary.")


  public List<Object> getEnum() {
    return _enum;
  }

  public void setEnum(List<Object> _enum) {
    this._enum = _enum;
  }

  public Argument example(Object example) {
    this.example = JsonNullable.of(example);
    return this;
  }

  /**
   * A free-form property to include an example for this argument. To represent examples that cannot be naturally represented in JSON, a string value can be used to contain the example with escaping where necessary.
   * @return example
  */
  @ApiModelProperty(value = "A free-form property to include an example for this argument. To represent examples that cannot be naturally represented in JSON, a string value can be used to contain the example with escaping where necessary.")


  public JsonNullable<Object> getExample() {
    return example;
  }

  public void setExample(JsonNullable<Object> example) {
    this.example = example;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Argument argument = (Argument) o;
    return Objects.equals(this.type, argument.type) &&
        Objects.equals(this.description, argument.description) &&
        Objects.equals(this.required, argument.required) &&
        Objects.equals(this._default, argument._default) &&
        Objects.equals(this.minimum, argument.minimum) &&
        Objects.equals(this.maximum, argument.maximum) &&
        Objects.equals(this._enum, argument._enum) &&
        Objects.equals(this.example, argument.example);
  }

  @Override
  public int hashCode() {
    return Objects.hash(type, description, required, _default, minimum, maximum, _enum, example);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Argument {\n");
    
    sb.append("    type: ").append(toIndentedString(type)).append("\n");
    sb.append("    description: ").append(toIndentedString(description)).append("\n");
    sb.append("    required: ").append(toIndentedString(required)).append("\n");
    sb.append("    _default: ").append(toIndentedString(_default)).append("\n");
    sb.append("    minimum: ").append(toIndentedString(minimum)).append("\n");
    sb.append("    maximum: ").append(toIndentedString(maximum)).append("\n");
    sb.append("    _enum: ").append(toIndentedString(_enum)).append("\n");
    sb.append("    example: ").append(toIndentedString(example)).append("\n");
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

