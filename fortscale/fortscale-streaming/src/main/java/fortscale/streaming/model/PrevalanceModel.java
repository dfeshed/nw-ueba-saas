package fortscale.streaming.model;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.HashMap;
import java.util.Map;


public class PrevalanceModel {
	
	private Map<String, FieldModel> fields = new HashMap<String, FieldModel>();  
	private String modelName;
	
	public PrevalanceModel(String modelName) {
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
	
	public FieldModel forField(String field) {
		checkNotNull(field);
		return fields.get(field);
	}
}
