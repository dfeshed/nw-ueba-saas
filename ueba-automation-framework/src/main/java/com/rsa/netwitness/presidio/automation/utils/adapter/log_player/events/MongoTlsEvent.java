package com.rsa.netwitness.presidio.automation.utils.adapter.log_player.events;

import java.lang.reflect.Field;
import java.time.Instant;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

public abstract class MongoTlsEvent implements ConverterEventBase {

    // Network:
    protected Instant dateTime;
    protected String time;
    protected String event_source_id;
    protected String data_source;
    protected Instant mongo_source_event_time;
    protected Instant insert_time = Instant.now();

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
    protected String ja3s;
    protected String network;
    protected String ip_dst;
    protected String country_src;
    protected List<String> alias_host;
    protected List<String> ssl_ca;
    protected int session_split;
    protected Boolean analysis_service;

    @Override
    public Map<String,Object> getAsMongoKeyValue() {
        Map<String, Object> fields = new LinkedHashMap<>();

        Stream<Field> classFields = Arrays.stream(MongoTlsEvent.class.getDeclaredFields());
        classFields
                .forEach(field -> fields.computeIfAbsent(field.getName(), v -> valueOf.apply(field)));

        fields.remove("dateTime");
        fields.remove("valueOf");

        return fields;
    }

    private Function<Field, Object> valueOf = field -> {
        try {
            return field.get(this);
        } catch (IllegalAccessException | ClassCastException e1) {
            System.err.println("Field mongoSchema: " + field.getName());
            e1.printStackTrace();
        }
        return  null;
    };


}
