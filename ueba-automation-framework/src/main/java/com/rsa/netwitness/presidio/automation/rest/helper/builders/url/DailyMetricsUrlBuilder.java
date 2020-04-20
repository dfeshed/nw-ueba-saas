package com.rsa.netwitness.presidio.automation.rest.helper.builders.url;

import com.rsa.netwitness.presidio.automation.rest.helper.builders.params.DailyMetricsParametersUrlHelper;
import com.rsa.netwitness.presidio.automation.rest.helper.rest.DailyMetricsRestCallHelper;

public class DailyMetricsUrlBuilder extends UrlBase {

    public DailyMetricsUrlBuilder(String url) {
        this.URL = BASE_URL.concat(url);
    }

    public DailyMetricsRestCallHelper request() {
        return new DailyMetricsRestCallHelper();
    }

    public DailyMetricsParametersUrlHelper url() {
        return new DailyMetricsParametersUrlHelper(URL);
    }

}
