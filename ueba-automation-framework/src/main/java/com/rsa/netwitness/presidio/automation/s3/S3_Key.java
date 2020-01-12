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

import static fortscale.common.general.Schema.*;
import static java.time.ZoneOffset.UTC;
import static java.time.temporal.ChronoUnit.MINUTES;

public class S3_Key {

    private static final Number UPLOAD_INTERVAL_MINUTES = 5;
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

    public Function<NetwitnessEvent, String> application = e -> getApplicationLabel(e.schema);

    public String key(Instant eventTime, Schema schema) {
        return toPath(eventTime, schema).concat(toFileName(eventTime, schema));
    }

    public Set<String> getAllS3_Keys(Instant from, Instant to, Schema schema) {
        LocalDateTime time = LocalDateTime.ofInstant(from.minus(UPLOAD_INTERVAL_MINUTES.intValue(), MINUTES), UTC);
        int nearestMinute = getNearestMinute(time);
        Instant firstInterval = time.withMinute(nearestMinute).toInstant(UTC);

        long between = MINUTES.between(firstInterval, to.plus(UPLOAD_INTERVAL_MINUTES.intValue(), MINUTES));
        long numOfIntervals = between / UPLOAD_INTERVAL_MINUTES.longValue();

        Stream<Instant> localDateTimeStream = LongStream.rangeClosed(0, numOfIntervals).parallel()
                .boxed()
                .map(i -> firstInterval.plus(UPLOAD_INTERVAL_MINUTES.longValue() * i, MINUTES));

        return localDateTimeStream.parallel().map(e -> key(e, schema)).collect(Collectors.toSet());
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
                .concat(String.valueOf(String.format("%02d" , time.getMonthValue()))).concat("/")
                .concat(String.valueOf(String.format("%02d" , time.getDayOfMonth()))).concat("/");
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
        int nearestMinute = getNearestMinute(time);
        LocalDateTime timestamp = time.withMinute(nearestMinute);
        return DATE_TIME_FORMATTER.format(timestamp);
    }

    private int getNearestMinute(LocalDateTime time) {
        /* plusMinutes(1) is required put minutes equal to (UPLOAD_INTERVAL_MINUTES * n) into the next chunk */
        int nearestMinute = (int) Math.ceil(time.plusMinutes(1).getMinute() / UPLOAD_INTERVAL_MINUTES.doubleValue()) * UPLOAD_INTERVAL_MINUTES.intValue();
        nearestMinute = (nearestMinute == 60) ? 0 : nearestMinute;
        return nearestMinute;
    }

    private String generateUnique() {
        return "00000000";
    }


}
