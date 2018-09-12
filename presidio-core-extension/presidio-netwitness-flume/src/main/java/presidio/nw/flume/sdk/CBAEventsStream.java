package presidio.nw.flume.sdk;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.rsa.netwitness.cba.api.S3DataIterator;
import fortscale.common.general.Schema;
import fortscale.domain.core.AbstractDocument;
import org.flume.source.sdk.EventsStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.Instant;
import java.util.Map;

import static java.util.Objects.requireNonNull;

/**
 * This class is a modification of {@link NetwitnessEventsStream}, which streams events from an Amazon S3 bucket
 * instead of a Netwitness Core device. the underlying mechanism is an {@link S3DataIterator}.
 *
 * @author Abhinav Iyappan
 * @since 11.3
 */
public class CBAEventsStream implements EventsStream {

    private static final Logger logger = LoggerFactory.getLogger(NetwitnessEventsStream.class);

    private S3DataIterator iterator;
    private Schema schema;
    private long count = 0;

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
    public void startStreaming(Schema schema, Instant startDate, Instant endDate, Map<String, String> config) {
        this.schema = schema;
        this.count = 0;
        AmazonS3 s3 = AmazonS3ClientBuilder.defaultClient();
        validateConfiguration(config);
        iterator = new S3DataIterator(s3, config, startDate, endDate);
        logger.info("Iterator {} instantiated", iterator);
    }

    /**
     * Is the iterator finished ?
     *
     * @return false is iterator is finished, true otherwise
     */
    @Override
    public boolean hasNext() {
        return iterator.hasNext();
    }

    /**
     * Get the next event from S3.
     *
     * @return an AbstractDocument
     */
    @Override
    public AbstractDocument next() {

        AbstractDocument document = null;
        Map<String, Object> event = iterator.next();
        if (event != null) {
            document = NetwitnessDocumentBuilder.getInstance().buildDocument(schema, event);
            count++;
            logger.debug("CBA document: {}", document);
        }
        return document;
    }

    /**
     * Close the {@link S3DataIterator}, and print a count of events read so far.
     */
    @Override
    public void stopStreaming() {
        if (iterator != null) {
            try {
                iterator.close();
            }
            catch (IOException e) {
                logger.error("Error while closing iterator. {}", e);
            }
            logger.info("No. of records read from current S3 source: {}", count);
        }

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
