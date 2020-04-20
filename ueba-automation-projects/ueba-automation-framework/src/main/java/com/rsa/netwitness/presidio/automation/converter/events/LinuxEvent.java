package com.rsa.netwitness.presidio.automation.converter.events;

import fortscale.common.general.Schema;

import java.lang.reflect.Field;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public abstract class LinuxEvent extends NetwitnessEvent {

    protected String event_type;
    protected String group;
    protected String ip_src;
    protected String lc_cid;
    protected String forward_ip;
    protected String device_ip;
    protected String medium;
    protected String header_id;
    protected String client;
    protected List<String> alias_host;
    protected String msg;
    protected String type;
    protected String msg_id;
    protected String user_src;
    protected String action;
    protected String host_src;
    protected String net_block;
    protected String net_subnet;
    protected String dst_net_block;
    protected String dst_net_subnet;
    protected String process;
    protected String result;
    protected String event_cat_name;
    protected String device_disc;
    protected String device_disc_type;
    protected String alert;
    protected String did;
    protected String rid;
    protected List<String> ip_all;
    protected List<String> host_all;
    protected String sessionid;
    protected String user_dst;
    protected String result_code;
    protected String event_source_id;
    protected String device_type;
    protected String reference_id;
    protected String obj_name;


    protected LinuxEvent(Instant eventTimeEpoch, Schema schema) {
        super(eventTimeEpoch, schema);
    }


    @Override
    public Map<String, Object> getEvent() {
        Field[] classFields = LinuxEvent.class.getDeclaredFields();
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
