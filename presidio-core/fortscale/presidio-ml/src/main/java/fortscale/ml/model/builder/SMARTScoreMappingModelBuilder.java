package fortscale.ml.model.builder;

import fortscale.ml.model.SMARTScoreMappingModel;
import org.springframework.util.Assert;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Documentation of the alert control can be found here: https://fortscale.atlassian.net/wiki/display/FSC/Alert+Control
 */
public class SMARTScoreMappingModelBuilder implements IModelBuilder {
    private static final String MODEL_BUILDER_DATA_TYPE_ERROR_MSG = String.format(
            "Model builder data must be of type %s.", Map.class.getSimpleName());
	static final double EPSILON = 0.00000001;

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
			// EntityEventUnreducedScoreRetriever retrieves numOfDays * numOfAlertsPerDay + 1 entities per day.
			// Solving for numOfAlertsPerDay:
			double numOfAlertsPerDay = ((double) (scoresPerDay.get(0).size() - 1)) / dateToHighestScores.size();
			scoresPerDay.forEach(dailyScores -> dailyScores.sort(Comparator.reverseOrder()));
			threshold = Math.max(config.getMinThreshold(), calcThreshold(scoresPerDay, numOfAlertsPerDay));
			maximalScore = Math.max(config.getMinMaximalScore(), calcMaximalScore(scoresPerDay));
			// calcThreshold might return the maximal score plus EPSILON in some situations,
			// which is bigger than what aclMaximalScore can return, so make sure the maximalScore is still bigger
			maximalScore = Math.max(maximalScore, threshold + EPSILON);
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
	 * @param numOfAlertsPerDay the number of alerts that should be triggered per day.
	 * @return the value which will be mapped to 50 (50 and above will trigger an alert).
	 */
	private double calcThreshold(List<List<Double>> scoresPerDay, double numOfAlertsPerDay) {
		long numOfLowOutliers = (long) Math.floor(scoresPerDay.size() * config.getLowOutliersFraction());
		long numOHighOutliers = (long) Math.floor(scoresPerDay.size() * config.getHighOutliersFraction());
		long numOfDaysToUse = scoresPerDay.size() - numOfLowOutliers - numOHighOutliers;
		List<Double> scores = scoresPerDay.stream()
				// sort by the lowest (highest) score per day (so we can filter outliers based on the median)
				.sorted((scores1, scores2) -> Double.compare(scores1.get(scores1.size() / 2), scores2.get(scores2.size() / 2)))
				// filter the low & high outliers
				.skip(numOfLowOutliers)
				.limit(numOfDaysToUse)
				// take the highest distinct scores
				.flatMap(Collection::stream)
				.sorted((s1, s2) -> Double.compare(s2, s1))
				.limit((long) (numOfDaysToUse * numOfAlertsPerDay + 1))
				.distinct()
				// and take the average of the two smallest ones
				.sorted()
				.limit(2)
				.collect(Collectors.toList());
		if (scores.size() == 1) {
			return scores.get(0) + EPSILON;
		}
		return (scores.get(0) + scores.get(1)) / 2;
	}

	/**
	 * @param scoresPerDay a list of days. For each day it has a list of the highest scores in that day.
	 * @return the value that will be mapped to 100.
	 */
	private double calcMaximalScore(List<List<Double>> scoresPerDay) {
		return scoresPerDay.stream()
				.mapToDouble(scores -> scores.get(0))
				.max()
				.getAsDouble();
	}

    protected Map<Long, List<Double>> castModelBuilderData(Object modelBuilderData) {
        Assert.isInstanceOf(Map.class, modelBuilderData, MODEL_BUILDER_DATA_TYPE_ERROR_MSG);
        return (Map<Long, List<Double>>) modelBuilderData;
    }
}
