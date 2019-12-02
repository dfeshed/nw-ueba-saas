package com.rsa.netwitness.presidio.automation.rest.helper.builders.params;

public abstract class PresidioUrl {
    public final String URL;
    public final String METHOD;
    public final String JSON_BODY;

    protected PresidioUrl(String url) {
        this(url, "GET", "");
    }

    protected PresidioUrl(String url, String method, String jsonBody) {
        URL = url;
        METHOD = method;
        JSON_BODY = jsonBody;
    }


    public String toString() {
        return METHOD + " " + URL + (JSON_BODY.isBlank() ? "" : "Body: " + JSON_BODY);
    }
}
