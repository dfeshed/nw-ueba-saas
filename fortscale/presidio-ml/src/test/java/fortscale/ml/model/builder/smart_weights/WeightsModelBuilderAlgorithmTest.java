package fortscale.ml.model.builder.smart_weights;

import fortscale.ml.model.retriever.smart_data.SmartAggregatedRecordDataContainer;
import fortscale.ml.scorer.algorithms.SmartWeightsScorerAlgorithm;
import fortscale.smart.record.conf.ClusterConf;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.Mockito;
import org.mockito.stubbing.OngoingStubbing;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

import static fortscale.ml.model.builder.smart_weights.SmartWeightModelTestUtils.createClusterConf;
import static fortscale.ml.model.builder.smart_weights.SmartWeightModelTestUtils.createClusterConfs;

/**
 * Created by barak_schuster on 31/08/2017.
 */
public class WeightsModelBuilderAlgorithmTest {
    private WeightsModelBuilderAlgorithm builderAlgorithm;
    private AggregatedFeatureReliability aggregatedFeaturesReliabilityMock;
    private ClustersContributionsSimulator clustersContributionsSimulator;

    /**
     * Create a {@link WeightsModelBuilderAlgorithm} with all its dependencies mocked.
     */
    @Before
    public void createBuilderAlgorithm() {
        aggregatedFeaturesReliabilityMock = Mockito.mock(AggregatedFeatureReliability.class);
        BiFunction<List<SmartAggregatedRecordDataContainer>, Integer, AggregatedFeatureReliability> aggregatedFeatureEventsReliabilityFactory =
                (smartAggregatedRecordDataContainers, numOfContexts) -> aggregatedFeaturesReliabilityMock;
        clustersContributionsSimulator = Mockito.mock(ClustersContributionsSimulator.class);
        builderAlgorithm = new WeightsModelBuilderAlgorithm(
                aggregatedFeatureEventsReliabilityFactory,
                clustersContributionsSimulator
        );
    }

    /*************************************************************************************************************
     ***************** WeightsModelBuilderAlgorithm::calculateClusterConfsViaReliability tests *****************
     *************************************************************************************************************/

    private List<ClusterConf> calculateClusterConfsViaReliability(List<ClusterConf> clusterConfs) {
        return builderAlgorithm.calculateClusterConfsViaReliability(
                Mockito.mock(List.class),
                clusterConfs,
                0
        );
    }

    private void setReliabilityPenalty(String fullAggregatedFeatureEventName, double penalty) {
        Mockito.when(aggregatedFeaturesReliabilityMock.calcReliabilityPenalty(Mockito.eq(fullAggregatedFeatureEventName)))
                .thenReturn(penalty);
    }

    @Test
    public void shouldGiveMaximalWeightForClusterWithOneReliableFeature() {
        String fullAggregatedFeatureEventName = "F1";
        setReliabilityPenalty(fullAggregatedFeatureEventName, 0.0);
        List<ClusterConf> clusterConfsPrototype = createClusterConfs(createClusterConf(fullAggregatedFeatureEventName));

        List<ClusterConf> clusterConfs = calculateClusterConfsViaReliability(clusterConfsPrototype);

        Assert.assertEquals(
                WeightsModelBuilderAlgorithm.MAX_ALLOWED_WEIGHT_DEFAULT,
                clusterConfs.get(0).getWeight(),
                0.000001
        );
    }

    @Test
    public void shouldGiveMinimalWeightForClusterWithOneReallyUnreliableFeature() {
        String fullAggregatedFeatureEventName = "F1";
        setReliabilityPenalty(fullAggregatedFeatureEventName, Double.MAX_VALUE);
        List<ClusterConf> clusterConfsPrototype = createClusterConfs(createClusterConf(fullAggregatedFeatureEventName));

        List<ClusterConf> clusterConfs = calculateClusterConfsViaReliability(clusterConfsPrototype);

        Assert.assertEquals(
                0.5 * WeightsModelBuilderAlgorithm.MAX_ALLOWED_WEIGHT_DEFAULT,
                clusterConfs.get(0).getWeight(),
                0.001
        );
    }

