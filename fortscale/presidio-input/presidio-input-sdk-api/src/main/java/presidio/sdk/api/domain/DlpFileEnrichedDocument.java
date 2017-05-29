package presidio.sdk.api.domain;


public class DlpFileEnrichedDocument extends DlpFileDataDocument {

    private final String normalizedUsername;
    private final String normalizedMachineName;


    public DlpFileEnrichedDocument(DlpFileDataDocument record, String normalizedUsername, String normalizedMachineName) {
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
        this.normalizedMachineName = normalizedMachineName;
    }
}
