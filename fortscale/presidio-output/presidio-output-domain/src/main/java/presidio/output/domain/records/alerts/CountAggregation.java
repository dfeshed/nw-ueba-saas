package presidio.output.domain.records.alerts;

import com.fasterxml.jackson.annotation.JsonTypeName;

import java.util.List;

@JsonTypeName(CountAggregation.TYPE)
public class CountAggregation extends Aggregation<String, Double> {

    public static final String TYPE = "countAggregation";

    public CountAggregation() {

    }

    public CountAggregation(List<Bucket<String, Double>> buckets) {
        super(buckets);
    }

}
