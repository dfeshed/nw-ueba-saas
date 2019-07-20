package com.rsa.netwitness.presidio.automation.helpers.rest;

public class IndicatorWithIdUrlBuilder extends RestBase {
    IndicatorWithIdUrlBuilder(String url) {
        this.URL = url;
    }

    public EventsUrlBuilder events() {
        return new EventsUrlBuilder(URL.concat("/events"));
    }
}
