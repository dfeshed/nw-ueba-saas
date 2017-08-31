package presidio.webapp.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;

import java.util.Objects;

/**
 * InlineResponse200
 */
@javax.annotation.Generated(value = "class io.swagger.codegen.languages.SpringCodegen", date = "2017-08-31T11:27:51.258Z")

public class InlineResponse200 {
    @JsonProperty("eventId")
    private String eventId = null;

    @JsonProperty("type")
    private String type = null;

    @JsonProperty("startTime")
    private Integer startTime = null;

    @JsonProperty("endTime")
    private Integer endTime = null;

    @JsonProperty("username")
    private String username = null;

    @JsonProperty("additionalFieldsMap")
    private Object additionalFieldsMap = null;

    public InlineResponse200 eventId(String eventId) {
        this.eventId = eventId;
        return this;
    }

    /**
     * Get eventId
     *
     * @return eventId
     **/
    @ApiModelProperty(required = true, value = "")
    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public InlineResponse200 type(String type) {
        this.type = type;
        return this;
    }

    /**
     * Data Source Name
     *
     * @return type
     **/
    @ApiModelProperty(required = true, value = "Data Source Name")
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public InlineResponse200 startTime(Integer startTime) {
        this.startTime = startTime;
        return this;
    }

    /**
     * Get startTime
     *
     * @return startTime
     **/
    @ApiModelProperty(required = true, value = "")
    public Integer getStartTime() {
        return startTime;
    }

    public void setStartTime(Integer startTime) {
        this.startTime = startTime;
    }

    public InlineResponse200 endTime(Integer endTime) {
        this.endTime = endTime;
        return this;
    }

    /**
     * Get endTime
     *
     * @return endTime
     **/
    @ApiModelProperty(required = true, value = "")
    public Integer getEndTime() {
        return endTime;
    }

    public void setEndTime(Integer endTime) {
        this.endTime = endTime;
    }

    public InlineResponse200 username(String username) {
        this.username = username;
        return this;
    }

    /**
     * Get username
     *
     * @return username
     **/
    @ApiModelProperty(required = true, value = "")
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public InlineResponse200 additionalFieldsMap(Object additionalFieldsMap) {
        this.additionalFieldsMap = additionalFieldsMap;
        return this;
    }

    /**
     * Get additionalFieldsMap
     *
     * @return additionalFieldsMap
     **/
    @ApiModelProperty(value = "")
    public Object getAdditionalFieldsMap() {
        return additionalFieldsMap;
    }

    public void setAdditionalFieldsMap(Object additionalFieldsMap) {
        this.additionalFieldsMap = additionalFieldsMap;
    }


    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        InlineResponse200 inlineResponse200 = (InlineResponse200) o;
        return Objects.equals(this.eventId, inlineResponse200.eventId) &&
                Objects.equals(this.type, inlineResponse200.type) &&
                Objects.equals(this.startTime, inlineResponse200.startTime) &&
                Objects.equals(this.endTime, inlineResponse200.endTime) &&
                Objects.equals(this.username, inlineResponse200.username) &&
                Objects.equals(this.additionalFieldsMap, inlineResponse200.additionalFieldsMap);
    }

    @Override
    public int hashCode() {
        return Objects.hash(eventId, type, startTime, endTime, username, additionalFieldsMap);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class InlineResponse200 {\n");

        sb.append("    eventId: ").append(toIndentedString(eventId)).append("\n");
        sb.append("    type: ").append(toIndentedString(type)).append("\n");
        sb.append("    startTime: ").append(toIndentedString(startTime)).append("\n");
        sb.append("    endTime: ").append(toIndentedString(endTime)).append("\n");
        sb.append("    username: ").append(toIndentedString(username)).append("\n");
        sb.append("    additionalFieldsMap: ").append(toIndentedString(additionalFieldsMap)).append("\n");
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

