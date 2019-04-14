package presidio.webapp.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static presidio.webapp.model.EntityQueryEnums.EntitySeverity;

/**
 * Entity
 */
@javax.annotation.Generated(value = "class io.swagger.codegen.languages.SpringCodegen", date = "2017-09-11T15:25:30.236Z")

public class Entity {
    @JsonProperty("id")
    private String id = null;

    @JsonProperty("entityId")
    private String entityId = null;

    @JsonProperty("entityName")
    private String entityName = null;

    @JsonProperty("tags")
    private List<String> tags = new ArrayList<String>();

    @JsonProperty("score")
    private Integer score = null;

    @JsonProperty("severity")
    private EntitySeverity severity = null;

    @JsonProperty("alertsCount")
    private Integer alertsCount = null;

    @JsonProperty("alerts")
    private List<Alert> alerts = new ArrayList<Alert>();

    @JsonProperty("alertClassifications")
    private List<String> alertClassifications = new ArrayList<String>();

    public Entity id(String id) {
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

    public Entity entityId(String entityId) {
        this.entityId = entityId;
        return this;
    }

    /**
     * Get id
     *
     * @return id
     **/
    @ApiModelProperty(example = "d290f1ee-6c54-4b01-90e6-d701748f0851", required = true, value = "")
    public String getEntityId() {
        return entityId;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }


    public Entity entityName(String entityName) {
        this.entityName = entityName;
        return this;
    }

    /**
     * Get entityName
     *
     * @return entityName
     **/
    @ApiModelProperty(example = "Moshe", value = "")
    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    public Entity tags(List<String> tags) {
        this.tags = tags;
        return this;
    }

    public Entity addTagsItem(String tagsItem) {
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

    public Entity score(Integer score) {
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

    public Entity entitySeverity(EntitySeverity entitySeverity) {
        this.severity = entitySeverity;
        return this;
    }

    /**
     * Get severity
     *
     * @return severity
     **/
    @ApiModelProperty(value = "")
    public EntitySeverity getSeverity() {
        return severity;
    }

    public void setSeverity(EntitySeverity severity) {
        this.severity = severity;
    }

    public Entity alertsNum(Integer alertsNum) {
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

    public Entity alerts(List<Alert> alerts) {
        this.alerts = alerts;
        return this;
    }

    public Entity addAlertsItem(Alert alertsItem) {
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

    public Entity alertClassifications(List<String> alertClassifications) {
        this.alertClassifications = alertClassifications;
        return this;
    }

    public Entity addAlertClassificationsItem(String alertClassificationsItem) {
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
        Entity entity = (Entity) o;
        return Objects.equals(id, entity.id) &&
                Objects.equals(entityId, entity.entityId) &&
                Objects.equals(entityName, entity.entityName) &&
                Objects.equals(tags, entity.tags) &&
                Objects.equals(score, entity.score) &&
                severity == entity.severity &&
                Objects.equals(alertsCount, entity.alertsCount) &&
                Objects.equals(alerts, entity.alerts) &&
                Objects.equals(alertClassifications, entity.alertClassifications);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, entityId, entityName, tags, score, severity, alertsCount, alerts, alertClassifications);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("User{");
        sb.append("id='").append(id).append('\'');
        sb.append(", entityId='").append(entityId).append('\'');
        sb.append(", entityName='").append(entityName).append('\'');
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

