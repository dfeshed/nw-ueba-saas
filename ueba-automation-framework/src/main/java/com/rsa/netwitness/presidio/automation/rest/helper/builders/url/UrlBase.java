package com.rsa.netwitness.presidio.automation.rest.helper.builders.url;

import com.rsa.netwitness.presidio.automation.config.AutomationConf;

public abstract class UrlBase {

    protected final String BASE_URL = AutomationConf.OUTPUT_REST_URL;
    protected String URL;

    protected UrlBase() {}

}
