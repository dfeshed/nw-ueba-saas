package presidio.ade.domain.record.enriched.tls;

import presidio.ade.domain.record.enriched.BaseEnrichedContext;
import presidio.sdk.api.domain.newoccurrencewrappers.*;

public class AdeEnrichedTlsContext extends BaseEnrichedContext {


    private String srcIp;
    private String dstIp;
    private String srcCountry;
    private DestinationCountry dstCountry;
    private SslSubject sslSubject;
    private Domain domain;
    private DestinationOrganization dstOrg;
    private String dstAsn;
    private String srcNetname;
    private String dstNetname;
    private Ja3 ja3;
    private String ja3s;
    private String direction;
    private String dstPort;
    private String srcPort;


    public AdeEnrichedTlsContext() {
        super();
    }

    public AdeEnrichedTlsContext(EnrichedTlsRecord enrichedTlsRecord) {
        super(enrichedTlsRecord.getEventId());
        this.domain = enrichedTlsRecord.getDomain();
        this.dstAsn = enrichedTlsRecord.getDstAsn();
        this.srcCountry = enrichedTlsRecord.getSrcCountry();
        this.dstCountry = enrichedTlsRecord.getDstCountry();
        this.dstNetname = enrichedTlsRecord.getDstNetname();
        this.dstOrg = enrichedTlsRecord.getDstOrg();
        this.dstPort = enrichedTlsRecord.getDstPort();
        this.srcPort = enrichedTlsRecord.getSrcPort();
        this.direction = enrichedTlsRecord.getDirection();
        this.ja3 = enrichedTlsRecord.getJa3();
        this.ja3s = enrichedTlsRecord.getJa3s();
        this.srcIp = enrichedTlsRecord.getSrcIp();
        this.dstIp = enrichedTlsRecord.getDstIp();
        this.srcNetname = enrichedTlsRecord.getSrcNetname();
        this.sslSubject = enrichedTlsRecord.getSslSubject();
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

    public String getDstAsn() {
        return dstAsn;
    }

    public void setDstAsn(String dstAsn) {
        this.dstAsn = dstAsn;
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

    public String getDstPort() {
        return dstPort;
    }

    public void setDstPort(String dstPort) {
        this.dstPort = dstPort;
    }

    public String getSrcPort() {
        return srcPort;
    }

    public void setSrcPort(String srcPort) {
        this.srcPort = srcPort;
    }
}
