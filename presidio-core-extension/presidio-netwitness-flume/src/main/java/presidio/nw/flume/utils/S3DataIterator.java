package presidio.nw.flume.utils;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.ListObjectsV2Request;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.TimeZone;
import java.util.stream.Collectors;
import java.util.zip.GZIPInputStream;

import static java.time.temporal.ChronoUnit.DAYS;
import static java.time.temporal.ChronoUnit.HOURS;

/**
 * An iterator that steps through events in a given time range. It makes the following assumptions.
 *
 * <ul>
 * <li>The files are gzipped new-line delimited JSON files</li>
 * <li>the S3 keys are in the following format:
 * <tenant>/NetWitness/<account>/<schema>/<region>/year/month/day/<Account>_<Region>_<Application>_<Timestamp>_<Unique>.json.gz</li>
 * </ul>
 *
 * @author Yael Berger
 */
public class S3DataIterator implements Iterator<Map<String, Object>>, Closeable {

    private static final Logger logger = LoggerFactory.getLogger(S3DataIterator.class);
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final TypeReference<HashMap<String, Object>> TYPE = new TypeReference<HashMap<String, Object>>() {
    };
    private final static String DEFAULT_DATE_FORMAT = "yyyyMMdd'T'HHmm'Z'";
    private final static String DATE_REGEX_FORMAT = ".*_(20\\d{6}T\\d{4}Z)_.*";


    private final AmazonS3 s3;
    private final String bucket;
    private final String streamPrefix;

    private Instant startTime;

    private Instant endTime;

    private Comparator<S3ObjectSummary> defaultS3ObjectSummaryComparator = Comparator.comparing(S3ObjectSummary::getKey);

    // STATEFUL FIELDS
    private Iterator<String> folderIterator;

    private BufferReaderIterator lineIterator;

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

        this.startTime = startTime.truncatedTo(HOURS);
        this.endTime = endTime.minusNanos(1).plusSeconds(3600).truncatedTo(HOURS);

