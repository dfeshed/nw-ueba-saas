package fortscale.streaming.alert.subscribers.evidence.filter;

import fortscale.aggregation.feature.event.AggrEvent;
import fortscale.domain.core.Evidence;

import java.util.*;

/**
 * Created by tomerd on 06/09/2015.
 */
public class FilterByHighScorePerUnqiuePValue implements EvidenceFilter{

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
					new AbstractMap.SimpleEntry<String, String>(dataEntity, evidence.getAnomalyValue());

			// If it the first time we read this key, add it to map
			if (!evidenceMap.containsKey(evidenceKey)){
				evidenceMap.put(evidenceKey, evidence);
			}
			// If we read evidence from that type before, keep the one with the highest score
			else if (evidence.getScore() > evidenceMap.get(evidenceKey).getScore()){
				evidenceMap.put(evidenceKey, evidence);
			}
		}

		// populate the evidences list with the filtered evidences map
		evidences.clear();
		evidences.addAll(evidenceMap.values());
	}
}
