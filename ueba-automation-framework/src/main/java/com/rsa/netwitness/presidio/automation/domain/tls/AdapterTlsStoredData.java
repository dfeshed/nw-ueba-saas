package com.rsa.netwitness.presidio.automation.domain.tls;

import com.google.gson.annotations.Expose;
import fortscale.domain.core.entityattributes.*;
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
    private DestinationCountry dstCountry;
    @Expose
    private SslSubject sslSubject;
    @Expose
    private DestinationOrganization dstOrg;
    @Expose
    private DestinationAsn dstAsn;
    @Expose
    private long numOfBytesSent;
    @Expose
    private long numOfBytesReceived;
    @Expose
    private String srcNetname;
    @Expose
    private String dstNetname;
    @Expose
    private Ja3 ja3;
    @Expose
    private String ja3s;
    @Expose
    private String direction;
    @Expose
    private DestinationPort dstPort;
    @Expose
    private String srcPort;
    @Expose
    private List<String> fqdn;
    @Expose
    private Domain domain;
    @Expose
    private List<String> sslCa;
    @Expose
    private String dataSource;


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

    public DestinationCountry getDstCountry() {
        return dstCountry;
    }

    public void setDstCountry(DestinationCountry dstCountry) {
        this.dstCountry = dstCountry;
    }

    public SslSubject getSslSubject() {
        return sslSubject;
    }

    public void setSslSubject(SslSubject sslSubject) {
        this.sslSubject = sslSubject;
    }

    public DestinationOrganization getDstOrg() {
        return dstOrg;
    }

    public void setDstOrg(DestinationOrganization dstOrg) {
        this.dstOrg = dstOrg;
    }

    public DestinationAsn getDstAsn() {
        return dstAsn;
    }

    public void setDstAsn(DestinationAsn dstAsn) {
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

    public Ja3 getJa3() {
        return ja3;
    }

    public void setJa3(Ja3 ja3) {
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

    public DestinationPort getDstPort() {
        return dstPort;
    }

    public void setDstPort(DestinationPort dstPort) {
        this.dstPort = dstPort;
    }

    public String getSrcPort() {
        return srcPort;
    }

    public void setSrcPort(String srcPort) {
        this.srcPort = srcPort;
    }

    public List<String> getFqdn() {
        return fqdn;
    }

    public void setFqdn(List<String> fqdn) {
        this.fqdn = fqdn;
    }

    public Domain getDomain() {
        return domain;
    }

    public void setDomain(Domain domain) {
        this.domain = domain;
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

    @Override
    public Instant getDateTime() {
        return dateTime;
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
                ", dstCountry=" + dstCountry +
                ", sslSubject=" + sslSubject +
                ", dstOrg=" + dstOrg +
                ", dstAsn=" + dstAsn +
                ", numOfBytesSent=" + numOfBytesSent +
                ", numOfBytesReceived=" + numOfBytesReceived +
                ", srcNetname='" + srcNetname + '\'' +
                ", dstNetname='" + dstNetname + '\'' +
                ", ja3=" + ja3 +
                ", ja3s='" + ja3s + '\'' +
                ", direction='" + direction + '\'' +
                ", dstPort=" + dstPort +
                ", srcPort='" + srcPort + '\'' +
                ", fqdn=" + fqdn +
                ", domain=" + domain +
                ", sslCa=" + sslCa +
                ", dataSource='" + dataSource + '\'' +
                '}';
    }
}
