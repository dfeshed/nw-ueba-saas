//package fortscale.ml.model.prevalance.field;
//
//import java.util.List;
//import java.util.Map;
//
//public class MediansQuantilesModelBuilder extends PopulationQuantilesModelBuilder {
//	@Override
//	protected void feedBuilder(ContinuousDataDistribution localDistribution) {
//		// Get sorted list of entries
//		List<Map.Entry<Double, Long>> sortedEntries = getSortedEntries(localDistribution.getDistribution());
//
//		// Calculate index of median value
//		double medianIndex = 0.5d * localDistribution.getTotalCount();
//
//		/* Iterate the list of entries and increment the running
//		 * index according to the counts until the median is found */
//		long currentIndex = 0;
//		for (Map.Entry<Double, Long> entry : sortedEntries) {
//			currentIndex += entry.getValue();
//			if (medianIndex <= currentIndex) {
//				addValue(entry.getKey(), 1L);
//				return;
//			}
//		}
//	}
//}
