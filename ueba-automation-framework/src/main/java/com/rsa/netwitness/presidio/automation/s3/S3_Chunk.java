package com.rsa.netwitness.presidio.automation.s3;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPOutputStream;

import static com.rsa.netwitness.presidio.automation.config.AWS_Config.S3_CONFIG;

public class S3_Chunk {

    private static final int FILE_MAX_EVENTS = 100000;
    private final Instant firstSample;
    private final Instant lastSample;
    private String bucketName = S3_CONFIG.getBucket();

    private final String path;
    private int partNumber = 1;
    private int fileUniqueId = 0;
    private int size = 0;
    private int total = 0;

    private InitiateMultipartUploadResult initResponse;
    private InitiateMultipartUploadRequest initRequest;
    private List<PartETag> partETags = new ArrayList<>();

    public S3_Chunk(Instant firstSample, Instant lastSample, String path) {
        this.firstSample = firstSample;
        this.lastSample = lastSample;
        this.path = path;

        initRequest = new InitiateMultipartUploadRequest(bucketName, addSuffix(path));
        initResponse = S3_Client.s3Client.initiateMultipartUpload(initRequest);
    }

    public void process(List<String> events) {
        String keyName = addSuffix(path);
        total += events.size();

        ByteArrayInputStream byteArrayInputStream = toStream(events);

        UploadPartRequest uploadRequest = new UploadPartRequest()
                .withBucketName(bucketName)
                .withKey(keyName)
                .withUploadId(initResponse.getUploadId())
                .withPartNumber(partNumber++)
                .withInputStream(byteArrayInputStream)
                .withPartSize(gzipSerializer(events).length);


        UploadPartResult uploadResult = S3_Client.s3Client.uploadPart(uploadRequest);
        partETags.add(uploadResult.getPartETag());
    }

    public void completeAll() {
        if (total > 0) {
            System.out.println( addSuffix(path));
            System.out.println(total);

            CompleteMultipartUploadRequest compRequest = new CompleteMultipartUploadRequest(bucketName,  addSuffix(path),
                    initResponse.getUploadId(), partETags);

            S3_Client.s3Client.completeMultipartUpload(compRequest);
        } else {
            System.out.println( "Empty: " + addSuffix(path));
        }

    }

    private String addSuffix(String path) {
        return path.concat("_")
                .concat(String.valueOf(fileUniqueId)).concat("_")
                .concat(".json.gz");
    }

    private ByteArrayInputStream toStream(List<String> stringStream) {
        byte[] zippedBytes = gzipSerializer(stringStream);
        return new ByteArrayInputStream(zippedBytes);
    }

    private byte[] gzipSerializer(List<String> lines) {
        byte[] bytesToWrite;
        bytesToWrite = String.join("", lines).getBytes();
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

    @Override
    public String toString() {
        return path;
    }








    public static void main(String[] args) throws IOException {

        String filePath = "*** Path to file to upload ***";

        File file = new File("C:\\Users\\feshed\\Downloads\\1-1PG2111329.zip");
        long contentLength = file.length();
        long partSize = 100 * 1024; // Set part size to 100K

        String keyName = "upload/test/1-1PG2111329.zip";
        String bucketName = "ueba-qa-team";


        try {
            AmazonS3 s3Client = S3_Client.s3Client;

            // Create a list of ETag objects. You retrieve ETags for each object part uploaded,
            // then, after each individual part has been uploaded, pass the list of ETags to
            // the request to complete the upload.
            List<PartETag> partETags = new ArrayList<PartETag>();

            // Initiate the multipart upload.
            InitiateMultipartUploadRequest initRequest = new InitiateMultipartUploadRequest(bucketName, keyName);
            InitiateMultipartUploadResult initResponse = s3Client.initiateMultipartUpload(initRequest);

            // Upload the file parts.
            long filePosition = 0;
            for (int i = 1; filePosition < contentLength; i++) {
                // Because the last part could be less than 5 MB, adjust the part size as needed.
                partSize = Math.min(partSize, (contentLength - filePosition));


                // Create the request to upload a part.
                UploadPartRequest uploadRequest = new UploadPartRequest()
                        .withBucketName(bucketName)
                        .withKey(keyName)
                        .withUploadId(initResponse.getUploadId())
                        .withPartNumber(i)
                        .withFileOffset(filePosition)
                        .withFile(file)
                        .withPartSize(partSize);

                // Upload the part and add the response's ETag to our list.
                UploadPartResult uploadResult = s3Client.uploadPart(uploadRequest);
                partETags.add(uploadResult.getPartETag());

                filePosition += partSize;
            }

            // Complete the multipart upload.
            CompleteMultipartUploadRequest compRequest = new CompleteMultipartUploadRequest(bucketName, keyName,
                    initResponse.getUploadId(), partETags);
            s3Client.completeMultipartUpload(compRequest);
        } catch (AmazonServiceException e) {
            // The call was transmitted successfully, but Amazon S3 couldn't process
            // it, so it returned an error response.
            e.printStackTrace();
        } catch (SdkClientException e) {
            // Amazon S3 couldn't be contacted for a response, or the client
            // couldn't parse the response from Amazon S3.
            e.printStackTrace();
        }
    }

    public Instant getFirstSample() {
        return firstSample;
    }
}
