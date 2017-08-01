package presidio.ade.domain.record.enriched;


import fortscale.common.general.EventResult;

public class AdeEnrichedAuthenticationContext {
    private String normalizedUsername;
    private EventResult result;
    private Boolean isDstMachineRemote;


    public AdeEnrichedAuthenticationContext(EnrichedAuthenticationRecord enrichedAuthenticationRecord) {
        this.normalizedUsername = enrichedAuthenticationRecord.getUserId();
        this.result = enrichedAuthenticationRecord.getResult();
        this.isDstMachineRemote = enrichedAuthenticationRecord.getDstMachineRemote();
    }

    public String getNormalizedUsername() {
        return normalizedUsername;
    }

    public void setNormalizedUsername(String normalizedUsername) {
        this.normalizedUsername = normalizedUsername;
    }

    public EventResult getResult() {
        return result;
    }

    public void setResult(EventResult result) {
        this.result = result;
    }

    public Boolean getDstMachineRemote() {
        return isDstMachineRemote;
    }

    public void setDstMachineRemote(Boolean dstMachineRemote) {
        isDstMachineRemote = dstMachineRemote;
    }
}
