package com.rsa.netwitness.presidio.automation.rest.helper.builders.url;

import com.rsa.netwitness.presidio.automation.rest.helper.builders.params.ParametersUrlHelper;

public class AlertsWithIdUrlBuilder extends UrlBase {

    AlertsWithIdUrlBuilder(String url) {
        this.URL = url;
    }

    public IndicatorUrlBuilder indicators() {
        return new IndicatorUrlBuilder(this.URL.concat("/indicators"));
    }

    public ParametersUrlHelper url() {
        return new ParametersUrlHelper(URL);
    }
}
