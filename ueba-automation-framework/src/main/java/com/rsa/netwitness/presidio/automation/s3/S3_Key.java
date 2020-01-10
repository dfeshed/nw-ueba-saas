package com.rsa.netwitness.presidio.automation.s3;

import com.google.common.collect.ImmutableMap;
import com.rsa.netwitness.presidio.automation.converter.events.NetwitnessEvent;
import fortscale.common.general.Schema;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.function.Function;

import static fortscale.common.general.Schema.*;
import static java.time.ZoneOffset.UTC;

public class S3_Key {

    private static final ImmutableMap<Schema, String> applicationLabel = new ImmutableMap.Builder<Schema, String>()
            .put(TLS, "NetworkTraffic")
            .put(ACTIVE_DIRECTORY, "ActiveDirectory")
            .put(AUTHENTICATION, "Authentication")
            .put(FILE, "File")
            .put(PROCESS, "Process")
            .put(REGISTRY, "Registry")
            .build();

    /** defaults **/
    private String tenant = "acme";
    private String account = "123456789012";
    private String region = "us-east-1";

    public S3_Key(String tenant, String account, String region) {
        this.tenant = tenant;
        this.account = account;
        this.region = region;
    }

    public S3_Key() { }


    public Function<NetwitnessEvent, String> key = e ->
            toPath(e.eventTimeEpoch, e.schema).concat(toFileName(e.eventTimeEpoch, e.schema));

    public String key(Instant eventTime, Schema schema) {
        return toPath(eventTime, schema).concat(toFileName(eventTime, schema));
    }



    private String getApplicationLabel(Schema schema) {
        return applicationLabel.getOrDefault(schema, "ֹֹUNKNOWN_APPLICATION");
    }



    /********************************************************************************************
     * bucket/acme/NetWitness/123456789012/NetworkTraffic/us-east-1/2019/12/10/
     * <bucket>/<tenant>/NetWitness/<Account>/<Application>/<Region>/year/month/day/<Filename>
     ********************************************************************************************/

    private String toPath(Instant eventTime, Schema schema) {
        LocalDateTime time = LocalDateTime.ofInstant(eventTime, UTC);

        return tenant.concat("/")
                .concat("NetWitness").concat("/")
                .concat(account).concat("/")
                .concat(getApplicationLabel(schema)).concat("/")
                .concat(region).concat("/")
                .concat(String.valueOf(time.getYear())).concat("/")
                .concat(String.valueOf(time.getMonthValue())).concat("/")
                .concat(String.valueOf(time.getDayOfMonth())).concat("/");
    }



    /********************************************************************************************
     * <Account>_<Region>_<Application>_<Timestamp>_<Unique>.json.gz
     * 123456789012_us-east-1_NetworkTraffic_20180620T1620Z_fe123456.json.gz
     ********************************************************************************************/

    private String toFileName(Instant eventTime, Schema schema) {
        return account.concat("_")
                .concat(region).concat("_")
                .concat(getApplicationLabel(schema)).concat("_")
                .concat(toFileTimestamp(eventTime)).concat("_")
                .concat(generateUnique()).concat("_")
                .concat(".json.gz");
    }

    //  is the minute after the latest record in the file
    private String toFileTimestamp(Instant eventTime) {
        DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("YYYYMMDD'T'HHmm'Z'").withZone(UTC);
        LocalDateTime time = LocalDateTime.ofInstant(eventTime, UTC);
        int nearestMinute = (int) Math.ceil(time.plusMinutes(1).getMinute() / 5d) * 5;
        nearestMinute = (nearestMinute == 60) ? 0 : nearestMinute;
        LocalDateTime timestamp = time.withMinute(nearestMinute);
        return DATE_TIME_FORMATTER.format(timestamp);
    }

    private String generateUnique() {
        return "00000000";
    }


}
