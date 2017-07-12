package presidio.ade.domain.record.enriched;

import presidio.ade.domain.record.enriched.EnrichedDlpFileRecord;

/**
 * Created by YaronDL on 6/14/2017.
 */
public class AdeEnrichedDlpFileContext {
    private String normalizedUsername;
    private String normalizedSrcMachine;
    private String sourcePath;
    private String sourceFileName;
    private String sourceDriveType;
    private String destinationPath;
    private String destinationFileName;
    private String destinationDriveType;
    private double fileSize;
    private String eventType;
    private boolean wasBlocked;
    private boolean wasClassified;
    private String malwareScanResult;
    private String executingApplication;

    public AdeEnrichedDlpFileContext(EnrichedDlpFileRecord enrichedDlpFileRecord) {
        this.normalizedUsername = enrichedDlpFileRecord.getNormalizedUsername();
        this.normalizedSrcMachine = enrichedDlpFileRecord.getNormalizedSrcMachine();
        this.sourcePath = enrichedDlpFileRecord.getSourcePath();
        this.sourceFileName = enrichedDlpFileRecord.getSourceFileName();
        this.sourceDriveType = enrichedDlpFileRecord.getSourceDriveType();
        this.destinationPath = enrichedDlpFileRecord.getDestinationPath();
        this.destinationFileName = enrichedDlpFileRecord.getDestinationFileName();
        this.destinationDriveType = enrichedDlpFileRecord.getDestinationDriveType();
        this.fileSize = enrichedDlpFileRecord.getFileSize();
        this.eventType = enrichedDlpFileRecord.getEventType();
        this.wasBlocked = enrichedDlpFileRecord.isWasBlocked();
        this.wasClassified = enrichedDlpFileRecord.isWasClassified();
        this.malwareScanResult = enrichedDlpFileRecord.getMalwareScanResult();
        this.executingApplication = enrichedDlpFileRecord.getExecutingApplication();
    }

    public String getNormalizedUsername() {
        return normalizedUsername;
    }

    public void setNormalizedUsername(String normalizedUsername) {
        this.normalizedUsername = normalizedUsername;
    }

    public String getNormalizedSrcMachine() {
        return normalizedSrcMachine;
    }

    public void setNormalizedSrcMachine(String normalizedSrcMachine) {
        this.normalizedSrcMachine = normalizedSrcMachine;
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

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
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
