package com.rsa.netwitness.presidio.automation.converter.producers;

import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.Upload;
import com.rsa.netwitness.presidio.automation.converter.events.NetwitnessEvent;
import com.rsa.netwitness.presidio.automation.converter.formatters.EventFormatter;
import com.rsa.netwitness.presidio.automation.s3.S3_Bucket;
import com.rsa.netwitness.presidio.automation.s3.S3_Helper;
import com.rsa.netwitness.presidio.automation.utils.common.Lazy;
import fortscale.common.general.Schema;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Stream;
import java.util.zip.GZIPOutputStream;

import static java.time.ZoneOffset.UTC;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.*;
import static org.assertj.core.api.Assertions.assertThat;

public class S3JsonGzipProducer implements EventsProducer<NetwitnessEvent> {
    private static String bucket = "ueba-qa-data";
    private static String tenant = "acme";
    private static String account = "123456789012";
    private static String region = "us-east-1";

    private final S3_Helper s3_helper = new S3_Helper();
    private final TransferManager transferManager = s3_helper.getTransferManager();
    private final EventFormatter<NetwitnessEvent,String> formatter;
    private static Set<String> keysUploaded = new HashSet<>();
    private static volatile Lazy<Boolean> truncateFlag = new Lazy<>();

    S3JsonGzipProducer(EventFormatter<NetwitnessEvent, String> formatter){
        requireNonNull(formatter);
        this.formatter = formatter;
    }


    @Override
    public Map<Schema, Long> send(Stream<NetwitnessEvent> eventsList) {
        assertThat(truncateFlag.getOrCompute(() -> new S3_Bucket(bucket).truncate()))
        .as("Failed to truncate bucket " + bucket)
        .isTrue();

        List<NetwitnessEvent> events = eventsList.parallel().collect(toList());

        Map<String, List<NetwitnessEvent>> eventsByKey = events.parallelStream()
                .collect(groupingBy(keys));

        assertThat(keysUploaded)
                .overridingErrorMessage("Trying to upload same key twice")
                .doesNotContainAnyElementsOf(eventsByKey.keySet());

        eventsByKey.entrySet().parallelStream()
                .forEach(entry -> upload(eventsByKey.get(entry.getKey()), entry.getKey()));

        keysUploaded.addAll(eventsByKey.keySet());

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

        return transferManager.upload(S3JsonGzipProducer.bucket,
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
                .concat(s3_helper.getApplicationLabel(event.schema)).concat("/")
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
                .concat(s3_helper.getApplicationLabel(event.schema)).concat("_")
                .concat(toFileTimestamp(event.eventTimeEpoch)).concat("_")
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
