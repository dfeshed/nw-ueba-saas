package fortscale.ml.model.retriever;

import fortscale.aggregation.feature.event.AggrEvent;
import fortscale.aggregation.feature.event.AggregatedFeatureEventConf;
import fortscale.aggregation.feature.event.AggregatedFeatureEventsConfService;
import fortscale.aggregation.feature.event.store.AggregatedFeatureEventsReaderService;
import fortscale.common.util.GenericHistogram;
import fortscale.utils.time.TimestampUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.util.Assert;

import java.util.Date;
import java.util.List;

@Configurable(preConstruction = true)
public class AggregatedFeatureValueRetriever extends AbstractDataRetriever {
	@Autowired
	private AggregatedFeatureEventsConfService aggregatedFeatureEventsConfService;
	@Autowired
	private AggregatedFeatureEventsReaderService aggregatedFeatureEventsReaderService;

	private AggregatedFeatureEventConf aggregatedFeatureEventConf;

	public AggregatedFeatureValueRetriever(AggregatedFeatureValueRetrieverConf config) {
		super(config);

		String aggregatedFeatureEventConfName = config.getAggregatedFeatureEventConfName();
		aggregatedFeatureEventConf = aggregatedFeatureEventsConfService
				.getAggregatedFeatureEventConf(aggregatedFeatureEventConfName);
		Assert.notNull(aggregatedFeatureEventConf);
	}

	@Override
	public Object retrieve(String contextId, Date endTime) {
		long endTimeInSeconds = TimestampUtils.convertToSeconds(endTime.getTime());
		long startTimeInSeconds = endTimeInSeconds - timeRangeInSeconds;
		Date startTime = new Date(TimestampUtils.convertToMilliSeconds(startTimeInSeconds));

		List<AggrEvent> aggrEvents = aggregatedFeatureEventsReaderService
				.findAggrEventsByContextIdAndTimeRange(
				aggregatedFeatureEventConf, contextId, startTime, endTime);
		GenericHistogram reductionHistogram = new GenericHistogram();

		for (AggrEvent aggrEvent : aggrEvents) {
			Double aggregatedFeatureValue = aggrEvent.getAggregatedFeatureValue();
			// TODO: Retriever functions should be iterated and executed here.
			reductionHistogram.add(aggregatedFeatureValue, 1d);
		}

		return reductionHistogram.getN() > 0 ? reductionHistogram : null;
	}
}
