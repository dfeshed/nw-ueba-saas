package com.rsa.netwitness.presidio.automation.converter.events;

import java.util.List;

public class EndpointEvent extends NetwitnessEvent {

    protected String event_time;
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

}
