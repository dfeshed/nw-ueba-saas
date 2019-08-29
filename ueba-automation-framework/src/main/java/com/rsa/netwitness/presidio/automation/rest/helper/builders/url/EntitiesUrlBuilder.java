package com.rsa.netwitness.presidio.automation.rest.helper.builders.url;

import com.rsa.netwitness.presidio.automation.rest.helper.builders.params.ParametersUrlHelper;
import com.rsa.netwitness.presidio.automation.rest.helper.rest.EntitiesRestCallHelper;

public class EntitiesUrlBuilder extends UrlBase {

    public EntitiesUrlBuilder(String url) {
        this.URL = BASE_URL.concat(url);
    }

    public EntitiesRestCallHelper request() {
        return new EntitiesRestCallHelper();
    }

    public ParametersUrlHelper url() {
        return new ParametersUrlHelper(URL);
    }
}
