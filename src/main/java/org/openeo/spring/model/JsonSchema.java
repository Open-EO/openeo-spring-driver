package org.openeo.spring.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.openeo.spring.model.AnyOfarrayjsonSchema;
import org.openeo.spring.model.OneOfjsonSchemaTypeset;
import org.openapitools.jackson.nullable.JsonNullable;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * Specifies a data type supported by a parameter or return value.  The data types are specified according to the [JSON Schema draft-07](http://json-schema.org/) specification. See the chapter [&#39;Schemas&#39; in &#39;Defining Processes&#39;](#section/Processes/Defining-Processes) for more information.  It is discouraged to specify JSON Schemas with &#x60;default&#x60;, &#x60;anyOf&#x60;, &#x60;oneOf&#x60;, &#x60;allOf&#x60; or &#x60;not&#x60; at the top-level of the schema. Instead specify each data type in a separate array element.  It is recommended to refrain from using the following more complex JSON Schema keywords: &#x60;if&#x60;, &#x60;then&#x60;, &#x60;else&#x60;, &#x60;readOnly&#x60;, &#x60;writeOnly&#x60;, &#x60;dependencies&#x60;, &#x60;minProperties&#x60;, &#x60;maxProperties&#x60;, &#x60;patternProperties&#x60;, &#x60;multipleOf&#x60;.  JSON Schemas should always be dereferenced (i.e. all &#x60;$refs&#x60; should be resolved). This allows clients to consume the schemas much better. Clients are not expected to support dereferencing &#x60;$refs&#x60;.  Note: The specified schema is only a common subset of JSON Schema. Additional keywords MAY be used.
 */
@ApiModel(description = "Specifies a data type supported by a parameter or return value.  The data types are specified according to the [JSON Schema draft-07](http://json-schema.org/) specification. See the chapter ['Schemas' in 'Defining Processes'](#section/Processes/Defining-Processes) for more information.  It is discouraged to specify JSON Schemas with `default`, `anyOf`, `oneOf`, `allOf` or `not` at the top-level of the schema. Instead specify each data type in a separate array element.  It is recommended to refrain from using the following more complex JSON Schema keywords: `if`, `then`, `else`, `readOnly`, `writeOnly`, `dependencies`, `minProperties`, `maxProperties`, `patternProperties`, `multipleOf`.  JSON Schemas should always be dereferenced (i.e. all `$refs` should be resolved). This allows clients to consume the schemas much better. Clients are not expected to support dereferencing `$refs`.  Note: The specified schema is only a common subset of JSON Schema. Additional keywords MAY be used.")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2020-07-02T08:31:05.442+02:00[Europe/Rome]")
public class JsonSchema extends HashMap<String, Object>  {
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

  public JsonSchema type(OneOfjsonSchemaTypeset type) {
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

  public JsonSchema subtype(String subtype) {
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

  public JsonSchema pattern(String pattern) {
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

  public JsonSchema _enum(List<Object> _enum) {
    this._enum = _enum;
    return this;
  }

  public JsonSchema addEnumItem(Object _enumItem) {
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

  public JsonSchema minimum(BigDecimal minimum) {
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

  public JsonSchema maximum(BigDecimal maximum) {
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

  public JsonSchema minItems(BigDecimal minItems) {
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

  public JsonSchema maxItems(BigDecimal maxItems) {
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

  public JsonSchema items(AnyOfarrayjsonSchema items) {
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


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    JsonSchema jsonSchema = (JsonSchema) o;
    return Objects.equals(this.type, jsonSchema.type) &&
        Objects.equals(this.subtype, jsonSchema.subtype) &&
        Objects.equals(this.pattern, jsonSchema.pattern) &&
        Objects.equals(this._enum, jsonSchema._enum) &&
        Objects.equals(this.minimum, jsonSchema.minimum) &&
        Objects.equals(this.maximum, jsonSchema.maximum) &&
        Objects.equals(this.minItems, jsonSchema.minItems) &&
        Objects.equals(this.maxItems, jsonSchema.maxItems) &&
        Objects.equals(this.items, jsonSchema.items) &&
        super.equals(o);
  }

  @Override
  public int hashCode() {
    return Objects.hash(type, subtype, pattern, _enum, minimum, maximum, minItems, maxItems, items, super.hashCode());
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class JsonSchema {\n");
    sb.append("    ").append(toIndentedString(super.toString())).append("\n");
    sb.append("    type: ").append(toIndentedString(type)).append("\n");
    sb.append("    subtype: ").append(toIndentedString(subtype)).append("\n");
    sb.append("    pattern: ").append(toIndentedString(pattern)).append("\n");
    sb.append("    _enum: ").append(toIndentedString(_enum)).append("\n");
    sb.append("    minimum: ").append(toIndentedString(minimum)).append("\n");
    sb.append("    maximum: ").append(toIndentedString(maximum)).append("\n");
    sb.append("    minItems: ").append(toIndentedString(minItems)).append("\n");
    sb.append("    maxItems: ").append(toIndentedString(maxItems)).append("\n");
    sb.append("    items: ").append(toIndentedString(items)).append("\n");
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

