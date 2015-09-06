package fortscale.streaming.alert.subscribers.evidence.filter;

import fortscale.aggregation.feature.event.AggrEvent;
import fortscale.domain.core.Evidence;

import java.util.*;

/**
 * Created by tomerd on 06/09/2015.
 */
public class FilterByHighScorePerValue implements EvidenceFilter{

	/**
	 * Filter evidence list by highest score per unique evidence type
	 * @param evidences
	 * @param aggregatedFeatureEvent
	 */
	@Override public void filterList(List<Evidence> evidences, AggrEvent aggregatedFeatureEvent) {

		// Map to hold the unique evidences
		Map<AbstractMap.SimpleEntry<String, String>, Evidence> evidenceMap = new HashMap<>();

		// Iterate the evidences
		for (Evidence evidence : evidences){

			// Read the current evidence data entity
			String dataEntity = evidence.getDataEntitiesIds().get(0);

			// Create new key from the current evidence
			AbstractMap.SimpleEntry<String, String> evidenceKey =
					new AbstractMap.SimpleEntry<String, String>(dataEntity, evidence.getAnomalyType());

			// If we read evidence from that type before, keep the one with the highest score
			if (evidence.getScore() > evidenceMap.get(evidenceKey).getScore()){
				evidenceMap.put(evidenceKey, evidence);
			}
		}

		// In case The number of evidences after the filter is not match the P value
		if (!aggregatedFeatureEvent.getAggregatedFeatureValue().equals(evidenceMap.size())) {
			// TODO: what to do?
		}

		// populate the evidences list with the filtered evidences map
		evidences.clear();
		evidences.addAll(evidenceMap.values());
	}
}
