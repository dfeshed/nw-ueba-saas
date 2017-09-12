package presidio.webapp.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * EventsWrapper
 */
@javax.annotation.Generated(value = "class io.swagger.codegen.languages.SpringCodegen", date = "2017-09-11T11:15:54.772Z")

public class EventsWrapper   {
  @JsonProperty("events")
  private List<Event> events = new ArrayList<Event>();

  @JsonProperty("total")
  private Integer total = null;

  @JsonProperty("page")
  private Integer page = null;

  public EventsWrapper events(List<Event> events) {
    this.events = events;
    return this;
  }

  public EventsWrapper addEventsItem(Event eventsItem) {
    this.events.add(eventsItem);
    return this;
  }

  /**
   * Get events
   * @return events
   **/
  @ApiModelProperty(value = "")
  public List<Event> getEvents() {
    return events;
  }

  public void setEvents(List<Event> events) {
    this.events = events;
  }

  public EventsWrapper total(Integer total) {
    this.total = total;
    return this;
  }

  /**
   * Get total
   * minimum: 0
   * @return total
   **/
  @ApiModelProperty(value = "")
  public Integer getTotal() {
    return total;
  }

  public void setTotal(Integer total) {
    this.total = total;
  }

  public EventsWrapper page(Integer page) {
    this.page = page;
    return this;
  }

  /**
   * Get page
   * minimum: 0
   * @return page
   **/
  @ApiModelProperty(value = "")
  public Integer getPage() {
    return page;
  }

  public void setPage(Integer page) {
    this.page = page;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    EventsWrapper eventsWrapper = (EventsWrapper) o;
    return Objects.equals(this.events, eventsWrapper.events) &&
            Objects.equals(this.total, eventsWrapper.total) &&
            Objects.equals(this.page, eventsWrapper.page);
  }

  @Override
  public int hashCode() {
    return Objects.hash(events, total, page);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class EventsWrapper {\n");

    sb.append("    events: ").append(toIndentedString(events)).append("\n");
    sb.append("    total: ").append(toIndentedString(total)).append("\n");
    sb.append("    page: ").append(toIndentedString(page)).append("\n");
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

