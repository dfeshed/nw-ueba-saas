package fortscale.ml.model.prevalance;

import static com.google.common.base.Preconditions.checkNotNull;
import static fortscale.utils.TimestampUtils.convertToMilliSeconds;

import java.util.HashMap;
import java.util.Map;

import net.minidev.json.JSONObject;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;

import fortscale.ml.feature.extractor.IFeatureExtractionService;


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
	
	public FieldModel getFieldModel(String field){
		return fields.get(field);
	}
	
	public UserTimeBarrier getBarrier() {
		if (barrier==null)
			barrier = new UserTimeBarrier();
		return barrier;
	}
	
	public void addFieldValues(IFeatureExtractionService featureExtractionService, JSONObject message, long timestamp) {		
		// add the fields to the model if passed the skip event check
		for (String fieldName : getFieldNames()) {
			Object value = featureExtractionService.extract(fieldName, message);
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
	
	public double calculateScore(IFeatureExtractionService featureExtractionService, JSONObject message, String fieldName){
		checkNotNull(fieldName);
		if (message==null || !fields.containsKey(fieldName))
			return 0 ; 
				
		FieldModel model = fields.get(fieldName);
		Object fieldValue = featureExtractionService.extract(fieldName, message);
		return model.calculateScore(fieldValue);
	}		
}
