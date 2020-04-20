package com.rsa.netwitness.presidio.automation.domain.output;

import com.google.gson.annotations.Expose;

public class IndicatorsStoredRecord {
    private String id;
    @Expose
    private String name;
    private long startDate;
    private long endDate;
    private String anomalyValue;
    @Expose
    private String schema;
    private double scoreContribution;
    @Expose
    private String type;
    private double score;
    @Expose
    private int eventsNum;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getStartDate() {
        return startDate;
    }

    public void setStartDate(long startDate) {
        this.startDate = startDate;
    }

    public long getEndDate() {
        return endDate;
    }

    public void setEndDate(long endDate) {
        this.endDate = endDate;
    }

    public String getAnomalyValue() {
        return anomalyValue;
    }

    public void setAnomalyValue(String anomalyValue) {
        this.anomalyValue = anomalyValue;
    }

    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

    public double getScoreContribution() {
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

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public int getEventsNum() {
        return eventsNum;
    }

    public void setEventsNum(int eventsNum) {
        this.eventsNum = eventsNum;
    }

    @Override
    public String toString() {
        return "IndicatorsStoredRecord{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", anomalyValue='" + anomalyValue + '\'' +
                ", schema='" + schema + '\'' +
                ", scoreContribution=" + scoreContribution +
                ", type='" + type + '\'' +
                ", score=" + score +
                ", eventsNum=" + eventsNum +
                '}';
    }
}
