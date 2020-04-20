package com.rsa.netwitness.presidio.automation.domain.output;

import com.google.gson.annotations.Expose;

import java.util.Arrays;
import java.util.List;

public class AlertsJsonRecord {
    @Expose
    private String id;
    @Expose
    private String[] classification;
    @Expose
    private String username;
    @Expose
    private String[] indicatorsName;
    @Expose
    private Integer indicatorsNum;
    @Expose
    private String score;
    @Expose
    private String feedback;
    @Expose
    private String userScoreContribution;
    @Expose
    private String timeframe;
    @Expose
    private String severity;
    @Expose
    private String userId;
    @Expose
    private List<IndicatorsStoredRecord> indicators;
    @Expose
    private String startDate;
    @Expose
    private String endDate;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String[] getClassification() {
        return classification;
    }

    public void setClassification(String[] classification) {
        this.classification = classification;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String[] getIndicatorsName() {
        return indicatorsName;
    }

    public void setIndicatorsName(String[] indicatorsName) {
        this.indicatorsName = indicatorsName;
    }

    public Integer getIndicatorsNum() {
        return indicatorsNum;
    }

    public void setIndicatorsNum(Integer indicatorsNum) {
        this.indicatorsNum = indicatorsNum;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public String getUserScoreContribution() {
        return userScoreContribution;
    }

    public void setUserScoreContribution(String userScoreContribution) {
        this.userScoreContribution = userScoreContribution;
    }

    public String getTimeframe() {
        return timeframe;
    }

    public void setTimeframe(String timeframe) {
        this.timeframe = timeframe;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public List<IndicatorsStoredRecord> getIndicators() {
        return indicators;
    }

    public void setIndicators(List<IndicatorsStoredRecord> indicators) {
        this.indicators = indicators;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    @Override
    public String toString() {
        return "AlertsJsonRecord{" +
                "id='" + id + '\'' +
                ", classification=" + Arrays.toString(classification) +
                ", username='" + username + '\'' +
                ", indicatorsName=" + Arrays.toString(indicatorsName) +
                ", indicatorsNum=" + indicatorsNum +
                ", score='" + score + '\'' +
                ", feedback='" + feedback + '\'' +
                ", userScoreContribution='" + userScoreContribution + '\'' +
                ", timeframe='" + timeframe + '\'' +
                ", severity='" + severity + '\'' +
                ", userId='" + userId + '\'' +
                ", indicators=" + indicators +
                ", startDate='" + startDate + '\'' +
                ", endDate='" + endDate + '\'' +
                '}';
    }
}
