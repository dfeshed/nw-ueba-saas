package presidio.ade.domain.record.enriched.tls;

import presidio.ade.domain.record.enriched.BaseEnrichedContext;

public class AdeEnrichedTlsContext extends BaseEnrichedContext {


    private String srcIp;
    private String dstCountry;
    private String sslSubject;
    private String domain;
    private String dstOrg;
    private String dstAsn;
    private String srcNetname;
    private String dstNetname;
    private String ja3;
    private String ja3s;
    private String direction;
    private Integer dstPort;


    public AdeEnrichedTlsContext() {
        super();
    }

    public AdeEnrichedTlsContext(EnrichedTlsRecord enrichedTlsRecord) {
        super(enrichedTlsRecord.getEventId());
        this.domain = enrichedTlsRecord.getDomain();
        this.dstAsn = enrichedTlsRecord.getDstAsn();
        this.dstCountry = enrichedTlsRecord.getDstCountry();
        this.dstNetname = enrichedTlsRecord.getDstNetname();
        this.dstOrg = enrichedTlsRecord.getDstOrg();
        this.dstPort = enrichedTlsRecord.getDstPort();
        this.direction = enrichedTlsRecord.getDirection();
        this.ja3 = enrichedTlsRecord.getJa3();
        this.ja3s = enrichedTlsRecord.getJa3s();
        this.srcIp = enrichedTlsRecord.getSrcIp();
        this.srcNetname = enrichedTlsRecord.getSrcNetname();
        this.sslSubject = enrichedTlsRecord.getSslSubject();
    }

    public String getSrcIp() {
        return srcIp;
    }

    public void setSrcIp(String srcIp) {
        this.srcIp = srcIp;
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
}
