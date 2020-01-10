package com.rsa.netwitness.presidio.automation.s3;

import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.TransferManagerBuilder;
import com.google.common.collect.ImmutableMap;
import fortscale.common.general.Schema;

import static fortscale.common.general.Schema.*;
import static fortscale.common.general.Schema.REGISTRY;

public class S3_Helper {

    private static final ImmutableMap<Schema, String> applicationLabel =  new ImmutableMap.Builder<Schema, String>()
            .put(TLS, "NetworkTraffic")
            .put(ACTIVE_DIRECTORY, "ActiveDirectory")
            .put(AUTHENTICATION, "Authentication")
            .put(FILE, "File")
            .put(PROCESS, "Process")
            .put(REGISTRY, "Registry")
            .build();

    public TransferManager getTransferManager() {
         return TransferManagerBuilder.standard()
                .withS3Client(S3_Client.s3Client)
                .build();
    }

    public String getApplicationLabel(Schema schema) {
        return applicationLabel.getOrDefault(schema, "ֹֹUNKNOWN_APPLICATION");
    }




}
