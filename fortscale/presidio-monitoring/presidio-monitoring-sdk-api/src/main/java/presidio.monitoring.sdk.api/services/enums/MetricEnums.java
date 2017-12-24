package presidio.monitoring.sdk.api.services.enums;


import java.util.Collection;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

public class MetricEnums {

    public enum MetricValues {
        DEFAULT_METRIC_VALUE("metric_value"), SUM("sum"), MAX("max"), AVG("avg"), COUNT("count"),
        AMOUNT_OF_SCORED("amountOfScored"),
        AMOUNT_OF_NON_ZERO_SCORE("amountOfNonZeroScore"),
        MAX_SCORE("maxScore"),
        AMOUNT_OF_SUCCEEDED_MODELS("amountOfSucceededModels"),
        AMOUNT_OF_FAILED_MODELS("amountOfFailedModels"),
        AMOUNT_OF_ALL_DATA_FILTERED("amountOfAllDataFiltered"),
        MAX_SIZE_OF_FEATURE_VALUES("maxSizeOfFeatureValues"),
        AVG_SIZE_OF_FEATURE_VALUES("avgSizeOfFeatureValues"),
        SUM_SIZE_OF_FEATURE_VALUES("sumSizeOfFeatureValues"),
        AMOUNT_OF_CONTEXTS_WITH_POSITIVE_BUCKET_VALUES("amountOfContextsWithPositiveBucketValues"),
        MAX_NUM_OF_PARTITIONS("maxNumOfPartitions"),
        AVG_NUM_OF_PARTITIONS("avgNumOfPartitions"),
        SUM_NUM_OF_PARTITIONS("sumNumOfPartitions"),
        AMOUNT_OF_CONTEXTS("amountOfContexts"),
        AVG_OF_BUCKET_HITS("avgOfBucketHits"),
        SUM_OF_BUCKET_HITS("sumOfBucketHits"),
        MAX_OF_BUCKET_HITS("maxOfBucketHits"),
        AVG_OF_SMOOTHED_BUCKETS("avgOfSmoothedBuckets"),
        SUM_OF_SMOOTHED_BUCKETS("sumOfSmoothedBuckets"),
        MAX_OF_SMOOTHED_BUCKETS("maxOfSmoothedBuckets"),
        READS("reads"),
        WRITES("writes"),
        HIT("modelFromMemory"),
        MISS("modelFromDB"),
        EMPTY_MODEL("emptyModel"),
        NULL_FEATURE_BUCKET_ID("nullFeatureBucketId"),
        FEATURE_BUCKETS("featureBuckets"),
        FEATURE_BUCKETS_UPDATES("featureBucketsUpdates"),
        SUCCESS_EVENTS("successEvents"),
        TOTAL_EVENTS("totalEvents"),
        FAILED_EVENTS("failedEvents"),
        SUCCESS_PAGES("successPages"),
        TOTAL_PAGES("totalPages"),
        FAILED_PAGES("failedPages"),
        AVG_PAGE_SIZE("avgPageSize")
        ;

        private String value;

        MetricValues(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return value;
        }

        public static MetricValues fromValue(String text) {
            for (MetricValues b : MetricValues.values()) {
                if (String.valueOf(b.value).equals(text)) {
                    return b;
                }
            }
            return null;
        }

        public static Collection<MetricValues> collectionOfMetricValues() {
            return EnumSet.allOf(MetricValues.class);
        }
    }

    public enum MetricTagKeysEnum {
        HOST, SCHEMA, UNIT, RESULT, APPLICATION_NAME, PID, IS_SYSTEM_METRIC,ADE_EVENT_TYPE,SCORER,MODEL,FAILURE_REASON, GROUP_NAME, TIME, FEATURE_BUCKET;
    }

    public enum MetricUnitType {
        NUMBER("number"), B("byte"), KB("kilo_byte"), MB("mega_byte"), GB("giga_byte"), MILLI_SECOND("milli_second"), SECOND("second"), DATE("date");

        private String value;

        MetricUnitType(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return value;
        }

        public static MetricUnitType fromValue(String text) {
            for (MetricUnitType b : MetricUnitType.values()) {
                if (String.valueOf(b.value).equals(text)) {
                    return b;
                }
            }
            return null;
        }

    }
}
