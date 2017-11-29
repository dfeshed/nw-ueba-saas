package fortscale.ml.model.builder;

import fortscale.common.util.GenericHistogram;
import fortscale.ml.model.Model;
import fortscale.ml.model.SMARTMaxValuesModel;
import fortscale.ml.model.retriever.smart_data.ContextSmartValueData;
import fortscale.ml.model.retriever.smart_data.SmartValueData;
import fortscale.utils.ConversionUtils;
import fortscale.utils.time.TimeService;
import org.springframework.util.Assert;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

public class SMARTMaxValuesModelBuilder implements IModelBuilder {
    private static final String MODEL_BUILDER_DATA_TYPE_ERROR_MSG = String.format(
            "Model builder data must be of type %s.", SmartValueData.class.getSimpleName());
    private long partitionsResolutionInSeconds;

    public SMARTMaxValuesModelBuilder(SMARTMaxValuesModelBuilderConf config) {
        partitionsResolutionInSeconds = config.getPartitionsResolutionInSeconds();
    }

    @Override
    public Model build(Object modelBuilderData) {
        Assert.isInstanceOf(ContextSmartValueData.class, modelBuilderData, MODEL_BUILDER_DATA_TYPE_ERROR_MSG);

        ContextSmartValueData contextSmartValueData = (ContextSmartValueData) modelBuilderData;
        Map<Instant, Double> startInstantToSmartValues = contextSmartValueData.getStartInstantToSmartValue();

        Map<Long, Double> startInstantToMaxSmartValue = new HashMap<>();
        startInstantToSmartValues.forEach((instant, smartValue) -> {
            Instant floorStartInstant = TimeService.floorTime(instant, partitionsResolutionInSeconds);

            Double curSmartValue = startInstantToMaxSmartValue.get(floorStartInstant.getEpochSecond());
            if(curSmartValue==null || smartValue > curSmartValue){
                startInstantToMaxSmartValue.put(floorStartInstant.getEpochSecond(), smartValue);
            }
        });

        Instant weightsModelEndTime = contextSmartValueData.getWeightsModelEndTime();
        long numOfPartitions = startInstantToMaxSmartValue.size();

        SMARTMaxValuesModel smartMaxValuesModel = new SMARTMaxValuesModel();
        smartMaxValuesModel.init(startInstantToMaxSmartValue, numOfPartitions, weightsModelEndTime);
        return smartMaxValuesModel;
    }

    protected Map<Double, Long> castModelBuilderData(GenericHistogram genericHistogram) {
        Map<Double, Long> map = new HashMap<>();

        genericHistogram.getHistogramMap().entrySet()
                .forEach(entry -> map.put(ConversionUtils.convertToDouble(entry.getKey()), entry.getValue().longValue()));
        return map;
    }


}
