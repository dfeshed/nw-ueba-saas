package com.rsa.netwitness.presidio.automation.s3;

import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.TransferManagerBuilder;
import com.amazonaws.services.s3.transfer.Upload;
import com.amazonaws.services.s3.transfer.model.UploadResult;

import java.io.ByteArrayInputStream;

import static com.rsa.netwitness.presidio.automation.config.AWS_Config.S3_CONFIG;

public class S3_Helper {

    public TransferManager getTransferManager() {
         return TransferManagerBuilder.standard()
                .withS3Client(S3_Client.s3Client)
                .build();
    }

    public UploadResult upload(String key, byte[] zippedBytes) {
        ObjectMetadata omd = new ObjectMetadata();
        omd.setContentLength(zippedBytes.length);
        omd.setContentType("application/octet-stream");

        Upload upload = getTransferManager().upload(S3_CONFIG.bucket.get(),
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

}
