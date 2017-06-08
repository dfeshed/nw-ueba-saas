package presidio.sdk.api.domain;


import fortscale.common.general.CommonStrings;
import fortscale.domain.core.AbstractAuditableDocument;
import fortscale.utils.time.TimestampUtils;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


@Document(collection = CommonStrings.DLP_FILE_INPUT_COLLECTION_NAME)
public class DlpFileDataDocument extends AbstractAuditableDocument {


    @Field(CommonStrings.DATE_TIME_UNIX_FIELD_NAME)
    protected long dateTimeUnix;

    @Field(CommonStrings.DATE_TIME_FIELD_NAME)
    protected Date dateTime;

    @Field(CommonStrings.EXECUTING_APPLICATION_FIELD_NAME)
    protected String executingApplication;

    @Field(CommonStrings.HOSTNAME_FIELD_NAME)
    protected String hostname;

    @Field(CommonStrings.FIRST_NAME_FIELD_NAME)
    protected String firstName;

    @Field(CommonStrings.LAST_NAME_FIELD_NAME)
    protected String lastName;

    @Field(CommonStrings.USERNAME_FIELD_NAME)
    protected String username;

    @Field(CommonStrings.MALWARE_SCAN_RESULT_FIELD_NAME)
    protected String malwareScanResult;

    @Field(CommonStrings.EVENT_ID_FIELD_NAME)
    protected String eventId;

    @Field(CommonStrings.SOURCE_IP_FIELD_NAME)
    protected String sourceIp;

    @Field(CommonStrings.WAS_BLOCKED_FIELD_NAME)
    protected boolean wasBlocked;

    @Field(CommonStrings.WAS_CLASSIFIED_FIELD_NAME)
    protected boolean wasClassified;

    @Field(CommonStrings.DESTINATION_PATH_FIELD_NAME)
    protected String destinationPath;

    @Field(CommonStrings.DESTINATION_FILE_NAME_FIELD_NAME)
    protected String destinationFileName;

    @Field(CommonStrings.FILE_SIZE_FIELD_NAME)
    protected Double fileSize;

    @Field(CommonStrings.SOURCE_PATH_FIELD_NAME)
    protected String sourcePath;

    @Field(CommonStrings.SOURCE_FILE_NAME_FIELD_NAME)
    protected String sourceFileName;

    @Field(CommonStrings.SOURCE_DRIVE_TYPE_FIELD_NAME)
    protected String sourceDriveType;

    @Field(CommonStrings.DESTINATION_DRIVE_TYPE_FIELD_NAME)
    protected String destinationDriveType;

    @Field(CommonStrings.EVENT_TYPE_FIELD_NAME)
    protected String eventType;


    public DlpFileDataDocument(String[] record) {
        try {
            dateTime = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(record[0]);
        } catch (ParseException e) {
            //todo  create not in Ctor
        }
        dateTimeUnix = TimestampUtils.convertToSeconds(dateTime.getTime());
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

    public Date getDateTime() {
        return dateTime;
    }

    public void setDateTime(Date dateTime) {
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
