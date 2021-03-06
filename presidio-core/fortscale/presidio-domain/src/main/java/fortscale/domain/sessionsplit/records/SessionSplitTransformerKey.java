package fortscale.domain.sessionsplit.records;

import org.apache.commons.lang3.builder.HashCodeBuilder;

public class SessionSplitTransformerKey {
    private String srcIp;
    private String dstIp;
    private String dstPort;
    private String srcPort;


    public SessionSplitTransformerKey(String srcIp, String dstIp, String dstPort, String srcPort) {
        this.srcIp = srcIp;
        this.dstIp = dstIp;
        this.dstPort = dstPort;
        this.srcPort = srcPort;
    }

    public String getSrcIp() {
        return srcIp;
    }

    public String getDstIp() {
        return dstIp;
    }

    public String getDstPort() {
        return dstPort;
    }

    public String getSrcPort() {
        return srcPort;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SessionSplitTransformerKey)) return false;
        return srcIp.equals(((SessionSplitTransformerKey) o).getSrcIp()) &&
                dstIp.equals(((SessionSplitTransformerKey) o).getDstIp()) &&
                dstPort.equals(((SessionSplitTransformerKey) o).getDstPort()) &&
                srcPort.equals(((SessionSplitTransformerKey) o).getSrcPort());
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(srcIp)
                .append(dstIp)
                .append(dstPort)
                .append(srcPort)
                .toHashCode();
    }

}
