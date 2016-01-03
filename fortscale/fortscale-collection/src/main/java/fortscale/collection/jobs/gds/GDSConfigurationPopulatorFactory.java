package fortscale.collection.jobs.gds;

import fortscale.collection.jobs.gds.configurators.GDSConfigurationType;
import fortscale.collection.jobs.gds.populators.GDSConfigurationPopulator;
import fortscale.collection.jobs.gds.populators.GDSEnrichmentCLIPopulator;
import fortscale.collection.jobs.gds.populators.GDSSchemaDefinitionCLIPopulator;

/**
 * @author gils
 * 03/01/2016
 */
public class GDSConfigurationPopulatorFactory {
    public GDSConfigurationPopulator getConfigurationPopulator(GDSConfigurationType gdsConfigurationType) {
        if (gdsConfigurationType == GDSConfigurationType.SCHEMA) {
            return new GDSSchemaDefinitionCLIPopulator();
        }
        else if (gdsConfigurationType == GDSConfigurationType.ENRICHMENT) {
            return new GDSEnrichmentCLIPopulator();
        }

        throw new UnsupportedOperationException("Could not find configurator populator of type " + gdsConfigurationType.name());
    }
}
