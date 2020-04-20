package com.rsa.netwitness.presidio.automation.rest.helper.builders.url;

import com.rsa.netwitness.presidio.automation.rest.helper.builders.params.ParametersUrlHelper;

public class IndicatorWithIdUrlBuilder extends UrlBase {
    IndicatorWithIdUrlBuilder(String url) {
        this.URL = url;
    }

    public EventsUrlBuilder events() {
        return new EventsUrlBuilder(URL.concat("/events"));
    }

    public ParametersUrlHelper url() {
        return new ParametersUrlHelper(URL);
    }
}
