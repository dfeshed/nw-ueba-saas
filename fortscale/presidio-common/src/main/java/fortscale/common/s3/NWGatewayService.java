package fortscale.common.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ListObjectsV2Request;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static java.time.temporal.ChronoUnit.DAYS;

public class NWGatewayService {

    private final static Logger logger = LoggerFactory.getLogger(NWGatewayService.class);
    private final static String DEFAULT_DATE_FORMAT = "yyyyMMdd'T'HHmm'Z'";
    private final static String DATE_REGEX_FORMAT = ".*_(20\\d{6}T\\d{4}Z)_.*";
    private final static Comparator<S3ObjectSummary> defaultS3ObjectSummaryComparator = Comparator.comparing(S3ObjectSummary::getKey);
    private String bucketName;
    private String tenant;
    private String account;
    private String region;
    private AmazonS3 s3;

    public NWGatewayService(String bucketName, String tenant, String account, String region, AmazonS3 s3) {
        this.bucketName = bucketName;
        this.tenant = tenant;
        this.account = account;
        this.region = region;
        this.s3 = s3;
    }

    /**
     * check if hour is ready by checking if later file is exists.
     * the method make the assumption that the timestamp in the filename is the minute after the latest record in the file.
     *
     * @param endDate until that time all the records were written to s3.
     * @param schema  the data schema
     * @return true if hour is ready, otherwise false.
     */
    public boolean isHourReady(Instant endDate, String schema) {
        endDate = endDate.plusSeconds(60);
        String prefix = getPrefix(tenant, account, schema, region, endDate);
        ListObjectsV2Result objects = getListOfObjectsFromS3ByPrefix(prefix);
        for (S3ObjectSummary obj : objects.getObjectSummaries()) {
            boolean result = getS3FileDate(obj).compareTo(endDate) >= 0;
            if (result) {
                logger.info("Found file with key: {}.", obj.getKey());
                return true;
            }
        }
        return false;
    }

    /**
     * Generates objects iterator for given start time, end time and schema.
     *
     * @param startDate start time to iterator on.
     * @param endDate   end time to iterate on.
     * @param schema    the data schema
     * @return list of objects.
     */
    public Iterator<S3ObjectSummary> getObjectsByRange(Instant startDate, Instant endDate, String schema) {
        List<S3ObjectSummary> objects = new ArrayList<>(Collections.emptyList());
        List<String> folders = getFolders(startDate, endDate, schema);
        for (String folder : folders) {
            objects.addAll(filterFilesByRange(getListOfObjectsFromS3ByPrefix(folder), startDate, endDate));
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

    private ListObjectsV2Result getListOfObjectsFromS3ByPrefix(String prefix) {
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

    private List<S3ObjectSummary> filterFilesByRange(ListObjectsV2Result objects, Instant startDate, Instant endDate) {
        List<S3ObjectSummary> result = objects.getObjectSummaries().stream().filter(obj -> {
            Instant s3FileDate = getS3FileDate(obj).minusNanos(1);
            return s3FileDate.isAfter(startDate) && s3FileDate.isBefore(endDate);
        }).collect(Collectors.toList());

        result.sort(defaultS3ObjectSummaryComparator);
        return result;
    }

    private Instant getS3FileDate(S3ObjectSummary object) {
        return parseDateStr(getS3FileDateStr(object), object);
    }

    private String getS3FileDateStr(S3ObjectSummary object) {
        Pattern p = Pattern.compile(DATE_REGEX_FORMAT);
        Matcher m = p.matcher(object.getKey());
        if (m.matches()) {
            return m.group(1);
        } else {
            logger.error("Invalid file name. Can't find time stamp for S3 file : {}, from S3 bucket: {}. Expected time stamp regex format: {}", object.getKey(), bucketName, DATE_REGEX_FORMAT);
            throw new IllegalArgumentException();
        }
    }

    private Instant parseDateStr(String dateStr, S3ObjectSummary object) {
        SimpleDateFormat sdf = new SimpleDateFormat(DEFAULT_DATE_FORMAT);
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        try {
            return sdf.parse(dateStr).toInstant().minusNanos(1);
        } catch (Exception ex) {
            logger.error("Invalid date format for S3 file: {} date: {}. Expected format: {}", object.getKey(), dateStr, DEFAULT_DATE_FORMAT, ex);
            throw new IllegalArgumentException(ex);
        }
    }

    private static String getPrefix(String tenant, String account, String schema, String region, Instant date) {
        return formStreamPrefix(tenant, account, schema, region) + generateDaySuffix(date);
    }

    /**
     * Generates the streamPrefix string from tenant, account, schema and region values for the following format:
     * <tenant>/NetWitness/<account>/<schema>/<region>
     *
     * @param tenant  the relevant tenant
     * @param account s3 account number
     * @param schema  the data schema
     * @param region  s3 region
     * @return the streamPrefix.
     */
    private static String formStreamPrefix(String tenant, String account, String schema, String region) {
        return tenant + "/NetWitness/" + account + "/" + schema + "/" + region + "/";
    }

    /**
     * Generates the time-part of the path in S3. This is in the YYYY/MM/DD format. Along with the tenant, account, schema
     * and region prefix, a object key would look like <tenant>/NetWitness/<account>/<schema>/<region>/year/month/day/events1.json.gz
     *
     * @param date an instant in time
     * @return the time-part of the key prefix
     */
    private static String generateDaySuffix(Instant date) {
        ZonedDateTime dateTime = ZonedDateTime.ofInstant(date, ZoneId.of("UTC"));
        return String.format("%1$tY/%1$tm/%1$td", dateTime);
    }
}
