package fortscale.ml.model.builder.factories;

import fortscale.ml.model.builder.IModelBuilder;
import fortscale.ml.model.builder.smart_weights.*;
import fortscale.ml.model.metrics.WeightModelBuilderMetricsContainer;
import fortscale.ml.model.retriever.smart_data.SmartAggregatedRecordDataContainer;
import fortscale.ml.scorer.algorithms.SmartWeightsScorerAlgorithm;
import fortscale.smart.record.conf.SmartRecordConfService;
import fortscale.utils.factory.AbstractServiceAutowiringFactory;
import fortscale.utils.factory.FactoryConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.BiFunction;

import static fortscale.ml.model.builder.smart_weights.WeightsModelBuilderConf.WEIGHTS_MODEL_BUILDER;

@SuppressWarnings("unused")
@Component
public class WeightsModelBuilderFactory extends AbstractServiceAutowiringFactory<IModelBuilder> {
    @Autowired
    private SmartRecordConfService smartRecordConfService;
    @Autowired
    private SmartWeightsScorerAlgorithm smartWeightsScorerAlgorithm;

    @Value("${presidio.ade.model.smart.weights.builder.use.weight.for.contribution.calc:true}")
    private Boolean useWeightForContributionCalculation;
    @Value("${presidio.ade.model.smart.weights.builder.max.allowed.weight:0.1}")
    private Double maxAllowedWeight;
    @Value("${presidio.ade.model.smart.weights.builder.min.allowed.weight:0.05}")
    private Double minAllowedWeight;
    @Value("${presidio.ade.model.smart.weights.builder.penalty.log.base:5}")
    private Double penaltyLogBase;
    @Value("${presidio.ade.model.smart.weights.builder.simulation.weight.decay.factor:0.97}")
    private Double simulationWeightDecayFactor;
    @Autowired
    private WeightModelBuilderMetricsContainer weightModelBuilderMetricsContainer;


    @Override
    public String getFactoryName() {
        return WEIGHTS_MODEL_BUILDER;
    }

    @Override
    public IModelBuilder getProduct(FactoryConfig factoryConfig) {
        BiFunction<List<SmartAggregatedRecordDataContainer>, Integer, AggregatedFeatureReliability> listIntegerAggregatedFeatureReliabilityBiFunction = (smartAggregatedRecordDataContainers, numOfContexts) -> new AggregatedFeatureReliability(smartAggregatedRecordDataContainers, numOfContexts);
        ClustersContributionsSimulator clustersContributionsSimulator = new ClustersContributionsSimulator(smartWeightsScorerAlgorithm, useWeightForContributionCalculation);
        WeightsModelBuilderAlgorithm algorithm = new WeightsModelBuilderAlgorithm(listIntegerAggregatedFeatureReliabilityBiFunction, clustersContributionsSimulator,
                maxAllowedWeight, minAllowedWeight,
                penaltyLogBase, simulationWeightDecayFactor);
        return new WeightsModelBuilder((WeightsModelBuilderConf) factoryConfig, algorithm, smartRecordConfService, weightModelBuilderMetricsContainer);
    }
}
