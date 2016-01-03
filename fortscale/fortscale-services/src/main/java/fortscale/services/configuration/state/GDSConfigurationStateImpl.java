package fortscale.services.configuration.state;

/**
 * @author gils
 * 30/12/2015
 */
public class GDSConfigurationStateImpl implements Resettable{

    private BaseDefinitionState baseDefinitionState = new BaseDefinitionState();
    private SchemaDefinitionState schemaDefinitionState = new SchemaDefinitionState();
    private CollectionDefinitionState collectionDefinitionState = new CollectionDefinitionState();
    private EnrichmentDefinitionState enrichmentDefinitionState = new EnrichmentDefinitionState();

    public String getDataSourceName() {
        return baseDefinitionState.getDataSourceName();
    }

    public void setDataSourceName(String dataSourceName) {

        baseDefinitionState.setDataSourceName(dataSourceName);
    }

    public GDSEntityType getEntityType() {
        return baseDefinitionState.getEntityType();
    }

    public void setEntityType(GDSEntityType entityType) {
        baseDefinitionState.setEntityType(entityType);
    }

    public boolean isDataSourceAlreadyDefined() {
        return baseDefinitionState.getDataSourceName() != null && baseDefinitionState.getEntityType() != null;
    }

    public String getExistingDataSources() {
        return baseDefinitionState.getExistingDataSources();
    }

    public void setExistingDataSources(String currentDataSources) {
        this.baseDefinitionState.setExistingDataSources(currentDataSources);
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
        baseDefinitionState.reset();
        schemaDefinitionState.reset();
        collectionDefinitionState.reset();
        enrichmentDefinitionState.reset();
    }
}
