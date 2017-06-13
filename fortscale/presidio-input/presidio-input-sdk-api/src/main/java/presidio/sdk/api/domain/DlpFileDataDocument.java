package presidio.sdk.api.domain;


import fortscale.domain.core.AbstractAuditableDocument;
import fortscale.utils.logging.Logger;
import fortscale.utils.time.TimestampUtils;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;

import static presidio.sdk.api.domain.DlpFileDataDocument.COLLECTION_NAME;


@Document(collection = COLLECTION_NAME)
public class DlpFileDataDocument extends AbstractAuditableDocument {

    public static final String COLLECTION_NAME = "dlpfile_stored_data";
    public static final String DATE_TIME_UNIX_FIELD_NAME = "dateTimeUnix";
    public static final String DATE_TIME_FIELD_NAME = "dateTime";
    public static final String EXECUTING_APPLICATION_FIELD_NAME = "executingApplication";
    public static final String HOSTNAME_FIELD_NAME = "hostname";
    public static final String FIRST_NAME_FIELD_NAME = "firstName";
    public static final String LAST_NAME_FIELD_NAME = "lastName";
    public static final String USERNAME_FIELD_NAME = "username";
    public static final String MALWARE_SCAN_RESULT_FIELD_NAME = "malwareScanResult";
    public static final String EVENT_ID_FIELD_NAME = "eventId";
    public static final String SOURCE_IP_FIELD_NAME = "sourceIp";
    public static final String WAS_BLOCKED_FIELD_NAME = "wasBlocked";
    public static final String WAS_CLASSIFIED_FIELD_NAME = "wasClassified";
    public static final String DESTINATION_PATH_FIELD_NAME = "destinationPath";
    public static final String DESTINATION_FILE_NAME_FIELD_NAME = "destinationFileName";
    public static final String FILE_SIZE_FIELD_NAME = "fileSize";
    public static final String SOURCE_PATH_FIELD_NAME = "sourcePath";
    public static final String SOURCE_FILE_NAME_FIELD_NAME = "sourceFileName";
    public static final String SOURCE_DRIVE_TYPE_FIELD_NAME = "sourceDriveType";
    public static final String DESTINATION_DRIVE_TYPE_FIELD_NAME = "destinationDriveType";
    public static final String EVENT_TYPE_FIELD_NAME = "eventType";
    private static final Logger logger = Logger.getLogger(DlpFileDataDocument.class);
    @Field(DATE_TIME_UNIX_FIELD_NAME)
    protected long dateTimeUnix;

    @Field(DATE_TIME_FIELD_NAME)
    protected Instant dateTime;

    @Field(EXECUTING_APPLICATION_FIELD_NAME)
    protected String executingApplication;

    @Field(HOSTNAME_FIELD_NAME)
    protected String hostname;

    @Field(FIRST_NAME_FIELD_NAME)
    protected String firstName;

    @Field(LAST_NAME_FIELD_NAME)
    protected String lastName;

    @Field(USERNAME_FIELD_NAME)
    protected String username;

    @Field(MALWARE_SCAN_RESULT_FIELD_NAME)
    protected String malwareScanResult;

    @Field(EVENT_ID_FIELD_NAME)
    protected String eventId;

    @Field(SOURCE_IP_FIELD_NAME)
    protected String sourceIp;

    @Field(WAS_BLOCKED_FIELD_NAME)
    protected boolean wasBlocked;

    @Field(WAS_CLASSIFIED_FIELD_NAME)
    protected boolean wasClassified;

    @Field(DESTINATION_PATH_FIELD_NAME)
    protected String destinationPath;

    @Field(DESTINATION_FILE_NAME_FIELD_NAME)
    protected String destinationFileName;

    @Field(FILE_SIZE_FIELD_NAME)
    protected Double fileSize;

    @Field(SOURCE_PATH_FIELD_NAME)
    protected String sourcePath;

    @Field(SOURCE_FILE_NAME_FIELD_NAME)
    protected String sourceFileName;

    @Field(SOURCE_DRIVE_TYPE_FIELD_NAME)
    protected String sourceDriveType;

    @Field(DESTINATION_DRIVE_TYPE_FIELD_NAME)
    protected String destinationDriveType;

    @Field(EVENT_TYPE_FIELD_NAME)
    protected String eventType;


    public DlpFileDataDocument(String[] record) {
        dateTime = Instant.parse(record[0]);
        dateTimeUnix = dateTime.getEpochSecond();
        eventType = record[1];
        executingApplication = record[2];
        hostname = record[3];
        firstName = record[4];
        lastName = record[5];
        username = record[6];
        malwareScanResult = record[7];
        eventId = record[8];
        sourceIp = record[9];
        wasBlocked = Boolean.valueOf(record[10]);
        wasClassified = Boolean.valueOf(record[11]);
        destinationPath = record[12];
        destinationFileName = record[13];
        fileSize = Double.parseDouble(record[14]);
        sourcePath = record[15];
        sourceFileName = record[16];
        sourceDriveType = record[17];
        destinationDriveType = record[18];

    }

    protected DlpFileDataDocument() {

    }

    public Instant getDateTime() {
        return dateTime;
    }

    public void setDateTime(Instant dateTime) {
        this.dateTime = dateTime;
    }

    public long getDateTimeUnix() {
        return dateTimeUnix;
    }

    public void setDateTimeUnix(long dateTimeUnix) {
        this.dateTimeUnix = dateTimeUnix;
    }

    public String getExecutingApplication() {
        return executingApplication;
    }

    public void setExecutingApplication(String executingApplication) {
        this.executingApplication = executingApplication;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getMalwareScanResult() {
        return malwareScanResult;
    }

    public void setMalwareScanResult(String malwareScanResult) {
        this.malwareScanResult = malwareScanResult;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getSourceIp() {
        return sourceIp;
    }

    public void setSourceIp(String sourceIp) {
        this.sourceIp = sourceIp;
    }

    public Boolean getWasBlocked() {
        return wasBlocked;
    }

    public void setWasBlocked(Boolean wasBlocked) {
        this.wasBlocked = wasBlocked;
    }

    public Boolean getWasClassified() {
        return wasClassified;
    }

    public void setWasClassified(Boolean wasClassified) {
        this.wasClassified = wasClassified;
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

    public Double getFileSize() {
        return fileSize;
    }

    public void setFileSize(Double fileSize) {
        this.fileSize = fileSize;
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

    public String getDestinationDriveType() {
        return destinationDriveType;
    }

    public void setDestinationDriveType(String destinationDriveType) {
        this.destinationDriveType = destinationDriveType;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    @Override
    public String toString() {
        return "DlpFileDataDocument{" +
                "dateTimeUnix=" + dateTimeUnix +
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
