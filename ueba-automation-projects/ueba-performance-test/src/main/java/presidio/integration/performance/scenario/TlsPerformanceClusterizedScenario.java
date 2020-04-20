package presidio.integration.performance.scenario;

import presidio.data.domain.event.network.TlsEvent;
import presidio.data.generators.event.AbstractEventGenerator;
import presidio.data.generators.event.performance.tls.TlsEventsClusteredPerfGen;
import presidio.data.generators.event.performance.tls.TlsPerfClusterParams;
import presidio.data.generators.event.performance.tls.TlsSessionSplitSimplePerfGen;

import java.time.Instant;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static presidio.integration.performance.generators.tls.ClusterSizeFactory.getSessionSplitClusterParams;
import static presidio.integration.performance.generators.tls.ClusterSizeFactory.tlsParamsGroupA;

public class TlsPerformanceClusterizedScenario {

    private final int SMALL_SIZE_CLUSTERS = 200;
    private final int LARGE_SIZE_CLUSTERS = 15;
    private final int SESSION_SPLIT_CLUSTER = 1;

    private final int SESSION_SPLIT_LIMIT = 100;

    private final Instant startInstant;
    private final Instant endInstant;
    private final double TLS_ALERTS_PROBABILITY;
    private final int TLS_GROUPS_MULTIPLIER;
    private final int TLS_LARGE_GROUP_MILLIS_BETWEEN_EVENTS = 41;
    private final int TLS_SESSION_SPLIT_GROUP_MILLIS_BETWEEN_EVENTS = 1000;
    private final int millisBetweenEvents;

    public List<AbstractEventGenerator<TlsEvent>> tlsEventsGenerators = new LinkedList<>();

    public TlsPerformanceClusterizedScenario(Instant startInstant, Instant endInstant, int tlsGroupsMultiplier, double tlsAlertsProbability, int millisBetweenEvents) {
        this.startInstant = startInstant;
        this.endInstant = endInstant;
        TLS_ALERTS_PROBABILITY = tlsAlertsProbability;
        TLS_GROUPS_MULTIPLIER = tlsGroupsMultiplier;
        this.millisBetweenEvents = millisBetweenEvents;
        init();
    }

    private void init() {
        TlsPerfClusterParams sessionSplitClusterParams = getSessionSplitClusterParams(startInstant, endInstant, 1e6, TLS_ALERTS_PROBABILITY);
        TlsPerfClusterParams smallClusterParams = tlsParamsGroupA(startInstant, endInstant, 1e4, TLS_ALERTS_PROBABILITY);

        List<TlsEventsClusteredPerfGen> tlsGroupSmall = IntStream.range(0, SMALL_SIZE_CLUSTERS * TLS_GROUPS_MULTIPLIER).boxed()
                .map(index -> new TlsEventsClusteredPerfGen(smallClusterParams)).collect(toList());

        List<TlsSessionSplitSimplePerfGen> tlsGroupSessionSplit = IntStream.range(0, SESSION_SPLIT_CLUSTER).boxed()
                .map(index -> new TlsSessionSplitSimplePerfGen(sessionSplitClusterParams, SESSION_SPLIT_LIMIT)).collect(toList());


        tlsEventsGenerators = Stream.of(
                tlsGroupSessionSplit.stream(),
                tlsGroupSmall.stream())
                .flatMap(a -> a)
                .collect(toList());
    }

}
