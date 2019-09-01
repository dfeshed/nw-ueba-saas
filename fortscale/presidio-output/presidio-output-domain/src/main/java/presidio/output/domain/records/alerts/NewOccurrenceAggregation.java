package presidio.output.domain.records.alerts;

import com.fasterxml.jackson.annotation.JsonTypeName;

import java.util.List;

@JsonTypeName(NewOccurrenceAggregation.TYPE)
public class NewOccurrenceAggregation extends Aggregation<String, Bucket<String, Double>> {

    public static final String TYPE = "newOccurrencesAggregation";

    public NewOccurrenceAggregation() {
    }

    public NewOccurrenceAggregation(List<Bucket<String, Bucket<String, Double>>> buckets) {

        super(buckets);
    }
}
