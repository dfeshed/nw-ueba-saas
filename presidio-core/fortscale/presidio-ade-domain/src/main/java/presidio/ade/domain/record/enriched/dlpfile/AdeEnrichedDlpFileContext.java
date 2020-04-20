package presidio.ade.domain.record.enriched.dlpfile;

import presidio.ade.domain.record.enriched.BaseEnrichedContext;

/**
 * Created by YaronDL on 6/14/2017.
 */
public class AdeEnrichedDlpFileContext extends BaseEnrichedContext {
    private String userId;
    private String srcMachineId;
    private String sourcePath;
    private String sourceFileName;
    private String sourceDriveType;
    private String destinationPath;
    private String destinationFileName;
    private String destinationDriveType;
    private double fileSize;
    private String operationType;
    private boolean wasBlocked;
    private boolean wasClassified;
    private String malwareScanResult;
    private String executingApplication;

    public AdeEnrichedDlpFileContext() {
        super();
    }

    public AdeEnrichedDlpFileContext(EnrichedDlpFileRecord enrichedDlpFileRecord) {
        super(enrichedDlpFileRecord.getEventId());
        this.userId = enrichedDlpFileRecord.getUserId();
        this.srcMachineId = enrichedDlpFileRecord.getSrcMachineId();
        this.sourcePath = enrichedDlpFileRecord.getSourcePath();
        this.sourceFileName = enrichedDlpFileRecord.getSourceFileName();
        this.sourceDriveType = enrichedDlpFileRecord.getSourceDriveType();
        this.destinationPath = enrichedDlpFileRecord.getDestinationPath();
        this.destinationFileName = enrichedDlpFileRecord.getDestinationFileName();
        this.destinationDriveType = enrichedDlpFileRecord.getDestinationDriveType();
        this.fileSize = enrichedDlpFileRecord.getFileSize();
        this.operationType = enrichedDlpFileRecord.getOperationType();
        this.wasBlocked = enrichedDlpFileRecord.isWasBlocked();
        this.wasClassified = enrichedDlpFileRecord.isWasClassified();
        this.malwareScanResult = enrichedDlpFileRecord.getMalwareScanResult();
        this.executingApplication = enrichedDlpFileRecord.getExecutingApplication();
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

    public String getSourcePath() {
        return sourcePath;
    }

    public void setSourcePath(String sourcePath) {
        this.sourcePath = sourcePath;
    }

    public String getSourceFileName() {
        return sourceFileName;
    }

    public void setSourceFileName(String sourceFileName) {
        this.sourceFileName = sourceFileName;
    }

    public String getSourceDriveType() {
        return sourceDriveType;
    }

    public void setSourceDriveType(String sourceDriveType) {
        this.sourceDriveType = sourceDriveType;
    }

    public String getDestinationPath() {
        return destinationPath;
    }

    public void setDestinationPath(String destinationPath) {
        this.destinationPath = destinationPath;
    }

    public String getDestinationFileName() {
        return destinationFileName;
    }

    public void setDestinationFileName(String destinationFileName) {
        this.destinationFileName = destinationFileName;
    }

    public String getDestinationDriveType() {
        return destinationDriveType;
    }

    public void setDestinationDriveType(String destinationDriveType) {
        this.destinationDriveType = destinationDriveType;
    }

    public double getFileSize() {
        return fileSize;
    }

    public void setFileSize(double fileSize) {
        this.fileSize = fileSize;
    }

    public String getOperationType() {
        return operationType;
    }

    public void setOperationType(String operationType) {
        this.operationType = operationType;
    }

    public boolean isWasBlocked() {
        return wasBlocked;
    }

    public void setWasBlocked(boolean wasBlocked) {
        this.wasBlocked = wasBlocked;
    }

    public boolean isWasClassified() {
        return wasClassified;
    }

    public void setWasClassified(boolean wasClassified) {
        this.wasClassified = wasClassified;
    }

    public String getMalwareScanResult() {
        return malwareScanResult;
    }

    public void setMalwareScanResult(String malwareScanResult) {
        this.malwareScanResult = malwareScanResult;
    }

    public String getExecutingApplication() {
        return executingApplication;
    }

    public void setExecutingApplication(String executingApplication) {
        this.executingApplication = executingApplication;
    }
}
