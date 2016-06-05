package fortscale.ml.model.builder;

import fortscale.common.util.GenericHistogram;
import fortscale.ml.model.Model;
import fortscale.ml.model.SMARTValuesModel;
import fortscale.utils.ConversionUtils;
import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.Map;

public class SMARTValuesModelBuilder implements IModelBuilder {
    private static final String MODEL_BUILDER_DATA_TYPE_ERROR_MSG = String.format(
            "Model builder data must be of type %s.", GenericHistogram.class.getSimpleName());

    @Override
    public Model build(Object modelBuilderData) {
        Map<Double, Long> smartValueToCountMap = castModelBuilderData(modelBuilderData);
        SMARTValuesModel smartValuesModel = new SMARTValuesModel();
        long numOfPositiveValues = smartValueToCountMap.entrySet().stream().filter(entry -> entry.getKey() != 0).mapToLong(Map.Entry::getValue).sum();
        double sumOfValues = smartValueToCountMap.entrySet().stream().filter(entry -> entry.getKey() != 0).mapToDouble(entry -> entry.getKey() * entry.getValue()).sum();
        smartValuesModel.init(smartValueToCountMap.getOrDefault(0D, 0L), numOfPositiveValues, sumOfValues);
        return smartValuesModel;
    }

    protected Map<Double, Long> castModelBuilderData(Object modelBuilderData) {
        Assert.isInstanceOf(GenericHistogram.class, modelBuilderData, MODEL_BUILDER_DATA_TYPE_ERROR_MSG);
        Map<Double, Long> map = new HashMap<>();
        ((GenericHistogram) modelBuilderData).getHistogramMap().entrySet()
                .forEach(entry -> map.put(ConversionUtils.convertToDouble(entry.getKey()), entry.getValue().longValue()));
        return map;
    }
}
