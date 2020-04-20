package presidio.output.domain.records.alerts;

import com.fasterxml.jackson.annotation.JsonTypeName;

import java.time.Instant;
import java.util.List;
import java.util.Map;


@JsonTypeName(TimeAggregation.TYPE)
public class TimeAggregation extends Aggregation<String, Double> {

    public static final String TYPE = "timeAggregation";

    public TimeAggregation() {
    }

    public TimeAggregation(List<Bucket<String, Double>> buckets, Map<String, String> contexts) {

        super(buckets, contexts);
    }

    public TimeAggregation(List<Bucket<String, Double>> buckets) {

        super(buckets);
    }
}
