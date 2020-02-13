package presidio.s3.services;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.PredefinedClientConfigurations;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
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
 * A netwitness gateway service that supply services over s3. It makes the following assumption:
 * the S3 keys are in the following format: <tenant>/NetWitness/<account>/<schema>/<region>/year/month/day/<Account>_<Region>_<Application>_<Timestamp>_<Unique>.json.gz
 */

public class NWGatewayService {

    private static final Logger logger = LoggerFactory.getLogger(NWGatewayService.class);
    private static final int timeToSleep = 30;
    private String bucketName;
    private String tenant;
    private String account;
    private String region;

    private Comparator<S3ObjectSummary> defaultS3ObjectSummaryComparator = Comparator.comparing(S3ObjectSummary::getKey);

    public NWGatewayService(String bucketName, String tenant, String account, String region) {
        this.bucketName = bucketName;
        this.tenant = tenant;
        this.account = account;
        this.region = region;
    }

    public boolean hourIsReady(Instant startDate, Instant endDate, String schema) throws InterruptedException {
        ClientConfiguration clientConfiguration = PredefinedClientConfigurations.defaultConfig();
        clientConfiguration.setMaxErrorRetry(10);
        AmazonS3 s3 = AmazonS3ClientBuilder.standard().withClientConfiguration(clientConfiguration).build();
        endDate = endDate.truncatedTo(HOURS).plusSeconds(60);
        String prefix = formStreamPrefix(tenant, account, schema, region) + generateDaySuffix(endDate);
        ListObjectsV2Result objects = getListOfObjectsFromS3ByPrefix(s3, prefix);
        boolean result;

        while (true) {
            for (S3ObjectSummary obj : objects.getObjectSummaries()) {
                result = filterFilesByCompareDates(obj, startDate, endDate, new CompareDates() {
                    @Override
                    public boolean compare(Instant date, Instant startDate, Instant endDate) {
                        return date.compareTo(endDate) >= 0;
                    }
                });
                if (result) {
                    logger.info("Hour {} is ready!. found file with key: {}", startDate, obj.getKey());
                    return true;
                }
            }

            logger.info("Hour {} is not ready!, going to sleep for {} seconds", startDate, timeToSleep);
            Thread.sleep(timeToSleep * 1000); // sleep for 30 seconds
        }
    }

    /**
     * Generates the objects iterator for given start time, end time and schema.
     *
     * @param s3        an AmazonS3 that is set up with access to the bucket paths provided.
     * @param startDate the start time to iterator on.
     * @param endDate   the end time to iterate on.
     * @param schema    the data schema
     * @return list of objects.
     */
    public Iterator<S3ObjectSummary> getObjectsByRange(AmazonS3 s3, Instant startDate, Instant endDate, String schema) {
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
            days.add(formStreamPrefix(tenant, account, schema, region) + generateDaySuffix(time));
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
        List<S3ObjectSummary> result = objects.getObjectSummaries().stream().filter(obj -> filterFilesByCompareDates(obj, startDate, endDate, new CompareDates() {
            @Override
            public boolean compare(Instant date, Instant startDate, Instant endDate) {
                return date.isAfter(startDate) && date.isBefore(endDate);
            }
        })).collect(Collectors.toList());
        result.sort(defaultS3ObjectSummaryComparator);
        return result;
    }

    private boolean filterFilesByCompareDates(S3ObjectSummary object, Instant startDate, Instant endDate, CompareDates compareDates) {
        Pattern p = Pattern.compile(DATE_REGEX_FORMAT);
        Matcher m = p.matcher(object.getKey());
        if (m.matches()) {
            SimpleDateFormat sdf = new SimpleDateFormat(DEFAULT_DATE_FORMAT);
            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
            String dateStr = m.group(1);
            try {
                Instant date = sdf.parse(dateStr).toInstant().minusNanos(1);
                if (compareDates.compare(date, startDate, endDate)) {
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

    interface CompareDates {
        boolean compare(Instant date, Instant startDate, Instant endDate);
    }
}


