package com.rsa.netwitness.presidio.automation.converter.events;

import com.google.common.collect.ImmutableMap;
import fortscale.common.general.Schema;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Map.Entry.comparingByKey;

public class AwsEvent extends NetwitnessEvent {
    private final Map<String, Object> awsEvent;

    public AwsEvent(NetwitnessEvent event) {
        super(event.eventTimeEpoch, event.schema);

        Map<String, Object> brokerEvent = event.getEvent();
        brokerEvent.put("event_time", event.timeMillis);
        awsEvent = brokerEvent.entrySet().parallelStream()
                .filter(e -> FIELDS_MAPPER.get(event.schema).containsKey(e.getKey()))
                .sorted(comparingByKey())
                .collect(Collectors.toMap(
                        e -> FIELDS_MAPPER.get(event.schema).get(e.getKey()),
                        e -> e.getValue(),
                        (oldValue, newValue) -> oldValue,
                        LinkedHashMap::new
                ));
    }

    private static final ImmutableMap<String, String> ACTIVE_DIRECTORY = new ImmutableMap.Builder<String, String>()
            .put("device_type", "deviceType")
            .put("user_dst", "userDst")
            .put("reference_id", "referenceId")
            .put("user_src", "userSrc")
            .put("event_time", "eventTime")
            .put("event_type", "eventType")
            .put("group", "groupObjectId")
            .put("obj_name", "objName")
            .put("event_source_id", "eventSourceId")
            .build();

    private static final ImmutableMap<String, String> AUTHENTICATION = new ImmutableMap.Builder<String, String>()
            .put("device_type", "deviceType")
            .put("event_time", "eventTime")
            .put("host_src", "hostSrc")
            .put("user_dst", "userDst")
            .put("ec_outcome", "ecOutcome")
            .put("sessionid", "sessionId")
            .put("user_src", "userSrc")
            .put("event_type", "eventType")
            .put("reference_id", "referenceId")
            .put("service_name", "serviceName")
            .put("logon_type", "logonType")
            .put("alias_host", "aliasHost")
            .put("host_dst", "hostDst")
            .put("result_code", "resultCode")
            .put("event_source_id", "eventSourceId")
            .build();

    private static final ImmutableMap<String, String> FILE = new ImmutableMap.Builder<String, String>()
            .put("user_dst", "userDst")
            .put("device_type", "deviceType")
            .put("reference_id", "referenceId")
            .put("obj_type", "objType")
            .put("obj_name", "objName")
            .put("event_type", "eventType")
            .put("result_code", "resultCode")
            .put("event_source_id", "eventSourceId")
            .put("event_time", "eventTime")
            .build();

    private static final ImmutableMap<String, String> PROCESS = new ImmutableMap.Builder<String, String>()
            .put("event_source_id", "eventSourceId")
            .put("device_type", "deviceType")
            .put("user_src", "userSrc")
            .put("alias_host", "aliasHost")
            .put("directory_src", "directorySrc")
            .put("filename_src", "filenameSrc")
            .put("cert_common", "certCommon")
            .put("directory_dst", "directoryDst")
            .put("filename_dst", "filenameDst")
            .put("event_time", "eventTime")
            .put("dir_path_src", "dirPathSrc")
            .put("dir_path_dst", "dirPathDst")
            .put("file_cat_src", "fileCatSrc")
            .put("file_cat_dst", "fileCatDst")
            .put("checksum_src", "checksumSrc")
            .put("checksum_dst", "checksumDst")
            .put("agent_id", "agentId")
            .put("nwe_callback_id", "nweCallbackId")
            .put("os", "os")
            .put("process_vid_src", "processVidSrc")
            .put("process_vid_dst", "processVidDst")
            .build();

    private static final ImmutableMap<String, String> REGISTRY = new ImmutableMap.Builder<String, String>()
            .put("event_source_id", "eventSourceId")
            .put("event_time", "eventTime")
            .put("device_type", "deviceType")
            .put("user_src", "userSrc")
            .put("alias_host", "aliasHost")
            .put("cert_common", "certCommon")
            .put("directory_src", "directorySrc")
            .put("filename_src", "filenameSrc")
            .put("ec_subject", "ecSubject")
            .put("registry_key", "registryKey")
            .put("dir_path_src", "dirPathSrc")
            .put("file_cat_src", "fileCatSrc")
            .put("nwe_callback_id", "nweCallbackId")
            .build();

    private static final ImmutableMap<String, String> TLS = new ImmutableMap.Builder<String, String>()
            .put("event_time", "time")
            .put("tcp_srcport", "tcpSrcPort")
            .put("tcp_dstport", "tcpDstPort")
            .put("ip_src", "ipSrc")
            .put("ip_dst", "ipDst")
            .put("event_source_id", "eventSourceId")
            .put("country_src", "countrySrc")
            .put("country_dst", "countryDst")
            .put("ssl_subject", "sslSubject")
            .put("ssl_ca", "sslCA")
            .put("alias_host", "aliasHost")
            .put("org_dst", "orgDst")
            .put("asn_dst", "asnDst")
            .put("payload_req", "payloadReq")
            .put("payload_res", "payloadRes")
            .put("session_split", "sessionSplit")
            .build();

    private static final ImmutableMap<Schema,  ImmutableMap<String, String>> FIELDS_MAPPER = new ImmutableMap.Builder<Schema, ImmutableMap<String, String>>()
            .put(Schema.ACTIVE_DIRECTORY, ACTIVE_DIRECTORY)
            .put(Schema.AUTHENTICATION, AUTHENTICATION)
            .put(Schema.FILE, FILE)
            .put(Schema.PROCESS, PROCESS)
            .put(Schema.REGISTRY, REGISTRY)
            .put(Schema.TLS, TLS)
            .build();


    @Override
    public Map<String, Object> getEvent() {
        return awsEvent;
    }
}
