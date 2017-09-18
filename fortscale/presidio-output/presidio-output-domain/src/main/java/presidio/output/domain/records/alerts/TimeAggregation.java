package presidio.output.domain.records.alerts;

import com.fasterxml.jackson.annotation.JsonTypeName;

import java.time.Instant;
import java.util.List;


@JsonTypeName(TimeAggregation.TYPE)
public class TimeAggregation extends Aggregation<String, Double> {

    public static final String TYPE = "timeAggregation";

    public TimeAggregation() {
    }

    public TimeAggregation(List<Bucket<String, Double>> buckets) {

        super(buckets);
    }
}
