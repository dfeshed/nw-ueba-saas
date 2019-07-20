package com.rsa.netwitness.presidio.automation.helpers.rest;

import com.rsa.netwitness.presidio.automation.domain.config.HostConf;

abstract class RestBase {

    protected final String BASE_URL = "http://" + HostConf.getOutputRestIpAndPort();
    protected String URL;

    public ParametersUrlHelper url() {
        return new ParametersUrlHelper(URL);
    }

    public RestCallHelper request() {
        return new RestCallHelper(URL);
    }

}
