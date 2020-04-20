package com.rsa.netwitness.presidio.automation.rest.client;

public enum HttpMethod {
    GET, POST, PATCH;

    @Override
    public String toString() {
        return this.name();
    }
}
