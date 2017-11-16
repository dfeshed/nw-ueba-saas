package presidio.webapp.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static presidio.webapp.model.UserQueryEnums.UserSeverity;

/**
 * User
 */
@javax.annotation.Generated(value = "class io.swagger.codegen.languages.SpringCodegen", date = "2017-09-11T15:25:30.236Z")

public class User {
    @JsonProperty("id")
    private String id = null;

    @JsonProperty("userId")
    private String userId = null;

    @JsonProperty("username")
    private String username = null;

    @JsonProperty("userDisplayName")
    private String userDisplayName = null;

    @JsonProperty("tags")
    private List<String> tags = new ArrayList<String>();

    @JsonProperty("score")
    private Integer score = null;

    @JsonProperty("severity")
    private UserSeverity severity = null;

    @JsonProperty("alertsCount")
    private Integer alertsCount = null;

    @JsonProperty("alerts")
    private List<Alert> alerts = new ArrayList<Alert>();

    @JsonProperty("alertClassifications")
    private List<String> alertClassifications = new ArrayList<String>();

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

    public User userId(String userId) {
        this.userId = userId;
        return this;
    }

    /**
     * Get id
     *
     * @return id
     **/
    @ApiModelProperty(example = "d290f1ee-6c54-4b01-90e6-d701748f0851", required = true, value = "")
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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
        this.severity = userSeverity;
        return this;
    }

    /**
     * Get severity
     *
     * @return severity
     **/
    @ApiModelProperty(value = "")
    public UserSeverity getSeverity() {
        return severity;
    }

    public void setSeverity(UserSeverity severity) {
        this.severity = severity;
    }

    public User alertsNum(Integer alertsNum) {
        this.alertsCount = alertsNum;
        return this;
    }

    /**
     * Get alertsCount
     *
     * @return alertsCount
     **/
    @ApiModelProperty(value = "")
    public Integer getAlertsCount() {
        return alertsCount;
    }

    public void setAlertsCount(Integer alertsCount) {
        this.alertsCount = alertsCount;
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

    public User alertClassifications(List<String> alertClassifications) {
        this.alertClassifications = alertClassifications;
        return this;
    }

    public User addAlertClassificationsItem(String alertClassificationsItem) {
        this.alertClassifications.add(alertClassificationsItem);
        return this;
    }

    /**
     * Get alertClassifications
     *
     * @return alertClassifications
     **/
    @ApiModelProperty(value = "")
    public List<String> getAlertClassifications() {
        return alertClassifications;
    }

    public void setAlertClassifications(List<String> alertClassifications) {
        this.alertClassifications = alertClassifications;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id) &&
                Objects.equals(userId, user.userId) &&
                Objects.equals(username, user.username) &&
                Objects.equals(userDisplayName, user.userDisplayName) &&
                Objects.equals(tags, user.tags) &&
                Objects.equals(score, user.score) &&
                severity == user.severity &&
                Objects.equals(alertsCount, user.alertsCount) &&
                Objects.equals(alerts, user.alerts) &&
                Objects.equals(alertClassifications, user.alertClassifications);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, userId, username, userDisplayName, tags, score, severity, alertsCount, alerts, alertClassifications);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("User{");
        sb.append("id='").append(id).append('\'');
        sb.append(", userId='").append(userId).append('\'');
        sb.append(", username='").append(username).append('\'');
        sb.append(", userDisplayName='").append(userDisplayName).append('\'');
        sb.append(", tags=").append(tags);
        sb.append(", score=").append(score);
        sb.append(", severity=").append(severity);
        sb.append(", alertsCount=").append(alertsCount);
        sb.append(", alerts=").append(alerts);
        sb.append(", alertClassifications=").append(alertClassifications);
        sb.append('}');
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

