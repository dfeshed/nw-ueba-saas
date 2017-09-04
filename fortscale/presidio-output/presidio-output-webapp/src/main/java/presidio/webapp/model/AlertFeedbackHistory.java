package presidio.webapp.model;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * AlertFeedbackHistory
 */
@javax.annotation.Generated(value = "class io.swagger.codegen.languages.SpringCodegen", date = "2017-08-31T11:27:51.258Z")

public class AlertFeedbackHistory {
    @JsonProperty("time")
    private BigDecimal time = null;

    /**
     * Gets or Sets feedback
     */
    public enum FeedbackEnum {
        NONE("None"),

        APPROVED("Approved"),

        REJECTED("Rejected");

        private String value;

        FeedbackEnum(String value) {
            this.value = value;
        }

        @Override
        @JsonValue
        public String toString() {
            return String.valueOf(value);
        }

        @JsonCreator
        public static FeedbackEnum fromValue(String text) {
            for (FeedbackEnum b : FeedbackEnum.values()) {
                if (String.valueOf(b.value).equals(text)) {
                    return b;
                }
            }
            return null;
        }
    }

    @JsonProperty("feedback")
    private FeedbackEnum feedback = null;

    public AlertFeedbackHistory time(BigDecimal time) {
        this.time = time;
        return this;
    }

    /**
     * Get time
     *
     * @return time
     **/
    @ApiModelProperty(value = "")
    public BigDecimal getTime() {
        return time;
    }

    public void setTime(BigDecimal time) {
        this.time = time;
    }

    public AlertFeedbackHistory feedback(FeedbackEnum feedback) {
        this.feedback = feedback;
        return this;
    }

    /**
     * Get feedback
     *
     * @return feedback
     **/
    @ApiModelProperty(value = "")
    public FeedbackEnum getFeedback() {
        return feedback;
    }

    public void setFeedback(FeedbackEnum feedback) {
        this.feedback = feedback;
    }


    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        AlertFeedbackHistory alertFeedbackHistory = (AlertFeedbackHistory) o;
        return Objects.equals(this.time, alertFeedbackHistory.time) &&
                Objects.equals(this.feedback, alertFeedbackHistory.feedback);
    }

    @Override
    public int hashCode() {
        return Objects.hash(time, feedback);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class AlertFeedbackHistory {\n");

        sb.append("    time: ").append(toIndentedString(time)).append("\n");
        sb.append("    feedback: ").append(toIndentedString(feedback)).append("\n");
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

