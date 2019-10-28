package presidio.data.generators.event.performance.tls.clusters;

public class TlsPerfClusterParams {

    public final int hostnameSize;
    public final int dstPortSize;
    public final int ja3Size;
    public final int sslSubjectSize;
    public final int dstOrgSize;
    public final int srcNetnameSize;
    public final int locationSize;
    public final int srcIpSize;
    public final int dstIpSize;


    public TlsPerfClusterParams(int hostnameSize, int dstPortSize, int ja3Size, int sslSubjectSize, int dstOrgSize, int srcNetnameSize, int locationSize, int srcIpSize, int dstIpSize) {
        this.hostnameSize = hostnameSize;
        this.dstPortSize = dstPortSize;
        this.ja3Size = ja3Size;
        this.sslSubjectSize = sslSubjectSize;
        this.dstOrgSize = dstOrgSize;
        this.srcNetnameSize = srcNetnameSize;
        this.locationSize = locationSize;
        this.srcIpSize = srcIpSize;
        this.dstIpSize = dstIpSize;
    }
}
