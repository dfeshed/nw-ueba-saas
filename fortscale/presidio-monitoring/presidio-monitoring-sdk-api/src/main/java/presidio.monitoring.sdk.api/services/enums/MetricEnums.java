package presidio.monitoring.sdk.api.services.enums;


import java.util.Collection;
import java.util.EnumSet;

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
        MIN_RESOLUTION("minResolution"),
        MAX_RESOLUTION("maxResolution"),
        AVG_RESOLUTION("avgResolution"),
        SUM_RESOLUTION("sumResolution"),
        MAX_CONTINUOUS_MEAN("maxContinuousMean"),
        AVG_CONTINUOUS_MEAN("avgContinuousMean"),
        SUM_CONTINUOUS_MEAN("sumContinuousMean"),
        MAX_MAX_CONTINUOUS_MEAN("maxMaxContinuousMean"),
        AVG_MAX_CONTINUOUS_MEAN("avgMaxContinuousMea"),
        SUM_MAX_CONTINUOUS_MEAN("sumMaxContinuousMea"),
        MAX_MAX_CONTINUOUS_SD("maxMaxContinuousSd"),
        AVG_MAX_CONTINUOUS_SD("avgMaxContinuousSd"),
        SUM_MAX_CONTINUOUS_SD("sumMaxContinuousSd"),
        MAX_CONTINUOUS_SD("maxContinuousSd"),
        AVG_CONTINUOUS_SD("avgContinuousSd"),
        SUM_CONTINUOUS_SD("sumContinuousSd"),
        MAX_CONTINUOUS_N("maxContinuousN"),
        AVG_CONTINUOUS_N("avgContinuousN"),
        SUM_CONTINUOUS_N("sumContinuousN"),
        MAX_MAX_CONTINUOUS_N("maxMaxContinuousN"),
        AVG_MAX_CONTINUOUS_N("avgMaxContinuousN"),
        SUM_MAX_CONTINUOUS_N("sumMaxContinuousN"),
        MAX_CONTINUOUS_MAX_VALUE("maxContinuousMaxValue"),
        AVG_CONTINUOUS_MAX_VALUE("avgContinuousMaxValue"),
        SUM_CONTINUOUS_MAX_VALUE("sumContinuousMaxValue"),
        MAX_MAX_CONTINUOUS_MAX_VALUE("maxMaxContinuousMaxValue"),
        AVG_MAX_CONTINUOUS_MAX_VALUE("avgMaxContinuousMaxValue"),
        SUM_MAX_CONTINUOUS_MAX_VALUE("sumMaxContinuousMaxValue"),
        MAX_GLOBAL_SD("maxGlobalSd"),
        AVG_GLOBAL_SD("avgGlobalSd"),
        SUM_GLOBAL_SD("sumGlobalSd"),
        SUCCESS_EVENTS("successEvents"),
        TOTAL_EVENTS("totalEvents"),
        FAILED_EVENTS("failedEvents"),
        SUCCESS_PAGES("successPages"),
        TOTAL_PAGES("totalPages"),
        FAILED_PAGES("failedPages"),
        AVG_PAGE_SIZE("avgPageSize"),
        MAX_MEAN("maxMean"),
        AMOUNT_OF_SEGMENT_PRIORS("amountOfSegmentPriors"),
        AGGREGATIONS("aggregations"),
        AMOUNT_OF_NON_ZERO_FEATURE_VALUES("amountOfNonZeroFeatureValue"),
        MAX_FEATURE_VALUE("maxFeatureValue"),
        WEIGHT("weight"),
        SYSTEM_MEM("mem"),
        SYSTEM_MEM_FREE("mem.free"),
        SYSTEM_UPTIME("uptime"),
        SYSTEM_HEAP_COMMITTED("heap.committed"),
        SYSTEM_HEAP_INIT("heap.init"),
        SYSTEM_HEAP_USED("heap.used"),
        SYSTEM_HEAP("heap"),
        SYSTEM_NONHEAP_COMMITTED("nonheap.committed"),
        SYSTEM_NONHEAP_INIT("nonheap.init"),
        SYSTEM_NONHEAP_USED("nonheap.used"),
        SYSTEM_NONHEAP("nonheap"),
        SYSTEM_THREADS_PEAK("threads.peak"),
        SYSTEM_THREADS_DAEMON("threads.daemon"),
        SYSTEM_THREADS_TOTAL_STARTED("threads.totalStarted"),
        SYSTEM_THREADS("threads"),
        SYSTEM_GC_YOUNG_COUNT("gc.young.count"),
        SYSTEM_GC_YOUNG_TIME("gc.young.time"),
        SYSTEM_GC_OLD_COUNT("gc.old.count"),
        SYSTEM_GC_OLD_TIME("gc.old.time"),
        SYSTEM_CPU_LOAD("cpu.load"),
        SYSTEM_CPU_TIME("cpu.time");

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
        HOST, SCHEMA, UNIT, RESULT, APPLICATION_NAME, PID, IS_SYSTEM_METRIC,ADE_EVENT_TYPE,SCORER,MODEL,FAILURE_REASON,
        FEATURE_NAME, AGGREGATED_FEATURE_TYPE,GROUP_NAME, TIME, FEATURE_BUCKET,FLUME_COMPONENT_TYPE,
        FLUME_COMPONENT_INSTANCE_ID,HOUR_CLOSED,GC_YOUNG_COLLECTOR, GC_OLD_COLLECTOR;
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
