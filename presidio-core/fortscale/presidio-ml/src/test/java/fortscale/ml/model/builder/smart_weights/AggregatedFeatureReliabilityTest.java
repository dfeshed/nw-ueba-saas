package fortscale.ml.model.builder.smart_weights;

import fortscale.ml.model.retriever.smart_data.SmartAggregatedRecordDataContainer;
import org.junit.Assert;
import org.junit.Test;

import java.util.*;


/**
 * Created by barak_schuster on 31/08/2017.
 */
public class AggregatedFeatureReliabilityTest {
    @Test
    public void shouldCreateHistOutOfOneReliableFeatureOccurrence() {
        String fullAggregatedFeatureEventName = "fullAggregatedFeatureEventName";
        List<SmartAggregatedRecordDataContainer> smartAggregatedRecordDataContainers =
                Collections.singletonList(new SmartAggregatedRecordDataContainer(1234, Collections.singletonMap(fullAggregatedFeatureEventName, 0.0)));

        Map<Integer, Double> scoresHist = new AggregatedFeatureReliability(smartAggregatedRecordDataContainers, 1)
                .getScoresHist(fullAggregatedFeatureEventName);

        Assert.assertEquals(Collections.singletonMap(0, 1.0), scoresHist);
    }

    @Test
    public void shouldRoundScore() {
        String fullAggregatedFeatureEventName = "fullAggregatedFeatureEventName";
        List<SmartAggregatedRecordDataContainer> smartAggregatedRecordDataContainers = Arrays.asList(
                new SmartAggregatedRecordDataContainer(1234, Collections.singletonMap(fullAggregatedFeatureEventName, 0.0)),
                new SmartAggregatedRecordDataContainer(1234, Collections.singletonMap(fullAggregatedFeatureEventName, 0.1))
        );

        Map<Integer, Double> scoresHist = new AggregatedFeatureReliability(smartAggregatedRecordDataContainers, 1)
                .getScoresHist(fullAggregatedFeatureEventName);

        Assert.assertEquals(Collections.singletonMap(0, 2.0), scoresHist);
    }

    @Test
    public void shouldCreateHistOutOfTwoReliableFeatureOccurrences() {
        String fullAggregatedFeatureEventName = "fullAggregatedFeatureEventName";
        SmartAggregatedRecordDataContainer smartAggregatedRecordDataContainer = new SmartAggregatedRecordDataContainer(1234, Collections.singletonMap(fullAggregatedFeatureEventName, 0.0));
        List<SmartAggregatedRecordDataContainer> smartAggregatedRecordDataContainers = Arrays.asList(smartAggregatedRecordDataContainer, smartAggregatedRecordDataContainer);

        Map<Integer, Double> scoresHist = new AggregatedFeatureReliability(smartAggregatedRecordDataContainers, 1)
                .getScoresHist(fullAggregatedFeatureEventName);

        Assert.assertEquals(Collections.singletonMap(0, 2.0), scoresHist);
    }

    @Test
    public void shouldCreateHistOutOfOneUnreliableFeatureOccurrence() {
        String fullAggregatedFeatureEventName = "fullAggregatedFeatureEventName";
        List<SmartAggregatedRecordDataContainer> smartAggregatedRecordDataContainers =
                Collections.singletonList(new SmartAggregatedRecordDataContainer(1234, Collections.singletonMap(fullAggregatedFeatureEventName, 2.0)));

        Map<Integer, Double> scoresHist = new AggregatedFeatureReliability(smartAggregatedRecordDataContainers, 1)
                .getScoresHist(fullAggregatedFeatureEventName);

        Map<Integer, Double> expected = new HashMap<Integer, Double>() {{
            put(2, 1.0);
            put(1, 1.0 * AggregatedFeatureReliability.SHADOWING_DECAY_FACTOR);
            put(0, 1.0 * AggregatedFeatureReliability.SHADOWING_DECAY_FACTOR * AggregatedFeatureReliability.SHADOWING_DECAY_FACTOR);
        }};
        Assert.assertEquals(expected, scoresHist);
    }

