package fortscale.ml.scorer.algorithm;

import fortscale.ml.model.prevalance.field.TimeModel;
import fortscale.ml.scorer.algorithms.TimeModelScorerAlgorithm;
import org.junit.Assert;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class TimeModelScorerAlgorithmTest extends AbstractScorerTest {
    private static final int DAILY_TIME_RESOLUTION = 60 * 60 * 24;
    private static final int DAILY_BUCKET_SIZE = 60 * 10;
    private static final int MAX_RARE_TIMESTAMP_COUNT = 10;
    private static final int MAX_NUM_OF_RARE_TIMESTAMPS = 5;


    private Double calcScore(List<Long> times, long timeToScore) {
        Map<Long, Double> timeToCounter = times.stream().collect(Collectors.groupingBy(
                        o -> o,
                        Collectors.reducing(
                                0D,
                                o -> 1D,
                                (o1, o2) -> o1 + o2)
                )
        );
        return calcScore(timeToCounter, timeToScore);
    }

    private Double calcScore(Map<Long, Double> timeToCounter, long timeToScore) {
        TimeModel model = new TimeModel(
                DAILY_TIME_RESOLUTION,
                DAILY_BUCKET_SIZE,
                timeToCounter
        );
        TimeModelScorerAlgorithm scorerAlgorithm = new TimeModelScorerAlgorithm(MAX_RARE_TIMESTAMP_COUNT, MAX_NUM_OF_RARE_TIMESTAMPS);
        return scorerAlgorithm.calculateScore(timeToScore, model);
    }

    private void assertScore(List<Long> times, long timeToScore, double expected) {
        Assert.assertEquals(expected, calcScore(times, timeToScore), 0.00001);
    }

    /*************************************************************************************
     *************************************************************************************
     ****************** TEST VARIOUS SCENARIOS - FROM BASIC TO ADVANCED ******************
     *************************************************************************************
     *************************************************************************************/

    @Test
    public void elementaryCheck() {
        List<Long> times = new ArrayList<>();
        long epochSeconds = 1000;
        for (int i = 0; i < 100; i++) {
            times.add(epochSeconds);
        }
        assertScore(times, epochSeconds, 0);
    }

    @Test
    public void elementaryCheckWithOneOutlier() {
        List<Long> times = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            times.add(1000L);
        }
        long epochSeconds = 5000;
        assertScore(times, epochSeconds, 60);
    }

    @Test
    public void testUniformlyRandomDistribution() {
        Random rnd = new Random(1);
        List<Long> times = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            times.add((long)(rnd.nextDouble( ) * DAILY_TIME_RESOLUTION));
        }

        for (int i = 0; i < times.size(); i++) {
            assertScore(times, times.get(i), 0);
        }
    }

    @Test
    public void testScoreOfIsolatedTimes() {
        Random rnd = new Random(1);
        List<Long> times = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            times.add((long)(rnd.nextDouble( ) * 6000));
        }
        long isolatedTimes[] = new long[]{30000, 40000, 50000, 60000};
        double scores[] = new double[]{100, 94, 80, 59};
        for (int i = 0; i < scores.length; i++) {
            assertScore(times, isolatedTimes[i], scores[i]);
            times.add(isolatedTimes[i]);
        }
        assertScore(times, 500, 0);
    }

    @Test
    public void testScoresInDifferentDistancesFromTheClusters() {
        Random rnd = new Random(1);
        List<Long> timesClustered = new ArrayList<>();
        int clusterSizes[] = new int[]{2, 2, 46};
        int clusterSpans[] = new int[]{600, 600, 2400};
        int clusterOffsets[] = new int[]{0, 6600, 2400};
        for (int cluster = 0; cluster < clusterSizes.length; cluster++) {
            for (int i = 0; i < clusterSizes[cluster]; i++) {
                long epochSeconds = (long)(rnd.nextDouble( ) * clusterSpans[cluster] + clusterOffsets[cluster]);
                timesClustered.add(epochSeconds);
            }
        }

        long[] timesToScore = new long[]{14000, 10500, 10000, 9000, 8500};
        double[] scores = new double[]{100, 100, 99, 89, 60};
        for (int i = 0; i < timesToScore.length; i++) {
            assertScore(timesClustered, timesToScore[i], scores[i]);
        }
    }

    @Test
    public void testScoresOfOneBigClusterAndManyDispersedTimes() {
        Random rnd = new Random(1);
        List<Long> times = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            long epochSeconds = (long)(rnd.nextDouble( ) * 3000);
            times.add(epochSeconds);
        }

        double scores[] = new double[]{100, 94, 80, 59};
        double finalScore = 32;
        long dispersedTimes[] = new long[scores.length];
        for (int i = 0; i < scores.length; i++) {
            dispersedTimes[i] = 3000 + (i + 1) * 9000;
            assertScore(times, dispersedTimes[i], scores[i]);
            times.add(dispersedTimes[i]);
        }

        for (int i = 0; i < scores.length; i++) {
            assertScore(times, dispersedTimes[i], finalScore);
        }
    }

    @Test
    // this test is built on the scenario of issue FV-3738.
    public void testNewWorkingTimeScore() {
        int scenarioSteps[] = new int[]{450, 900, 900};
        int scenarioNumberOfSteps[] = new int[]{16, 8, 4};
        int scenarioScoreThresholds[] = new int[]{0, 7, 16};
        for (int scenario = 0; scenario < scenarioSteps.length; scenario++) {
            List<Long> times = new ArrayList<>();
            int step = 200;
            for (int i = 0; i < 5; i++) {
                long epochSeconds = 0; //12AM UTC
                times.add(epochSeconds);
                for (int j = 0; j < 17; j++) {
                    assertScore(times, epochSeconds, 0);
                    times.add(epochSeconds);
                    epochSeconds += step;
                }
            }

            step = scenarioSteps[scenario];
            int numberOfSteps = scenarioNumberOfSteps[scenario];
            double prevCycleScores[] = new double[numberOfSteps];
            long epochSeconds = 43200; //12PM UTC
            double score;
            double prevScore = 100;
            for (int j = 0; j < numberOfSteps; j++) {
                score = calcScore(times, epochSeconds);
                times.add(epochSeconds);
                Assert.assertTrue(prevScore >= score);
                prevCycleScores[j] = score;
                prevScore = score;
                epochSeconds += step;
            }

            for (int i = 0; i < 4; i++) {
                epochSeconds = 43200; //12PM UTC
                for (int j = 0; j < numberOfSteps; j++) {
                    score = calcScore(times, epochSeconds);
                    times.add(epochSeconds);
                    Assert.assertTrue(prevCycleScores[j] >= score);
                    prevCycleScores[j] = score;
                    epochSeconds += step;
                }
                for (int j = numberOfSteps - 2; j < numberOfSteps; j++) {
                    score = calcScore(times, epochSeconds);
                    times.add(epochSeconds);
                    Assert.assertTrue(score + " <= " + scenarioScoreThresholds[scenario] + " (scenario #" + scenario + ")?", score <= scenarioScoreThresholds[scenario]);
                    prevCycleScores[j] = score;
                    epochSeconds += step;
                }
            }
        }
    }

    /*************************************************************************************
     *************************************************************************************
     ***************************** TEST REAL DATA SCENARIOS ******************************
     ***************** THESE TESTS ARE MORE OF RESEARCH SCRIPTS THAN TESTS ***************
     ********* THEY ARE MEANT FOR RUNNING REAL DATA SCENARIOS AND THEN INSPECTING ********
     ************ THE RESULTS BY HANDS (ALTHOUGH ASSERTS COULD BE USED AS WELL) **********
     ************** READ AbstractModelTest.java'S DOCUMENTATION FOR MORE INFO ************
     *************************************************************************************
     *************************************************************************************/

    private class TimeModelScenarioCallbacks extends ScenarioCallbacks {
        private Map<Long, Double> timeToCounter;

        @Override
        public void onScenarioRunStart() {
            timeToCounter = new HashMap<>();
        }

        @Override
        public Double onScore(TestEventsBatch eventsBatch) {
            return calcScore(timeToCounter, eventsBatch.time_bucket);
        }

        @Override
        public void onFinishProcessEvent(TestEventsBatch eventsBatch) {
            // note: the reason we calc timeInDay (and not use time_bucket) is that big scenarios
            // take ridiculous amount of time otherwise. This of course should be fixed in the model
            long timeInDay = eventsBatch.time_bucket % DAILY_TIME_RESOLUTION;
            timeToCounter.put(timeInDay, timeToCounter.getOrDefault(timeInDay, 0D) + 1);
        }
    }

    @Test
    public void testRealScenarioSshSrcMachineUsername_278997272() throws IOException {
        try {
            runAndPrintRealScenario(new TimeModelScenarioCallbacks(), "username_278997272.csv", 0);
        } catch (FileNotFoundException e) {
            println("file not found");
        }
    }

    @Test
    public void testRealScenariosHowManyAnomalousUsers() throws IOException {
        boolean RUN_FAST = true;
        if (RUN_FAST) {
            testRealScenariosHowManyAnomalousUsers(new TimeModelScenarioCallbacks(), 0.07, 50, 800);
        }
        else {
            testRealScenariosHowManyAnomalousUsers(new TimeModelScenarioCallbacks(), 0.076, 50, 920);
        }
    }
}
