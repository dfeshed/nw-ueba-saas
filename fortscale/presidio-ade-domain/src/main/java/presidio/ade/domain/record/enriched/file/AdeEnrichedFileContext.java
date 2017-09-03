package presidio.ade.domain.record.enriched.file;

import fortscale.domain.core.EventResult;
import presidio.ade.domain.record.enriched.BaseEnrichedContext;

import java.util.List;

public class AdeEnrichedFileContext extends BaseEnrichedContext {
    private List<String> operationTypeCategories;
    private String userId;
    private EventResult result;
    private String operationType;
    private Boolean srcDriveShared;
    private Boolean dstDriveShared;

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
        this.srcDriveShared = enrichedFileRecord.getSrcDriveShared();
        this.dstDriveShared = enrichedFileRecord.getDstDriveShared();
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
        return srcDriveShared;
    }

    public void setSrcDriveShared(Boolean srcDriveShared) {
        this.srcDriveShared = srcDriveShared;
    }

    public Boolean getDstDriveShared() {
        return dstDriveShared;
    }

    public void setDstDriveShared(Boolean dstDriveShared) {
        this.dstDriveShared = dstDriveShared;
    }

    public List<String> getOperationTypeCategories() {
        return operationTypeCategories;
    }

    public void setOperationTypeCategories(List<String> operationTypeCategories) {
        this.operationTypeCategories = operationTypeCategories;
    }
}
