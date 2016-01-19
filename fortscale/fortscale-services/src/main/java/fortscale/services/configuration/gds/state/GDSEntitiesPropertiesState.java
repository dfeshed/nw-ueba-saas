package fortscale.services.configuration.gds.state;

import fortscale.services.configuration.ConfigurationParam;

import java.util.Map;

/**
 * holds the state given by the user in the populator.
 * the state is kept in memory until the user apply (writes to file) or reset.
 * Created by galiar on 19/01/2016.
 */
public class GDSEntitiesPropertiesState extends GDSStreamingTaskState {

	public static final String TABLE_CONFIG_KEY = "tableConfigs";
	public static final String DECLARED_FIELDS_KEY = "declaredFields";
	public static final String HIDDEN_FIELDS = "hiddenFields";
	public static final String FIELD_PREFIX = "Field_";

	// configuration params contains tableConfigs, declared fields, hidden fields and (maybe) additional blocks of additional fields
	Map<String, Map<String, ConfigurationParam>> configurationParams;

	public Map<String, Map<String, ConfigurationParam>> getConfigurationParams() {
		return configurationParams;
	}

	public void setConfigurationParams(Map<String, Map<String, ConfigurationParam>> configurationParams) {
		this.configurationParams = configurationParams;
	}


}
