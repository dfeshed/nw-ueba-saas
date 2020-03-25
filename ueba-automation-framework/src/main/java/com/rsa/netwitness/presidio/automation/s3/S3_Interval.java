package com.rsa.netwitness.presidio.automation.s3;

import ch.qos.logback.classic.Logger;
import com.amazonaws.services.s3.transfer.model.UploadResult;
import com.rsa.netwitness.presidio.automation.converter.producers.stream_converters.GzipStreamConverter;
import fortscale.common.general.Schema;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.LinkedList;
import java.util.List;

public class S3_Interval implements Comparable<S3_Interval> {
    private static final int FILE_MAX_EVENTS = 100000;
    private static Logger LOGGER = (Logger) LoggerFactory.getLogger(S3_Interval.class);
    private final Instant interval;
    private final S3_Key keyGen = new S3_Key();
    private final String keyBeginningPart;
    private GzipStreamConverter streamConverter = new GzipStreamConverter();
    private List<String> cache = new LinkedList<>();
    private int fileUniqueId = 0;
    private int totalUploaded = 0;

    public S3_Interval(Instant interval, Schema schema) {
        this.interval = interval;
        this.keyBeginningPart = keyGen.getKeyBeginningPart(interval, schema);
    }

    public Instant getInterval() {
        return interval;
    }


    public void process(List<String> events) {
        if (events.size() + cache.size() <= FILE_MAX_EVENTS) {
            cache.addAll(events);
        } else {
            do {
                int gapToFillTheCache = Math.min(events.size(), FILE_MAX_EVENTS - cache.size());
                cache.addAll(events.subList(0, gapToFillTheCache));
                events = events.subList(FILE_MAX_EVENTS - cache.size(), events.size());
                uploadCache();
            } while (events.size() > FILE_MAX_EVENTS);
        }
    }

    public void close() {
        if (cache.isEmpty()) {
            if (fileUniqueId == 0) {
                uploadEmpty();
            }
        } else {
            uploadCache();
        }
    }

    private UploadResult uploadCache() {
        S3_Helper helper = new S3_Helper();
        byte[] bytes = streamConverter.convert(cache);
        UploadResult upload = helper.upload(keyBeginningPart.concat(keyGen.getKeyEndPart(fileUniqueId)), bytes);
        int uploadedCurrently = (totalUploaded < 0) ? 0 : cache.size();
        fileUniqueId++;
        totalUploaded += cache.size();
        LOGGER.info(uploadedCurrently + " " + upload.getKey());

        cache.clear();
        return upload;
    }

    private UploadResult uploadEmpty() {
        totalUploaded = -1;
        cache.add("{\"records\":[]}");
        return uploadCache();
    }

    @Override
    public String toString() {
        return keyBeginningPart;
    }

    @Override
    public int compareTo(S3_Interval interval) {
        return this.interval.compareTo(interval.interval);
    }

    public int getTotalUploaded() {
        return totalUploaded;
    }
}
