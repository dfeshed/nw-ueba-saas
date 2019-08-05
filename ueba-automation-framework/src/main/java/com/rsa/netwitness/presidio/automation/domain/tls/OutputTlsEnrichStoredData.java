package com.rsa.netwitness.presidio.automation.domain.tls;


import com.google.gson.annotations.Expose;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;

@Document(collection = "output_tls_enriched_events")
public class OutputTlsEnrichStoredData {

    @Id
    private String id;
    private Instant startInstant;

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
    private String dstCountry;

    @Expose
    @Field("sslSubject")
    private String sslSubject;

    @Expose
    @Field("domain")
    private String domain;

    @Expose
    @Field("dstOrg")
    private String dstOrg;

    @Expose
    @Field("dstAsn")
    private String dstAsn;

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
    private String ja3;

    @Expose
    @Field("ja3s")
    private String ja3s;

    @Expose
    @Field("direction")
    private String direction;

    @Expose
    @Field("dstPort")
    private int dstPort;

    @Expose
    @Field("eventId")
    private String eventId;

    @Expose
    @Field("dataSource")
    private String dataSource;

    public String getId() {
        return id;
    }

    public Instant getStartInstant() {
        return startInstant;
    }

    public String getSrcIp() {
        return srcIp;
    }

    public String getDstIp() {
        return dstIp;
    }

    public String getSrcCountry() {
        return srcCountry;
    }

    public String getDstCountry() {
        return dstCountry;
    }

    public String getSslSubject() {
        return sslSubject;
    }

    public String getDomain() {
        return domain;
    }

    public String getDstOrg() {
        return dstOrg;
    }

    public String getDstAsn() {
        return dstAsn;
    }

    public long getNumOfBytesSent() {
        return numOfBytesSent;
    }

    public long getNumOfBytesReceived() {
        return numOfBytesReceived;
    }

    public String getSrcNetname() {
        return srcNetname;
    }

    public String getDstNetname() {
        return dstNetname;
    }

    public String getJa3() {
        return ja3;
    }

    public String getJa3s() {
        return ja3s;
    }

    public String getDirection() {
        return direction;
    }

    public int getDstPort() {
        return dstPort;
    }

    public String getEventId() {
        return eventId;
    }

    public String getDataSource() {
        return dataSource;
    }
}
