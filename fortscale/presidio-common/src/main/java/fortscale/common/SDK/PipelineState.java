package fortscale.common.SDK;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.annotations.ApiModelProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * PipelineState
 */
@javax.annotation.Generated(value = "class io.swagger.codegen.languages.SpringCodegen", date = "2017-08-07T07:15:37.402Z")

public class PipelineState   {
  /**
   * the current system state
   */
  public enum StatusEnum {
    RUNNING("RUNNING"),
    
    CLEANING("CLEANING"),
    
    STOPPING("STOPPING"),

    BUILDING_BASELINE("BUILDING_BASELINE"),
    
    STOPPED("STOPPED");

    private String value;

    StatusEnum(String value) {
      this.value = value;
    }

    @Override
    @JsonValue
    public String toString() {
      return String.valueOf(value);
    }

    @JsonCreator
    public static StatusEnum fromValue(String text) {
      for (StatusEnum b : StatusEnum.values()) {
        if (String.valueOf(b.value).equals(text)) {
          return b;
        }
      }
      return null;
    }
  }

  @JsonProperty("status")
  private StatusEnum status = null;

  @JsonProperty("dataProcessingCursor")
  private List<PipelineStateDataProcessingCursor> dataProcessingCursor = new ArrayList<PipelineStateDataProcessingCursor>();

  public PipelineState status(StatusEnum status) {
    this.status = status;
    return this;
  }

   /**
   * the current system state
   * @return status
  **/
  @ApiModelProperty(required = true, value = "the current system state")
  public StatusEnum getStatus() {
    return status;
  }

  public void setStatus(StatusEnum status) {
    this.status = status;
  }

  public PipelineState dataProcessingCursor(List<PipelineStateDataProcessingCursor> dataProcessingCursor) {
    this.dataProcessingCursor = dataProcessingCursor;
    return this;
  }

  public PipelineState addDataProcessingCursorItem(PipelineStateDataProcessingCursor dataProcessingCursorItem) {
    this.dataProcessingCursor.add(dataProcessingCursorItem);
    return this;
  }

   /**
   * time frames having current status of \"running\" in the workflow
   * @return dataProcessingCursor
  **/
  @ApiModelProperty(value = "time frames having current status of \"running\" in the workflow")
  public List<PipelineStateDataProcessingCursor> getDataProcessingCursor() {
    return dataProcessingCursor;
  }

  public void setDataProcessingCursor(List<PipelineStateDataProcessingCursor> dataProcessingCursor) {
    this.dataProcessingCursor = dataProcessingCursor;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    PipelineState pipelineState = (PipelineState) o;
    return Objects.equals(this.status, pipelineState.status) &&
        Objects.equals(this.dataProcessingCursor, pipelineState.dataProcessingCursor);
  }

  @Override
  public int hashCode() {
    return Objects.hash(status, dataProcessingCursor);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class PipelineState {\n");

    sb.append("    status: ").append(toIndentedString(status)).append("\n");
    sb.append("    dataProcessingCursor: ").append(toIndentedString(dataProcessingCursor)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}

