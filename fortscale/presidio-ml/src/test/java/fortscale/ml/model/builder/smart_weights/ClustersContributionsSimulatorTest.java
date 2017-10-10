package fortscale.ml.model.builder.smart_weights;

import fortscale.ml.model.retriever.smart_data.SmartAggregatedRecordDataContainer;
import fortscale.ml.scorer.algorithms.SmartWeightsScorerAlgorithm;
import fortscale.smart.record.conf.ClusterConf;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static fortscale.ml.model.builder.smart_weights.SmartWeightModelTestUtils.createClusterConf;
import static fortscale.ml.model.builder.smart_weights.SmartWeightModelTestUtils.createClusterConfs;

/**
 * Created by barak_schuster on 31/08/2017.
 */
public class ClustersContributionsSimulatorTest {
    /*************************************************************************************************************
     ************************* ClustersContributionsSimulator::calcTopEntityEvents tests *************************
     *************************************************************************************************************/

    /**
     * A helper class for creating {@link SmartAggregatedRecordDataContainer} objects with mocked smart values.
     */
    private static class SmartAggregatedRecordDataContainerGenerator {
        private static double index = 0;

        /**
         * Create a {@link SmartAggregatedRecordDataContainer} with the given startTime and fullAggregatedFeatureEventNameToScore,
         * such that calling {@link SmartWeightsScorerAlgorithm ::calculateScore} on the result
         * {@link SmartAggregatedRecordDataContainer} will result with the given smartValue.
         */
        public static SmartAggregatedRecordDataContainer createSmartAggregatedRecordDataContainer(SmartWeightsScorerAlgorithm scorerAlgorithmMock,
                                                                      long startTime,
                                                                      Map<String, Double> fullAggregatedFeatureEventNameToScore,
                                                                      double smartValue) {
            Instant startTimeInstant = Instant.ofEpochMilli(startTime);
            SmartAggregatedRecordDataContainer smartAggregatedRecordDataContainer = new SmartAggregatedRecordDataContainer(startTimeInstant, fullAggregatedFeatureEventNameToScore);
            Mockito.when(scorerAlgorithmMock.calculateScore(
                    Mockito.eq(smartAggregatedRecordDataContainer.getSmartAggregatedRecordsData()),
                    Mockito.anyListOf(ClusterConf.class)
            )).thenReturn(smartValue);
            return smartAggregatedRecordDataContainer;
        }

        /**
         * Create a {@link SmartAggregatedRecordDataContainer} with the given startTime, such that calling
         * {@link SmartWeightsScorerAlgorithm ::calculateScore} on the result {@link SmartAggregatedRecordDataContainer} will
         * result with the given smartValue.
         */
        public static SmartAggregatedRecordDataContainer createSmartAggregatedRecordDataContainer(SmartWeightsScorerAlgorithm scorerAlgorithmMock,
                                                                      long startTime,
                                                                      double smartValue) {
            return createSmartAggregatedRecordDataContainer(
                    scorerAlgorithmMock,
                    startTime,
                    Collections.singletonMap("some feature. Please don't use it outside of JSmartAggregatedRecordDataContainerGenerator", index++),
                    smartValue
            );
        }
    }

    @Test
    public void shouldReturnEmptyListAsTopEntitiesGivenEmptyList() {

        List<SmartAggregatedRecordDataContainer> topEntityEvents = new ClustersContributionsSimulator(new SmartWeightsScorerAlgorithm())
                .calcTopSmartEvents(
                        Collections.emptyList(),
                        createClusterConfs(createClusterConf("F1")),
                        10
                );

        Assert.assertTrue(topEntityEvents.isEmpty());
    }

    @Test
    public void shouldReturnTopEntitiesGivenEntitiesFromTheSameDay() {
        SmartWeightsScorerAlgorithm scorerAlgorithm = Mockito.mock(SmartWeightsScorerAlgorithm.class);
        long startTime = 1234;
        SmartAggregatedRecordDataContainer eSmall = SmartAggregatedRecordDataContainerGenerator.createSmartAggregatedRecordDataContainer(scorerAlgorithm, startTime, 0.1);
        SmartAggregatedRecordDataContainer eMedium = SmartAggregatedRecordDataContainerGenerator.createSmartAggregatedRecordDataContainer(scorerAlgorithm, startTime, 0.2);
        SmartAggregatedRecordDataContainer eLarge = SmartAggregatedRecordDataContainerGenerator.createSmartAggregatedRecordDataContainer(scorerAlgorithm, startTime, 0.3);

        List<SmartAggregatedRecordDataContainer> topEntityEvents = new ClustersContributionsSimulator(scorerAlgorithm).calcTopSmartEvents(
                Arrays.asList(eMedium, eSmall, eLarge),
                createClusterConfs(),
                2
        );

        Assert.assertEquals(new HashSet<>(Arrays.asList(eMedium, eLarge)), new HashSet<>(topEntityEvents));
    }

