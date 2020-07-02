package org.openeo.spring.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import org.openeo.spring.model.ProcessGraphWithMetadata;
import org.openapitools.jackson.nullable.JsonNullable;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * The metadata of a batch jobs that has been submitted by the authenticated user.
 */
@ApiModel(description = "The metadata of a batch jobs that has been submitted by the authenticated user.")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2020-07-02T08:45:00.334+02:00[Europe/Rome]")
public class BatchJob   {
  @JsonProperty("id")
  private String id;

  @JsonProperty("title")
  private JsonNullable<String> title = JsonNullable.undefined();

  @JsonProperty("description")
  private JsonNullable<String> description = JsonNullable.undefined();

  @JsonProperty("process")
  private ProcessGraphWithMetadata process;

  /**
   * The current status of a batch job.  The following status changes can occur: * `POST /jobs`: The status is initialized as `created`. * `POST /jobs/{job_id}/results`: The status is set to `queued`, if processing doesn't start instantly.     * Once the processing starts the status is set to `running`.     * Once the data is available to download the status is set to `finished`.     * Whenever an error occurs during processing, the status must be set to `error`. * `DELETE /jobs/{job_id}/results`: The status is set to `canceled` if the status was `running` beforehand and partial or preliminary results are available to be downloaded. Otherwise the status is set to `created`. 
   */
  public enum StatusEnum {
    CREATED("created"),
    
    QUEUED("queued"),
    
    RUNNING("running"),
    
    CANCELED("canceled"),
    
    FINISHED("finished"),
    
    ERROR("error");

    private String value;

    StatusEnum(String value) {
      this.value = value;
    }

    @JsonValue
    public String getValue() {
      return value;
    }

    @Override
    public String toString() {
      return String.valueOf(value);
    }

    @JsonCreator
    public static StatusEnum fromValue(String value) {
      for (StatusEnum b : StatusEnum.values()) {
        if (b.value.equals(value)) {
          return b;
        }
      }
      throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }
  }

  @JsonProperty("status")
  private StatusEnum status = StatusEnum.CREATED;

  @JsonProperty("progress")
  private BigDecimal progress;

  @JsonProperty("created")
  @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE_TIME)
  private OffsetDateTime created;

  @JsonProperty("updated")
  @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE_TIME)
  private OffsetDateTime updated;

  @JsonProperty("plan")
  private String plan;

  @JsonProperty("costs")
  private JsonNullable<BigDecimal> costs = JsonNullable.undefined();

  @JsonProperty("budget")
  private JsonNullable<BigDecimal> budget = JsonNullable.undefined();

  public BatchJob id(String id) {
    this.id = id;
    return this;
  }

  /**
   * Unique identifier of the batch job, generated by the back-end during creation. MUST match the specified pattern.
   * @return id
  */
  @ApiModelProperty(example = "a3cca2b2aa1e3b5b", required = true, value = "Unique identifier of the batch job, generated by the back-end during creation. MUST match the specified pattern.")
  @NotNull