    @Test
    public void shouldGiveSmallerWeightForClusterWithLessReliableFeature() {
        String moreReliableFeatureName = "F_more_reliable";
        String lessReliableFeatureName = "F_less_reliable";
        setReliabilityPenalty(moreReliableFeatureName, 0.1);
        setReliabilityPenalty(lessReliableFeatureName, 0.2);
        List<ClusterConf> clusterConfsPrototype = createClusterConfs(
                createClusterConf(moreReliableFeatureName),
                createClusterConf(lessReliableFeatureName)
        );

        List<ClusterConf> clusterConfs = calculateClusterConfsViaReliability(clusterConfsPrototype);

        Assert.assertTrue(clusterConfs.get(0).getWeight() > clusterConfs.get(1).getWeight());
    }

    @Test
    public void shouldGiveWeightAccordingToTheMostUnreliableFeatureInTheCluster() {
        String moreReliableFeatureName = "F_more_reliable";
        String lessReliableFeatureName = "F_less_reliable";
        setReliabilityPenalty(moreReliableFeatureName, 0.1);
        setReliabilityPenalty(lessReliableFeatureName, 0.2);
        List<ClusterConf> clusterConfsPrototype = createClusterConfs(
                createClusterConf(lessReliableFeatureName),
                createClusterConf(moreReliableFeatureName, lessReliableFeatureName)
        );

        List<ClusterConf> clusterConfs = calculateClusterConfsViaReliability(clusterConfsPrototype);

        Assert.assertEquals(
                clusterConfs.get(0).getWeight(),
                clusterConfs.get(1).getWeight(),
                0.00000001
        );
    }

    /*************************************************************************************************************
     ***************** WeightsModelBuilderAlgorithm::calculateClusterConfsViaSimulations tests *****************
     *************************************************************************************************************/

    /**
     * Call {@link WeightsModelBuilderAlgorithm::calculateClusterConfsViaSimulations} after mocking
     * {@link ClustersContributionsSimulator::simulate}'s results.
     * @param initialClusterConfs the {@link List<ClusterConf>} passed to
     *                          {@link WeightsModelBuilderAlgorithm::calculateClusterConfsViaSimulations}.
     * @param clusterSpecsToContributionSimulations the simulations results.
     *                                              {@link WeightsModelBuilderAlgorithm::calculateClusterConfsViaSimulations}
     *                                              iteratively runs many simulations. Each simulation result is
     *                                              specified here.
     * @return
     */
    private List<ClusterConf> calculateClusterConfsViaSimulations(List<ClusterConf> initialClusterConfs,
                                                                  Map<ClusterConf, Double>... clusterSpecsToContributionSimulations) {
        if (clusterSpecsToContributionSimulations.length > 0) {
            OngoingStubbing<Map> stubbing = Mockito.when(clustersContributionsSimulator.simulate(
                    Mockito.any(List.class),
                    Mockito.anyListOf(ClusterConf.class)
            ));
            for (Map<ClusterConf, Double> simulationsResult : clusterSpecsToContributionSimulations) {
                stubbing = stubbing.thenReturn(simulationsResult);
            }

        }
        return builderAlgorithm.calculateClusterConfsViaSimulations(
                Mockito.mock(List.class),
                initialClusterConfs,
                clusterSpecsToContributionSimulations.length
        );
    }

    /**
     * A builder which can build a mocked ClusterSpec to contribution map.
     * Note: since {@link WeightsModelBuilderAlgorithm::calculateClusterConfsViaSimulations} changes the weights of
     * clusters, if we want to specify in the tests what a ClusterSpec's contribution is, we HAVE to know what's the
     * ClusterSpec's weight is. But we don't want to get too deep to these details. We want to state what is the
     * ClusterSpec's contribution no matter what is its weight. This is why this builder builds a mock instead of a
     * real map. Using a mock, we can mock its {@link Map::get} method, and examine the passed ClusterSpec argument.
     * At this point we'll ignore the ClusterSpec's weight, and return the contribution associated with a ClusterConf
     * with the same {@link ClusterConf::getAggregatedFeatureEventNames} stated by
     * calling {@link ClusterConfToContributionBuilder::append}.
     */
    private static class ClusterConfToContributionBuilder {
        // map from a ClusterSpec::getAggregatedFeatureEventNames() to the ClusterSpec's contribution
        private Map<List<String>, Double> aggregatedFeatureEventNamesToContribution;

