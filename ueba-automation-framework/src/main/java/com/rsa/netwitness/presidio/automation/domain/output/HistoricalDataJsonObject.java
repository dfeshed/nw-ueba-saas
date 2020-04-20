package com.rsa.netwitness.presidio.automation.domain.output;

import com.google.gson.annotations.Expose;

public class HistoricalDataJsonObject {
    @Expose
    private String type;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "HistoricalDataJsonObject{" +
                "type='" + type + '\'' +
                '}';
    }
}