        initFolderIterator();
        lineIterator = BufferReaderIterator.empty();
        fileIterator = Collections.emptyIterator();
        nextFile();
    }

    /**
     * returns true, if there are any events left to iterate through. This method is also responsible for logic to jump
     * through files.
     *
     * @return true, if any events left
     */
    @Override
    public boolean hasNext() {
        try {
            // if current file is empty
            if (!lineIterator.hasNext() && (fileIterator.hasNext() || folderIterator.hasNext())) {
                // but we still have remaining files/folders, so iterate to a non-empty file, or to the end
                nextFile();
            }
            return lineIterator.hasNext();
        } catch (Exception e) {
            logger.error("S3 hasNext failure");
            throw new RuntimeException(e);
        }
    }

    /**
     * This method is also responsible for handling the cross-file and cross-folder boundaries regarding iterator setup.
     *
     * @return the next line of the file iterator
     */
    @Override
    public Map<String, Object> next() {
        String event = lineIterator.next();
        try {
            return MAPPER.readValue(event, TYPE);
        } catch (IOException e) {
            logger.error("Failed to deserialize JSON string {}.", event, e);
            throw new IllegalArgumentException(e);
        }
    }

    @Override
    public String toString() {
        return String.format("S3DataIterator{bucket='%s', streamPrefix='%s'}", bucket, streamPrefix);
    }

    @Override
    public void close() {
        if (lineIterator != null) {
            lineIterator.close();
        }
    }

    /**
     * recurse to the next non-empty file, stepping through folders as necessary.
     */
    private void nextFile() {
        // remaining files in the current folder
        if (fileIterator.hasNext()) {
            lineIterator = getS3Reader(fileIterator.next().getKey());
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
        }
    }

    /**
     * recurse to a hour-folder with non-zero # of files.
     */
    private void nextFolder() {
        // if any folders remaining
        if (folderIterator.hasNext()) {
            // get next folder
            fileIterator = getListOfObjectsFromS3ByPrefix(folderIterator.next()).iterator();
            // recursive till we reach a non-empty folder
            if (!fileIterator.hasNext()) {
                nextFolder();
            }
        }
    }

    /**
     * Instantiates a list containing the folders from which to pull the events in the given time-range.
     */
    private void initFolderIterator() {
        List<String> days = new ArrayList<>();
        logger.info("Fetching events from inclusive {} to exclusive {}.", startTime, endTime);
        for (Instant time = startTime; time.compareTo(endTime) <= 0; time = time.plus(1, DAYS).truncatedTo(DAYS)) {
            days.add(streamPrefix + generateDaySuffix(time));
        }
        folderIterator = days.iterator();
    }

    /**
     * Generates the time-part of the path in S3. This is in the YYYY/MM/DD format. Along with the tenant, account, schema
     * and region prefix, a object key would look like <tenant>/NetWitness/<account>/<schema>/<region>/year/month/day/events1.json.gz
     *
     * @param date an instant in time
     * @return the time-part of the key prefix
     */
    private String generateDaySuffix(Instant date) {
        ZonedDateTime dateTime = ZonedDateTime.ofInstant(date, ZoneId.of("UTC"));
        return String.format("%1$tY/%1$tm/%1$td", dateTime);
    }

    /**
     * Read a file from S3, a provide access to them as an iterator of lines.
     *
     * @param filePath the key of the file to read
     * @return An iterator to a List containing the lines of the file as {@link String}s
     */
    private BufferReaderIterator getS3Reader(String filePath) {
        S3ObjectInputStream S3Object;
        try {
            S3Object = s3.getObject(bucket, filePath).getObjectContent();
        } catch (AmazonS3Exception e) {
            logger.error("Failed to get object key: {}, from S3 bucket: {}.", filePath, bucket, e);
            throw new RuntimeException(e);
        }

        try {
            GZIPInputStream gzip = new GZIPInputStream(S3Object);
            BufferedReader reader = new BufferedReader(new InputStreamReader(gzip));
            return new BufferReaderIterator(reader);
        } catch (IOException e) {
            logger.error("Failed to open file with key: {}.", filePath, e);
            throw new RuntimeException(e);
        }
    }

    private List<S3ObjectSummary> getListOfObjectsFromS3ByPrefix(String prefix) {
        List<S3ObjectSummary> result;
        ListObjectsV2Request req = new ListObjectsV2Request().withBucketName(bucket).withPrefix(prefix);
        ListObjectsV2Result objects;
        try {
            objects = this.s3.listObjectsV2(req);
        } catch (AmazonS3Exception ex) {
            logger.error("Failed to list S3 objects with prefix: {}, from S3 bucket: {}.", prefix, bucket, ex);
            throw new RuntimeException(ex);
        }

        result = objects.getObjectSummaries().stream().filter(this::fileInRange).collect(Collectors.toList());
        result.sort(defaultS3ObjectSummaryComparator);
        return result;
    }

    private boolean fileInRange(S3ObjectSummary object) {
        Pattern p = Pattern.compile(DATE_REGEX_FORMAT);
        Matcher m = p.matcher(object.getKey());
        if (m.matches()) {
            SimpleDateFormat sdf = new SimpleDateFormat(DEFAULT_DATE_FORMAT);
            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
            String dateStr = m.group(1);
            try {
                Instant date = sdf.parse(dateStr).toInstant().minusNanos(1);
                if (date.isAfter(startTime) && date.isBefore(endTime)) {
                    return true;
                }
            } catch (ParseException ex) {
                logger.error("Invalid date format for S3 file: {} date: {}. Expected format: {}", object.getKey(), dateStr, DEFAULT_DATE_FORMAT, ex);
                throw new IllegalArgumentException(ex);
            }
        } else {
            logger.error("Invalid file name. Can't find time stamp for S3 file : {}, from S3 bucket: {}. Expected time stamp regex format: {}", object.getKey(), bucket, DATE_REGEX_FORMAT);
            throw new IllegalArgumentException();
        }
        return false;
    }

    /**
     * This class iterates through lines of a BufferedReader and allows the reader to be closed on completion.
     */
    private static final class BufferReaderIterator implements Iterator<String>, Closeable {

        private Iterator<String> iter;
        private BufferedReader reader;

        private BufferReaderIterator(BufferedReader reader) {
            if (reader != null) {
                this.iter = reader.lines().iterator();
                this.reader = reader;
                if (!iter.hasNext()) {
                    close();
                }
            } else {
                iter = Collections.emptyIterator();
            }
        }

        private static BufferReaderIterator empty() {
            return new BufferReaderIterator(null);
        }

        @Override
        public void close() {
            iter = Collections.emptyIterator();
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    logger.error("Could not close iterator", e);
                }
            }
        }

        @Override
        public boolean hasNext() {
            return iter.hasNext();
        }

        @Override
        public String next() {
            String next;
            try {
                next = iter.next();
                if (!iter.hasNext()) {
                    close();
                }
            } catch (Exception ex) {
                logger.error("Failed to fetch next record");
                throw new RuntimeException(ex);
            }
            return next;
        }
    }
}
