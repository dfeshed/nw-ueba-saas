package presidio.webapp.service;

import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.bucket.histogram.InternalDateHistogram;
import org.elasticsearch.search.aggregations.bucket.terms.InternalTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RestUtils {
    public static Map<String, Map<String, Long>> convertAggregationsToMap(Map<String, Aggregation> aggregations, Map<String, String> aggregationNamesEnumMapping) {
        Map<String, Map<String, Long>> aggregationsMap = new HashMap<>();
        aggregations.forEach((s, aggregation) -> {
            Map<String, Long> bucketAsMap = new HashMap<>();
            // single level aggregation
            if (aggregation instanceof InternalTerms) {
                List<Terms.Bucket> buckets = ((InternalTerms) aggregation).getBuckets();
                buckets.forEach(bucket -> {
                    bucketAsMap.put(bucket.getKeyAsString(), bucket.getDocCount());
                });

                // Two level aggregation
            } else if (aggregation instanceof InternalDateHistogram) {
                // Get the first level buckets
                List<InternalDateHistogram.Bucket> firstLevelBuckets = ((InternalDateHistogram) aggregation).getBuckets();
                firstLevelBuckets.forEach(firstLevelBucket -> {
                    Map<String, Aggregation> secondLevelAggregations = firstLevelBucket.getAggregations().asMap();
                    secondLevelAggregations.forEach((aggregationFieldName, secondLevelAggregation) -> {
                                if (secondLevelAggregation instanceof InternalTerms) {
                                    // Get the second level bucket
                                    List<Terms.Bucket> secondLevelBuckets = ((InternalTerms) secondLevelAggregation).getBuckets();
                                    secondLevelBuckets.forEach(secondLevelBucket -> {
                                        if (s.equals("severityPerDay")) {
                                            long dateInMilli = Instant.parse(firstLevelBucket.getKeyAsString()).toEpochMilli();
                                            bucketAsMap.put(String.format("%s:%s", dateInMilli, secondLevelBucket.getKeyAsString()), secondLevelBucket.getDocCount());
                                        } else {
                                            bucketAsMap.put(String.format("%s:%s", firstLevelBucket.getKeyAsString(), secondLevelBucket.getKeyAsString()), secondLevelBucket.getDocCount());
                                        }
                                    });
                                }
                            }
                    );
                });
            }

            String aggregationName = s;
            if (aggregationNamesEnumMapping.get(s) != null) {
                aggregationName = aggregationNamesEnumMapping.get(s);
            }
            aggregationsMap.put(aggregationName, bucketAsMap);
        });
        return aggregationsMap;
    }
}
