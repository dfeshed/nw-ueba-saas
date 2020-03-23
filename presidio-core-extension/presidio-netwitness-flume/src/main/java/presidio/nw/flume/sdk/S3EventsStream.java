package presidio.nw.flume.sdk;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.PredefinedClientConfigurations;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import fortscale.common.general.Schema;
import fortscale.common.s3.NWGatewayService;
import fortscale.common.s3.NetwitnessS3EventExtractor;
import fortscale.utils.s3.S3DataIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.Iterator;
import java.util.Map;

import static java.time.temporal.ChronoUnit.HOURS;
import static java.util.Objects.requireNonNull;

/**
 * This class is a modification of {@link NetwitnessEventsStream}, which streams events from an Amazon S3 bucket
 * instead of a Netwitness Core device. the underlying mechanism is an {@link S3DataIterator}.
 *
 * @author Yael Berger
 */
public class S3EventsStream extends AbstractNetwitnessEventsStream {

    private static final Logger logger = LoggerFactory.getLogger(S3EventsStream.class);

    /**
     * Instantiate the iterator for streaming. The config map MUST contain the following properties:
     * <ul>
     * <li>bucket: the name of the Amazon S3 bucket to read from</li>
     * <li>tenant: S3 object key prefix that separates records between tenants</li>
     * <li>account: aws account number</li>
     * <li>schema: the name of the stream to read, eg. active_directory, file etc. NOTE: CASE-SENSITIVE</li>
     * <li>region: AWS region to read from</li>
     *
     * </ul>
     *
     * The ClientConfiguration will handle retries. The default settings retrying requests for the following cases:
     * <ul>
     * <li>Retry on client exceptions caused by IOException
     * <li>Retry on service exceptions that are either 500 internal server
     *     errors, 503 service unavailable errors, service throttling errors or
     *     clock skew errors.
     *<ul>
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
        validateStartAndEndDate(startDate, endDate);
        String bucket = config.get("bucket");
        String tenant = config.get("tenant");
        String account = config.get("account");
        String region = config.get("region");
        String configSchema = config.get("schema");

        ClientConfiguration clientConfiguration = PredefinedClientConfigurations.defaultConfig();
        clientConfiguration.setMaxErrorRetry(10);
        AmazonS3 s3 = AmazonS3ClientBuilder.standard().withClientConfiguration(clientConfiguration).build();

        S3DataIterator iterator;

        try {
            NWGatewayService nwGatewayService = new NWGatewayService(bucket, tenant, account, region, s3);
            Iterator<S3ObjectSummary> objects = nwGatewayService.getObjectsByRange(startDate, endDate, configSchema);
            iterator = new S3DataIterator(s3, bucket, objects, new NetwitnessS3EventExtractor());
        }
        catch (Exception e) {
            logger.error("start streaming failed", e);
            throw new RuntimeException("start streaming failed", e);
        }
        return iterator;
    }

    /**
     * Check if bucket, tenant, account, schema and region config values are available.
     *
     * @param config the flume config map
     */
    private void validateConfiguration(Map<String, String> config) {
        requireNonNull(config.get("bucket"), "'bucket' is missing in configuration");
        requireNonNull(config.get("tenant"), "'tenant' is missing in configuration");
        requireNonNull(config.get("account"), "'account' is missing in configuration");
        requireNonNull(config.get("schema"), "'schema' is missing in configuration");
        requireNonNull(config.get("region"), "'region' is missing in configuration");
    }

    private void validateStartAndEndDate(Instant startDate, Instant endDate){
        if (!startDate.truncatedTo(HOURS).equals(startDate)) throw new RuntimeException("start time must be hour on the hour.");
        if (!endDate.truncatedTo(HOURS).equals(endDate)) throw new RuntimeException("end time must be hour on the hour.");
    }
}
