package com.rsa.netwitness.presidio.automation.converter.producers;

import ch.qos.logback.classic.Logger;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.Upload;
import com.amazonaws.services.s3.transfer.model.UploadResult;
import com.rsa.netwitness.presidio.automation.converter.events.NetwitnessEvent;
import com.rsa.netwitness.presidio.automation.converter.formatters.EventFormatter;
import com.rsa.netwitness.presidio.automation.s3.S3_Bucket;
import com.rsa.netwitness.presidio.automation.s3.S3_Helper;
import com.rsa.netwitness.presidio.automation.s3.S3_Key;
import com.rsa.netwitness.presidio.automation.utils.common.Lazy;
import fortscale.common.general.Schema;
import org.apache.mina.util.ConcurrentHashSet;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;
import java.util.zip.GZIPOutputStream;

import static com.rsa.netwitness.presidio.automation.config.AWS_Config.S3_CONFIG;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.*;
import static org.assertj.core.api.Assertions.assertThat;

public class S3JsonGzipBucketsProducer implements EventsProducer<NetwitnessEvent> {
    private static Logger LOGGER = (Logger) LoggerFactory.getLogger(S3JsonGzipBucketsProducer.class);
    private static String bucket = S3_CONFIG.getBucket();

    private S3_Key keyGen = new S3_Key();
    private final S3_Helper s3_helper = new S3_Helper();
    private final TransferManager transferManager = s3_helper.getTransferManager();
    private final EventFormatter<NetwitnessEvent,String> formatter;
    private static Set<String> keysUploaded = new ConcurrentHashSet<>();
    private static volatile Lazy<Boolean> truncateFlag = new Lazy<>();
    private long total = 0;

    S3JsonGzipBucketsProducer(EventFormatter<NetwitnessEvent, String> formatter){
        requireNonNull(formatter);
        this.formatter = formatter;
    }


    @Override
    public Map<Schema, Long> send(Stream<NetwitnessEvent> eventsList) {
        // truncateBucketOnInit();

        List<NetwitnessEvent> events = eventsList.parallel().collect(toList());

        Map<String, List<NetwitnessEvent>> eventsByKey = events.parallelStream()
                .collect(groupingBy(keyGen.key));

        LOGGER.info("+++ Amount of keys = " + eventsByKey.keySet().size());

        assertThat(keysUploaded)
                .overridingErrorMessage("Trying to upload same key twice")
                .doesNotContainAnyElementsOf(eventsByKey.keySet());

        LOGGER.info("Starting to upload the data to S3");
        eventsByKey.entrySet().parallelStream()
                .forEach(entry -> System.out.println(upload(eventsByKey.get(entry.getKey()), entry.getKey()).getKey()));
        LOGGER.info("Data upload finished.");

        keysUploaded.addAll(eventsByKey.keySet());

        uploadEmptyFilesForMissingTimeSlots(events);

        LOGGER.info("TOTAL EVENTS=" + total);
        assertThat(total).as("Events count mismatch").isEqualTo(events.size());

        return events.parallelStream().collect(groupingBy(e -> e.schema, counting()));
    }







    private void truncateBucketOnInit() {
        assertThat(truncateFlag.getOrCompute(() -> new S3_Bucket(bucket).truncate()))
                .as("Failed to truncate bucket " + bucket)
                .isTrue();
    }

    private UploadResult upload(List<NetwitnessEvent> eventsByKey, String key) {
        System.out.print(eventsByKey.size() + " ");
        Stream<String> stringStream = eventsByKey.parallelStream().map(formatter::format);
        byte[] zippedBytes = gzipSerializer(stringStream);
        total += eventsByKey.size();
        return upload(key, zippedBytes);
    }

    private UploadResult upload(String key, byte[] zippedBytes) {
        ObjectMetadata omd = new ObjectMetadata();
        omd.setContentLength(zippedBytes.length);
        omd.setContentType("application/octet-stream");

        Upload upload = transferManager.upload(S3JsonGzipBucketsProducer.bucket,
                key,
                new ByteArrayInputStream(zippedBytes),
                omd);
        try {
            return upload.waitForUploadResult();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void uploadEmptyFilesForMissingTimeSlots(List<NetwitnessEvent> events) {
        Map<String, List<NetwitnessEvent>> eventsByApplication = events.parallelStream().collect(groupingBy(keyGen.application));
        LOGGER.info("Amount of empty files=" + eventsByApplication);
        eventsByApplication.forEach( (app, values) -> {
            Instant max = eventsByApplication.get(app).parallelStream().map(e -> e.eventTimeEpoch).max(Instant::compareTo).orElseThrow();
            Instant min = eventsByApplication.get(app).parallelStream().map(e -> e.eventTimeEpoch).min(Instant::compareTo).orElseThrow();

            System.out.println("Min=" + min + " Max=" + max);
            Set<String> missingKeys = keyGen.getAllS3_Keys(min, max, values.get(0).schema);
            missingKeys.removeAll(keysUploaded);

            LOGGER.info("Going to upload" + missingKeys.size() + " empty files. Application = " + app);
            missingKeys.parallelStream().forEach(e -> System.out.println(uploadEmptyFile(e).getKey()));
            LOGGER.info("Empty files upload finished. Application = " + app);
        });
    }

    private UploadResult uploadEmptyFile(String key) {
        return upload(key, gzipSerializer(Stream.of("")));
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


}
