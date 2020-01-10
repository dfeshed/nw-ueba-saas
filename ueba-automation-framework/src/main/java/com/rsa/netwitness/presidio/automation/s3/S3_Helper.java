package com.rsa.netwitness.presidio.automation.s3;

import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.TransferManagerBuilder;
import com.google.common.collect.ImmutableMap;
import fortscale.common.general.Schema;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

import static fortscale.common.general.Schema.*;

public class S3_Helper {

    public static final File EMPTY_FILE = getEmptyFile();

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

    private static File getEmptyFile() {
        File file = null;
        try {
            file = File.createTempFile("empty", ".tmp");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Objects.requireNonNull(file);
    }

}
