package presidio.ade.domain.record.enriched.activedirectory;


import fortscale.domain.core.EventResult;
import presidio.ade.domain.record.enriched.BaseEnrichedContext;

public class AdeEnrichedActiveDirectoryContext  extends BaseEnrichedContext {

    private String userId;
    private String srcMachineId;
    private String srcMachineNameRegexCluster;
    private EventResult result;
    private Boolean isUserAdmin;
    private String operationType;

    public AdeEnrichedActiveDirectoryContext() {
        super();
    }

    public AdeEnrichedActiveDirectoryContext(EnrichedActiveDirectoryRecord enrichedActiveDirectoryRecord) {
        super(enrichedActiveDirectoryRecord.getEventId());
        this.userId = enrichedActiveDirectoryRecord.getUserId();
        this.srcMachineId = enrichedActiveDirectoryRecord.getSrcMachineId();
        this.srcMachineNameRegexCluster = enrichedActiveDirectoryRecord.getSrcMachineNameRegexCluster();
        this.result = enrichedActiveDirectoryRecord.getResult();
        this.isUserAdmin = enrichedActiveDirectoryRecord.getUserAdmin();
        this.operationType = enrichedActiveDirectoryRecord.getOperationType();
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getSrcMachineId() {
        return srcMachineId;
    }

    public void setSrcMachineId(String srcMachineId) {
        this.srcMachineId = srcMachineId;
    }

    public String getSrcMachineNameRegexCluster() {
        return srcMachineNameRegexCluster;
    }

    public void setSrcMachineNameRegexCluster(String srcMachineNameRegexCluster) {
        this.srcMachineNameRegexCluster = srcMachineNameRegexCluster;
    }

    public EventResult getResult() {
        return result;
    }

    public void setResult(EventResult result) {
        this.result = result;
    }

    public Boolean getUserAdmin() {
        return isUserAdmin;
    }

    public void setUserAdmin(Boolean userAdmin) {
        isUserAdmin = userAdmin;
    }

    public String getOperationType() {
        return operationType;
    }

    public void setOperationType(String operationType) {
        this.operationType = operationType;
    }
}
