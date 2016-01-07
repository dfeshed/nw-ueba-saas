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

    private boolean sourceIpResolvingRequired;
    private boolean targetIpResolvingRequired;
    private boolean sourceMachineNormalizationRequired;
    private boolean targetMachineNormalizationRequired;
    private boolean sourceIpGeoLocationRequired;
    private boolean targetIpGeoLocationRequired;

    public boolean isSourceIpResolvingRequired() {
        return sourceIpResolvingRequired;
    }

    public void setSourceIpResolvingRequired(boolean sourceIpResolvingRequired) {
        this.sourceIpResolvingRequired = sourceIpResolvingRequired;
    }

    public boolean isTargetIpResolvingRequired() {
        return targetIpResolvingRequired;
    }

    public void setTargetIpResolvingRequired(boolean targetIpResolving) {
        this.targetIpResolvingRequired = targetIpResolving;
    }

    public boolean isSourceMachineNormalizationRequired() {
        return sourceMachineNormalizationRequired;
    }

    public void setSourceMachineNormalizationRequired(boolean sourceMachineNormalizationRequired) {
        this.sourceMachineNormalizationRequired = sourceMachineNormalizationRequired;
    }

    public boolean isTargetMachineNormalizationRequired() {
        return targetMachineNormalizationRequired;
    }

    public void setTargetMachineNormalizationRequired(boolean targetMachineNormalizationRequired) {
        this.targetMachineNormalizationRequired = targetMachineNormalizationRequired;
    }

    public boolean isSourceIpGeoLocationRequired() {
        return sourceIpGeoLocationRequired;
    }

    public void setSourceIpGeoLocationRequired(boolean sourceIpGeoLocationRequired) {
        this.sourceIpGeoLocationRequired = sourceIpGeoLocationRequired;
    }

    public boolean isTargetIpGeoLocationRequired() {
        return targetIpGeoLocationRequired;
    }

    public void setTargetIpGeoLocationRequired(boolean targetIpGeoLocationRequired) {
        this.targetIpGeoLocationRequired = targetIpGeoLocationRequired;
    }

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
