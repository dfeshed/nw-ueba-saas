package presidio.integration.performance.scenario;

import com.google.common.collect.Iterators;
import com.google.common.collect.UnmodifiableIterator;
import org.assertj.core.util.Lists;
import presidio.data.domain.event.Event;
import presidio.data.domain.event.network.TlsEvent;
import presidio.data.generators.event.AbstractEventGenerator;
import presidio.data.generators.event.IEventGenerator;
import presidio.data.generators.event.performance.PerformanceStabilityScenario;
import presidio.data.generators.event.performance.tls.TlsEventsSimplePerfGen;
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

public class TlsPerformanceStabilityScenario extends PerformanceStabilityScenario {

    private final int SESSION_SPLIT_CLUSTER = 1;
    private final int SESSION_SPLIT_LIMIT = 100;
    private final int SESSION_SPLIT_FACTOR = 660;

    private final Instant startInstant;
    private final Instant endInstant;
    private final double TLS_ALERTS_PROBABILITY;
    private final int TLS_GROUPS;
    private final double TLS_EVENTS_PER_DAY_PER_GROUP;
    private final double TLS_SESSSION_SPLIT_EVENTS_PER_DAY;

    public List<AbstractEventGenerator<TlsEvent>> tlsEventsGenerators = new LinkedList<>();

    public TlsPerformanceStabilityScenario(Instant startInstant, Instant endInstant,
                                           double tlsAlertsProbability, int groupsToCreate, double tlsEventsPerDayPerGroup) {
        super(startInstant, endInstant, 0);
        this.startInstant = startInstant;
        this.endInstant = endInstant;
        TLS_ALERTS_PROBABILITY = tlsAlertsProbability;
        TLS_GROUPS = groupsToCreate;
        TLS_EVENTS_PER_DAY_PER_GROUP = tlsEventsPerDayPerGroup;
        TLS_SESSSION_SPLIT_EVENTS_PER_DAY = groupsToCreate * tlsEventsPerDayPerGroup / SESSION_SPLIT_FACTOR;
        initBuilders();
    }

    @Override
    protected void initBuilders() {
        TlsPerfClusterParams sessionSplitClusterParams = getSessionSplitClusterParams(startInstant, endInstant, TLS_SESSSION_SPLIT_EVENTS_PER_DAY, 0);
        TlsPerfClusterParams smallClusterParams = tlsParamsGroupA(startInstant, endInstant, TLS_EVENTS_PER_DAY_PER_GROUP, TLS_ALERTS_PROBABILITY);

        List<TlsEventsSimplePerfGen> tlsGroupSmall = IntStream.range(0, TLS_GROUPS).boxed()
                .map(index -> new TlsEventsSimplePerfGen(smallClusterParams)).collect(toList());


        List<TlsSessionSplitSimplePerfGen> tlsGroupSessionSplit = IntStream.range(0, SESSION_SPLIT_CLUSTER).boxed()
                .map(index -> new TlsSessionSplitSimplePerfGen(sessionSplitClusterParams, SESSION_SPLIT_LIMIT)).collect(toList());


        tlsEventsGenerators = Stream.of(
                tlsGroupSessionSplit.stream(),
                tlsGroupSmall.stream())
                .flatMap(a -> a)
                .collect(toList());
    }

    @Override
    public List<Event> generateEvents(int numOfEventsToGenerate) {
        Stream<Event> events = tlsEventsGenerators.stream().flatMap(IEventGenerator::generateToStream);
        UnmodifiableIterator<List<Event>> partition = Iterators.partition(events.iterator(), numOfEventsToGenerate);
        if (partition.hasNext()) {
            return partition.next();
        } else {
            return Lists.emptyList();
        }
    }

}
