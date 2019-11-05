package presidio.integration.performance.generators.tls;

import presidio.data.generators.event.performance.tls.TlsPerfClusterParams;

import java.time.Instant;

public class ClusterSizeFactory {

    public static TlsPerfClusterParams tlsParamsGroupA(Instant startInstant, Instant endInstant, double eventsPerDay, double tlsAlertsProbability) {
        return new TlsPerfClusterParams.Builder()
                .setJa3Size(10)
                .setSslSubjectSize(100)

                .setDstIpSize(10)
                .setDstOrgSize(10)
                .setDstPortSize(10)
                .setSrcNetnameSize(10)
                .setSrcIpSize(10)
                .setLocationSize(10)
                .setHostnameSize(10)

                .setRegularActivityStartHour(8)
                .setRegularActivityEndHour(18)
                .setAbnormalActivityStartHour(1)
                .setAbnormalActivityEndHour(5)
                .setStartInstant(startInstant)
                .setEndInstant(endInstant)

                .setEventsPerDay(eventsPerDay)
                .setOffPeekToActiveRatio(0.3)
                .setWeekendSkipEventProbability(0.4)
                .setAlertsProbability(tlsAlertsProbability)
                .build();
    }

    public static TlsPerfClusterParams getSessionSplitClusterParams(Instant startInstant, Instant endInstant, double eventsPerDay, double tlsAlertsProbability) {
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
                .setEventsPerDay(eventsPerDay)
                .setOffPeekToActiveRatio(0.3)
                .setAlertsProbability(tlsAlertsProbability)
                .build();
    }
}
