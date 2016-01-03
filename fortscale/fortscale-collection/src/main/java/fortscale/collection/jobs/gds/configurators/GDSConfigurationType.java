package fortscale.collection.jobs.gds.configurators;

import fortscale.collection.jobs.gds.GDSConfigurator;

/**
 * @author gils
 * 03/01/2016
 */
public enum GDSConfigurationType {
    COLLECTION(GDSCollectionConfigurator.class),
    SCHEMA(GDSSchemaConfigurator.class),
    ENRICHMENT(GDSEnrichmentConfigurator.class);

    private Class<? extends GDSConfigurator> gdsConfiguratorClass;

    GDSConfigurationType(Class<? extends GDSConfigurator> gdsConfiguratorClass) {
        this.gdsConfiguratorClass = gdsConfiguratorClass;
    }

    public Class<? extends GDSConfigurator> getGDSConfiguratorClass() {
        return gdsConfiguratorClass;
    }
}
