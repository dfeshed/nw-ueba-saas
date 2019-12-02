package com.rsa.netwitness.presidio.automation.rest.helper.builders.params;

import com.rsa.netwitness.presidio.automation.rest.client.HttpMethod;

public abstract class PresidioUrl {
    public final String URL;
    public final HttpMethod METHOD;
    public final String JSON_BODY;

    protected PresidioUrl(String url) {
        this(url, HttpMethod.GET, "");
    }

    protected PresidioUrl(String url, HttpMethod method, String jsonBody) {
        URL = url;
        METHOD = method;
        JSON_BODY = jsonBody;
    }


    public String toString() {
        return URL;
    }

    public String print() {
        return METHOD + " \n" + URL + (JSON_BODY.isBlank() ? "" : " \nBody: " + JSON_BODY);
    }
}
