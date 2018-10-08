package presidio.nw.flume.sdk;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.rsa.netwitness.cba.api.S3DataIterator;
import fortscale.common.general.Schema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.Iterator;
import java.util.Map;

import static java.util.Objects.requireNonNull;

/**
 * This class is a modification of {@link NetwitnessEventsStream}, which streams events from an Amazon S3 bucket
 * instead of a Netwitness Core device. the underlying mechanism is an {@link S3DataIterator}.
 *
 * @author Abhinav Iyappan
 * @since 11.3
 */
public class CBAEventsStream extends AbstractNetwitnessEventsStream {

    private static final Logger logger = LoggerFactory.getLogger(NetwitnessEventsStream.class);

    /**
     * Instantiate the iterator for streaming. The config map MUST contain the following properties:
     * <ul>
     * <li>bucket: the name of the Amazon S3 bucket to read from</li>
     * <li>customerId: the GUID of the customer</li>
     * <li>schema: the name of the stream to read, eg. active_directory, file etc. NOTE: CASE-SENSITIVE</li>
     * </ul>
     *
     * @param schema    the data schema
     * @param startDate the start date of events to fetch
     * @param endDate   the end date of events to fetch
     * @param config    configuration paramters like bucket name, path of the event files etc.
     */
    @Override
    public Iterator<Map<String, Object>> iterator(Schema schema, Instant startDate, Instant endDate,
            Map<String, String> config) {
        validateConfiguration(config);
        AmazonS3 s3 = AmazonS3ClientBuilder.defaultClient();
        S3DataIterator iterator;
        try {
            iterator = new S3DataIterator(s3, config, startDate, endDate);
        }
        catch (Exception e) {
            logger.error("start streaming failed", e);
            throw new RuntimeException("start streaming failed", e);
        }
        return iterator;
    }

    /**
     * Check if bucket, schema and customerId config values are available.
     *
     * @param config the flume config map
     */
    private void validateConfiguration(Map<String, String> config) {
        requireNonNull(config.get("bucket"), "'bucket' is missing in configuration");
        requireNonNull(config.get("schema"), "'schema' is missing in configuration");
        requireNonNull(config.get("customerId"), "'customerId' is missing in configuration");
    }
}
