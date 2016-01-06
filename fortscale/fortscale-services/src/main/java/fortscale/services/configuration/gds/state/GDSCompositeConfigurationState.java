package fortscale.services.configuration.gds.state;

import fortscale.services.configuration.EntityType;

/**
 * Generic data source composite configuration state
 *
 * @author gils
 * 30/12/2015
 */
public class GDSCompositeConfigurationState implements Resettable{

    private GDSBaseDefinitionState GDSBaseDefinitionState = new GDSBaseDefinitionState();
    private GDSSchemaDefinitionState GDSSchemaDefinitionState = new GDSSchemaDefinitionState();
    private GDSCollectionDefinitionState GDSCollectionDefinitionState = new GDSCollectionDefinitionState();
    private GDSEnrichmentDefinitionState GDSEnrichmentDefinitionState = new GDSEnrichmentDefinitionState();

    public String getDataSourceName() {
        return GDSBaseDefinitionState.getDataSourceName();
    }

    public void setDataSourceName(String dataSourceName) {

        GDSBaseDefinitionState.setDataSourceName(dataSourceName);
    }

    public EntityType getEntityType() {
        return GDSBaseDefinitionState.getEntityType();
    }

    public void setEntityType(EntityType entityType) {
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

    public GDSSchemaDefinitionState getGDSSchemaDefinitionState() {
        return GDSSchemaDefinitionState;
    }

    public void setGDSSchemaDefinitionState(GDSSchemaDefinitionState GDSSchemaDefinitionState) {
        this.GDSSchemaDefinitionState = GDSSchemaDefinitionState;
    }

    public GDSCollectionDefinitionState getGDSCollectionDefinitionState() {
        return GDSCollectionDefinitionState;
    }

    public void setGDSCollectionDefinitionState(GDSCollectionDefinitionState GDSCollectionDefinitionState) {
        this.GDSCollectionDefinitionState = GDSCollectionDefinitionState;
    }

    public GDSEnrichmentDefinitionState getGDSEnrichmentDefinitionState() {
        return GDSEnrichmentDefinitionState;
    }

    public void setGDSEnrichmentDefinitionState(GDSEnrichmentDefinitionState GDSEnrichmentDefinitionState) {
        this.GDSEnrichmentDefinitionState = GDSEnrichmentDefinitionState;
    }

    public void reset() {
        GDSBaseDefinitionState.reset();
        GDSSchemaDefinitionState.reset();
        GDSCollectionDefinitionState.reset();
        GDSEnrichmentDefinitionState.reset();
    }
}
