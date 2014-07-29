package fortscale.streaming.model.prevalance;

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
	private UserTimeBarrier barrier;
	
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
	
	public UserTimeBarrier getBarrier() {
		if (barrier==null)
			barrier = new UserTimeBarrier();
		return barrier;
	}
	
	public void addFieldValue(String fieldName, Object value, long timestamp) {
		checkNotNull(fieldName);
		if (value==null || !fields.containsKey(fieldName))
			return; 
		
		// add the value to the field model
		FieldModel model = fields.get(fieldName);
		model.add(value, convertToMilliSeconds(timestamp));
	}
	
	public double calculateScore(String fieldName, Object value){
		checkNotNull(fieldName);
		if (value==null || !fields.containsKey(fieldName))
			return 0 ; 
		
		FieldModel model = fields.get(fieldName);
		return model.calculateScore(value);
	}
	
	public boolean shouldAffectEventScore(String fieldName) {
		checkNotNull(fieldName);
		FieldModel model = fields.get(fieldName);
		return (model!=null && model.shouldAffectEventScore());
	}
	
}
