package org.openeo.spring.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModelProperty;

/**
 * Asset
 */
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2020-07-02T08:45:00.334+02:00[Europe/Rome]")
@Entity
@Table(name = "asset")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Asset implements Serializable {

	private static final long serialVersionUID = 7914016373878205106L;
	
	@Id
	@JsonProperty("href")
	private String href;

	@JsonProperty("title")
	private String title;

	@JsonProperty("description")
	private String description;

	@JsonProperty("type")
	@Column(name = "content_type")
	private String type;

	@JsonProperty("roles")
	@Valid
	@Embedded
	private List<String> roles = null;

	public Asset href(String href) {
		this.href = href;
		return this;
	}

	/**
	 * URL to the downloadable asset. The URLs SHOULD be available without
	 * authentication so that external clients can download them easily. If the data
	 * is confidential, signed URLs SHOULD be used to protect against unauthorized
	 * access from third parties.
	 * 
	 * @return href
	 */
	@ApiModelProperty(required = true, value = "URL to the downloadable asset. The URLs SHOULD be available without authentication so that external clients can download them easily. If the data is confidential, signed URLs SHOULD be used to protect against unauthorized access from third parties.")
	@NotNull
	public String getHref() {
		return href;
	}

	public void setHref(String href) {
		this.href = href;
	}

	public Asset title(String title) {
		this.title = title;
		return this;
	}

	/**
	 * The displayed title for clients and users.
	 * 
	 * @return title
	 */
	@ApiModelProperty(value = "The displayed title for clients and users.")
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Asset description(String description) {
		this.description = description;
		return this;
	}

	/**
	 * Multi-line description to explain the asset. [CommonMark
	 * 0.29](http://commonmark.org/) syntax MAY be used for rich text
	 * representation.
	 * 
	 * @return description
	 */
	@ApiModelProperty(value = "Multi-line description to explain the asset.  [CommonMark 0.29](http://commonmark.org/) syntax MAY be used for rich text representation.")
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Asset type(String type) {
		this.type = type;
		return this;
	}

	/**
	 * Media type of the asset.
	 * 
	 * @return type
	 */
	@ApiModelProperty(example = "image/tiff; application=geotiff", value = "Media type of the asset.")
	@NotNull
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Asset roles(List<String> roles) {
		this.roles = roles;
		return this;
	}

	public Asset addRolesItem(String rolesItem) {
		if (this.roles == null) {
			this.roles = new ArrayList<>();
		}
		this.roles.add(rolesItem);
		return this;
	}

	/**
	 * Purposes of the asset. Can be any value, but commonly used values are: *
	 * `thumbnail`: A visualization of the data, usually a lower-resolution true
	 * color image in JPEG or PNG format. * `reproducibility`: Information how the
	 * data was produced and/or can be reproduced, e.g. the process graph used to
	 * compute the data in JSON format. * `data`: The computed data in the format
	 * specified by the user in the process graph (applicable in `GET
	 * /jobs/{job_id}/results` only). * `metadata`: Additional metadata available
	 * for the computed data.
	 * 
	 * @return roles
	 */
	@ApiModelProperty(example = "[\"data\"]", value = "Purposes of the asset. Can be any value, but commonly used values are:  * `thumbnail`: A visualization of the data, usually a lower-resolution true color image in JPEG or PNG format. * `reproducibility`: Information how the data was produced and/or can be reproduced, e.g. the process graph used to compute the data in JSON format. * `data`: The computed data in the format specified by the user in the process graph (applicable in `GET /jobs/{job_id}/results` only). * `metadata`: Additional metadata available for the computed data.")
	public List<String> getRoles() {
		return roles;
	}

	public void setRoles(List<String> roles) {
		this.roles = roles;
	}

	@Override
	public boolean equals(java.lang.Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		Asset asset = (Asset) o;
		return Objects.equals(this.href, asset.href) && Objects.equals(this.title, asset.title)
				&& Objects.equals(this.description, asset.description) && Objects.equals(this.type, asset.type)
				&& Objects.equals(this.roles, asset.roles);
	}

	@Override
	public int hashCode() {
		return Objects.hash(href, title, description, type, roles);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("class Asset {\n");

		sb.append("    href: ").append(toIndentedString(href)).append("\n");
		sb.append("    title: ").append(toIndentedString(title)).append("\n");
		sb.append("    description: ").append(toIndentedString(description)).append("\n");
		sb.append("    type: ").append(toIndentedString(type)).append("\n");
		sb.append("    roles: ").append(toIndentedString(roles)).append("\n");
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
