package com.rsa.netwitness.presidio.automation.domain.output;

import com.google.gson.annotations.Expose;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class EntitiesStoredRecord {
    @Expose
    private String id;
    @Expose
    private List<AlertsStoredRecord> alerts;
    @Expose
    private String entityName;
    @Expose
    private String entityId;
    @Expose
    private String entityType;
    @Expose
    private String[] tags;
    @Expose
    private String score;
    @Expose
    private String sevirity;
    @Expose
    private Integer alertCount;
    @Expose
    private String[] alertClassifications;
    @Expose
    private Map<String, Integer> trendingScore;


    public EntitiesStoredRecord() {
    }

    public EntitiesStoredRecord(String id, List<AlertsStoredRecord> alerts, String entityName, String entityId,
                                String entityType, String[] tags, String score, String sevirity, Integer alertCount,
                                String[] alertClassifications, Map<String, Integer> trendingScore) {
        this.id = id;
        this.alerts = alerts;
        this.entityName = entityName;
        this.entityId = entityId;
        this.entityType = entityType;
        this.tags = tags;
        this.score = score;
        this.sevirity = sevirity;
        this.alertCount = alertCount;
        this.alertClassifications = alertClassifications;
        this.trendingScore = trendingScore;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<AlertsStoredRecord> getAlerts() {
        return alerts;
    }

    public void setAlerts(List<AlertsStoredRecord> alerts) {
        this.alerts = alerts;
    }

    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    public String getEntityId() {
        return entityId;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    public String getEntityType() {
        return entityType;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    public String[] getTags() {
        return tags;
    }

    public void setTags(String[] tags) {
        this.tags = tags;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getSeverity() {
        return sevirity;
    }

    public void setSeverity(String severity) {
        this.sevirity = severity;
    }

    public Integer getAlertCount() {
        return alertCount;
    }

    public void setAlertCount(Integer alertCount) {
        this.alertCount = alertCount;
    }

    public String[] getAlertClassifications() {
        return alertClassifications;
    }

    public void setAlertClassifications(String[] alertClassifications) {
        this.alertClassifications = alertClassifications;
    }

    public String getSevirity() {
        return sevirity;
    }

    public void setSevirity(String sevirity) {
        this.sevirity = sevirity;
    }

    public Map<String, Integer> getTrendingScore() {
        return trendingScore;
    }

    public void setTrendingScore(Map<String, Integer> trendingScore) {
        this.trendingScore = trendingScore;
    }

    public int compareScore(EntitiesStoredRecord anotherEntity) {
        int current = Integer.valueOf(score);
        int another = Integer.valueOf(anotherEntity.score);
        return Integer.compare(current, another);
    }

    @Override
    public String toString() {
        return "EntitiesStoredRecord{" +
                "id='" + id + '\'' +
                ", alerts=" + alerts +
                ", entityName='" + entityName + '\'' +
                ", entityId='" + entityId + '\'' +
                ", entityType='" + entityType + '\'' +
                ", tags=" + Arrays.toString(tags) +
                ", score='" + score + '\'' +
                ", sevirity='" + sevirity + '\'' +
                ", alertCount=" + alertCount +
                ", alertClassifications=" + Arrays.toString(alertClassifications) +
                ", trendingScore=" + trendingScore +
                '}';
    }
}
