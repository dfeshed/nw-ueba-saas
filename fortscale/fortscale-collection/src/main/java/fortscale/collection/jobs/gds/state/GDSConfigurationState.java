package fortscale.collection.jobs.gds.state;

import fortscale.collection.jobs.gds.GDSEntityType;

/**
 * @author gils
 * 30/12/2015
 */
public class GDSConfigurationState implements Resettable{

    private String dataSourceName;
    private GDSEntityType entityType;
    private String currentDataSources;

    private SchemaDefinitionState schemaDefinitionState = new SchemaDefinitionState();
    private CollectionDefinitionState collectionDefinitionState = new CollectionDefinitionState();
    private EnrichmentDefinitionState enrichmentDefinitionState = new EnrichmentDefinitionState();

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

    public SchemaDefinitionState getSchemaDefinitionState() {
        return schemaDefinitionState;
    }

    public void setSchemaDefinitionState(SchemaDefinitionState schemaDefinitionState) {
        this.schemaDefinitionState = schemaDefinitionState;
    }

    public CollectionDefinitionState getCollectionDefinitionState() {
        return collectionDefinitionState;
    }

    public void setCollectionDefinitionState(CollectionDefinitionState collectionDefinitionState) {
        this.collectionDefinitionState = collectionDefinitionState;
    }

    public EnrichmentDefinitionState getEnrichmentDefinitionState() {
        return enrichmentDefinitionState;
    }

    public void setEnrichmentDefinitionState(EnrichmentDefinitionState enrichmentDefinitionState) {
        this.enrichmentDefinitionState = enrichmentDefinitionState;
    }

    public void reset() {
        dataSourceName = null;
        entityType = null;
        currentDataSources = null;

        schemaDefinitionState.reset();
        collectionDefinitionState.reset();
        enrichmentDefinitionState.reset();
    }
}
