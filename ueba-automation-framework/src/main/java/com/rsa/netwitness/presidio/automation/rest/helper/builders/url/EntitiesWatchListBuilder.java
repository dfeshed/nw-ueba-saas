package com.rsa.netwitness.presidio.automation.rest.helper.builders.url;

import com.rsa.netwitness.presidio.automation.rest.helper.builders.params.EntitiesWatchListUrlHelper;
import com.rsa.netwitness.presidio.automation.rest.helper.rest.EntitiesRestCallHelper;

public class EntitiesWatchListBuilder extends UrlBase {

    public EntitiesWatchListBuilder(String url) {
        this.URL = BASE_URL.concat(url);
    }

    public EntitiesRestCallHelper request() {
        return new EntitiesRestCallHelper();
    }

    public EntitiesWatchListUrlHelper url() {
        return new EntitiesWatchListUrlHelper(URL);
    }

}
