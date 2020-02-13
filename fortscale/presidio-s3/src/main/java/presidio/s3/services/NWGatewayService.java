package presidio.s3.services;

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

/**
 * An netwitness gateway service that supply services over s3. It makes the following assumptions:
 *
 * <ul>
 *     <li> the S3 keys are in the following format:
 *     <tenant>/NetWitness/<account>/<schema>/<region>/year/month/day/<Account>_<Region>_<Application>_<Timestamp>_<Unique>.json.gz</li>
 *  </ul>
 */

public class NWGatewayService {

    private static final Logger logger = LoggerFactory.getLogger(NWGatewayService.class);

    private String bucketName;
    private String tenant;
    private String account;
    private String region;

    private final static String DEFAULT_DATE_FORMAT = "yyyyMMdd'T'HHmm'Z'";
    private final static String DATE_REGEX_FORMAT = ".*_(20\\d{6}T\\d{4}Z)_.*";
    private Comparator<S3ObjectSummary> defaultS3ObjectSummaryComparator = Comparator.comparing(S3ObjectSummary::getKey);

    public NWGatewayService(String bucketName, String tenant, String account, String region) {
        this.bucketName = bucketName;
        this.tenant = tenant;
        this.account = account;
        this.region = region;
    }

    /**
     * Generates the object iterator from start time, end time and schema.
     *
     * @param s3        an AmazonS3Client that is set up with access to the bucket paths provided.
     * @param startDate the start time to iterator on.
     * @param endDate   the end time to iterate on.
     * @param schema    the data schema
     * @return list of objects.
     */

    public Iterator<S3ObjectSummary> getObjectsByRange(AmazonS3 s3, Instant startDate, Instant endDate, String schema) {
        List<S3ObjectSummary> objects = new ArrayList<>(Collections.emptyList());
        List<String> folders = getFolders(startDate, endDate, schema);
        for (String folder : folders) {
            objects.addAll(getListOfObjectsFromS3ByPrefix(s3, folder, startDate, endDate));
        }
        return objects.iterator();
    }

    private List<String> getFolders(Instant startDate, Instant endDate, String schema) {
        List<String> days = new ArrayList<>();
        logger.info("Fetching events from inclusive {} to exclusive {}.", startDate, endDate);
        for (Instant time = startDate; time.compareTo(endDate) <= 0; time = time.plus(1, DAYS).truncatedTo(DAYS)) {
            days.add(formStreamPrefix(schema) + generateDaySuffix(time));
        }
        return days;
    }

    private List<S3ObjectSummary> getListOfObjectsFromS3ByPrefix(AmazonS3 s3, String prefix, Instant startDate, Instant endDate) {
        List<S3ObjectSummary> result;
        ListObjectsV2Request req = new ListObjectsV2Request().withBucketName(bucketName).withPrefix(prefix);
        ListObjectsV2Result objects;
        try {
            objects = s3.listObjectsV2(req);
        } catch (Exception ex) {
            logger.error("Failed to list S3 objects with prefix: {}, from S3 bucket: {}.", prefix, bucketName, ex);
            throw new RuntimeException(ex);
        }

        result = objects.getObjectSummaries().stream().filter(obj -> fileInRange(obj, startDate, endDate)).collect(Collectors.toList());
        result.sort(defaultS3ObjectSummaryComparator);
        return result;
    }

    private boolean fileInRange(S3ObjectSummary object, Instant startTime, Instant endDate) {
        Pattern p = Pattern.compile(DATE_REGEX_FORMAT);
        Matcher m = p.matcher(object.getKey());
        if (m.matches()) {
            SimpleDateFormat sdf = new SimpleDateFormat(DEFAULT_DATE_FORMAT);
            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
            String dateStr = m.group(1);
            try {
                Instant date = sdf.parse(dateStr).toInstant().minusNanos(1);
                if (date.isAfter(startTime) && date.isBefore(endDate)) {
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

    /**
     * Generates the streamPrefix string from tenant, schema and region values.
     *
     * @param schema the data schema
     * @return the streamPrefix.
     */
    private String formStreamPrefix(String schema) {
        return this.tenant + "/NetWitness/" + this.account + "/" + schema + "/" + this.region + "/";
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
}
