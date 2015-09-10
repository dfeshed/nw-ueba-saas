package fortscale.streaming.alert.subscribers.evidence.filter;

import fortscale.aggregation.feature.event.AggrEvent;
import fortscale.domain.core.Evidence;

import java.util.List;

/**
 * Created by tomerd on 06/09/2015.
 */
public interface EvidenceFilter {

	/**
	 * Filter Evidence list
	 * @param evidences
	 * @param aggregatedFeatureEvent
	 */
	void filterList(List<Evidence> evidences, AggrEvent aggregatedFeatureEvent);
}
