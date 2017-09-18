package presidio.output.domain.records.alerts;

import java.util.List;

public class WeekdayAggregation extends Aggregation<String, List<Bucket<String, Integer>>> {

    public static final String TYPE = "weekdayAggregation";

    public WeekdayAggregation(List<Bucket<String,List<Bucket<String, Integer>>>> buckets) {
        super(buckets);
    }

}
