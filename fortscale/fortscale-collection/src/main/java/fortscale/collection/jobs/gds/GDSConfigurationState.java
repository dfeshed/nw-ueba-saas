package fortscale.collection.jobs.gds;

/**
 * @author gils
 * 30/12/2015
 */
public class GDSConfigurationState {

    private String dataSourceName;
    private GDSEntityType entityType;

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
}
