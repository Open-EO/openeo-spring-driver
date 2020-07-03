package org.openeo.spring.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.springframework.core.io.Resource;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModelProperty;

/**
 * WorkspaceFiles
 */
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2020-07-02T08:45:00.334+02:00[Europe/Rome]")
public class WorkspaceFiles   {
  @JsonProperty("files")
  @Valid
  private List<Resource> files = new ArrayList<>();

  @JsonProperty("links")
  @Valid
  private List<Link> links = new ArrayList<>();

  public WorkspaceFiles files(List<Resource> files) {
    this.files = files;
    return this;
  }

  public WorkspaceFiles addFilesItem(Resource filesItem) {
    this.files.add(filesItem);
    return this;
  }

  /**
   * Get files
   * @return files
  */
  @ApiModelProperty(required = true, value = "")
  @NotNull

  @Valid

  public List<Resource> getFiles() {
    return files;
  }

  public void setFiles(List<Resource> files) {
    this.files = files;
  }

  public WorkspaceFiles links(List<Link> links) {
    this.links = links;
    return this;
  }

  public WorkspaceFiles addLinksItem(Link linksItem) {
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
    WorkspaceFiles workspaceFiles = (WorkspaceFiles) o;
    return Objects.equals(this.files, workspaceFiles.files) &&
        Objects.equals(this.links, workspaceFiles.links);
  }

  @Override
  public int hashCode() {
    return Objects.hash(files, links);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class WorkspaceFiles {\n");
    
    sb.append("    files: ").append(toIndentedString(files)).append("\n");
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

