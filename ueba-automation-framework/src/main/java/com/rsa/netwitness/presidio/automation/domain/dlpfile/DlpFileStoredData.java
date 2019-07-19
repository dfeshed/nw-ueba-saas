package com.rsa.netwitness.presidio.automation.domain.dlpfile;

import com.google.gson.annotations.Expose;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;


@Document(collection = "input_dlpfile_raw_events")
public class DlpFileStoredData{

    @Id
    private String id;

    public Instant dateTime;
    @Expose
    private String executingApplication;
    @Expose
    private String hostname;
    @Expose
    private String firstName;
    @Expose
    private String lastName;
    @Expose
    private String username;
    @Expose
    private String malwareScanResult;
    @Expose
    private String eventId;
    @Expose
    private String sourceIp;
    @Expose
    private boolean wasBlocked;
    @Expose
    private boolean wasClassified;
    @Expose
    private String destinationPath;
    @Expose
    private String destinationFileName;
    @Expose
    private double fileSize;
    @Expose
    private String sourcePath;
    @Expose
    private String sourceFileName;
    @Expose
    private String sourceDriveType;
    @Expose
    private String destinationDriveType;
    @Expose
    private String eventType;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Instant getDateTime() {
        return dateTime;
    }

    public void setDateTime(Instant dateTime) {
        this.dateTime = dateTime;
    }

    public void setFileSize(double fileSize) {
        this.fileSize = fileSize;
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

    public double getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
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

    public String toString() {
        return  executingApplication + ',' +
                hostname + ',' +
                firstName + ',' +
                lastName + ',' +
                username + ',' +
                malwareScanResult + ',' +
                eventId + ',' +
                sourceIp + ',' +
                wasBlocked + ',' +
                wasClassified + ',' +
                destinationPath + ',' +
                destinationFileName + ',' +
                fileSize + ',' +
                sourcePath + ',' +
                sourceFileName + ',' +
                sourceDriveType + ',' +
                destinationDriveType + ',' +
                eventType;
    }
}
