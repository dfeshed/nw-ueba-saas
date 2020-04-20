package com.rsa.netwitness.presidio.automation.s3;

import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.TransferManagerBuilder;
import com.amazonaws.services.s3.transfer.Upload;
import com.rsa.netwitness.presidio.automation.config.AWS_Config;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

import static com.rsa.netwitness.presidio.automation.config.AWS_Config.S3_CONFIG;
import static com.rsa.netwitness.presidio.automation.config.AWS_Config.UPLOAD_INTERVAL_MINUTES;
import static java.time.ZoneOffset.UTC;
import static java.time.temporal.ChronoUnit.MINUTES;

public class S3_Helper {

    void upload(String key, byte[] zippedBytes) {
        ObjectMetadata omd = new ObjectMetadata();
        omd.setContentLength(zippedBytes.length);
        omd.setContentType("application/octet-stream");

        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(zippedBytes)) {
            Upload upload = getTransferManager().upload(S3_CONFIG.getBucket(),
                    key,
                    byteArrayInputStream,
                    omd);
            upload.waitForCompletion();
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }
    }


    public List<Instant> divideToIntervals(Instant start, Instant end) {
        long between = MINUTES.between(start, end);
        long numOfIntervals = between / AWS_Config.UPLOAD_INTERVAL_MINUTES.longValue();

        return LongStream.rangeClosed(0, numOfIntervals).parallel()
                .boxed()
                .map(i -> start.plus(AWS_Config.UPLOAD_INTERVAL_MINUTES.longValue() * i, MINUTES))
                .collect(Collectors.toList());
    }

    public static UnaryOperator<Instant> toChunkInterval = eventTime -> {
        LocalDateTime time = LocalDateTime.ofInstant(eventTime, UTC).plusMinutes(UPLOAD_INTERVAL_MINUTES.intValue());

        /* plusMinutes(1) is required put minutes equal to (UPLOAD_INTERVAL_MINUTES * n) into the next chunk */
        int nearestMinute = (int) Math.floor(time.getMinute() / UPLOAD_INTERVAL_MINUTES.doubleValue()) * UPLOAD_INTERVAL_MINUTES.intValue();

        LocalDateTime result = (nearestMinute == 60)
                ? time.plusHours(1).withNano(0).withSecond(0).withMinute(0)
                : time.withNano(0).withSecond(0).withMinute(nearestMinute);

        return result.toInstant(UTC);
    };


    private TransferManager getTransferManager() {
        return TransferManagerBuilder.defaultTransferManager();
    }
}
