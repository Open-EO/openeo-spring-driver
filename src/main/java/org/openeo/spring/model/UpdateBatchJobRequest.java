package org.openeo.spring.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.math.BigDecimal;
import org.openeo.spring.model.ProcessGraphWithMetadata;
import org.openapitools.jackson.nullable.JsonNullable;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * UpdateBatchJobRequest
 */
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2020-06-30T15:12:47.411+02:00[Europe/Rome]")
public class UpdateBatchJobRequest   {
  @JsonProperty("title")
  private JsonNullable<String> title = JsonNullable.undefined();

  @JsonProperty("description")
  private JsonNullable<String> description = JsonNullable.undefined();

  @JsonProperty("process")
  private ProcessGraphWithMetadata process = null;

  @JsonProperty("plan")
  private JsonNullable<String> plan = JsonNullable.undefined();

  @JsonProperty("budget")
  private JsonNullable<BigDecimal> budget = JsonNullable.undefined();

  public UpdateBatchJobRequest title(String title) {
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

  public UpdateBatchJobRequest description(String description) {
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

  public UpdateBatchJobRequest process(ProcessGraphWithMetadata process) {
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

  public UpdateBatchJobRequest plan(String plan) {
    this.plan = JsonNullable.of(plan);
    return this;
  }

  /**
   * The billing plan to process and charge the job with.  The plans and the default plan can be retrieved by calling `GET /`.  Billing plans MUST be accepted *case insensitive*. Billing plans not on the list of available plans MUST be rejected with openEO error `BillingPlanInvalid`.  If no billing plan is specified by the client, the server MUST default to the default billing plan in `GET /`. If the default billing plan of the provider changes, the job or service MUST not be affected by the change, i.e. the default plan which is valid during job or service creation must be permanently assigned to the job or service until the client requests to change it.
   * @return plan
  */
  @ApiModelProperty(example = "free", value = "The billing plan to process and charge the job with.  The plans and the default plan can be retrieved by calling `GET /`.  Billing plans MUST be accepted *case insensitive*. Billing plans not on the list of available plans MUST be rejected with openEO error `BillingPlanInvalid`.  If no billing plan is specified by the client, the server MUST default to the default billing plan in `GET /`. If the default billing plan of the provider changes, the job or service MUST not be affected by the change, i.e. the default plan which is valid during job or service creation must be permanently assigned to the job or service until the client requests to change it.")


  public JsonNullable<String> getPlan() {
    return plan;
  }

  public void setPlan(JsonNullable<String> plan) {
    this.plan = plan;
  }

  public UpdateBatchJobRequest budget(BigDecimal budget) {
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
    UpdateBatchJobRequest updateBatchJobRequest = (UpdateBatchJobRequest) o;
    return Objects.equals(this.title, updateBatchJobRequest.title) &&
        Objects.equals(this.description, updateBatchJobRequest.description) &&
        Objects.equals(this.process, updateBatchJobRequest.process) &&
        Objects.equals(this.plan, updateBatchJobRequest.plan) &&
        Objects.equals(this.budget, updateBatchJobRequest.budget);
  }

  @Override
  public int hashCode() {
    return Objects.hash(title, description, process, plan, budget);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class UpdateBatchJobRequest {\n");
    
    sb.append("    title: ").append(toIndentedString(title)).append("\n");
    sb.append("    description: ").append(toIndentedString(description)).append("\n");
    sb.append("    process: ").append(toIndentedString(process)).append("\n");
    sb.append("    plan: ").append(toIndentedString(plan)).append("\n");
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

