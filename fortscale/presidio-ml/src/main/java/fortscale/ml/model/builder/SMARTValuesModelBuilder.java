package fortscale.ml.model.builder;

import fortscale.common.util.GenericHistogram;
import fortscale.ml.model.Model;
import fortscale.ml.model.SMARTValuesModel;
import fortscale.ml.model.retriever.smart_data.SmartValueData;
import fortscale.utils.ConversionUtils;
import org.springframework.util.Assert;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

public class SMARTValuesModelBuilder implements IModelBuilder {
    private static final String MODEL_BUILDER_DATA_TYPE_ERROR_MSG = String.format(
            "Model builder data must be of type %s.", SmartValueData.class.getSimpleName());

    @Override
    public Model build(Object modelBuilderData) {
        Assert.isInstanceOf(SmartValueData.class, modelBuilderData, MODEL_BUILDER_DATA_TYPE_ERROR_MSG);
        SmartValueData smartValueData = (SmartValueData) modelBuilderData;
        Instant weightsModelEndTime = smartValueData.getWeightsModelEndTime();
        GenericHistogram genericHistogram = smartValueData.getGenericHistogram();
        Map<Double, Long> smartValueToCountMap = castModelBuilderData(genericHistogram);

        SMARTValuesModel smartValuesModel = new SMARTValuesModel();
        long numOfPositiveValues = smartValueToCountMap.entrySet().stream().filter(entry -> entry.getKey() != 0).mapToLong(Map.Entry::getValue).sum();
        double sumOfValues = smartValueToCountMap.entrySet().stream().filter(entry -> entry.getKey() != 0).mapToDouble(entry -> entry.getKey() * entry.getValue()).sum();
        smartValuesModel.init(smartValueToCountMap.getOrDefault(0D, 0L), numOfPositiveValues, sumOfValues,genericHistogram.getNumberOfPartitions(), weightsModelEndTime);
        return smartValuesModel;
    }

    protected Map<Double, Long> castModelBuilderData(GenericHistogram genericHistogram) {
        Map<Double, Long> map = new HashMap<>();

        genericHistogram.getHistogramMap().entrySet()
                .forEach(entry -> map.put(ConversionUtils.convertToDouble(entry.getKey()), entry.getValue().longValue()));
        return map;
    }
}
