package org.openeo.spring.model;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.Valid;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.annotations.GenericGenerator;
import org.json.JSONArray;
import org.json.JSONObject;
import org.openeo.spring.json.ProcessSerializerFull;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import io.swagger.annotations.ApiModelProperty;

/**
 * Process
 */
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2020-07-02T08:45:00.334+02:00[Europe/Rome]")
@Entity
@Table(name = "process")
@JsonSerialize(using = ProcessSerializerFull.class)
public class Process implements Serializable{
	
	/**
	 * 
	 */
	//@JsonProperty("engine")
	@JsonIgnore
	@Embedded
	private List<EngineTypes> engine = null;
	
	private static final long serialVersionUID = -6102545771306725349L;
	
	@Transient
	private final Logger log = LogManager.getLogger(Process.class);
	
	@Id
	@JsonProperty("id")
	@Column(name = "process_id")
	@GenericGenerator(name = "uuid", strategy = "uuid2")
	@GeneratedValue(generator = "uuid")
	private String id;
	
	@JsonIgnore
	@OneToOne( cascade = {CascadeType.ALL}, mappedBy = "process")
	private Job job;

	@JsonProperty("summary")
	private String summary;

	@JsonProperty("description")
	@Column(name = "process_description")
	private String description;

	@JsonProperty("categories")
	@Valid
	@Embedded
	private List<String> categories = null;

	@JsonProperty("parameters")
	@Valid
	@Lob
	private byte[] parameters = null;

	//TODO fix the parsing of this from a json perspective of deserialization 
	@JsonProperty("returns")
	@Valid
	@Embedded
	private Object returns = null;

	@JsonProperty("deprecated")
	private Boolean deprecated = false;

	@JsonProperty("experimental")
	private Boolean experimental = false;

	@JsonProperty("exceptions")
	@Valid
	@Embedded
	private Object exceptions = null;

	@JsonProperty("examples")
	@Valid
	@Lob
	private byte[] examples = null;

	@JsonProperty("links")
	@Valid
	@Embedded
	private List<Link> links = null;
	
	@JsonProperty("process_graph")
	@Valid
	@Lob
	private byte[] processGraph = null;

	public List<EngineTypes> getEngine() {
		return engine;
	}

	public void setEngine(List<EngineTypes> enginesList) {
		this.engine = enginesList;
	}
	
	public void addEngine(EngineTypes engine) {
		if (this.engine == null) {
			this.engine = new ArrayList<>();
		}
		this.engine.add(engine);
	}

	public Process id(String id) {
		this.id = id;
		return this;
	}

	/**
	 * Unique identifier for the process. MUST be unique across all predefined and
	 * user-defined processes available for the authenticated user. If a back-end
	 * adds a process with the name of a user-defined process, the user-defined
	 * process takes preference over predefined processes in execution to not break
	 * existing process graphs. Back-ends may choose to enforce a prefix for
	 * user-defined processes while storing the process, e.g. `user_ndvi` with
	 * `user_` being the prefix. Prefixes must still follow the pattern.
	 * 
	 * @return id
	 */
	@ApiModelProperty(example = "ndvi", value = "Unique identifier for the process.  MUST be unique across all predefined and user-defined processes available for the authenticated user. If a back-end adds a process with the name of a user-defined process, the user-defined process takes preference over predefined processes in execution to not break existing process graphs.  Back-ends may choose to enforce a prefix for user-defined processes while storing the process, e.g. `user_ndvi` with `user_` being the prefix. Prefixes must still follow the pattern.")

//	@Pattern(regexp = "^\\w+$")
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Job getJob() {
		return job;
	}

	public void setJob(Job job) {
		this.job = job;
	}

	public Process summary(String summary) {
		this.summary = summary;
		return this;
	}

	/**
	 * A short summary of what the process does.
	 * 
	 * @return summary
	 */
	@ApiModelProperty(value = "A short summary of what the process does.")

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public Process description(String description) {
		this.description = description;
		return this;
	}

	/**
	 * Detailed description to explain the entity. [CommonMark
	 * 0.29](http://commonmark.org/) syntax MAY be used for rich text
	 * representation. In addition to the CommonMark syntax, clients can convert
	 * process IDs that are formatted as in the following example into links instead
	 * of code blocks: ``` ``process_id()`` ```
	 * 
	 * @return description
	 */
	@ApiModelProperty(value = "Detailed description to explain the entity.  [CommonMark 0.29](http://commonmark.org/) syntax MAY be used for rich text representation. In addition to the CommonMark syntax, clients can convert process IDs that are formatted as in the following example into links instead of code blocks: ``` ``process_id()`` ```")

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Process categories(List<String> categories) {
		this.categories = categories;
		return this;
	}

