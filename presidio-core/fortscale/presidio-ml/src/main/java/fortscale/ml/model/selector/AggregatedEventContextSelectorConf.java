package fortscale.ml.model.selector;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.util.Assert;

public class AggregatedEventContextSelectorConf implements IContextSelectorConf {
	public static final String AGGREGATED_EVENT_CONTEXT_SELECTOR = "aggregated_event_context_selector";

	private String aggregatedFeatureEventConfName;

	@JsonCreator
	public AggregatedEventContextSelectorConf(
			@JsonProperty("aggregatedFeatureEventConfName") String aggregatedFeatureEventConfName) {

		Assert.hasText(aggregatedFeatureEventConfName);
		this.aggregatedFeatureEventConfName = aggregatedFeatureEventConfName;
	}

	@Override
	public String getFactoryName() {
		return AGGREGATED_EVENT_CONTEXT_SELECTOR;
	}

	public String getAggregatedFeatureEventConfName() {
		return aggregatedFeatureEventConfName;
	}
}
