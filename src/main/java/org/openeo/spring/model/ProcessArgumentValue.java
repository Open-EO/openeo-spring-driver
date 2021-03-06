package org.openeo.spring.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * Arguments for a process. See the API documentation for more information.
 */
@ApiModel(description = "Arguments for a process. See the API documentation for more information.")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2020-07-02T08:45:00.334+02:00[Europe/Rome]")
public class ProcessArgumentValue   {
  @JsonProperty("from_parameter")
  private String fromParameter;

  @JsonProperty("from_node")
  private String fromNode;

  @JsonProperty("process_graph")
  @Valid
  private Map<String, Object> processGraph = new HashMap<>();

  @JsonProperty("id")
  private String id;

  @JsonProperty("summary")
  private String summary;

  @JsonProperty("description")
  private String description;

  @JsonProperty("categories")
  @Valid
  private List<String> categories = null;

  @JsonProperty("parameters")
  @Valid
  private List<ProcessParameter> parameters = null;

  @JsonProperty("returns")
  private ProcessReturnValue returns;

  @JsonProperty("deprecated")
  private Boolean deprecated = false;

  @JsonProperty("experimental")
  private Boolean experimental = false;

  @JsonProperty("exceptions")
  @Valid
  private Map<String, Object> exceptions = null;

  @JsonProperty("examples")
  @Valid
  private List<ProcessExample> examples = null;

  @JsonProperty("links")
  @Valid
  private List<Link> links = null;

  public ProcessArgumentValue fromParameter(String fromParameter) {
    this.fromParameter = fromParameter;
    return this;
  }

  /**
   * The name of the parameter that data is expected to come from.
   * @return fromParameter
  */
  @ApiModelProperty(required = true, value = "The name of the parameter that data is expected to come from.")
  @NotNull


  public String getFromParameter() {
    return fromParameter;
  }

  public void setFromParameter(String fromParameter) {
    this.fromParameter = fromParameter;
  }

  public ProcessArgumentValue fromNode(String fromNode) {
    this.fromNode = fromNode;
    return this;
  }

  /**
   * The ID of the node that data is expected to come from.
   * @return fromNode
  */
  @ApiModelProperty(required = true, value = "The ID of the node that data is expected to come from.")
  @NotNull


  public String getFromNode() {
    return fromNode;
  }

  public void setFromNode(String fromNode) {
    this.fromNode = fromNode;
  }

  public ProcessArgumentValue processGraph(Map<String, Object> processGraph) {
    this.processGraph = processGraph;
    return this;
  }

  public ProcessArgumentValue putProcessGraphItem(String key, Object processGraphItem) {
    this.processGraph.put(key, processGraphItem);
    return this;
  }

  /**
   * A process graph defines a graph-like structure as a connected set of executable processes. Each key is a unique identifier (node ID) that is used to refer to the process in the graph.
   * @return processGraph
  */
  @ApiModelProperty(example = "{\"dc\":{\"process_id\":\"load_collection\",\"arguments\":{\"id\":\"Sentinel-2\",\"spatial_extent\":{\"west\":16.1,\"east\":16.6,\"north\":48.6,\"south\":47.2},\"temporal_extent\":[\"2018-01-01\",\"2018-02-01\"]}},\"bands\":{\"process_id\":\"filter_bands\",\"description\":\"Filter and order the bands. The order is important for the following reduce operation.\",\"arguments\":{\"data\":{\"from_node\":\"dc\"},\"bands\":[\"B08\",\"B04\",\"B02\"]}},\"evi\":{\"process_id\":\"reduce\",\"description\":\"Compute the EVI. Formula: 2.5 * (NIR - RED) / (1 + NIR + 6*RED + -7.5*BLUE)\",\"arguments\":{\"data\":{\"from_node\":\"bands\"},\"dimension\":\"bands\",\"reducer\":{\"process_graph\":{\"nir\":{\"process_id\":\"array_element\",\"arguments\":{\"data\":{\"from_parameter\":\"data\"},\"index\":0}},\"red\":{\"process_id\":\"array_element\",\"arguments\":{\"data\":{\"from_parameter\":\"data\"},\"index\":1}},\"blue\":{\"process_id\":\"array_element\",\"arguments\":{\"data\":{\"from_parameter\":\"data\"},\"index\":2}},\"sub\":{\"process_id\":\"subtract\",\"arguments\":{\"data\":[{\"from_node\":\"nir\"},{\"from_node\":\"red\"}]}},\"p1\":{\"process_id\":\"product\",\"arguments\":{\"data\":[6,{\"from_node\":\"red\"}]}},\"p2\":{\"process_id\":\"product\",\"arguments\":{\"data\":[-7.5,{\"from_node\":\"blue\"}]}},\"sum\":{\"process_id\":\"sum\",\"arguments\":{\"data\":[1,{\"from_node\":\"nir\"},{\"from_node\":\"p1\"},{\"from_node\":\"p2\"}]}},\"div\":{\"process_id\":\"divide\",\"arguments\":{\"data\":[{\"from_node\":\"sub\"},{\"from_node\":\"sum\"}]}},\"p3\":{\"process_id\":\"product\",\"arguments\":{\"data\":[2.5,{\"from_node\":\"div\"}]},\"result\":true}}}}},\"mintime\":{\"process_id\":\"reduce\",\"description\":\"Compute a minimum time composite by reducing the temporal dimension\",\"arguments\":{\"data\":{\"from_node\":\"evi\"},\"dimension\":\"temporal\",\"reducer\":{\"process_graph\":{\"min\":{\"process_id\":\"min\",\"arguments\":{\"data\":{\"from_parameter\":\"data\"}},\"result\":true}}}}},\"save\":{\"process_id\":\"save_result\",\"arguments\":{\"data\":{\"from_node\":\"mintime\"},\"format\":\"GTiff\"},\"result\":true}}", required = true, value = "A process graph defines a graph-like structure as a connected set of executable processes. Each key is a unique identifier (node ID) that is used to refer to the process in the graph.")
  @NotNull