        /**
         * A shorthand for creating a contribution map consisting of only one ClusterSpec.
         */
        public static Map<ClusterConf, Double> singleton(ClusterConf clusterSpecs, double contribution) {
            return new ClusterConfToContributionBuilder().append(clusterSpecs, contribution).build();
        }

        public ClusterConfToContributionBuilder() {
            aggregatedFeatureEventNamesToContribution = new HashMap<>();
        }

        /**
         * Set the contribution of the given ClusterSpec.
         */
        public ClusterConfToContributionBuilder append(ClusterConf clusterSpecs, double contribution) {
            aggregatedFeatureEventNamesToContribution.put(clusterSpecs.getAggregationRecordNames(), contribution);
            return this;
        }

        /**
         * Build a mocked ClusterSpec to contribution map with the prepared in advance contributions specified by
         * calling {@link ClusterConfToContributionBuilder::append}
         */
        public Map<ClusterConf, Double> build() {
            Map<ClusterConf, Double> clusterToContribution = Mockito.mock(Map.class);
            aggregatedFeatureEventNamesToContribution.forEach((aggregatedFeatureEventNames, contribution) ->
                    Mockito.when(clusterToContribution.get(Mockito.argThat(new ArgumentMatcher<ClusterConf>() {
                        @Override
                        public boolean matches(Object argument) {
                            return argument instanceof ClusterConf &&
                                    ((ClusterConf)argument).getAggregationRecordNames()
                                            .equals(aggregatedFeatureEventNames);
                        }
                    }))).thenReturn(contribution));
            Mockito.when(clusterToContribution.values()).thenReturn(aggregatedFeatureEventNamesToContribution.values());
            return clusterToContribution;
        }
    }

    @Test
    public void shouldReturnInitialClusterConfsIfNumOfSimulationsIsZero() {
        List<ClusterConf> initialClusterConfs= createClusterConfs(createClusterConf("F"));

        List<ClusterConf> clusterConfs = calculateClusterConfsViaSimulations(initialClusterConfs);

        Assert.assertEquals(initialClusterConfs, clusterConfs);
    }

    @Test
    public void shouldReturnInitialClusterConfsIfNumOfSimulationsIsOne() {
        // after running only one simulation, WeightsModelBuilderAlgorithm knows which cluster's weight to reduce
        // in the second simulation. Only in the second simulation the algorithm evaluate the goodness of this choice.
        // This is why after only one simulation nothing is supposed to change in the result (compared to 0
        // simulations).
        ClusterConf c = createClusterConf("F");
        List<ClusterConf> initialClusterConfs = createClusterConfs(c);
        Map<ClusterConf, Double> clusterToContribution = Collections.singletonMap(c, 1.0);

        List<ClusterConf> clusterConfs = calculateClusterConfsViaSimulations(initialClusterConfs, clusterToContribution);

        Assert.assertEquals(initialClusterConfs, clusterConfs);
    }

    @Test
    public void shouldReturnInitialClusterConfsIfNoImprovementWasMade() {
        ClusterConf c = createClusterConf("F");
        List<ClusterConf> initialClusterConfs = createClusterConfs(c);
        Map<ClusterConf, Double> clusterSpecsToContributionOfInitialClusterConfs = ClusterConfToContributionBuilder.singleton(c, 1.0);
        Map<ClusterConf, Double> clusterSpecsToContributionAfterOneImprovementAttempt = ClusterConfToContributionBuilder.singleton(c, 1.1);
        List<ClusterConf> clusterConfs = calculateClusterConfsViaSimulations(
                initialClusterConfs,
                clusterSpecsToContributionOfInitialClusterConfs,
                clusterSpecsToContributionAfterOneImprovementAttempt
        );

        Assert.assertEquals(initialClusterConfs, clusterConfs);
    }

