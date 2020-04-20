package com.rsa.netwitness.presidio.automation.domain.file;

import com.google.gson.annotations.Expose;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "ca_file_events")
public class CollectorFileStoredData {
    @Id
    private String id;
    @Expose
    private String folderPath;
    @Expose
    private String fileName;
    @Expose
    private String oSVersion;
    @Expose
    private String fileSystemAttribute;
    @Expose
    private String fileSystemType;
    @Expose
    private String fileSystemLogonID;
    @Expose
    private String fileSystemPrimarySID;
    @Expose
    private String processName;
    @Expose
    private String from;
    @Expose
    private String to;
    @Expose
    private String subsystem;
    @Expose
    private String event;
    @Expose
    private String severity;
    @Expose
    private String action;
    @Expose
    private String result;
    @Expose
    private String timeZoneOffset;
    @Expose
    private String userSID;
    @Expose
    private String user;
    @Expose
    private String userDisplay;
    @Expose
    private String origin;
    @Expose
    private String originIPv4;
    @Expose
    private String originIPv6;
    @Expose
    private String userMail;
    @Expose
    private String description;
    @Expose
    private String comment;
    @Expose
    private String serverDN;
    @Expose
    private String serverFQDN;
    @Expose
    private String computer;
    @Expose
    private String iPAddress;
    @Expose
    private String domainDN;
    @Expose
    private String domainFQDN;
    @Expose
    private String domain;
    @Expose
    private String administrator;

    private Instant timeDetected;

    public Instant getTimeDetected() {
        return timeDetected;
    }

    public void setTimeDetected(Instant timeDetected) {
        this.timeDetected = timeDetected;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFolderPath() {
        return folderPath;
    }

    public void setFolderPath(String folderPath) {
        this.folderPath = folderPath;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getoSVersion() {
        return oSVersion;
    }

    public void setoSVersion(String oSVersion) {
        this.oSVersion = oSVersion;
    }

    public String getFileSystemAttribute() {
        return fileSystemAttribute;
    }

    public void setFileSystemAttribute(String fileSystemAttribute) {
        this.fileSystemAttribute = fileSystemAttribute;
    }

    public String getFileSystemType() {
        return fileSystemType;
    }

    public void setFileSystemType(String fileSystemType) {
        this.fileSystemType = fileSystemType;
    }

    public String getFileSystemLogonID() {
        return fileSystemLogonID;
    }

    public void setFileSystemLogonID(String fileSystemLogonID) {
        this.fileSystemLogonID = fileSystemLogonID;
    }

    public String getFileSystemPrimarySID() {
        return fileSystemPrimarySID;
    }

    public void setFileSystemPrimarySID(String fileSystemPrimarySID) {
        this.fileSystemPrimarySID = fileSystemPrimarySID;
    }

    public String getProcessName() {
        return processName;
    }

    public void setProcessName(String processName) {
        this.processName = processName;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getSubsystem() {
        return subsystem;
    }

    public void setSubsystem(String subsystem) {
        this.subsystem = subsystem;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getTimeZoneOffset() {
        return timeZoneOffset;
    }

    public void setTimeZoneOffset(String timeZoneOffset) {
        this.timeZoneOffset = timeZoneOffset;
    }

    public String getUserSID() {
        return userSID;
    }

    public void setUserSID(String userSID) {
        this.userSID = userSID;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getUserDisplay() {
        return userDisplay;
    }

    public void setUserDisplay(String userDisplay) {
        this.userDisplay = userDisplay;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getOriginIPv4() {
        return originIPv4;
    }

    public void setOriginIPv4(String originIPv4) {
        this.originIPv4 = originIPv4;
    }

    public String getOriginIPv6() {
        return originIPv6;
    }

    public void setOriginIPv6(String originIPv6) {
        this.originIPv6 = originIPv6;
    }

    public String getUserMail() {
        return userMail;
    }

    public void setUserMail(String userMail) {
        this.userMail = userMail;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getServerDN() {
        return serverDN;
    }

    public void setServerDN(String serverDN) {
        this.serverDN = serverDN;
    }

    public String getServerFQDN() {
        return serverFQDN;
    }

    public void setServerFQDN(String serverFQDN) {
        this.serverFQDN = serverFQDN;
    }

    public String getComputer() {
        return computer;
    }

    public void setComputer(String computer) {
        this.computer = computer;
    }

    public String getiPAddress() {
        return iPAddress;
    }

    public void setiPAddress(String iPAddress) {
        this.iPAddress = iPAddress;
    }

    public String getDomainDN() {
        return domainDN;
    }

    public void setDomainDN(String domainDN) {
        this.domainDN = domainDN;
    }

    public String getDomainFQDN() {
        return domainFQDN;
    }

    public void setDomainFQDN(String domainFQDN) {
        this.domainFQDN = domainFQDN;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getAdministrator() {
        return administrator;
    }

    public void setAdministrator(String administrator) {
        this.administrator = administrator;
    }

    @Override
    public String toString() {
        return "CollectorFileStoredData{" +
                "id='" + id + '\'' +
                ", folderPath='" + folderPath + '\'' +
                ", fileName='" + fileName + '\'' +
                ", oSVersion='" + oSVersion + '\'' +
                ", fileSystemAttribute='" + fileSystemAttribute + '\'' +
                ", fileSystemType='" + fileSystemType + '\'' +
                ", fileSystemLogonID='" + fileSystemLogonID + '\'' +
                ", fileSystemPrimarySID='" + fileSystemPrimarySID + '\'' +
                ", processName='" + processName + '\'' +
                ", from='" + from + '\'' +
                ", to='" + to + '\'' +
                ", subsystem='" + subsystem + '\'' +
                ", event='" + event + '\'' +
                ", severity='" + severity + '\'' +
                ", action='" + action + '\'' +
                ", result='" + result + '\'' +
                ", timeZoneOffset='" + timeZoneOffset + '\'' +
                ", userSID='" + userSID + '\'' +
                ", user='" + user + '\'' +
                ", userDisplay='" + userDisplay + '\'' +
                ", origin='" + origin + '\'' +
                ", originIPv4='" + originIPv4 + '\'' +
                ", originIPv6='" + originIPv6 + '\'' +
                ", userMail='" + userMail + '\'' +
                ", description='" + description + '\'' +
                ", comment='" + comment + '\'' +
                ", serverDN='" + serverDN + '\'' +
                ", serverFQDN='" + serverFQDN + '\'' +
                ", computer='" + computer + '\'' +
                ", iPAddress='" + iPAddress + '\'' +
                ", domainDN='" + domainDN + '\'' +
                ", domainFQDN='" + domainFQDN + '\'' +
                ", domain='" + domain + '\'' +
                ", administrator='" + administrator + '\'' +
                '}';
    }
}
