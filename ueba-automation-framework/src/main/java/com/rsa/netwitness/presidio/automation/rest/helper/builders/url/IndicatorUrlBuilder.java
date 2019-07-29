package com.rsa.netwitness.presidio.automation.rest.helper.builders.url;

public class IndicatorUrlBuilder extends UrlBase {
    IndicatorUrlBuilder(String url) {
        this.URL = url;
    }

    public IndicatorWithIdUrlBuilder withId(String indicatorId) {
        return new IndicatorWithIdUrlBuilder(this.URL.concat("/").concat(indicatorId));
    }
}
