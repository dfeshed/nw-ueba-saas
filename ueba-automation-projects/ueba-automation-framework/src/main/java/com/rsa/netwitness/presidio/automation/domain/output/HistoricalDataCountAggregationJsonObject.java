package com.rsa.netwitness.presidio.automation.domain.output;

import com.google.gson.annotations.Expose;

import java.util.List;

public class HistoricalDataCountAggregationJsonObject extends HistoricalDataJsonObject{
    @Expose
    private List<CountAggregationBucketsRecord> buckets;

    public List<CountAggregationBucketsRecord> getBuckets() {
        return buckets;
    }
    public void setBuckets(List<CountAggregationBucketsRecord> buckets) {
        this.buckets = buckets;
    }

    @Override
    public String toString() {
        String superString = super.toString();
        return superString.substring(0,superString.lastIndexOf("}")-1) +
                ", buckets=" + buckets + '}';
    }
}
