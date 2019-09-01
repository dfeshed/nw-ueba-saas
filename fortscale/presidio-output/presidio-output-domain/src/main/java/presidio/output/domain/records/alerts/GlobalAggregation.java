package presidio.output.domain.records.alerts;

import com.fasterxml.jackson.annotation.JsonTypeName;

import java.util.List;

@JsonTypeName(GlobalAggregation.TYPE)
public class GlobalAggregation extends Aggregation<String, Bucket<String, Double>> {

    public static final String TYPE = "globalAggregation";

    public GlobalAggregation() {
    }

    public GlobalAggregation(List<Bucket<String, Bucket<String, Double>>> buckets) {

        super(buckets);
    }
}
