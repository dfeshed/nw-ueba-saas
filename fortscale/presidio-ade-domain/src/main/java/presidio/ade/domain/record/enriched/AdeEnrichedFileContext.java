package presidio.ade.domain.record.enriched;


public class AdeEnrichedFileContext {

    private String normalizedUsername;
    private String result;
    private String operationType;
    private Boolean isSrcDriveShared;
    private Boolean isDstDriveShared;


    public AdeEnrichedFileContext(EnrichedFileRecord enrichedFileRecord) {
        this.normalizedUsername = enrichedFileRecord.getNormalizedUsername();
        this.result = enrichedFileRecord.getResult();
        this.operationType = enrichedFileRecord.getOperationType();
        this.isSrcDriveShared = enrichedFileRecord.getSrcDriveShared();
        this.isDstDriveShared = enrichedFileRecord.getDstDriveShared();
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
