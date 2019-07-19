package com.rsa.netwitness.presidio.automation.domain.tls;

import com.google.gson.annotations.Expose;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import presidio.data.domain.event.Event;

import java.time.Instant;
import java.util.List;

@Document(collection = "input_tls_raw_events")
public class AdapterTlsStoredData extends Event {

    @Id
    private String id;
    private Instant dateTime;
    @Expose
    private String eventId;
    @Expose
    private String srcIp;
    @Expose
    private String dstIp;
    @Expose
    private String srcCountry;
    @Expose
    private String dstCountry;
    @Expose
    private String sslSubject;
    @Expose
    private String dstOrg;
    @Expose
    private String dstAsn;
    @Expose
    private long numOfBytesSent;
    @Expose
    private long numOfBytesReceived;
    @Expose
    private String srcNetname;
    @Expose
    private String dstNetname;
    @Expose
    private String ja3;
    @Expose
    private String ja3s;
    @Expose
    private String direction;
    @Expose
    private int dstPort;

    public String getSrcIp() {
        return srcIp;
    }

    public void setSrcIp(String srcIp) {
        this.srcIp = srcIp;
    }

    public String getDstIp() {
        return dstIp;
    }

    public void setDstIp(String dstIp) {
        this.dstIp = dstIp;
    }

    public String getSrcCountry() {
        return srcCountry;
    }

    public void setSrcCountry(String srcCountry) {
        this.srcCountry = srcCountry;
    }

    public String getDstCountry() {
        return dstCountry;
    }

    public void setDstCountry(String dstCountry) {
        this.dstCountry = dstCountry;
    }

    public String getSslSubject() {
        return sslSubject;
    }

    public void setSslSubject(String sslSubject) {
        this.sslSubject = sslSubject;
    }

    public String getDstOrg() {
        return dstOrg;
    }

    public void setDstOrg(String dstOrg) {
        this.dstOrg = dstOrg;
    }

    public String getDstAsn() {
        return dstAsn;
    }

    public void setDstAsn(String dstAsn) {
        this.dstAsn = dstAsn;
    }

    public long getNumOfBytesSent() {
        return numOfBytesSent;
    }

    public void setNumOfBytesSent(long numOfBytesSent) {
        this.numOfBytesSent = numOfBytesSent;
    }

    public long getNumOfBytesReceived() {
        return numOfBytesReceived;
    }

    public void setNumOfBytesReceived(long numOfBytesReceived) {
        this.numOfBytesReceived = numOfBytesReceived;
    }

    public String getSrcNetname() {
        return srcNetname;
    }

    public void setSrcNetname(String srcNetname) {
        this.srcNetname = srcNetname;
    }

    public String getDstNetname() {
        return dstNetname;
    }

    public void setDstNetname(String dstNetname) {
        this.dstNetname = dstNetname;
    }

    public String getJa3() {
        return ja3;
    }

    public void setJa3(String ja3) {
        this.ja3 = ja3;
    }

    public String getJa3s() {
        return ja3s;
    }