    @Test
    public void shouldCreateHistOutOfMultipleUnreliableFeatureOccurrence() {
        String fullAggregatedFeatureEventName = "fullAggregatedFeatureEventName";
        List<SmartAggregatedRecordDataContainer> smartAggregatedRecordDataContainers = Arrays.asList(
                new SmartAggregatedRecordDataContainer(1234, Collections.singletonMap(fullAggregatedFeatureEventName, 2.0)),
                new SmartAggregatedRecordDataContainer(1234, Collections.singletonMap(fullAggregatedFeatureEventName, 2.0)),
                new SmartAggregatedRecordDataContainer(1234, Collections.singletonMap(fullAggregatedFeatureEventName, 1.0))
        );

        Map<Integer, Double> scoresHist = new AggregatedFeatureReliability(smartAggregatedRecordDataContainers, 1)
                .getScoresHist(fullAggregatedFeatureEventName);

        Map<Integer, Double> expected = new HashMap<Integer, Double>() {{
            put(2, 2.0);
            put(1, 2.0 * AggregatedFeatureReliability.SHADOWING_DECAY_FACTOR);
            put(0, 2.0 * AggregatedFeatureReliability.SHADOWING_DECAY_FACTOR * AggregatedFeatureReliability.SHADOWING_DECAY_FACTOR);
        }};
        Assert.assertEquals(expected, scoresHist);
    }

    @Test
    public void shouldCreateHistOutOfMultipleSemiUnreliableFeatureOccurrence() {
        String fullAggregatedFeatureEventName = "fullAggregatedFeatureEventName";
        List<SmartAggregatedRecordDataContainer> smartAggregatedRecordDataContainers = Arrays.asList(
                new SmartAggregatedRecordDataContainer(1234, Collections.singletonMap(fullAggregatedFeatureEventName, 2.0)),
                new SmartAggregatedRecordDataContainer(1234, Collections.singletonMap(fullAggregatedFeatureEventName, 1.0)),
                new SmartAggregatedRecordDataContainer(1234, Collections.singletonMap(fullAggregatedFeatureEventName, 1.0))
        );

        Map<Integer, Double> scoresHist = new AggregatedFeatureReliability(smartAggregatedRecordDataContainers, 1)
                .getScoresHist(fullAggregatedFeatureEventName);

        Map<Integer, Double> expected = new HashMap<Integer, Double>() {{
            put(2, 1.0);
            put(1, 2.0);
            put(0, 2.0 * AggregatedFeatureReliability.SHADOWING_DECAY_FACTOR);
        }};
        Assert.assertEquals(expected, scoresHist);
    }

    @Test
    public void shouldCreateOneHistPerFeature() {
        String fullAggregatedFeatureEventName1 = "fullAggregatedFeatureEventName1";
        String fullAggregatedFeatureEventName2 = "fullAggregatedFeatureEventName2";
        List<SmartAggregatedRecordDataContainer> smartAggregatedRecordDataContainers = Arrays.asList(
                new SmartAggregatedRecordDataContainer(1234, Collections.singletonMap(fullAggregatedFeatureEventName1, 0.0)),
                new SmartAggregatedRecordDataContainer(1234, Collections.singletonMap(fullAggregatedFeatureEventName2, 0.0)),
                new SmartAggregatedRecordDataContainer(1234, Collections.singletonMap(fullAggregatedFeatureEventName2, 0.0))
        );

        AggregatedFeatureReliability aggr = new AggregatedFeatureReliability(smartAggregatedRecordDataContainers, 1);
        Map<Integer, Double> scoresHist1 = aggr.getScoresHist(fullAggregatedFeatureEventName1);
        Map<Integer, Double> scoresHist2 = aggr.getScoresHist(fullAggregatedFeatureEventName2);

        Assert.assertEquals(Collections.singletonMap(0, 1.0), scoresHist1);
        Assert.assertEquals(Collections.singletonMap(0, 2.0), scoresHist2);
    }

    @Test
    public void shouldCalcSameTypicalHistAsGivenHist() {
        final Map<Integer, Double> scoresHist = new HashMap<Integer, Double>() {{
            put(0, 10.0);
            put(1, 5.0);
        }};
        Map<String, Map<Integer, Double>> fullAggregatedFeatureEventNameToScoresHist = new HashMap<String, Map<Integer, Double>>() {{
            put("fullAggregatedFeatureEventName1", scoresHist);
        }};
        Map<Integer, Double> typicalHist = AggregatedFeatureReliability.calcTypicalHist(fullAggregatedFeatureEventNameToScoresHist);

        Assert.assertEquals(scoresHist, typicalHist);
    }

    @Test
    public void shouldCalcSameTypicalHistAsGivenHistsWhenIdentical() {
        Map<String, Map<Integer, Double>> fullAggregatedFeatureEventNameToScoresHist = new HashMap<String, Map<Integer, Double>>() {{
            put("fullAggregatedFeatureEventName1", Collections.singletonMap(0, 1.0));
            put("fullAggregatedFeatureEventName2", Collections.singletonMap(0, 1.0));
            put("fullAggregatedFeatureEventName3", Collections.singletonMap(0, 1.0));
            put("fullAggregatedFeatureEventName4", Collections.singletonMap(0, 1.0));
        }};
        Map<Integer, Double> typicalHist = AggregatedFeatureReliability.calcTypicalHist(fullAggregatedFeatureEventNameToScoresHist);

        Assert.assertEquals(Collections.singletonMap(0, 1.0), typicalHist);
    }

