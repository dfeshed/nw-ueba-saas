package com.rsa.netwitness.presidio.automation.converter.events;

import fortscale.common.general.Schema;

import java.lang.reflect.Field;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public abstract class EndpointEvent extends NetwitnessEvent {

    protected String event_source_id;
    protected String device_type;
    protected String category;
    protected String user_src;
    protected List<String> action;
    protected List<String> alias_host;
    protected String directory_src;
    protected String filename_src;
    protected String cert_common;
    protected String directory_dst;
    protected String filename_dst;
    protected String nwe_callback_id;
    protected String checksum_src;
    protected String checksum_dst;
    protected String process_vid_src;
    protected String agent_id;
    protected String process_vid_dst;
    protected String registry_key;
    protected String ec_subject;
    protected List<String> file_cat;
    protected List<String> file_cat_src;
    protected List<String> file_cat_dst;
    protected String os;
    protected String owner;
    protected List<String> dir_path_src;
    protected List<String> dir_path_dst;
    protected String checksum$dash$id;
    protected String process$dash$vid$dash$src;
    protected String os$dash$type;
    protected String agent$dash$id;

    protected EndpointEvent(Instant eventTimeEpoch, Schema schema) {
        super(eventTimeEpoch, schema);
    }

    @Override
    public Map<String, Object> getEvent() {
        Field[] classFields = EndpointEvent.class.getDeclaredFields();
        Map<String, Object> eventMap = new LinkedHashMap<>();

        for (Field classField : classFields) {
            eventMap.computeIfAbsent(classField.getName().replaceAll("\\$dash\\$", "-"), e -> {
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
