package com.rsa.netwitness.presidio.automation.rest.helper.builders.url;

import com.rsa.netwitness.presidio.automation.rest.helper.builders.params.ParametersUrlHelper;

public class EventsUrlBuilder extends UrlBase {
    EventsUrlBuilder(String url) {
        this.URL = url;
    }

    public ParametersUrlHelper url() {
        return new ParametersUrlHelper(URL);
    }
}
