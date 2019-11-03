package presidio.integration.performance.generators.tls;

import presidio.data.generators.event.performance.tls.clusters.TlsPerfClusterParams;

import java.time.Instant;

public class ClusterSizeFactory {

    public static TlsPerfClusterParams getSmallClusterParams(double tlsAlertsProbability, Instant startInstant, Instant endInstant, int millisBetweenEvents) {
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

    public static TlsPerfClusterParams getMediumClusterParams(double tlsAlertsProbability, Instant startInstant, Instant endInstant, int millisBetweenEvents) {
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
                .setRegularActivityStartHour(15)
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
                .setDstIpSize(1000)
                .setDstOrgSize(1000)
                .setDstPortSize(900)
                .setJa3Size(1000)
                .setSrcNetnameSize(1000)
                .setSslSubjectSize(1000)
                .setSrcIpSize(900)

                .setHostnameSize(100)
                .setLocationSize(50)
                .setRegularActivityStartHour(6)
                .setRegularActivityEndHour(18)
                .setAbnormalActivityStartHour(1)
                .setAbnormalActivityEndHour(5)
                .setStartInstant(startInstant)
                .setEndInstant(endInstant)
                .setMillisBetweenEvents(millisBetweenEvents)
                .setAlertsProbability(tlsAlertsProbability)
                .build();
    }

}
