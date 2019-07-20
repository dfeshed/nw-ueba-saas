package com.rsa.netwitness.presidio.automation.helpers.rest;

public class AlertsUrlBuilder extends RestBase {

    AlertsUrlBuilder(String url) {
        this.URL = BASE_URL.concat(url);
    }

    public AlertsWithIdUrlBuilder withId(String alertId) {
        return new AlertsWithIdUrlBuilder(this.URL.concat("/").concat(alertId));
    }
}
