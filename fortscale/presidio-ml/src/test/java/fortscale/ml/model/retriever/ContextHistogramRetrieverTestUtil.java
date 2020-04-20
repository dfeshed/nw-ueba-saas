package fortscale.ml.model.retriever;

import fortscale.aggregation.feature.bucket.FeatureBucket;
import fortscale.common.feature.Feature;
import fortscale.common.util.GenericHistogram;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;


/**
 * Created by barak_schuster on 10/19/17.
 */
public class ContextHistogramRetrieverTestUtil {
    static final String FEATURE_BUCKET_CONF_NAME = "test_conf_name";
    static final String TEST_FEATURE_NAME = "test_feature_name";
    static final String CONTEXTID_WITH_NO_DATA = "contextId_with_no_data";
    static final String CONTEXT_ID_WITH_DATA = "contextId_with_data";

    public static LinkedList<FeatureBucket> generateHourlyFeatureBuckets(Instant fromInstant, Instant toInstant) {
        Instant cursor = Instant.ofEpochSecond(fromInstant.getEpochSecond());
        LinkedList<FeatureBucket> featureBuckets = new LinkedList<>();

        while (cursor.isBefore(toInstant))
        {
            FeatureBucket featureBucket = new FeatureBucket();

            featureBucket.setStartTime(cursor);
            featureBucket.setEndTime(cursor.plus(1, ChronoUnit.HOURS));
            featureBucket.setContextId(CONTEXT_ID_WITH_DATA);
            featureBucket.setFeatureBucketConfName(FEATURE_BUCKET_CONF_NAME);

            cursor = cursor.plus(1, ChronoUnit.HOURS);
            Map<String,Double> genericHistogramMap = new HashMap<>();
            genericHistogramMap.put("a",55d);
            genericHistogramMap.put("b",56d);
            genericHistogramMap.put("c",57d);
            GenericHistogram genericHistogram = new GenericHistogram(genericHistogramMap);
            featureBucket.getAggregatedFeatures().put(TEST_FEATURE_NAME,new Feature(TEST_FEATURE_NAME, genericHistogram));
            featureBucket.getAggregatedFeatures().put(TEST_FEATURE_NAME+"2",new Feature(TEST_FEATURE_NAME,33));
            featureBuckets.add(featureBucket);
        }

        return featureBuckets;
    }
}
