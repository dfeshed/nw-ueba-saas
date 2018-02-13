package org.flume.source.AWS_S3;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.*;
import fortscale.common.general.Schema;
import fortscale.domain.core.AbstractDocument;
import fortscale.utils.logging.Logger;
import org.apache.flume.Context;
import org.flume.source.csv.CsvFormatSource;
import org.flume.source.csv.domain.GenericRawEvent;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipInputStream;

/**
 * This Source read events from AWS S3 storage
 *
 */
public class PresidioAwsS3Source extends CsvFormatSource {

    private static final Logger logger = Logger.getLogger(PresidioAwsS3Source.class);
    protected static final String AWS_BUKCET_NAME = "bucketName";
    protected static final String AWS_KEY = "awsKey";
    protected static final String AWS_SECRET_KEY = "awsSecretKey";
    protected static final String AWS_REGION = "awsRegion";
    protected static final String TENANT_PREFIX = "tenantPrefix";
    protected static final String SCHEMA_PREFIX = "schemaPrefix";
    protected static final String IS_COMPRESSED = "";
    protected static final String STARTDATE_REGEXP = "startDateRegexp";
    protected static final String TS_IN_FILE_NAME_IS_SECOND ="tsInFileNameIsSecond";
    private static final String SKIP_THAT_HOUR = "skip" ;
    private static final String FILES_EXTENSIONS = "filesExtenstions";
    private static final String COMPRESSION_EXTENSION  = "compressionExtension";


    //From configuration
    private String bucketName;
    private String tenantPrefix;
    private String schemaPrefix;
    private boolean isCompressed;
    private boolean tsInFileNameIsSecond;
    private String compressionExtension;


    //build during doPresidioConfigure
    private BasicAWSCredentials awsCreds;
    private AmazonS3 s3Client;
    private String objectNameExtension;
    private Pattern startDatePattern;
    private String specificFilesToLook;
    private String objectPath;






    @SuppressWarnings("unchecked")
    public void doPresidioConfigure(Context context) {



        //Configure the specific S3 source properties
        try {
            super.doPresidioConfigure(context);
            logger.debug("context is: {}", context);
            setName("presidio-flume-aws-s3-source");
            bucketName = context.getString(AWS_BUKCET_NAME, "");
            String awsKey = context.getString(AWS_KEY, "");
            String awsSecretKey = context.getString(AWS_SECRET_KEY, "");
            String awsRegion = context.getString(AWS_REGION, "");
            tenantPrefix = context.getString(TENANT_PREFIX, "");
            schemaPrefix = context.getString(SCHEMA_PREFIX, "");
            isCompressed = context.getBoolean(IS_COMPRESSED, false);
            tsInFileNameIsSecond = context.getBoolean(TS_IN_FILE_NAME_IS_SECOND,true);
            String startDateRegexp = context.getString(STARTDATE_REGEXP, "");
            String filesExtenstions = context.getString(FILES_EXTENSIONS, "csv");
            compressionExtension = context.getString(COMPRESSION_EXTENSION, "zip");

            startDatePattern = Pattern.compile(startDateRegexp);

            if(isCompressed)
                objectNameExtension=compressionExtension;
            else
                objectNameExtension= filesExtenstions;

            specificFilesToLook = ".*" + tenantPrefix + "_" + schemaPrefix + ".*\\."+objectNameExtension;


            //create the S3 client instance
            this.awsCreds = new BasicAWSCredentials(awsKey, awsSecretKey);
            this.s3Client = AmazonS3ClientBuilder.standard()
                    .withCredentials(new AWSStaticCredentialsProvider(awsCreds)).withRegion(Regions.fromName(awsRegion))
                    .build();


            objectPath = getNextFilePath();

        } catch (Exception e) {
            logger.error("Error configuring AwsS3 Source!", e);
        }

    }


