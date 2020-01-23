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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;
import java.util.zip.GZIPOutputStream;

import static com.rsa.netwitness.presidio.automation.config.AWS_Config.S3_CONFIG;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.*;
import static org.assertj.core.api.Assertions.assertThat;

public class S3JsonGzipChunksProducer implements EventsProducer<NetwitnessEvent> {
    private static Logger LOGGER = (Logger) LoggerFactory.getLogger(S3JsonGzipChunksProducer.class);
    private static String bucket = S3_CONFIG.bucket.get();

    private S3_Key keyGen = new S3_Key();
    private final S3_Helper s3_helper = new S3_Helper();
    private final TransferManager transferManager = s3_helper.getTransferManager();
    private final EventFormatter<NetwitnessEvent,String> formatter;
    private static Set<String> keysUploaded = new ConcurrentHashSet<>();
    private static volatile Lazy<Boolean> truncateFlag = new Lazy<>();
    private long total = 0;

    S3JsonGzipChunksProducer(EventFormatter<NetwitnessEvent, String> formatter){
        requireNonNull(formatter);
        this.formatter = formatter;
    }


    @Override
    public Map<Schema, Long> send(Stream<NetwitnessEvent> eventsList) {
        List<NetwitnessEvent> events = eventsList.parallel().collect(toList());
        List<Schema> schema = events.parallelStream().map(e -> e.schema).distinct().collect(toList());
        assertThat(schema).as("same schema").hasSize(1);
        Instant firstSample = events.parallelStream().map(e -> e.eventTimeEpoch).min(Instant::compareTo).orElseThrow();
        Instant lastSample = events.parallelStream().map(e -> e.eventTimeEpoch).max(Instant::compareTo).orElseThrow();

//        Set<S3_Chunk> allS3_batches = keyGen.getAllS3_Batches(firstSample, lastSample, schema.get(0), bucket);
//
//        Map<String, List<NetwitnessEvent>> eventsByBatch = events
//                .parallelStream()
//                .collect(groupingBy(keyGen.batch));
//
//
//
//
//
//
//
//        // save last for next time
//
//
//        allS3_batches.forEach();
//
//
//        initS3Chunks(firstSample, schema.get(0));
//
//        Map<String, List<NetwitnessEvent>> eventsByChunk = events.parallelStream().collect(groupingBy(keyGen.batch));
//
//        assertThat(s3Chunks.keySet()).as("keys out of range").containsAll(eventsByChunk.keySet());
//
//        eventsByChunk.entrySet().parallelStream()
//                .forEach( e -> s3Chunks.get(e.getKey()).process(e.getValue().parallelStream().map(formatter::format).collect(toList())));

        return new HashMap<>();
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

        Upload upload = transferManager.upload(S3JsonGzipChunksProducer.bucket,
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
