package fortscale.collection.jobs.gds.input.populators;

import fortscale.collection.jobs.gds.configurators.GDSConfiguratorType;
import fortscale.collection.jobs.gds.input.populators.enrichment.*;

/**
 * Factory class of populators
 *
 * @author gils
 * 03/01/2016
 */
public class GDSConfigurationPopulatorFactory {
    public GDSConfigurationPopulator getConfigurationPopulator(GDSConfiguratorType gdsConfiguratorType) {
        if (gdsConfiguratorType == GDSConfiguratorType.SCHEMA) {
            return new GDSSchemaDefinitionCLIPopulator();
        }
        else if (gdsConfiguratorType == GDSConfiguratorType.USER_NORMALIZATION) {
            return new GDSUserNormalizationCLIPopulator();
        }
        else if (gdsConfiguratorType == GDSConfiguratorType.IP_RESOLVING) {
            return new GDSIPResolvingCLIPopulator();
        }
        else if (gdsConfiguratorType == GDSConfiguratorType.GEO_LOCATION) {
            return new GDSGeoLocationCLIPopulator();
        }
        else if (gdsConfiguratorType == GDSConfiguratorType.COMPUTER_TAGGING) {
            return new GDSComputerTaggingCLIPopulator();
        }
        else if (gdsConfiguratorType == GDSConfiguratorType.USER_MONGO_UPDATE) {
            return new GDSUserMongoUpdateCLIPopulator();
        }
        else if (gdsConfiguratorType == GDSConfiguratorType.HDFS_WRITE) {
            return new GDSHDFSWriteCLIPopulator();
        }

        throw new UnsupportedOperationException("Could not find configurator populator of type " + gdsConfiguratorType.name());
    }
}
