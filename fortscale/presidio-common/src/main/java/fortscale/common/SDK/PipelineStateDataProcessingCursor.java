package fortscale.common.SDK;

import com.fasterxml.jackson.annotation.JsonProperty;
import fortscale.utils.time.TimeRange;
import io.swagger.annotations.ApiModelProperty;
import org.joda.time.DateTime;

import java.time.Instant;
import java.util.Objects;

/**
 * PipelineStateDataProcessingCursor
 */
@javax.annotation.Generated(value = "class io.swagger.codegen.languages.SpringCodegen", date = "2017-08-07T07:15:37.402Z")

public class PipelineStateDataProcessingCursor   {
  @JsonProperty("from")
  private Instant from = null;

  @JsonProperty("to")
  private Instant to = null;

  public PipelineStateDataProcessingCursor() {
  }

  public PipelineStateDataProcessingCursor(Instant from, Instant to) {
    this.from = from;
    this.to = to;
  }

  public PipelineStateDataProcessingCursor(TimeRange timeRange) {
    this(timeRange.getStart(),timeRange.getEnd());
  }

  public PipelineStateDataProcessingCursor from(Instant from) {
    this.from = from;
    return this;
  }

   /**
   * Get from
   * @return from
  **/
  @ApiModelProperty(value = "")
  public Instant getFrom() {
    return from;
  }

  public void setFrom(Instant from) {
    this.from = from;
  }

  public PipelineStateDataProcessingCursor to(Instant to) {
    this.to = to;
    return this;
  }

   /**
   * Get to
   * @return to
  **/
  @ApiModelProperty(value = "")
  public Instant getTo() {
    return to;
  }

  public void setTo(Instant to) {
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

