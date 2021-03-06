package fortscale.ml.model.builder.smart_weights;

import fortscale.ml.model.SmartWeightsModel;
import fortscale.ml.model.metrics.WeightModelBuilderMetricsContainer;
import fortscale.ml.model.retriever.smart_data.SmartWeightsModelBuilderData;
import fortscale.ml.scorer.algorithms.SmartWeightsScorerAlgorithm;
import fortscale.smart.record.conf.ClusterConf;
import fortscale.smart.record.conf.SmartRecordConf;
import fortscale.smart.record.conf.SmartRecordConfService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Collections;
import java.util.List;

/**
 * @author Barak Schuster.
 * @author Lior Govrin.
 */
public class WeightsModelBuilderTest {

    private List<ClusterConf> clusterConfs;
    private WeightsModelBuilderAlgorithm modelBuilderAlgorithm;
    private SmartRecordConfService smartRecordConfService;
    private WeightModelBuilderMetricsContainer weightModelBuilderMetricsContainer;
    @Before
    public void setup() {
        smartRecordConfService = Mockito.mock(SmartRecordConfService.class);
        weightModelBuilderMetricsContainer = Mockito.mock(WeightModelBuilderMetricsContainer.class);
        clusterConfs =
                Collections.singletonList(new ClusterConf(Collections.singletonList("F1"), 0.1))
        ;
        modelBuilderAlgorithm = new WeightsModelBuilderAlgorithm(
                AggregatedFeatureReliability::new,
                new ClustersContributionsSimulator(createSmartWeightsScorerAlgorithm())
        );
    }

    private SmartWeightsScorerAlgorithm createSmartWeightsScorerAlgorithm(){
        return new SmartWeightsScorerAlgorithm(0.5, 50);
    }

    private WeightsModelBuilderConf createAndRegisterWeightsModelBuilderConf() {
        String smartRecordConfName = "smartRecordConfName";
        SmartRecordConf entityEventConf = Mockito.mock(SmartRecordConf.class);
        Mockito.when(entityEventConf.getClusterConfs()).thenReturn(clusterConfs);
        Mockito.when(smartRecordConfService.getSmartRecordConf(Mockito.eq(smartRecordConfName))).thenReturn(entityEventConf);
        return new WeightsModelBuilderConf(smartRecordConfName, null);
    }

    private WeightsModelBuilder createModelBuilder() {
        return createModelBuilder(modelBuilderAlgorithm);
    }

    private WeightsModelBuilder createModelBuilder(WeightsModelBuilderAlgorithm algorithm) {
        return new WeightsModelBuilder(createAndRegisterWeightsModelBuilderConf(), algorithm,smartRecordConfService, weightModelBuilderMetricsContainer);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailGivenNullAsConf() {
        new WeightsModelBuilder(null, modelBuilderAlgorithm,smartRecordConfService, weightModelBuilderMetricsContainer);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailGivenNullAsWeightsModelBuilderAlgorithm() {
        new WeightsModelBuilder(createAndRegisterWeightsModelBuilderConf(), null,smartRecordConfService, weightModelBuilderMetricsContainer);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailGivenInvalidEntityEventConfName() {
        String entityEventConfName = "smartRecordConfName";
        Mockito.when(smartRecordConfService.getSmartRecordConf(Mockito.eq(entityEventConfName))).thenReturn(null);

        new WeightsModelBuilder(new WeightsModelBuilderConf(entityEventConfName, null), modelBuilderAlgorithm, smartRecordConfService, weightModelBuilderMetricsContainer);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailGivenIllegalInputType() {
        createModelBuilder().build("");
    }

    @Test
    public void shouldCreateModelWithClusterConfsSpecifiedByAlgorithm() {
        SmartWeightsModelBuilderData modelBuilderData = new SmartWeightsModelBuilderData();
        List<ClusterConf> clusterConfs = Collections.emptyList();
        WeightsModelBuilderAlgorithm algorithm = Mockito.mock(WeightsModelBuilderAlgorithm .class);
        Mockito.when(algorithm.createWeightsClusterConfs(
                Mockito.eq(this.clusterConfs),
                Mockito.eq(modelBuilderData.getSmartAggregatedRecordDataContainers()),
                Mockito.eq(modelBuilderData.getNumOfContexts()),
                Mockito.anyInt(),
                Mockito.eq(weightModelBuilderMetricsContainer)
        )).thenReturn(clusterConfs);

        SmartWeightsModel model = (SmartWeightsModel) createModelBuilder(algorithm).build(modelBuilderData);

        Assert.assertEquals(clusterConfs, model.getClusterConfs());
    }
}
