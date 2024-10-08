package org.openeo.spring.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModelProperty;

/**
 * DimensionBands
 */
@Entity
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2020-07-02T08:45:00.334+02:00[Europe/Rome]")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class DimensionBands extends Dimension  {

    @JsonProperty("values")
    @Valid
    @ElementCollection //(targetClass = String.class, fetch = FetchType.EAGER)
//    @CollectionTable(name = "dim_bands_values", joinColumns = @JoinColumn(name = "dimension_id"))
    @Column(name = "dim_values", nullable = true)
    private List<String> values = new ArrayList<>();

    public DimensionBands values(List<String> values) {
        this.values = values;
        return this;
    }

    public DimensionBands addValuesItem(String valuesItem) {
        this.values.add(valuesItem);
        return this;
    }

    /**
     * A set of all potential values, especially useful for [nominal](https://en.wikipedia.org/wiki/Level_of_measurement#Nominal_level) values.  **Important:** The order of the values MUST be exactly how the dimension values are also ordered in the data (cube). If the values specify band names, the values MUST be in the same order as they are in the corresponding band fields (i.e. `eo:bands` or `sar:bands`).
     * @return values
     */
    @ApiModelProperty(required = true, value = "A set of all potential values, especially useful for [nominal](https://en.wikipedia.org/wiki/Level_of_measurement#Nominal_level) values.  **Important:** The order of the values MUST be exactly how the dimension values are also ordered in the data (cube). If the values specify band names, the values MUST be in the same order as they are in the corresponding band fields (i.e. `eo:bands` or `sar:bands`).")
    @NotNull
    @Valid
    @Size(min=1)
    public List<String> getValues() {
        return values;
    }

    public void setValues(List<String> values) {
        this.values = values;
    }

    public boolean containsValue(String value) {
        return values.contains(value);
    }


    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        DimensionBands dimensionBands = (DimensionBands) o;
        return Objects.equals(this.values, dimensionBands.values) &&
                super.equals(o);
    }

    @Override
    public int hashCode() {
        return Objects.hash(values, super.hashCode());
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class DimensionBands {\n");
        sb.append("    ").append(toIndentedString(super.toString())).append("\n");
        sb.append("    values: ").append(toIndentedString(values)).append("\n");
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

