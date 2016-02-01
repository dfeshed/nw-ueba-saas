package fortscale.services.configuration.gds.state;

/**
 * @author gils
 * 07/01/2016
 */
public class GDSStreamingTopologyDefinitionState implements GDSConfigurationState{
    private boolean sourceUsernameResolvingRequired;
    private boolean targetUsernameResolvingRequired;
    private boolean sourceIpResolvingRequired;
    private boolean targetIpResolvingRequired;
    private boolean sourceMachineNormalizationRequired;
    private boolean targetMachineNormalizationRequired;
    private boolean sourceIpGeoLocationRequired;
    private boolean targetIpGeoLocationRequired;

	private String lastStateValue;

    public boolean isSourceUsernameResolvingRequired() {
        return sourceUsernameResolvingRequired;
    }

    public void setSourceUsernameResolvingRequired(boolean sourceUsernameResolvingRequired) {
        this.sourceUsernameResolvingRequired = sourceUsernameResolvingRequired;
    }

    public boolean isTargetUsernameResolvingRequired() {
        return targetUsernameResolvingRequired;
    }

    public void setTargetUsernameResolvingRequired(boolean targetUsernameResolvingRequired) {
        this.targetUsernameResolvingRequired = targetUsernameResolvingRequired;
    }

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

	public String getLastStateValue() {
		return lastStateValue;
	}

	public void setLastStateValue(String lastStateValue) {
		this.lastStateValue = lastStateValue;
	}

	@Override
    public void reset() {
        sourceIpResolvingRequired = false;
        targetIpResolvingRequired = false;
        sourceMachineNormalizationRequired = false;
        targetMachineNormalizationRequired = false;
        sourceIpGeoLocationRequired = false;
        targetIpGeoLocationRequired = false;
		lastStateValue= "etl";
    }
}
