package com.rsa.netwitness.presidio.automation.domain.output;

import com.google.gson.annotations.Expose;

public class CountAggregationBucketsRecord {
    @Expose
    private String key;
    @Expose
    private int value;
    @Expose
    private boolean anomaly;

    @Override
    public String toString() {
        return "CountAggregationBucketsRecord{" +
                "key='" + key + '\'' +
                ", value=" + value +
                ", anomaly=" + anomaly +
                '}';
    }
}
