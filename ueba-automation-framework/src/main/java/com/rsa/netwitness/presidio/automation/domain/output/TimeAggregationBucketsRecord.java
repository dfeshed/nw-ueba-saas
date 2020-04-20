package com.rsa.netwitness.presidio.automation.domain.output;

import com.google.gson.annotations.Expose;

public class TimeAggregationBucketsRecord {
    private String key;
    @Expose
    private int value;
    @Expose
    private boolean anomaly;

    @Override
    public String toString() {
        return "TimeAggregationBucketsRecord{" +
                "key='" + key + '\'' +
                ", value=" + value +
                ", anomaly=" + anomaly +
                '}';
    }
}