    @Test
    public void shouldDecreaseClusterWeightIfImprovementWasMade() {
        ClusterConf c = createClusterConf("F");
        Map<ClusterConf, Double> clusterSpecsToContributionOfInitialClusterConfs = ClusterConfToContributionBuilder.singleton(c, 1.0);
        Map<ClusterConf, Double> clusterSpecsToContributionAfterOneImprovementAttempt = ClusterConfToContributionBuilder.singleton(c, 0.9);

        List<ClusterConf> clusterConfs = calculateClusterConfsViaSimulations(
                createClusterConfs(c),
                clusterSpecsToContributionOfInitialClusterConfs,
                clusterSpecsToContributionAfterOneImprovementAttempt
        );

        ClusterConf resClusterConf = clusterConfs.get(0);
        Assert.assertTrue(resClusterConf.getWeight() < c.getWeight());
    }

    @Test
    public void shouldReturnBestWeightEvenIfItWasNotTheLastSimulation() {
        ClusterConf c = createClusterConf("F");
        Map<ClusterConf, Double> clusterSpecsToContributionOfInitialClusterConfs = ClusterConfToContributionBuilder.singleton(c, 1.0);
        Map<ClusterConf, Double> clusterSpecsToContributionAfterOneImprovementAttempt = ClusterConfToContributionBuilder.singleton(c, 0.9);
        Map<ClusterConf, Double> clusterSpecsToContributionAfterTwoImprovementAttempts = ClusterConfToContributionBuilder.singleton(c, 1.1);

        List<ClusterConf> clusterConfs = calculateClusterConfsViaSimulations(
                createClusterConfs(c),
                clusterSpecsToContributionOfInitialClusterConfs,
                clusterSpecsToContributionAfterOneImprovementAttempt
        );
        List<ClusterConf> clusterConfsAfterMoreNotImprovingSimulations = calculateClusterConfsViaSimulations(
                createClusterConfs(c),
                clusterSpecsToContributionOfInitialClusterConfs,
                clusterSpecsToContributionAfterOneImprovementAttempt,
                clusterSpecsToContributionAfterTwoImprovementAttempts
        );

        Assert.assertEquals(clusterConfs, clusterConfsAfterMoreNotImprovingSimulations);
    }

    @Test
    public void shouldReturnSmallerWeightIfMoreSimulationsWereNeededToMakeAnImprovement() {
        ClusterConf c = createClusterConf("F");
        Map<ClusterConf, Double> clusterSpecsToContributionOfInitialClusterSpecs = ClusterConfToContributionBuilder.singleton(c, 1.0);
        Map<ClusterConf, Double> betterClusterConfToContribution = ClusterConfToContributionBuilder.singleton(c, 0.9);

        List<ClusterConf> clusterConfs = calculateClusterConfsViaSimulations(
                createClusterConfs(c),
                clusterSpecsToContributionOfInitialClusterSpecs,
                betterClusterConfToContribution
        );
        List<ClusterConf> clusterConfsAfterMoreSimulations = calculateClusterConfsViaSimulations(
                createClusterConfs(c),
                clusterSpecsToContributionOfInitialClusterSpecs,
                clusterSpecsToContributionOfInitialClusterSpecs,
                betterClusterConfToContribution
        );

        Assert.assertTrue(clusterConfsAfterMoreSimulations.get(0).getWeight() < clusterConfs.get(0).getWeight());
    }

