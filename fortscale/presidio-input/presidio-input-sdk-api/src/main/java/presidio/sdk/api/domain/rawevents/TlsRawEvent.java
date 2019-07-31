package presidio.sdk.api.domain.rawevents;


import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import presidio.sdk.api.domain.AbstractInputDocument;

import java.time.Instant;
import java.util.List;
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
    public static final String SOURCE_PORT_FIELD_NAME = "srcPort";
    public static final String FQDN_FIELD_NAME = "fqdn";
    public static final String SSL_CA_FIELD_NAME = "sslCa";
    public static final String SESSION_SPLIT_FIELD_NAME = "sessionSplit";




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
    private Domain domain;

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
    private String dstPort;

    @Field(SOURCE_PORT_FIELD_NAME)
    private String srcPort;

    @Field(FQDN_FIELD_NAME)
    private List<String> fqdn;

    @Field(SSL_CA_FIELD_NAME)
    private List<String> sslCa;

    @Field(SESSION_SPLIT_FIELD_NAME)
    private Integer sessionSplit;



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
        this.srcPort = other.srcPort;
        this.fqdn = other.fqdn;
        this.sslCa = other.sslCa;
        this.sessionSplit = other.sessionSplit;
    }

    public TlsRawEvent(Instant dateTime, String eventId, String dataSource, Map<String, String> additionalInfo,
                       String srcIp, String dstIp, String srcPort, String srcCountry, String dstCountry, String sslSubject,
                       Domain domain, String dstOrg,
                       String dstAsn, Long numOfBytesSent, Long numOfBytesReceived, String srcNetname,
                       String dstNetname, String ja3, String ja3s, String direction, String dstPort, List<String> fqdn,
                       List<String> sslCa, Integer sessionSplit) {
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
        this.srcPort = srcPort;
        this.fqdn = fqdn;
        this.sslCa = sslCa;
        this.sessionSplit = sessionSplit;
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

    public Domain getDomain() {
        return domain;
    }

    public void setDomain(Domain domain) {
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

    public String getDstPort() {
        return dstPort;
    }

    public void setDstPort(String dstPort) {
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

    public Integer getSessionSplit() {
        return sessionSplit;
    }

    public void setSessionSplit(Integer sessionSplit) {
        this.sessionSplit = sessionSplit;
    }

    public String getSrcPort() {
        return srcPort;
    }

    public void setSrcPort(String srcPort) {
        this.srcPort = srcPort;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }
}
