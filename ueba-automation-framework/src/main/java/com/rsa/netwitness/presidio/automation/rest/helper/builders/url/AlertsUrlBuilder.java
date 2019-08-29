package com.rsa.netwitness.presidio.automation.rest.helper.builders.url;

import com.rsa.netwitness.presidio.automation.rest.helper.builders.params.ParametersUrlHelper;
import com.rsa.netwitness.presidio.automation.rest.helper.rest.AlertsRestCallHelper;

public class AlertsUrlBuilder extends UrlBase {

    public AlertsUrlBuilder(String url) {
        this.URL = BASE_URL.concat(url);
    }

    public AlertsWithIdUrlBuilder withId(String alertId) {
        return new AlertsWithIdUrlBuilder(this.URL.concat("/").concat(alertId));
    }

    public AlertsRestCallHelper request() {
        return new AlertsRestCallHelper();
    }

    public ParametersUrlHelper url() {
        return new ParametersUrlHelper(URL);
    }
}
