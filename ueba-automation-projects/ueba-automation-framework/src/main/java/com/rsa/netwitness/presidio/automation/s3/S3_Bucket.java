package com.rsa.netwitness.presidio.automation.s3;

import ch.qos.logback.classic.Logger;
import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.model.*;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

import static com.rsa.netwitness.presidio.automation.s3.S3_Client.s3Client;

public class S3_Bucket {
    private static  Logger LOGGER = (Logger) LoggerFactory.getLogger(S3_Bucket.class);

    private final String bucketName;

    public S3_Bucket(String bucketName) {
        this.bucketName = bucketName;
    }

    public void create() {
        s3Client.createBucket(bucketName);
    }

    public Boolean delete() {
        if (exist()) {
            truncate();
            s3Client.deleteBucket(bucketName);
        }
        return Boolean.TRUE;
    }

    public Boolean truncate() {
        if (exist()) {
            try {

                // Delete all objects from the bucket. This is sufficient
                // for unversioned buckets. For versioned buckets, when you attempt to delete objects, Amazon S3 inserts
                // delete markers for all objects, but doesn't delete the object versions.
                // To delete objects from versioned buckets, delete all of the object versions before deleting
                // the bucket (see below for an example).

                ObjectListing objectListing = s3Client.listObjects(bucketName);

                List<DeleteObjectsRequest.KeyVersion> keysToDelete = objectListing.getObjectSummaries().parallelStream()
                        .map(e -> new DeleteObjectsRequest.KeyVersion(e.getKey()))
                        .collect(Collectors.toList());

                if (keysToDelete.isEmpty()) {
                    return Boolean.TRUE;
                }


                DeleteObjectsRequest deleteObjectsRequest = new DeleteObjectsRequest(bucketName)
                        .withKeys(keysToDelete);

                System.out.println("   ---------------------------------------------");
                System.out.println("   Going to DELETE all objects from bucket '" + bucketName + "'");
                System.out.println("   ---------------------------------------------");

                DeleteObjectsResult deleteObjectsResult = s3Client.deleteObjects(deleteObjectsRequest);
                LOGGER.debug("Deleted objects:");
                deleteObjectsResult.getDeletedObjects().stream().map(DeleteObjectsResult.DeletedObject::getKey).forEach(e -> LOGGER.debug(e));

                // Delete all object versions (required for versioned buckets).
                VersionListing versionList = s3Client.listVersions(new ListVersionsRequest().withBucketName(bucketName));
                while (true) {
                    for (S3VersionSummary vs : versionList.getVersionSummaries()) {
                        s3Client.deleteVersion(bucketName, vs.getKey(), vs.getVersionId());
                    }

                    if (versionList.isTruncated()) {
                        versionList = s3Client.listNextBatchOfVersions(versionList);
                    } else {
                        break;
                    }
                }

                return Boolean.TRUE;

            } catch (SdkClientException e) {
                // The call was transmitted successfully, but Amazon S3 couldn't process
                // it, so it returned an error response.
                e.printStackTrace();
            }   // Amazon S3 couldn't be contacted for a response, or the client couldn't
                // parse the response from Amazon S3.
            return Boolean.FALSE;
        }

        return Boolean.TRUE;
    }

    private boolean exist() {
        return s3Client.doesBucketExistV2(bucketName);
    }


}