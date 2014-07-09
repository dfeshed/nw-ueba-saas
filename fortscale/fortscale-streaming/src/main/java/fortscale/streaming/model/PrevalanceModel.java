package fortscale.streaming.model;

import static com.google.common.base.Preconditions.checkNotNull;
import static fortscale.utils.TimestampUtils.convertToMilliSeconds;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;


@JsonAutoDetect(fieldVisibility=Visibility.ANY, getterVisibility=Visibility.NONE, setterVisibility=Visibility.NONE)
public class PrevalanceModel {
	
	private Map<String, FieldModel> fields = new HashMap<String, FieldModel>();
	private String modelName;
	private long timeMark;
	
	@JsonCreator
	public PrevalanceModel(@JsonProperty("modelName") String modelName) {
		this.modelName = modelName;
	}
	
	public String getModelName() {
		return modelName;
	}
	
	public Iterable<String> getFieldNames() {
		return fields.keySet();
	}
	
	public void setFieldModel(String field, FieldModel model) {
		checkNotNull(field);
		fields.put(field, model);
	}
	
	public long getTimeMark() {
		return timeMark;
	}
	
	public void addFieldValue(String fieldName, Object value, long timestamp) {
		checkNotNull(fieldName);
		if (value==null || !fields.containsKey(fieldName))
			return; 
		
		// check that the value time stamp is not before the time mark
		long millis = convertToMilliSeconds(timestamp);
		if (isBeforeTimeMark(timestamp))
			return;
		
		// add the value to the field model
		FieldModel model = fields.get(fieldName);
		model.add(value, millis);
		
		// update the time mark
		timeMark = Math.max(timeMark, millis);
	}
	
	public double calculateScore(String fieldName, Object value){
		checkNotNull(fieldName);
		if (value==null || !fields.containsKey(fieldName))
			return 0 ; 
		
		FieldModel model = fields.get(fieldName);
		return model.calculateScore(value);
	}
	
	public boolean isAfterTimeMark(long timestamp) {
		// check that the value time stamp is after the time mark
		return (timeMark < convertToMilliSeconds(timestamp));
	}
	
	public boolean isBeforeTimeMark(long timestamp) {
		// check that the value time stamp is before the time mark
		return (convertToMilliSeconds(timestamp) < timeMark);
	}
	
}
