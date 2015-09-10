package fortscale.streaming.alert.subscribers.evidence.filter;

import fortscale.aggregation.feature.event.AggrEvent;
import fortscale.domain.core.Evidence;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class FilterByHighestScore implements EvidenceFilter {

	/**
	 * Find the evidence with the max score
	 * @param evidences
	 * @param aggregatedFeatureEvent
	 */
	@Override public void filterList(List<Evidence> evidences, AggrEvent aggregatedFeatureEvent) {
		Evidence max = Collections.max(evidences, new Comparator<Evidence>() {
			@Override public int compare(Evidence o1, Evidence o2) {
				if (o1.getScore() > o2.getScore()) {
					return 1;
				} else if (o1.getScore() < o2.getScore()) {
					return -1;
				}

				return 0;
			}
		});

		evidences.clear();
		evidences.add(max);
	}
}
