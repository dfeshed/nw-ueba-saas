package fortscale.ml.model.prevalance;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.samza.config.Config;

public class PrevalanceModelBuilderImpl implements PrevalanceModelBuilder{

	public static PrevalanceModelBuilderImpl createModel(String name, Config config, String configPrefix) {
		return new PrevalanceModelBuilderImpl(name, config, configPrefix);
	}
	
	private String name;
	private Config config;
	String configPrefix;
	private Map<String, String> fields = new HashMap<String, String>();
	
	private PrevalanceModelBuilderImpl(String name, Config config, String configPrefix) {
		this.name = name;
		this.config = config;
		this.configPrefix = configPrefix;
	}
	
	@Override
	public PrevalanceModelBuilderImpl withField(String fieldName, String fieldModelClassName) {
		checkNotNull(fieldName);
		checkNotNull(fieldModelClassName);
		
		fields.put(fieldName, fieldModelClassName);
		
		// return this for fluent type usage
		return this;
	}
	
	@Override
	public PrevalanceModel build() throws Exception {
		
		PrevalanceModel model = new PrevalanceModel(name);
		for (Entry<String, String> entry :  fields.entrySet()) {
			String fieldName = entry.getKey();
			
			// Construct a field model
			FieldModel fieldModel = (FieldModel)Class.forName(entry.getValue()).newInstance();
			fieldModel.init(configPrefix, fieldName, config);
			
			model.setFieldModel(fieldName, fieldModel);
		}
		
		return model;
	}
	
	@Override
	public String getModelName() {
		return name;
	}
}
