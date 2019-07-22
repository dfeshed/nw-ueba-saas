package com.rsa.netwitness.presidio.automation.rest.helper;

public class AlertsWithIdUrlBuilder extends RestBase {

    AlertsWithIdUrlBuilder(String url) {
        this.URL = url;
    }

    public IndicatorUrlBuilder indicators() {
        return new IndicatorUrlBuilder(this.URL.concat("/indicators"));
    }

}
