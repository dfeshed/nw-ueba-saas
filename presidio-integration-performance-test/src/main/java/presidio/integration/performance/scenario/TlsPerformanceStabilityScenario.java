package presidio.integration.performance.scenario;

import presidio.data.domain.event.network.TlsEvent;
import presidio.data.generators.event.AbstractEventGenerator;
import presidio.data.generators.event.performance.tls.clusters.TlsEventsSimplePerfGen;
import presidio.data.generators.event.performance.tls.clusters.TlsPerfClusterParams;
import presidio.data.generators.event.performance.tls.clusters.TlsSessionSplitSimplePerfGen;

import java.time.Instant;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static presidio.integration.performance.generators.tls.ClusterSizeFactory.*;

public class TlsPerformanceStabilityScenario {

    private final int SMALL_SIZE_CLUSTERS = 100;
    private final int MEDIUM_SIZE_CLUSTERS = 1;
    private final int LARGE_SIZE_CLUSTERS = 1;
    private final int SESSION_SPLIT_CLUSTER = 10;
    private final int SESSION_SPLIT_LIMIT = 50;

    private final Instant startInstant;
    private final Instant endInstant;
    private final double TLS_ALERTS_PROBABILITY;
    private final int TLS_GROUPS_MULTIPLIER;
    private final int millisBetweenEvents;

    public List<AbstractEventGenerator<TlsEvent>> tlsEventsGenerators = new LinkedList<>();

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

        List<TlsEventsSimplePerfGen> tlsGroupSmall = IntStream.range(0, SMALL_SIZE_CLUSTERS * TLS_GROUPS_MULTIPLIER).boxed()
                .map(index -> new TlsEventsSimplePerfGen(smallClusterParams)).collect(toList());

        List<TlsEventsSimplePerfGen> tlsGroupMedium = IntStream.range(0, MEDIUM_SIZE_CLUSTERS * TLS_GROUPS_MULTIPLIER).boxed()
                .map(index -> new TlsEventsSimplePerfGen(mediumClusterParams)).collect(toList());

        List<TlsEventsSimplePerfGen> tlsGroupLarge = IntStream.range(0, LARGE_SIZE_CLUSTERS * TLS_GROUPS_MULTIPLIER).boxed()
                .map(index -> new TlsEventsSimplePerfGen(largeClusterParams)).collect(toList());


        List<TlsSessionSplitSimplePerfGen> tlsGroupSessionSplit = IntStream.range(0, SESSION_SPLIT_CLUSTER).boxed()
                .map(index -> new TlsSessionSplitSimplePerfGen(smallClusterParams, SESSION_SPLIT_LIMIT)).collect(toList());


        tlsEventsGenerators = Stream.of(
                tlsGroupSessionSplit.stream(),
                tlsGroupSmall.stream(),
                tlsGroupMedium.stream(),
                tlsGroupLarge.stream())
                .flatMap(a -> a)
                .collect(toList());
    }

}
