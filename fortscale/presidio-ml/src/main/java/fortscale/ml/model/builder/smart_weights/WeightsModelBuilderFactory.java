package fortscale.ml.model.builder.smart_weights;

import fortscale.ml.model.builder.IModelBuilder;
import fortscale.ml.model.retriever.smart_data.SmartAggregatedRecordDataContainer;
import fortscale.ml.scorer.algorithms.SmartWeightsScorerAlgorithm;
import fortscale.smart.record.conf.SmartRecordConfService;
import fortscale.utils.factory.AbstractServiceAutowiringFactory;
import fortscale.utils.factory.FactoryConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.BiFunction;

import static fortscale.ml.model.builder.smart_weights.WeightsModelBuilderConf.WEIGHTS_MODEL_BUILDER;

/**
 * Created by barak_schuster on 30/08/2017.
 */
@Component
public class WeightsModelBuilderFactory extends AbstractServiceAutowiringFactory<IModelBuilder> {
    @Autowired
    private SmartRecordConfService smartRecordConfService;

    @Override
    public String getFactoryName() {
        return WEIGHTS_MODEL_BUILDER;
    }

    @Override
    public IModelBuilder getProduct(FactoryConfig factoryConfig) {

        BiFunction<List<SmartAggregatedRecordDataContainer>, Integer, AggregatedFeatureReliability> listIntegerAggregatedFeatureReliabilityBiFunction = (smartAggregatedRecordDataContainers, numOfContexts) -> new AggregatedFeatureReliability(smartAggregatedRecordDataContainers, numOfContexts);
        SmartWeightsScorerAlgorithm scorerAlgorithm = new SmartWeightsScorerAlgorithm();
        ClustersContributionsSimulator clustersContributionsSimulator = new ClustersContributionsSimulator(scorerAlgorithm);
        WeightsModelBuilderAlgorithm algorithm = new WeightsModelBuilderAlgorithm(listIntegerAggregatedFeatureReliabilityBiFunction, clustersContributionsSimulator);

        return new WeightsModelBuilder((WeightsModelBuilderConf) factoryConfig, algorithm, smartRecordConfService);
    }
}
