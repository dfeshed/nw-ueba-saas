package presidio.webapp.service;

import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.bucket.terms.InternalTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RestUtils {
    public static Map<String, Map<String, Long>> convertAggregationsToMap(Map<String, Aggregation> aggregations, Map<String, String> aggregationNamesEnumMapping) {
        Map<String, Map<String, Long>> aggregationsMap = new HashMap<>();
        aggregations.forEach((s, aggregation) -> {
            List<Terms.Bucket> buckets = ((InternalTerms) aggregation).getBuckets();
            Map<String, Long> bucketAsMap = new HashMap<>();
            buckets.forEach(bucket -> {
                bucketAsMap.put(bucket.getKeyAsString(), bucket.getDocCount());
            });
            // getting the aggregation name
            String aggregationName = s;
            if (aggregationNamesEnumMapping.get(s) != null) {
                aggregationName = aggregationNamesEnumMapping.get(s);
            }
            aggregationsMap.put(aggregationName, bucketAsMap);
        });
        return aggregationsMap;
    }
}
