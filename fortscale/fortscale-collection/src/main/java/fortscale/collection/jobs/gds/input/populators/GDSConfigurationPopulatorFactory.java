package fortscale.collection.jobs.gds.input.populators;

import fortscale.collection.jobs.gds.configurators.GDSConfiguratorType;
import fortscale.collection.jobs.gds.input.populators.enrichment.*;

import java.util.EnumMap;

/**
 * Factory class of populators
 *
 * @author gils
 * 03/01/2016
 */
public class GDSConfigurationPopulatorFactory {

    private EnumMap<GDSConfiguratorType, GDSConfigurationPopulator> populatorsMap = new EnumMap<GDSConfiguratorType, GDSConfigurationPopulator>(GDSConfiguratorType.class);

    public GDSConfigurationPopulator getConfigurationPopulator(GDSConfiguratorType gdsConfiguratorType) {
        if (gdsConfiguratorType == GDSConfiguratorType.SCHEMA) {
            populatorsMap.putIfAbsent(gdsConfiguratorType, new GDSSchemaDefinitionCLIPopulator());
        }
        else if (gdsConfiguratorType == GDSConfiguratorType.USER_NORMALIZATION) {
            populatorsMap.putIfAbsent(gdsConfiguratorType, new GDSUserNormalizationCLIPopulator());
        }
        else if (gdsConfiguratorType == GDSConfiguratorType.IP_RESOLVING) {
            populatorsMap.putIfAbsent(gdsConfiguratorType, new GDSIPResolvingCLIPopulator());
        }
        else if (gdsConfiguratorType == GDSConfiguratorType.GEO_LOCATION) {
            populatorsMap.putIfAbsent(gdsConfiguratorType, new GDSGeoLocationCLIPopulator());
        }
        else if (gdsConfiguratorType == GDSConfiguratorType.COMPUTER_TAGGING) {
            populatorsMap.putIfAbsent(gdsConfiguratorType, new GDSComputerTaggingCLIPopulator());
        }
        else if (gdsConfiguratorType == GDSConfiguratorType.USER_MONGO_UPDATE) {
            populatorsMap.putIfAbsent(gdsConfiguratorType, new GDSUserMongoUpdateCLIPopulator());
        }
        else if (gdsConfiguratorType == GDSConfiguratorType.HDFS_WRITE) {
            populatorsMap.putIfAbsent(gdsConfiguratorType, new GDSHDFSWriteCLIPopulator());
        }
        else {
            throw new UnsupportedOperationException("Could not find configurator populator of type " + gdsConfiguratorType.name());
        }

        return populatorsMap.get(gdsConfiguratorType);
    }
}
