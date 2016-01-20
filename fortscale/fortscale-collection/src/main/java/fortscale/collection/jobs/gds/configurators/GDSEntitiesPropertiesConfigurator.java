package fortscale.collection.jobs.gds.configurators;

import fortscale.collection.jobs.gds.GDSConfigurationType;
import fortscale.services.configuration.ConfigurationParam;
import fortscale.services.configuration.Impl.EntitiesPropertiesConfigurationWriter;

import java.util.Map;

/**
 * sets the data from the populator into state.
 * this is a stub class, other configurators do more.
 * Created by galiar on 17/01/2016.
 */
public class GDSEntitiesPropertiesConfigurator extends GDSBaseConfigurator {

	public GDSEntitiesPropertiesConfigurator() {
		configurationWriterService = new EntitiesPropertiesConfigurationWriter();
	}

	@Override
	public void configure(Map<String, Map<String, ConfigurationParam>> configurationParams) throws Exception {
		currGDSConfigurationState.getEntitiesPropertiesState().setConfigurationParams(configurationParams);
	}

	@Override
	public void reset() throws Exception {
	}

	@Override
	public GDSConfigurationType getType() {
		return GDSConfigurationType.ENTITIES_PROPERTIES;
	}
}