    @Test
    public void shouldReturnAllEntitiesGivenEntitiesFromTheSameDayAndSmallKPerDay() {
        SmartWeightsScorerAlgorithm scorerAlgorithm = Mockito.mock(SmartWeightsScorerAlgorithm.class);
        long startTime = 1234;
        SmartAggregatedRecordDataContainer eSmall = SmartAggregatedRecordDataContainerGenerator.createSmartAggregatedRecordDataContainer(scorerAlgorithm, startTime, 0.1);
        SmartAggregatedRecordDataContainer eMedium = SmartAggregatedRecordDataContainerGenerator.createSmartAggregatedRecordDataContainer(scorerAlgorithm, startTime, 0.2);
        SmartAggregatedRecordDataContainer eLarge = SmartAggregatedRecordDataContainerGenerator.createSmartAggregatedRecordDataContainer(scorerAlgorithm, startTime, 0.3);

        List<SmartAggregatedRecordDataContainer> topEntityEvents = new ClustersContributionsSimulator(scorerAlgorithm).calcTopSmartEvents(
                Arrays.asList(eMedium, eSmall, eLarge),
                createClusterConfs(),
                7
        );

        Assert.assertEquals(new HashSet<>(Arrays.asList(eSmall, eMedium, eLarge)), new HashSet<>(topEntityEvents));
    }

    @Test
    public void shouldReturnTopEntitiesGivenEntitiesFromDifferentDays() {
        SmartWeightsScorerAlgorithm scorerAlgorithm = Mockito.mock(SmartWeightsScorerAlgorithm.class);
        long startTime1 = 1234;
        SmartAggregatedRecordDataContainer eSmallD1 = SmartAggregatedRecordDataContainerGenerator.createSmartAggregatedRecordDataContainer(scorerAlgorithm, startTime1, 0.01);
        SmartAggregatedRecordDataContainer eMediumD1 = SmartAggregatedRecordDataContainerGenerator.createSmartAggregatedRecordDataContainer(scorerAlgorithm, startTime1, 0.02);
        SmartAggregatedRecordDataContainer eLargeD1 = SmartAggregatedRecordDataContainerGenerator.createSmartAggregatedRecordDataContainer(scorerAlgorithm, startTime1, 0.03);
        long startTime2 = 11111234;
        SmartAggregatedRecordDataContainer eSmallD2 = SmartAggregatedRecordDataContainerGenerator.createSmartAggregatedRecordDataContainer(scorerAlgorithm, startTime2, 0.11);
        SmartAggregatedRecordDataContainer eMediumD2 = SmartAggregatedRecordDataContainerGenerator.createSmartAggregatedRecordDataContainer(scorerAlgorithm, startTime2, 0.12);
        SmartAggregatedRecordDataContainer eLargeD2 = SmartAggregatedRecordDataContainerGenerator.createSmartAggregatedRecordDataContainer(scorerAlgorithm, startTime2, 0.13);

        List<SmartAggregatedRecordDataContainer> topEntityEvents = new ClustersContributionsSimulator(scorerAlgorithm).calcTopSmartEvents(
                Arrays.asList(eMediumD1, eSmallD1, eLargeD1, eMediumD2, eSmallD2, eLargeD2),
                createClusterConfs(),
                2
        );

        Assert.assertEquals(new HashSet<>(Arrays.asList(eMediumD1, eLargeD1, eMediumD2, eLargeD2)), new HashSet<>(topEntityEvents));
    }

    /*************************************************************************************************************
     ************************** ClustersContributionsSimulator::calcContributions tests **************************
     *************************************************************************************************************/

