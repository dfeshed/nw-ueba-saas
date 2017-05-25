package presidio.sdk.api.domain;


import fortscale.domain.core.AbstractAuditableDocument;
import fortscale.utils.logging.Logger;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


@Document(collection=DlpFileDataDocument.COLLECTION_NAME)
public class DlpFileDataDocument extends AbstractAuditableDocument {

    private static final Logger logger = Logger.getLogger(DlpFileDataDocument.class);


    public static final String COLLECTION_NAME = "dlpfile_stored_data";

    public static final String DATE_TIME_UNIX_FIELD_NAME="dateTimeUnix";
    public static final String DATE_TIME_FIELD_NAME="dateTime";
    public static final String EXECUTING_APPLICATION_FIELD_NAME="executingApplication";
    public static final String HOSTNAME_FIELD_NAME="hostname";
    public static final String FIRST_NAME_FIELD_NAME="firstName";
    public static final String LAST_NAME_FIELD_NAME="lastName";
    public static final String USERNAME_FIELD_NAME="username";
    public static final String MALWARE_SCAN_RESULT_FIELD_NAME="malwareScanResult";
    public static final String EVENT_ID_FIELD_NAME="eventId";
    public static final String SOURCE_IP_FIELD_NAME="sourceIp";
    public static final String WAS_BLOCKED_FIELD_NAME="wasBlocked";
    public static final String WAS_CLASSIFIED_FIELD_NAME="wasClassified";
    public static final String DESTINATION_PATH_FIELD_NAME="destinationPath";
    public static final String DESTINATION_FILE_NAME_FIELD_NAME="destinationFileName";
    public static final String FILE_SIZE_FIELD_NAME="fileSize";
    public static final String SOURCE_PATH_FIELD_NAME="sourcePath";
    public static final String SOURCE_FILE_NAME_FIELD_NAME="sourceFileName";
    public static final String SOURCE_DRIVE_TYPE_FIELD_NAME="sourceDriveType";
    public static final String DESTINATION_DRIVE_TYPE_FIELD_NAME="destinationDriveType";
    public static final String EVENT_TYPE_FIELD_NAME="eventType";


    @Field(DATE_TIME_UNIX_FIELD_NAME)
    private long dateTimeUnix;

    @Field(DATE_TIME_FIELD_NAME)
    private Date dateTime;

    @Field(EXECUTING_APPLICATION_FIELD_NAME)
    private String executingApplication;

    @Field(HOSTNAME_FIELD_NAME)
    private String hostname;

    @Field(FIRST_NAME_FIELD_NAME)
    private String firstName;

    @Field(LAST_NAME_FIELD_NAME)
    private String lastName;

    @Field(USERNAME_FIELD_NAME)
    private String username;

    @Field(MALWARE_SCAN_RESULT_FIELD_NAME)
    private String malwareScanResult;

    @Field(EVENT_ID_FIELD_NAME)
    private String eventId;

    @Field(SOURCE_IP_FIELD_NAME)
    private String sourceIp;

    @Field(WAS_BLOCKED_FIELD_NAME)
    private boolean wasBlocked;

    @Field(WAS_CLASSIFIED_FIELD_NAME)
    private boolean wasClassified;

    @Field(DESTINATION_PATH_FIELD_NAME)
    private String destinationPath;

    @Field(DESTINATION_FILE_NAME_FIELD_NAME)
    private String destinationFileName;

    @Field(FILE_SIZE_FIELD_NAME)
    private Double fileSize;

    @Field(SOURCE_PATH_FIELD_NAME)
    private String sourcePath;

    @Field(SOURCE_FILE_NAME_FIELD_NAME)
    private String sourceFileName;

    @Field(SOURCE_DRIVE_TYPE_FIELD_NAME)
    private String sourceDriveType;

    @Field(DESTINATION_DRIVE_TYPE_FIELD_NAME)
    private String destinationDriveType;

    @Field(EVENT_TYPE_FIELD_NAME)
    private String eventType;

    public DlpFileDataDocument(String[] record) {
        try {
            dateTime = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(record[0]);
        } catch (ParseException e) {
            logger.error("Failed to create DlpFileDataDocument. Bad date: {}. Format should be yyyy-MM-dd hh:mm:ss", record[0], e); //todo remove this. create not in Ctor
        }
        dateTimeUnix = dateTime.getTime();
        eventType = record[1];
        executingApplication = record[2];
        hostname = record[3];
        firstName = record[4];
        lastName = record[5];
        username = record[6];
        malwareScanResult = record[7];
        eventId = record[8];
        sourceIp = record[9];
        wasBlocked = Boolean.getBoolean(record[10]);
        wasClassified = Boolean.getBoolean(record[11]);
        destinationPath = record[12];
        destinationFileName = record[13];
        fileSize = Double.parseDouble(record[14]);
        sourcePath = record[15];
        sourceFileName = record[16];
        sourceDriveType = record[17];
        destinationDriveType = record[18];

    }

    public Date getDateTime() {
        return dateTime;
    }

    public long getDateTimeUnix() {
        return dateTimeUnix;
    }

    public String getExecutingApplication() {
        return executingApplication;
    }

    public String getHostname() {
        return hostname;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getUsername() {
        return username;
    }

    public String getMalwareScanResult() {
        return malwareScanResult;
    }

    public String getEventId() {
        return eventId;
    }

    public String getSourceIp() {
        return sourceIp;
    }

    public Boolean getWasBlocked() {
        return wasBlocked;
    }

    public Boolean getWasClassified() {
        return wasClassified;
    }

    public String getDestinationPath() {
        return destinationPath;
    }

    public String getDestinationFileName() {
        return destinationFileName;
    }

    public Double getFileSize() {
        return fileSize;
    }

    public String getSourcePath() {
        return sourcePath;
    }

    public String getSourceFileName() {
        return sourceFileName;
    }

    public void setDateTimeUnix(long dateTimeUnix) {
        this.dateTimeUnix = dateTimeUnix;
    }

    public void setDateTime(Date dateTime) {
        this.dateTime = dateTime;
    }

    public void setExecutingApplication(String executingApplication) {
        this.executingApplication = executingApplication;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setMalwareScanResult(String malwareScanResult) {
        this.malwareScanResult = malwareScanResult;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public void setSourceIp(String sourceIp) {
        this.sourceIp = sourceIp;
    }

    public void setWasBlocked(Boolean wasBlocked) {
        this.wasBlocked = wasBlocked;
    }

    public void setWasClassified(Boolean wasClassified) {
        this.wasClassified = wasClassified;
    }

    public void setDestinationPath(String destinationPath) {
        this.destinationPath = destinationPath;
    }

    public void setDestinationFileName(String destinationFileName) {
        this.destinationFileName = destinationFileName;
    }

    public void setFileSize(Double fileSize) {
        this.fileSize = fileSize;
    }

    public void setSourcePath(String sourcePath) {
        this.sourcePath = sourcePath;
    }

    public void setSourceFileName(String sourceFileName) {
        this.sourceFileName = sourceFileName;
    }

    public void setSourceDriveType(String sourceDriveType) {
        this.sourceDriveType = sourceDriveType;
    }

    public void setDestinationDriveType(String destinationDriveType) {
        this.destinationDriveType = destinationDriveType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getSourceDriveType() {

        return sourceDriveType;
    }

    public String getDestinationDriveType() {
        return destinationDriveType;
    }

    public String getEventType() {
        return eventType;
    }


}
