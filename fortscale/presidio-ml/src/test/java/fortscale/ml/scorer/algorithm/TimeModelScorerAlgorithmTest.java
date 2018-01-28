package fortscale.ml.scorer.algorithm;

import fortscale.ml.model.TimeModel;
import fortscale.ml.model.metrics.CategoryRarityModelBuilderMetricsContainer;
import fortscale.ml.model.metrics.TimeModelBuilderMetricsContainer;
import fortscale.ml.model.metrics.TimeModelBuilderPartitionsMetricsContainer;
import fortscale.ml.scorer.algorithms.TimeModelScorerAlgorithm;
import org.junit.Assert;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

import static org.mockito.Mockito.mock;

public class TimeModelScorerAlgorithmTest extends AbstractScorerTest {
    private static final int DAILY_TIME_RESOLUTION = 60 * 60 * 24;
    private static final int DAILY_BUCKET_SIZE = 60 * 10;
    private static final int MAX_RARE_TIMESTAMP_COUNT = 10;
    private TimeModelBuilderMetricsContainer timeModelBuilderMetricsContainer = mock(TimeModelBuilderMetricsContainer.class);
    private TimeModelBuilderPartitionsMetricsContainer timeModelBuilderPartitionsMetricsContainer = mock(TimeModelBuilderPartitionsMetricsContainer.class);
    private CategoryRarityModelBuilderMetricsContainer categoryRarityModelBuilderMetricsContainer = mock(CategoryRarityModelBuilderMetricsContainer.class);
    private static final int MAX_NUM_OF_RARE_TIMESTAMPS = 15;
    private static final double X_WITH_VALUE_HALF_FACTOR = 0.3333333333333333;


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
        TimeModel model = new TimeModel();
        model.init(DAILY_TIME_RESOLUTION, DAILY_BUCKET_SIZE, MAX_RARE_TIMESTAMP_COUNT, timeToCounter, 1, timeModelBuilderMetricsContainer, timeModelBuilderPartitionsMetricsContainer,categoryRarityModelBuilderMetricsContainer);
        TimeModelScorerAlgorithm scorerAlgorithm = new TimeModelScorerAlgorithm(MAX_RARE_TIMESTAMP_COUNT, MAX_NUM_OF_RARE_TIMESTAMPS,X_WITH_VALUE_HALF_FACTOR);
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

        long epochSeconds = 1000;
        Random rnd = new Random(1);

