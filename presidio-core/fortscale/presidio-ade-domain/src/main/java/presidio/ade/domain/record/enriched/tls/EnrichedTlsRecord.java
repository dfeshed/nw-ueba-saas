package presidio.ade.domain.record.enriched.tls;


import fortscale.common.general.Schema;
import fortscale.domain.core.entityattributes.*;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import presidio.ade.domain.record.enriched.EnrichedRecord;
import presidio.ade.domain.record.util.AdeRecordMetadata;

import java.time.Instant;
import java.util.List;

@Document
@AdeRecordMetadata(adeEventType = Schema.TLS)
public class EnrichedTlsRecord extends EnrichedRecord {


    public static final String SOURCE_IP_FIELD_NAME = "srcIp";
    public static final String DESTINATION_IP_FIELD_NAME = "dstIp";
    public static final String SOURCE_COUNTRY_FIELD_NAME = "srcCountry";
    public static final String DESTINATION_COUNTRY_FIELD_NAME = "dstCountry";
    public static final String SSL_SUBJECT_FIELD_NAME = "sslSubject";
    public static final String SSL_CAS_FIELD_NAME = "sslCas";
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


    @Field(SOURCE_IP_FIELD_NAME)
    private String srcIp;

    @Field(DESTINATION_IP_FIELD_NAME)
    private String dstIp;

    @Field(SOURCE_COUNTRY_FIELD_NAME)
    private String srcCountry;

    @Field(DESTINATION_COUNTRY_FIELD_NAME)
    private DestinationCountry dstCountry;

    @Field(SSL_SUBJECT_FIELD_NAME)
    private SslSubject sslSubject;

    @Field(SSL_CAS_FIELD_NAME)
    private List<String> sslCas;

    @Field(DOMAIN_FIELD_NAME)
    private Domain domain;

    @Field(DESTINATION_ORGANIZATION_FIELD_NAME)
    private DestinationOrganization dstOrg;

    @Field(DESTINATION_ASN_FIELD_NAME)
    private DestinationAsn dstAsn;

    @Field(NUM_OF_BYTES_SENT_FIELD_NAME)
    private Long numOfBytesSent;

    @Field(NUM_OF_BYTES_RECEIVED_FIELD_NAME)
    private Long numOfBytesReceived;

    @Field(SOURCE_NETNAME_FIELD_NAME)
    private String srcNetname;

    @Field(DESTINATION_NETNAME_FIELD_NAME)
    private String dstNetname;

    @Field(JA3_FIELD_NAME)
    private Ja3 ja3;

    @Field(JA3S_FIELD_NAME)
    private String ja3s;

    @Field(DIRECTION_FIELD_NAME)
    private String direction;

    @Field(DESTINATION_PORT_FIELD_NAME)
    private DestinationPort dstPort;

    @Field(SOURCE_PORT_FIELD_NAME)
    private String srcPort;

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }

    public EnrichedTlsRecord(Instant startInstant) {
        super(startInstant);
    }

    @Override
    @Transient
    public String getAdeEventType() {
        return Schema.TLS.getName();
    }

    @Transient
    public AdeEnrichedTlsContext getContext() {
        return new AdeEnrichedTlsContext(this);
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

    public List<String> getSslCas() {
        return sslCas;
    }

    public void setSslCas(List<String> sslCas) {
        this.sslCas = sslCas;
    }
}
