package fortscale.streaming.feature.extractor;

import net.minidev.json.JSONObject;

public class MessageFeatureExtractor implements FeatureExtractor{
	protected String originalFieldName;
	protected String normalizedFieldName;

	public MessageFeatureExtractor(){}
	
	public MessageFeatureExtractor(String originalFieldName, String normalizedFieldName) {
		this.originalFieldName = originalFieldName;
		this.normalizedFieldName = normalizedFieldName;
	}

	@Override
	public Object extract(JSONObject message) {
		Object ret = message.get(originalFieldName);
		
		saveToMessage(message, ret);
		
		return ret;
	}
	
	protected Object extractValue(JSONObject message){
		return message.get(originalFieldName);
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

	@Override public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		MessageFeatureExtractor that = (MessageFeatureExtractor) o;

		if (normalizedFieldName != null ? !normalizedFieldName.equals(that.normalizedFieldName) : that.normalizedFieldName != null)
			return false;
		if (originalFieldName != null ? !originalFieldName.equals(that.originalFieldName) : that.originalFieldName != null)
			return false;

		return true;
	}

	@Override public int hashCode() {
		return originalFieldName.hashCode();
	}
}
