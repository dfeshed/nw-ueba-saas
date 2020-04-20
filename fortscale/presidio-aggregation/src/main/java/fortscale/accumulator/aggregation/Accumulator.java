package fortscale.accumulator.aggregation;

import presidio.ade.domain.record.aggregated.AdeAggregationRecord;

import java.util.List;

public interface Accumulator {

    /**
     * Accumulate records
     * @param adeAggregationRecords list of aggregation records
     */
    void accumulate(List<AdeAggregationRecord> adeAggregationRecords);

}
