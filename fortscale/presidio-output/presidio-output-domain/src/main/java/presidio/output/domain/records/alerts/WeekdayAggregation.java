package presidio.output.domain.records.alerts;

import java.util.List;
import java.util.Map;

public class WeekdayAggregation extends Aggregation<String, List<Bucket<String, Integer>>> {

    public static final String TYPE = "weekdayAggregation";

    public WeekdayAggregation() {
    }

    public WeekdayAggregation(List<Bucket<String,List<Bucket<String, Integer>>>> buckets, Map<String, String> contexts) {
        super(buckets, contexts);
    }

}
