package presidio.ade.domain.record.enriched;


public class AdeEnrichedAuthenticationContext {
    private String normalizedUsername;
    private String result;
    private Boolean isDstMachineRemote;


    public AdeEnrichedAuthenticationContext(EnrichedAuthenticationRecord enrichedAuthenticationRecord) {
        this.normalizedUsername = enrichedAuthenticationRecord.getNormalizedUsername();
        this.result = enrichedAuthenticationRecord.getResult();
        this.isDstMachineRemote = enrichedAuthenticationRecord.getDstMachineRemote();
    }

    public String getNormalizedUsername() {
        return normalizedUsername;
    }

    public void setNormalizedUsername(String normalizedUsername) {
        this.normalizedUsername = normalizedUsername;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public Boolean getDstMachineRemote() {
        return isDstMachineRemote;
    }

    public void setDstMachineRemote(Boolean dstMachineRemote) {
        isDstMachineRemote = dstMachineRemote;
    }
}
