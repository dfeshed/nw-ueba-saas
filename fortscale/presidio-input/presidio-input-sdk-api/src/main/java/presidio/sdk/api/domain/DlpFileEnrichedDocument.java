package presidio.sdk.api.domain;

import java.time.Instant;

//todo: consider composition over inheritance
public class DlpFileEnrichedDocument extends DlpFileDataDocument {

    private final String normalizedUsername;
    private final String normalizedSrcMachine;

    public DlpFileEnrichedDocument(DlpFileDataDocument record, String normalizedUsername, String normalizedSrcMachine) {
        dateTime = record.getDateTime();
        dateTimeUnix = record.getDateTimeUnix();
        eventType = record.getEventType();
        executingApplication = record.getExecutingApplication();
        hostname = record.getHostname();
        firstName = record.getFirstName();
        lastName = record.getLastName();
        username = record.getUsername();
        malwareScanResult = record.getMalwareScanResult();
        eventId = record.getEventId();
        sourceIp = record.getSourceIp();
        wasBlocked = record.getWasBlocked();
        wasClassified = record.getWasClassified();
        destinationPath = record.getDestinationPath();
        destinationFileName = record.getDestinationFileName();
        fileSize = record.getFileSize();
        sourcePath = record.getSourcePath();
        sourceFileName = record.getSourceFileName();
        sourceDriveType = record.getSourceDriveType();
        destinationDriveType = record.getDestinationDriveType();
        this.normalizedUsername = normalizedUsername;
        this.normalizedSrcMachine = normalizedSrcMachine;
    }

    public DlpFileEnrichedDocument(Instant dateTime, long dateTimeUnix, String executingApplication, String hostname,
                                   String firstName, String lastName, String username, String malwareScanResult,
                                   String eventId, String sourceIp, boolean wasBlocked, boolean wasClassified,
                                   String destinationPath, String destinationFileName, Double fileSize,
                                   String sourcePath, String sourceFileName, String sourceDriveType,
                                   String destinationDriveType, String eventType, String normalizedUsername,
                                   String normalizedSrcMachine) {
        super(dateTime, dateTimeUnix, executingApplication, hostname, firstName, lastName, username, malwareScanResult, eventId, sourceIp, wasBlocked, wasClassified, destinationPath, destinationFileName, fileSize, sourcePath, sourceFileName, sourceDriveType, destinationDriveType, eventType);
        this.normalizedUsername = normalizedUsername;
        this.normalizedSrcMachine = normalizedSrcMachine;
    }

    public String getNormalizedUsername() {
        return normalizedUsername;
    }

    public String getNormalizedSrcMachine() {
        return normalizedSrcMachine;
    }

    @Override
    public String toString() {
        return "DlpFileEnrichedDocument{" +
                "normalizedUsername='" + normalizedUsername + '\'' +
                ", normalizedSrcMachine='" + normalizedSrcMachine + '\'' +
                ", dateTimeUnix=" + dateTimeUnix +
                ", dateTime=" + dateTime +
                ", executingApplication='" + executingApplication + '\'' +
                ", hostname='" + hostname + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", username='" + username + '\'' +
                ", malwareScanResult='" + malwareScanResult + '\'' +
                ", eventId='" + eventId + '\'' +
                ", sourceIp='" + sourceIp + '\'' +
                ", wasBlocked=" + wasBlocked +
                ", wasClassified=" + wasClassified +
                ", destinationPath='" + destinationPath + '\'' +
                ", destinationFileName='" + destinationFileName + '\'' +
                ", fileSize=" + fileSize +
                ", sourcePath='" + sourcePath + '\'' +
                ", sourceFileName='" + sourceFileName + '\'' +
                ", sourceDriveType='" + sourceDriveType + '\'' +
                ", destinationDriveType='" + destinationDriveType + '\'' +
                ", eventType='" + eventType + '\'' +
                '}';
    }
}
