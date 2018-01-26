package org.flume.source.AWS_S3;

import fortscale.domain.core.AbstractDocument;
import fortscale.utils.logging.Logger;
import org.apache.flume.Context;
import org.apache.flume.conf.Configurable;
import org.flume.source.AbstractPageablePresidioSource;
import org.flume.source.csv.CsvSourceAgile;

import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * This Source read events from AWS S3 storage
 *
 */
public class PresidioAwsS3Source extends CsvSourceAgile {

    private static final Logger logger = Logger.getLogger(PresidioAwsS3Source.class);
    protected static final String FILE_PATH_CONF_NAME = "filePath";
    protected static final String AWS_BUKCET_NAME = "bucketName";
    protected static final String AWS_KEY = "awsKey";
    protected static final String AWS_SECRET_KEY = "awsSecretKey";
    protected static final String TENANT_PREFIX = "tenantPrefix";
    protected static final String SCHEMA_PREFIX = "schemaPrefix";



    @Override
    public void doPresidioConfigure(Context context) {

        super.doPresidioConfigure(context);
        logger.debug("context is: {}", context);
        setName("presidio-flume-aws-s3-source");


        // TODO - get the specific s3 configuration

    }


    /**
     * The method that populated the filePath - basically responsible to get the path of the next file
     * @param context
     */
    protected void getNextFile(Context context) {

    }





    @Override
    protected List<AbstractDocument> doFetch(int pageNum) {

        //TODO - Use S3 sdk to get the right object and stream it

        //TODO - use the CsvToBean to parse the line from the object and translate it to AuthenticationRawEvent


        return null;
    }

}
