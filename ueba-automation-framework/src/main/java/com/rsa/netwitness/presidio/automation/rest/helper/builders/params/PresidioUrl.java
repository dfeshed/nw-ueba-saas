package com.rsa.netwitness.presidio.automation.rest.helper.builders.params;

public abstract class PresidioUrl {
    protected final String URL;

    protected PresidioUrl(String url) {
        URL = url;
    }

    public String toString() {
        return URL;
    }
}
