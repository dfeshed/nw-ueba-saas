package com.rsa.netwitness.presidio.automation.s3;

import ch.qos.logback.classic.Logger;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.rsa.netwitness.presidio.automation.utils.common.Lazy;
import org.slf4j.LoggerFactory;

import static com.rsa.netwitness.presidio.automation.config.AWS_Config.S3_CONFIG;

public class S3_Client {
    private static  Logger LOGGER = (Logger) LoggerFactory.getLogger(S3_Client.class);

    private static Lazy<AmazonS3> amazonS3Lazy = new Lazy<>();
    public static String region = "us-east-1";
    public static String accessKey = S3_CONFIG.accessKey;
    public static String secretKey = S3_CONFIG.secretKey;
    public static AmazonS3 s3Client = amazonS3Lazy.getOrCompute(S3_Client::connectToS3);

    private static AmazonS3 connectToS3() {
        BasicAWSCredentials awsCreds = new BasicAWSCredentials(accessKey, secretKey);
        try {
            s3Client = AmazonS3ClientBuilder.standard()
                    .withCredentials(new AWSStaticCredentialsProvider(awsCreds))
                    .withRegion(region)
                    .build();
        } catch (AmazonServiceException e) {
            LOGGER.error("Failed to init s3Client");
            e.printStackTrace();
        }
        return s3Client;
    }
}
