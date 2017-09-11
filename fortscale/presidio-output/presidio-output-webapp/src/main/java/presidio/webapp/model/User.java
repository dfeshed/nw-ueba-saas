package presidio.webapp.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * User
 */
@javax.annotation.Generated(value = "class io.swagger.codegen.languages.SpringCodegen", date = "2017-09-11T14:54:02.996Z")

public class User {
    @JsonProperty("id")
    private String id = null;

    @JsonProperty("username")
    private String username = null;

    @JsonProperty("userDisplayName")
    private String userDisplayName = null;

    @JsonProperty("tags")
    private List<String> tags = new ArrayList<String>();

    @JsonProperty("score")
    private Integer score = null;

    @JsonProperty("userSeverity")
    private UserSeverity userSeverity = null;

    @JsonProperty("alertsNum")
    private Integer alertsNum = null;

    @JsonProperty("alerts")
    private List<Alert> alerts = new ArrayList<Alert>();

    @JsonProperty("alertClassifications")
    private List<String> alertclassifications = new ArrayList<String>();

    public User id(String id) {
        this.id = id;
        return this;
    }

    /**
     * Get id
     *
     * @return id
     **/
    @ApiModelProperty(example = "d290f1ee-6c54-4b01-90e6-d701748f0851", required = true, value = "")
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public User username(String username) {
        this.username = username;
        return this;
    }

    /**
     * Get username
     *
     * @return username
     **/
    @ApiModelProperty(example = "Moshe", value = "")
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public User userDisplayName(String userDisplayName) {
        this.userDisplayName = userDisplayName;
        return this;
    }

    /**
     * Get userDisplayName
     *
     * @return userDisplayName
     **/
    @ApiModelProperty(example = "Moshe@someBigCompany.com", value = "")
    public String getUserDisplayName() {
        return userDisplayName;
    }

    public void setUserDisplayName(String userDisplayName) {
        this.userDisplayName = userDisplayName;
    }

    public User tags(List<String> tags) {
        this.tags = tags;
        return this;
    }

    public User addTagsItem(String tagsItem) {
        this.tags.add(tagsItem);
        return this;
    }

    /**
     * Array of strings
     *
     * @return tags
     **/
    @ApiModelProperty(value = "Array of strings")
    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public User score(Integer score) {
        this.score = score;
        return this;
    }

    /**
     * Get score
     * minimum: 0
     *
     * @return score
     **/
    @ApiModelProperty(value = "")
    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public User userSeverity(UserSeverity userSeverity) {
        this.userSeverity = userSeverity;
        return this;
    }

    /**
     * Get userSeverity
     *
     * @return userSeverity
     **/
    @ApiModelProperty(value = "")
    public UserSeverity getUserSeverity() {
        return userSeverity;
    }

    public void setUserSeverity(UserSeverity userSeverity) {
        this.userSeverity = userSeverity;
    }

    public User alertsNum(Integer alertsNum) {
        this.alertsNum = alertsNum;
        return this;
    }

    /**
     * Get alertsNum
     *
     * @return alertsNum
     **/
    @ApiModelProperty(value = "")
    public Integer getAlertsNum() {
        return alertsNum;
    }

    public void setAlertsNum(Integer alertsNum) {
        this.alertsNum = alertsNum;
    }

    public User alerts(List<Alert> alerts) {
        this.alerts = alerts;
        return this;
    }

    public User addAlertsItem(Alert alertsItem) {
        this.alerts.add(alertsItem);
        return this;
    }

    /**
     * Get alerts
     *
     * @return alerts
     **/
    @ApiModelProperty(value = "")
    public List<Alert> getAlerts() {
        return alerts;
    }

    public void setAlerts(List<Alert> alerts) {
        this.alerts = alerts;
    }

    public User alertclassifications(List<String> alertclassifications) {
        this.alertclassifications = alertclassifications;
        return this;
    }

    public User addAlertclassificationsItem(String alertclassificationsItem) {
        this.alertclassifications.add(alertclassificationsItem);
        return this;
    }

    /**
     * Get alertClassifications
     *
     * @return alertClassifications
     **/
    @ApiModelProperty(value = "")
    public List<String> getAlertclassifications() {
        return alertclassifications;
    }

    public void setAlertclassifications(List<String> alertclassifications) {
        this.alertclassifications = alertclassifications;
    }


    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        User user = (User) o;
        return Objects.equals(this.id, user.id) &&
                Objects.equals(this.username, user.username) &&
                Objects.equals(this.userDisplayName, user.userDisplayName) &&
                Objects.equals(this.tags, user.tags) &&
                Objects.equals(this.score, user.score) &&
                Objects.equals(this.userSeverity, user.userSeverity) &&
                Objects.equals(this.alertsNum, user.alertsNum) &&
                Objects.equals(this.alerts, user.alerts) &&
                Objects.equals(this.alertclassifications, user.alertclassifications);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username, userDisplayName, tags, score, userSeverity, alertsNum, alerts, alertclassifications);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class User {\n");

        sb.append("    id: ").append(toIndentedString(id)).append("\n");
        sb.append("    username: ").append(toIndentedString(username)).append("\n");
        sb.append("    userDisplayName: ").append(toIndentedString(userDisplayName)).append("\n");
        sb.append("    tags: ").append(toIndentedString(tags)).append("\n");
        sb.append("    score: ").append(toIndentedString(score)).append("\n");
        sb.append("    userSeverity: ").append(toIndentedString(userSeverity)).append("\n");
        sb.append("    alertsNum: ").append(toIndentedString(alertsNum)).append("\n");
        sb.append("    alerts: ").append(toIndentedString(alerts)).append("\n");
        sb.append("    alertClassifications: ").append(toIndentedString(alertclassifications)).append("\n");
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

