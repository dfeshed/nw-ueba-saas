package presidio.ade.domain.record.enriched;


import fortscale.common.general.EventResult;

public class AdeEnrichedFileContext {

    private String userId;
    private EventResult result;
    private String operationType;
    private Boolean isSrcDriveShared;
    private Boolean isDstDriveShared;


    public AdeEnrichedFileContext(EnrichedFileRecord enrichedFileRecord) {
        this.userId = enrichedFileRecord.getUserId();
        this.result = enrichedFileRecord.getResult();
        this.operationType = enrichedFileRecord.getOperationType();
        this.isSrcDriveShared = enrichedFileRecord.getSrcDriveShared();
        this.isDstDriveShared = enrichedFileRecord.getDstDriveShared();
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

    public String getOperationType() {
        return operationType;
    }

    public void setOperationType(String operationType) {
        this.operationType = operationType;
    }

    public Boolean getSrcDriveShared() {
        return isSrcDriveShared;
    }

    public void setSrcDriveShared(Boolean srcDriveShared) {
        isSrcDriveShared = srcDriveShared;
    }

    public Boolean getDstDriveShared() {
        return isDstDriveShared;
    }

    public void setDstDriveShared(Boolean dstDriveShared) {
        isDstDriveShared = dstDriveShared;
    }
}
