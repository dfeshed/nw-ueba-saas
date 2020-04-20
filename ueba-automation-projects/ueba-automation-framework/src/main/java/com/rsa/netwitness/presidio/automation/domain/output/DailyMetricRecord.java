package com.rsa.netwitness.presidio.automation.domain.output;

import com.google.gson.annotations.Expose;

public class DailyMetricRecord {
    @Expose
    public final String metricName;
    @Expose
    public final Integer metricValue;
    @Expose
    public final String reportTime;
    @Expose
    public final String logicalTime;


    public DailyMetricRecord(String metricName, Integer metricValue, String reportTime, String logicalTime) {
        this.metricName = metricName;
        this.metricValue = metricValue;
        this.reportTime = reportTime;
        this.logicalTime = logicalTime;
    }

    @Override
    public String toString() {
        return "DailyMetricRecord{" +
                "metricName='" + metricName + '\'' +
                ", metricValue=" + metricValue +
                ", reportTime=" + reportTime +
                ", logicalTime=" + logicalTime +
                '}';
    }
}
