package fortscale.ml.model.builder;

import fortscale.common.util.GenericHistogram;
import fortscale.ml.model.Model;
import fortscale.ml.model.SMARTValuesPriorModel;
import fortscale.utils.ConversionUtils;
import org.springframework.util.Assert;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;

public class SMARTValuesPriorModelBuilder implements IModelBuilder {
    private static final String MODEL_BUILDER_DATA_TYPE_ERROR_MSG = String.format(
            "Model builder data must be of type %s.", GenericHistogram.class.getSimpleName());

    private SMARTValuesPriorModelBuilderConf conf;

    public SMARTValuesPriorModelBuilder(SMARTValuesPriorModelBuilderConf conf) {
        Assert.notNull(conf);
        this.conf = conf;
    }

    @Override
    public Model build(Object modelBuilderData) {
        Map<Double, Long> smartValueToCountMap = castModelBuilderData(modelBuilderData);
		smartValueToCountMap.remove(0.0);
        long totalCount = smartValueToCountMap.values().stream().mapToLong(valueCount -> valueCount).sum();
        long indexOfQuantile = (long) Math.ceil((totalCount - 1) * (1 - conf.getQuantile()));
        Double prior = smartValueToCountMap.entrySet().stream()
                .sorted((valueToCount1, valueToCount2) -> Double.compare(valueToCount2.getKey(), valueToCount1.getKey()))
                .reduce(new AbstractMap.SimpleImmutableEntry<>(null, 0L), (resValueAndCumsum, valueToCount) -> {
					if (resValueAndCumsum.getKey() != null) {
                        // we've already found the quantile, so just skip
						return resValueAndCumsum;
					}
					long updatedCumsum = resValueAndCumsum.getValue() + valueToCount.getValue();
					Double resValue = null;
					if (resValueAndCumsum.getValue() >= indexOfQuantile || updatedCumsum == totalCount) {
						resValue = valueToCount.getKey();
					}
					return new AbstractMap.SimpleImmutableEntry<>(resValue, updatedCumsum);
				})
                .getKey();
        return new SMARTValuesPriorModel().init(prior == null ? 0 : prior);
    }

    protected Map<Double, Long> castModelBuilderData(Object modelBuilderData) {
        Assert.isInstanceOf(GenericHistogram.class, modelBuilderData, MODEL_BUILDER_DATA_TYPE_ERROR_MSG);
        Map<Double, Long> map = new HashMap<>();
        ((GenericHistogram) modelBuilderData).getHistogramMap().entrySet()
                .forEach(entry -> map.put(ConversionUtils.convertToDouble(entry.getKey()), entry.getValue().longValue()));
        return map;
    }
}
