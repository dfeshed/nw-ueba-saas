package fortscale.ml.model.selector;

import fortscale.aggregation.feature.event.AggregatedFeatureEventConf;
import fortscale.aggregation.feature.event.AggregatedFeatureEventsConfService;
import fortscale.aggregation.feature.event.store.AggregatedFeatureEventsReaderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.util.Assert;

import java.util.Date;
import java.util.List;

@Configurable(preConstruction = true)
public class AggregatedEventContextSelector implements IContextSelector {
	@Autowired
	private AggregatedFeatureEventsConfService aggregatedFeatureEventsConfService;
	@Autowired
	private AggregatedFeatureEventsReaderService aggregatedFeatureEventsReaderService;

	private AggregatedFeatureEventConf aggregatedFeatureEventConf;

	public AggregatedEventContextSelector(AggregatedEventContextSelectorConf config) {
		String aggregatedFeatureEventConfName = config.getAggregatedFeatureEventConfName();
		aggregatedFeatureEventConf = aggregatedFeatureEventsConfService
				.getAggregatedFeatureEventConf(aggregatedFeatureEventConfName);
		Assert.notNull(aggregatedFeatureEventConf);
	}

	@Override
	public List<String> getContexts(Date startTime, Date endTime) {
		return aggregatedFeatureEventsReaderService.findDistinctContextsByTimeRange(
				aggregatedFeatureEventConf, startTime, endTime);
	}
}
