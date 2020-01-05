package com.rsa.netwitness.presidio.automation.converter.producers;

import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.TransferManagerBuilder;
import com.amazonaws.services.s3.transfer.Upload;
import com.amazonaws.services.s3.transfer.model.UploadResult;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rsa.netwitness.presidio.automation.converter.events.NetwitnessEvent;
import com.rsa.netwitness.presidio.automation.s3.S3_Bucket;
import com.rsa.netwitness.presidio.automation.s3.S3_Client;
import fortscale.common.general.Schema;
import org.testng.collections.Maps;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.zip.GZIPOutputStream;

import static java.util.stream.Collectors.groupingBy;

public class S3JsonGzipProducer implements EventsProducer<List<NetwitnessEvent>> {
    private static String bucketName = "ueba-qa-data";
    private Map<Schema, String> folders = Maps.newHashMap();
    private static final long GEN_START_TIME = System.currentTimeMillis();


    @Override
    public Map<Schema, Long> send(List<NetwitnessEvent> eventsList) {

        S3_Bucket bucket = new S3_Bucket(bucketName);
        bucket.truncate();

        TransferManager xfer_mgr = TransferManagerBuilder.standard()
                .withS3Client(S3_Client.s3Client)
                .build();


        // add destination file path
        Map<String, List<NetwitnessEvent>> eventsByKey = eventsList.parallelStream()
                .collect(groupingBy(e -> eventFilePath(e, ChronoUnit.DAYS)));


        for (String key : eventsByKey.keySet()) {
            byte[] bytesToWrite;

            ObjectMapper objectMapper = new ObjectMapper();

            String lines = eventsByKey.get(key).stream()
                    .map(e -> {
                        try {
                            return objectMapper.writeValueAsString(e);
                        } catch (JsonProcessingException ex) {
                            ex.printStackTrace();
                        }
                        return null;
                    }).collect(Collectors.joining("\n"));

            bytesToWrite = lines.getBytes();

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

            byte[] zippedBytes = byteOut.toByteArray();

            ObjectMetadata omd = new ObjectMetadata();
            omd.setContentLength(zippedBytes.length);
            omd.setContentType("application/octet-stream");

            Upload upload = xfer_mgr.upload(bucketName,
                    key,
                    new ByteArrayInputStream(zippedBytes),
                    omd);

            try {
                UploadResult uploadResult = upload.waitForUploadResult();
                System.out.println(eventsByKey.get(key).size());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }





        return null;
    }




    private Instant getEventTime(NetwitnessEvent event, ChronoUnit truncatedTo) {
        return event.eventTimeEpoch.truncatedTo(truncatedTo);
    }



    private String eventFilePath(NetwitnessEvent event, ChronoUnit truncatedTo) {
        String eventFolder = eventFolderName.apply(event);
        folders.putIfAbsent(event.schema, eventFolder);
        String fileName = event.schema + "_" + instantToString(getEventTime(event, truncatedTo));
        return eventFolder + "/" + fileName;
    }

    private String instantToString(Instant instant){
        return instant.toString().replaceAll(":","_");
    }

    private Function<NetwitnessEvent, String> eventFolderName = event ->
            event.schema.getName().concat("_").concat(instantToString(Instant.ofEpochMilli(GEN_START_TIME)));


}
