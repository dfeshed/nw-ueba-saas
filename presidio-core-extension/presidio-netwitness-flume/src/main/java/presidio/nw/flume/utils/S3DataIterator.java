package presidio.nw.flume.utils;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.iterable.S3Objects;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import static java.time.temporal.ChronoUnit.HOURS;
import static java.util.Objects.requireNonNull;

/**
 * An iterator that steps through events in a given time range. It makes the following assumptions.
 *
 * <ul>
 * <li>The files are gzipped new-line delimited JSON files</li>
 * <li>the S3 keys are in the following format: streamPrefix/YYYY/MM/DD/HH/foobar.json.gz</li>
 * </ul>
 *
 * @author Abhinav Iyappan
 * @since 0.3
 */
public class S3DataIterator implements Iterator<Map<String, Object>>, Closeable {

    private static final Logger logger = LoggerFactory.getLogger(S3DataIterator.class);
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final TypeReference<HashMap<String, Object>> TYPE = new TypeReference<HashMap<String, Object>>() {
    };

    private final AmazonS3 s3;
    private final String bucket;
    private final String streamPrefix;

    // STATEFUL FIELDS
    private Iterator<String> folderIterator;

    private CloseableIterator lineIterator;

    private Iterator<S3ObjectSummary> fileIterator;

    /**
     * Internal constructor. Requires knowledge of the streamPrefix format, which is an implementation detail.
     * The current implementation rounds the start time down, and the endTime up. ie.
     * * <ul>
     * * <li>2:00 to 3:00 fetches events from 2:00 to 2:59</li>
     * * <li>2:30 to 3:30 fetches events from 2:00 to 3:59</li>
     * * </ul>
     *
     * @param s3           an AmazonS3Client that is set up with access to the bucket paths provided.
     * @param bucket       the S3 bucket to read from
     * @param streamPrefix the first part of the path in the S3 bucket
     * @param startTime    the start time of the events to stream.
     * @param endTime      the end time of the events to stream.
     */
    public S3DataIterator(AmazonS3 s3, String bucket, String streamPrefix, Instant startTime, Instant endTime) {
        this.s3 = s3;
        this.bucket = bucket;
        this.streamPrefix = streamPrefix;

        Instant startTimeRoundedDown = startTime.truncatedTo(HOURS);
        Instant endTimeRoundedUp = endTime.minusNanos(1).plusSeconds(3600).truncatedTo(HOURS);
        initPathIterator(startTimeRoundedDown, endTimeRoundedUp);
        lineIterator = CloseableIterator.empty();
        fileIterator = Collections.<S3ObjectSummary>emptyList().iterator();
        nextFile();
    }

    /**
     * Default constructor.
     *
     * @param s3        an AmazonS3Client that is set up with access to the bucket paths provided.
     * @param params    map that should contain bucket, customerId and schema parameters.
     * @param startTime the start time of the events to stream.
     * @param endTime   the end time of the events to stream.
     */
    public S3DataIterator(AmazonS3 s3, Map<String, String> params, Instant startTime,
                          Instant endTime) {
        this(
                s3,
                requireNonNull(params.get("bucket"), "Bucket name is missing"),
                formStreamPrefix(params),
                startTime,
                endTime
        );
    }

    /**
     * Generates the streamPrefix string from tenant, schema and region value in the parameter map.
     *
     * @param params the parameter config map
     * @return the streamPrefix.
     */
    private static String formStreamPrefix(Map<String, String> params) {
        String tenant = requireNonNull(params.get("tenant"), "tenant is missing");
        String account = requireNonNull(params.get("account"), "account is missing");
        String schema = requireNonNull(params.get("schema"), "schema is missing");
        String region = requireNonNull(params.get("region"), "region is missing");
        return tenant + "/NetWitness/" + account + "/" +  schema + "/" + region + "/";
    }

    /**
     * returns true, if there are any events left to iterate through. This method is also responsible for logic to jump
     * through files.
     *
     * @return true, if any events left
     */
    @Override
    public boolean hasNext() {
        // if current file is empty
        if (!lineIterator.hasNext() && (fileIterator.hasNext() || folderIterator.hasNext())) {
            // but we still have remaining files/folders, so iterate to a non-empty file, or to the end
            nextFile();
        }
        return lineIterator.hasNext();
    }

