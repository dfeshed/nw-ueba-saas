package fortscale.services.configuration.state;

/**
 * @author gils
 * 30/12/2015
 */
public class GDSCompositeConfigurationState implements Resettable{

    private GDSBaseDefinitionState GDSBaseDefinitionState = new GDSBaseDefinitionState();
    private SchemaDefinitionState schemaDefinitionState = new SchemaDefinitionState();
    private CollectionDefinitionState collectionDefinitionState = new CollectionDefinitionState();
    private EnrichmentDefinitionState enrichmentDefinitionState = new EnrichmentDefinitionState();

    public String getDataSourceName() {
        return GDSBaseDefinitionState.getDataSourceName();
    }

    public void setDataSourceName(String dataSourceName) {

        GDSBaseDefinitionState.setDataSourceName(dataSourceName);
    }

    public GDSEntityType getEntityType() {
        return GDSBaseDefinitionState.getEntityType();
    }

    public void setEntityType(GDSEntityType entityType) {
        GDSBaseDefinitionState.setEntityType(entityType);
    }

    public boolean isDataSourceAlreadyDefined() {
        return GDSBaseDefinitionState.getDataSourceName() != null && GDSBaseDefinitionState.getEntityType() != null;
    }

    public String getExistingDataSources() {
        return GDSBaseDefinitionState.getExistingDataSources();
    }

    public void setExistingDataSources(String currentDataSources) {
        this.GDSBaseDefinitionState.setExistingDataSources(currentDataSources);
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
        GDSBaseDefinitionState.reset();
        schemaDefinitionState.reset();
        collectionDefinitionState.reset();
        enrichmentDefinitionState.reset();
    }
}
