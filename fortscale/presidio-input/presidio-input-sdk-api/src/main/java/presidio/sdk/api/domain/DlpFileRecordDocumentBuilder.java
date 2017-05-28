package presidio.sdk.api.domain;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DlpFileRecordDocumentBuilder {
    private Date dateTime;
    private long dateTimeUnix;
    private String eventType;
    private String eventId;
    private String username;
    private String normalizedUsername;
    private String fullName;
    private String hostname;
    private String normalizedSrcMachine;
    private String sourceIp;
    private String executingApplication;
    private String sourcePath;
    private String destinationPath;
    private String sourceFileName;
    private String destinationFileName;
    private double fileSize;
    private String sourceDriveType;
    private String destinationDriveType;
    private boolean wasClassified;
    private boolean wasBlocked;
    private String malwareScanResult;
    private String policyName;
    private String isRdp;
    private String isAdminActivity;
    private String isRegistryChanged;
    private String updateTimestamp;
    private String yearmonthday;

    public DlpFileRecordDocumentBuilder setDateTime(Date dateTime) {
        this.dateTime = dateTime;
        return this;
    }

    public DlpFileRecordDocumentBuilder setDateTimeUnix(long dateTimeUnix) {
        this.dateTimeUnix = dateTimeUnix;
        return this;
    }

    public DlpFileRecordDocumentBuilder setEventType(String eventType) {
        this.eventType = eventType;
        return this;
    }

    public DlpFileRecordDocumentBuilder setEventId(String eventId) {
        this.eventId = eventId;
        return this;
    }

    public DlpFileRecordDocumentBuilder setUsername(String username) {
        this.username = username;
        return this;
    }

    public DlpFileRecordDocumentBuilder setNormalizedUsername(String normalizedUsername) {
        this.normalizedUsername = normalizedUsername;
        return this;
    }

    public DlpFileRecordDocumentBuilder setFullName(String fullName) {
        this.fullName = fullName;
        return this;
    }

    public DlpFileRecordDocumentBuilder setHostname(String hostname) {
        this.hostname = hostname;
        return this;
    }

    public DlpFileRecordDocumentBuilder setNormalizedSrcMachine(String normalizedSrcMachine) {
        this.normalizedSrcMachine = normalizedSrcMachine;
        return this;
    }

    public DlpFileRecordDocumentBuilder setSourceIp(String sourceIp) {
        this.sourceIp = sourceIp;
        return this;
    }

    public DlpFileRecordDocumentBuilder setExecutingApplication(String executingApplication) {
        this.executingApplication = executingApplication;
        return this;
    }

    public DlpFileRecordDocumentBuilder setSourcePath(String sourcePath) {
        this.sourcePath = sourcePath;
        return this;
    }

    public DlpFileRecordDocumentBuilder setDestinationPath(String destinationPath) {
        this.destinationPath = destinationPath;
        return this;
    }

    public DlpFileRecordDocumentBuilder setSourceFileName(String sourceFileName) {
        this.sourceFileName = sourceFileName;
        return this;
    }

    public DlpFileRecordDocumentBuilder setDestinationFileName(String destinationFileName) {
        this.destinationFileName = destinationFileName;
        return this;
    }

    public DlpFileRecordDocumentBuilder setFileSize(double fileSize) {
        this.fileSize = fileSize;
        return this;
    }

    public DlpFileRecordDocumentBuilder setSourceDriveType(String sourceDriveType) {
        this.sourceDriveType = sourceDriveType;
        return this;
    }

    public DlpFileRecordDocumentBuilder setDestinationDriveType(String destinationDriveType) {
        this.destinationDriveType = destinationDriveType;
        return this;
    }

    public DlpFileRecordDocumentBuilder setWasClassified(boolean wasClassified) {
        this.wasClassified = wasClassified;
        return this;
    }

    public DlpFileRecordDocumentBuilder setWasBlocked(boolean wasBlocked) {
        this.wasBlocked = wasBlocked;
        return this;
    }

    public DlpFileRecordDocumentBuilder setMalwareScanResult(String malwareScanResult) {
        this.malwareScanResult = malwareScanResult;
        return this;
    }

    public DlpFileRecordDocumentBuilder setPolicyName(String policyName) {
        this.policyName = policyName;
        return this;
    }

    public DlpFileRecordDocumentBuilder setIsRdp(String isRdp) {
        this.isRdp = isRdp;
        return this;
    }

    public DlpFileRecordDocumentBuilder setIsAdminActivity(String isAdminActivity) {
        this.isAdminActivity = isAdminActivity;
        return this;
    }

    public DlpFileRecordDocumentBuilder setIsRegistryChanged(String isRegistryChanged) {
        this.isRegistryChanged = isRegistryChanged;
        return this;
    }

    public DlpFileRecordDocumentBuilder setUpdateTimestamp(String updateTimestamp) {
        this.updateTimestamp = updateTimestamp;
        return this;
    }

    public DlpFileRecordDocumentBuilder setYearmonthday(String yearmonthday) {
        this.yearmonthday = yearmonthday;
        return this;
    }

    public DlpFileRecordDocument createDlpFileRecordDocument() {
        return new DlpFileRecordDocument(dateTime, dateTimeUnix, eventType, eventId, username, normalizedUsername, fullName, hostname, normalizedSrcMachine, sourceIp, executingApplication, sourcePath, destinationPath, sourceFileName, destinationFileName, fileSize, sourceDriveType, destinationDriveType, wasClassified, wasBlocked, malwareScanResult, policyName, isRdp, isAdminActivity, isRegistryChanged, updateTimestamp, yearmonthday);
    }


    public DlpFileRecordDocument createDlpFileRecordDocument(String[] record) throws ParseException { //todo: temporary just for milestone 1 - delete me after (the fetcher should use the setters to create the document)
        setDateTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(record[0])); //todo: take from configuration
        setDateTimeUnix(Long.parseLong(record[1]));
        setEventType(record[2]);
        setEventId(record[3]);
        setUsername(record[4]);
        setNormalizedUsername(record[5]);
        setFullName(record[6]);
        setHostname(record[7]);
        setNormalizedSrcMachine(record[8]);
        setSourceIp(record[9]);
        setExecutingApplication(record[10]);
        setSourcePath(record[11]);
        setDestinationPath(record[12]);
        setSourceFileName(record[13]);
        setDestinationFileName(record[14]);
        setFileSize(Double.parseDouble(record[15]));
        setSourceDriveType(record[16]);
        setDestinationDriveType(record[17]);
        setWasClassified(Boolean.parseBoolean(record[18]));
        setWasBlocked(Boolean.parseBoolean(record[19]));
        setMalwareScanResult(record[20]);
        setPolicyName(record[21]);
        setIsRdp(record[22]);
        setIsAdminActivity(record[23]);
        setIsRegistryChanged(record[24]);
        setUpdateTimestamp(record[25]);
        setYearmonthday(record[26]);

        return createDlpFileRecordDocument();
    }
}