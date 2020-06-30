package org.openeo.spring.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import org.openapitools.jackson.nullable.JsonNullable;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * BatchJobEstimateResponse
 */
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2020-06-30T15:12:47.411+02:00[Europe/Rome]")
public class BatchJobEstimateResponse   {
  @JsonProperty("costs")
  private JsonNullable<BigDecimal> costs = JsonNullable.undefined();

  @JsonProperty("duration")
  private String duration;

  @JsonProperty("size")
  private Integer size;

  @JsonProperty("downloads_included")
  private JsonNullable<Integer> downloadsIncluded = JsonNullable.undefined();

  @JsonProperty("expires")
  @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE_TIME)
  private OffsetDateTime expires;

  public BatchJobEstimateResponse costs(BigDecimal costs) {
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

  public BatchJobEstimateResponse duration(String duration) {
    this.duration = duration;
    return this;
  }

  /**
   * Estimated duration for the operation. Duration MUST be specified as a ISO 8601 duration.
   * @return duration
  */
  @ApiModelProperty(example = "P1Y2M10DT2H30M", value = "Estimated duration for the operation. Duration MUST be specified as a ISO 8601 duration.")


  public String getDuration() {
    return duration;
  }

  public void setDuration(String duration) {
    this.duration = duration;
  }

  public BatchJobEstimateResponse size(Integer size) {
    this.size = size;
    return this;
  }

  /**
   * Estimated required storage capacity, i.e. the size of the generated files. Size MUST be specified in bytes.
   * @return size
  */
  @ApiModelProperty(example = "157286400", value = "Estimated required storage capacity, i.e. the size of the generated files. Size MUST be specified in bytes.")


  public Integer getSize() {
    return size;
  }

  public void setSize(Integer size) {
    this.size = size;
  }

  public BatchJobEstimateResponse downloadsIncluded(Integer downloadsIncluded) {
    this.downloadsIncluded = JsonNullable.of(downloadsIncluded);
    return this;
  }

  /**
   * Specifies how many full downloads of the processed data are included in the estimate. Set to `null` for unlimited downloads, which is also the default value.
   * @return downloadsIncluded
  */
  @ApiModelProperty(example = "5", value = "Specifies how many full downloads of the processed data are included in the estimate. Set to `null` for unlimited downloads, which is also the default value.")


  public JsonNullable<Integer> getDownloadsIncluded() {
    return downloadsIncluded;
  }

  public void setDownloadsIncluded(JsonNullable<Integer> downloadsIncluded) {
    this.downloadsIncluded = downloadsIncluded;
  }

  public BatchJobEstimateResponse expires(OffsetDateTime expires) {
    this.expires = expires;
    return this;
  }

  /**
   * Time until which the estimate is valid, formatted as a [RFC 3339](https://www.ietf.org/rfc/rfc3339) date-time.
   * @return expires
  */
  @ApiModelProperty(example = "2020-11-01T00:00Z", value = "Time until which the estimate is valid, formatted as a [RFC 3339](https://www.ietf.org/rfc/rfc3339) date-time.")

  @Valid

  public OffsetDateTime getExpires() {
    return expires;
  }

  public void setExpires(OffsetDateTime expires) {
    this.expires = expires;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    BatchJobEstimateResponse batchJobEstimateResponse = (BatchJobEstimateResponse) o;
    return Objects.equals(this.costs, batchJobEstimateResponse.costs) &&
        Objects.equals(this.duration, batchJobEstimateResponse.duration) &&
        Objects.equals(this.size, batchJobEstimateResponse.size) &&
        Objects.equals(this.downloadsIncluded, batchJobEstimateResponse.downloadsIncluded) &&
        Objects.equals(this.expires, batchJobEstimateResponse.expires);
  }

  @Override
  public int hashCode() {
    return Objects.hash(costs, duration, size, downloadsIncluded, expires);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class BatchJobEstimateResponse {\n");
    
    sb.append("    costs: ").append(toIndentedString(costs)).append("\n");
    sb.append("    duration: ").append(toIndentedString(duration)).append("\n");
    sb.append("    size: ").append(toIndentedString(size)).append("\n");
    sb.append("    downloadsIncluded: ").append(toIndentedString(downloadsIncluded)).append("\n");
    sb.append("    expires: ").append(toIndentedString(expires)).append("\n");
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

