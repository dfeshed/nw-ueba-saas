package com.rsa.netwitness.presidio.automation.helpers.rest;

public class IndicatorUrlBuilder extends RestBase {
    IndicatorUrlBuilder(String url) {
        this.URL = url;
    }

    public IndicatorWithIdUrlBuilder withId(String indicatorId) {
        return new IndicatorWithIdUrlBuilder(this.URL.concat("/").concat(indicatorId));
    }
}