@Pattern(regexp="^[\\w\\-\\.~]+$") 
  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public BatchJob title(String title) {
    this.title = JsonNullable.of(title);
    return this;
  }

  /**
   * A short description to easily distinguish entities.
   * @return title
  */
  @ApiModelProperty(example = "NDVI based on Sentinel 2", value = "A short description to easily distinguish entities.")


  public JsonNullable<String> getTitle() {
    return title;
  }

  public void setTitle(JsonNullable<String> title) {
    this.title = title;
  }

  public BatchJob description(String description) {
    this.description = JsonNullable.of(description);
    return this;
  }

  /**
   * Detailed multi-line description to explain the entity.  [CommonMark 0.29](http://commonmark.org/) syntax MAY be used for rich text representation.
   * @return description
  */
  @ApiModelProperty(example = "Deriving minimum NDVI measurements over pixel time series of Sentinel 2", value = "Detailed multi-line description to explain the entity.  [CommonMark 0.29](http://commonmark.org/) syntax MAY be used for rich text representation.")


  public JsonNullable<String> getDescription() {
    return description;
  }

  public void setDescription(JsonNullable<String> description) {
    this.description = description;
  }

  public BatchJob process(ProcessGraphWithMetadata process) {
    this.process = process;
    return this;
  }

  /**
   * Get process
   * @return process
  */
  @ApiModelProperty(value = "")

  @Valid

  public ProcessGraphWithMetadata getProcess() {
    return process;
  }

  public void setProcess(ProcessGraphWithMetadata process) {
    this.process = process;
  }

  public BatchJob status(StatusEnum status) {
    this.status = status;
    return this;
  }

  /**
   * The current status of a batch job.  The following status changes can occur: * `POST /jobs`: The status is initialized as `created`. * `POST /jobs/{job_id}/results`: The status is set to `queued`, if processing doesn't start instantly.     * Once the processing starts the status is set to `running`.     * Once the data is available to download the status is set to `finished`.     * Whenever an error occurs during processing, the status must be set to `error`. * `DELETE /jobs/{job_id}/results`: The status is set to `canceled` if the status was `running` beforehand and partial or preliminary results are available to be downloaded. Otherwise the status is set to `created`. 
   * @return status
  */
  @ApiModelProperty(example = "running", required = true, value = "The current status of a batch job.  The following status changes can occur: * `POST /jobs`: The status is initialized as `created`. * `POST /jobs/{job_id}/results`: The status is set to `queued`, if processing doesn't start instantly.     * Once the processing starts the status is set to `running`.     * Once the data is available to download the status is set to `finished`.     * Whenever an error occurs during processing, the status must be set to `error`. * `DELETE /jobs/{job_id}/results`: The status is set to `canceled` if the status was `running` beforehand and partial or preliminary results are available to be downloaded. Otherwise the status is set to `created`. ")
  @NotNull


  public StatusEnum getStatus() {
    return status;
  }

  public void setStatus(StatusEnum status) {
    this.status = status;
  }

  public BatchJob progress(BigDecimal progress) {
    this.progress = progress;
    return this;
  }

  /**
   * Indicates the process of a running batch job in percent. Can also be set for a job which stopped due to an error or was canceled by the user. In this case, the value indicates the progress at which the job stopped. Property may not be available for the status codes `created` and `queued`. Submitted and queued jobs only allow the value `0`, finished jobs only allow the value `100`.
   * minimum: 0
   * maximum: 100
   * @return progress
  */
  @ApiModelProperty(example = "75.5", value = "Indicates the process of a running batch job in percent. Can also be set for a job which stopped due to an error or was canceled by the user. In this case, the value indicates the progress at which the job stopped. Property may not be available for the status codes `created` and `queued`. Submitted and queued jobs only allow the value `0`, finished jobs only allow the value `100`.")

  @Valid
