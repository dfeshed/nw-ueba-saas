package com.rsa.netwitness.presidio.automation.converter.events;

import fortscale.common.general.Schema;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Map;

import static java.util.Objects.requireNonNull;

public abstract class NetwitnessEvent {
    private static final DateTimeFormatter BROKER_EVENT_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
            .withLocale(Locale.getDefault()).withZone(ZoneId.of("UTC"));

    public final Instant eventTimeEpoch;
    public final Instant mongo_source_event_time;
    public final String mongoEventTime;
    public final String brokerEventTime;
    public final Schema schema;
    public CefHeader cefHeader;


    public NetwitnessEvent(Instant eventTimeEpoch, Schema schema) {
        this.eventTimeEpoch = requireNonNull(eventTimeEpoch);
        this.mongo_source_event_time = requireNonNull(eventTimeEpoch);
        this.mongoEventTime = String.valueOf(eventTimeEpoch.toEpochMilli());
        this.brokerEventTime = BROKER_EVENT_TIME_FORMATTER.format(eventTimeEpoch);
        this.schema = requireNonNull(schema);
    }

    public abstract Map<String, Object> getEvent();
}
