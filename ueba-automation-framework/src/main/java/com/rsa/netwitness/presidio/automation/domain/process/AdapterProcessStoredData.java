package com.rsa.netwitness.presidio.automation.domain.process;

import com.google.gson.annotations.Expose;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import presidio.data.domain.event.Event;

import java.time.Instant;

@Document(collection = "input_process_raw_events")
public class AdapterProcessStoredData extends Event {

    @Id
    private String id;
    private Instant dateTime;
    @Expose
    private String eventId;
    @Expose
    private String machineName;
    @Expose
    private String machineId;
    @Expose
    private String srcProcessDirectory;
    @Expose
    private String srcProcessFileName;
    @Expose
    private String[] srcProcessDirectoryGroups;
    @Expose
    private String[] srcProcessCategories;
    @Expose
    private String srcProcessCertificateIssuer;
    @Expose
    private String dstProcessDirectory;
    @Expose
    private String dstProcessFileName;
    @Expose
    private String userId;
    @Expose
    private String userName;
    @Expose
    private String userDisplayName;
    @Expose
    private String dataSource;
    @Expose
    private String operationType;
    @Expose
    private AdditionalInfo additionalInfo;

    private AdapterProcessStoredData(){
    }

    @Override
    public String toString() {
        return "AdapterProcessStoredData{" +
                "id='" + id + '\'' +
                ", dateTime=" + dateTime +
                ", eventId='" + eventId + '\'' +
                ", machineName='" + machineName + '\'' +
                ", machineId='" + machineId + '\'' +
                ", srcProcessDirectory='" + srcProcessDirectory + '\'' +
                ", srcProcessFileName='" + srcProcessFileName + '\'' +
                ", srcProcessCertificateIssuer='" + srcProcessCertificateIssuer + '\'' +
                ", dstProcessDirectory='" + dstProcessDirectory + '\'' +
                ", dstProcessFileName='" + dstProcessFileName + '\'' +
                ", userId='" + userId + '\'' +
                ", userName='" + userName + '\'' +
                ", userDisplayName='" + userDisplayName + '\'' +
                ", dataSource='" + dataSource + '\'' +
                ", operationType='" + operationType + '\'' +
                ", additionalInfo=" + ((additionalInfo != null) ? additionalInfo.toString() : "") +
                '}';
    }

    @Override
    public Instant getDateTime() {
        return this.dateTime;
    }

    private AdapterProcessStoredData(Builder builder) {
        this.id = builder.id;
        this.dateTime = builder.dateTime;
        this.eventId = builder.eventId;
        this.srcProcessFileName = builder.srcProcessFileName;
        this.machineName = builder.machineName;
        this.machineId = builder.machineId;
        this.srcProcessDirectory = builder.srcProcessDirectory;
        this.srcProcessFileName = builder.srcProcessFileName;
        this.srcProcessDirectoryGroups = builder.srcProcessDirectoryGroups;
        this.srcProcessCertificateIssuer = builder.srcProcessCertificateIssuer;
        this.dstProcessDirectory = builder.dstProcessDirectory;
        this.dstProcessFileName = builder.dstProcessFileName;
        this.userId = builder.userId;
        this.userName = builder.userName;
        this.userDisplayName = builder.userDisplayName;
        this.dataSource = builder.dataSource;
        this.operationType = builder.operationType;
        this.additionalInfo = builder.additionalInfo;
    }