  public Map<String, Object> getProcessGraph() {
    return processGraph;
  }

  public void setProcessGraph(Map<String, Object> processGraph) {
    this.processGraph = processGraph;
  }

  public ProcessArgumentValue id(String id) {
    this.id = id;
    return this;
  }

  /**
   * Unique identifier for the process.  MUST be unique across all predefined and user-defined processes available for the authenticated user. If a back-end adds a process with the name of a user-defined process, the user-defined process takes preference over predefined processes in execution to not break existing process graphs.  Back-ends may choose to enforce a prefix for user-defined processes while storing the process, e.g. `user_ndvi` with `user_` being the prefix. Prefixes must still follow the pattern.
   * @return id
  */
  @ApiModelProperty(example = "ndvi", value = "Unique identifier for the process.  MUST be unique across all predefined and user-defined processes available for the authenticated user. If a back-end adds a process with the name of a user-defined process, the user-defined process takes preference over predefined processes in execution to not break existing process graphs.  Back-ends may choose to enforce a prefix for user-defined processes while storing the process, e.g. `user_ndvi` with `user_` being the prefix. Prefixes must still follow the pattern.")

@Pattern(regexp="^\\w+$") 
  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public ProcessArgumentValue summary(String summary) {
    this.summary = summary;
    return this;
  }

  /**
   * A short summary of what the process does.
   * @return summary
  */
  @ApiModelProperty(value = "A short summary of what the process does.")


  public String getSummary() {
    return summary;
  }

  public void setSummary(String summary) {
    this.summary = summary;
  }

  public ProcessArgumentValue description(String description) {
    this.description = description;
    return this;
  }

  /**
   * Detailed description to explain the entity.  [CommonMark 0.29](http://commonmark.org/) syntax MAY be used for rich text representation. In addition to the CommonMark syntax, clients can convert process IDs that are formatted as in the following example into links instead of code blocks: ``` ``process_id()`` ```
   * @return description
  */
  @ApiModelProperty(value = "Detailed description to explain the entity.  [CommonMark 0.29](http://commonmark.org/) syntax MAY be used for rich text representation. In addition to the CommonMark syntax, clients can convert process IDs that are formatted as in the following example into links instead of code blocks: ``` ``process_id()`` ```")


  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public ProcessArgumentValue categories(List<String> categories) {
    this.categories = categories;
    return this;
  }

  public ProcessArgumentValue addCategoriesItem(String categoriesItem) {
    if (this.categories == null) {
      this.categories = new ArrayList<>();
    }
    this.categories.add(categoriesItem);
    return this;
  }

  /**
   * A list of categories.
   * @return categories
  */
  @ApiModelProperty(value = "A list of categories.")


  public List<String> getCategories() {
    return categories;
  }

  public void setCategories(List<String> categories) {
    this.categories = categories;
  }

  public ProcessArgumentValue parameters(List<ProcessParameter> parameters) {
    this.parameters = parameters;
    return this;
  }

