package com.rsa.netwitness.presidio.automation.domain.output;

public class AlertFeedbackRecord {
    private String type;
    private String id;
    private String analystUserName;
    private String modifiedAt;
    private String alertId;
    private String lastModifiedDate;
    private String lastModifiedBy;
    private String createdBy;
    private String alertFeedback;
    private String userScoreAfter;
    private String userScoreSeverityAfter;
    private String scoreDelta;
    private String createdDate;

    public String getType() {
        return type;
    }

    public String getId() {
        return id;
    }

    public String getAnalystUserName() {
        return analystUserName;
    }

    public String getModifiedAt() {
        return modifiedAt;
    }

    public String getAlertId() {
        return alertId;
    }

    public String getLastModifiedDate() {
        return lastModifiedDate;
    }

    public String getLastModifiedBy() {
        return lastModifiedBy;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public String getAlertFeedback() {
        return alertFeedback;
    }

    public String getUserScoreAfter() {
        return userScoreAfter;
    }

    public String getUserScoreSeverityAfter() {
        return userScoreSeverityAfter;
    }

    public String getScoreDelta() {
        return scoreDelta;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    @Override
    public String toString() {
        return "AlertFeedbackRecord{" +
                "type='" + type + '\'' +
                ", id='" + id + '\'' +
                ", analystUserName='" + analystUserName + '\'' +
                ", modifiedAt='" + modifiedAt + '\'' +
                ", alertId='" + alertId + '\'' +
                ", lastModifiedDate='" + lastModifiedDate + '\'' +
                ", lastModifiedBy='" + lastModifiedBy + '\'' +
                ", createdBy='" + createdBy + '\'' +
                ", alertFeedback='" + alertFeedback + '\'' +
                ", userScoreAfter='" + userScoreAfter + '\'' +
                ", userScoreSeverityAfter='" + userScoreSeverityAfter + '\'' +
                ", scoreDelta='" + scoreDelta + '\'' +
                ", createdDate='" + createdDate + '\'' +
                '}';
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setAnalystUserName(String analystUserName) {
        this.analystUserName = analystUserName;
    }

    public void setModifiedAt(String modifiedAt) {
        this.modifiedAt = modifiedAt;
    }

    public void setAlertId(String alertId) {
        this.alertId = alertId;
    }

    public void setLastModifiedDate(String lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public void setLastModifiedBy(String lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public void setAlertFeedback(String alertFeedback) {
        this.alertFeedback = alertFeedback;
    }

    public void setUserScoreAfter(String userScoreAfter) {
        this.userScoreAfter = userScoreAfter;
    }

    public void setUserScoreSeverityAfter(String userScoreSeverityAfter) {
        this.userScoreSeverityAfter = userScoreSeverityAfter;
    }

    public void setScoreDelta(String scoreDelta) {
        this.scoreDelta = scoreDelta;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }
}