    @Test
    public void shouldCalcQuartileCountWhenCalculatingTypicalHist() {
        Map<String, Map<Integer, Double>> fullAggregatedFeatureEventNameToScoresHist = new HashMap<String, Map<Integer, Double>>() {{
            put("fullAggregatedFeatureEventName1", Collections.singletonMap(0, 1.0));
            put("fullAggregatedFeatureEventName2", Collections.singletonMap(0, 2.0));
            put("fullAggregatedFeatureEventName3", Collections.singletonMap(0, 3.0));
            put("fullAggregatedFeatureEventName4", Collections.singletonMap(0, 4.0));
        }};
        Map<Integer, Double> typicalHist = AggregatedFeatureReliability.calcTypicalHist(fullAggregatedFeatureEventNameToScoresHist);

        Assert.assertEquals(Collections.singletonMap(0, 1.0), typicalHist);
    }

    @Test
    public void shouldUseZeroWhenCalculatingTypicalHistWhenNotEnoughData() {
        Map<String, Map<Integer, Double>> fullAggregatedFeatureEventNameToScoresHist = new HashMap<String, Map<Integer, Double>>() {{
            put("fullAggregatedFeatureEventName1", new HashMap<Integer, Double>() {{
                put(0, 1.0);
                put(1, 1.0);
            }});
            put("fullAggregatedFeatureEventName2", Collections.singletonMap(0, 2.0));
            put("fullAggregatedFeatureEventName3", Collections.singletonMap(0, 3.0));
            put("fullAggregatedFeatureEventName4", Collections.singletonMap(0, 4.0));
        }};
        Map<Integer, Double> typicalHist = AggregatedFeatureReliability.calcTypicalHist(fullAggregatedFeatureEventNameToScoresHist);

        Map<Integer, Double> expected = new HashMap<Integer, Double>() {{
            put(0, 1.0);
            put(1, 0.0);
        }};
        Assert.assertEquals(expected, typicalHist);
    }

    @Test
    public void shouldCalculateZeroAreaForTypicalFeature() {
        Map<Integer, Double> scoresHist = new HashMap<Integer, Double>() {{
            put(50, 1.0);
            put(60, 1.0);
        }};

        double area = AggregatedFeatureReliability.calcWeightedAreaOfHistDeviatedFromTypicalHist(scoresHist, scoresHist);

        Assert.assertEquals(0, area, 0.000001);
    }

    @Test
    public void shouldCalculateZeroAreaForFeatureMoreQuietThanTypicalHist() {
        Map<Integer, Double> scoresHist = new HashMap<Integer, Double>() {{
            put(50, 1.0);
            put(60, 1.0);
        }};
        Map<Integer, Double> typicalHist = new HashMap<Integer, Double>() {{
            put(50, 2.0);
            put(60, 2.0);
        }};

        double area = AggregatedFeatureReliability.calcWeightedAreaOfHistDeviatedFromTypicalHist(scoresHist, typicalHist);

        Assert.assertEquals(0, area, 0.000001);
    }

    @Test
    public void shouldCalculateZeroAreaForZeroScores() {
        Map<Integer, Double> scoresHist = Collections.singletonMap(0, 100.0);
        Map<Integer, Double> typicalHist = new HashMap<Integer, Double>() {{
            put(0, 1.0);
        }};

        double area = AggregatedFeatureReliability.calcWeightedAreaOfHistDeviatedFromTypicalHist(scoresHist, typicalHist);

        Assert.assertEquals(0, area, 0.000001);
    }

    @Test
    public void shouldCalculateBiggerAreaForBiggerScore() {
        Map<Integer, Double> typicalHist = new HashMap<Integer, Double>() {{
            put(50, 1.0);
            put(60, 1.0);
        }};

        double area1 = AggregatedFeatureReliability.calcWeightedAreaOfHistDeviatedFromTypicalHist(Collections.singletonMap(50, 10.0), typicalHist);
        double area2 = AggregatedFeatureReliability.calcWeightedAreaOfHistDeviatedFromTypicalHist(Collections.singletonMap(60, 10.0), typicalHist);

        Assert.assertTrue(area1 < area2);
    }

