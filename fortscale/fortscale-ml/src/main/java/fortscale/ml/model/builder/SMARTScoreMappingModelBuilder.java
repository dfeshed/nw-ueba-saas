package fortscale.ml.model.builder;

import fortscale.ml.model.SMARTScoreMappingModel;
import org.springframework.util.Assert;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.OptionalInt;
import java.util.stream.Collectors;

/**
 * Documentation of the alert control can be found here: https://fortscale.atlassian.net/wiki/display/FSC/Alert+Control
 */
public class SMARTScoreMappingModelBuilder implements IModelBuilder {
    private static final String MODEL_BUILDER_DATA_TYPE_ERROR_MSG = String.format(
            "Model builder data must be of type %s.", Map.class.getSimpleName());

	private SMARTScoreMappingModelBuilderConf config;

    public SMARTScoreMappingModelBuilder(SMARTScoreMappingModelBuilderConf config) {
		this.config = config;
    }

    @Override
    public SMARTScoreMappingModel build(Object modelBuilderData) {
        Map<Long, List<Double>> dateToHighestScores = castModelBuilderData(modelBuilderData);
        SMARTScoreMappingModel model = new SMARTScoreMappingModel();
		List<List<Double>> scoresPerDay = filterPartialDays(dateToHighestScores);
		double threshold;
		double maximalScore;
		if (!scoresPerDay.isEmpty()) {
			int numOfDays = dateToHighestScores.size();
			// EntityEventUnreducedScoreRetriever retrieves numOfDays * numOfAlertsPerDay + 1 entities per day
			int numOfAlertsPerDay = (scoresPerDay.get(0).size() - 1) / numOfDays;
			threshold = Math.max(config.getMinThreshold(), calcThreshold(scoresPerDay, numOfDays, numOfAlertsPerDay));
			maximalScore = Math.max(config.getMinMaximalScore(), calcMaximalScore(scoresPerDay));
		} else {
			threshold = config.getDefaultThreshold();
			maximalScore = config.getDefaultMaximalScore();
		}
		model.init(threshold, maximalScore);
        return model;
    }

	/**
	 * Filter out days with partial or no scores at all.
	 * @param dateToHighestScores a mapping from a day to its highest scores.
	 */
	private List<List<Double>> filterPartialDays(Map<Long, List<Double>> dateToHighestScores) {
		OptionalInt numOfScoresPerDay = dateToHighestScores.values().stream()
				.mapToInt(List::size)
				.max();
		return dateToHighestScores.values().stream()
				.filter(scores -> numOfScoresPerDay.isPresent() && numOfScoresPerDay.getAsInt() > 0 && scores.size() == numOfScoresPerDay.getAsInt())
				.collect(Collectors.toList());
	}

	/**
	 * @param scoresPerDay a list of days. For each day it has a list of the highest scores in that day.
	 * @param numOfDays the number of days (before filtering by filterPartialDays).
	 * @param numOfAlertsPerDay the number of alerts that should be triggered per day.
	 * @return the value which will be mapped to 50 (50 and above will trigger an alert).
	 */
	private double calcThreshold(List<List<Double>> scoresPerDay, int numOfDays, int numOfAlertsPerDay) {
		long numOfLowOutliers = (long) Math.floor(scoresPerDay.size() * config.getLowOutliersFraction());
		long numOHighOutliers = (long) Math.floor(scoresPerDay.size() * config.getHighOutliersFraction());
		long numOfDaysToUse = scoresPerDay.size() - numOfLowOutliers - numOHighOutliers;
		return scoresPerDay.stream()
				// sort by the lowest (highest) score per day (so we can filter outliers)
				.sorted((scores1, scores2) -> Double.compare(scores1.get(scores1.size() - 1), scores2.get(scores2.size() - 1)))
				// filter the low & high outliers
				.skip(numOfLowOutliers)
				.limit(numOfDaysToUse)
				// take the highest distinct scores
				.flatMap(Collection::stream)
				.sorted((s1, s2) -> Double.compare(s2, s1))
				.limit(numOfDaysToUse * numOfAlertsPerDay + 1)
				.distinct()
				// and take the average of the two smallest ones
				.sorted()
				.limit(2)
				.mapToDouble(s -> s)
				.average()
				.getAsDouble();
	}

	/**
	 * @param scoresPerDay a list of days. For each day it has a list of the highest scores in that day.
	 * @return the value that will be mapped to 100.
	 */
	private double calcMaximalScore(List<List<Double>> scoresPerDay) {
		return scoresPerDay.stream()
				.mapToDouble(scores -> scores.get(scores.size() - 1))
				.max()
				.getAsDouble();
	}

    protected Map<Long, List<Double>> castModelBuilderData(Object modelBuilderData) {
        Assert.isInstanceOf(Map.class, modelBuilderData, MODEL_BUILDER_DATA_TYPE_ERROR_MSG);
        return (Map<Long, List<Double>>) modelBuilderData;
    }
}
