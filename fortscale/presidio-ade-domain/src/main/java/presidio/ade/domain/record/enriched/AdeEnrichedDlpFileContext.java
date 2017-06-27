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

    public AdeEnrichedDlpFileContext(EnrichedDlpFileRecord enrichedDlpFileRecord){
        this.normalizedUsername = enrichedDlpFileRecord.getNormalized_username();
        this.normalizedSrcMachine = enrichedDlpFileRecord.getNormalized_src_machine();
        this.sourcePath = enrichedDlpFileRecord.getSource_path();
        this.sourceFileName = enrichedDlpFileRecord.getSource_file_name();
        this.sourceDriveType = enrichedDlpFileRecord.getSource_drive_type();
        this.destinationPath = enrichedDlpFileRecord.getDestination_path();
        this.destinationFileName = enrichedDlpFileRecord.getDestination_file_name();
        this.destinationDriveType = enrichedDlpFileRecord.getDestination_drive_type();
        this.fileSize = enrichedDlpFileRecord.getFile_size();
        this.eventType = enrichedDlpFileRecord.getEvent_type();
        this.wasBlocked = enrichedDlpFileRecord.isWas_blocked();
        this.wasClassified = enrichedDlpFileRecord.isWas_classified();
        this.malwareScanResult = enrichedDlpFileRecord.getMalware_scan_result();
        this.executingApplication = enrichedDlpFileRecord.getExecuting_application();
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
