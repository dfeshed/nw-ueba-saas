package fortscale.collection.jobs.gds.configurators;

import fortscale.collection.jobs.gds.GDSConfigurationException;
import fortscale.collection.jobs.gds.GDSConfigurationType;

import java.util.EnumMap;

/**
 * Configurator's factory class
 *
 * @author gils
 * 03/01/2016
 */
public class GDSConfiguratorFactory {

    private EnumMap<GDSConfigurationType, Class<? extends GDSConfigurator>> configurationTypeToConfiguratorMap = new EnumMap<>(GDSConfigurationType.class);

    public GDSConfiguratorFactory() {
        configurationTypeToConfiguratorMap.put(GDSConfigurationType.SCHEMA, GDSSchemaConfigurator.class);
        configurationTypeToConfiguratorMap.put(GDSConfigurationType.COLLECTION, GDSCollectionConfigurator.class);
        configurationTypeToConfiguratorMap.put(GDSConfigurationType.USER_NORMALIZATION, GDSUserNormalizationConfigurator.class);
        configurationTypeToConfiguratorMap.put(GDSConfigurationType.IP_RESOLVING, GDSIPResolvingConfigurator.class);
        configurationTypeToConfiguratorMap.put(GDSConfigurationType.COMPUTER_TAGGING, GDSComputerTaggingConfigurator.class);
        configurationTypeToConfiguratorMap.put(GDSConfigurationType.GEO_LOCATION, GDSGeoLocationConfigurator.class);
        configurationTypeToConfiguratorMap.put(GDSConfigurationType.USER_MONGO_UPDATE, GDSUserMongoUpdateConfigurator.class);
        configurationTypeToConfiguratorMap.put(GDSConfigurationType.HDFS_WRITER, GDSHDFSWriterConfigurator.class);
        configurationTypeToConfiguratorMap.put(GDSConfigurationType.ENTITIES_PROPERTIES, GDSEntitiesPropertiesConfigurator.class);
    }

    private EnumMap<GDSConfigurationType, GDSConfigurator> configuratorsMap =  new EnumMap<>(GDSConfigurationType.class);

    public GDSConfigurator getConfigurator(GDSConfigurationType gdsConfigurationType) throws GDSConfigurationException {
        Class<? extends GDSConfigurator> gdsConfiguratorClass = configurationTypeToConfiguratorMap.get(gdsConfigurationType);
        if (gdsConfiguratorClass == null) {
            throw new GDSConfigurationException("Could not find configurator mapping for configuration type " + gdsConfigurationType.name());
        }

        try {
            configuratorsMap.putIfAbsent(gdsConfigurationType, gdsConfiguratorClass.newInstance());
            return configuratorsMap.get(gdsConfigurationType);
        } catch (InstantiationException | IllegalAccessException e) {
            throw new GDSConfigurationException("Could not create configurator for configuration type " + gdsConfigurationType.name(), e);
        }
    }
}

