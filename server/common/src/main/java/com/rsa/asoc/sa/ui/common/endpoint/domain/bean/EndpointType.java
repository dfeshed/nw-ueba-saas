package com.rsa.asoc.sa.ui.common.endpoint.domain.bean;

/**
 * Supported endpoint types
 *
 * @author Jay Garala
 * @since 10.6.0
 */
public enum EndpointType {

    APPLIANCE(TransportType.NEXTGEN),
    BROKER(TransportType.NEXTGEN),
    CONCENTRATOR(TransportType.NEXTGEN),
    DECODER(TransportType.NEXTGEN),
    LOG_DECODER(TransportType.NEXTGEN),
    WORKBENCH(TransportType.NEXTGEN),
    IPDB_EXTRACTOR(TransportType.NEXTGEN),
    WAREHOUSE_CONNECTOR(TransportType.NEXTGEN),
    LOG_COLLECTOR(TransportType.NEXTGEN),

    EVENT_STREAM_ANALYSIS(TransportType.JMS),
    INCIDENT_MANAGEMENT(TransportType.JMS),
    REPORTING_ENGINE(TransportType.VIVES);


    private final TransportType transportType;

    EndpointType(TransportType transport) {
        this.transportType = transport;
    }

    public TransportType getTransportType() {
        return transportType;
    }
}
