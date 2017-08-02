package presidio.ade.domain.record.enriched;


import fortscale.common.general.EventResult;

public class AdeEnrichedActiveDirectoryContext {

    private String userId;
    private EventResult result;
    private Boolean isUserAdmin;
    private String operationType;


    public AdeEnrichedActiveDirectoryContext(EnrichedActiveDirectoryRecord enrichedActiveDirectoryRecord) {
        this.userId = enrichedActiveDirectoryRecord.getUserId();
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
