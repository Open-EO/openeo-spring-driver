package org.openeo.spring.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.ArrayList;
import java.util.List;
import org.openeo.spring.model.Link;
import org.openeo.spring.model.UserDefinedProcessMeta;
import org.openapitools.jackson.nullable.JsonNullable;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * UserDefinedProcesses
 */
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2020-07-02T08:45:00.334+02:00[Europe/Rome]")
public class UserDefinedProcesses   {
  @JsonProperty("processes")
  @Valid
  private List<UserDefinedProcessMeta> processes = new ArrayList<>();

  @JsonProperty("links")
  @Valid
  private List<Link> links = new ArrayList<>();

  public UserDefinedProcesses processes(List<UserDefinedProcessMeta> processes) {
    this.processes = processes;
    return this;
  }

  public UserDefinedProcesses addProcessesItem(UserDefinedProcessMeta processesItem) {
    this.processes.add(processesItem);
    return this;
  }

  /**
   * Array of user-defined processes
   * @return processes
  */
  @ApiModelProperty(required = true, value = "Array of user-defined processes")
  @NotNull

  @Valid

  public List<UserDefinedProcessMeta> getProcesses() {
    return processes;
  }

  public void setProcesses(List<UserDefinedProcessMeta> processes) {
    this.processes = processes;
  }

  public UserDefinedProcesses links(List<Link> links) {
    this.links = links;
    return this;
  }

  public UserDefinedProcesses addLinksItem(Link linksItem) {
    this.links.add(linksItem);
    return this;
  }

  /**
   * Links related to this list of resources, for example links for pagination or alternative formats such as a human-readable HTML version. The links array MUST NOT be paginated.  If pagination is implemented, the following `rel` (relation) types apply:  1. `next` (REQUIRED): A link to the next page, except on the last page.  2. `prev` (OPTIONAL): A link to the previous page, except on the first page.  3. `first` (OPTIONAL): A link to the first page, except on the first page.  4. `last` (OPTIONAL): A link to the last page, except on the last page.  For additional relation types see also the lists of [common relation types in openEO](#section/API-Principles/Web-Linking).
   * @return links
  */
  @ApiModelProperty(required = true, value = "Links related to this list of resources, for example links for pagination or alternative formats such as a human-readable HTML version. The links array MUST NOT be paginated.  If pagination is implemented, the following `rel` (relation) types apply:  1. `next` (REQUIRED): A link to the next page, except on the last page.  2. `prev` (OPTIONAL): A link to the previous page, except on the first page.  3. `first` (OPTIONAL): A link to the first page, except on the first page.  4. `last` (OPTIONAL): A link to the last page, except on the last page.  For additional relation types see also the lists of [common relation types in openEO](#section/API-Principles/Web-Linking).")
  @NotNull

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
    UserDefinedProcesses userDefinedProcesses = (UserDefinedProcesses) o;
    return Objects.equals(this.processes, userDefinedProcesses.processes) &&
        Objects.equals(this.links, userDefinedProcesses.links);
  }

  @Override
  public int hashCode() {
    return Objects.hash(processes, links);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class UserDefinedProcesses {\n");
    
    sb.append("    processes: ").append(toIndentedString(processes)).append("\n");
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