    private void assertContributions(Map<ClusterConf, Double> expectedContributions,
                                     Map<ClusterConf, Double> actualContributions) {
        List<Map<HashSet<String>, Double>> allContributions = Stream.of(expectedContributions, actualContributions).map(contributions -> contributions.entrySet().stream().collect(
                Collectors.toMap(
                        clusterSpecsAndContribution -> new HashSet<>(clusterSpecsAndContribution.getKey().getAggregationRecordNames()),
                        Map.Entry::getValue
                )
        )).collect(Collectors.toList());

        Map<HashSet<String>, Double> expected = allContributions.get(0);
        Map<HashSet<String>, Double> actual = allContributions.get(1);
        Assert.assertEquals(expected.size(), actual.size());
        expected.keySet().forEach(aggregatedFeatureEventNames -> {
            Assert.assertTrue(actual.containsKey(aggregatedFeatureEventNames));
            Assert.assertEquals(expected.get(aggregatedFeatureEventNames), actual.get(aggregatedFeatureEventNames), 0.00000001);
        });
    }

    @Test
    public void shouldReturnEmptyContributionsMapGivenNoData() {
        SmartWeightsScorerAlgorithm scorerAlgorithm = Mockito.mock(SmartWeightsScorerAlgorithm.class);

        Map<ClusterConf, Double> contributions = new ClustersContributionsSimulator(scorerAlgorithm).calcContributions(
                Collections.emptyList(),
                createClusterConfs()
        );

        Assert.assertTrue(contributions.isEmpty());
    }

    @Test
    public void shouldAssociateAllContributionToSingleClusterGivenOneEntityAndOneCluster() {
        SmartWeightsScorerAlgorithm scorerAlgorithm = Mockito.mock(SmartWeightsScorerAlgorithm.class);
        String featureName = "F1";
        ClusterConf c = createClusterConf(featureName);
        SmartAggregatedRecordDataContainer e = SmartAggregatedRecordDataContainerGenerator.createSmartAggregatedRecordDataContainer(
                scorerAlgorithm,
                1234,
                Collections.singletonMap(featureName, 50.0),
                0.01
        );

        Map<ClusterConf, Double> contributions = new ClustersContributionsSimulator(scorerAlgorithm).calcContributions(
                Collections.singletonList(e),
                createClusterConfs(c)
        );

        Map<ClusterConf, Double> expectedContributions = Collections.singletonMap(c, 1.0);
        assertContributions(expectedContributions, contributions);
    }

    @Test
    public void shouldDivideContributionBetweenParticipatingClusters() {
        SmartWeightsScorerAlgorithm scorerAlgorithm = Mockito.mock(SmartWeightsScorerAlgorithm.class);
        String featureName1 = "F1";
        ClusterConf c1 = createClusterConf(featureName1);
        String featureName2 = "F2";
        ClusterConf c2 = createClusterConf(featureName2);
        String featureName3 = "non participating feature";
        ClusterConf c3 = createClusterConf(featureName3);
        SmartAggregatedRecordDataContainer e = SmartAggregatedRecordDataContainerGenerator.createSmartAggregatedRecordDataContainer(
                scorerAlgorithm,
                1234,
                new HashMap<String, Double>() {{
                    put(featureName1, 40.0);
                    put(featureName2, 80.0);
                }},
                0.01
        );

        Map<ClusterConf, Double> contributions = new ClustersContributionsSimulator(scorerAlgorithm).calcContributions(
                Collections.singletonList(e),
                createClusterConfs(c1, c2, c3)
        );

        Map<ClusterConf, Double> expectedContributions = new HashMap<ClusterConf, Double>() {{
            put(c1, 1.0/3);
            put(c2, 2.0/3);
            put(c3, 0.0);
        }};
        assertContributions(expectedContributions, contributions);
    }

    @Test
    public void shouldDivideContributionBetweenParticipatingClustersAccordingToMaxScoreWithinCluster() {
        SmartWeightsScorerAlgorithm scorerAlgorithm = Mockito.mock(SmartWeightsScorerAlgorithm.class);
        String featureName1 = "F1";
        String featureName2 = "F2";
        ClusterConf c1 = createClusterConf(featureName1, featureName2);
        String featureName3 = "F3";
        ClusterConf c2 = createClusterConf(featureName3);
        SmartAggregatedRecordDataContainer e = SmartAggregatedRecordDataContainerGenerator.createSmartAggregatedRecordDataContainer(
                scorerAlgorithm,
                1234,
                new HashMap<String, Double>() {{
                    put(featureName1, 30.0);
                    put(featureName2, 40.0);
                    put(featureName3, 80.0);
                }},
                0.01
        );

        Map<ClusterConf, Double> contributions = new ClustersContributionsSimulator(scorerAlgorithm).calcContributions(
                Collections.singletonList(e),
                createClusterConfs(c1, c2)
        );

        Map<ClusterConf, Double> expectedContributions = new HashMap<ClusterConf, Double>() {{
            put(c1, 1.0/3);
            put(c2, 2.0/3);
        }};
        assertContributions(expectedContributions, contributions);
    }

