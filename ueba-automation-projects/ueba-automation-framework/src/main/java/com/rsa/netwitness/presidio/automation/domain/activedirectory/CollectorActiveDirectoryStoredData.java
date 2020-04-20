package com.rsa.netwitness.presidio.automation.domain.activedirectory;

import com.google.gson.annotations.Expose;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "ca_active_directory_events")
public class CollectorActiveDirectoryStoredData {
    @Id
    private String id;

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

    private Instant timeDetected;

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

    private Instant timeBatched;

    @Expose
    private String to;

    @Expose
    private String organizationalUnit;

    @Expose
    private String serverOU;

    @Expose
    private String oSVersion;

    @Expose
    private String objectClass;

    @Expose
    private String objectName;

    @Expose
    private String objectId;

    @Expose
    private String attributeName;

    @Expose
    private String objectDN;

    @Expose
    private String objectCanonical;

    @Expose
    private String samAccountName;

    @Expose
    private String userPrincipalName;

    @Expose
    private String facility;

    private String InitiatorSid;
    private String InitiatorUserName;
    private String InitiatorMail;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public Instant getTimeDetected() {
        return timeDetected;
    }

    public void setTimeDetected(Instant timeDetected) {
        this.timeDetected = timeDetected;
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

    public Instant getTimeBatched() {
        return timeBatched;
    }

    public void setTimeBatched(Instant timeBatched) {
        this.timeBatched = timeBatched;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getOrganizationalUnit() {
        return organizationalUnit;
    }

    public void setOrganizationalUnit(String organizationalUnit) {
        this.organizationalUnit = organizationalUnit;
    }

    public String getServerOU() {
        return serverOU;
    }

    public void setServerOU(String serverOU) {
        this.serverOU = serverOU;
    }

    public String getoSVersion() {
        return oSVersion;
    }

    public void setoSVersion(String oSVersion) {
        this.oSVersion = oSVersion;
    }

    public String getObjectClass() {
        return objectClass;
    }

    public void setObjectClass(String objectClass) {
        this.objectClass = objectClass;
    }

    public String getObjectName() {
        return objectName;
    }

    public void setObjectName(String objectName) {
        this.objectName = objectName;
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public String getAttributeName() {
        return attributeName;
    }

    public void setAttributeName(String attributeName) {
        this.attributeName = attributeName;
    }

    public String getObjectDN() {
        return objectDN;
    }

    public void setObjectDN(String objectDN) {
        this.objectDN = objectDN;
    }

    public String getObjectCanonical() {
        return objectCanonical;
    }

    public void setObjectCanonical(String objectCanonical) {
        this.objectCanonical = objectCanonical;
    }

    public String getSamAccountName() {
        return samAccountName;
    }

    public void setSamAccountName(String samAccountName) {
        this.samAccountName = samAccountName;
    }

    public String getUserPrincipalName() {
        return userPrincipalName;
    }

    public void setUserPrincipalName(String userPrincipalName) {
        this.userPrincipalName = userPrincipalName;
    }

    public String getFacility() {
        return facility;
    }

    public void setFacility(String facility) {
        this.facility = facility;
    }

    public String getInitiatorSid() {
        return InitiatorSid;
    }

    public void setInitiatorSid(String initiatorSid) {
        InitiatorSid = initiatorSid;
    }

    public String getInitiatorUserName() {
        return InitiatorUserName;
    }

    public void setInitiatorUserName(String initiatorUserName) {
        InitiatorUserName = initiatorUserName;
    }

    public String getInitiatorMail() {
        return InitiatorMail;
    }

    public void setInitiatorMail(String initiatorMail) {
        InitiatorMail = initiatorMail;
    }

    public String getAdministrator() {
        return administrator;
    }

    public void setAdministrator(String administrator) {
        this.administrator = administrator;
    }

    @Override
    public String toString() {
        return "CollectorActiveDirectoryStoredData{" +
                "id='" + id + '\'' +
                ", subsystem='" + subsystem + '\'' +
                ", event='" + event + '\'' +
                ", severity='" + severity + '\'' +
                ", action='" + action + '\'' +
                ", result='" + result + '\'' +
                ", timeDetected=" + timeDetected +
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
                ", timeBatched=" + timeBatched +
                ", to='" + to + '\'' +
                ", organizationalUnit='" + organizationalUnit + '\'' +
                ", serverOU='" + serverOU + '\'' +
                ", oSVersion='" + oSVersion + '\'' +
                ", objectClass='" + objectClass + '\'' +
                ", objectName='" + objectName + '\'' +
                ", objectId='" + objectId + '\'' +
                ", attributeName='" + attributeName + '\'' +
                ", objectDN='" + objectDN + '\'' +
                ", objectCanonical='" + objectCanonical + '\'' +
                ", samAccountName='" + samAccountName + '\'' +
                ", userPrincipalName='" + userPrincipalName + '\'' +
                ", facility='" + facility + '\'' +
                ", InitiatorSid='" + InitiatorSid + '\'' +
                ", InitiatorUserName='" + InitiatorUserName + '\'' +
                ", InitiatorMail='" + InitiatorMail + '\'' +
                '}';
    }
}
