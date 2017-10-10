package fortscale.ml.model.builder.smart_weights;

import fortscale.ml.model.Model;
import fortscale.ml.model.SmartWeightsModel;
import fortscale.ml.model.builder.IModelBuilder;
import fortscale.ml.model.retriever.smart_data.SmartWeightsModelBuilderData;
import fortscale.smart.record.conf.SmartRecordConf;
import fortscale.smart.record.conf.SmartRecordConfService;
import fortscale.utils.logging.Logger;
import org.springframework.util.Assert;

import java.util.List;

/**
 * Full documentation can be found here: https://fortscale.atlassian.net/wiki/pages/viewpage.action?pageId=75071492
 */
public class WeightsModelBuilder implements IModelBuilder {
    private static final Logger logger = Logger.getLogger(WeightsModelBuilder.class);

    private int numOfSimulations;
    private SmartRecordConf smartRecordConf;
    private List<String> zeroWeightFeatures;
    private WeightsModelBuilderAlgorithm algorithm;

    public WeightsModelBuilder(WeightsModelBuilderConf conf, WeightsModelBuilderAlgorithm algorithm, SmartRecordConfService smartRecordConfService) {
        Assert.notNull(conf, "conf must be not null");
        Assert.notNull(algorithm, "algorithm must be not null");
        String smartRecordConfName = conf.getSmartRecordConfName();
        SmartRecordConf entityEventConf = smartRecordConfService.getSmartRecordConf(smartRecordConfName);
        Assert.notNull(entityEventConf, String.format("did not found smartConf for name=%s", smartRecordConfName));
        this.numOfSimulations = conf.getNumOfSimulations();
        this.smartRecordConf = entityEventConf;
        this.zeroWeightFeatures = conf.getZeroWeightFeatures();
        this.algorithm = algorithm;
    }

    @Override
    public Model build(Object modelBuilderData) {
        logger.debug("building {} for {}", SmartWeightsModel.class.getSimpleName(), smartRecordConf.getName());
        SmartWeightsModelBuilderData smartWeightsModelBuilderData = castModelBuilderData(modelBuilderData);
        return new SmartWeightsModel().setClusterConfs(algorithm.createWeightsClusterConfs(
                smartRecordConf.getClusterConfs(),
                smartWeightsModelBuilderData.getSmartAggregatedRecordDataContainers(),
                smartWeightsModelBuilderData.getNumOfContexts(),
                numOfSimulations,
                zeroWeightFeatures
        ));
    }

    protected SmartWeightsModelBuilderData castModelBuilderData(Object modelBuilderData) {
        Assert.isInstanceOf(SmartWeightsModelBuilderData.class, modelBuilderData, String.format("model builder data type is=%s, should be=%s", modelBuilderData.getClass().getSimpleName(), SmartWeightsModelBuilderData.class.getSimpleName()));
        return (SmartWeightsModelBuilderData) modelBuilderData;
    }
}
