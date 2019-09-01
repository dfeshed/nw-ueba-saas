package com.rsa.netwitness.presidio.automation.rest.helper.builders.url;

import com.rsa.netwitness.presidio.automation.rest.helper.builders.params.ParametersUrlHelper;

public class IndicatorUrlBuilder extends UrlBase {
    IndicatorUrlBuilder(String url) {
        this.URL = url;
    }

    public IndicatorWithIdUrlBuilder withId(String indicatorId) {
        return new IndicatorWithIdUrlBuilder(this.URL.concat("/").concat(indicatorId));
    }

    public ParametersUrlHelper url() {
        return new ParametersUrlHelper(URL);
    }
}