  public ProcessArgumentValue addParametersItem(ProcessParameter parametersItem) {
    if (this.parameters == null) {
      this.parameters = new ArrayList<>();
    }
    this.parameters.add(parametersItem);
    return this;
  }

  /**
   * A list of parameters.  The order in the array corresponds to the parameter order to be used in clients that don't support named parameters.  **Note:** Specifying an empty array is different from (if allowed) `null` or the property being absent. An empty array means the process has no parameters. `null` / property absent means that the parameters are unknown as the user has not specified them. There could still be parameters in the process graph, if one is specified.
   * @return parameters
  */
  @ApiModelProperty(value = "A list of parameters.  The order in the array corresponds to the parameter order to be used in clients that don't support named parameters.  **Note:** Specifying an empty array is different from (if allowed) `null` or the property being absent. An empty array means the process has no parameters. `null` / property absent means that the parameters are unknown as the user has not specified them. There could still be parameters in the process graph, if one is specified.")

  @Valid

  public List<ProcessParameter> getParameters() {
    return parameters;
  }

  public void setParameters(List<ProcessParameter> parameters) {
    this.parameters = parameters;
  }

  public ProcessArgumentValue returns(ProcessReturnValue returns) {
    this.returns = returns;
    return this;
  }

  /**
   * Get returns
   * @return returns
  */
  @ApiModelProperty(value = "")

  @Valid

  public ProcessReturnValue getReturns() {
    return returns;
  }

  public void setReturns(ProcessReturnValue returns) {
    this.returns = returns;
  }

  public ProcessArgumentValue deprecated(Boolean deprecated) {
    this.deprecated = deprecated;
    return this;
  }

  /**
   * Specifies that the process or parameter is deprecated with the potential to be removed in any of the next versions. It should be transitioned out of usage as soon as possible and users should refrain from using it in new implementations.  For processes a link with relation type `latest-version` SHOULD be added to the `links` and MUST refer to the process that can be used instead.
   * @return deprecated
  */
  @ApiModelProperty(value = "Specifies that the process or parameter is deprecated with the potential to be removed in any of the next versions. It should be transitioned out of usage as soon as possible and users should refrain from using it in new implementations.  For processes a link with relation type `latest-version` SHOULD be added to the `links` and MUST refer to the process that can be used instead.")


  public Boolean isDeprecated() {
    return deprecated;
  }

  public void setDeprecated(Boolean deprecated) {
    this.deprecated = deprecated;
  }

  public ProcessArgumentValue experimental(Boolean experimental) {
    this.experimental = experimental;
    return this;
  }

  /**
   * Declares the process or parameter to be experimental, which means that it is likely to change or may produce unpredictable behaviour. Users should refrain from using it in production,  but still feel encouraged to try it out and give feedback.
   * @return experimental
  */
  @ApiModelProperty(value = "Declares the process or parameter to be experimental, which means that it is likely to change or may produce unpredictable behaviour. Users should refrain from using it in production,  but still feel encouraged to try it out and give feedback.")


  public Boolean isExperimental() {
    return experimental;
  }

  public void setExperimental(Boolean experimental) {
    this.experimental = experimental;
  }

  public ProcessArgumentValue exceptions(Map<String, Object> exceptions) {
    this.exceptions = exceptions;
    return this;
  }

  public ProcessArgumentValue putExceptionsItem(String key, Object exceptionsItem) {
    if (this.exceptions == null) {
      this.exceptions = new HashMap<>();
    }
    this.exceptions.put(key, exceptionsItem);
    return this;
  }

  /**
   * Declares any exceptions (errors) that might occur during execution of this process. This list is just for informative purposes and may be incomplete. This list MUST only contain exceptions that stop the execution of a process and MUST NOT contain warnings, notices or debugging messages.  The keys define the error code and MUST match the following pattern: `^\\w+$`  This schema follows the schema of the general openEO error list (see errors.json).
   * @return exceptions
  */
  @ApiModelProperty(value = "Declares any exceptions (errors) that might occur during execution of this process. This list is just for informative purposes and may be incomplete. This list MUST only contain exceptions that stop the execution of a process and MUST NOT contain warnings, notices or debugging messages.  The keys define the error code and MUST match the following pattern: `^\\w+$`  This schema follows the schema of the general openEO error list (see errors.json).")


  public Map<String, Object> getExceptions() {
    return exceptions;
  }

  public void setExceptions(Map<String, Object> exceptions) {
    this.exceptions = exceptions;
  }

