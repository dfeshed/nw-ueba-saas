package fortscale.streaming.feature.extractor;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;

import fortscale.utils.logging.Logger;
import net.minidev.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.springframework.util.Assert;

@JsonTypeName(EventFeatureExtractor.EVENT_FEATURE_EXTRACTOR_TYPE)
@JsonAutoDetect(fieldVisibility = Visibility.ANY, getterVisibility = Visibility.NONE, setterVisibility = Visibility.NONE)
public class EventFeatureExtractor implements FeatureExtractor{
	private static final Logger logger = Logger.getLogger(EventFeatureExtractor.class);
	protected static final String EVENT_FEATURE_EXTRACTOR_TYPE = "event_feature_extractor";

	private String originalFieldName;
	private String normalizedFieldName;
	private FeatureAdjustor featureAdjustor;

	public EventFeatureExtractor(
			@JsonProperty("originalFieldName") String originalFieldName,
			@JsonProperty("normalizedFieldName") String normalizedFieldName,
			@JsonProperty("featureAdjustor") FeatureAdjustor featureAdjustor) {
		setOriginalFieldName(originalFieldName);
		setNormalizedFieldName(normalizedFieldName);
		setFeatureAdjustor(featureAdjustor);
	}

	@Override
	public Object extract(JSONObject message) {
		Object ret = null;

		try {
			ret = extractValue(message);
		} catch (Exception e) {
			logger.debug("got the following exception while trying to extract feature", e);
		}

		saveToMessage(message, ret);
		return ret;
	}

	protected Object extractValue(JSONObject message) {
		Object ret = extractValueFromJson(message);

		if (featureAdjustor != null) {
			return featureAdjustor.adjust(ret, message);
		} else {
			return ret;
		}
	}
	
	private Object extractValueFromJson(JSONObject message){
		String featurePathElems[] = originalFieldName.split("\\.");
		JSONObject jsonObject = message;
		try{
			for(int i = 0; i<featurePathElems.length-1; i++){
				jsonObject = (JSONObject) jsonObject.get(featurePathElems[i]);
			}
		} catch(Exception e){
			return null;
		}
		
		if(jsonObject == null){
			return null;
		}
		
		return jsonObject.get(featurePathElems[featurePathElems.length-1]);
	}

	protected void saveToMessage(JSONObject message, Object val) {
		if (normalizedFieldName != null) {
			message.put(normalizedFieldName, val);
		}
	}

	public String getOriginalFieldName() {
		return originalFieldName;
	}

	public void setOriginalFieldName(String originalFieldName) {
		Assert.isTrue(StringUtils.isNotBlank(originalFieldName), "Illegal blank field name");
		this.originalFieldName = originalFieldName;
	}

	public String getNormalizedFieldName() {
		return normalizedFieldName;
	}

	public void setNormalizedFieldName(String normalizedFieldName) {
		this.normalizedFieldName = normalizedFieldName;
	}

	public FeatureAdjustor getFeatureAdjustor() {
		return featureAdjustor;
	}

	public void setFeatureAdjustor(FeatureAdjustor featureAdjustor) {
		this.featureAdjustor = featureAdjustor;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		EventFeatureExtractor that = (EventFeatureExtractor)o;
		if (normalizedFieldName != null ? !normalizedFieldName.equals(that.normalizedFieldName) : that.normalizedFieldName != null)
			return false;
		if (originalFieldName != null ? !originalFieldName.equals(that.originalFieldName) : that.originalFieldName != null)
			return false;
		if (!featureAdjustor.equals(that.featureAdjustor)) {
			return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		return originalFieldName.hashCode();
	}
}
