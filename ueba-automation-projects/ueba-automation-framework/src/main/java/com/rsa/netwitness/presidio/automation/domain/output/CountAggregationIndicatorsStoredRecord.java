package com.rsa.netwitness.presidio.automation.domain.output;

import com.google.gson.annotations.Expose;

public class CountAggregationIndicatorsStoredRecord extends IndicatorsStoredRecord {
    @Expose
    private HistoricalDataCountAggregationJsonObject historicalData;

    public HistoricalDataCountAggregationJsonObject getHistoricalData() {
        return historicalData;
    }

    public void setHistoricalData(HistoricalDataCountAggregationJsonObject historicalData) {
        this.historicalData = historicalData;
    }

    @Override
    public String toString() {
        String superString = super.toString();
        return superString.substring(0,superString.lastIndexOf("}")-1) +
                ", historicalData=" + historicalData + '}';
    }
}