    @Test
    public void shouldDecreaseOnlyWeightOfClustersWithBiggestContribution() {
        ClusterConf lessNoisyCluster = createClusterConf("F1");
        ClusterConf moreNoisyCluster1 = createClusterConf("F2");
        ClusterConf moreNoisyCluster2 = createClusterConf("F3");
        Map<ClusterConf, Double> clusterSpecsToContributionOfInitialClusterSpecs = new ClusterConfToContributionBuilder()
                .append(lessNoisyCluster, 0.9)
                .append(moreNoisyCluster1, 1.0)
                .append(moreNoisyCluster2, 1.0)
                .build();
        Map<ClusterConf, Double> betterClusterConfToContribution = new ClusterConfToContributionBuilder()
                .append(lessNoisyCluster, 0.9)
                .append(moreNoisyCluster1, 0.9)
                .append(moreNoisyCluster2, 0.9)
                .build();

        List<ClusterConf> clusterConfs = calculateClusterConfsViaSimulations(
                createClusterConfs(lessNoisyCluster, moreNoisyCluster1, moreNoisyCluster2),
                // the first simulation is supposed to notice moreNoisyCluster's contribution is bigger, and update
                // the List<ClusterConf> accordingly
                clusterSpecsToContributionOfInitialClusterSpecs,
                // we need another simulation with improved contribution so the List<ClusterConf> calculated in the previous
                // simulation will be considered as an improvement, and will be returned here to the test for further
                // assertions
                betterClusterConfToContribution
        );

        ClusterConf lessNoisyClusterRes = clusterConfs.get(0);
        ClusterConf moreNoisyClusterRes1 = clusterConfs.get(1);
        ClusterConf moreNoisyClusterRes2 = clusterConfs.get(2);
        Assert.assertTrue(moreNoisyClusterRes1.getWeight() < moreNoisyCluster1.getWeight());
        Assert.assertEquals(moreNoisyClusterRes1.getWeight(), moreNoisyClusterRes2.getWeight(), 0.000000001);
        Assert.assertEquals(lessNoisyCluster.getWeight(), lessNoisyClusterRes.getWeight(), 0.000000001);
    }

    /*************************************************************************************************************
     ************************* WeightsModelBuilderAlgorithm::createList<ClusterConf> tests **************************
     *************************************************************************************************************/

    @Test
    public void shouldReturnClusterSpecsPrototypeWhenEmptyModelBuilderData() {
        List<ClusterConf> clusterConfsPrototype = createClusterConfs(createClusterConf("F1"));

        List<ClusterConf> clusterConfs = builderAlgorithm.createWeightsClusterConfs(clusterConfsPrototype, Collections.emptyList(), 0, 100, null);

        Assert.assertEquals(clusterConfsPrototype, clusterConfs);
    }

    @Test
    public void shouldCallInnerWorkingsCorrectlyWhenCreateClusterSpecs() {
        // this is an uber test that just makes sure everything is glued in the right way
        // (all the other tests test the inner functions, but no one guarantees they are
        // called in the right order. This test tests exactly that)
        SmartWeightModelTestUtils.TestData testData = new SmartWeightModelTestUtils.TestData();

        WeightsModelBuilderAlgorithm builderAlgorithm = new WeightsModelBuilderAlgorithm(
                AggregatedFeatureReliability::new,
                new ClustersContributionsSimulator(createSmartWeightsScorerAlgorithm())
        );
        List<ClusterConf> clusterConfs = builderAlgorithm.createWeightsClusterConfs(testData.clusterConfs, testData.smartAggregatedRecordDataContainers, 100, 100, Collections.singletonList("F6"));

        Assert.assertEquals(0.0256554137983594, clusterConfs.get(0).getWeight(), 0.00000001);
        Assert.assertEquals(0.0256554137983594, clusterConfs.get(1).getWeight(), 0.00000001);
        Assert.assertEquals(0.032731683358239394, clusterConfs.get(2).getWeight(), 0.00000001);
        Assert.assertEquals(0.025817110649495652, clusterConfs.get(3).getWeight(), 0.00000001);
        Assert.assertEquals(0.010849231041599516, clusterConfs.get(4).getWeight(), 0.00000001);
        Assert.assertEquals(0.010841916663386316, clusterConfs.get(5).getWeight(), 0.00000001);
        Assert.assertEquals(0.0, clusterConfs.get(6).getWeight(), 0.0);
    }

    private SmartWeightsScorerAlgorithm createSmartWeightsScorerAlgorithm(){
        return new SmartWeightsScorerAlgorithm(0.5, 50);
    }
}