package presidio.integration.performance.generators.tls;

import presidio.data.generators.event.performance.tls.clusters.TlsPerfClusterParams;

public class ClusterSizeFactory {

    public static TlsPerfClusterParams getSmallClusterParams() {
        return new TlsPerfClusterParams.Builder()
                .setDstIpSize(2)
                .setDstOrgSize(2)
                .setDstPortSize(2)
                .setHostnameSize(2)
                .setJa3Size(2)
                .setSrcNetnameSize(2)
                .setSslSubjectSize(2)
                .setLocationSize(2)
                .setSrcIpSize(2)
                .build();
    }

    public static TlsPerfClusterParams getMediumClusterParams() {
        return new TlsPerfClusterParams.Builder()
                .setDstIpSize(10)
                .setDstOrgSize(10)
                .setDstPortSize(10)
                .setJa3Size(10)
                .setSrcNetnameSize(10)
                .setSslSubjectSize(10)
                .setSrcIpSize(10)

                .setHostnameSize(10)
                .setLocationSize(10)
                .build();
    }

    public static TlsPerfClusterParams getLargeClusterParams() {
        return new TlsPerfClusterParams.Builder()
                .setDstIpSize(1000)
                .setDstOrgSize(1000)
                .setDstPortSize(900)
                .setJa3Size(1000)
                .setSrcNetnameSize(1000)
                .setSslSubjectSize(1000)
                .setSrcIpSize(900)

                .setHostnameSize(100)
                .setLocationSize(50)
                .build();
    }
}
