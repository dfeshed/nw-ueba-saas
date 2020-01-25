package presidio.integration.performance.scenario;

import presidio.data.domain.event.Event;
import presidio.data.domain.event.network.TlsEvent;
import presidio.data.generators.common.GeneratorException;
import presidio.data.generators.event.AbstractEventGenerator;
import presidio.data.generators.event.performance.PerformanceStabilityScenario;
import presidio.data.generators.event.performance.tls.TlsEventsSimplePerfGen;
import presidio.data.generators.event.performance.tls.TlsPerfClusterParams;
import presidio.data.generators.event.performance.tls.TlsSessionSplitSimplePerfGen;

import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
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
        List<Event> events = new ArrayList<>();

        while (events.size() < numOfEventsToGenerate) {
            Optional<GeneratorWithNextEventTime> minTimeGen = tlsEventsGenerators.parallelStream()
                    .map(e -> new GeneratorWithNextEventTime(e.hasNext(), e))
                    .filter(e -> e.nextTime != null)
                    .min(GeneratorWithNextEventTime::compareTo);

            if (minTimeGen.isPresent()) {
                events.add(generateEvent(minTimeGen.get().generator));
            } else {
                break;
            }
        }

        return events;
    }

    private class GeneratorWithNextEventTime implements Comparable<GeneratorWithNextEventTime> {
        final Instant nextTime;
        final AbstractEventGenerator<TlsEvent> generator;

        GeneratorWithNextEventTime(Instant nextTime, AbstractEventGenerator<TlsEvent> generator) {
            this.nextTime = nextTime;
            this.generator = generator;
        }

        @Override
        public int compareTo(GeneratorWithNextEventTime other) {
            return this.nextTime.compareTo(other.nextTime);
        }
    }


    private Event generateEvent(AbstractEventGenerator gen) {
        if (gen.hasNext() != null) {
            try {
                return gen.generateNext();
            } catch (GeneratorException e) {
                e.printStackTrace();
            }
        }
        return null;
    }



}
