package presidio.nw.flume.sdk;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import fortscale.common.general.Schema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import presidio.nw.flume.utils.S3DataIterator;

import java.time.Instant;
import java.util.Iterator;
import java.util.Map;

import static java.util.Objects.requireNonNull;

/**
 * This class is a modification of {@link NetwitnessEventsStream}, which streams events from an Amazon S3 bucket
 * instead of a Netwitness Core device. the underlying mechanism is an {@link S3DataIterator}.
 *
 * @author Yael Berger
 */
public class S3EventsStream extends AbstractNetwitnessEventsStream {

    private static final Logger logger = LoggerFactory.getLogger(NetwitnessEventsStream.class);

    /**
     * Instantiate the iterator for streaming. The config map MUST contain the following properties:
     * <ul>
     * <li>accessKey: the access key for AWS</li>
     * <li>secretKey: the secret key for AWS</li>
     * <li>bucket: the name of the Amazon S3 bucket to read from</li>
     * <li>tenant: S3 object key prefix that separates records between tenants</li>
     * <li>account: aws account number</li>
     * <li>schema: the name of the stream to read, eg. active_directory, file etc. NOTE: CASE-SENSITIVE</li>
     * <li>region: AWS region to read from</li>
     *
     * </ul>
     *
     * @param schema    the data schema
     * @param startDate the start date of events to fetch
     * @param endDate   the end date of events to fetch
     * @param config    configuration parameters like bucket name, path of the event files etc.
     */
    @Override
    public Iterator<Map<String, Object>> iterator(Schema schema, Instant startDate, Instant endDate,
            Map<String, String> config) {

        validateConfiguration(config);
        String accessKey = config.get("accessKey");
        String secretKey = config.get("secretKey");
        String region = config.get("region");
        String bucket = config.get("bucket");

        BasicAWSCredentials awsCredentials = new BasicAWSCredentials(accessKey, secretKey);
        AmazonS3 s3 = AmazonS3ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
                .withRegion(region)
                .build();

        S3DataIterator iterator;
        try {
            iterator = new S3DataIterator(s3, bucket, formStreamPrefix(config), startDate, endDate);
        }
        catch (Exception e) {
            logger.error("start streaming failed", e);
            throw new RuntimeException("start streaming failed", e);
        }
        return iterator;
    }

    /**
     * Check if accessKey, secretKey, bucket, tenant, account, schema and region config values are available.
     *
     * @param config the flume config map
     */
    private void validateConfiguration(Map<String, String> config) {
        requireNonNull(config.get("accessKey"), "'accessKey' is missing in configuration");
        requireNonNull(config.get("secretKey"), "'secretKey' is missing in configuration");
        requireNonNull(config.get("bucket"), "'bucket' is missing in configuration");
        requireNonNull(config.get("tenant"), "'tenant' is missing in configuration");
        requireNonNull(config.get("account"), "'account' is missing in configuration");
        requireNonNull(config.get("schema"), "'schema' is missing in configuration");
        requireNonNull(config.get("region"), "'region' is missing in configuration");
    }

    /**
     * Generates the streamPrefix string from tenant, schema and region value in the parameter map.
     *
     * @param params the parameter config map
     * @return the streamPrefix.
     */
    private String formStreamPrefix(Map<String, String> params) {
        String tenant = requireNonNull(params.get("tenant"), "tenant is missing");
        String account = requireNonNull(params.get("account"), "account is missing");
        String schema = requireNonNull(params.get("schema"), "schema is missing");
        String region = requireNonNull(params.get("region"), "region is missing");
        return tenant + "/NetWitness/" + account + "/" +  schema + "/" + region + "/";
    }
}
