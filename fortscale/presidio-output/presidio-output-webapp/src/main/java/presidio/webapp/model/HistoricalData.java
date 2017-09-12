package presidio.webapp.model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.util.Objects;

/**
 * HistoricalData
 */
@javax.annotation.Generated(value = "class io.swagger.codegen.languages.SpringCodegen", date = "2017-09-11T09:31:06.391Z")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = HistoricalDataCountAggregation.class, name = "COUNT_AGGRAGEATION"),
        @JsonSubTypes.Type(value = HistoricalDataTimeAggregation.class, name = "TIME_AGGREGATION"),
        @JsonSubTypes.Type(value = HistoricalDataWeekdayAggregation.class, name = "WEEKDAY_AGGREGATION")
})
public class HistoricalData   {

  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    return Objects.hash();
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class HistoricalData {\n");
    
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