	public Process addCategoriesItem(String categoriesItem) {
		if (this.categories == null) {
			this.categories = new ArrayList<>();
		}
		this.categories.add(categoriesItem);
		return this;
	}

	/**
	 * A list of categories.
	 * 
	 * @return categories
	 */
	@ApiModelProperty(value = "A list of categories.")

	public List<String> getCategories() {
		return categories;
	}

	public void setCategories(List<String> categories) {
		this.categories = categories;
	}

	public Process parameters(Object parameters) {
		log.debug("Called: processGraph(Object processGraph)");
		try {
			 ByteArrayOutputStream out = new ByteArrayOutputStream();
		    ObjectOutputStream os = new ObjectOutputStream(out);
		    os.writeObject(parameters);
		    this.parameters = out.toByteArray();
		} catch (Exception e) {
			log.error("processGraph(Object processGraph): An error occured while deserializing process graph from byte array: " + e.getMessage());
			StringBuilder builder = new StringBuilder();
			for (StackTraceElement element : e.getStackTrace()) {
				builder.append(element.toString() + "\n");
			}
			log.error(builder.toString());
		}
		return this;
	}

//	public Process addParametersItem(ProcessParameter parametersItem) {
//		if (this.parameters == null) {
//			this.parameters = new ArrayList<>();
//		}
//		this.parameters.add(parametersItem);
//		return this;
//	}

	/**
	 * A list of parameters. The order in the array corresponds to the parameter
	 * order to be used in clients that don't support named parameters. **Note:**
	 * Specifying an empty array is different from (if allowed) `null` or the
	 * property being absent. An empty array means the process has no parameters.
	 * `null` / property absent means that the parameters are unknown as the user
	 * has not specified them. There could still be parameters in the process graph,
	 * if one is specified.
	 * 
	 * @return parameters
	 */
	@ApiModelProperty(value = "A list of parameters.  The order in the array corresponds to the parameter order to be used in clients that don't support named parameters.  **Note:** Specifying an empty array is different from (if allowed) `null` or the property being absent. An empty array means the process has no parameters. `null` / property absent means that the parameters are unknown as the user has not specified them. There could still be parameters in the process graph, if one is specified.")

	@Valid

	public Object getParameters() {
		log.debug("Called: getParameters(Object parameters)");
		if(this.parameters == null) return null;
		ByteArrayInputStream in = new ByteArrayInputStream(this.parameters);
		JSONArray parameters = null;
		try {
		    ObjectInputStream is = new ObjectInputStream(in);
		    parameters = new JSONArray((List<Object>) is.readObject());
		}catch (Exception e) {
			log.error("getParameters(): An error occured while deserializing parameters from byte array: " + e.getMessage());
			StringBuilder builder = new StringBuilder();
			for (StackTraceElement element : e.getStackTrace()) {
				builder.append(element.toString() + "\n");
			}
			log.error(builder.toString());
		}
		return parameters;
	}

	public void setParameters(Object parameters) {
		log.debug("Called: setParameters(Object parameters)");
		try {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
		    ObjectOutputStream os = new ObjectOutputStream(out);
		    os.writeObject(parameters);
		    this.parameters = out.toByteArray();
		} catch (Exception e) {
			log.error("setParameters(Object processGraph): An error occured while serializing parameters to byte array: " + e.getMessage());
			StringBuilder builder = new StringBuilder();
			for (StackTraceElement element : e.getStackTrace()) {
				builder.append(element.toString() + "\n");
			}
			log.error(builder.toString());
		}
	}

	
	public Process deprecated(Boolean deprecated) {
		this.deprecated = deprecated;
		return this;
	}

	/**
	 * Specifies that the process or parameter is deprecated with the potential to
	 * be removed in any of the next versions. It should be transitioned out of
	 * usage as soon as possible and users should refrain from using it in new
	 * implementations. For processes a link with relation type `latest-version`
	 * SHOULD be added to the `links` and MUST refer to the process that can be used
	 * instead.
	 * 
	 * @return deprecated
	 */
	@ApiModelProperty(value = "Specifies that the process or parameter is deprecated with the potential to be removed in any of the next versions. It should be transitioned out of usage as soon as possible and users should refrain from using it in new implementations.  For processes a link with relation type `latest-version` SHOULD be added to the `links` and MUST refer to the process that can be used instead.")

