package org.openeo.spring.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.openapitools.jackson.nullable.JsonNullable;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * Information about the storage space available to the user.
 */
@ApiModel(description = "Information about the storage space available to the user.")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2020-06-30T14:48:14.663+02:00[Europe/Rome]")
public class UserStorage   {
  @JsonProperty("free")
  private Integer free;

  @JsonProperty("quota")
  private Integer quota;

  public UserStorage free(Integer free) {
    this.free = free;
    return this;
  }

  /**
   * Free storage space in bytes, which is still available to the user. Effectively, this is the disk quota minus the used space by the user, e.g. user-uploaded files and job results.
   * @return free
  */
  @ApiModelProperty(example = "536870912", required = true, value = "Free storage space in bytes, which is still available to the user. Effectively, this is the disk quota minus the used space by the user, e.g. user-uploaded files and job results.")
  @NotNull


  public Integer getFree() {
    return free;
  }

  public void setFree(Integer free) {
    this.free = free;
  }

  public UserStorage quota(Integer quota) {
    this.quota = quota;
    return this;
  }

  /**
   * Maximum storage space (disk quota) in bytes available to the user.
   * @return quota
  */
  @ApiModelProperty(example = "1073741824", required = true, value = "Maximum storage space (disk quota) in bytes available to the user.")
  @NotNull


  public Integer getQuota() {
    return quota;
  }

  public void setQuota(Integer quota) {
    this.quota = quota;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    UserStorage userStorage = (UserStorage) o;
    return Objects.equals(this.free, userStorage.free) &&
        Objects.equals(this.quota, userStorage.quota);
  }

  @Override
  public int hashCode() {
    return Objects.hash(free, quota);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class UserStorage {\n");
    
    sb.append("    free: ").append(toIndentedString(free)).append("\n");
    sb.append("    quota: ").append(toIndentedString(quota)).append("\n");
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

