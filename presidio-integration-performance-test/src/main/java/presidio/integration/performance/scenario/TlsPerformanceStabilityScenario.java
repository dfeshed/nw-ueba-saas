package presidio.integration.performance.scenario;

import presidio.data.generators.event.performance.tls.clusters.TlsEventsSimplePerfGen;
import presidio.data.generators.event.performance.tls.clusters.TlsPerfClusterParams;

import java.time.Instant;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static presidio.integration.performance.generators.tls.ClusterSizeFactory.*;

public class TlsPerformanceStabilityScenario {

    private final int LOW_SIZE_CLUSTERS = 5;
    private final int MEDIUM_SIZE_CLUSTERS = 2;
    private final int LARGE_SIZE_CLUSTERS = 1;

    private final Instant startInstant;
    private final Instant endInstant;
    private final double TLS_ALERTS_PROBABILITY;
    private final int TLS_GROUPS_MULTIPLIER;
    private final int millisBetweenEvents;

    public List<TlsEventsSimplePerfGen> tlsEventsGenerators = new LinkedList<>();

    private List<TlsEventsSimplePerfGen> tlsGroupSmall = new LinkedList<>();
    private List<TlsEventsSimplePerfGen> tlsGroupMedium = new LinkedList<>();
    private List<TlsEventsSimplePerfGen> tlsGroupLarge = new LinkedList<>();


    public TlsPerformanceStabilityScenario(Instant startInstant, Instant endInstant, int tlsGroupsMultiplier, double tlsAlertsProbability, int millisBetweenEvents) {
        this.startInstant = startInstant;
        this.endInstant = endInstant;
        TLS_ALERTS_PROBABILITY = tlsAlertsProbability;
        TLS_GROUPS_MULTIPLIER =tlsGroupsMultiplier;
        this.millisBetweenEvents = millisBetweenEvents;
        init();
    }

    private void init() {
        TlsPerfClusterParams smallClusterParams = getSmallClusterParams(TLS_ALERTS_PROBABILITY, startInstant, endInstant, millisBetweenEvents);
        TlsPerfClusterParams mediumClusterParams = getMediumClusterParams(TLS_ALERTS_PROBABILITY, startInstant, endInstant, millisBetweenEvents);
        TlsPerfClusterParams largeClusterParams = getLargeClusterParams(TLS_ALERTS_PROBABILITY, startInstant, endInstant, millisBetweenEvents);

        tlsGroupSmall = IntStream.range(0, LOW_SIZE_CLUSTERS * TLS_GROUPS_MULTIPLIER).boxed()
                .map(index -> new TlsEventsSimplePerfGen(smallClusterParams)).collect(toList());

        tlsGroupMedium = IntStream.range(0, MEDIUM_SIZE_CLUSTERS * TLS_GROUPS_MULTIPLIER).boxed()
                .map(index -> new TlsEventsSimplePerfGen(mediumClusterParams)).collect(toList());

        tlsGroupLarge = IntStream.range(0, LARGE_SIZE_CLUSTERS * TLS_GROUPS_MULTIPLIER).boxed()
                .map(index -> new TlsEventsSimplePerfGen(largeClusterParams)).collect(toList());

        tlsEventsGenerators = Stream.of(
                tlsGroupSmall.stream(),
                tlsGroupMedium.stream(),
                tlsGroupLarge.stream())
                .flatMap(a -> a)
                .collect(toList());
    }

}
