package com.rsa.netwitness.presidio.automation.domain.output;

import com.google.gson.annotations.Expose;

import java.util.List;

public class HistoricalDataTimeAggregationJsonObject extends HistoricalDataJsonObject{
    @Expose
    private List<TimeAggregationBucketsRecord> buckets;

    public List<TimeAggregationBucketsRecord> getBuckets() {
        return buckets;
    }

    public void setBuckets(List<TimeAggregationBucketsRecord> buckets) {
        this.buckets = buckets;
    }

    @Override
    public String toString() {
        String superString = super.toString();
        return superString.substring(0,superString.lastIndexOf("}")-1) +
                ", buckets=" + buckets + '}';
    }
}
