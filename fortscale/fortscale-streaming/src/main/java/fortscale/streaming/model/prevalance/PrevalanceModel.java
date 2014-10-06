package fortscale.streaming.model.prevalance;

import static com.google.common.base.Preconditions.checkNotNull;
import static fortscale.utils.TimestampUtils.convertToMilliSeconds;

import java.util.HashMap;
import java.util.Map;

import net.minidev.json.JSONObject;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;


@JsonAutoDetect(fieldVisibility=Visibility.ANY, getterVisibility=Visibility.NONE, setterVisibility=Visibility.NONE)
public class PrevalanceModel {
	
	private Map<String, FieldModel> fields = new HashMap<String, FieldModel>();
	private Map<String, FieldScoreBooster> fieldBooster = new HashMap<String, FieldScoreBooster>();
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
	
	public void setFieldModel(String field, FieldModel model, FieldScoreBooster booster) {
		checkNotNull(field);
		fields.put(field, model);
		if (booster!=null)
			fieldBooster.put(field, booster);
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
	
	public void addFieldValues(JSONObject message, long timestamp) {
		if (shouldSkipEventScore(message))
			return;
		
		// add the fields to the model if passed the skip event check
		for (String fieldName : getFieldNames()) {
			Object value = message.get(fieldName);
			addFieldValue(fieldName, value, timestamp);
		}
	}
	
	private void addFieldValue(String fieldName, Object value, long timestamp) {
		checkNotNull(fieldName);
		if (value==null || !fields.containsKey(fieldName))
			return; 
		
		// add the value to the field model
		FieldModel model = fields.get(fieldName);
		model.add(value, convertToMilliSeconds(timestamp));
	}
	
	public double calculateScore(JSONObject message, String fieldName){
		checkNotNull(fieldName);
		if (message==null || !fields.containsKey(fieldName))
			return 0 ; 
		
		if (shouldSkipEventScore(message))
			return 0;
		
		FieldModel model = fields.get(fieldName);
		Object fieldValue = message.get(fieldName);
		double score = model.calculateScore(fieldValue);

		// adjust score if required
		if (fieldBooster.containsKey(fieldName))
			score = fieldBooster.get(fieldName).adjustScore(fieldValue, score);
			
		return score;
	}
	
	private boolean shouldSkipEventScore(JSONObject message) {
		// go over the fields and check that they non should force
		// the event to be skipped
		for (String fieldName : getFieldNames()) {
			FieldModel model = fields.get(fieldName);
			if (model.shouldSkipEvent(message.get(fieldName)))
				return true;
		}
		return false;
	}
	
	public boolean shouldAffectEventScore(String fieldName) {
		checkNotNull(fieldName);
		FieldModel model = fields.get(fieldName);
		return (model!=null && model.shouldAffectEventScore());
	}
	
}
