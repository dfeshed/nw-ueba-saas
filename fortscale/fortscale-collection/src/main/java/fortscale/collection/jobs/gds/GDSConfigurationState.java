package fortscale.collection.jobs.gds;

import fortscale.services.configuration.ConfigurationParam;

import java.util.HashMap;
import java.util.Map;

/**
 * @author gils
 * 30/12/2015
 */
public class GDSConfigurationState {

    private String dataSourceName;
    private GDSEntityType entityType;
    private String currentDataSources;

    private SchemaDefinitionsState schemaDefinitionsState = new SchemaDefinitionsState();

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

    public boolean isDataSourceAlreadyDefined() {
        return dataSourceName != null && entityType != null;
    }

    public String getCurrentDataSources() {
        return currentDataSources;
    }

    public void setCurrentDataSources(String currentDataSources) {
        this.currentDataSources = currentDataSources;
    }

    public SchemaDefinitionsState getSchemaDefinitionsState() {
        return schemaDefinitionsState;
    }

    public void clear() {
        // TODO implement
    }

    public static class SchemaDefinitionsState {
        private Map<String, ConfigurationParam> paramsMap = new HashMap<>();

        public Map<String, ConfigurationParam> getParamsMap() {
            return paramsMap;
        }

        public void setParamsMap(Map<String, ConfigurationParam> paramsMap) {
            this.paramsMap = paramsMap;
        }
    }
}