    public void setJa3s(String ja3s) {
        this.ja3s = ja3s;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public int getDstPort() {
        return dstPort;
    }

    public void setDstPort(int dstPort) {
        this.dstPort = dstPort;
    }

    public List<String> getFqdn() {
        return fqdn;
    }

    public void setFqdn(List<String> fqdn) {
        this.fqdn = fqdn;
    }

    public List<String> getSslCa() {
        return sslCa;
    }

    public void setSslCa(List<String> sslCa) {
        this.sslCa = sslCa;
    }

    public String getDataSource() {
        return dataSource;
    }

    public void setDataSource(String dataSource) {
        this.dataSource = dataSource;
    }

    public AdditionalInfo getAdditionalInfo() {
        return additionalInfo;
    }

    public void setAdditionalInfo(AdditionalInfo additionalInfo) {
        this.additionalInfo = additionalInfo;
    }

    @Expose
    private List<String> fqdn;
    @Expose
    private List<String> sslCa;
    @Expose
    private String dataSource;
    @Expose
    private AdditionalInfo additionalInfo;

    private AdapterTlsStoredData(){
    }

    private AdapterTlsStoredData(Builder builder) {
        this.id = builder.id;
        this.eventId = builder.eventId;
        this.id = builder.id;
        this.dateTime = builder.dateTime;
        this.eventId = builder.eventId;
        this.srcIp = builder.srcIp;
        this.dstIp = builder.dstIp;
        this.srcCountry = builder.srcCountry;
        this.dstCountry = builder.dstCountry;
        this.sslSubject = builder.sslSubject;
        this.dstOrg = builder.dstOrg;
        this.dstAsn = builder.dstAsn;
        this.numOfBytesSent = builder.numOfBytesSent;
        this.numOfBytesReceived = builder.numOfBytesReceived;
        this.srcNetname = builder.srcNetname;
        this.dstNetname = builder.dstNetname;
        this.ja3 = builder.ja3;
        this.ja3s = builder.ja3s;
        this.direction = builder.direction;
        this.dstPort = builder.dstPort;
        this.fqdn = builder.fqdn;
        this.sslCa = builder.sslCa;
        this.dataSource = builder.dataSource;
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


    @Override
    public String toString() {
        return "AdapterTlsStoredData{" +
                "id='" + id + '\'' +
                ", dateTime=" + dateTime +
                ", eventId='" + eventId + '\'' +
                ", srcIp='" + srcIp + '\'' +
                ", dstIp='" + dstIp + '\'' +
                ", srcCountry='" + srcCountry + '\'' +
                ", dstCountry='" + dstCountry + '\'' +
                ", sslSubject='" + sslSubject + '\'' +
                ", dstOrg='" + dstOrg + '\'' +
                ", dstAsn='" + dstAsn + '\'' +
                ", numOfBytesSent=" + numOfBytesSent +
                ", numOfBytesReceived=" + numOfBytesReceived +
                ", srcNetname='" + srcNetname + '\'' +
                ", dstNetname='" + dstNetname + '\'' +
                ", ja3='" + ja3 + '\'' +
                ", ja3s='" + ja3s + '\'' +
                ", direction='" + direction + '\'' +
                ", dstPort=" + dstPort +
                ", fqdn=" + fqdn +
                ", sslCa=" + sslCa +
                ", dataSource='" + dataSource + '\'' +
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
        private String srcIp;
        private String dstIp;
        private String srcCountry;
        private String dstCountry;
        private String sslSubject;
        private String dstOrg;
        private String dstAsn;
        private long numOfBytesSent;
        private long numOfBytesReceived;
        private String srcNetname;
        private String dstNetname;
        private String ja3;
        private String ja3s;
        private String direction;
        private int dstPort;
        private List<String> fqdn;
        private List<String> sslCa;
        private String dataSource;
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

        public Builder srcIp(String srcIp) {
            this.srcIp = srcIp;
            return this;
        }

        public Builder dstIp(String dstIp) {
            this.dstIp = dstIp;
            return this;
        }

        public Builder srcCountry(String srcCountry) {
            this.srcCountry = srcCountry;
            return this;
        }

        public Builder dstCountry(String dstCountry) {
            this.dstCountry = dstCountry;
            return this;
        }

        public Builder sslSubject(String sslSubject) {
            this.sslSubject = sslSubject;
            return this;
        }

        public Builder additionalInfo(AdditionalInfo additionalInfo) {
            this.additionalInfo = additionalInfo;
            return this;
        }

        public Builder dstOrg(String dstOrg) {
            this.dstOrg = dstOrg;
            return this;
        }

        public Builder dstAsn(String dstAsn) {
            this.dstAsn = dstAsn;
            return this;
        }

        public Builder numOfBytesSent(long numOfBytesSent) {
            this.numOfBytesSent = numOfBytesSent;
            return this;
        }

        public Builder numOfBytesReceived(long numOfBytesReceived) {
            this.numOfBytesReceived = numOfBytesReceived;
            return this;
        }

        public Builder srcNetname(String srcNetname) {
            this.srcNetname = srcNetname;
            return this;
        }

        public Builder dstNetname(String dstNetname) {
            this.dstNetname = dstNetname;
            return this;
        }

        public Builder ja3(String ja3) {
            this.ja3 = ja3;
            return this;
        }

        public Builder ja3s(String ja3s) {
            this.ja3s = ja3;
            return this;
        }

        public Builder direction(String direction) {
            this.direction = direction;
            return this;
        }

        public Builder dstPort(int dstPort) {
            this.dstPort = dstPort;
            return this;
        }


        public Builder fqdn(List<String> fqdn) {
            this.fqdn = fqdn;
            return this;
        }

        public Builder sslCa(List<String> sslCa) {
            this.sslCa = sslCa;
            return this;
        }

        public Builder dataSource(String dataSource) {
            this.dataSource = dataSource;
            return this;
        }
        public AdapterTlsStoredData build() {
            return new AdapterTlsStoredData(this);
        }
    }
}
