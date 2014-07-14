package fortscale.streaming.model.prevalance;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public final class PrevalanceModelBuilder {

	public static PrevalanceModelBuilder createModel(String name) {
		return new PrevalanceModelBuilder(name);
	}
	
	private String name;
	private Map<String, String> fields = new HashMap<String, String>();
	
	
	private PrevalanceModelBuilder(String name) {
		this.name = name;
	}
	
	public PrevalanceModelBuilder withField(String fieldName, String fieldModelClassName) {
		checkNotNull(fieldName);
		checkNotNull(fieldModelClassName);
		
		fields.put(fieldName, fieldModelClassName);

		// return this for fluent type usage
		return this;
	}
	
	public PrevalanceModel build() throws Exception {
		
		PrevalanceModel model = new PrevalanceModel(name);
		for (Entry<String, String> entry :  fields.entrySet()) {
			FieldModel fieldModel = (FieldModel)Class.forName(entry.getValue()).newInstance();
			model.setFieldModel(entry.getKey(), fieldModel);	
		}
		
		return model;
	}
	
	public String getModelName() {
		return name;
	}
}
