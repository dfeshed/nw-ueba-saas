package presidio.s3.services;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ListObjectsV2Request;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static fortscale.common.s3.NWGateway.DATE_REGEX_FORMAT;
import static fortscale.common.s3.NWGateway.DEFAULT_DATE_FORMAT;
import static fortscale.common.s3.NWGateway.formStreamPrefix;
import static fortscale.common.s3.NWGateway.generateDaySuffix;
import static java.time.temporal.ChronoUnit.DAYS;
import static java.time.temporal.ChronoUnit.HOURS;

/**
 * A netwitness gateway service that supply services over s3.
 */

public class NWGatewayService {

    private static final Logger logger = LoggerFactory.getLogger(NWGatewayService.class);
    private static final int timeToSleep = 30;
    private String bucketName;
    private String tenant;
    private String account;
    private String region;
    private AmazonS3 s3;

    private Comparator<S3ObjectSummary> defaultS3ObjectSummaryComparator = Comparator.comparing(S3ObjectSummary::getKey);

    public NWGatewayService(String bucketName, String tenant, String account, String region, AmazonS3 s3) {
        this.bucketName = bucketName;
        this.tenant = tenant;
        this.account = account;
        this.region = region;
        this.s3 = s3;
    }

    /**
     * Check and wait for hour to be ready for reading by checking if later file is exists.
     *
     * @param startDate the hour start time
     * @param endDate   the hour end time
     * @param schema    the data schema
     *
     * @return true if ready
     * @throws InterruptedException if the current thread is interrupted
     */
    public boolean hourIsReady(Instant startDate, Instant endDate, String schema) throws InterruptedException {
        endDate = endDate.truncatedTo(HOURS).plusSeconds(60);
        String prefix = getPrefix(tenant, account, schema, region, endDate);
        ListObjectsV2Result objects = getListOfObjectsFromS3ByPrefix(s3, prefix);
        boolean result;

        while (true) {
            for (S3ObjectSummary obj : objects.getObjectSummaries()) {
                result = filterFilesByS3Comparator(obj, startDate, endDate, new S3Comparator() {
                    @Override
                    public boolean compare(Instant s3FileDate, Instant startDate, Instant endDate) {
                        return s3FileDate.compareTo(endDate) >= 0;
                    }
                });
                if (result) {
                    logger.info("Hour {} is ready!. found file with key: {}.", startDate, obj.getKey());
                    return true;
                }
            }

            logger.info("Hour {} is not ready!, going to sleep for {} seconds.", startDate, timeToSleep);
            Thread.sleep(timeToSleep * 1000); // sleep for 30 seconds
        }
    }

    /**
     * Generates objects iterator for given start time, end time and schema.
     *
     * @param startDate the start time to iterator on.
     * @param endDate   the end time to iterate on.
     * @param schema    the data schema
     * @return list of objects.
     */
    public Iterator<S3ObjectSummary> getObjectsByRange(Instant startDate, Instant endDate, String schema) {
        List<S3ObjectSummary> objects = new ArrayList<>(Collections.emptyList());
        List<String> folders = getFolders(startDate, endDate, schema);
        for (String folder : folders) {
            objects.addAll(filterFilesByRange(getListOfObjectsFromS3ByPrefix(s3, folder), startDate, endDate));
        }
        return objects.iterator();
    }

    private List<String> getFolders(Instant startDate, Instant endDate, String schema) {
        List<String> days = new ArrayList<>();
        logger.info("Fetching events from inclusive {} to exclusive {}.", startDate, endDate);
        for (Instant time = startDate; time.compareTo(endDate) <= 0; time = time.plus(1, DAYS).truncatedTo(DAYS)) {
            days.add(getPrefix(tenant, account, schema, region, time));
        }
        return days;
    }

    private ListObjectsV2Result getListOfObjectsFromS3ByPrefix(AmazonS3 s3, String prefix) {
        ListObjectsV2Request req = new ListObjectsV2Request().withBucketName(bucketName).withPrefix(prefix);
        ListObjectsV2Result objects;
        try {
            objects = s3.listObjectsV2(req);
        } catch (Exception ex) {
            logger.error("Failed to list S3 objects with prefix: {}, from S3 bucket: {}.", prefix, bucketName, ex);
            throw new RuntimeException(ex);
        }

        return objects;
    }

    private List<S3ObjectSummary> filterFilesByRange(ListObjectsV2Result objects, Instant startDate, Instant endDate){
        List<S3ObjectSummary> result = objects.getObjectSummaries().stream().filter(obj -> filterFilesByS3Comparator(obj, startDate, endDate, new S3Comparator() {
            @Override
            public boolean compare(Instant s3FileDate, Instant startDate, Instant endDate) {
                return s3FileDate.isAfter(startDate) && s3FileDate.isBefore(endDate);
            }
        })).collect(Collectors.toList());
        result.sort(defaultS3ObjectSummaryComparator);
        return result;
    }

    private boolean filterFilesByS3Comparator(S3ObjectSummary object, Instant startDate, Instant endDate, S3Comparator s3Comparator) {
        Pattern p = Pattern.compile(DATE_REGEX_FORMAT);
        Matcher m = p.matcher(object.getKey());
        if (m.matches()) {
            SimpleDateFormat sdf = new SimpleDateFormat(DEFAULT_DATE_FORMAT);
            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
            String dateStr = m.group(1);
            try {
                Instant date = sdf.parse(dateStr).toInstant().minusNanos(1);
                if (s3Comparator.compare(date, startDate, endDate)) {
                    return true;
                }
            } catch (Exception ex) {
                logger.error("Invalid date format for S3 file: {} date: {}. Expected format: {}", object.getKey(), dateStr, DEFAULT_DATE_FORMAT, ex);
                throw new IllegalArgumentException(ex);
            }
        } else {
            logger.error("Invalid file name. Can't find time stamp for S3 file : {}, from S3 bucket: {}. Expected time stamp regex format: {}", object.getKey(), bucketName, DATE_REGEX_FORMAT);
            throw new IllegalArgumentException();
        }
        return false;
    }

    private String getPrefix(String tenant, String account, String schema, String region, Instant date){
        return formStreamPrefix(tenant, account, schema, region) + generateDaySuffix(date);
    }

    interface S3Comparator {
        boolean compare(Instant s3FileDate, Instant startDate, Instant endDate);
    }
}


