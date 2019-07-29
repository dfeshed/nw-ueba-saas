package com.rsa.netwitness.presidio.automation.rest.helper;

import com.rsa.netwitness.presidio.automation.rest.helper.builders.url.AlertsUrlBuilder;
import com.rsa.netwitness.presidio.automation.rest.helper.builders.url.EntitiesUrlBuilder;

public class RestHelper {

    public AlertsUrlBuilder alerts() {
        return new AlertsUrlBuilder("/alerts");
    }

    public EntitiesUrlBuilder entities() {
        return new EntitiesUrlBuilder("/entities");
    }
}
