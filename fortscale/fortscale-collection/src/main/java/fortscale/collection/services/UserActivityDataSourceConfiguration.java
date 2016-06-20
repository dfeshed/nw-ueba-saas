package fortscale.collection.services;

/**
 * Created by shays on 14/06/2016.
 */
public class UserActivityDataSourceConfiguration {
    private String datasource;
    private String collectionName;
    private String featureName;
    private String propertyName;

    public UserActivityDataSourceConfiguration(String datasource, String collectionName, String featureName, String propertyName) {
        this.collectionName = collectionName;
        this.featureName = featureName;
        this.propertyName = propertyName;
        this.datasource = datasource;
    }

    public String getDatasource() {
        return datasource;
    }

    public void setDatasource(String datasource) {
        this.datasource = datasource;
    }

    public String getCollectionName() {
        return collectionName;
    }

    public void setCollectionName(String collectionName) {
        this.collectionName = collectionName;
    }

    public String getFeatureName() {
        return featureName;
    }

    public void setFeatureName(String featureName) {
        this.featureName = featureName;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }
}