    @Test
    public void shouldAssociateAllContributionToSingleClusterGivenManyEntitiesAndOneCluster() {
        SmartWeightsScorerAlgorithm scorerAlgorithm = Mockito.mock(SmartWeightsScorerAlgorithm.class);
        String featureName = "F1";
        ClusterConf c = createClusterConf(featureName);
        SmartAggregatedRecordDataContainer e = SmartAggregatedRecordDataContainerGenerator.createSmartAggregatedRecordDataContainer(
                scorerAlgorithm,
                1234,
                Collections.singletonMap(featureName, 50.0),
                0.01
        );

        Map<ClusterConf, Double> contributions = new ClustersContributionsSimulator(scorerAlgorithm).calcContributions(
                Arrays.asList(e, e),
                createClusterConfs(c)
        );

        Map<ClusterConf, Double> expectedContributions = Collections.singletonMap(c, 1.0);
        assertContributions(expectedContributions, contributions);
    }

    @Test
    public void shouldDivideContributionBetweenParticipatingClustersAccordingToEntityEventValue() {
        SmartWeightsScorerAlgorithm scorerAlgorithm = Mockito.mock(SmartWeightsScorerAlgorithm.class);
        String featureName1 = "F1";
        ClusterConf c1 = createClusterConf(featureName1);
        String featureName2 = "F2";
        ClusterConf c2 = createClusterConf(featureName2);
        SmartAggregatedRecordDataContainer e1 = SmartAggregatedRecordDataContainerGenerator.createSmartAggregatedRecordDataContainer(
                scorerAlgorithm,
                1234,
                new HashMap<String, Double>() {{
                    put(featureName1, 30.0);
                }},
                0.01
        );
        SmartAggregatedRecordDataContainer e2 = SmartAggregatedRecordDataContainerGenerator.createSmartAggregatedRecordDataContainer(
                scorerAlgorithm,
                1234,
                new HashMap<String, Double>() {{
                    put(featureName2, 30.0);
                }},
                0.02
        );

        Map<ClusterConf, Double> contributions = new ClustersContributionsSimulator(scorerAlgorithm).calcContributions(
                Arrays.asList(e1, e2),
                createClusterConfs(c1, c2)
        );

        Map<ClusterConf, Double> expectedContributions = new HashMap<ClusterConf, Double>() {{
            put(c1, 1.0/3);
            put(c2, 2.0/3);
        }};
        assertContributions(expectedContributions, contributions);
    }

    /*************************************************************************************************************
     ****************************** ClustersContributionsSimulator::simulate tests *******************************
     *************************************************************************************************************/

    @Test
    public void shouldCallInnerWorkingsCorrectlyWhenCreateClusterConfs() {
        // this is an uber test that just makes sure everything is glued in the right way
        // (all the other tests test the inner functions, but no one guarantees they are
        // called in the right order. This test tests exactly that)
        SmartWeightModelTestUtils.TestData testData = new SmartWeightModelTestUtils.TestData();

        ClustersContributionsSimulator simulator = new ClustersContributionsSimulator(new SmartWeightsScorerAlgorithm());
        Map<ClusterConf, Double> contributions = simulator.simulate(testData.smartAggregatedRecordDataContainers, testData.clusterConfs);

        Map<ClusterConf, Double> expectedContributions = new HashMap<ClusterConf, Double>() {{
            put(testData.clusterConfs.get(0), 0.04507233044160597);
            put(testData.clusterConfs.get(1), 0.052289474027702884);
            put(testData.clusterConfs.get(2), 0.050066470289609255);
            put(testData.clusterConfs.get(3), 0.05773153766577976);
            put(testData.clusterConfs.get(4), 0.3903772390719948);
            put(testData.clusterConfs.get(5), 0.3593906180617014);
            put(testData.clusterConfs.get(6), 0.04507233044160597);
        }};
        assertContributions(expectedContributions, contributions);
    }

}