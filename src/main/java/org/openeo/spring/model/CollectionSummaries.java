package org.openeo.spring.model;

import java.net.URI;
import java.util.List;
import java.util.Objects;

import javax.persistence.Embeddable;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * A link to another resource on the web. Bases on [RFC 5899](https://tools.ietf.org/html/rfc5988).
 */
@ApiModel(description = "A link to another resource on the web. Bases on [RFC 5899](https://tools.ietf.org/html/rfc5988).")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2020-07-02T08:45:00.334+02:00[Europe/Rome]")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CollectionSummaries   {
	@JsonProperty("constellation")
	private List<String> constellation;

	@JsonProperty("platform")
	private List<String> platform;
	
	@JsonProperty("rows")
	private Integer rows;
	
	@JsonProperty("columns")
	private Integer columns;

	@JsonProperty("instruments")
	private List<String> instruments;

	@JsonProperty("eo:cloud cover")
	private CollectionSummaryStats cloudCover;

	@JsonProperty("sat:orbit_state")
	private List<String> orbitState;

	@JsonProperty("eo:gsd")
	private List<Double> gsd;

	@JsonProperty("proj:epsg")
	private CollectionSummaryStats epsg;

	@JsonProperty("eo:bands")
	private List<BandSummary> bands;

	public CollectionSummaries constellation(List<String> constellation) {
		this.constellation = constellation;
		return this;
	}

	/**
	 * Relationship between the current document and the linked document. SHOULD be a [registered link relation type](https://www.iana.org/assignments/link-relations/link-relations.xml) whenever feasible.
	 * @return rel
	 */
	@ApiModelProperty(example = "related", required = true, value = "Relationship between the current document and the linked document. SHOULD be a [registered link relation type](https://www.iana.org/assignments/link-relations/link-relations.xml) whenever feasible.")
	@NotNull

	

	public List<String> getConstellation() {
		return constellation;
	}

	public void setConstellation(List<String> constellation) {
		this.constellation = constellation;
	}

	public CollectionSummaries platform(List<String> platform) {
		this.platform = platform;
		return this;
	}

	/**
	 * The value MUST be a valid String.
	 * @return href
	 */
	@ApiModelProperty(example = "Sentinel-2", required = true, value = "The value MUST be a valid String.")
	@NotNull

	@Valid

	public List<String> getPlatform() {
		return platform;
	}

	public void setPlatform(List<String> platform) {
		this.platform = platform;
	}
	
	
	public CollectionSummaries rows(Integer rows) {
		this.rows = rows;
		return this;
	}
	
	@ApiModelProperty(example = "22", required = true, value = "The value MUST be a valid Integer.")
	@NotNull

	@Valid

	public Integer getRows() {
		return rows;
	}

	public void setRows(Integer rows) {
		this.rows = rows;
	}
	
	
	public CollectionSummaries columns(Integer columns) {
		this.columns = columns;
		return this;
	}
	
	@ApiModelProperty(example = "22", required = true, value = "The value MUST be a valid Integer.")
	@NotNull

	@Valid

	public Integer getColumns() {
		return columns;
	}

	public void setColumns(Integer columns) {
		this.columns = columns;
	}
	

	public CollectionSummaries cloudCover(CollectionSummaryStats cloudCover) {
		this.cloudCover = cloudCover;
		return this;
	}

	/**
	 * The value MUST be a valid URL.
	 * @return href
	 */
	@ApiModelProperty(example = "https://example.openeo.org", required = true, value = "The value MUST be a valid URL.")
	@NotNull

	@Valid

	public CollectionSummaryStats getCloudCover() {
		return cloudCover;
	}

	public void setCloudCover(CollectionSummaryStats cloudCover) {
		this.cloudCover = cloudCover;
	}

	public CollectionSummaries epsg(CollectionSummaryStats epsg) {
		this.epsg = epsg;
		return this;
	}

	/**
	 * The value MUST be a valid URL.
	 * @return href
	 */
	@ApiModelProperty(example = "https://example.openeo.org", required = true, value = "The value MUST be a valid URL.")
	@NotNull

	@Valid

	public CollectionSummaryStats getEpsg() {
		return epsg;
	}

	public void setEpsg(CollectionSummaryStats epsg) {
		this.epsg = epsg;
	}

	public CollectionSummaries orbitState(List<String> orbitState) {
		this.orbitState = orbitState;
		return this;
	}

	/**
	 * The value MUST be a valid URL.
	 * @return href
	 */
	@ApiModelProperty(example = "https://example.openeo.org", required = true, value = "The value MUST be a valid URL.")
	@NotNull

	@Valid

	public List<String> getOrbitState() {
		return orbitState;
	}

	public void setOrbitState(List<String> orbitState) {
		this.orbitState = orbitState;
	}

	public CollectionSummaries gsd(List<Double> gsd) {
		this.gsd = gsd;
		return this;
	}

	/**
	 * The value MUST be a valid URL.
	 * @return href
	 */
	@ApiModelProperty(example = "https://example.openeo.org", required = true, value = "The value MUST be a valid URL.")
	@NotNull

	@Valid

	public List<Double> getGsd() {
		return gsd;
	}

	public void setGsd(List<Double> gsd) {
		this.gsd = gsd;
	}

	public CollectionSummaries instruments(List<String> instruments) {
		this.instruments = instruments;
		return this;
	}

	/**
	 * The value MUST be a valid URL.
	 * @return href
	 */
	@ApiModelProperty(example = "https://example.openeo.org", required = true, value = "The value MUST be a valid URL.")
	@NotNull

	@Valid

	public List<String> getInstruments() {
		return instruments;
	}

	public void setInstruments(List<String> instruments) {
		this.instruments = instruments;
	}

	public CollectionSummaries bands(List<BandSummary> bands) {
		this.bands = bands;
		return this;
	}

	/**
	 * The value MUST be a valid URL.
	 * @return href
	 */
	@ApiModelProperty(example = "https://example.openeo.org", required = true, value = "The value MUST be a valid URL.")
	@NotNull

	@Valid

	public List<BandSummary> getBands() {
		return bands;
	}

	public void setBands(List<BandSummary> bands) {
		this.bands = bands;
	}

	@Override
	public boolean equals(java.lang.Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		CollectionSummaries summaries = (CollectionSummaries) o;
		return Objects.equals(this.constellation, summaries.constellation) &&
				Objects.equals(this.platform, summaries.platform) &&
				Objects.equals(this.instruments, summaries.instruments) &&
				Objects.equals(this.cloudCover, summaries.cloudCover) &&
				Objects.equals(this.orbitState, summaries.orbitState) &&
				Objects.equals(this.gsd, summaries.gsd) &&
				Objects.equals(this.epsg, summaries.epsg) &&
				Objects.equals(this.bands, summaries.bands);
	}

	@Override
	public int hashCode() {
		return Objects.hash(constellation, platform, instruments, cloudCover, orbitState, gsd, epsg);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("class CollectionSummaries {\n");

		sb.append("    constellation: ").append(toIndentedString(constellation)).append("\n");
		sb.append("    platform: ").append(toIndentedString(platform)).append("\n");
		sb.append("    instruments: ").append(toIndentedString(instruments)).append("\n");
		sb.append("    eo:cloud cover: ").append(toIndentedString(cloudCover)).append("\n");
		sb.append("    sat:orbit_state: ").append(toIndentedString(orbitState)).append("\n");
		sb.append("    eo:gsd: ").append(toIndentedString(gsd)).append("\n");
		sb.append("    proj:epsg: ").append(toIndentedString(epsg)).append("\n");
		sb.append("    eo:bands: ").append(toIndentedString(bands)).append("\n");
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