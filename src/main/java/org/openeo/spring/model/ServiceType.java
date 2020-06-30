package org.openeo.spring.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.openeo.spring.model.Argument;
import org.openeo.spring.model.Link;
import org.openeo.spring.model.Parameter;
import org.openapitools.jackson.nullable.JsonNullable;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * ServiceType
 */
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2020-06-30T14:48:14.663+02:00[Europe/Rome]")
public class ServiceType   {
  @JsonProperty("title")
  private String title;

  @JsonProperty("description")
  private String description;

  @JsonProperty("configuration")
  @Valid
  private Map<String, Argument> _configuration = null;

  @JsonProperty("process_parameters")
  @Valid
  private List<Parameter> processParameters = null;

  @JsonProperty("links")
  @Valid
  private List<Link> links = null;

  public ServiceType title(String title) {
    this.title = title;
    return this;
  }

  /**
   * A human-readable short title to be displayed to users **in addition** to the names specified in the keys. This property is only for better user experience so that users can understand the names better. Example titles could be `GeoTiff` for the key `GTiff` (for file formats) or `OGC Web Map Service` for the key `WMS` (for service types). The title MUST NOT be used in communication (e.g. in process graphs), although clients MAY translate the titles into the corresponding names.
   * @return title
  */
  @ApiModelProperty(value = "A human-readable short title to be displayed to users **in addition** to the names specified in the keys. This property is only for better user experience so that users can understand the names better. Example titles could be `GeoTiff` for the key `GTiff` (for file formats) or `OGC Web Map Service` for the key `WMS` (for service types). The title MUST NOT be used in communication (e.g. in process graphs), although clients MAY translate the titles into the corresponding names.")


  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public ServiceType description(String description) {
    this.description = description;
    return this;
  }

  /**
   * Detailed description to explain the entity.  [CommonMark 0.29](http://commonmark.org/) syntax MAY be used for rich text representation.
   * @return description
  */
  @ApiModelProperty(value = "Detailed description to explain the entity.  [CommonMark 0.29](http://commonmark.org/) syntax MAY be used for rich text representation.")


  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public ServiceType _configuration(Map<String, Argument> _configuration) {
    this._configuration = _configuration;
    return this;
  }

  public ServiceType putConfigurationItem(String key, Argument _configurationItem) {
    if (this._configuration == null) {
      this._configuration = new HashMap<>();
    }
    this._configuration.put(key, _configurationItem);
    return this;
  }

  /**
   * Map of supported configuration settings made available to the creator the service.
   * @return _configuration
  */
  @ApiModelProperty(value = "Map of supported configuration settings made available to the creator the service.")

  @Valid

  public Map<String, Argument> getConfiguration() {
    return _configuration;
  }

  public void setConfiguration(Map<String, Argument> _configuration) {
    this._configuration = _configuration;
  }

  public ServiceType processParameters(List<Parameter> processParameters) {
    this.processParameters = processParameters;
    return this;
  }

  public ServiceType addProcessParametersItem(Parameter processParametersItem) {
    if (this.processParameters == null) {
      this.processParameters = new ArrayList<>();
    }
    this.processParameters.add(processParametersItem);
    return this;
  }

  /**
   * List of parameters made available to user-defined processes.
   * @return processParameters
  */
  @ApiModelProperty(value = "List of parameters made available to user-defined processes.")

  @Valid

  public List<Parameter> getProcessParameters() {
    return processParameters;
  }

  public void setProcessParameters(List<Parameter> processParameters) {
    this.processParameters = processParameters;
  }

  public ServiceType links(List<Link> links) {
    this.links = links;
    return this;
  }

  public ServiceType addLinksItem(Link linksItem) {
    if (this.links == null) {
      this.links = new ArrayList<>();
    }
    this.links.add(linksItem);
    return this;
  }

  /**
   * Links related to this service type, e.g. more information about the configuration settings and process parameters.  For relation types see the lists of [common relation types in openEO](#section/API-Principles/Web-Linking).
   * @return links
  */
  @ApiModelProperty(value = "Links related to this service type, e.g. more information about the configuration settings and process parameters.  For relation types see the lists of [common relation types in openEO](#section/API-Principles/Web-Linking).")

  @Valid

  public List<Link> getLinks() {
    return links;
  }

  public void setLinks(List<Link> links) {
    this.links = links;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ServiceType serviceType = (ServiceType) o;
    return Objects.equals(this.title, serviceType.title) &&
        Objects.equals(this.description, serviceType.description) &&
        Objects.equals(this._configuration, serviceType._configuration) &&
        Objects.equals(this.processParameters, serviceType.processParameters) &&
        Objects.equals(this.links, serviceType.links);
  }

  @Override
  public int hashCode() {
    return Objects.hash(title, description, _configuration, processParameters, links);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ServiceType {\n");
    
    sb.append("    title: ").append(toIndentedString(title)).append("\n");
    sb.append("    description: ").append(toIndentedString(description)).append("\n");
    sb.append("    _configuration: ").append(toIndentedString(_configuration)).append("\n");
    sb.append("    processParameters: ").append(toIndentedString(processParameters)).append("\n");
    sb.append("    links: ").append(toIndentedString(links)).append("\n");
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

