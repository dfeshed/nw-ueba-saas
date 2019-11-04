package presidio.integration.performance.generators.tls;

import presidio.data.generators.event.performance.tls.clusters.TlsPerfClusterParams;

import java.time.Instant;

public class ClusterSizeFactory {

    public static TlsPerfClusterParams getSmallClusterParams(double tlsAlertsProbability, Instant startInstant, Instant endInstant, int millisBetweenEvents) {
        return new TlsPerfClusterParams.Builder()
                .setJa3Size(10)
                .setSslSubjectSize(10)

                .setDstIpSize(10)
                .setDstOrgSize(10)
                .setDstPortSize(10)
                .setSrcNetnameSize(10)
                .setSrcIpSize(10)
                .setLocationSize(10)
                .setHostnameSize(10)
                .setRegularActivityStartHour(13)
                .setRegularActivityEndHour(23)
                .setAbnormalActivityStartHour(1)
                .setAbnormalActivityEndHour(5)
                .setStartInstant(startInstant)
                .setEndInstant(endInstant)
                .setMillisBetweenEvents(millisBetweenEvents)
                .setAlertsProbability(tlsAlertsProbability)
                .build();
    }

    public static TlsPerfClusterParams getLargeClusterParams(double tlsAlertsProbability, Instant startInstant, Instant endInstant, int millisBetweenEvents) {
        return new TlsPerfClusterParams.Builder()
                .setJa3Size(1000)
                .setSslSubjectSize(1000)

                .setDstIpSize(10)
                .setDstOrgSize(10)
                .setDstPortSize(10)
                .setSrcNetnameSize(10)
                .setSrcIpSize(10)
                .setHostnameSize(10)
                .setLocationSize(10)
                .setRegularActivityStartHour(7)
                .setRegularActivityEndHour(17)
                .setAbnormalActivityStartHour(1)
                .setAbnormalActivityEndHour(5)
                .setStartInstant(startInstant)
                .setEndInstant(endInstant)
                .setMillisBetweenEvents(millisBetweenEvents)
                .setAlertsProbability(tlsAlertsProbability)
                .build();
    }

    public static TlsPerfClusterParams getSessionSplitClusterParams(double tlsAlertsProbability, Instant startInstant, Instant endInstant, int millisBetweenEvents) {
        return new TlsPerfClusterParams.Builder()
                .setJa3Size(10)
                .setSslSubjectSize(10)

                .setDstIpSize(10)
                .setDstOrgSize(10)
                .setDstPortSize(10)
                .setSrcNetnameSize(10)
                .setSrcIpSize(10)
                .setLocationSize(10)
                .setHostnameSize(10)
                .setRegularActivityStartHour(8)
                .setRegularActivityEndHour(17)
                .setAbnormalActivityStartHour(1)
                .setAbnormalActivityEndHour(5)
                .setStartInstant(startInstant)
                .setEndInstant(endInstant)
                .setMillisBetweenEvents(millisBetweenEvents)
                .setAlertsProbability(tlsAlertsProbability)
                .build();
    }

}
