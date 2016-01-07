package fortscale.services.configuration.gds.state;

/**
 * @author gils
 * 07/01/2016
 */
public class GDSStreamingTopologyDefinitionState implements GDSConfigurationState{
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

    @Override
    public void reset() {
        sourceIpResolvingRequired = false;
        targetIpResolvingRequired = false;
        sourceMachineNormalizationRequired = false;
        targetMachineNormalizationRequired = false;
        sourceIpGeoLocationRequired = false;
        targetIpGeoLocationRequired = false;
    }
}
