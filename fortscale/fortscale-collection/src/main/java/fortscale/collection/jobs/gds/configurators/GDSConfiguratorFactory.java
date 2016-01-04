package fortscale.collection.jobs.gds.configurators;

import fortscale.collection.jobs.gds.GDSConfigurationException;
import fortscale.collection.jobs.gds.GDSConfigurator;

import java.util.EnumMap;

/**
 * @author gils
 * 03/01/2016
 */
public class GDSConfiguratorFactory {
    private EnumMap<GDSConfiguratorType, GDSConfigurator> configuratorsMap =
            new EnumMap<>(GDSConfiguratorType.class);

    public GDSConfigurator getConfigurator(GDSConfiguratorType gdsConfiguratorType) throws GDSConfigurationException {
        Class<? extends GDSConfigurator> gdsConfiguratorClass = gdsConfiguratorType.getGDSConfiguratorClass();
        if (configuratorsMap.containsKey(gdsConfiguratorType)) {
            return configuratorsMap.get(gdsConfiguratorType);
        }
        else {
            try {
                GDSConfigurator gdsConfigurator = gdsConfiguratorClass.newInstance();
                configuratorsMap.put(gdsConfiguratorType, gdsConfigurator);
                return gdsConfigurator;
            } catch (InstantiationException | IllegalAccessException e) {
                throw new GDSConfigurationException("Could not create configurator for type " + gdsConfiguratorType.name(), e);
            }

        }
    }
}
