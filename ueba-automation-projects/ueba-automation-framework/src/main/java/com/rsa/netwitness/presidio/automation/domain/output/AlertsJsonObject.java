package com.rsa.netwitness.presidio.automation.domain.output;

import com.google.gson.annotations.Expose;

import java.util.List;

public class AlertsJsonObject {
    @Expose
    private List<AlertsStoredRecord> alerts;

    public List<AlertsStoredRecord> getAlerts() {
        return alerts;
    }

    public void setAlerts(List<AlertsStoredRecord> alerts) {
        this.alerts = alerts;
    }

    @Override
    public String toString() {
        return "AlertsJsonObject{" +
                "alerts=" + alerts +
                '}';
    }
}
