package presidio.output.processor.services.alert.supportinginformation.transformer;


import presidio.output.domain.records.alerts.Aggregation;
import presidio.output.domain.records.alerts.Bucket;
import presidio.output.domain.records.alerts.HistoricalData;

import java.util.List;

public class AbnormalSourceMachineTransformer implements SupportingInformationTransformer {

    public static final String NOT_AVAILABLE = "N/A";
    public static final String UNRESOLVED = "Unresolved";

    @Override
    public void transformHistoricalData(HistoricalData historicalData) {
        Aggregation aggr = historicalData.getAggregation();
        List<Bucket<String, Integer>> buckets = aggr.getBuckets();
        for (Bucket<String, Integer> bucket: buckets) {
            if (NOT_AVAILABLE.equals(bucket.getKey())) {
                bucket.setKey(UNRESOLVED);
            }
        }

    }
}
