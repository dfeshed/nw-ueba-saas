package fortscale.streaming.model.prevalance;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.samza.config.Config;

public final class PrevalanceModelBuilder {

	public static PrevalanceModelBuilder createModel(String name, Config config) {
		return new PrevalanceModelBuilder(name, config);
	}
	
	private String name;
	private Config config;
	private Map<String, String> fields = new HashMap<String, String>();
	
	
	private PrevalanceModelBuilder(String name, Config config) {
		this.name = name;
		this.config = config;
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
			fieldModel.init(entry.getKey(), config);
			model.setFieldModel(entry.getKey(), fieldModel);	
		}
		
		return model;
	}
	
	public String getModelName() {
		return name;
	}
}
