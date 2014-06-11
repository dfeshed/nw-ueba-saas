package fortscale.streaming.model;

import static com.google.common.base.Preconditions.checkNotNull;

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
	
	public FieldModel getFieldModel(String field) {
		checkNotNull(field);
		return fields.get(field);
	}
	
	public boolean isTimeMarkAfter(long time) {
		// check if the given time is before all field marks
		// do so by going over all field models and see if we 
		// have at least one model whose mark is before the 
		// given time
		for (FieldModel model : fields.values()) {
			if (model.isTimeMarkAfter(time))
				return true;
		}
		// if no models were found with time mark after the given time,
		// consider the given time as after the marks if we have field models
		return (fields.size()!=0);
	}
	
	public long getHighTimeMark() {
		long highMark = 0L;
		for (FieldModel model : fields.values()) {
			highMark = Math.max(highMark, model.getTimeMark());
		}
		return highMark;
	}
}
