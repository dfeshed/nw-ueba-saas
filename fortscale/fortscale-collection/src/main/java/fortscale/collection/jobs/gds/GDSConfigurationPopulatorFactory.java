package fortscale.collection.jobs.gds;

import fortscale.collection.jobs.gds.configurators.GDSConfigurationType;
import fortscale.collection.jobs.gds.populators.GDSSchemaDefinitionCLIPopulator;
import fortscale.collection.jobs.gds.populators.enrichment.*;

/**
 * @author gils
 * 03/01/2016
 */
public class GDSConfigurationPopulatorFactory {
    public GDSConfigurationPopulator getConfigurationPopulator(GDSConfigurationType gdsConfigurationType) {
        if (gdsConfigurationType == GDSConfigurationType.SCHEMA) {
            return new GDSSchemaDefinitionCLIPopulator();
        }
        else if (gdsConfigurationType == GDSConfigurationType.USER_NORMALIZATION) {
            return new GDSUserNormalizationCLIPopulator();
        }
        else if (gdsConfigurationType == GDSConfigurationType.IP_RESOLVING) {
            return new GDSIPResolvingCLIPopulator();
        }
        else if (gdsConfigurationType == GDSConfigurationType.GEO_LOCATION) {
            return new GDSGeoLocationCLIPopulator();
        }
        else if (gdsConfigurationType == GDSConfigurationType.COMPUTER_TAGGING) {
            return new GDSComputerTaggingCLIPopulator();
        }
        else if (gdsConfigurationType == GDSConfigurationType.USER_MONGO_UPDATE) {
            return new GDSUserMongoUpdateCLIPopulator();
        }
        else if (gdsConfigurationType == GDSConfigurationType.HDFS_WRITE) {
            return new GDSHDFSWriteCLIPopulator();
        }

        throw new UnsupportedOperationException("Could not find configurator populator of type " + gdsConfigurationType.name());
    }
}
