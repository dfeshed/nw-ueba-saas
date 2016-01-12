package fortscale.services.configuration.gds.state;

import fortscale.services.configuration.EntityType;

/**
 * Generic data source composite configuration state
 *
 * @author gils
 * 30/12/2015
 */
public class GDSCompositeConfigurationState implements Resettable{

    private GDSBaseDefinitionState baseDefinitionState = new GDSBaseDefinitionState();
    private GDSSchemaDefinitionState schemaDefinitionState = new GDSSchemaDefinitionState();
    private GDSCollectionDefinitionState collectionDefinitionState = new GDSCollectionDefinitionState();
    private GDSEnrichmentDefinitionState enrichmentDefinitionState = new GDSEnrichmentDefinitionState();
    private GDSStreamingTopologyDefinitionState streamingTopologyDefinitionState = new GDSStreamingTopologyDefinitionState();
	private GDSRAWDataModelAndScoreState rawDataModelAndScoreState = new GDSRAWDataModelAndScoreState();

    public String getDataSourceName() {
        return baseDefinitionState.getDataSourceName();
    }

    public void setDataSourceName(String dataSourceName) {

        baseDefinitionState.setDataSourceName(dataSourceName);
    }

    public EntityType getEntityType() {
        return baseDefinitionState.getEntityType();
    }

    public void setEntityType(EntityType entityType) {
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

    public GDSSchemaDefinitionState getSchemaDefinitionState() {
        return schemaDefinitionState;
    }

    public void setSchemaDefinitionState(GDSSchemaDefinitionState schemaDefinitionState) {
        this.schemaDefinitionState = schemaDefinitionState;
    }

    public GDSCollectionDefinitionState getCollectionDefinitionState() {
        return collectionDefinitionState;
    }

    public void setCollectionDefinitionState(GDSCollectionDefinitionState collectionDefinitionState) {
        this.collectionDefinitionState = collectionDefinitionState;
    }

    public GDSEnrichmentDefinitionState getEnrichmentDefinitionState() {
        return enrichmentDefinitionState;
    }

    public void setEnrichmentDefinitionState(GDSEnrichmentDefinitionState enrichmentDefinitionState) {
        this.enrichmentDefinitionState = enrichmentDefinitionState;
    }

    public GDSStreamingTopologyDefinitionState getStreamingTopologyDefinitionState() {
        return streamingTopologyDefinitionState;
    }

    public void setStreamingTopologyDefinitionState(GDSStreamingTopologyDefinitionState streamingTopologyDefinitionState) {
        this.streamingTopologyDefinitionState = streamingTopologyDefinitionState;
    }

	public GDSBaseDefinitionState getBaseDefinitionState() {
		return baseDefinitionState;
	}

	public void setBaseDefinitionState(GDSBaseDefinitionState baseDefinitionState) {
		this.baseDefinitionState = baseDefinitionState;
	}

	public GDSRAWDataModelAndScoreState getRawDataModelAndScoreState() {
		return rawDataModelAndScoreState;
	}

	public void setRawDataModelAndScoreState(GDSRAWDataModelAndScoreState rawDataModelAndScoreState) {
		this.rawDataModelAndScoreState = rawDataModelAndScoreState;
	}

	public void reset() {
        baseDefinitionState.reset();
        schemaDefinitionState.reset();
        collectionDefinitionState.reset();
        enrichmentDefinitionState.reset();
        streamingTopologyDefinitionState.reset();
		rawDataModelAndScoreState.reset();
    }
}
