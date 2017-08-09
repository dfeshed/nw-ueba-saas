package fortscale.ml.model.selector;

import fortscale.aggregation.exceptions.InvalidAggregatedFeatureEventConfNameException;
import fortscale.aggregation.feature.event.AggregatedFeatureEventConf;
import fortscale.aggregation.feature.event.AggregatedFeatureEventsConfService;
import fortscale.aggregation.feature.event.store.AggregatedFeatureEventsReaderService;
import fortscale.utils.time.TimeRange;

import java.util.Date;
import java.util.Set;

public class AggregatedEventContextSelector implements IContextSelector {
	private AggregatedFeatureEventConf aggregatedFeatureEventConf;
	private AggregatedFeatureEventsReaderService aggregatedFeatureEventsReaderService;

	public AggregatedEventContextSelector(
			AggregatedEventContextSelectorConf conf,
			AggregatedFeatureEventsConfService aggregatedFeatureEventsConfService,
			AggregatedFeatureEventsReaderService aggregatedFeatureEventsReaderService) {

		this.aggregatedFeatureEventConf = aggregatedFeatureEventsConfService
				.getAggregatedFeatureEventConf(conf.getAggregatedFeatureEventConfName());
		this.aggregatedFeatureEventsReaderService = aggregatedFeatureEventsReaderService;
		validate(conf);
	}

	@Override
	public Set<String> getContexts(TimeRange timeRange) {
		return aggregatedFeatureEventsReaderService.findDistinctAcmContextsByTimeRange(
				aggregatedFeatureEventConf,
				Date.from(timeRange.getStart()),
				Date.from(timeRange.getEnd()));
	}

	private void validate(AggregatedEventContextSelectorConf conf) {
		if (aggregatedFeatureEventConf == null) {
			throw new InvalidAggregatedFeatureEventConfNameException(conf.getAggregatedFeatureEventConfName());
		}
	}
}