  public ProcessArgumentValue examples(List<ProcessExample> examples) {
    this.examples = examples;
    return this;
  }

  public ProcessArgumentValue addExamplesItem(ProcessExample examplesItem) {
    if (this.examples == null) {
      this.examples = new ArrayList<>();
    }
    this.examples.add(examplesItem);
    return this;
  }

  /**
   * Examples, may be used for unit tests.
   * @return examples
  */
  @ApiModelProperty(value = "Examples, may be used for unit tests.")

  @Valid

  public List<ProcessExample> getExamples() {
    return examples;
  }

  public void setExamples(List<ProcessExample> examples) {
    this.examples = examples;
  }

  public ProcessArgumentValue links(List<Link> links) {
    this.links = links;
    return this;
  }

  public ProcessArgumentValue addLinksItem(Link linksItem) {
    if (this.links == null) {
      this.links = new ArrayList<>();
    }
    this.links.add(linksItem);
    return this;
  }

  /**
   * Links related to this process, e.g. additional external documentation. It is RECOMMENDED to provide links with the following `rel` (relation) types: 1. `latest-version`: If a process has been marked as deprecated, a link should point to the preferred version of the process. The relation types `predecessor-version` (link to older version) and `successor-version` (link to newer version) can also be used to show the relation between versions. 2. `example`: Links to examples of other processes that use this process. 3. `cite-as`: For all DOIs associated with the process, the respective DOI links should be added. For additional relation types see also the lists of [common relation types in openEO](#section/API-Principles/Web-Linking).
   * @return links
  */
  @ApiModelProperty(value = "Links related to this process, e.g. additional external documentation. It is RECOMMENDED to provide links with the following `rel` (relation) types: 1. `latest-version`: If a process has been marked as deprecated, a link should point to the preferred version of the process. The relation types `predecessor-version` (link to older version) and `successor-version` (link to newer version) can also be used to show the relation between versions. 2. `example`: Links to examples of other processes that use this process. 3. `cite-as`: For all DOIs associated with the process, the respective DOI links should be added. For additional relation types see also the lists of [common relation types in openEO](#section/API-Principles/Web-Linking).")

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
    ProcessArgumentValue processArgumentValue = (ProcessArgumentValue) o;
    return Objects.equals(this.fromParameter, processArgumentValue.fromParameter) &&
        Objects.equals(this.fromNode, processArgumentValue.fromNode) &&
        Objects.equals(this.processGraph, processArgumentValue.processGraph) &&
        Objects.equals(this.id, processArgumentValue.id) &&
        Objects.equals(this.summary, processArgumentValue.summary) &&
        Objects.equals(this.description, processArgumentValue.description) &&
        Objects.equals(this.categories, processArgumentValue.categories) &&
        Objects.equals(this.parameters, processArgumentValue.parameters) &&
        Objects.equals(this.returns, processArgumentValue.returns) &&
        Objects.equals(this.deprecated, processArgumentValue.deprecated) &&
        Objects.equals(this.experimental, processArgumentValue.experimental) &&
        Objects.equals(this.exceptions, processArgumentValue.exceptions) &&
        Objects.equals(this.examples, processArgumentValue.examples) &&
        Objects.equals(this.links, processArgumentValue.links);
  }

  @Override
  public int hashCode() {
    return Objects.hash(fromParameter, fromNode, processGraph, id, summary, description, categories, parameters, returns, deprecated, experimental, exceptions, examples, links);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ProcessArgumentValue {\n");
    
    sb.append("    fromParameter: ").append(toIndentedString(fromParameter)).append("\n");
    sb.append("    fromNode: ").append(toIndentedString(fromNode)).append("\n");
    sb.append("    processGraph: ").append(toIndentedString(processGraph)).append("\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    summary: ").append(toIndentedString(summary)).append("\n");
    sb.append("    description: ").append(toIndentedString(description)).append("\n");
    sb.append("    categories: ").append(toIndentedString(categories)).append("\n");
    sb.append("    parameters: ").append(toIndentedString(parameters)).append("\n");
    sb.append("    returns: ").append(toIndentedString(returns)).append("\n");
    sb.append("    deprecated: ").append(toIndentedString(deprecated)).append("\n");
    sb.append("    experimental: ").append(toIndentedString(experimental)).append("\n");
    sb.append("    exceptions: ").append(toIndentedString(exceptions)).append("\n");
    sb.append("    examples: ").append(toIndentedString(examples)).append("\n");
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