	public Boolean isDeprecated() {
		return deprecated;
	}

	public void setDeprecated(Boolean deprecated) {
		this.deprecated = deprecated;
	}

	public Process experimental(Boolean experimental) {
		this.experimental = experimental;
		return this;
	}

	/**
	 * Declares the process or parameter to be experimental, which means that it is
	 * likely to change or may produce unpredictable behaviour. Users should refrain
	 * from using it in production, but still feel encouraged to try it out and give
	 * feedback.
	 * 
	 * @return experimental
	 */
	@ApiModelProperty(value = "Declares the process or parameter to be experimental, which means that it is likely to change or may produce unpredictable behaviour. Users should refrain from using it in production,  but still feel encouraged to try it out and give feedback.")

	public Boolean isExperimental() {
		return experimental;
	}

	public void setExperimental(Boolean experimental) {
		this.experimental = experimental;
	}

	public Process returns(Object returns) {
		this.returns = returns;
		return this;
	}

	/**
	 * Get returns
	 * 
	 * @return returns
	 */
	@ApiModelProperty(value = "")
	@JsonProperty("returns")
	@Valid

	public Object getReturns() {
		return new JSONObject((Map<String, Object>) this.returns);
	}

	public void setReturns(Object returns) {
		this.returns = returns;
	}
	
	public Process exceptions(Object exceptions) {
		this.exceptions = exceptions;
		return this;
	}

	/**
	 * Declares any exceptions (errors) that might occur during execution of this
	 * process. This list is just for informative purposes and may be incomplete.
	 * This list MUST only contain exceptions that stop the execution of a process
	 * and MUST NOT contain warnings, notices or debugging messages. The keys define
	 * the error code and MUST match the following pattern: `^\\w+$` This schema
	 * follows the schema of the general openEO error list (see errors.json).
	 * 
	 * @return exceptions
	 */
	@ApiModelProperty(value = "Declares any exceptions (errors) that might occur during execution of this process. This list is just for informative purposes and may be incomplete. This list MUST only contain exceptions that stop the execution of a process and MUST NOT contain warnings, notices or debugging messages.  The keys define the error code and MUST match the following pattern: `^\\w+$`  This schema follows the schema of the general openEO error list (see errors.json).")
	@JsonProperty("exceptions")
	public Object getExceptions() {
		return new JSONObject((Map<String, Object>) this.exceptions);
	}

	public void setExceptions(Object exceptions) {
		this.exceptions = exceptions;
	}

	public Process examples(List<ProcessExample> examples) {
		log.debug("Called: examples(Object examples)");
		try {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
		    ObjectOutputStream os = new ObjectOutputStream(out);
		    os.writeObject(examples);
		    this.examples = out.toByteArray();
		} catch (Exception e) {
			log.error("examples(Object examples): An error occured while deserializing examples from byte array: " + e.getMessage());
			StringBuilder builder = new StringBuilder();
			for (StackTraceElement element : e.getStackTrace()) {
				builder.append(element.toString() + "\n");
			}
			log.error(builder.toString());
		}
		return this;
	}


	/**
	 * Examples, may be used for unit tests.
	 * 
	 * @return examples
	 */
	@ApiModelProperty(value = "Examples, may be used for unit tests.")

	@Valid

	public Object getExamples() {
		log.debug("Called: getExamples(Object examples)");
		if(this.examples == null) return null;
		ByteArrayInputStream in = new ByteArrayInputStream(this.examples);
		JSONArray examples = null;
		try {
		    ObjectInputStream is = new ObjectInputStream(in);
		    examples = new JSONArray((List<Object>) is.readObject());
		}catch (Exception e) {
			log.error("getExamples(): An error occured while deserializing examples from byte array: " + e.getMessage());
			StringBuilder builder = new StringBuilder();
			for (StackTraceElement element : e.getStackTrace()) {
				builder.append(element.toString() + "\n");
			}
			log.error(builder.toString());
		}
		return examples;
	}

