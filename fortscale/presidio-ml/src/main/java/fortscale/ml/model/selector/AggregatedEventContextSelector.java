package fortscale.ml.model.selector;

import fortscale.aggregation.exceptions.InvalidAggregatedFeatureEventConfNameException;
import fortscale.aggregation.feature.event.AggregatedFeatureEventConf;
import fortscale.aggregation.feature.event.AggregatedFeatureEventsConfService;
import fortscale.utils.time.TimeRange;
import presidio.ade.domain.store.accumulator.AggregationEventsAccumulationDataReader;

import java.util.Date;
import java.util.Set;

public class AggregatedEventContextSelector implements IContextSelector {
	private AggregatedFeatureEventConf aggregatedFeatureEventConf;
	private AggregationEventsAccumulationDataReader aggregationEventsAccumulationDataReader;

	public AggregatedEventContextSelector(
			AggregatedEventContextSelectorConf conf,
			AggregatedFeatureEventsConfService aggregatedFeatureEventsConfService,
			AggregationEventsAccumulationDataReader aggregationEventsAccumulationDataReader) {

		this.aggregatedFeatureEventConf = aggregatedFeatureEventsConfService
				.getAggregatedFeatureEventConf(conf.getAggregatedFeatureEventConfName());
		this.aggregationEventsAccumulationDataReader = aggregationEventsAccumulationDataReader;
		validate(conf);
	}

	@Override
	public Set<String> getContexts(TimeRange timeRange) {
		return aggregationEventsAccumulationDataReader.findDistinctAcmContextsByTimeRange(
				aggregatedFeatureEventConf.getName(),
				timeRange);
	}

	private void validate(AggregatedEventContextSelectorConf conf) {
		if (aggregatedFeatureEventConf == null) {
			throw new InvalidAggregatedFeatureEventConfNameException(conf.getAggregatedFeatureEventConfName());
		}
	}
}
