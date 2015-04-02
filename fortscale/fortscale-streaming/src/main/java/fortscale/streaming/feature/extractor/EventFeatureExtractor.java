package fortscale.streaming.feature.extractor;

import net.minidev.json.JSONObject;

import com.fasterxml.jackson.annotation.JsonTypeName;

import fortscale.utils.logging.Logger;




@JsonTypeName(EventFeatureExtractor.EVENT_FEATURE_EXTRACTOR_TYPE)
public class EventFeatureExtractor implements FeatureExtractor{
	private static final Logger logger = Logger.getLogger(EventFeatureExtractor.class);
	protected static final String EVENT_FEATURE_EXTRACTOR_TYPE = "event_feature_extractor";
	
	private String originalFieldName;
	private String normalizedFieldName;
	private FeatureAdjustor featureAdjustor;

	public EventFeatureExtractor(){}
	
	public EventFeatureExtractor(String originalFieldName, String normalizedFieldName, FeatureAdjustor featureAdjustor) {
		this.originalFieldName = originalFieldName;
		this.normalizedFieldName = normalizedFieldName;
		this.featureAdjustor = featureAdjustor;
	}

	@Override
	public Object extract(JSONObject message) {
		Object ret = null;
		try{
			ret = extractValue(message);
		} catch(Exception e){
			logger.debug("got the following exception while trying to extract feature", e);
		}
		
		saveToMessage(message, ret);
		
		return ret;
	}
	
	protected Object extractValue(JSONObject message){
		Object ret = message.get(originalFieldName);
		if(featureAdjustor!=null){
			return featureAdjustor.adjust(ret, message);
		} else{
			return ret;
		}
	}
	
	protected void saveToMessage(JSONObject message, Object val){
		if(normalizedFieldName != null){
			message.put(normalizedFieldName, val);
		}
	}

	public String getOriginalFieldName() {
		return originalFieldName;
	}

	public void setOriginalFieldName(String originalFieldName) {
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

		EventFeatureExtractor that = (EventFeatureExtractor) o;

		if (normalizedFieldName != null ? !normalizedFieldName.equals(that.normalizedFieldName) : that.normalizedFieldName != null)
			return false;
		if (originalFieldName != null ? !originalFieldName.equals(that.originalFieldName) : that.originalFieldName != null)
			return false;
		if(!featureAdjustor.equals(that.featureAdjustor)){
			return false;
		}

		return true;
	}

	@Override public int hashCode() {
		return originalFieldName.hashCode();
	}
}