	public void setExamples(Object examples) {
		log.debug("Called: setExamples(Object parameters)");
		try {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
		    ObjectOutputStream os = new ObjectOutputStream(out);
		    os.writeObject(examples);
		    this.examples = out.toByteArray();
		} catch (Exception e) {
			log.error("setExamples(Object examples): An error occured while serializing examples to byte array: " + e.getMessage());
			StringBuilder builder = new StringBuilder();
			for (StackTraceElement element : e.getStackTrace()) {
				builder.append(element.toString() + "\n");
			}
			log.error(builder.toString());
		}
	}

	public Process links(List<Link> links) {
		this.links = links;
		return this;
	}

	public Process addLinksItem(Link linksItem) {
		if (this.links == null) {
			this.links = new ArrayList<>();
		}
		this.links.add(linksItem);
		return this;
	}

	/**
	 * Links related to this process, e.g. additional external documentation. It is
	 * RECOMMENDED to provide links with the following `rel` (relation) types: 1.
	 * `latest-version`: If a process has been marked as deprecated, a link should
	 * point to the preferred version of the process. The relation types
	 * `predecessor-version` (link to older version) and `successor-version` (link
	 * to newer version) can also be used to show the relation between versions. 2.
	 * `example`: Links to examples of other processes that use this process. 3.
	 * `cite-as`: For all DOIs associated with the process, the respective DOI links
	 * should be added. For additional relation types see also the lists of [common
	 * relation types in openEO](#section/API-Principles/Web-Linking).
	 * 
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

	public Process processGraph(Object processGraph) {
		log.debug("Called: processGraph(Object processGraph)");
		try {
			 ByteArrayOutputStream out = new ByteArrayOutputStream();
		    ObjectOutputStream os = new ObjectOutputStream(out);
		    os.writeObject(processGraph);
		    this.processGraph = out.toByteArray();
		} catch (Exception e) {
			log.error("processGraph(Object processGraph): An error occured while deserializing process graph from byte array: " + e.getMessage());
			StringBuilder builder = new StringBuilder();
			for (StackTraceElement element : e.getStackTrace()) {
				builder.append(element.toString() + "\n");
			}
			log.error(builder.toString());
		}
		return this;

	}

	/**
	 * A process graph defines a graph-like structure as a connected set of
	 * executable processes. Each key is a unique identifier (node ID) that is used
	 * to refer to the process in the graph.
	 * 
	 * @return processGraph
	 */
	@ApiModelProperty(example = "{\"dc\":{\"process_id\":\"load_collection\",\"arguments\":{\"id\":\"Sentinel-2\",\"spatial_extent\":{\"west\":16.1,\"east\":16.6,\"north\":48.6,\"south\":47.2},\"temporal_extent\":[\"2018-01-01\",\"2018-02-01\"]}},\"bands\":{\"process_id\":\"filter_bands\",\"description\":\"Filter and order the bands. The order is important for the following reduce operation.\",\"arguments\":{\"data\":{\"from_node\":\"dc\"},\"bands\":[\"B08\",\"B04\",\"B02\"]}},\"evi\":{\"process_id\":\"reduce\",\"description\":\"Compute the EVI. Formula: 2.5 * (NIR - RED) / (1 + NIR + 6*RED + -7.5*BLUE)\",\"arguments\":{\"data\":{\"from_node\":\"bands\"},\"dimension\":\"bands\",\"reducer\":{\"process_graph\":{\"nir\":{\"process_id\":\"array_element\",\"arguments\":{\"data\":{\"from_parameter\":\"data\"},\"index\":0}},\"red\":{\"process_id\":\"array_element\",\"arguments\":{\"data\":{\"from_parameter\":\"data\"},\"index\":1}},\"blue\":{\"process_id\":\"array_element\",\"arguments\":{\"data\":{\"from_parameter\":\"data\"},\"index\":2}},\"sub\":{\"process_id\":\"subtract\",\"arguments\":{\"data\":[{\"from_node\":\"nir\"},{\"from_node\":\"red\"}]}},\"p1\":{\"process_id\":\"product\",\"arguments\":{\"data\":[6,{\"from_node\":\"red\"}]}},\"p2\":{\"process_id\":\"product\",\"arguments\":{\"data\":[-7.5,{\"from_node\":\"blue\"}]}},\"sum\":{\"process_id\":\"sum\",\"arguments\":{\"data\":[1,{\"from_node\":\"nir\"},{\"from_node\":\"p1\"},{\"from_node\":\"p2\"}]}},\"div\":{\"process_id\":\"divide\",\"arguments\":{\"data\":[{\"from_node\":\"sub\"},{\"from_node\":\"sum\"}]}},\"p3\":{\"process_id\":\"product\",\"arguments\":{\"data\":[2.5,{\"from_node\":\"div\"}]},\"result\":true}}}}},\"mintime\":{\"process_id\":\"reduce\",\"description\":\"Compute a minimum time composite by reducing the temporal dimension\",\"arguments\":{\"data\":{\"from_node\":\"evi\"},\"dimension\":\"temporal\",\"reducer\":{\"process_graph\":{\"min\":{\"process_id\":\"min\",\"arguments\":{\"data\":{\"from_parameter\":\"data\"}},\"result\":true}}}}},\"save\":{\"process_id\":\"save_result\",\"arguments\":{\"data\":{\"from_node\":\"mintime\"},\"format\":\"GTiff\"},\"result\":true}}", value = "A process graph defines a graph-like structure as a connected set of executable processes. Each key is a unique identifier (node ID) that is used to refer to the process in the graph.")
	@JsonProperty("process_graph")
	public Object getProcessGraph() {
		log.debug("Called: getProcessGraph(Object processGraph)");
		ByteArrayInputStream in = new ByteArrayInputStream(this.processGraph);
		JSONObject processGraph = null;
		try {
		    ObjectInputStream is = new ObjectInputStream(in);
		    processGraph = new JSONObject((Map<String, Object>) is.readObject());
		}catch (Exception e) {
			log.error("getProcessGraph(): An error occured while deserializing process graph from byte array: " + e.getMessage());
			StringBuilder builder = new StringBuilder();
			for (StackTraceElement element : e.getStackTrace()) {
				builder.append(element.toString() + "\n");
			}
			log.error(builder.toString());
		}
		return processGraph;
	}

