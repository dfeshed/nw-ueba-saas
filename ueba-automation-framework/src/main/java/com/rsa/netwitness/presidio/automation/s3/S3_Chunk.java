package com.rsa.netwitness.presidio.automation.s3;

import com.amazonaws.services.s3.model.PartETag;
import com.amazonaws.services.s3.transfer.model.UploadResult;
import com.rsa.netwitness.presidio.automation.converter.producers.stream_converters.GzipStreamConverter;
import com.rsa.netwitness.presidio.automation.converter.producers.stream_converters.ProducerStreamConverter;
import fortscale.common.general.Schema;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.function.UnaryOperator;

import static com.rsa.netwitness.presidio.automation.config.AWS_Config.S3_CONFIG;
import static com.rsa.netwitness.presidio.automation.config.AWS_Config.UPLOAD_INTERVAL_MINUTES;
import static java.lang.Math.min;
import static java.time.ZoneOffset.UTC;

public class S3_Chunk implements Comparable<S3_Chunk> {

    private static final int FILE_MAX_EVENTS = 100000;


    private final Instant interval;
    private final Schema schema;
    private final S3_Key keyGen = new S3_Key();
    private String bucketName = S3_CONFIG.getBucket();
    private ProducerStreamConverter streamConverter = new GzipStreamConverter();
    private List<String> cache = new LinkedList<>();

    private int fileUniqueId = 1;
    private final String keyBeginningPart;

    private List<PartETag> partETags = new ArrayList<>();

    public S3_Chunk(Instant interval, Schema schema) {
        this.interval = interval;
        this.schema = schema;
        this.keyBeginningPart = keyGen.getKeyBeginningPart(interval, schema);
    }



    public static UnaryOperator<Instant> toChunkInterval = sampleTime -> {
        LocalDateTime time = LocalDateTime.ofInstant(sampleTime, UTC).plusMinutes(UPLOAD_INTERVAL_MINUTES.intValue());

        /* plusMinutes(1) is required put minutes equal to (UPLOAD_INTERVAL_MINUTES * n) into the next chunk */
        int nearestMinute = (int) Math.floor(time.getMinute() / UPLOAD_INTERVAL_MINUTES.doubleValue()) * UPLOAD_INTERVAL_MINUTES.intValue();
        LocalDateTime result = (nearestMinute == 60)
                ? time.plusHours(1).withSecond(0).withMinute(0) : time.withSecond(0).withMinute(nearestMinute);

        return result.toInstant(UTC);
    };


    @Override
    public int compareTo(S3_Chunk chunk) {
        return this.interval.compareTo(chunk.interval);
    }

    public Instant getInterval() {
        return interval;
    }


    public void process(List<String> events) {
        if (events.size() + cache.size() < FILE_MAX_EVENTS) {
            cache.addAll(events);
        } else {
            List<String> residue;

            do {
                residue = events.subList(0, FILE_MAX_EVENTS - cache.size() -1);
                events.removeAll(residue);
                cache.addAll(residue);
                uploadCache();
                cache.clear();
                cache.addAll(events.subList(0, min(events.size()-1, FILE_MAX_EVENTS- 1)));
                events.removeAll(cache);
            } while (events.size() < FILE_MAX_EVENTS);


        }
    }

    public void close() {
        if (cache.isEmpty()) {
            uploadEmpty();
        } else {
            uploadCache();
        }
    }

    private UploadResult uploadCache() {
     return null;
    }

    private UploadResult uploadEmpty() {
        return null;
    }



    private String addSuffix() {
        return keyBeginningPart.concat("_")
                .concat(String.valueOf(fileUniqueId)).concat("_")
                .concat(".json.gz");
    }


    @Override
    public String toString() {
        return keyBeginningPart;
    }








}
