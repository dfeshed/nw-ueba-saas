package fortscale.streaming.alert.subscribers.evidence.filter;

import fortscale.aggregation.feature.event.AggrEvent;
import fortscale.domain.core.Evidence;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class FilterByHighScore implements EvidenceFilter {

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

		// TODO: what to do in case max is null?
		evidences.clear();
		evidences.add(max);
	}
}
