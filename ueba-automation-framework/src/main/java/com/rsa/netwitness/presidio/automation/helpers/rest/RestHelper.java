package com.rsa.netwitness.presidio.automation.helpers.rest;

public class RestHelper {

    public AlertsUrlBuilder alerts() {
        return new AlertsUrlBuilder("/alerts");
    }
}
