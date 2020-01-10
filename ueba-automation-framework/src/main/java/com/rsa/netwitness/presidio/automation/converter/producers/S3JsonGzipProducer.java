package com.rsa.netwitness.presidio.automation.converter.producers;

import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.Upload;
import com.rsa.netwitness.presidio.automation.converter.events.NetwitnessEvent;
import com.rsa.netwitness.presidio.automation.converter.formatters.EventFormatter;
import com.rsa.netwitness.presidio.automation.s3.S3_Bucket;
import com.rsa.netwitness.presidio.automation.s3.S3_Helper;
import com.rsa.netwitness.presidio.automation.s3.S3_Key;
import com.rsa.netwitness.presidio.automation.utils.common.Lazy;
import fortscale.common.general.Schema;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;
import java.util.zip.GZIPOutputStream;

import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.*;
import static org.assertj.core.api.Assertions.assertThat;

public class S3JsonGzipProducer implements EventsProducer<NetwitnessEvent> {
    private static String bucket = "ueba-qa-data";

    private S3_Key keyGen = new S3_Key();
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
                .collect(groupingBy(keyGen.key));

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

    private void uploadEmptyFile(String key) {
        PutObjectRequest putObjectRequest = new PutObjectRequest(S3JsonGzipProducer.bucket, key, S3_Helper.EMPTY_FILE);
        try {
            transferManager.upload(putObjectRequest).waitForUploadResult();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
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
