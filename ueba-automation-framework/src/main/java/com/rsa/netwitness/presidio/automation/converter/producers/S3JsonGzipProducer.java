package com.rsa.netwitness.presidio.automation.converter.producers;

import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.TransferManagerBuilder;
import com.amazonaws.services.s3.transfer.Upload;
import com.google.common.collect.ImmutableMap;
import com.rsa.netwitness.presidio.automation.converter.events.NetwitnessEvent;
import com.rsa.netwitness.presidio.automation.converter.formatters.EventFormatter;
import com.rsa.netwitness.presidio.automation.s3.S3_Bucket;
import com.rsa.netwitness.presidio.automation.s3.S3_Client;
import fortscale.common.general.Schema;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;
import java.util.zip.GZIPOutputStream;

import static fortscale.common.general.Schema.*;
import static java.time.ZoneOffset.UTC;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.*;

public class S3JsonGzipProducer implements EventsProducer<NetwitnessEvent> {
    private static String bucket = "ueba-qa-data";
    private static String tenant = "acme";
    private static String account = "123456789012";
    private static String region = "us-east-1";

    private static ImmutableMap<Schema, String> application =  new ImmutableMap.Builder<Schema, String>()
            .put(TLS, "NetworkTraffic")
            .put(ACTIVE_DIRECTORY, "ActiveDirectory")
            .put(AUTHENTICATION, "Authentication")
            .put(FILE, "File")
            .put(PROCESS, "Process")
            .put(REGISTRY, "Registry")
            .build();

    private TransferManager xfer_mgr = TransferManagerBuilder.standard()
            .withS3Client(S3_Client.s3Client)
            .build();

    private final EventFormatter<NetwitnessEvent,String> formatter;

    S3JsonGzipProducer(EventFormatter<NetwitnessEvent, String> formatter){
        requireNonNull(formatter);
        this.formatter = formatter;
    }


    @Override
    public Map<Schema, Long> send(Stream<NetwitnessEvent> eventsList) {
        new S3_Bucket(bucket).truncate();

        List<NetwitnessEvent> events = eventsList.parallel().collect(toList());

        Map<String, List<NetwitnessEvent>> eventsByKey = events.parallelStream()
                .collect(groupingBy(keys));

        eventsByKey.entrySet().parallelStream()
                .forEach(entry -> upload(eventsByKey.get(entry.getKey()), entry.getKey()));

        return events.parallelStream().collect(groupingBy(e -> e.schema, counting()));
    }

    private void upload(List<NetwitnessEvent> eventsByKey, String key) {
        Stream<String> stringStream = eventsByKey.parallelStream().map(formatter::format);
        byte[] zippedBytes = gzipSerializer(stringStream);
        upload(key, zippedBytes);
        System.out.println(eventsByKey.size() + " " + key);
    }

    private Upload upload(String key, byte[] zippedBytes) {
        ObjectMetadata omd = new ObjectMetadata();
        omd.setContentLength(zippedBytes.length);
        omd.setContentType("application/octet-stream");

        return xfer_mgr.upload(S3JsonGzipProducer.bucket,
                key,
                new ByteArrayInputStream(zippedBytes),
                omd);
    }

    private byte[] gzipSerializer(Stream<String> lines) {
        byte[] bytesToWrite;
        bytesToWrite = lines.collect(joining()).getBytes();
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        GZIPOutputStream gzipOut;

        try {
            gzipOut = new GZIPOutputStream(byteOut);
            gzipOut.write(bytesToWrite, 0, bytesToWrite.length);
            gzipOut.flush();
            gzipOut.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return byteOut.toByteArray();
    }


    private Function<NetwitnessEvent, String> keys = e -> toPath(e).concat(toFileName(e));

    //  bucket/acme/NetWitness/123456789012/NetworkTraffic/us-east-1/2019/12/10/
    //  <bucket>/<tenant>/NetWitness/<Account>/<Application>/<Region>/year/month/day/<Filename>
    private String toPath(NetwitnessEvent event) {
        Instant eventTime = event.eventTimeEpoch;
        LocalDateTime time = LocalDateTime.ofInstant(eventTime, UTC);

        return tenant.concat("/")
                .concat("NetWitness").concat("/")
                .concat(account).concat("/")
                .concat(application.getOrDefault(event.schema, "unknown")).concat("/")
                .concat(region).concat("/")
                .concat(String.valueOf(time.getYear())).concat("/")
                .concat(String.valueOf(time.getMonthValue())).concat("/")
                .concat(String.valueOf(time.getDayOfMonth())).concat("/");
    }

    // <Account>_<Region>_<Application>_<Timestamp>_<Unique>.json.gz
    // 123456789012_us-east-1_NetworkTraffic_20180620T1620Z_fe123456.json.gz
    private String toFileName(NetwitnessEvent event) {
        return account.concat("_")
                .concat(region).concat("_")
                .concat(application.getOrDefault(event.schema, "unknown")).concat("_")
                .concat(toFileTimestamp(event.eventTimeEpoch)).concat("_")
                .concat(generateUnique()).concat("_")
                .concat(".json.gz");
    }

    //  is the minute after the latest record in the file
    private String toFileTimestamp(Instant eventTime) {
        DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("YYYYMMDD'T'HHmm'Z'").withZone(UTC);
        LocalDateTime time = LocalDateTime.ofInstant(eventTime, UTC);
        int nearestMinute = (int) Math.ceil(time.plusNanos(1).getMinute() / 5d) * 5;
        nearestMinute = (nearestMinute == 60) ? 0 : nearestMinute;
        LocalDateTime timestamp = time.withMinute(nearestMinute);
        return DATE_TIME_FORMATTER.format(timestamp);
    }

    private String generateUnique() {
        return "00000000";
    }


}
