package presidio.ade.domain.record.enriched.authentication;


import fortscale.domain.core.EventResult;
import presidio.ade.domain.record.enriched.BaseEnrichedContext;

public class AdeEnrichedAuthenticationContext extends BaseEnrichedContext {
    private String userId;
    private EventResult result;
    private String dstMachineDomain;
    private String dstMachineNameRegexCluster;
    private String srcMachineNameRegexCluster;
    private String dstMachineId;
    private String srcMachineId;

    public AdeEnrichedAuthenticationContext() {
        super();
    }

    public AdeEnrichedAuthenticationContext(EnrichedAuthenticationRecord enrichedAuthenticationRecord) {
        super(enrichedAuthenticationRecord.getEventId());
        this.userId = enrichedAuthenticationRecord.getUserId();
        this.result = enrichedAuthenticationRecord.getResult();
        this.dstMachineDomain = enrichedAuthenticationRecord.getDstMachineDomain();
        this.dstMachineId = enrichedAuthenticationRecord.getDstMachineId();
        this.dstMachineNameRegexCluster = enrichedAuthenticationRecord.getDstMachineNameRegexCluster();
        this.srcMachineNameRegexCluster = enrichedAuthenticationRecord.getSrcMachineNameRegexCluster();
        this.srcMachineId = enrichedAuthenticationRecord.getSrcMachineId();
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public EventResult getResult() {
        return result;
    }

    public void setResult(EventResult result) {
        this.result = result;
    }

    public String getDstMachineDomain() {
        return dstMachineDomain;
    }

    public void setDstMachineDomain(String dstMachineDomain) {
        this.dstMachineDomain = dstMachineDomain;
    }

    public String getDstMachineNameRegexCluster() {
        return dstMachineNameRegexCluster;
    }

    public void setDstMachineNameRegexCluster(String dstMachineNameRegexCluster) {
        this.dstMachineNameRegexCluster = dstMachineNameRegexCluster;
    }

    public String getSrcMachineNameRegexCluster() {
        return srcMachineNameRegexCluster;
    }

    public void setSrcMachineNameRegexCluster(String srcMachineNameRegexCluster) {
        this.srcMachineNameRegexCluster = srcMachineNameRegexCluster;
    }

    public String getDstMachineId() {
        return dstMachineId;
    }

    public void setDstMachineId(String dstMachineId) {
        this.dstMachineId = dstMachineId;
    }

    public String getSrcMachineId() {
        return srcMachineId;
    }

    public void setSrcMachineId(String srcMachineId) {
        this.srcMachineId = srcMachineId;
    }
}
