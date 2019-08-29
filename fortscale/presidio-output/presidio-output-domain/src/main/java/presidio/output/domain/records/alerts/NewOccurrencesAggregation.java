package presidio.output.domain.records.alerts;

import com.fasterxml.jackson.annotation.JsonTypeName;

import java.util.List;


@JsonTypeName(NewOccurrencesAggregation.TYPE)
public class NewOccurrencesAggregation extends Aggregation<String, List<Bucket<String, Bucket<String, Double>>>> {

    public static final String TYPE = "newOccurrencesAggregation";

    public NewOccurrencesAggregation() {
    }

    public NewOccurrencesAggregation(List<Bucket<String, List<Bucket<String, Bucket<String, Double>>>>> buckets) {

        super(buckets);
    }
}
