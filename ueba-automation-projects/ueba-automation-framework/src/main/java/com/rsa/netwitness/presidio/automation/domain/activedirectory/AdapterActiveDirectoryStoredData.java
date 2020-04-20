package com.rsa.netwitness.presidio.automation.domain.activedirectory;

import com.google.gson.annotations.Expose;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import presidio.data.domain.event.Event;

import java.time.Instant;

@Document(collection = "input_active_directory_raw_events")
public class AdapterActiveDirectoryStoredData extends Event {

    @Id
    private String id;
    private Instant dateTime;

    @Expose
    private boolean isUserAdmin;

    @Expose
    private String eventId;

    @Expose
    private String objectId;

    @Expose
    private String dataSource;

    @Expose
    private String userId;

    @Expose
    private String operationType;

    @Expose
    private String result;

    @Expose
    private String userName;

    @Expose
    private String userDisplayName;

    @Expose
    private AdditionalInfo additionalInfo;

    private AdapterActiveDirectoryStoredData(){
    }

    private AdapterActiveDirectoryStoredData(Builder builder) {
        this.id = builder.id;
        this.eventId = builder.eventId;
        this.isUserAdmin = builder.isUserAdmin;
        this.eventId = builder.eventId;
        this.objectId = builder.objectId;
        this.dataSource = builder.dataSource;
        this.userId = builder.userId;
        this.operationType = builder.operationType;
        this.result = builder.result;
        this.userName = builder.userName;
        this.userDisplayName = builder.userDisplayName;
        this.additionalInfo = builder.additionalInfo;
    }

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

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDataSource() {
        return dataSource;
    }

    public void setDataSource(String dataSource) {
        this.dataSource = dataSource;
    }

    public String getOperationType() {
        return operationType;
    }

    public void setOperationType(String operationType) {
        this.operationType = operationType;
    }

    public boolean isUserAdmin() {
        return isUserAdmin;
    }

    public void setUserAdmin(boolean userAdmin) {
        isUserAdmin = userAdmin;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserDisplayName() {
        return userDisplayName;
    }

    public void setUserDisplayName(String userDisplayName) {
        this.userDisplayName = userDisplayName;
    }

    public AdditionalInfo getAdditionalInfo() {
        return additionalInfo;
    }

    public void setAdditionalInfo(AdditionalInfo additionalInfo) {
        this.additionalInfo = additionalInfo;
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    @Override
    public String toString() {
        return "AdapterActiveDirectoryStoredData{" +
                "id='" + id + '\'' +
                ", dateTime=" + dateTime +
                ", userId='" + userId + '\'' +
                ", dataSource='" + dataSource + '\'' +
                ", operationType='" + operationType + '\'' +
                ", isUserAdmin=" + isUserAdmin +
                ", result='" + result + '\'' +
                ", userName='" + userName + '\'' +
                ", userDisplayName='" + userDisplayName + '\'' +
                ", additionalInfo=" + additionalInfo +
                ", objectId='" + objectId + '\'' +
                ", eventId='" + eventId + '\'' +
                '}';
    }

    class AdditionalInfo {
        @Expose
        private String result;

        @Expose
        private String origin;

        @Expose
        private String originIPv4;

        @Expose
        private String to;

        @Expose
        private String description;

        @Expose
        private String computer;

        @Expose
        private String oSVersion;

        @Expose
        private String iPAddress;

        @Expose
        private String domainDN;

        @Expose
        private String objectDN;

        @Expose
        private String action;

        @Expose
        private String isUserAdmin;

        @Expose
        private String operationType;

        public String getResult() {
            return result;
        }

        public void setResult(String result) {
            this.result = result;
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

        public String getTo() {
            return to;
        }

        public void setTo(String to) {
            this.to = to;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getComputer() {
            return computer;
        }

        public void setComputer(String computer) {
            this.computer = computer;
        }

        public String getoSVersion() {
            return oSVersion;
        }

        public void setoSVersion(String oSVersion) {
            this.oSVersion = oSVersion;
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

        public String getObjectDN() {
            return objectDN;
        }

        public void setObjectDN(String objectDN) {
            this.objectDN = objectDN;
        }

        public String getAction() {
            return action;
        }

        public void setAction(String action) {
            this.action = action;
        }

        public String getIsUserAdmin() {
            return isUserAdmin;
        }

        public void setIsUserAdmin(String isUserAdmin) {
            this.isUserAdmin = isUserAdmin;
        }

        public String getOperationType() {
            return operationType;
        }

        public void setOperationType(String operationType) {
            this.operationType = operationType;
        }

        @Override
        public String toString() {
            return "AdditionalInfo{" +
                    "result='" + result + '\'' +
                    ", origin='" + origin + '\'' +
                    ", originIPv4='" + originIPv4 + '\'' +
                    ", to='" + to + '\'' +
                    ", description='" + description + '\'' +
                    ", computer='" + computer + '\'' +
                    ", oSVersion='" + oSVersion + '\'' +
                    ", iPAddress='" + iPAddress + '\'' +
                    ", domainDN='" + domainDN + '\'' +
                    ", objectDN='" + objectDN + '\'' +
                    ", action='" + action + '\'' +
                    ", isUserAdmin='" + isUserAdmin + '\'' +
                    ", operationType='" + operationType + '\'' +
                    '}';
        }
    }


    public static class Builder {

        private String id;
        private Instant dateTime;
        private boolean isUserAdmin;
        private String eventId;
        private String objectId;
        private String dataSource;
        private String userId;
        private String operationType;
        private String result;
        private String userName;
        private String userDisplayName;
        private AdditionalInfo additionalInfo;

        public Builder() {

        }

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder dateTime(Instant dateTime) {
            this.dateTime = dateTime;
            return this;
        }

        public Builder userAdmin(boolean userAdmin) {
            isUserAdmin = userAdmin;
            return this;
        }

        public Builder eventId(String eventId) {
            this.eventId = eventId;
            return this;
        }

        public Builder objectId(String objectId) {
            this.objectId = objectId;
            return this;
        }

        public Builder dataSource(String dataSource) {
            this.dataSource = dataSource;
            return this;
        }

        public Builder userId(String userId) {
            this.userId = userId;
            return this;
        }

        public Builder operationType(String operationType) {
            this.operationType = operationType;
            return this;
        }

        public Builder result(String result) {
            this.result = result;
            return this;
        }

        public Builder userName(String userName) {
            this.userName = userName;
            return this;
        }

        public Builder userDisplayName(String userDisplayName) {
            this.userDisplayName = userDisplayName;
            return this;
        }

        public Builder additionalInfo(AdditionalInfo additionalInfo) {
            this.additionalInfo = additionalInfo;
            return this;
        }

        public AdapterActiveDirectoryStoredData build() {
            return new AdapterActiveDirectoryStoredData(this);
        }
    }
}
