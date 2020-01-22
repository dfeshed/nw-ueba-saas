package com.rsa.netwitness.presidio.automation.s3;

import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.TransferManagerBuilder;

public class S3_Helper {

    public TransferManager getTransferManager() {
         return TransferManagerBuilder.standard()
                .withS3Client(S3_Client.s3Client)
                .build();
    }

}
