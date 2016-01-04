package fortscale.collection.jobs.gds.configurators;

/**
 * Enum of configurator types associated to their configurator class
 *
 * @author gils
 * 03/01/2016
 */
public enum GDSConfiguratorType {
    SCHEMA(GDSSchemaConfigurator.class),
    COLLECTION(GDSCollectionConfigurator.class),
    USER_NORMALIZATION(GDSUserNormalizationConfigurator.class),
    IP_RESOLVING(GDSIPResolvingConfigurator.class),
    COMPUTER_TAGGING(GDSComputerTaggingConfigurator.class),
    GEO_LOCATION(GDSGeoLocationConfigurator.class),
    USER_MONGO_UPDATE(GDSUserMongoUpdateConfigurator.class),
    HDFS_WRITE(GDSHDFSWriterConfigurator.class);

    private Class<? extends GDSConfigurator> gdsConfiguratorClass;

    GDSConfiguratorType(Class<? extends GDSConfigurator> gdsConfiguratorClass) {
        this.gdsConfiguratorClass = gdsConfiguratorClass;
    }

    public Class<? extends GDSConfigurator> getGDSConfiguratorClass() {
        return gdsConfiguratorClass;
    }
}
