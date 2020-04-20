package com.rsa.netwitness.presidio.automation.domain.tls;


import com.google.gson.annotations.Expose;
import fortscale.domain.core.entityattributes.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Document(collection = "output_tls_enriched_events")
public class OutputTlsEnrichStoredData {

    @Id
    private String id;

    @Expose
    @Field("srcIp")
    private String srcIp;

    @Expose
    @Field("dstIp")
    private String dstIp;

    @Expose
    @Field("srcCountry")
    private String srcCountry;

    @Expose
    @Field("dstCountry")
    private DestinationCountry dstCountry;

    @Expose
    @Field("sslSubject")
    private SslSubject sslSubject;

    @Expose
    @Field("domain")
    private Domain domain;

    @Expose
    @Field("dstOrg")
    private DestinationOrganization dstOrg;

    @Expose
    @Field("dstAsn")
    private DestinationAsn dstAsn;

    @Expose
    @Field("numOfBytesSent")
    private long numOfBytesSent;

    @Expose
    @Field("numOfBytesReceived")
    private long numOfBytesReceived;

    @Expose
    @Field("srcNetname")
    private String srcNetname;

    @Expose
    @Field("dstNetname")
    private String dstNetname;

    @Expose
    @Field("ja3")
    private Ja3 ja3;

    @Expose
    @Field("ja3s")
    private String ja3s;

    @Expose
    @Field("direction")
    private String direction;

    @Expose
    @Field("dstPort")
    private DestinationPort dstPort;

    @Expose
    @Field("srcPort")
    private String srcPort;

    @Expose
    @Field("fqdn")
    private List<String> fqdn;

    @Expose
    @Field("createdDate")
    private Instant createdDate;

    @Expose
    @Field("eventDate")
    private Instant eventDate;

    @Expose
    @Field("eventId")
    private String eventId;

    @Expose
    @Field("dataSource")
    private String dataSource;

    @Expose
    @Field("sslCas")
    private List<String> sslCas;

    @Expose
    @Field("additionalInfo")
    private Map additionalInfo;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public Domain getDomain() {
        return domain;
    }

    public void setDomain(Domain domain) {
        this.domain = domain;
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

    public Instant getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Instant createdDate) {
        this.createdDate = createdDate;
    }

    public Instant getEventDate() {
        return eventDate;
    }

    public void setEventDate(Instant eventDate) {
        this.eventDate = eventDate;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getDataSource() {
        return dataSource;
    }

    public void setDataSource(String dataSource) {
        this.dataSource = dataSource;
    }

    public Map getAdditionalInfo() {
        return additionalInfo;
    }

    public void setAdditionalInfo(Map additionalInfo) {
        this.additionalInfo = additionalInfo;
    }

    public List<String> getSslCas() {
        return sslCas;
    }

    public void setSslCas(List<String> sslCas) {
        this.sslCas = sslCas;
    }

    @Override
    public String toString() {
        return "OutputTlsEnrichStoredData{" +
                "id='" + id + '\'' +
                ", srcIp='" + srcIp + '\'' +
                ", dstIp='" + dstIp + '\'' +
                ", srcCountry='" + srcCountry + '\'' +
                ", dstCountry=" + dstCountry +
                ", sslSubject=" + sslSubject +
                ", domain=" + domain +
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
                ", createdDate=" + createdDate +
                ", eventDate=" + eventDate +
                ", eventId='" + eventId + '\'' +
                ", dataSource='" + dataSource + '\'' +
                ", sslCas=" + sslCas +
                ", additionalInfo=" + additionalInfo +
                '}';
    }
}
