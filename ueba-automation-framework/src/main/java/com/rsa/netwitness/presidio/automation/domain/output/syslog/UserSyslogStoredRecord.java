package com.rsa.netwitness.presidio.automation.domain.output.syslog;

public class UserSyslogStoredRecord {
    private String id;
    private String vendorUserId;
    private String userDisplayNameSortLowercase;
    private String score;
    private String severity;
    private Integer alertsCount;
    private Integer updatedByLogicalStartDate;
    private Integer updatedByLogicalEndDate;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getVendorUserId() {
        return vendorUserId;
    }

    public void setVendorUserId(String vendorUserId) {
        this.vendorUserId = vendorUserId;
    }

    public String getUserDisplayNameSortLowercase() {
        return userDisplayNameSortLowercase;
    }

    public void setUserDisplayNameSortLowercase(String userDisplayNameSortLowercase) {
        this.userDisplayNameSortLowercase = userDisplayNameSortLowercase;
    }

    public String getScore() {
        return score.substring(0, score.indexOf("."));
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public Integer getAlertsCount() {
        return alertsCount;
    }

    public void setAlertsCount(int alertsCount) {
        this.alertsCount = alertsCount;
    }

    public Integer getUpdatedByLogicalStartDate() {
        return updatedByLogicalStartDate;
    }

    public void setUpdatedByLogicalStartDate(int updatedByLogicalStartDate) {
        this.updatedByLogicalStartDate = updatedByLogicalStartDate;
    }

    public Integer getUpdatedByLogicalEndDate() {
        return updatedByLogicalEndDate;
    }

    public void setUpdatedByLogicalEndDate(int updatedByLogicalEndDate) {
        this.updatedByLogicalEndDate = updatedByLogicalEndDate;
    }

    @Override
    public String toString() {
        return "UserSyslogStoredRecord{" +
                "id='" + id + '\'' +
                ", vendorUserId='" + vendorUserId + '\'' +
                ", userDisplayNameSortLowercase='" + userDisplayNameSortLowercase + '\'' +
                ", score=" + score +
                ", severity='" + severity + '\'' +
                ", alertsCount=" + alertsCount +
                ", updatedByLogicalStartDate=" + updatedByLogicalStartDate +
                ", updatedByLogicalEndDate=" + updatedByLogicalEndDate +
                '}';
    }
}
