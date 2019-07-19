package com.rsa.netwitness.presidio.automation.utils.adapter.log_player.events;

import fortscale.common.general.Schema;

import java.lang.reflect.Field;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;

import static java.util.stream.Collectors.toList;

public abstract class NetwitnessEvent implements ConverterEventBase {

    @Override
    public NetwitnessEvent getAsNetwitnessEvent() {
        return this;
    }

    protected DateTimeFormatter eventTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss").withLocale( Locale.getDefault() ).withZone( ZoneId.of("UTC"));
    protected Instant eventTimeEpoch;
    protected Schema schema;

    protected String cefVendor;
    protected String cefProduct;
    protected String cefEventType;
    protected String cefEventDesc;

    protected String device;
    protected String group;
    protected String event_type;
    protected String event_time;
    protected String ip_src;
    protected String lc_cid;
    protected String forward_ip;
    protected String device_ip;
    protected String medium;
    protected String header_id;
    protected String client;
    protected String alias_host;
    protected String alias_host$1;
    protected String alias_host$2;
    protected String msg;
    protected String type;
    protected String msg_id;
    protected String user_src;
    protected String action;
    protected String host_src;
    protected String net_block;
    protected String net_subnet;
    protected String host_dst;
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
    protected String ip_all;
    protected String ip_all$1;
    protected String host_all;
    protected String host_all$1;
    protected String user_all$2;
    protected String host_all$3;
    protected String ip_all$2;
    protected String sessionid;
    protected String user_dst;
    protected String result_code;
    protected String event_source_id;
    protected String device_type;
    protected String reference_id;
    protected String event_source;
    protected String event_computer;
    protected String category;
    protected String event_desc;
    protected String event_user;
    protected String logon_type;
    protected String context;
    protected String netname;
    protected String netname$1;
    protected String ec_theme;
    protected String ec_subject;
    protected String ec_activity;
    protected String ec_outcome;
    protected String ec_all;
    protected String obj_name;
    protected String domain;
    protected String obj_type;
    protected String service_name;
    protected String filename;
    protected String accesses;

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
    protected String os;
    protected String owner;
    protected String dir_path_src;
    protected String dir_path_src$1;
    protected String dir_path_src$2;
    protected String dir_path_dst;
    protected String dir_path_dst$1;
    protected String dir_path_dst$2;

    protected String file_cat;
    protected String file_cat$1;
    protected String file_cat$2;
    protected String registry_key;
    protected String process_vid_dst;


    protected String ip_dst;
    protected String country_dst;
    protected String ssl_subject;
    protected String org_dst;
    protected String asn_dst;
    protected String payload_req;
    protected String payload_res;
    protected String ja3;
    protected String direction;
    protected String tcp_dstport;
    protected String ja3s;
    protected String network;
    protected String service;
    protected String ssl_ca;
    protected String analysis_service;
    protected String session_split;
    protected String country_src;



    public String getCefVendor() {
        return cefVendor;
    }

    public String getCefProduct() {
        return cefProduct;
    }

    public String getCefEventType() {
        return cefEventType;
    }

    public String getCefEventDesc() {
        return cefEventDesc;
    }

    @Override
    public Schema mongoSchema() {
        return schema;
    }

    public Instant getEventTimeEpoch() {
        return eventTimeEpoch;
    }

    public Map<String, String> getCefKeyValueMap() {
        Map<String, String> cefFields = new LinkedHashMap<>();

        cefOnlyFields
                .forEach(field -> cefFields.computeIfAbsent(field.getName(), v -> stringValueOf.apply(field)));

        return cefFields;
    }

    private Function<Field, String> stringValueOf = field -> {
        try {
            return (String) field.get(this);
        } catch (IllegalAccessException | ClassCastException e1) {
            System.err.println("Field name: " + field.getName());
            e1.printStackTrace();
        }
        return  null;
    };

    private static boolean cefFields(Field cefFeild) {
        String [] filterList = {"eventTimeFormatter", "schema", "eventTimeEpoch"};

        boolean result = Arrays.stream(filterList)
                .anyMatch(elementToFilter -> elementToFilter.equalsIgnoreCase(cefFeild.getName()));

        result |= cefFeild.getName().startsWith("cef");
        result |= ( !cefFeild.getType().isAssignableFrom(String.class) );
        return !result;
    }

    private static final Field[] classFields = NetwitnessEvent.class.getDeclaredFields();
    private static final List<Field> cefOnlyFields = Arrays.stream(classFields)
            .filter(NetwitnessEvent::cefFields)
            .collect(toList());
}
