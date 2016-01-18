package fortscale.services.configuration.gds.state;

import fortscale.services.configuration.EntityType;

/**
 * Generic data source base definition state
 *
 * @author gils
 * 03/01/2016
 */
public class GDSBaseDefinitionState implements GDSConfigurationState{
    private String dataSourceName;
    private EntityType entityType;
    // TODO move it outside
    private String existingDataSources;

    public String getDataSourceName() {
        return dataSourceName;
    }

    public void setDataSourceName(String dataSourceName) {
        this.dataSourceName = dataSourceName;
    }

    public EntityType getEntityType() {
        return entityType;
    }

    public void setEntityType(EntityType entityType) {
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
