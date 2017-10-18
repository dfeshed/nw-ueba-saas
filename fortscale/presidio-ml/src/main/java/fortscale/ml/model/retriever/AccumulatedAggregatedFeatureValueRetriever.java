package fortscale.ml.model.retriever;

import fortscale.aggregation.feature.event.AggregatedFeatureEventConf;
import fortscale.aggregation.feature.event.AggregatedFeatureEventsConfService;
import presidio.ade.domain.record.accumulator.AccumulatedAggregationFeatureRecord;
import presidio.ade.domain.store.accumulator.AggregationEventsAccumulationDataReader;

import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.stream.DoubleStream;

public class AccumulatedAggregatedFeatureValueRetriever extends AbstractAggregatedFeatureValueRetriever {

    private AggregationEventsAccumulationDataReader aggregationEventsAccumulationDataReader;


    public AccumulatedAggregatedFeatureValueRetriever(AccumulatedAggregatedFeatureValueRetrieverConf config, AggregationEventsAccumulationDataReader aggregationEventsAccumulationDataReader, AggregatedFeatureEventsConfService aggregatedFeatureEventsConfService) {
        super(config, aggregatedFeatureEventsConfService, true);
        this.aggregationEventsAccumulationDataReader = aggregationEventsAccumulationDataReader;
    }

    @Override
    protected DoubleStream readAggregatedFeatureValues(AggregatedFeatureEventConf aggregatedFeatureEventConf,
                                                       String contextId,
                                                       Date startTime,
                                                       Date endTime) {
        return getAccumulatedEventsByContextIdAndStartTimeRange(aggregatedFeatureEventConf, contextId, endTime).stream().flatMapToDouble(accAggEvent -> accAggEvent
                .getAggregatedFeatureValuesAsList()
                .stream()
                .mapToDouble(v -> v));
    }

    @Override
    protected long getAmountOfDistinctDays(AggregatedFeatureEventConf aggregatedFeatureEventConf, String contextId, Date startTime, Date endTime) {
        return getAccumulatedEventsByContextIdAndStartTimeRange(aggregatedFeatureEventConf, contextId, endTime).stream().map(x->x.getStartInstant().truncatedTo(ChronoUnit.DAYS)).distinct().count();
    }

    private List<AccumulatedAggregationFeatureRecord> getAccumulatedEventsByContextIdAndStartTimeRange(AggregatedFeatureEventConf aggregatedFeatureEventConf, String contextId, Date endTime) {
        return aggregationEventsAccumulationDataReader.findAccumulatedEventsByContextIdAndStartTimeRange(
                aggregatedFeatureEventConf.getName(),
                contextId,
                getStartTime(endTime).toInstant(),
                endTime.toInstant()
        );
    }
}
