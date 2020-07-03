package org.openeo.spring.model;

import java.math.BigDecimal;
import java.util.Objects;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.openapitools.jackson.nullable.JsonNullable;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModelProperty;

/**
 * SynchronousResultRequest
 */
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2020-07-02T08:45:00.334+02:00[Europe/Rome]")
public class SynchronousResultRequest   {
  @JsonProperty("process")
  private ProcessGraphWithMetadata process;

  @JsonProperty("budget")
  private JsonNullable<BigDecimal> budget = JsonNullable.undefined();

  @JsonProperty("plan")
  private JsonNullable<String> plan = JsonNullable.undefined();

  public SynchronousResultRequest process(ProcessGraphWithMetadata process) {
    this.process = process;
    return this;
  }

  /**
   * Get process
   * @return process
  */
  @ApiModelProperty(required = true, value = "")
  @NotNull

  @Valid

  public ProcessGraphWithMetadata getProcess() {
    return process;
  }

  public void setProcess(ProcessGraphWithMetadata process) {
    this.process = process;
  }

  public SynchronousResultRequest budget(BigDecimal budget) {
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

  public SynchronousResultRequest plan(String plan) {
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


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    SynchronousResultRequest synchronousResultRequest = (SynchronousResultRequest) o;
    return Objects.equals(this.process, synchronousResultRequest.process) &&
        Objects.equals(this.budget, synchronousResultRequest.budget) &&
        Objects.equals(this.plan, synchronousResultRequest.plan);
  }

  @Override
  public int hashCode() {
    return Objects.hash(process, budget, plan);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class SynchronousResultRequest {\n");
    
    sb.append("    process: ").append(toIndentedString(process)).append("\n");
    sb.append("    budget: ").append(toIndentedString(budget)).append("\n");
    sb.append("    plan: ").append(toIndentedString(plan)).append("\n");
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

