package presidio.webapp.service;

import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RestUtils {
    public static Map<String, Map<String, Long>> convertAggregationsToMap(Map<String, Aggregation> aggregations) {
        Map<String, Map<String, Long>> aggregationsMap = new HashMap<>();
        aggregations.forEach((s, aggregation) -> {
            List<Terms.Bucket> buckets = ((StringTerms) aggregation).getBuckets();
            Map<String, Long> bucketAsMap = new HashMap<>();
            buckets.forEach(bucket -> {
                bucketAsMap.put(bucket.getKeyAsString(), bucket.getDocCount());
            });
            aggregationsMap.put(s, bucketAsMap);
        });
        return aggregationsMap;
    }
}