        Map<Long, Double> timeToCounter = new HashMap<>();
        for (int i = 0; i < 50 ; i++) {
            timeToCounter.put((long)(rnd.nextDouble( )* 1000)+i*DAILY_TIME_RESOLUTION,1D);
        }
        Double score = calcScore(timeToCounter, epochSeconds);
        Assert.assertEquals(score,0,0.001);
    }

    @Test
    public void elementaryCheckWithOneOutlier() {
        List<Long> times = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            times.add(1000L);
        }
        Map<Long, Double> timeToCounter = new HashMap<>();
        for (int i = 0; i < times.size() ; i++) {
            timeToCounter.put(i*times.get(i)*DAILY_TIME_RESOLUTION,times.get(i).doubleValue());
        }
        long epochSeconds = 5000;

        Assert.assertEquals(38D,calcScore(timeToCounter,epochSeconds),0.001);
    }

    @Test
    public void testUniformlyRandomDistribution() {
        Random rnd = new Random(1);
        Map<Long, Double> timeToCounter = new HashMap<>();
        for (int i = 0; i < 100 ; i++) {
            timeToCounter.put((long)(rnd.nextDouble( )* DAILY_TIME_RESOLUTION),1D);
        }
        for (Map.Entry<Long, Double> entry : timeToCounter.entrySet()) {
            Double score = calcScore(timeToCounter, entry.getKey());
            Assert.assertEquals(score,0,0.001);
        }
    }

    @Test
    public void testScoreOfIsolatedTimes() {
        Random rnd = new Random(1);
        Map<Long, Double> timeToCounter = new HashMap<>();
        for (int i = 0; i < 50 ; i++) {
            timeToCounter.put((long)(rnd.nextDouble( )* 6000)+i*DAILY_TIME_RESOLUTION,1D);
        }
        long isolatedTimes[] = new long[]{30000, 40000, 50000, 60000};
        double scores[] = new double[]{87, 87, 76, 65};
        for (int i = 0; i < scores.length; i++) {
            timeToCounter.put(isolatedTimes[i],scores[i]);
            Double score = calcScore(timeToCounter, isolatedTimes[i]);
            Assert.assertEquals(scores[i],score,0.01);
            timeToCounter.put(isolatedTimes[i]+i*DAILY_TIME_RESOLUTION,1D);
        }
        Assert.assertEquals(0,calcScore(timeToCounter,500),0.001);
    }

    @Test
    public void testScoresInDifferentDistancesFromTheClusters() {
        Random rnd = new Random(1);
        Map<Long, Double> timeToCounter = new HashMap<>();
        int clusterSizes[] = new int[]{2, 2, 46};
        int clusterSpans[] = new int[]{600, 600, 2400};
        int clusterOffsets[] = new int[]{0, 6600, 2400};
        int amountOfDays=8;
        for(int day=0;day<amountOfDays;day ++) {
            for (int cluster = 0; cluster < clusterSizes.length; cluster++) {
                for (int i = 0; i < clusterSizes[cluster]; i++) {
                    long epochSeconds = day*DAILY_TIME_RESOLUTION + (long) (rnd.nextDouble() * clusterSpans[cluster] + clusterOffsets[cluster]);
                    timeToCounter.put(epochSeconds, 1D);
                }
            }
        }

        long[] timesToScore = new long[]{14000, 13000, 12000, 11000, 10000};
        double[] scores = new double[]{92, 87, 84, 65, 0};
        for (int i = 0; i < timesToScore.length; i++) {
            Double score = calcScore(timeToCounter, amountOfDays*DAILY_TIME_RESOLUTION+timesToScore[i]);
            Assert.assertEquals(scores[i],score,0.01);
        }
    }

    @Test
    public void testScoreInOneMajorClusterAndSecondaryCluster()
    {
        Instant startInstant = Instant.EPOCH;
        Instant endInstant = startInstant.plus(Duration.ofDays(30));
        int activityStartHour = 8;
        int activityEndHour=10;
        int activityIntervalInSeconds=10*60;

        // 30 days of 10minutes activity 8AM-10AM
        Map<Long, Double> trainTimeToCounterData = fillTimeToCounter(startInstant, endInstant, activityStartHour, activityEndHour, activityIntervalInSeconds);
        // days 31 of 10minutes activity 2AM-4AM
        startInstant = endInstant;
        endInstant = endInstant.plus(Duration.ofDays(1));
        activityStartHour=20;
        activityEndHour=22;
        trainTimeToCounterData.putAll(fillTimeToCounter(startInstant, endInstant, activityStartHour, activityEndHour, activityIntervalInSeconds));

        // days 32 of 10minutes activity 8PM-10PM
        startInstant = endInstant;
        endInstant = endInstant.plus(Duration.ofDays(1));
        activityStartHour=20;
        activityEndHour=22;
        Map<Long, Double> testTimeToCounterData = fillTimeToCounter(startInstant, endInstant, activityStartHour, activityEndHour, activityIntervalInSeconds);

        // days 32 of 10minutes activity 2AM-4AM
        activityStartHour=2;
        activityEndHour=4;
        testTimeToCounterData.putAll(fillTimeToCounter(startInstant, endInstant, activityStartHour, activityEndHour, activityIntervalInSeconds));

        for (Long time: testTimeToCounterData.keySet()){
            Assert.assertEquals(87,calcScore(trainTimeToCounterData,time),1);
        }
    }

    @Test
    public void testScoreInOneMajorClusterInHighDensityAndSecondaryCluster()
    {
        Instant startInstant = Instant.EPOCH;
        Instant endInstant = startInstant.plus(Duration.ofDays(30));
        int activityStartHour = 8;
        int activityEndHour=10;
        int activityIntervalInSeconds=10*60;
        int highDensityActivityIntervalInSeconds=30;

        // 30 days of 10minutes activity 8AM-10AM
        Map<Long, Double> trainTimeToCounterData = fillTimeToCounter(startInstant, endInstant, activityStartHour, activityEndHour, activityIntervalInSeconds);
        // days 31 of 1 sec activity 2AM-4AM
        startInstant = endInstant;
        endInstant = endInstant.plus(Duration.ofDays(1));
        activityStartHour=20;
        activityEndHour=22;
        trainTimeToCounterData.putAll(fillTimeToCounter(startInstant, endInstant, activityStartHour, activityEndHour, highDensityActivityIntervalInSeconds));

        // days 32 of 1 sec activity 8PM-10PM
        startInstant = endInstant;
        endInstant = endInstant.plus(Duration.ofDays(1));
        activityStartHour=20;
        activityEndHour=22;
        Map<Long, Double> testTimeToCounterData = fillTimeToCounter(startInstant, endInstant, activityStartHour, activityEndHour, highDensityActivityIntervalInSeconds);

        // days 32 of 1 sec activity 2AM-4AM
        activityStartHour=2;
        activityEndHour=4;
        testTimeToCounterData.putAll(fillTimeToCounter(startInstant, endInstant, activityStartHour, activityEndHour, highDensityActivityIntervalInSeconds));

        for (Long time: testTimeToCounterData.keySet()){
            Assert.assertEquals(87,calcScore(trainTimeToCounterData,time),1);
        }
    }


    public Map<Long, Double> fillTimeToCounter(Instant startInstant, Instant endInstant, int activityStartHour, int activityEndHour, int activityIntervalInSeconds) {
        Map<Long, Double> timeToCounter = new HashMap<>();

        Instant cursor = Instant.ofEpochSecond(startInstant.plus(Duration.ofHours(activityStartHour)).getEpochSecond());
        while(cursor.isBefore(endInstant))
        {
            while (LocalDateTime.ofInstant(cursor, ZoneOffset.UTC).getHour()<activityEndHour)
            {
                timeToCounter.put(cursor.getEpochSecond(),1D);
                cursor=cursor.plus(Duration.ofSeconds(activityIntervalInSeconds));
            }
            cursor=cursor.truncatedTo(ChronoUnit.DAYS).plus(Duration.ofDays(1)).plus(Duration.ofHours(activityStartHour));
        }
        return timeToCounter;
    }

    @Test
    public void testScoresOfOneBigClusterAndManyDispersedTimes() {
        Random rnd = new Random(1);
        Map<Long, Double> timeToCounter = new HashMap<>();

        int amountOfDays = 30;
        for (int day = 0; day < amountOfDays; day++) {
            for (int i = 0; i < 50; i++) {
                long epochSeconds = day * DAILY_TIME_RESOLUTION + (long) (rnd.nextDouble() * 3000);
                timeToCounter.put(epochSeconds, 1D);
            }
        }

        double scores[] = new double[]{100, 87, 74, 64};
        double finalScore = 51;
        long dispersedTimes[] = new long[scores.length];
        for (int i = 0; i < scores.length; i++) {
            dispersedTimes[i] = amountOfDays * DAILY_TIME_RESOLUTION + 3000 + (i + 1) * 6000;
            Double score = calcScore(timeToCounter, dispersedTimes[i]);
            Assert.assertEquals(scores[i], score,0.001);
            timeToCounter.put(dispersedTimes[i]+i*DAILY_TIME_RESOLUTION,1D);
        }

        for (int i = 0; i < scores.length; i++) {
            Assert.assertEquals(finalScore,calcScore(timeToCounter,dispersedTimes[i]),1);
        }
    }

    @Test
    // this test is built on the scenario of issue FV-3738.
    public void testNewWorkingTimeScore() {
        int scenarioSteps[] = new int[]{450, 900, 900};
        int scenarioNumberOfSteps[] = new int[]{16, 8, 4};
        int scenarioScoreThresholds[] = new int[]{0, 7, 26};
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
