package com.rsa.netwitness.presidio.automation.s3;

import fortscale.common.general.Schema;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static com.rsa.netwitness.presidio.automation.config.AWS_Config.S3_CONFIG;
import static java.time.ZoneOffset.UTC;

public class S3_Key {
    private String bucket = S3_CONFIG.getBucket();
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

    private String getApplicationLabel(Schema schema) {
        return S3_CONFIG.applicationLabels.getOrDefault(schema, "ֹֹUNKNOWN_APPLICATION");
    }

    /********************************************************************************************
     *  bucket/acme/NetWitness/NetworkTraffic/us-east-1/2019/12/10/
     * <bucket>/<tenant>/NetWitness/<Application>/<Region>/year/month/day/<Filename>
     ********************************************************************************************/

    private String toPath(Instant interval, Schema schema) {
        LocalDateTime intervalDate = LocalDateTime.ofInstant(interval, UTC);

        return tenant.concat("/")
                .concat(S3_CONFIG.netwitness).concat("/")
                .concat(getApplicationLabel(schema)).concat("/")
                .concat(region).concat("/")
                .concat(String.valueOf(intervalDate.getYear())).concat("/")
                .concat(String.valueOf(String.format("%02d" , intervalDate.getMonthValue()))).concat("/")
                .concat(String.valueOf(String.format("%02d" , intervalDate.getDayOfMonth()))).concat("/");
    }


    /********************************************************************************************
     * <Account>_<Region>_<Application>_<Timestamp>_<Unique>.json.gz
     * 123456789012_us-east-1_NetworkTraffic_20180620T1620Z_0.json.gz
     ********************************************************************************************/

    // 123456789012_us-east-1_NetworkTraffic_20180620T1620Z
    private String toFileNamePrefix(Instant interval, Schema schema) {
        return tenant.concat("_")
                .concat(region).concat("_")
                .concat(getApplicationLabel(schema)).concat("_")
                .concat(toFileTimestamp(interval));
    }

    //  is the minute after the latest record in the file
    private String toFileTimestamp(Instant interval) {
        LocalDateTime intervalDate = LocalDateTime.ofInstant(interval, UTC);
        DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmm'Z'").withZone(UTC);
        return DATE_TIME_FORMATTER.format(intervalDate);
    }

    private String defaultUnique() {
        return "0";
    }


}
