package fortscale.common.SDK;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import org.joda.time.DateTime;

import java.util.Objects;

/**
 * PipelineStateDataProcessingCursor
 */
@javax.annotation.Generated(value = "class io.swagger.codegen.languages.SpringCodegen", date = "2017-08-07T07:15:37.402Z")

public class PipelineStateDataProcessingCursor   {
  @JsonProperty("from")
  private DateTime from = null;

  @JsonProperty("to")
  private DateTime to = null;

  public PipelineStateDataProcessingCursor from(DateTime from) {
    this.from = from;
    return this;
  }

   /**
   * Get from
   * @return from
  **/
  @ApiModelProperty(value = "")
  public DateTime getFrom() {
    return from;
  }

  public void setFrom(DateTime from) {
    this.from = from;
  }

  public PipelineStateDataProcessingCursor to(DateTime to) {
    this.to = to;
    return this;
  }

   /**
   * Get to
   * @return to
  **/
  @ApiModelProperty(value = "")
  public DateTime getTo() {
    return to;
  }

  public void setTo(DateTime to) {
    this.to = to;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    PipelineStateDataProcessingCursor pipelineStateDataProcessingCursor = (PipelineStateDataProcessingCursor) o;
    return Objects.equals(this.from, pipelineStateDataProcessingCursor.from) &&
        Objects.equals(this.to, pipelineStateDataProcessingCursor.to);
  }

  @Override
  public int hashCode() {
    return Objects.hash(from, to);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class PipelineStateDataProcessingCursor {\n");

    sb.append("    from: ").append(toIndentedString(from)).append("\n");
    sb.append("    to: ").append(toIndentedString(to)).append("\n");
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

