package com.rsa.netwitness.presidio.automation.rest.helper;

public class RestHelper {

    public AlertsUrlBuilder alerts() {
        return new AlertsUrlBuilder("/alerts");
    }
}
