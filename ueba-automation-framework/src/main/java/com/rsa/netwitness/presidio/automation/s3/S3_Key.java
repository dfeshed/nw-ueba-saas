package com.rsa.netwitness.presidio.automation.s3;

import com.google.common.collect.ImmutableMap;
import com.rsa.netwitness.presidio.automation.converter.events.NetwitnessEvent;
import fortscale.common.general.Schema;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.LongStream;
import java.util.stream.Stream;

import static com.rsa.netwitness.presidio.automation.config.AWS_Config.S3_CONFIG;
import static fortscale.common.general.Schema.*;
import static java.time.ZoneOffset.UTC;
import static java.time.temporal.ChronoUnit.MINUTES;

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
    private String region = S3_CONFIG.getRegion();

    public S3_Key() { }


    public Function<NetwitnessEvent, String> key = e ->
            toPath(e.eventTimeEpoch, e.schema).concat(toFileName(e.eventTimeEpoch, e.schema));

    public Function<NetwitnessEvent, String> application = e -> getApplicationLabel(e.schema);


    public Set<String> getAllS3_Keys(Instant from, Instant to, Schema schema) {
        long between = MINUTES.between(getRelatedTimeInterval(from), getRelatedTimeInterval(to));
        long numOfIntervals = between / S3_CONFIG.UPLOAD_INTERVAL_MINUTES.longValue();

        Stream<Instant> localDateTimeStream = LongStream.rangeClosed(0, numOfIntervals).parallel()
                .boxed()
                .map(i -> from.plus(S3_CONFIG.UPLOAD_INTERVAL_MINUTES.longValue() * i, MINUTES));

        return localDateTimeStream.parallel().map(e -> key(e, schema)).collect(Collectors.toSet());
    }


    private String getApplicationLabel(Schema schema) {
        return applicationLabel.getOrDefault(schema, "ֹֹUNKNOWN_APPLICATION");
    }

    private String key(Instant eventTime, Schema schema) {
        return toPath(eventTime, schema).concat(toFileName(eventTime, schema));
    }

    /********************************************************************************************
     * bucket/acme/NetWitness/123456789012/NetworkTraffic/us-east-1/2019/12/10/
     * <bucket>/<tenant>/NetWitness/<Account>/<Application>/<Region>/year/month/day/<Filename>
     ********************************************************************************************/

    private String toPath(Instant eventTime, Schema schema) {
        LocalDateTime nearestMinute = getRelatedTimeInterval(eventTime);

        return tenant.concat("/")
                .concat("NetWitness").concat("/")
                .concat(account).concat("/")
                .concat(getApplicationLabel(schema)).concat("/")
                .concat(region).concat("/")
                .concat(String.valueOf(nearestMinute.getYear())).concat("/")
                .concat(String.valueOf(String.format("%02d" , nearestMinute.getMonthValue()))).concat("/")
                .concat(String.valueOf(String.format("%02d" , nearestMinute.getDayOfMonth()))).concat("/");
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
        LocalDateTime nearestMinute = getRelatedTimeInterval(eventTime);
        DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("YYYYMMDD'T'HHmm'Z'").withZone(UTC);
        return DATE_TIME_FORMATTER.format(nearestMinute);
    }

    private LocalDateTime getRelatedTimeInterval(Instant toConvert) {
        LocalDateTime time = LocalDateTime.ofInstant(toConvert, UTC).plusMinutes(S3_CONFIG.UPLOAD_INTERVAL_MINUTES.intValue());

        /* plusMinutes(1) is required put minutes equal to (UPLOAD_INTERVAL_MINUTES * n) into the next chunk */
        int nearestMinute = (int) Math.floor(time.getMinute() / S3_CONFIG.UPLOAD_INTERVAL_MINUTES.doubleValue()) * S3_CONFIG.UPLOAD_INTERVAL_MINUTES.intValue();
        return (nearestMinute == 60) ? time.plusHours(1).withSecond(0).withMinute(0) : time.withSecond(0).withMinute(nearestMinute);
    }

    private String generateUnique() {
        return "00000000";
    }


}
