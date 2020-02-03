package com.rsa.netwitness.presidio.automation.s3;

import com.google.common.collect.ImmutableMap;
import com.rsa.netwitness.presidio.automation.converter.events.NetwitnessEvent;
import fortscale.common.general.Schema;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.function.Function;

import static com.rsa.netwitness.presidio.automation.config.AWS_Config.S3_CONFIG;
import static fortscale.common.general.Schema.*;
import static java.time.ZoneOffset.UTC;

public class S3_Key {

    private static final ImmutableMap<Schema, String> applicationLabel = new ImmutableMap.Builder<Schema, String>()
            .put(TLS, "TLS")
            .put(ACTIVE_DIRECTORY, "ACTIVE_DIRECTORY")
            .put(AUTHENTICATION, "AUTHENTICATION")
            .put(FILE, "FILE")
            .put(PROCESS, "PROCESS")
            .put(REGISTRY, "REGISTRY")
            .build();

    private String tenant = S3_CONFIG.getTenant();
    private String account = S3_CONFIG.getAccount();
    private String region = S3_Client.s3Client.getRegionName();

    public S3_Key() { }

    /** key common part */
    String getKeyBeginningPart(Instant interval, Schema schema) {
        return toPath(interval, schema).concat(toFileNamePrefix(interval, schema));
    }

    /** key unique part */
    String getKeyEndPart(int fileUniqueId) {
        return "_".concat(String.valueOf(fileUniqueId)).concat(".json.gz");
    }

    /** key with default index */
    public Function<NetwitnessEvent, String> key = e -> S3_Helper.toChunkInterval
                    .andThen(interval -> toPath(interval, e.schema).concat(toFileName(interval, e.schema)))
                    .apply(e.eventTimeEpoch);



    private String getApplicationLabel(Schema schema) {
        return applicationLabel.getOrDefault(schema, "ֹֹUNKNOWN_APPLICATION");
    }

    /********************************************************************************************
     * bucket/acme/NetWitness/123456789012/NetworkTraffic/us-east-1/2019/12/10/
     * <bucket>/<tenant>/NetWitness/<Account>/<Application>/<Region>/year/month/day/<Filename>
     ********************************************************************************************/

    private String toPath(Instant interval, Schema schema) {
        LocalDateTime intervalDate = LocalDateTime.ofInstant(interval, UTC);

        return tenant.concat("/")
                .concat("NetWitness").concat("/")
                .concat(account).concat("/")
                .concat(getApplicationLabel(schema)).concat("/")
                .concat(region).concat("/")
                .concat(String.valueOf(intervalDate.getYear())).concat("/")
                .concat(String.valueOf(String.format("%02d" , intervalDate.getMonthValue()))).concat("/")
                .concat(String.valueOf(String.format("%02d" , intervalDate.getDayOfMonth()))).concat("/");
    }


    /********************************************************************************************
     * <Account>_<Region>_<Application>_<Timestamp>_<Unique>.json.gz
     * 123456789012_us-east-1_NetworkTraffic_20180620T1620Z_fe123456.json.gz
     ********************************************************************************************/

    private String toFileName(Instant interval, Schema schema) {
        return account.concat("_")
                .concat(region).concat("_")
                .concat(getApplicationLabel(schema)).concat("_")
                .concat(toFileTimestamp(interval)).concat("_")
                .concat(defaultUnique())
                .concat(".json.gz");
    }

    // 123456789012_us-east-1_NetworkTraffic_20180620T1620Z
    private String toFileNamePrefix(Instant interval, Schema schema) {
        return account.concat("_")
                .concat(region).concat("_")
                .concat(getApplicationLabel(schema)).concat("_")
                .concat(toFileTimestamp(interval));
    }

    //  is the minute after the latest record in the file
    private String toFileTimestamp(Instant interval) {
        LocalDateTime intervalDate = LocalDateTime.ofInstant(interval, UTC);
        DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("YYYYMMDD'T'HHmm'Z'").withZone(UTC);
        return DATE_TIME_FORMATTER.format(intervalDate);
    }

    private String defaultUnique() {
        return "0";
    }


}
