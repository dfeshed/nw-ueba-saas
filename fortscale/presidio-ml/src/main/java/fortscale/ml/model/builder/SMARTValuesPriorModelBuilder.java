package fortscale.ml.model.builder;

import fortscale.common.util.GenericHistogram;
import fortscale.ml.model.Model;
import fortscale.ml.model.SMARTValuesPriorModel;
import fortscale.ml.model.retriever.smart_data.SmartValueData;
import fortscale.utils.ConversionUtils;
import org.springframework.util.Assert;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SMARTValuesPriorModelBuilder implements IModelBuilder {
    private static final String MODEL_BUILDER_DATA_TYPE_ERROR_MSG = String.format(
            "Model builder data must be of type %s.", SmartValueData.class.getSimpleName());

    private SMARTValuesPriorModelBuilderConf conf;

    public SMARTValuesPriorModelBuilder(SMARTValuesPriorModelBuilderConf conf) {
        Assert.notNull(conf);
        this.conf = conf;
    }

    @Override
    public Model build(Object modelBuilderData) {

		Assert.isInstanceOf(SmartValueData.class, modelBuilderData, MODEL_BUILDER_DATA_TYPE_ERROR_MSG);
		SmartValueData smartValueData = (SmartValueData) modelBuilderData;
		GenericHistogram genericHistogram = smartValueData.getGenericHistogram();
		Instant weightsModelEndTime = smartValueData.getWeightsModelEndTime();

		Map<Double, Long> smartValueToCountMap = castModelBuilderData(genericHistogram);
		smartValueToCountMap.remove(0.0);
		SMARTValuesPriorModel res = new SMARTValuesPriorModel();

		res.setWeightsModelEndTime(weightsModelEndTime);

		long numOfPartitions = genericHistogram.getNumberOfPartitions();
		res.setNumOfPartitions(numOfPartitions);

		if (smartValueToCountMap.isEmpty()) {
			return res.init(0);
		}

		long totalCount = smartValueToCountMap.values().stream().mapToLong(valueCount -> valueCount).sum();
		long indexOfQuantile = (long) Math.ceil((totalCount - 1) * (1 - conf.getQuantile()));
		long cumsum = 0;
		List<Map.Entry<Double, Long>> entries = smartValueToCountMap.entrySet().stream()
				.sorted((valueToCount1, valueToCount2) -> Double.compare(valueToCount2.getKey(), valueToCount1.getKey()))
				.collect(Collectors.toList());
		for (Map.Entry<Double, Long> valueToCount : entries) {
			long updatedCumsum = cumsum + valueToCount.getValue();
			if (cumsum >= indexOfQuantile || updatedCumsum == totalCount) {
				res.init(valueToCount.getKey());
				break;
			}
			cumsum = updatedCumsum;
		}
		return res;
    }

    protected Map<Double, Long> castModelBuilderData(GenericHistogram genericHistogram) {
        Map<Double, Long> map = new HashMap<>();

		genericHistogram.getHistogramMap().entrySet()
                .forEach(entry -> map.put(ConversionUtils.convertToDouble(entry.getKey()), entry.getValue().longValue()));
        return map;
    }

}
