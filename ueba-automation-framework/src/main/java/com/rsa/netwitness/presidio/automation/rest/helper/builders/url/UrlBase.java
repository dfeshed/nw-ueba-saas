package com.rsa.netwitness.presidio.automation.rest.helper.builders.url;

import com.rsa.netwitness.presidio.automation.domain.config.HostConf;
import com.rsa.netwitness.presidio.automation.rest.helper.builders.params.ParametersUrlHelper;

public abstract class UrlBase {

    protected final String BASE_URL = "http://" + HostConf.getOutputRestIpAndPort();
    protected String URL;

    protected UrlBase() {}
    public ParametersUrlHelper url() {
        return new ParametersUrlHelper(URL);
    }
}
