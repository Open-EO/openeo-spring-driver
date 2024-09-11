package org.openeo.spring.model;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.validation.constraints.NotNull;

import org.openeo.spring.jpa.GenericExpressionConverter;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModelProperty;

/**
 * A processing expression object from the {@code processing} STAC extension.
 * 
 * This is referenced by the {@code processing:expression} JSON key, and essentially
 * contains a processing expression in a specified format.
 * 
 * @see <a href="https://github.com/stac-extensions/processing?tab=readme-ov-file#expression-object">Expression Objects</a>
 */
@Entity
public class ProcessingExpression   {

    @Id
    @JsonIgnore
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    
    @Lob
    @NotNull
    @JsonProperty("expression")
    @Convert(converter = GenericExpressionConverter.class)
    private String expression;

    @NotNull
    @Enumerated
    @Column(name = "format")
    @JsonProperty("format")
    private ProcessingExpressionFormat format;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
    
    @ApiModelProperty
    public Object getExpression() {
        return this.expression;
    }
    
    public void setExpression(Object expr) {
        this.expression = expr.toString();
    }

    @ApiModelProperty
    public ProcessingExpressionFormat getFormat() {
        return this.format;
    }

    public void setFormat(ProcessingExpressionFormat fmt) {
        this.format = fmt;
    }

    // overload
    public void setFormat(String fmt) {
        setFormat(ProcessingExpressionFormat.fromValue(fmt));
    }
    
    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ProcessingExpression userData = (ProcessingExpression) o;
        return (this.id == userData.id) &&
                Objects.equals(this.expression, userData.expression) &&
                Objects.equals(this.format, userData.format);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, expression, format);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class ProcessingExpression {\n");
        sb.append("    format: ").append(toIndentedString(format)).append("\n");
        sb.append("    expression: ").append(toIndentedString(expression)).append("\n");
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

