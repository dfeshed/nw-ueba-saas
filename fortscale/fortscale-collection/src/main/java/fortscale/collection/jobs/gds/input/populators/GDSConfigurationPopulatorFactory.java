package fortscale.collection.jobs.gds.input.populators;

import fortscale.collection.jobs.gds.GDSConfigurationException;
import fortscale.collection.jobs.gds.GDSConfigurationType;
import fortscale.collection.jobs.gds.input.populators.enrichment.*;

import java.util.EnumMap;

/**
 * Factory class of configuration populators
 *
 * @author gils
 * 03/01/2016
 */
public class GDSConfigurationPopulatorFactory {

    private EnumMap<GDSConfigurationType, Class<? extends GDSConfigurationPopulator>> configurationTypeToPopulatorMap = new EnumMap<>(GDSConfigurationType.class);

    public GDSConfigurationPopulatorFactory() {
        configurationTypeToPopulatorMap.put(GDSConfigurationType.SCHEMA, GDSSchemaDefinitionCLIPopulator.class);
        configurationTypeToPopulatorMap.put(GDSConfigurationType.USER_NORMALIZATION, GDSUserNormalizationCLIPopulator.class);
        configurationTypeToPopulatorMap.put(GDSConfigurationType.IP_RESOLVING, GDSIPResolvingCLIPopulator.class);
        configurationTypeToPopulatorMap.put(GDSConfigurationType.COMPUTER_TAGGING, GDSComputerTaggingCLIPopulator.class);
        configurationTypeToPopulatorMap.put(GDSConfigurationType.GEO_LOCATION, GDSGeoLocationCLIPopulator.class);
        configurationTypeToPopulatorMap.put(GDSConfigurationType.USER_MONGO_UPDATE, GDSUserMongoUpdateCLIPopulator.class);
        configurationTypeToPopulatorMap.put(GDSConfigurationType.HDFS_WRITER, GDSHDFSWriteCLIPopulator.class);
        configurationTypeToPopulatorMap.put(GDSConfigurationType.ENTITIES_PROPERTIES, GDSEntitiesPropertiesCLIPopulator.class);
    }

    private EnumMap<GDSConfigurationType, GDSConfigurationPopulator> populatorsMap = new EnumMap<>(GDSConfigurationType.class);

    public GDSConfigurationPopulator getConfigurationPopulator(GDSConfigurationType gdsConfigurationType) throws GDSConfigurationException {
        Class<? extends GDSConfigurationPopulator> gdsPopulatorClass = configurationTypeToPopulatorMap.get(gdsConfigurationType);
        if (gdsPopulatorClass == null) {
            throw new GDSConfigurationException("Could not find populator mapping for configuration type " + gdsConfigurationType.name());
        }

        try {
            populatorsMap.putIfAbsent(gdsConfigurationType,  gdsPopulatorClass.newInstance());
            return populatorsMap.get(gdsConfigurationType);
        } catch (InstantiationException | IllegalAccessException e) {
            throw new GDSConfigurationException("Could not create populator for configuration type " + gdsConfigurationType.name(), e);
        }
    }
}
