package fortscale.streaming.feature.extractor;

import java.util.ArrayList;
import java.util.List;

import net.minidev.json.JSONObject;

import org.apache.commons.lang.StringUtils;

public class EventFeatureExtractor implements FeatureExtractor{
	private String originalFieldName;
	private String normalizedFieldName;
	private List<FeatureAdjustor> featureAdjustorPriorityList = new ArrayList<>();

	public EventFeatureExtractor(){}
	
	public EventFeatureExtractor(String originalFieldName, String normalizedFieldName, List<FeatureAdjustor> featureAdjustorPriorityList) {
		this.originalFieldName = originalFieldName;
		this.normalizedFieldName = normalizedFieldName;
		this.featureAdjustorPriorityList = featureAdjustorPriorityList;
	}

	@Override
	public Object extract(JSONObject message) {
		Object ret = extractValue(message);
		
		saveToMessage(message, ret);
		
		return ret;
	}
	
	protected Object extractValue(JSONObject message){
		Object ret = message.get(originalFieldName);
		for(FeatureAdjustor adjustor: featureAdjustorPriorityList){
			ret = adjustor.adjust(ret, message);
			if(ret instanceof String){
				if(!StringUtils.isBlank((String) ret)){
					break;
				}
			}else{
				if(ret != null){
					break;
				}
			}
		}
		
		return ret;
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

	public List<FeatureAdjustor> getFeatureAdjustorPriorityList() {
		return featureAdjustorPriorityList;
	}

	public void setFeatureAdjustorPriorityList(List<FeatureAdjustor> featureAdjustorPriorityList) {
		this.featureAdjustorPriorityList = featureAdjustorPriorityList;
	}

	@Override public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		EventFeatureExtractor that = (EventFeatureExtractor) o;

		if (normalizedFieldName != null ? !normalizedFieldName.equals(that.normalizedFieldName) : that.normalizedFieldName != null)
			return false;
		if (originalFieldName != null ? !originalFieldName.equals(that.originalFieldName) : that.originalFieldName != null)
			return false;
		if(!featureAdjustorPriorityList.equals(that.featureAdjustorPriorityList)){
			return false;
		}

		return true;
	}

	@Override public int hashCode() {
		return originalFieldName.hashCode();
	}
}
