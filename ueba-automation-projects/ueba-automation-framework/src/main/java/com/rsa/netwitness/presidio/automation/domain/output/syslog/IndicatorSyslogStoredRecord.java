package com.rsa.netwitness.presidio.automation.domain.output.syslog;

import java.util.Arrays;

public class IndicatorSyslogStoredRecord {
    private String id;
    private String[] events;
    private String name;
    private String anomalyValue;
    private String alertId;
    private Integer startDate;
    private String schema;
    private Double score;
    private Double scoreContribution;
    private String type;
    private Integer eventsNum;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String[] getEvents() {
        return events;
    }

    public void setEvents(String[] events) {
        this.events = events;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAnomalyValue() {
        return anomalyValue;
    }

    public void setAnomalyValue(String anomalyValue) {
        this.anomalyValue = anomalyValue;
    }

    public String getAlertId() {
        return alertId;
    }

    public void setAlertId(String alertId) {
        this.alertId = alertId;
    }

    public Integer getStartDate() {
        return startDate;
    }

    public void setStartDate(int startDate) {
        this.startDate = startDate;
    }

    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public Double getScoreContribution() {
        return scoreContribution;
    }

    public void setScoreContribution(double scoreContribution) {
        this.scoreContribution = scoreContribution;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getEventsNum() {
        return eventsNum;
    }

    public void setEventsNum(int eventsNum) {
        this.eventsNum = eventsNum;
    }

    @Override
    public String toString() {
        return "IndicatorSyslogStoredRecord{" +
                "id='" + id + '\'' +
                ", events=" + Arrays.toString(events) +
                ", name='" + name + '\'' +
                ", anomalyValue='" + anomalyValue + '\'' +
                ", alertId='" + alertId + '\'' +
                ", startDate=" + startDate +
                ", schema='" + schema + '\'' +
                ", score=" + score +
                ", scoreContribution=" + scoreContribution +
                ", type='" + type + '\'' +
                ", eventsNum=" + eventsNum +
                '}';
    }
}
