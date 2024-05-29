package org.openeo.spring.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModelProperty;

/**
 * BatchJobResult for items of type "Feature".
 */
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2020-07-02T08:45:00.334+02:00[Europe/Rome]")
@Entity
@Table(name = "job_results_feature")
//@JsonIgnoreProperties(ignoreUnknown = true)
public class BatchJobResultFeature extends BatchJobResult implements Serializable {

	private static final long serialVersionUID = -879934306104454215L;
	
	{
	    // init section
	    setType(AssetType.FEATURE);
	}

	@JsonProperty("bbox")
	@Valid
	@Embedded
	private List<BigDecimal> bbox = null;

	@JsonProperty("geometry")
	@Embedded
	private GeoJsonGeometry geometry = null;
	
	@JsonProperty("properties")
	@Valid
	@Transient
	private Map < String, Object> properties = new HashMap<>();

    @Override
    public void setType(AssetType type) {
        if (AssetType.FEATURE != type) {
            throw new RuntimeException("Wrong type for STAC Collection: " + type);
        }
        super.setType(type);
    }

	public BatchJobResultFeature bbox(List<BigDecimal> bbox) {
		this.bbox = bbox;
		return this;
	}

	public BatchJobResultFeature addBboxItem(BigDecimal bboxItem) {
		if (this.bbox == null) {
			this.bbox = new ArrayList<>();
		}
		this.bbox.add(bboxItem);
		return this;
	}

	/**
	 * Potential *spatial extent* covered by the data. The bounding box is provided
	 * as four or six numbers. Six numbers are specified, if the coordinate
	 * reference system includes a vertical axis (height or depth). The order of the
	 * elements in the array: - West (lower left corner, coordinate axis 1) - South
	 * (lower left corner, coordinate axis 2) - Base (optional, lower left corner,
	 * coordinate axis 3) - East (upper right corner, coordinate axis 1) - North
	 * (upper right corner, coordinate axis 2) - Height (optional, upper right
	 * corner, coordinate axis 3) The coordinate reference system of the values is
	 * WGS84 longitude/latitude. Specifying the `bbox` is strongly RECOMMENDED for
	 * STAC compliance, but can be omitted if the result is unlocated and the
	 * `geometry` is set to `null`.
	 * 
	 * @return bbox
	 */
	@ApiModelProperty(example = "[-180,-90,180,90]", value = "Potential *spatial extent* covered by the data.  The bounding box is provided as four or six numbers. Six numbers are specified, if the coordinate reference system includes a vertical axis (height or depth). The order of the elements in the array:  - West (lower left corner, coordinate axis 1) - South (lower left corner, coordinate axis 2) - Base (optional, lower left corner, coordinate axis 3) - East (upper right corner, coordinate axis 1) - North (upper right corner, coordinate axis 2) - Height (optional, upper right corner, coordinate axis 3)  The coordinate reference system of the values is WGS84 longitude/latitude.  Specifying the `bbox` is strongly RECOMMENDED for STAC compliance, but can be omitted if the result is unlocated and the `geometry` is set to `null`.")

	@Valid

	public List<BigDecimal> getBbox() {
		return bbox;
	}

	public void setBbox(List<BigDecimal> bbox) {
		this.bbox = bbox;
	}

	public BatchJobResultFeature geometry(GeoJsonGeometry geometry) {
		this.geometry = geometry;
		return this;
	}

	/**
	 * Defines the full footprint of the asset represented by this item as GeoJSON
	 * Geometry. Results without a known location can set this value to `null`.
	 * 
	 * @return geometry
	 */
	@ApiModelProperty(example = "{\"type\":\"Polygon\",\"coordinates\":[[[-180,-90],[180,-90],[180,90],[-180,90],[-180,-90]]]}", required = true, value = "Defines the full footprint of the asset represented by this item as GeoJSON Geometry.  Results without a known location can set this value to `null`.")

	@Valid

	public GeoJsonGeometry getGeometry() {
		return geometry;
	}

	public void setGeometry(GeoJsonGeometry geometry) {
		this.geometry = geometry;
	}

	public BatchJobResultFeature properties(Map<String, Object> properties) {
		this.properties = properties;
		return this;
	}

	public BatchJobResultFeature putPropertiesItem(String key, Object propertiesItem) {
		this.properties.put(key, propertiesItem);
		return this;
	}

	/**
	 * MAY contain any additional properties, e.g. other STAC Item properties,
	 * properties from STAC extensions or custom properties.
	 * 
	 * @return properties
	 */
	@ApiModelProperty(required = true, value = "MAY contain any additional properties, e.g. other STAC Item properties, properties from STAC extensions or custom properties.")
	@NotNull

	public Map<String, Object> getProperties() {
		return properties;
	}

	public void setProperties(Map<String, Object> properties) {
		this.properties = properties;
	}
	@Override
	public boolean equals(java.lang.Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		BatchJobResultFeature batchJobResult = (BatchJobResultFeature) o;
		return super.equals(o)
				&& Objects.equals(this.bbox, batchJobResult.bbox)
				&& Objects.equals(this.geometry, batchJobResult.geometry)
				&& Objects.equals(this.properties, batchJobResult.properties);
	}

	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), bbox, geometry, properties);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("class BatchJobResult-Feature {\n");
		sb.append("    stacVersion: ").append(toIndentedString(getStacVersion())).append("\n");
		sb.append("    stacExtensions: ").append(toIndentedString(getStacExtensions())).append("\n");
		sb.append("    id: ").append(toIndentedString(getId())).append("\n");
		sb.append("    type: ").append(toIndentedString(getType())).append("\n");
		sb.append("    bbox: ").append(toIndentedString(bbox)).append("\n");
		sb.append("    geometry: ").append(toIndentedString(geometry)).append("\n");
		sb.append("    properties: ").append(toIndentedString(properties)).append("\n");
		sb.append("    assets: ").append(toIndentedString(getAssets())).append("\n");
		sb.append("    links: ").append(toIndentedString(getLinks())).append("\n");
		sb.append("}");
		return sb.toString();
	}
}
