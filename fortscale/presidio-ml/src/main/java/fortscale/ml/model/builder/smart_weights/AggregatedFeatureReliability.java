package fortscale.ml.model.builder.smart_weights;

import fortscale.ml.model.retriever.smart_data.SmartAggregatedRecordData;
import fortscale.ml.model.retriever.smart_data.SmartAggregatedRecordDataContainer;
import fortscale.utils.logging.Logger;
import fortscale.utils.time.TimestampUtils;

import java.time.Duration;
import java.util.*;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Full documentation can be found here: https://fortscale.atlassian.net/wiki/pages/viewpage.action?pageId=75071492
 * This class can calculate a penalty for every aggregated feature event based on how reliable the feature is.
 * Reliability is a function of the feature's scores histogram: the more high scores it gives, the less reliable
 * it is. Moreover, if the histogram doesn't behave (i.e. it isn't monotonically decreasing), the penalty gets higher.
 * Schematically without being precise with the details, the penalty calculation scheme is composed of several steps:
 * 1. From each feature's scores histogram we calculate a "shadowed" histogram ("+" are the original data,
 * and "o" are the result of the shadowing):
 * <p>
 * count                                                       count
 * ^                                                           ^
 * |                                                           |                                     \| / (the sun)
 * |                                                           |                                    - O -
 * |                                                           |                                    / | \
 * |                                                    \      |
 * |                                              -------\     |
 * |         +                                            |    |         +
 * |         + +                                  -------/     |         + +
 * |         + + +                                      /      |         + + +
 * |         + + +                        +                    |       o + + +                        +
 * |         + + +           +            +                    |       o + + +           +            +
 * |       + + + +           + +          +                    |     o + + + +           + +        o +
 * |       + + + +           + +          +                    |   o o + + + +         o + +      o o +
 * |________________________________________> score            |________________________________________> score
 * 0                                     100                   0                                     100
 * <p>
 * 2. A "typical scores histogram" is calculated (that is, a histogram where the count for each score equals
 * to the quartile count of all the features' shadowed histograms).
 * 3. The typical histogram is subtracted from each feature's histogram (each bar is truncated at zero).
 * 3. Each bar is stretched as a function of the score (the higher the score the more penalty we want to
 * get from the score's count).
 * 4. The penalty is the resulting histogram's area.
 */
public class AggregatedFeatureReliability {
    private static final Logger logger = Logger.getLogger(AggregatedFeatureReliability.class);
    static final double SHADOWING_DECAY_FACTOR = 0.9;

    private Map<String, Map<Integer, Double>> fullAggregatedFeatureEventNameToScoresHist;
    private Map<Integer, Double> typicalHist;
    private int numOfContexts;
    private long numOfDays;

    public AggregatedFeatureReliability(List<SmartAggregatedRecordDataContainer> smartAggregatedRecordDataContainers, int numOfContexts) {
        fullAggregatedFeatureEventNameToScoresHist = calcShadowedHists(calcScoresHistPerFeature(smartAggregatedRecordDataContainers));
        typicalHist = calcTypicalHist(fullAggregatedFeatureEventNameToScoresHist);
        long minStartTime = smartAggregatedRecordDataContainers.stream()
                .mapToLong(smartAggregatedRecordDataContainer -> smartAggregatedRecordDataContainer.getStartTime().toEpochMilli() / 1000)
                .min()
                .orElseGet(() -> 0);
        long maxStartTime = smartAggregatedRecordDataContainers.stream()
                .mapToLong(smartAggregatedRecordDataContainer -> smartAggregatedRecordDataContainer.getStartTime().toEpochMilli() / 1000)
                .max()
                .orElseGet(() -> 0);
        numOfDays = Duration.ofSeconds(TimestampUtils.convertToSeconds(maxStartTime - minStartTime)).toDays() + 1;
        this.numOfContexts = numOfContexts;
        logger.debug("{} contexts through {} days", numOfContexts, numOfDays);
    }

    Map<Integer, Double> getScoresHist(String fullAggregatedFeatureEventName) {
        return fullAggregatedFeatureEventNameToScoresHist.getOrDefault(
                fullAggregatedFeatureEventName, Collections.emptyMap());
    }

    public double calcReliabilityPenalty(String fullAggregatedFeatureEventName) {
        double area = calcWeightedAreaOfHistDeviatedFromTypicalHist(getScoresHist(fullAggregatedFeatureEventName), typicalHist);
        return Math.max(0, allowOneHighScorePerUserPerYear(area));
    }

    private double allowOneHighScorePerUserPerYear(double area) {
        // area can be treated as the number of high scores occurred in the organization.
        // We want to allow every user to generate one high score per year.
        // Hence, the number of users multiplied by the number of years are legitimate high score,
        // and should be removed from the overall penalty.
        logger.debug("area {}, numOfContexts {}, numOfDays {}", area, numOfContexts, numOfDays);
        double userPercentage = 0.4; // TODO: This should be configurable
        return area - userPercentage * numOfContexts * numOfDays / 365.0;
    }

    static double calcWeightedAreaOfHistDeviatedFromTypicalHist(Map<Integer, Double> scoresHist, Map<Integer, Double> typicalHist) {
        return scoresHist.entrySet().stream()
                // calculate weighted area for every column separately
                .mapToDouble(scoreAndCount -> calcWeightedAreaOfColumnDeviatedFromTypicalHist(
                        scoreAndCount.getKey(),
                        scoreAndCount.getValue(),
                        typicalHist
                ))
                // sum the areas
                .sum();
    }

    /**
     * Given a bar in a scores histogram, calculate the weighted area of the part of the bar which
     * deviates from the respective bar in the typical histogram.
     * "Weighted" means that if score == 100 each deviated count should be treated as one, and if score < 100,
     * each deviated count is treated as less than one. As score decreases, the weight decreases in a sub-linear manner.
     * Scores above 100 (which only happens for Ps and not Fs) result in linear increase of the weight.
     * Schematically it looks like this:
     * <p>
     * weight
     * ^                              *
     * |                           *
     * |                        *
     * |                     *
     * |                  *
     * 1 |                 *
     * |                 *
     * |                *
     * |               *
     * |             **
     * |          ***
     * |     *****
     * |*****
     * |______________________________> score
     * 0               100
     */
    private static double calcWeightedAreaOfColumnDeviatedFromTypicalHist(int score,
                                                                          double count,
                                                                          Map<Integer, Double> typicalHist) {
        // subtract a typical count for this score. Note that score is a valid key of typicalHist by construction
        count -= typicalHist.get(score);
        if (count <= 0) {
            // the count wasn't really high relative to the typical count, so we shouldn't give it a penalty
            return 0;
        }
        double s = score * 0.01;
        if (s < 1) {
            // we want to give really small penalty to small scores (in a sub-linear manner)
            s = Math.pow(s, 3);
        }
        return count * s;
    }

    private Map<String, Map<Integer, Integer>> calcScoresHistPerFeature(List<SmartAggregatedRecordDataContainer> smartAggregatedRecordDataContainers) {
        Map<String, Map<Integer, Integer>> scoresHistPerFeature = smartAggregatedRecordDataContainers.stream()
                // create a stream of all the features
                .flatMap(smartAggregatedRecordDataContainer -> smartAggregatedRecordDataContainer.getSmartAggregatedRecordsData().stream())
                .collect(Collectors.groupingBy(
                        // group by feature name
                        SmartAggregatedRecordData::getFeatureName,
                        // and create a histogram from each feature's data
                        Collectors.toMap(
                                aggregatedRecordData -> (int) Math.round(aggregatedRecordData.getScore()),
                                aggregatedRecordData -> 1,
                                Integer::sum
                        )
                ));
        logScoresHistPerFeature("scores hist per feature", scoresHistPerFeature);
        return scoresHistPerFeature;
    }

    private static <T extends Number> void logScoresHist(String scoresHistName, Map<Integer, T> scoresHist) {
        logger.debug("{}:", scoresHistName);
        logger.debug(scoresHist.entrySet().stream()
                .sorted(Comparator.comparing(Map.Entry::getKey))
                .map(scoreAndCount -> String.format("%d: %s", scoreAndCount.getKey(), scoreAndCount.getValue()))
                .collect(Collectors.joining(", ")));
    }

    private static <T extends Number> void logScoresHistPerFeature(
            String scoresHistPerFeatureName, Map<String, Map<Integer, T>> scoresHistPerFeature) {

        logger.debug("{}:", scoresHistPerFeatureName);
        scoresHistPerFeature.entrySet().forEach(featureAndScoreHist ->
                logScoresHist(featureAndScoreHist.getKey(), featureAndScoreHist.getValue()));
    }

    private Map<String, Map<Integer, Double>> calcShadowedHists(Map<String, Map<Integer, Integer>> fullAggregatedFeatureEventNameToScoresHist) {
        Map<String, Map<Integer, Double>> shadowedHists = fullAggregatedFeatureEventNameToScoresHist.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        nameAndScoresHist -> calcShadowedHist(nameAndScoresHist.getValue())
                ));
        logScoresHistPerFeature("shadowed scores hist per feature", shadowedHists);
        return shadowedHists;
    }

    /**
     * Histograms with peaks are smoothed a little bit such that the picks are less noticeable.
     * Each peak is smoothed to its left, as if the sun is rising to the right of it.
     */
    private Map<Integer, Double> calcShadowedHist(Map<Integer, Integer> scoresHist) {
        Map<Integer, Double> shadowedHist = new HashMap<>();
        double maxCount = 0;
        for (int score = Collections.max(scoresHist.keySet()); score >= 0; score--) {
            maxCount = Math.max(maxCount * SHADOWING_DECAY_FACTOR, scoresHist.getOrDefault(score, 0));
            shadowedHist.put(score, maxCount);
        }
        return shadowedHist;
    }

    /**
     * Create a histogram where the count for each score equals to the quartile count of all the given histograms.
     */
    static Map<Integer, Double> calcTypicalHist(Map<String, Map<Integer, Double>> fullAggregatedFeatureEventNameToScoresHist) {
        Map<Integer, List<Double>> scoreToCounts = fullAggregatedFeatureEventNameToScoresHist.values().stream()
                // create a stream of all the histograms' <score, count> pairs
                .flatMap(scoresHist -> scoresHist.entrySet().stream())
                .collect(Collectors.toMap(
                        (Function<Map.Entry<Integer, Double>, Integer>) Map.Entry::getKey,
                        scoreAndCount -> Collections.singletonList(scoreAndCount.getValue()),
                        // merge counts from several histograms into a list
                        (BinaryOperator<List<Double>>) (counts1, counts2) -> Stream.concat(
                                counts1.stream(),
                                counts2.stream()
                        ).collect(Collectors.toList())
                ));
        Map<Integer, Double> typicalHist =
                pickQuartileCountPerScore(fullAggregatedFeatureEventNameToScoresHist.size(), scoreToCounts);
        logScoresHist("typical hist", typicalHist);
        return typicalHist;
    }

    private static Map<Integer, Double> pickQuartileCountPerScore(int numOfHists, Map<Integer, List<Double>> scoreToCounts) {
        return scoreToCounts.entrySet().stream()
                // create a histogram
                .collect(Collectors.toMap(
                        // where the key is the original key (i.e. score)
                        Map.Entry::getKey,
                        // and the value is the quartile count
                        scoreAndCounts -> scoreAndCounts.getValue().stream()
                                .sorted(Comparator.reverseOrder())
                                .skip(numOfHists * 3 / 4)
                                .findFirst()
                                // if there's not enough data - use zero
                                .orElseGet(() -> 0.0)
                ));
    }
}
