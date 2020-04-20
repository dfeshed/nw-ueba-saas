package presidio.output.domain.records.alerts;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = CountAggregation.class, name = CountAggregation.TYPE),
        @JsonSubTypes.Type(value = TimeAggregation.class, name = TimeAggregation.TYPE),
        @JsonSubTypes.Type(value = WeekdayAggregation.class, name = WeekdayAggregation.TYPE)
})
public abstract class Aggregation<K,V> {

    List<Bucket<K,V>> buckets;
    Map<String, String> contexts = Collections.emptyMap();


    public Aggregation() {}

    public Aggregation(List<Bucket<K,V>> buckets, Map<String, String> contexts) {
        this.buckets = buckets;
        this.contexts = contexts;
    }

    public Aggregation(List<Bucket<K,V>> buckets) {
        this.buckets = buckets;
    }

    public List<Bucket<K,V>> getBuckets() {
        return buckets;
    }

    public Map<String, String> getContexts() {
        return contexts;
    }

}
