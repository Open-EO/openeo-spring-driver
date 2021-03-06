package org.openeo.spring.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.openapitools.jackson.nullable.JsonNullable;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModelProperty;

/**
 * DockerContainer
 */
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2020-07-02T08:45:00.334+02:00[Europe/Rome]")
public class DockerContainer   {
  @JsonProperty("docker")
  private String docker;

  @JsonProperty("default")
  private JsonNullable<Object> _default = JsonNullable.undefined();

  @JsonProperty("tags")
  @Valid
  private List<String> tags = new ArrayList<>();

  public DockerContainer docker(String docker) {
    this.docker = docker;
    return this;
  }

  /**
   * Identifier of a Docker image on Docker Hub or a private repository, i.e. the docker image name.
   * @return docker
  */
  @ApiModelProperty(required = true, value = "Identifier of a Docker image on Docker Hub or a private repository, i.e. the docker image name.")
  @NotNull


  public String getDocker() {
    return docker;
  }

  public void setDocker(String docker) {
    this.docker = docker;
  }

  public DockerContainer _default(Object _default) {
    this._default = JsonNullable.of(_default);
    return this;
  }

  /**
   * The default tag. MUST be one of the values in the `tags` array.
   * @return _default
  */
  @ApiModelProperty(required = true, value = "The default tag. MUST be one of the values in the `tags` array.")
  @NotNull


  public JsonNullable<Object> getDefault() {
    return _default;
  }

  public void setDefault(JsonNullable<Object> _default) {
    this._default = _default;
  }

  public DockerContainer tags(List<String> tags) {
    this.tags = tags;
    return this;
  }

  public DockerContainer addTagsItem(String tagsItem) {
    this.tags.add(tagsItem);
    return this;
  }

  /**
   * The docker tags that are supported.
   * @return tags
  */
  @ApiModelProperty(required = true, value = "The docker tags that are supported.")
  @NotNull

@Size(min=1) 
  public List<String> getTags() {
    return tags;
  }

  public void setTags(List<String> tags) {
    this.tags = tags;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    DockerContainer dockerContainer = (DockerContainer) o;
    return Objects.equals(this.docker, dockerContainer.docker) &&
        Objects.equals(this._default, dockerContainer._default) &&
        Objects.equals(this.tags, dockerContainer.tags);
  }

  @Override
  public int hashCode() {
    return Objects.hash(docker, _default, tags);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class DockerContainer {\n");
    
    sb.append("    docker: ").append(toIndentedString(docker)).append("\n");
    sb.append("    _default: ").append(toIndentedString(_default)).append("\n");
    sb.append("    tags: ").append(toIndentedString(tags)).append("\n");
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

