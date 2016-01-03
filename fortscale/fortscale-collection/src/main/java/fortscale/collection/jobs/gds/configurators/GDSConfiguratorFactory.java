package fortscale.collection.jobs.gds.configurators;

import fortscale.collection.jobs.gds.GDSConfigurationException;
import fortscale.collection.jobs.gds.GDSConfigurator;

import java.util.EnumMap;

/**
 * @author gils
 * 03/01/2016
 */
public class GDSConfiguratorFactory {
    private EnumMap<GDSConfigurationType, GDSConfigurator> configuratorsMap =
            new EnumMap<>(GDSConfigurationType.class);

    public GDSConfigurator getConfigurator(GDSConfigurationType gdsConfigurationType) throws GDSConfigurationException {
        Class<? extends GDSConfigurator> gdsConfiguratorClass = gdsConfigurationType.getGDSConfiguratorClass();
        if (configuratorsMap.containsKey(gdsConfigurationType)) {
            return configuratorsMap.get(gdsConfigurationType);
        }
        else {
            try {
                GDSConfigurator gdsConfigurator = gdsConfiguratorClass.newInstance();
                configuratorsMap.put(gdsConfigurationType, gdsConfigurator);
                return gdsConfigurator;
            } catch (InstantiationException | IllegalAccessException e) {
                throw new GDSConfigurationException("Could not create configurator for type " + gdsConfigurationType.name(), e);
            }

        }
    }
}