    /**
     * This method is also responsible for handling the cross-file and cross-folder boundaries regarding iterator setup.
     *
     * @return the next line of the file iterator
     */
    @Override
    public Map<String, Object> next() {
        try {
            return MAPPER.readValue(lineIterator.next(), TYPE);
        }
        catch (IOException e) {
            logger.warn(e.getMessage());
            logger.debug(e.getMessage(), e);
            return Collections.emptyMap();
        }
    }

    @Override
    public String toString() {
        return String.format("S3DataIterator{bucket='%s', streamPrefix='%s'}", bucket, streamPrefix);
    }

    @Override
    public void close() throws IOException {
        if (lineIterator != null) {
            lineIterator.close();
        }
    }

    /**
     * recurse to the next non-empty file, stepping through folders as necessary.
     */
    private void nextFile() {
        try {
            // remaining files in the current folder
            if (fileIterator.hasNext()) {
                // close the current file's connection and read the next one
                lineIterator.close();
                lineIterator = readS3File(fileIterator.next().getKey());
                // if this file is empty, recurse !
                if (!lineIterator.hasNext()) {
                    nextFile();
                }
            }
            // current folder is finished
            else {
                // but others folders are left, so go to next folder and then read files
                if (folderIterator.hasNext()) {
                    nextFolder();
                    nextFile();
                }
                else {
                    // no more files or folders left, close the last file
                    lineIterator.close();
                }
            }
        }
        catch (IOException e) {
            logger.warn(e.getMessage());
            logger.debug(e.getMessage(), e);
        }
    }

    /**
     * recurse to a hour-folder with non-zero # of files.
     */
    private void nextFolder() {
        // if any folders remaining
        if (folderIterator.hasNext()) {
            // get next folder
            fileIterator = S3Objects.withPrefix(s3, bucket, folderIterator.next()).iterator();
            // recursive till we reach a non-empty folder
            if (!fileIterator.hasNext()) {
                nextFolder();
            }
        }
    }

    /**
     * Instantiates a list containing the paths from which to pull the events in the given time-range.
     *
     * @param startTime the start time of the events to pull
     * @param endTime   the end time of the events to pull
     */
    private void initPathIterator(Instant startTime, Instant endTime) {
        List<String> hours = new ArrayList<>();
        logger.info("Fetching events from inclusive {} to exclusive {}.", startTime, endTime);
        for (Instant time = startTime; time.isBefore(endTime); time = time.plusSeconds(3600)) {
            hours.add(streamPrefix + generateHourSuffix(time));
        }
        folderIterator = hours.iterator();
    }

    /**
     * Generates the time-part of the path in S3. This is in the YYYY/MM/DD/HH format. Along with the customer
     * and schema prefix, a object key would look like customer_id/prep/FILE/2018/12/31/15/events1.json.gz
     *
     * @param date an instant in time
     * @return the time-part of the key prefix
     */
    private String generateHourSuffix(Instant date) {
        ZonedDateTime dateTime = ZonedDateTime.ofInstant(date, ZoneId.of("UTC"));
        return String.format("%1$tY/%1$tm/%1$td/%1$tH", dateTime);
    }

    /**
     * Read a file from S3, a provide access to them as an iterator of lines.
     *
     * @param filePath the key of the file to read
     * @return An iterator to a List containing the lines of the file as {@link String}s
     */
    private CloseableIterator readS3File(String filePath) {
        try {
            GZIPInputStream gzip = new GZIPInputStream(s3.getObject(bucket, filePath).getObjectContent());
            BufferedReader reader = new BufferedReader(new InputStreamReader(gzip));
            return new CloseableIterator(reader);
        }
        catch (IOException e) {
            logger.warn(e.getMessage());
            logger.debug(e.getMessage(), e);
            return CloseableIterator.empty();
        }
    }

    /**
     * This class iterates through lines of a BufferedReader and allows the reader to be closed on completion.
     */
    private static final class CloseableIterator implements Iterator<String>, Closeable {

        private Iterator<String> iter;
        private BufferedReader reader;

        private CloseableIterator(BufferedReader reader) {
            if (reader != null) {
                this.iter = reader.lines().iterator();
                this.reader = reader;
            }
            else {
                iter = Collections.<String>emptyList().iterator();
            }
        }

        private static CloseableIterator empty() {
            return new CloseableIterator(null);
        }

        @Override
        public void close() throws IOException {
            iter = Collections.<String>emptyList().iterator();
            if (reader != null) {
                reader.close();
            }
        }

        @Override
        public boolean hasNext() {
            return iter.hasNext();
        }

        @Override
        public String next() {
            return iter.next();
        }
    }
}
