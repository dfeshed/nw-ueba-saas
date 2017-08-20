package presidio.ade.domain.record.enriched.file;


import fortscale.domain.core.EventResult;
import presidio.ade.domain.record.enriched.BaseEnrichedContext;

import java.util.List;

public class AdeEnrichedFileContext extends BaseEnrichedContext {
    private List<String> operationTypeCategories;
    private String userId;
    private EventResult result;
    private String operationType;
    private Boolean isSrcDriveShared;
    private Boolean isDstDriveShared;

    public AdeEnrichedFileContext() {
        super();
    }

    public AdeEnrichedFileContext(String eventId) {
        super(eventId);
    }

    public AdeEnrichedFileContext(EnrichedFileRecord enrichedFileRecord) {
        super(enrichedFileRecord.getEventId());
        this.userId = enrichedFileRecord.getUserId();
        this.result = enrichedFileRecord.getResult();
        this.operationType = enrichedFileRecord.getOperationType();
        this.isSrcDriveShared = enrichedFileRecord.getSrcDriveShared();
        this.isDstDriveShared = enrichedFileRecord.getDstDriveShared();
        this.operationTypeCategories = enrichedFileRecord.getOperationTypeCategories();
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


    public List<String> getOperationTypeCategories() {
        return operationTypeCategories;
    }
}