    @Test
    public void shouldCalculateBiggerAreaForBiggerCount() {
        Map<Integer, Double> typicalHist = new HashMap<Integer, Double>() {{
            put(50, 1.0);
        }};

        double area1 = AggregatedFeatureReliability.calcWeightedAreaOfHistDeviatedFromTypicalHist(Collections.singletonMap(50, 10.0), typicalHist);
        double area2 = AggregatedFeatureReliability.calcWeightedAreaOfHistDeviatedFromTypicalHist(Collections.singletonMap(50, 20.0), typicalHist);

        Assert.assertTrue(area1 < area2);
    }

    @Test
    public void shouldAdditivelyAddAreasForTheCounts() {
        Map<Integer, Double> typicalHist = new HashMap<Integer, Double>() {{
            put(50, 1.0);
            put(60, 1.0);
        }};

        double area1 = AggregatedFeatureReliability.calcWeightedAreaOfHistDeviatedFromTypicalHist(Collections.singletonMap(50, 10.0), typicalHist);
        double area2 = AggregatedFeatureReliability.calcWeightedAreaOfHistDeviatedFromTypicalHist(Collections.singletonMap(60, 20.0), typicalHist);
        double areaBoth = AggregatedFeatureReliability.calcWeightedAreaOfHistDeviatedFromTypicalHist(new HashMap<Integer, Double>() {{
            put(50, 10.0);
            put(60, 20.0);
        }}, typicalHist);

        Assert.assertEquals(area1 + area2, areaBoth, 0.000001);
    }

    @Test
    public void shouldCallInnerWorkingsCorrectlyWhenCalculatingPenalty() {
        // this is an uber test that just makes sure everything is glued in the right way
        // (all the other tests test the inner functions, but no one guarantees they are
        // called in the right order. This test tests exactly that)
        String fullAggregatedFeatureEventName1 = "fullAggregatedFeatureEventName1";
        String fullAggregatedFeatureEventName2 = "fullAggregatedFeatureEventName2";
        List<SmartAggregatedRecordDataContainer> smartAggregatedRecordDataContainers = Arrays.asList(
                new SmartAggregatedRecordDataContainer(1234, Collections.singletonMap(fullAggregatedFeatureEventName1, 0.0)),
                new SmartAggregatedRecordDataContainer(1234, Collections.singletonMap(fullAggregatedFeatureEventName1, 1.0)),
                new SmartAggregatedRecordDataContainer(1234, Collections.singletonMap(fullAggregatedFeatureEventName1, 1.1)),
                new SmartAggregatedRecordDataContainer(1234, Collections.singletonMap(fullAggregatedFeatureEventName1, 10.0)),
                new SmartAggregatedRecordDataContainer(1234, Collections.singletonMap(fullAggregatedFeatureEventName1, 20.0)),
                new SmartAggregatedRecordDataContainer(1234, Collections.singletonMap(fullAggregatedFeatureEventName1, 20.0)),
                new SmartAggregatedRecordDataContainer(1234, Collections.singletonMap(fullAggregatedFeatureEventName1, 90.0)),
                new SmartAggregatedRecordDataContainer(1234, Collections.singletonMap(fullAggregatedFeatureEventName1, 99.0)),
                new SmartAggregatedRecordDataContainer(1234, Collections.singletonMap(fullAggregatedFeatureEventName1, 91.0)),
                new SmartAggregatedRecordDataContainer(1234, Collections.singletonMap(fullAggregatedFeatureEventName1, 98.9)),

                new SmartAggregatedRecordDataContainer(1234, Collections.singletonMap(fullAggregatedFeatureEventName2, 0.0)),
                new SmartAggregatedRecordDataContainer(1234, Collections.singletonMap(fullAggregatedFeatureEventName2, 10.0)),
                new SmartAggregatedRecordDataContainer(1234, Collections.singletonMap(fullAggregatedFeatureEventName2, 10.1)),
                new SmartAggregatedRecordDataContainer(1234, Collections.singletonMap(fullAggregatedFeatureEventName2, 20.0)),
                new SmartAggregatedRecordDataContainer(1234, Collections.singletonMap(fullAggregatedFeatureEventName2, 12.0)),
                new SmartAggregatedRecordDataContainer(1234, Collections.singletonMap(fullAggregatedFeatureEventName2, 21.0)),
                new SmartAggregatedRecordDataContainer(1234, Collections.singletonMap(fullAggregatedFeatureEventName2, 33.1)),
                new SmartAggregatedRecordDataContainer(1234, Collections.singletonMap(fullAggregatedFeatureEventName2, 100.0))
        );

        AggregatedFeatureReliability aggr = new AggregatedFeatureReliability(smartAggregatedRecordDataContainers, 1);

        Assert.assertEquals(9.64253998632522, aggr.calcReliabilityPenalty(fullAggregatedFeatureEventName1), 0.000001);
        Assert.assertEquals(1.1874550992013186, aggr.calcReliabilityPenalty(fullAggregatedFeatureEventName2), 0.000001);
    }

