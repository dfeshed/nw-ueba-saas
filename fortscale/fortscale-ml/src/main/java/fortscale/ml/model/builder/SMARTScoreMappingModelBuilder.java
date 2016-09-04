package fortscale.ml.model.builder;

import fortscale.ml.model.SMARTScoreMappingModel;
import org.springframework.util.Assert;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.OptionalInt;
import java.util.stream.Collectors;

public class SMARTScoreMappingModelBuilder implements IModelBuilder {
    private static final String MODEL_BUILDER_DATA_TYPE_ERROR_MSG = String.format(
            "Model builder data must be of type %s.", Map.class.getSimpleName());
    static final double EPSILON = 0.00000001;

    private double minThreshold;
    private double minMaximalScore;
    private double lowOutliersFraction;
    private double highOutliersFraction;

    public SMARTScoreMappingModelBuilder(SMARTScoreMappingModelBuilderConf config) {
        minThreshold = config.getMinThreshold();
        minMaximalScore = config.getMinMaximalScore();
        lowOutliersFraction = config.getLowOutliersFraction();
        highOutliersFraction = config.getHighOutliersFraction();
    }

    @Override
    public SMARTScoreMappingModel build(Object modelBuilderData) {
        Map<Long, List<Double>> dateToHighestScores = castModelBuilderData(modelBuilderData);
        SMARTScoreMappingModel model = new SMARTScoreMappingModel();
        List<List<Double>> scoresPerDay = filterPartialDays(dateToHighestScores);
        double threshold;
        double maximalScore;
        if (scoresPerDay.isEmpty()) {
            threshold = 50;
            maximalScore = 100;
        } else {
            int numOfDays = dateToHighestScores.size();
            // EntityEventUnreducedScoreRetriever retrieves numOfDays * numOfAlertsPerDay entities per day
            int numOfAlertsPerDay = scoresPerDay.get(0).size() / numOfDays;
            threshold = Math.max(minThreshold, calcThreshold(scoresPerDay, numOfDays, numOfAlertsPerDay) + EPSILON);
            maximalScore = Math.max(minMaximalScore, calcMaximalScore(scoresPerDay));
        }
        if (threshold > maximalScore) {
            maximalScore = threshold;
        }
        model.init(threshold, maximalScore);
        return model;
    }

    private List<List<Double>> filterPartialDays(Map<Long, List<Double>> dateToHighestScores) {
        OptionalInt numOfScoresPerDay = dateToHighestScores.values().stream()
                .mapToInt(List::size)
                .max();
        return dateToHighestScores.values().stream()
                .filter(scores -> numOfScoresPerDay.isPresent() && numOfScoresPerDay.getAsInt() > 0 && scores.size() == numOfScoresPerDay.getAsInt())
                .collect(Collectors.toList());
    }

    private double calcThreshold(List<List<Double>> scoresPerDay, int numOfDays, int numOfAlertsPerDay) {
        long numOfLowOutliers = (long) Math.floor(scoresPerDay.size() * lowOutliersFraction);
        long numOHighOutliers = (long) Math.floor(scoresPerDay.size() * highOutliersFraction);
        long numOfDaysToUse = scoresPerDay.size() - numOfLowOutliers - numOHighOutliers;
        return scoresPerDay.stream()
                // sort by the lowest (highest) score per day (so we can filter outliers)
                .sorted((scores1, scores2) -> Double.compare(scores1.get(scores1.size() - 1), scores2.get(scores2.size() - 1)))
                // filter the low outliers
                .skip(numOfLowOutliers)
                // filter the high outliers
                .limit(numOfDaysToUse)
                .flatMap(Collection::stream)
                // reverse sort them
                .sorted((s1, s2) -> Double.compare(s2, s1))
                // and take the N'th highest (where N is the desired number of alerts per day times the number of days we use after discarding outliers)
                .skip(numOfDaysToUse * numOfAlertsPerDay - 1)
                .findFirst()
                .get();
    }

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
