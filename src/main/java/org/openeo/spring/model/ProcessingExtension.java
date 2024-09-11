package org.openeo.spring.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.MapKeyColumn;
import javax.persistence.OneToOne;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModelProperty;

/**
 * Class that encapsulates all fields of the STAC "Processing" extension.
 * 
 * @see <a href="https://github.com/stac-extensions/processing">Processing Extension</a>
 */
@Entity
@JsonInclude(Include.NON_EMPTY)
public class ProcessingExtension {
    
    @Id
    @JsonIgnore
    @GeneratedValue
    private Long id;
    
    @JsonProperty("processing:expression")
    @OneToOne(cascade = CascadeType.ALL, optional = true)
    @JoinColumn(name = "expression_id", referencedColumnName = "id", nullable = true)
    private ProcessingExpression expression;

    @JsonProperty("processing:lineage")
    private String lineage;
    
    @JsonProperty("processing:level")
    private String level;
    
    @JsonProperty("processing:facility")
    private String facility;
    
    // TODO to datetime Java object?
    @JsonProperty("processing:datetime")
    private String datetime;
    
    @JsonProperty("processing:version")
    private String version;
    
    @ElementCollection
    @JoinTable(name="processing_software_version", joinColumns=@JoinColumn(name="id"))
    @MapKeyColumn (name="software_id")
    @Column(name="software")
    private Map<String, String> software = new HashMap<>();

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
    
    @ApiModelProperty
    public ProcessingExpression getProcessingExpression() {
        return expression;
    }

    public void setProcessingExpression(ProcessingExpression expr) {
        this.expression = expr;
    }

    @ApiModelProperty
    public String getLineage() {
        return lineage;
    }

    public void setLineage(String lineage) {
        this.lineage = lineage;
    }

    @ApiModelProperty
    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    @ApiModelProperty
    public String getFacility() {
        return facility;
    }
    
    public void setFacility(String facility) {
        this.facility = facility;
    }

    @ApiModelProperty
    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    @ApiModelProperty
    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    @ApiModelProperty
    public Map<String, String> getSoftware() {
        return software;
    }

    public void setSoftware(Map<String, String> software) {
        this.software = software;
    }
    
    public String addSoftware(String software, String version) {
        return this.software.put(software, version);
    }
    
    public String removeSoftware(String software) {
        return this.software.remove(software);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class ProcessingExtension {\n");
        sb.append("    expression: ").append(toIndentedString(expression)).append("\n");
        sb.append("    lineage: ").append(toIndentedString(lineage)).append("\n");
        sb.append("    facility: ").append(toIndentedString(facility)).append("\n");
        sb.append("    datetime: ").append(toIndentedString(datetime)).append("\n");
        sb.append("    version: ").append(toIndentedString(version)).append("\n");
        sb.append("    software: ").append(toIndentedString(software)).append("\n");
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

    @Override
    public int hashCode() {
        return Objects.hash(datetime, expression, facility, id, level, lineage, software, version);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ProcessingExtension other = (ProcessingExtension) obj;
        return Objects.equals(datetime, other.datetime) && Objects.equals(expression, other.expression)
                && Objects.equals(facility, other.facility) && Objects.equals(id, other.id)
                && Objects.equals(level, other.level) && Objects.equals(lineage, other.lineage)
                && Objects.equals(software, other.software) && Objects.equals(version, other.version);
    }
}
