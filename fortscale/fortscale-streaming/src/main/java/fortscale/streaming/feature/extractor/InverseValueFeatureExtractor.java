package fortscale.streaming.feature.extractor;

import static fortscale.utils.ConversionUtils.convertToDouble;
import net.minidev.json.JSONObject;

public class InverseValueFeatureExtractor extends MessageFeatureExtractor{

	@Override
	protected Object extractValue(JSONObject message) {
		Double originalFieldValue = convertToDouble(message.get(originalFieldName));
		Double invOriginalFieldValue = (originalFieldValue==null || originalFieldValue==0)? null : 1.0/(originalFieldValue+1.0);
		
		return invOriginalFieldValue;
	}
}
