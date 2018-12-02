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
    private static final int MAX_RARE_TIMESTAMP_COUNT = 8;
    private TimeModelBuilderMetricsContainer timeModelBuilderMetricsContainer = mock(TimeModelBuilderMetricsContainer.class);
    private TimeModelBuilderPartitionsMetricsContainer timeModelBuilderPartitionsMetricsContainer = mock(TimeModelBuilderPartitionsMetricsContainer.class);
    private CategoryRarityModelBuilderMetricsContainer categoryRarityModelBuilderMetricsContainer = mock(CategoryRarityModelBuilderMetricsContainer.class);
    private static final int MAX_NUM_OF_RARE_TIMESTAMPS = 15;
    private static final double X_WITH_VALUE_HALF_FACTOR = 0.25;


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
        TimeModel timeModel = getTimeModel(timeToCounter);
        return calcScore(timeModel, timeToScore);
    }

    private Double calcScore(TimeModel model, long timeToScore) {
        TimeModelScorerAlgorithm scorerAlgorithm = new TimeModelScorerAlgorithm(MAX_RARE_TIMESTAMP_COUNT, MAX_NUM_OF_RARE_TIMESTAMPS,X_WITH_VALUE_HALF_FACTOR);
        return scorerAlgorithm.calculateScore(timeToScore, model);
    }

    private TimeModel getTimeModel(Map<Long, Double> timeToCounter){
        TimeModel model = new TimeModel();
        model.init(DAILY_TIME_RESOLUTION, DAILY_BUCKET_SIZE, MAX_RARE_TIMESTAMP_COUNT, timeToCounter, 1, timeModelBuilderMetricsContainer, timeModelBuilderPartitionsMetricsContainer,categoryRarityModelBuilderMetricsContainer);

        return model;
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
    public void testOneOutlierAgainstOneNormalTime1() {
        double[] scores = {96, 84, 45, 17};
        testOneOutlierAgainstOneNormalTime(1000, 50000,scores);
    }

    @Test
    public void testOneOutlierAgainstOneNormalTime2() {
        double[] scores = {87, 48, 19, 8};
        testOneOutlierAgainstOneNormalTime(1000, 5000,scores);
    }

    private void testOneOutlierAgainstOneNormalTime(long normalTimeInEpochSecond, long abnormalTimeInEpochSeconds, double... scores){
        Map<Long, Double> timeToCounter = new HashMap<>();
        updateTimeCounter(timeToCounter, normalTimeInEpochSecond,100);

        Assert.assertEquals(scores[0],calcScore(timeToCounter,abnormalTimeInEpochSeconds),0.001);

        updateTimeCounter(timeToCounter, abnormalTimeInEpochSeconds,1);
        Assert.assertEquals(scores[1],calcScore(timeToCounter,abnormalTimeInEpochSeconds),0.001);

        updateTimeCounter(timeToCounter, abnormalTimeInEpochSeconds,2);
        Assert.assertEquals(scores[2],calcScore(timeToCounter,abnormalTimeInEpochSeconds),0.001);

        updateTimeCounter(timeToCounter, abnormalTimeInEpochSeconds,3);
        Assert.assertEquals(scores[3],calcScore(timeToCounter,abnormalTimeInEpochSeconds),0.001);
    }

    private void updateTimeCounter(Map<Long, Double> timeToCounter, long timeInDay, int numOfDays){
        List<Long> times = new ArrayList<>();
        for (int i = 0; i < numOfDays; i++) {
            times.add(timeInDay);
        }

        for (int i = 0; i < times.size() ; i++) {
            timeToCounter.put(i*DAILY_TIME_RESOLUTION + times.get(i),1000D);
        }
    }

    @Test
    public void testUniformlyRandomDistribution() {
        Random rnd = new Random(1);
        Map<Long, Double> timeToCounter = new HashMap<>();
        for(int j=0; j<90; j++) {
            for (int i = 0; i < 10; i++) {
                timeToCounter.put((long) (rnd.nextDouble() * DAILY_TIME_RESOLUTION) + j*DAILY_TIME_RESOLUTION, 1D);
            }
        }

        TimeModel timeModel = getTimeModel(timeToCounter);
        for (Map.Entry<Long, Double> entry : timeToCounter.entrySet()) {
            Double score = calcScore(timeModel, entry.getKey());
            Assert.assertEquals(score,0,0.001);
        }
    }

    @Test
    public void testScoreOfIsolatedTimesAfter30Days() {
        double scores[] = new double[]{89, 77, 60, 38, 20, 9};
        testScoreOfIsolatedTimes(30, scores);
    }

    @Test
    public void testScoreOfIsolatedTimesAfter50Days() {
        double scores[] = new double[]{93, 85, 71, 49, 30, 16};
        testScoreOfIsolatedTimes(50, scores);
    }

    @Test
    public void testScoreOfIsolatedTimesAfter90Days() {
        double scores[] = new double[]{96, 91, 79, 57, 36, 21};
        testScoreOfIsolatedTimes(90, scores);
    }

    private void testScoreOfIsolatedTimes(int numOfDays, double... scores) {
        Random rnd = new Random(1);
        Map<Long, Double> timeToCounter = new HashMap<>();
        for (int i = 0; i < numOfDays ; i++) {
            timeToCounter.put((long)(rnd.nextDouble( )* 6000)+i*DAILY_TIME_RESOLUTION,1D);
        }
        long isolatedTimes[] = new long[]{20000, 30000, 40000, 50000, 60000, 70000};

        for (int i = 0; i < scores.length; i++) {
            Double score = calcScore(timeToCounter, isolatedTimes[i]);
            Assert.assertEquals(scores[i],score,0.01);
            timeToCounter.put(isolatedTimes[i]+i*DAILY_TIME_RESOLUTION,1D);
        }
        Assert.assertEquals(0,calcScore(timeToCounter,500),0.001);
    }

    @Test
    public void testScoresInDifferentDistancesFromTheClustersAfter10Days() {
        double[] scores = new double[]{69, 68, 67, 65, 45};
        testScoresInDifferentDistancesFromTheClusters(10, scores);
    }

    @Test
    public void testScoresInDifferentDistancesFromTheClustersAfter30Days() {
        double[] scores = new double[]{89, 88, 87, 84, 16};
        testScoresInDifferentDistancesFromTheClusters(30, scores);
    }

    @Test
    public void testScoresInDifferentDistancesFromTheClustersAfter90Days() {
        double[] scores = new double[]{96, 95, 94, 88, 1};
        testScoresInDifferentDistancesFromTheClusters(90, scores);
    }

    private void testScoresInDifferentDistancesFromTheClusters(int numOfDays, double... scores) {
        Random rnd = new Random(1);
        Map<Long, Double> timeToCounter = new HashMap<>();
        int clusterSizes[] = new int[]{2, 2, 46};
        int clusterSpans[] = new int[]{600, 600, 2400};
        int clusterOffsets[] = new int[]{0, 6600, 2400};
        for(int day=0;day<numOfDays;day ++) {
            for (int cluster = 0; cluster < clusterSizes.length; cluster++) {
                for (int i = 0; i < clusterSizes[cluster]; i++) {
                    long epochSeconds = day*DAILY_TIME_RESOLUTION + (long) (rnd.nextDouble() * clusterSpans[cluster] + clusterOffsets[cluster]);
                    timeToCounter.put(epochSeconds, 1D);
                }
            }
        }

        long[] timesToScore = new long[]{14000, 13000, 12000, 11000, 10000};
        TimeModel timeModel = getTimeModel(timeToCounter);
        for (int i = 0; i < timesToScore.length; i++) {
            Double score = calcScore(timeModel, numOfDays*DAILY_TIME_RESOLUTION+timesToScore[i]);
            Assert.assertEquals(scores[i],score,0.01);
        }
    }

    @Test
    public void testScoreInOneMajorClusterAndOutlierCluster()
    {
        Instant startInstant = Instant.EPOCH;
        Instant endInstant = startInstant.plus(Duration.ofDays(30));
        int activityStartHour = 8;
        int activityEndHour=10;
        int activityIntervalInSeconds=10*60;

        // 30 days of 10 minutes activity 8AM-10AM
        Map<Long, Double> trainTimeToCounterData = fillTimeToCounter(startInstant, endInstant, activityStartHour, activityEndHour, activityIntervalInSeconds);
        // days 31 of 10minutes activity 8PM-10PM
        startInstant = endInstant;
        endInstant = endInstant.plus(Duration.ofDays(1));
        activityStartHour=20;
        activityEndHour=22;
        trainTimeToCounterData.putAll(fillTimeToCounter(startInstant, endInstant, activityStartHour, activityEndHour, activityIntervalInSeconds));

        TimeModel timeModel = getTimeModel(trainTimeToCounterData);

        // days 32 of 10minutes activity 8PM-10PM
        startInstant = endInstant;
        endInstant = endInstant.plus(Duration.ofDays(1));
        activityStartHour=20;
        activityEndHour=22;
        Map<Long, Double> testTimeToCounterData = fillTimeToCounter(startInstant, endInstant, activityStartHour, activityEndHour, activityIntervalInSeconds);

        for (Long time: testTimeToCounterData.keySet()){
            Assert.assertEquals(71,calcScore(timeModel,time),1);
        }


        // days 32 of 10minutes activity 2AM-4AM
        activityStartHour=2;
        activityEndHour=4;
        testTimeToCounterData = fillTimeToCounter(startInstant, endInstant, activityStartHour, activityEndHour, activityIntervalInSeconds);

        for (Long time: testTimeToCounterData.keySet()){
            Assert.assertEquals(78,calcScore(timeModel,time),1);
        }
    }

    @Test
    public void testScoreInOneMajorClusterAndHighDensityOutlierCluster()
    {
        Instant startInstant = Instant.EPOCH;
        Instant endInstant = startInstant.plus(Duration.ofDays(30));
        int activityStartHour = 8;
        int activityEndHour=10;
        int activityIntervalInSeconds=10*60;
        int highDensityActivityIntervalInSeconds=30;

        // 30 days of 10minutes activity 8AM-10AM
        Map<Long, Double> trainTimeToCounterData = fillTimeToCounter(startInstant, endInstant, activityStartHour, activityEndHour, activityIntervalInSeconds);
        // days 31 of 1 sec activity 8PM-10PM
        startInstant = endInstant;
        endInstant = endInstant.plus(Duration.ofDays(1));
        activityStartHour=20;
        activityEndHour=22;
        trainTimeToCounterData.putAll(fillTimeToCounter(startInstant, endInstant, activityStartHour, activityEndHour, highDensityActivityIntervalInSeconds));

        TimeModel timeModel = getTimeModel(trainTimeToCounterData);


        // days 32 of 1 sec activity 8PM-10PM
        startInstant = endInstant;
        endInstant = endInstant.plus(Duration.ofDays(1));
        activityStartHour=20;
        activityEndHour=22;
        Map<Long, Double> testTimeToCounterData = fillTimeToCounter(startInstant, endInstant, activityStartHour, activityEndHour, highDensityActivityIntervalInSeconds);

        for (Long time: testTimeToCounterData.keySet()){
            Assert.assertEquals(71,calcScore(timeModel,time),1);
        }

        // days 32 of 1 sec activity 2AM-4AM
        activityStartHour=2;
        activityEndHour=4;
        testTimeToCounterData = fillTimeToCounter(startInstant, endInstant, activityStartHour, activityEndHour, highDensityActivityIntervalInSeconds);

        for (Long time: testTimeToCounterData.keySet()){
            Assert.assertEquals(78,calcScore(timeModel,time),1);
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

        double scores[] = new double[]{89, 77, 64, 46};
        long dispersedTimes[] = new long[scores.length];
        for (int i = 0; i < scores.length; i++) {
            dispersedTimes[i] = amountOfDays * DAILY_TIME_RESOLUTION + 3000 + (i + 1) * 6000;
            Double score = calcScore(timeToCounter, dispersedTimes[i]);
            Assert.assertEquals(scores[i], score,0.001);
            timeToCounter.put(dispersedTimes[i]+i*DAILY_TIME_RESOLUTION,1D);
        }

        double finalScore = 23;
        TimeModel timeModel = getTimeModel(timeToCounter);
        for (int i = 0; i < scores.length; i++) {
            Assert.assertEquals(finalScore,calcScore(timeModel,dispersedTimes[i]),1);
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
