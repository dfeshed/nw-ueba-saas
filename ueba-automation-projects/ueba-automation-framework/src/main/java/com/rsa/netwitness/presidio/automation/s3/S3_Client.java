package com.rsa.netwitness.presidio.automation.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.rsa.netwitness.presidio.automation.utils.common.Lazy;

enum S3_Client {
    S3_CLIENT;

    private Lazy<AmazonS3> amazonS3Lazy = new Lazy<>();

    static AmazonS3 s3Client = S3_CLIENT.amazonS3Lazy.getOrCompute(S3_CLIENT::get);

    private AmazonS3 get() {
        return AmazonS3ClientBuilder.defaultClient();
    }
}
