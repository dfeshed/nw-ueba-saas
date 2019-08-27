package com.rsa.netwitness.presidio.automation.converter.events;

import fortscale.common.general.Schema;

import java.lang.reflect.Field;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public abstract class WindowsEvent extends NetwitnessEvent {

    protected String group;
    protected String ip_src;
    protected String device_ip;
    protected String medium;
    protected String header_id;
    protected List<String> alias_host;
    protected String msg_id;
    protected String user_src;
    protected String host_src;
    protected String host_dst;
    protected String process;
    protected String result;
    protected String event_cat_name;
    protected String device_disc;
    protected String did;
    protected String rid;
    protected String sessionid;
    protected String user_dst;
    protected String domain;
    protected String result_code;
    protected String event_source_id;
    protected String event_type;
    protected String device_type;
    protected String reference_id;
    protected String event_source;
    protected String event_computer;
    protected String category;
    protected String event_user;
    protected String logon_type;
    protected String context;
    protected String netname;
    protected String ec_theme;
    protected String ec_subject;
    protected String ec_activity;
    protected String ec_outcome;
    protected String obj_name;
    protected String obj_type;
    protected String service_name;
    protected String filename;
    protected String accesses;


    protected WindowsEvent(Instant eventTimeEpoch, Schema schema) {
        super(eventTimeEpoch, schema);
    }


    @Override
    public Map<String, Object> getEvent() {
        Field[] classFields = WindowsEvent.class.getDeclaredFields();
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
