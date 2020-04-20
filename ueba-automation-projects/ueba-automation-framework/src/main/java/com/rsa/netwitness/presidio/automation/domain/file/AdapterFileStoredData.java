package com.rsa.netwitness.presidio.automation.domain.file;

import com.google.gson.annotations.Expose;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import presidio.data.domain.event.Event;

import java.time.Instant;

@Document(collection = "input_file_raw_events")
public class AdapterFileStoredData extends Event {

    @Id
    private String id;
    private Instant dateTime;
    @Expose
    private String eventId;
    @Expose
    private String srcFilePath;
    @Expose
    private Boolean isSrcDriveShared;
    @Expose
    private String dstFilePath;
    @Expose
    private Boolean isDstDriveShared;
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
    private String srcMachineName;
    @Expose
    private String srcMachineId;
    @Expose
    private String dstMachineName;
    @Expose
    private String dstMachineId;
    @Expose
    private String userDisplayName;
    @Expose
    private AdditionalInfo additionalInfo;

    private AdapterFileStoredData(){
    }

    private AdapterFileStoredData(Builder builder) {
        this.id = builder.id;
        this.eventId = builder.eventId;
        this.dataSource = builder.dataSource;
        this.userId = builder.userId;
        this.operationType = builder.operationType;
        this.result = builder.result;
        this.userName = builder.userName;
        this.userDisplayName = builder.userDisplayName;
        this.additionalInfo = builder.additionalInfo;
        this.srcFilePath = builder.srcFilePath;
        this.isSrcDriveShared = builder.isSrcDriveShared;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public Instant getDateTime() {
        return dateTime;
    }

    public void setDateTime(Instant dateTime) {
        this.dateTime = dateTime;
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

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getSrcFilePath() {
        return srcFilePath;
    }

    public void setSrcFilePath(String srcFilePath) {
        this.srcFilePath = srcFilePath;
    }

    public String getDstFilePath() {
        return dstFilePath;
    }

    public void setDstFilePath(String dstFilePath) {
        this.dstFilePath = dstFilePath;
    }

    public Boolean isSrcDriveShared() {
        return isSrcDriveShared;
    }

    public void setSrcDriveShared(Boolean srcDriveShared) {
        isSrcDriveShared = srcDriveShared;
    }

    public Boolean isDstDriveShared() {
        return isDstDriveShared;
    }

    public void setDstDriveShared(Boolean dstDriveShared) {
        isDstDriveShared = dstDriveShared;
    }

    public Boolean getSrcDriveShared() {
        return isSrcDriveShared;
    }

    public Boolean getDstDriveShared() {
        return isDstDriveShared;
    }

    public String getSrcMachineName() {
        return srcMachineName;
    }

    public void setSrcMachineName(String srcMachineName) {
        this.srcMachineName = srcMachineName;
    }

    public String getSrcMachineId() {
        return srcMachineId;
    }

    public void setSrcMachineId(String srcMachineId) {
        this.srcMachineId = srcMachineId;
    }

    public String getDstMachineName() {
        return dstMachineName;
    }

    public void setDstMachineName(String dstMachineName) {
        this.dstMachineName = dstMachineName;
    }

    public String getDstMachineId() {
        return dstMachineId;
    }

    public void setDstMachineId(String dstMachineId) {
        this.dstMachineId = dstMachineId;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    @Override
    public String toString() {
        return "AdapterFileStoredData{" +
                "id='" + id + '\'' +
                ", dateTime=" + dateTime +
                ", eventId='" + eventId + '\'' +
                ", srcFilePath='" + srcFilePath + '\'' +
                ", isSrcDriveShared=" + isSrcDriveShared +
                ", dstFilePath='" + dstFilePath + '\'' +
                ", isDstDriveShared=" + isDstDriveShared +
                ", dataSource='" + dataSource + '\'' +
                ", userId='" + userId + '\'' +
                ", operationType='" + operationType + '\'' +
                ", result='" + result + '\'' +
                ", userName='" + userName + '\'' +
                ", srcMachineName='" + srcMachineName + '\'' +
                ", srcMachineId='" + srcMachineId + '\'' +
                ", dstMachineName='" + dstMachineName + '\'' +
                ", dstMachineId='" + dstMachineId + '\'' +
                ", userDisplayName='" + userDisplayName + '\'' +
                ", additionalInfo=" + additionalInfo +
                '}';
    }

    class AdditionalInfo {
        @Expose
        private String originIPv4;

        @Expose
        private String description;

        @Expose
        private String oSVersion;

        @Expose
        private String iPAddress;

        @Expose
        private String domainDN;

        @Expose
        private String fileSystemType;

        @Expose
        private String fileSystemLogonID;

        @Expose
        private String origin;

        @Expose
        private String computer;

        @Expose
        private String isUserAdmin;

        @Expose
        private String operationType;

        public String getOriginIPv4() {
            return originIPv4;
        }

        public void setOriginIPv4(String originIPv4) {
            this.originIPv4 = originIPv4;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
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

        public String getOrigin() {
            return origin;
        }

        public void setOrigin(String origin) {
            this.origin = origin;
        }

        public String getComputer() {
            return computer;
        }

        public void setComputer(String computer) {
            this.computer = computer;
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
                    "originIPv4='" + originIPv4 + '\'' +
                    ", description='" + description + '\'' +
                    ", oSVersion='" + oSVersion + '\'' +
                    ", iPAddress='" + iPAddress + '\'' +
                    ", domainDN='" + domainDN + '\'' +
                    ", fileSystemType='" + fileSystemType + '\'' +
                    ", fileSystemLogonID='" + fileSystemLogonID + '\'' +
                    ", origin='" + origin + '\'' +
                    ", computer='" + computer + '\'' +
                    ", isUserAdmin='" + isUserAdmin + '\'' +
                    ", operationType='" + operationType + '\'' +
                    '}';
        }
    }

    public static class Builder {

        private String id;
        private Instant dateTime;
        private String eventId;
        private String dataSource;
        private String userId;
        private String operationType;
        private String result;
        private String userName;
        private String userDisplayName;
        private AdditionalInfo additionalInfo;
        private String srcFilePath;
        private boolean isSrcDriveShared;

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

        public Builder eventId(String eventId) {
            this.eventId = eventId;
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

        public Builder srcFilePath(String srcFilePath) {
            this.srcFilePath = srcFilePath;
            return this;
        }

        public Builder isSrcDriveShared(boolean isSrcDriveShared) {
            this.isSrcDriveShared = isSrcDriveShared;
            return this;
        }

        public AdapterFileStoredData build() {
            return new AdapterFileStoredData(this);
        }
    }
}
