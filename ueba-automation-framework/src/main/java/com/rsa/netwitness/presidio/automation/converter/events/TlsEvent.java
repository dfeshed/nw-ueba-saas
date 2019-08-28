package com.rsa.netwitness.presidio.automation.converter.events;

import fortscale.common.general.Schema;

import java.lang.reflect.Field;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public abstract class TlsEvent extends NetwitnessEvent {

    protected String event_source_id;
    protected String data_source;
    protected String ip_src;
    protected String country_dst;
    protected String ssl_subject;
    protected String org_dst;
    protected String asn_dst;
    protected long payload_req;
    protected long payload_res;
    protected List<String> netname;
    protected String ja3;
    protected String direction;
    protected int tcp_dstport;
    protected int tcp_srcport;
    protected String ja3s;
    protected String network;
    protected String ip_dst;
    protected String country_src;
    protected List<String> alias_host;
    protected List<String> ssl_ca;
    protected int session_split;
    protected Boolean analysis_service;
    protected String service_name;
    protected String time;


    protected TlsEvent(Instant eventTimeEpoch, Schema schema) {
        super(eventTimeEpoch, schema);
        time = timeMillis;
    }

    @Override
    public Map<String, Object> getEvent() {
        Field[] classFields = TlsEvent.class.getDeclaredFields();
        Map<String, Object> eventMap = new LinkedHashMap<>();

        for (Field classField : classFields) {
            eventMap.computeIfAbsent(classField.getName(), e -> {
                try {
                    return classField.get(this);
                } catch (IllegalAccessException ex) {
                    System.err.println("Field name: " + classField.getName());
                    ex.printStackTrace();
                    return eventMap;
                }
            });
        }
        return eventMap;
    }


}
