package com.rsa.netwitness.presidio.automation.rest.helper.builders.url;

public class IndicatorWithIdUrlBuilder extends UrlBase {
    IndicatorWithIdUrlBuilder(String url) {
        this.URL = url;
    }

    public EventsUrlBuilder events() {
        return new EventsUrlBuilder(URL.concat("/events"));
    }
}
