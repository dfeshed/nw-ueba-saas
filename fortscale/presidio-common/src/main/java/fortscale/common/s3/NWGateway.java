package fortscale.common.s3;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class NWGateway {
    public final static String DEFAULT_DATE_FORMAT = "yyyyMMdd'T'HHmm'Z'";
    public final static String DATE_REGEX_FORMAT = ".*_(20\\d{6}T\\d{4}Z)_.*";

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
    public static String formStreamPrefix(String tenant, String account, String schema, String region) {
        return tenant + "/NetWitness/" + account + "/" + schema + "/" + region + "/";
    }

    /**
     * Generates the time-part of the path in S3. This is in the YYYY/MM/DD format. Along with the tenant, account, schema
     * and region prefix, a object key would look like <tenant>/NetWitness/<account>/<schema>/<region>/year/month/day/events1.json.gz
     *
     * @param date an instant in time
     * @return the time-part of the key prefix
     */
    public static String generateDaySuffix(Instant date) {
        ZonedDateTime dateTime = ZonedDateTime.ofInstant(date, ZoneId.of("UTC"));
        return String.format("%1$tY/%1$tm/%1$td", dateTime);
    }
}
