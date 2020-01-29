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

enum S3_Client {
    S3_CLIENT;

    private static  Logger LOGGER = (Logger) LoggerFactory.getLogger(S3_Client.class);

    private Lazy<AmazonS3> amazonS3Lazy = new Lazy<>();

    static AmazonS3 s3Client = S3_CLIENT.amazonS3Lazy.getOrCompute(S3_CLIENT::connectToS3);

    private AmazonS3 connectToS3() {
        try {
            String region = S3_CONFIG.getRegion();
            if (S3_CONFIG.getAccessKey().isBlank() || S3_CONFIG.getSecretKey().isBlank()) {
                // AwsClientBuilder.EndpointConfiguration ec = new AwsClientBuilder.EndpointConfiguration("https://s3." + region + ".amazonaws.com", region);
                return AmazonS3ClientBuilder.defaultClient();
            } else {
                BasicAWSCredentials awsCreds = new BasicAWSCredentials(S3_CONFIG.getAccessKey(), S3_CONFIG.getSecretKey());
                return AmazonS3ClientBuilder.standard()
                        .withCredentials(new AWSStaticCredentialsProvider(awsCreds))
                        .withRegion(region)
                        .build();
            }

        } catch (AmazonServiceException e) {
            LOGGER.error("Failed to init s3Client");
            e.printStackTrace();
            return null;

        }
    }
}
