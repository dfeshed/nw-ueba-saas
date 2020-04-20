package presidio.output.domain.records.alerts;

import com.fasterxml.jackson.annotation.JsonTypeName;

import java.util.List;
import java.util.Map;

@JsonTypeName(CountAggregation.TYPE)
public class CountAggregation extends Aggregation<String, Double> {

    public static final String TYPE = "countAggregation";

    public CountAggregation() {

    }

    public CountAggregation(List<Bucket<String, Double>> buckets, Map<String, String> contexts) {
        super(buckets, contexts);
    }

}