    public AdapterProcessStoredData(String id, Instant dateTime, String eventId, String srcProcessFileName, String machineName, String machineId, String srcProcessDirectory, String srcProcessFileName1, String srcProcessCertificateIssuer, String dstProcessDirectory, String dstProcessFileName, String userId, String userName, String userDisplayName, String dataSource, String operationType, AdditionalInfo additionalInfo) {
        this.id = id;
        this.dateTime = dateTime;
        this.eventId = eventId;
        this.srcProcessFileName = srcProcessFileName;
        this.machineName = machineName;
        this.machineId = machineId;
        this.srcProcessDirectory = srcProcessDirectory;
        this.srcProcessFileName = srcProcessFileName1;
        this.srcProcessCertificateIssuer = srcProcessCertificateIssuer;
        this.dstProcessDirectory = dstProcessDirectory;
        this.dstProcessFileName = dstProcessFileName;
        this.userId = userId;
        this.userName = userName;
        this.userDisplayName = userDisplayName;
        this.dataSource = dataSource;
        this.operationType = operationType;
        this.additionalInfo = additionalInfo;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setDateTime(Instant dateTime) {
        this.dateTime = dateTime;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getMachineName() {
        return machineName;
    }

    public void setMachineName(String machineName) {
        this.machineName = machineName;
    }

    public String getMachineId() {
        return machineId;
    }

    public void setMachineId(String machineId) {
        this.machineId = machineId;
    }

    public String getSrcProcessDirectory() {
        return srcProcessDirectory;
    }

    public void setSrcProcessDirectory(String srcProcessDirectory) {
        this.srcProcessDirectory = srcProcessDirectory;
    }

    public String getSrcProcessFileName() {
        return srcProcessFileName;
    }

    public void setSrcProcessFileName(String srcProcessFileName) {
        this.srcProcessFileName = srcProcessFileName;
    }

    public String getSrcProcessCertificateIssuer() {
        return srcProcessCertificateIssuer;
    }

    public void setSrcProcessCertificateIssuer(String srcProcessCertificateIssuer) {
        this.srcProcessCertificateIssuer = srcProcessCertificateIssuer;
    }

    public String getDstProcessDirectory() {
        return dstProcessDirectory;
    }

    public void setDstProcessDirectory(String dstProcessDirectory) {
        this.dstProcessDirectory = dstProcessDirectory;
    }

    public String getDstProcessFileName() {
        return dstProcessFileName;
    }

    public void setDstProcessFileName(String dstProcessFileName) {
        this.dstProcessFileName = dstProcessFileName;
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

    public AdditionalInfo getAdditionalInfo() {
        return additionalInfo;
    }

    public void setAdditionalInfo(AdditionalInfo additionalInfo) {
        this.additionalInfo = additionalInfo;
    }


    class AdditionalInfo {
        @Expose
        private String operationType="";


        public String getOperationType() {
            return operationType;
        }

        public void setOperationType(String operationType) {
            this.operationType = operationType;
        }

        @Override
        public String toString() {
            return (operationType == null)?"":"AdditionalInfo{" +
                    "operationType='" + operationType + '\'' +
                    '}';
        }
    }

    public static class Builder {
        private String id;
        private Instant dateTime;
        private String eventId;
        private String machineName;
        private String machineId;
        private String srcProcessDirectory;
        private String srcProcessFileName;
        private String[] srcProcessDirectoryGroups;
        private String[] srcProcessDirectoryCategories;
        private String srcProcessCertificateIssuer;
        private String dstProcessDirectory;
        private String dstProcessFileName;
        private String userId;
        private String userName;
        private String userDisplayName;
        private String dataSource;
        private String operationType;
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

        public Builder eventId(String eventId) {
            this.eventId = eventId;
            return this;
        }
        public Builder machineId(String machineId) {
            this.machineId = machineId;
            return this;
        }
        public Builder machineName(String machineName) {
            this.machineName = machineName;
            return this;
        }
        public Builder srcProcessDirectory(String srcProcessDirectory) {
            this.srcProcessDirectory = srcProcessDirectory;
            return this;
        }
        public Builder srcProcessFileName(String srcProcessFileName) {
            this.srcProcessFileName = srcProcessFileName;
            return this;
        }
        public Builder srcProcessCertificateIssuer(String srcProcessCertificateIssuer) {
            this.srcProcessCertificateIssuer = srcProcessCertificateIssuer;
            return this;
        }
        public Builder dstProcessDirectory(String dstProcessDirectory) {
            this.dstProcessDirectory = dstProcessDirectory;
            return this;
        }
        public Builder dstProcessFileName(String dstProcessFileName) {
            this.dstProcessFileName = dstProcessFileName;
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

        public AdapterProcessStoredData build() {
            return new AdapterProcessStoredData(this);
        }
    }
}