    /**
     * The method that populated the filePath - basically responsible to get the path of the next file
     * This implementation will be based on the list of objects in the S3 compare to the startDate of that execution (the logical data that should be processed)
     * In case we have Object on S3 that his logical start date is bigger or equal from the current logical execution we will assign at the filePath parameter that value
     *
     */
    @SuppressWarnings("unchecked")
    protected String getNextFilePath() {

        try {
            ListObjectsV2Result result = getListOfObjectsFromS3();

            List<S3ObjectSummary> objects = result.getObjectSummaries();

            //get teh minimal value of start date from the relevant list of files (based on tenant and schema name -
            //Remember that the file name convention is <tenantId>_<schema_name>_<logical start date>_<logical end date>.<extension>
            Optional<String> optionalMinStartDate = objects.stream().
                    filter(s -> s.getKey().matches(specificFilesToLook)).
                    map(s -> parseStartDateFromObjectName(s.getKey())).filter(this::isItRelevantFile).sorted().min(String.CASE_INSENSITIVE_ORDER);

            //If there is minimal value this is the potential next file to process
            //need to compare it to the given start date in case its bigger or equal to the given start sate
            //We need to work on that file
            if (optionalMinStartDate.isPresent()) {
                String minStartDate = optionalMinStartDate.get();
                long potentialFileStartDate = Long.valueOf(minStartDate);
                Instant potentialFileTS = tsInFileNameIsSecond ? Instant.ofEpochSecond(potentialFileStartDate) : Instant.ofEpochMilli(potentialFileStartDate);
                if (potentialFileTS.equals(startDate)) {
                    String fileRegexp = ".*" + tenantPrefix + "_" + schemaPrefix + "_" + minStartDate + ".*\\." + objectNameExtension;
                    Optional<S3ObjectSummary> s3ObjectSummaryOptional = objects.stream().filter(s -> s.getKey().matches(fileRegexp)).findFirst();
                    return s3ObjectSummaryOptional.isPresent() ? s3ObjectSummaryOptional.get().getKey() : null;

                }

            }
            return SKIP_THAT_HOUR;
        }
        catch (Exception e){
            logger.error("Exception occur during the decision of which is the next object to processed in Bucket : " + bucketName);
            logger.error("Exception Details: " + e);
        }   return null;


    }

    private String parseStartDateFromObjectName(String str )
    {
        Matcher matcher = this.startDatePattern.matcher(str);
        if (matcher.find())
        {
            return matcher.group();
        }
        return "";
    }

    private boolean isItRelevantFile(String filename)
    {
        return tsInFileNameIsSecond ? Long.valueOf(filename) >= startDate.getEpochSecond() : Long.valueOf(filename) >= startDate.toEpochMilli();
    }


    /**
     * This method handle the call for AWS for getting the list of objects from a given bucket name
     * @return -  ListObjectsV2Result - Result that contain List of S3ObjectsSummary
     */
    private ListObjectsV2Result getListOfObjectsFromS3() {

        ListObjectsV2Result result = null;
        try {
            logger.debug("Listing objects from bucket - " + bucketName);
            final ListObjectsV2Request req = new ListObjectsV2Request().withBucketName(bucketName);
            result =  this.s3Client.listObjectsV2(req);
        } catch (AmazonServiceException ase) {
            logger.error("Caught an AmazonServiceException, " +
                    "which means your request made it " +
                    "to Amazon S3, but was rejected with an error response " +
                    "for some reason.");
            logger.error("Error Message:    " + ase.getMessage());
            logger.error("HTTP Status Code: " + ase.getStatusCode());
            logger.error("AWS Error Code:   " + ase.getErrorCode());
            logger.error("Error Type:       " + ase.getErrorType());
            logger.error("Request ID:       " + ase.getRequestId());
        } catch (AmazonClientException ace) {
            logger.error("Caught an AmazonClientException, " +
                    "which means the client encountered " +
                    "an internal error while trying to communicate" +
                    " with S3, " +
                    "such as not being able to access the network.");
            logger.error("Error Message: " + ace.getMessage());
        }

        return result;
    }






    @SuppressWarnings("unchecked")
    protected List<AbstractDocument> doFetch(Schema schema,int pageNum) {
        List<GenericRawEvent> genericEvents;
        BufferedReader reader;

        if(objectPath == null){
            logger.warn("No object was define to be processed");
            return null;

        }

        //In case that we decide to skip this hour return empty list
        //Why should we skip this hour - In case that we have hole in the data and the next hour was arrived.
        if (objectPath.equals(SKIP_THAT_HOUR))
            return  new ArrayList<>();
        try{

            //Get the specific object from S3
            S3Object s3object = s3Client.getObject(new GetObjectRequest(
                    bucketName, objectPath));

            InputStream objectData = s3object.getObjectContent();


            if (isCompressed)
            {
                //Read the Zipped object from S3
                ZipInputStream zipStream =  new ZipInputStream(new BufferedInputStream(objectData));
                //Get the actual object
                zipStream.getNextEntry();

                //Create buffer reader for reading the content
                reader = new BufferedReader(new InputStreamReader(zipStream));
            }
            else
                reader = new BufferedReader(new InputStreamReader(objectData));

            genericEvents = getGenericRawEventsFromCsv(reader);
            return convertEvents(genericEvents);
        }

        catch (Exception e)
        {
            logger.error("Error during retrieving file from S3 for schema - " + schema + " from bucket - " + bucketName);
            logger.error("Exception Details: " + e);
            return null;
        }

    }

}
