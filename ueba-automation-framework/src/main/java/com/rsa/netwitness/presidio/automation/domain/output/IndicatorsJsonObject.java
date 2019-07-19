package com.rsa.netwitness.presidio.automation.domain.output;

import com.google.gson.annotations.Expose;

import java.util.List;

public class IndicatorsJsonObject {
    @Expose
    private List<IndicatorsStoredRecord> indicators;

    public List<IndicatorsStoredRecord> getIndicators() {
        return indicators;
    }

    public void setIndicators(List<IndicatorsStoredRecord> indicators) {
        this.indicators = indicators;
    }

    @Override
    public String toString() {
        return "IndicatorsStoredRecord{" +
                "indicators=" + indicators +
                '}';
    }
}
