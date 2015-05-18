package fortscale.ml.model.prevalance.field;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class MediansQuantilesModelBuilder extends PopulationQuantilesModelBuilder {
	@Override
	protected void feedBuilder(ContinuousDataDistribution localDistribution) {
		Map<Double, Long> dist = localDistribution.getDistribution();
		Long count = localDistribution.getTotalCount();

		// Get sorted list of keys
		List<Double> sortedValues = new ArrayList<>(dist.keySet());
		Collections.sort(sortedValues);

		// Calculate index of median value
		double medianIndex = 0.5d * count;

		/* Iterate the list of keys and increment the running
		 * index accordingly until the median is found */
		long upperIndex = 0;
		for (Double value : sortedValues) {
			upperIndex += dist.get(value);
			if (medianIndex <= upperIndex) {
				addValue(value, 1L);
				return;
			}
		}
	}
}