	public void setProcessGraph(Object processGraph) {
		log.debug("Called: setProcessGraph(Object processGraph)");
		try {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
		    ObjectOutputStream os = new ObjectOutputStream(out);
		    os.writeObject(processGraph);
		    this.processGraph = out.toByteArray();
		} catch (Exception e) {
			log.error("setProcessGraph(Object processGraph): An error occured while serializing process graph to byte array: " + e.getMessage());
			StringBuilder builder = new StringBuilder();
			for (StackTraceElement element : e.getStackTrace()) {
				builder.append(element.toString() + "\n");
			}
			log.error(builder.toString());
		}
	}

	@Override
	public boolean equals(java.lang.Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		Process process = (Process) o;
		return Objects.equals(this.id, process.id) && Objects.equals(this.summary, process.summary)
				&& Objects.equals(this.description, process.description)
				&& Objects.equals(this.categories, process.categories)
				&& Objects.equals(this.parameters, process.parameters) && Objects.equals(this.returns, process.returns)
				&& Objects.equals(this.deprecated, process.deprecated)
				&& Objects.equals(this.experimental, process.experimental)
				&& Objects.equals(this.exceptions, process.exceptions)
				&& Objects.equals(this.examples, process.examples) && Objects.equals(this.links, process.links)
				&& Objects.equals(this.processGraph, process.processGraph);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, summary, description, categories, parameters, returns, deprecated, experimental,
				exceptions, examples, links, processGraph);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("class Process {\n");

		//sb.append("    engine: ").append(toIndentedString(engine)).append("\n");
		sb.append("    id: ").append(toIndentedString(id)).append("\n");
		sb.append("    summary: ").append(toIndentedString(summary)).append("\n");
		sb.append("    description: ").append(toIndentedString(description)).append("\n");
		sb.append("    categories: ").append(toIndentedString(categories)).append("\n");
		sb.append("    parameters: ").append(toIndentedString(parameters)).append("\n");
		sb.append("    returns: ").append(toIndentedString(returns)).append("\n");
		sb.append("    deprecated: ").append(toIndentedString(deprecated)).append("\n");
		sb.append("    experimental: ").append(toIndentedString(experimental)).append("\n");
		sb.append("    exceptions: ").append(((JSONObject)this.getExceptions()).toString(4)).append("\n");
		sb.append("    examples: ").append(toIndentedString(examples)).append("\n");
		sb.append("    links: ").append(toIndentedString(links)).append("\n");
		sb.append("    processGraph: ").append(((JSONObject)this.getProcessGraph()).toString(4)).append("\n");
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
