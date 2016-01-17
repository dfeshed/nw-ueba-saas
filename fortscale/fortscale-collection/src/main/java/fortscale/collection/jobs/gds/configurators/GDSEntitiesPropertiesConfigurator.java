package fortscale.collection.jobs.gds.configurators;

import fortscale.collection.jobs.gds.GDSConfigurationType;
import fortscale.services.configuration.ConfigurationParam;

import java.util.Map;

/**
 * Created by galiar on 17/01/2016.
 */
public class GDSEntitiesPropertiesConfigurator extends GDSBaseConfigurator {
	@Override
	public void configure(Map<String, Map<String, ConfigurationParam>> configurationParams) throws Exception {
		//TODO implement
	}

	@Override
	public void reset() throws Exception {
 		//TODO implement
	}

	@Override
	public GDSConfigurationType getType() {
		return GDSConfigurationType.ENTITIES_PROPERTIES;
	}
}
