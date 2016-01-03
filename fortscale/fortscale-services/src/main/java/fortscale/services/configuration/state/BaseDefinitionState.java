package fortscale.services.configuration.state;

/**
 * @author gils
 * 03/01/2016
 */
public class BaseDefinitionState implements GDSConfigurationState{
    private String dataSourceName;
    private GDSEntityType entityType;
    private String existingDataSources;

    public String getDataSourceName() {
        return dataSourceName;
    }

    public void setDataSourceName(String dataSourceName) {
        this.dataSourceName = dataSourceName;
    }

    public GDSEntityType getEntityType() {
        return entityType;
    }

    public void setEntityType(GDSEntityType entityType) {
        this.entityType = entityType;
    }

    public String getExistingDataSources() {
        return existingDataSources;
    }

    public void setExistingDataSources(String existingDataSources) {
        this.existingDataSources = existingDataSources;
    }

    @Override
    public void reset() {
        dataSourceName = null;
        entityType = null;
        existingDataSources = null;
    }
}