    @Test
    public void shouldCalculateZeroPenaltyForFeatureNotAvailable() {
        String fullAggregatedFeatureEventName1 = "fullAggregatedFeatureEventName1";
        String fullAggregatedFeatureEventName2 = "fullAggregatedFeatureEventName2";
        List<SmartAggregatedRecordDataContainer> smartAggregatedRecordDataContainers = Arrays.asList(
                new SmartAggregatedRecordDataContainer(1234, Collections.singletonMap(fullAggregatedFeatureEventName1, 90.0)),
                new SmartAggregatedRecordDataContainer(1234, Collections.singletonMap(fullAggregatedFeatureEventName1, 90.0)),
                new SmartAggregatedRecordDataContainer(1234, Collections.singletonMap(fullAggregatedFeatureEventName2, 90.0))
        );

        double penalty = new AggregatedFeatureReliability(smartAggregatedRecordDataContainers, 1)
                .calcReliabilityPenalty("unavailable feature");

        Assert.assertEquals(0, penalty, 0.00000001);
    }

    @Test
    public void shouldCalculateBiggerPenaltyForSmallerNumOfContexts() {
        String fullAggregatedFeatureEventName1 = "fullAggregatedFeatureEventName1";
        String fullAggregatedFeatureEventName2 = "fullAggregatedFeatureEventName2";
        List<SmartAggregatedRecordDataContainer> smartAggregatedRecordDataContainers = Arrays.asList(
                new SmartAggregatedRecordDataContainer(1234, Collections.singletonMap(fullAggregatedFeatureEventName1, 90.0)),
                new SmartAggregatedRecordDataContainer(1234, Collections.singletonMap(fullAggregatedFeatureEventName1, 90.0)),
                new SmartAggregatedRecordDataContainer(1234, Collections.singletonMap(fullAggregatedFeatureEventName2, 90.0))
        );

        double penalty1 = new AggregatedFeatureReliability(smartAggregatedRecordDataContainers, 1)
                .calcReliabilityPenalty(fullAggregatedFeatureEventName1);
        double penalty2 = new AggregatedFeatureReliability(smartAggregatedRecordDataContainers, 10)
                .calcReliabilityPenalty(fullAggregatedFeatureEventName1);

        Assert.assertTrue(penalty1 > penalty2);
    }

    @Test
    public void shouldCalculateBiggerPenaltyForShorterPeriodOfTime() {
        String fullAggregatedFeatureEventName1 = "fullAggregatedFeatureEventName1";
        String fullAggregatedFeatureEventName2 = "fullAggregatedFeatureEventName2";
        List<SmartAggregatedRecordDataContainer> smartAggregatedRecordDataContainers = Arrays.asList(
                new SmartAggregatedRecordDataContainer(1234, Collections.singletonMap(fullAggregatedFeatureEventName1, 90.0)),
                new SmartAggregatedRecordDataContainer(1234, Collections.singletonMap(fullAggregatedFeatureEventName1, 90.0)),
                new SmartAggregatedRecordDataContainer(1234, Collections.singletonMap(fullAggregatedFeatureEventName2, 90.0))
        );
        List<SmartAggregatedRecordDataContainer> smartAggregatedRecordDataContainers1 = Arrays.asList(
                new SmartAggregatedRecordDataContainer(1234, Collections.singletonMap(fullAggregatedFeatureEventName1, 90.0)),
                new SmartAggregatedRecordDataContainer(1234, Collections.singletonMap(fullAggregatedFeatureEventName1, 90.0)),
                new SmartAggregatedRecordDataContainer(567890, Collections.singletonMap(fullAggregatedFeatureEventName2, 90.0))
        );

        double penalty1 = new AggregatedFeatureReliability(smartAggregatedRecordDataContainers, 1)
                .calcReliabilityPenalty(fullAggregatedFeatureEventName1);
        double penalty2 = new AggregatedFeatureReliability(smartAggregatedRecordDataContainers1, 1)
                .calcReliabilityPenalty(fullAggregatedFeatureEventName1);

        Assert.assertTrue(penalty1 > penalty2);
    }
}