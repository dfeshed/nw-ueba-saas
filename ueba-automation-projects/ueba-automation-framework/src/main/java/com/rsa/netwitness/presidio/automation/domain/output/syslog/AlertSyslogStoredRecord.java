package com.rsa.netwitness.presidio.automation.domain.output.syslog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AlertSyslogStoredRecord {
    private String id;
    private String[] classifications;
    private String vendorUserId;
    private String userId;
    private Integer startDate;
    private Integer endDate;
    private String score;
    private Integer indicatorsNum;
    private String timeframe;
    private String severity;
    private String[] indicatorsNames;
    private Double contributionToUserScore;

    // Only for testing convenient
    private List<IndicatorSyslogStoredRecord> indicators;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String[] getClassifications() {
        return classifications;
    }

    public void setClassifications(String[] classifications) {
        this.classifications = classifications;
    }

    public String getVendorUserId() {
        return vendorUserId;
    }

    public void setVendorUserId(String vendorUserId) {
        this.vendorUserId = vendorUserId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Integer getStartDate() {
        return startDate;
    }

    public void setStartDate(int startDate) {
        this.startDate = startDate;
    }

    public Integer getEndDate() {
        return endDate;
    }

    public void setEndDate(int endDate) {
        this.endDate = endDate;
    }

    public String getScore() {
        return score.substring(0, score.indexOf("."));
    }

    public void setScore(String score) {
        this.score = score;
    }

    public Integer getIndicatorsNum() {
        return indicatorsNum;
    }

    public void setIndicatorsNum(int indicatorsNum) {
        this.indicatorsNum = indicatorsNum;
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

    public String[] getIndicatorsNames() {
        return indicatorsNames;
    }

    public void setIndicatorsNames(String[] indicatorsNames) {
        this.indicatorsNames = indicatorsNames;
    }

    public Double getContributionToUserScore() {
        return contributionToUserScore;
    }

    public void setContributionToUserScore(double contributionToUserScore) {
        this.contributionToUserScore = contributionToUserScore;
    }

    public List<IndicatorSyslogStoredRecord> getIndicators() {
        return indicators;
    }

    public void setIndicators(List<IndicatorSyslogStoredRecord> indicators) {
        this.indicators = indicators;
    }

    public void addIndicatorToIndicatorList(IndicatorSyslogStoredRecord indicator){
        if(indicators == null) {
            indicators = new ArrayList<>();
        }

        indicators.add(indicator);
    }

    public AlertSyslogStoredRecord(String id, String[] classifications, String vendorUserId, String userId, int startDate, int endDate, String score, int indicatorsNum, String timeframe, String severity, String[] indicatorsNames, double contributionToUserScore) {
        this.id = id;
        this.classifications = classifications;
        this.vendorUserId = vendorUserId;
        this.userId = userId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.score = score;
        this.indicatorsNum = indicatorsNum;
        this.timeframe = timeframe;
        this.severity = severity;
        this.indicatorsNames = indicatorsNames;
        this.contributionToUserScore = contributionToUserScore;
    }

    public AlertSyslogStoredRecord(){}

    @Override
    public String toString() {
        return "AlertSyslogStoredRecord{" +
                "id='" + id + '\'' +
                ", classifications=" + Arrays.toString(classifications) +
                ", vendorUserId='" + vendorUserId + '\'' +
                ", userId='" + userId + '\'' +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", score=" + score +
                ", indicatorsNum=" + indicatorsNum +
                ", timeframe='" + timeframe + '\'' +
                ", severity='" + severity + '\'' +
                ", indicatorsNames=" + Arrays.toString(indicatorsNames) +
                ", contributionToUserScore=" + contributionToUserScore +
                '}';
    }
}
