package com.rsa.netwitness.presidio.automation.domain.authentication;

import com.google.gson.annotations.Expose;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import presidio.data.domain.event.Event;

import java.time.Instant;

@Document(collection = "input_authentication_raw_events")
public class AdapterAuthenticationStoredData extends Event {

    @Id
    private String id;
    private Instant dateTime;


    @Expose
    private String eventId;
    @Expose
    private String srcMachineId;
    @Expose
    private String srcMachineName;
    @Expose
    private String dstMachineId;
    @Expose
    private String dstMachineName;
    @Expose
    private String dstMachineDomain;
    @Expose
    private String site;
    @Expose
    private String country;
    @Expose
    private String city;
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

    private AdapterAuthenticationStoredData(){
    }

    private AdapterAuthenticationStoredData(Builder builder) {
        this.id = builder.id;
        this.eventId = builder.eventId;
        this.srcMachineId = builder.srcMachineId;
        this.srcMachineName = builder.srcMachineName;
        this.dstMachineId = builder.dstMachineId;
        this.srcMachineName = builder.srcMachineName;
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

    @Override
    public Instant getDateTime() {
        return dateTime;
    }

    public void setDateTime(Instant dateTime) {
        this.dateTime = dateTime;
    }

    public String getSrcMachineId() {
        return srcMachineId;
    }

    public void setSrcMachineId(String srcMachineId) {
        this.srcMachineId = srcMachineId;
    }

    public String getSrcMachineName() {
        return srcMachineName;
    }

    public void setSrcMachineName(String srcMachineName) {
        this.srcMachineName = srcMachineName;
    }

    public String getDstMachineId() {
        return dstMachineId;
    }

    public void setDstMachineId(String dstMachineId) {
        this.dstMachineId = dstMachineId;
    }

    public String getDstMachineName() {
        return dstMachineName;
    }

    public void setDstMachineName(String dstMachineName) {
        this.dstMachineName = dstMachineName;
    }

    public String getDstMachineDomain() {
        return dstMachineDomain;
    }

    public void setDstMachineDomain(String dstMachineDomain) {
        this.dstMachineDomain = dstMachineDomain;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDataSource() {
        return dataSource;
    }

    public void setDataSource(String dataSource) {
        this.dataSource = dataSource;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getOperationType() {
        return operationType;
    }

    public void setOperationType(String operationType) {
        this.operationType = operationType;
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


    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    @Override
    public String toString() {
        return "AdapterAuthenticationStoredData{" +
                "id='" + id + '\'' +
                ", dateTime=" + dateTime +
                ", eventId=" + eventId +
                ", srcMachineId='" + srcMachineId + '\'' +
                ", srcMachineName='" + srcMachineName + '\'' +
                ", dstMachineId='" + dstMachineId + '\'' +
                ", dstMachineName='" + dstMachineName + '\'' +
                ", dstMachineDomain='" + dstMachineDomain + '\'' +
                ", site='" + site + '\'' +
                ", country='" + country + '\'' +
                ", city='" + city + '\'' +
                ", dataSource='" + dataSource + '\'' +
                ", userId='" + userId + '\'' +
                ", operationType='" + operationType + '\'' +
                ", result='" + result + '\'' +
                ", userName='" + userName + '\'' +
                ", userDisplayName='" + userDisplayName + '\'' +
                ", additionalInfo=" + additionalInfo +
                '}';
    }

    class AdditionalInfo {
        @Expose
        private String result;
        @Expose
        private String originIPv4;
        @Expose
        private String description;
        @Expose
        private String oSVersion;
        @Expose
        private String iPAddress;
        @Expose
        private String srcDomainFQDN;
        @Expose
        private String domainDN;
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

        public String getSrcDomainFQDN() {
            return srcDomainFQDN;
        }

        public void setSrcDomainFQDN(String srcDomainFQDN) {
            this.srcDomainFQDN = srcDomainFQDN;
        }

        public String getDomainDN() {
            return domainDN;
        }

        public void setDomainDN(String domainDN) {
            this.domainDN = domainDN;
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
                    ", originIPv4='" + originIPv4 + '\'' +
                    ", description='" + description + '\'' +
                    ", oSVersion='" + oSVersion + '\'' +
                    ", iPAddress='" + iPAddress + '\'' +
                    ", srcDomainFQDN='" + srcDomainFQDN + '\'' +
                    ", domainDN='" + domainDN + '\'' +
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
        private String srcMachineId;
        private String srcMachineName;
        private String dstMachineId;
        private String dstMachineName;
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

        public Builder srcMachineId(String srcMachineId) {
            this.srcMachineId = srcMachineId;
            return this;
        }

        public Builder srcMachineName(String srcMachineName) {
            this.srcMachineName = srcMachineName;
            return this;
        }

       public Builder dstMachineId(String dstMachineId) {
            this.dstMachineId = dstMachineId;
            return this;
       }

       public Builder dstMachineName(String dstMachineName) {
            this.dstMachineName = dstMachineName;
            return this;
       }

        public AdapterAuthenticationStoredData build() {
            return new AdapterAuthenticationStoredData(this);
        }
    }
}
