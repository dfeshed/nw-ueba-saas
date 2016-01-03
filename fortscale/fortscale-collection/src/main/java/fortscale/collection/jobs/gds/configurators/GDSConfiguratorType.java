package fortscale.collection.jobs.gds.configurators;

import fortscale.collection.jobs.gds.GDSConfigurator;

/**
 * @author gils
 * 03/01/2016
 */
public enum GDSConfiguratorType {
    COLLECTION(GDSCollectionConfigurator.class),
    SCHEMA(GDSSchemaConfigurator.class),
    ENRICHMENT(GDSEnrichmentConfigurator.class);

    private Class<? extends GDSConfigurator> gdsConfiguratorClass;

    GDSConfiguratorType(Class<? extends GDSConfigurator> gdsConfiguratorClass) {
        this.gdsConfiguratorClass = gdsConfiguratorClass;
    }

    public Class<? extends GDSConfigurator> getGDSConfiguratorClass() {
        return gdsConfiguratorClass;
    }
}
