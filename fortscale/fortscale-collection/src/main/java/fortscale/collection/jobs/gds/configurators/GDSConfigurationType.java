package fortscale.collection.jobs.gds.configurators;

import fortscale.collection.jobs.gds.GDSConfigurator;

/**
 * @author gils
 * 03/01/2016
 */
public enum GDSConfigurationType {
    COLLECTION(GDSCollectionConfigurator.class),
    SCHEMA(GDSSchemaConfigurator.class),
    USER_NORMALIZATION(GDSUserNormalizationConfigurator.class),
    IP_RESOLVING(GDSIPResolvingConfigurator.class),
    COMPUTER_TAGGING(GDSComputerTaggingConfigurator.class),
    GEO_LOCATION(GDSGeoLocationConfigurator.class),
    USER_MONGO_UPDATE(GDSUserMongoUpdateConfigurator.class),
    HDFS_WRITE(GDSHDFSWriterConfigurator.class);

    private Class<? extends GDSConfigurator> gdsConfiguratorClass;

    GDSConfigurationType(Class<? extends GDSConfigurator> gdsConfiguratorClass) {
        this.gdsConfiguratorClass = gdsConfiguratorClass;
    }

    public Class<? extends GDSConfigurator> getGDSConfiguratorClass() {
        return gdsConfiguratorClass;
    }
}
