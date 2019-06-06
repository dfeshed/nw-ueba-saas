package presidio.sdk.api.domain.rawevents;


import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import presidio.sdk.api.domain.AbstractInputDocument;

import java.time.Instant;
import java.util.Map;

@Document
public class TlsRawEvent extends AbstractInputDocument {
    public static final String SOURCE_IP_FIELD_NAME = "srcIp";
    public static final String DESTINATION_IP_FIELD_NAME = "dstIp";
    public static final String SOURCE_COUNTRY_FIELD_NAME = "srcCountry";
    public static final String DESTINATION_COUNTRY_FIELD_NAME = "dstCountry";
    public static final String SSL_SUBJECT_FIELD_NAME = "sslSubject";
    public static final String DOMAIN_FIELD_NAME = "domain";
    public static final String DESTINATION_ORGANIZATION_FIELD_NAME = "dstOrg";
    public static final String DESTINATION_ASN_FIELD_NAME = "dstAsn";
    public static final String NUM_OF_BYTES_SENT_FIELD_NAME = "numOfBytesSent";
    public static final String NUM_OF_BYTES_RECEIVED_FIELD_NAME = "numOfBytesReceived";
    public static final String SOURCE_NETNAME_FIELD_NAME = "srcNetname";
    public static final String DESTINATION_NETNAME_FIELD_NAME = "dstNetname";
    public static final String JA3_FIELD_NAME = "ja3";
    public static final String JA3S_FIELD_NAME = "ja3s";
    public static final String DIRECTION_FIELD_NAME = "direction";
    public static final String DESTINATION_PORT_FIELD_NAME = "dstPort";
    public static final String FQDN_FIELD_NAME = "fqdn";




    @Field(SOURCE_IP_FIELD_NAME)
    private String srcIp;

    @Field(DESTINATION_IP_FIELD_NAME)
    private String dstIp;

    @Field(SOURCE_COUNTRY_FIELD_NAME)
    private String srcCountry;

    @Field(DESTINATION_COUNTRY_FIELD_NAME)
    private String dstCountry;

    @Field(SSL_SUBJECT_FIELD_NAME)
    private String sslSubject;

    @Field(DOMAIN_FIELD_NAME)
    private String domain;

    @Field(DESTINATION_ORGANIZATION_FIELD_NAME)
    private String dstOrg;

    @Field(DESTINATION_ASN_FIELD_NAME)
    private String dstAsn;

    @Field(NUM_OF_BYTES_SENT_FIELD_NAME)
    private Long numOfBytesSent;

    @Field(NUM_OF_BYTES_RECEIVED_FIELD_NAME)
    private Long numOfBytesReceived;

    @Field(SOURCE_NETNAME_FIELD_NAME)
    private String srcNetname;

    @Field(DESTINATION_NETNAME_FIELD_NAME)
    private String dstNetname;

    @Field(JA3_FIELD_NAME)
    private String ja3;

    @Field(JA3S_FIELD_NAME)
    private String ja3s;

    @Field(DIRECTION_FIELD_NAME)
    private String direction;

    @Field(DESTINATION_PORT_FIELD_NAME)
    private Integer dstPort;

    @Field(FQDN_FIELD_NAME)
    private String fqdn;



    public TlsRawEvent(){super();}

    public TlsRawEvent(TlsRawEvent other){
        super(other);
        this.srcIp = other.srcIp;
        this.dstIp = other.dstIp;
        this.srcCountry = other.srcCountry;
        this.dstCountry = other.dstCountry;
        this.sslSubject = other.sslSubject;
        this.domain = other.domain;
        this.dstOrg = other.dstOrg;
        this.dstAsn = other.dstAsn;
        this.numOfBytesSent = other.numOfBytesSent;
        this.numOfBytesReceived = other.numOfBytesReceived;
        this.srcNetname = other.srcNetname;
        this.dstNetname = other.dstNetname;
        this.ja3 = other.ja3;
        this.ja3s = other.ja3s;
        this.direction = other.direction;
        this.dstPort = other.dstPort;
        this.fqdn = other.fqdn;
    }

    public TlsRawEvent(Instant dateTime, String eventId, String dataSource, Map<String, String> additionalInfo,
                       String srcIp, String dstIp, String srcCountry, String dstCountry, String sslSubject, String domain, String dstOrg,
                       String dstAsn, Long numOfBytesSent, Long numOfBytesReceived, String srcNetname,
                       String dstNetname, String ja3, String ja3s, String direction, Integer dstPort, String fqdn) {
        super(dateTime, eventId, dataSource, additionalInfo);
        this.srcIp = srcIp;
        this.dstIp = dstIp;
        this.srcCountry = srcCountry;
        this.dstCountry = dstCountry;
        this.sslSubject = sslSubject;
        this.domain = domain;
        this.dstOrg = dstOrg;
        this.dstAsn = dstAsn;
        this.numOfBytesSent = numOfBytesSent;
        this.numOfBytesReceived = numOfBytesReceived;
        this.srcNetname = srcNetname;
        this.dstNetname = dstNetname;
        this.ja3 = ja3;
        this.ja3s = ja3s;
        this.direction = direction;
        this.dstPort = dstPort;
        this.fqdn = fqdn;
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

    public String getSrcCountry() {
        return srcCountry;
    }

    public void setSrcCountry(String srcCountry) {
        this.srcCountry = srcCountry;
    }

    public void setDstIp(String dstIp) {
        this.dstIp = dstIp;
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

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
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

    public Long getNumOfBytesSent() {
        return numOfBytesSent;
    }

    public void setNumOfBytesSent(Long numOfBytesSent) {
        this.numOfBytesSent = numOfBytesSent;
    }

    public Long getNumOfBytesReceived() {
        return numOfBytesReceived;
    }

    public void setNumOfBytesReceived(Long numOfBytesReceived) {
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

    public Integer getDstPort() {
        return dstPort;
    }

    public void setDstPort(Integer dstPort) {
        this.dstPort = dstPort;
    }

    public String getFqdn() {
        return fqdn;
    }

    public void setFqdn(String fqdn) {
        this.fqdn = fqdn;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }
}
