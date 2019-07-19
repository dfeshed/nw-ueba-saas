package com.rsa.netwitness.presidio.automation.domain.output;

import com.google.gson.annotations.Expose;

public class TimeAggregationIndicatorsStoredRecord extends IndicatorsStoredRecord{
    @Expose
    private HistoricalDataTimeAggregationJsonObject historicalData;

    public HistoricalDataTimeAggregationJsonObject getHistoricalData() {
        return historicalData;
    }

    public void setHistoricalData(HistoricalDataTimeAggregationJsonObject historicalData) {
        this.historicalData = historicalData;
    }

    @Override
    public String toString() {
        String superString = super.toString();
        return superString.substring(0,superString.lastIndexOf("}")-1) +
                ", historicalData=" + historicalData + '}';
    }
}