@DecimalMin("0") @DecimalMax("100") 
  public BigDecimal getProgress() {
    return progress;
  }

  public void setProgress(BigDecimal progress) {
    this.progress = progress;
  }

  public BatchJob created(OffsetDateTime created) {
    this.created = created;
    return this;
  }

  /**
   * Date and time of creation, formatted as a [RFC 3339](https://www.ietf.org/rfc/rfc3339) date-time.
   * @return created
  */
  @ApiModelProperty(example = "2017-01-01T09:32:12Z", required = true, value = "Date and time of creation, formatted as a [RFC 3339](https://www.ietf.org/rfc/rfc3339) date-time.")
  @NotNull

  @Valid

  public OffsetDateTime getCreated() {
    return created;
  }

  public void setCreated(OffsetDateTime created) {
    this.created = created;
  }

  public BatchJob updated(OffsetDateTime updated) {
    this.updated = updated;
    return this;
  }

  /**
   * Date and time of last status change, formatted as a [RFC 3339](https://www.ietf.org/rfc/rfc3339) date-time.
   * @return updated
  */
  @ApiModelProperty(example = "2017-01-01T09:36:18Z", value = "Date and time of last status change, formatted as a [RFC 3339](https://www.ietf.org/rfc/rfc3339) date-time.")

  @Valid

  public OffsetDateTime getUpdated() {
    return updated;
  }

  public void setUpdated(OffsetDateTime updated) {
    this.updated = updated;
  }

  public BatchJob plan(String plan) {
    this.plan = plan;
    return this;
  }

  /**
   * The billing plan to process and charge the job with. The plans can be retrieved by calling `GET /`. Billing plans MUST be accepted *case insensitive*.
   * @return plan
  */
  @ApiModelProperty(example = "free", value = "The billing plan to process and charge the job with. The plans can be retrieved by calling `GET /`. Billing plans MUST be accepted *case insensitive*.")


  public String getPlan() {
    return plan;
  }

  public void setPlan(String plan) {
    this.plan = plan;
  }

  public BatchJob costs(BigDecimal costs) {
    this.costs = JsonNullable.of(costs);
    return this;
  }

  /**
   * An amount of money or credits. The value MUST be specified in the currency the back-end is working with. The currency can be retrieved by calling `GET /`. If no currency is set, this field MUST be `null`.
   * @return costs
  */
  @ApiModelProperty(example = "12.98", value = "An amount of money or credits. The value MUST be specified in the currency the back-end is working with. The currency can be retrieved by calling `GET /`. If no currency is set, this field MUST be `null`.")

  @Valid

  public JsonNullable<BigDecimal> getCosts() {
    return costs;
  }

  public void setCosts(JsonNullable<BigDecimal> costs) {
    this.costs = costs;
  }

  public BatchJob budget(BigDecimal budget) {
    this.budget = JsonNullable.of(budget);
    return this;
  }

  /**
   * Maximum amount of costs the request is allowed to produce. The value MUST be specified in the currency the back-end is working with. The currency can be retrieved by calling `GET /`. If no currency is set, this field MUST NOT be a number.   If possible, back-ends SHOULD reject jobs with openEO error `PaymentRequired` if the budget is too low to process the request completely. Otherwise, when reaching the budget jobs MAY try to return partial results if possible. Otherwise the request and results are discarded. Users SHOULD be warned by clients that reaching the budget MAY discard the results and that setting this value should be well-wrought.   Setting the budget to `null` means there is no specified budget.
   * @return budget
  */
  @ApiModelProperty(example = "100", value = "Maximum amount of costs the request is allowed to produce. The value MUST be specified in the currency the back-end is working with. The currency can be retrieved by calling `GET /`. If no currency is set, this field MUST NOT be a number.   If possible, back-ends SHOULD reject jobs with openEO error `PaymentRequired` if the budget is too low to process the request completely. Otherwise, when reaching the budget jobs MAY try to return partial results if possible. Otherwise the request and results are discarded. Users SHOULD be warned by clients that reaching the budget MAY discard the results and that setting this value should be well-wrought.   Setting the budget to `null` means there is no specified budget.")

  @Valid

  public JsonNullable<BigDecimal> getBudget() {
    return budget;
  }

  public void setBudget(JsonNullable<BigDecimal> budget) {
    this.budget = budget;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    BatchJob batchJob = (BatchJob) o;
    return Objects.equals(this.id, batchJob.id) &&
        Objects.equals(this.title, batchJob.title) &&
        Objects.equals(this.description, batchJob.description) &&
        Objects.equals(this.process, batchJob.process) &&
        Objects.equals(this.status, batchJob.status) &&
        Objects.equals(this.progress, batchJob.progress) &&
        Objects.equals(this.created, batchJob.created) &&
        Objects.equals(this.updated, batchJob.updated) &&
        Objects.equals(this.plan, batchJob.plan) &&
        Objects.equals(this.costs, batchJob.costs) &&
        Objects.equals(this.budget, batchJob.budget);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, title, description, process, status, progress, created, updated, plan, costs, budget);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class BatchJob {\n");
    
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    title: ").append(toIndentedString(title)).append("\n");
    sb.append("    description: ").append(toIndentedString(description)).append("\n");
    sb.append("    process: ").append(toIndentedString(process)).append("\n");
    sb.append("    status: ").append(toIndentedString(status)).append("\n");
    sb.append("    progress: ").append(toIndentedString(progress)).append("\n");
    sb.append("    created: ").append(toIndentedString(created)).append("\n");
    sb.append("    updated: ").append(toIndentedString(updated)).append("\n");
    sb.append("    plan: ").append(toIndentedString(plan)).append("\n");
    sb.append("    costs: ").append(toIndentedString(costs)).append("\n");
    sb.append("    budget: ").append(toIndentedString(budget)).append("\n");
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

