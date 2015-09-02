package com.rsa.asoc.sa.ui.common.endpoint.domain.bean;

/**
 * Supported endpoint transport types
 *
 * @author Jay Garala
 * @since 10.6.0
 */
public enum TransportType {
    NEXTGEN("nextgen"),
    JMS("jms"),
    VM("vm"),
    VIVES("vives");

    private final String transport;

    TransportType(String transport) {
        this.transport = transport;
    }

    public String getTransport() {
        return this.transport;
    }

}