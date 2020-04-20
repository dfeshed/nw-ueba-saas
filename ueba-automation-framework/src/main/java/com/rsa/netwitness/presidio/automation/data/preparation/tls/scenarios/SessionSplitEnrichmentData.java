package com.rsa.netwitness.presidio.automation.data.preparation.tls.scenarios;

import com.google.common.collect.Lists;
import presidio.data.domain.event.network.NETWORK_DIRECTION_TYPE;
import presidio.data.domain.event.network.TlsEvent;
import presidio.data.generators.common.GeneratorException;
import presidio.data.generators.common.time.ITimeGenerator;
import presidio.data.generators.common.time.SingleSampleTimeGenerator;
import presidio.data.generators.event.tls.TlsRangeEventsGen;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.function.BiConsumer;
import java.util.function.UnaryOperator;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.time.Instant.now;
import static java.time.temporal.ChronoUnit.DAYS;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;

public class SessionSplitEnrichmentData extends NetworkScenarioBase {

    private static long historicalDaysBack;

    public SessionSplitEnrichmentData(long historicalDaysBack) {
        this.historicalDaysBack = historicalDaysBack;
    }

    @Override
    String getScenarioName() {
        return SCENARIO_NAME;
    }
    private static final String SCENARIO_NAME = "session_split";
    public static final String MARKER = "MARKER#";   // required for validations to find specific test event
    private final Instant EVENTS_END_TIME = now().truncatedTo(DAYS);

    public static final TestDataParameters simpleEnrichmentTestDataParams = generateDataParamsByIndex(1);
    public static final TestDataParameters maxIntervalTestDataParams = generateDataParamsByIndex(2);
    public static final TestDataParameters missingSessionsInTheMiddleTestDataParams = generateDataParamsByIndex(3);
    public static final TestDataParameters duplicatedSessionsInTheMiddleTestDataParams = generateDataParamsByIndex(4);
    public static final TestDataParameters missingZeroSessionTestDataParams = generateDataParamsByIndex(5);
    public static final TestDataParameters newSessionOpenedFirstSession = generateDataParamsByIndex(6, 61);
    public static final TestDataParameters newSessionOpenedSecondSession = generateDataParamsByIndex(6, 62);
    public static final TestDataParameters newSessionOpenedAndZeroEventIsMissing = generateDataParamsByIndex(7);
    public static final TestDataParameters zeroSessionEventHasNullJa3AndSslSubject = generateDataParamsByIndex(8);
    public static final TestDataParameters secondEventHaveJa3AndSslSubject = generateDataParamsByIndex(9);


    public Stream<TlsEvent> generateAll() {
        return Stream.of(
                getBaseTestEvents(),
                getMaxIntervalTestEvents(),
                getMissingSessionsInTheMiddleTestEvents(),
                getDuplicatedSessionsInTheMiddleTestEvents(),
                getMissingZeroSessionTestEvents(),
                newSessionOpenedTestEvents(),
                newSessionOpenedAndZeroEventIsMissingTestEvents(),
                zeroSessionEventHasNullJa3AndSslSubjectTestEvents(),
                secondEventHaveJa3AndSslSubjectTestEvents()
        ).flatMap(e -> e);
    }


    public Stream<TlsEvent> sslSubjectsForPreProcessing() {
        TlsRangeEventsGen eventsGenInit = new TlsRangeEventsGen(1);
        List<String> sslSubjects = this.generateAll().map(TlsEvent::getSslSubject).distinct().collect(toList());
        Instant preProcessingTime = now().truncatedTo(DAYS).minus(historicalDaysBack, DAYS).plus(1, ChronoUnit.HOURS);

        return sslSubjects.parallelStream()
                .map(e -> {
                    TlsEvent tlsEvent = null;
                    try {
                        tlsEvent = eventsGenInit.generateNext();
                    } catch (GeneratorException ex) {
                        ex.printStackTrace();
                    }

                    Objects.requireNonNull(tlsEvent).setSslSubject(e);
                    Objects.requireNonNull(tlsEvent).setNumOfBytesSent(1);
                    Objects.requireNonNull(tlsEvent).setDateTime(preProcessingTime);
                    return tlsEvent;
                });
    }

    private Stream<TlsEvent> getBaseTestEvents() {
        return generateSessionSplitHourlyEvents(EVENTS_END_TIME, 10, simpleEnrichmentTestDataParams);
    }

    private Stream<TlsEvent> getMaxIntervalTestEvents() {
        return generateSessionSplitHourlyEvents(EVENTS_END_TIME, 13, maxIntervalTestDataParams);
    }

    // 012457..9
    private Stream<TlsEvent> getMissingSessionsInTheMiddleTestEvents() {
        Random r = new Random();
        List<Integer> indexesToFilter = r.ints(2, 9).limit(3).boxed().collect(toList());
        int minIndex = indexesToFilter.stream().sorted().findFirst().get();

        return generateSessionSplitHourlyEvents(EVENTS_END_TIME, 10, missingSessionsInTheMiddleTestDataParams)
                .peek(e -> markPreviousEvent.accept(minIndex, e))
                .filter(e -> !indexesToFilter.contains(e.getSessionSplit()));
    }

    // 01123445..9
    private Stream<TlsEvent> getDuplicatedSessionsInTheMiddleTestEvents() {
        Random r = new Random();
        List<Integer> duplicatesIndexes = r.ints(2, 9).limit(3).boxed().collect(toList());
        int minIndex = duplicatesIndexes.stream().sorted().findFirst().get();

        Stream<TlsEvent> mainStream = generateSessionSplitHourlyEvents(EVENTS_END_TIME, 10, duplicatedSessionsInTheMiddleTestDataParams)
                .peek(e -> markPreviousEvent.accept(minIndex, e));

        List<TlsEvent> duplicates = generateSessionSplitHourlyEvents(EVENTS_END_TIME, 10, duplicatedSessionsInTheMiddleTestDataParams)
                .filter(e -> duplicatesIndexes.contains(e.getSessionSplit())).collect(toList());
        // adding 5 min to the duplicates
        duplicates.forEach(e -> e.setDateTime(e.getDateTime().plusSeconds(300)));
        return Stream.concat(mainStream, duplicates.stream());
    }

    private Stream<TlsEvent> getMissingZeroSessionTestEvents() {
        return generateSessionSplitHourlyEvents(EVENTS_END_TIME, 3, missingZeroSessionTestDataParams)
                .filter(e -> e.getSessionSplit() != 0);
    }

    // 012012
    private Stream<TlsEvent> newSessionOpenedTestEvents() {
        List<TlsEvent> session1 = generateSessionSplitHourlyEvents(EVENTS_END_TIME, 3, newSessionOpenedFirstSession).collect(toList());
        Instant secondSessionEndTime = session1.stream().map(TlsEvent::getDateTime).min(comparing(Instant::getEpochSecond)).get();
        Stream<TlsEvent> session2 = generateSessionSplitHourlyEvents(secondSessionEndTime, 3, newSessionOpenedSecondSession);
        return Stream.concat(session1.stream(), session2);
    }

    // 0123245
    private Stream<TlsEvent> newSessionOpenedAndZeroEventIsMissingTestEvents() {
        List<TlsEvent> session = generateSessionSplitHourlyEvents(EVENTS_END_TIME, 7, newSessionOpenedAndZeroEventIsMissing)
                .sorted(comparing(TlsEvent::getSessionSplit))
                .collect(toList());

        session.get(4).setSessionSplit(2);
        session.get(5).setSessionSplit(4);
        session.get(6).setSessionSplit(5);
        return session.stream();
    }

    private Stream<TlsEvent> zeroSessionEventHasNullJa3AndSslSubjectTestEvents() {
        List<TlsEvent> events = generateSessionSplitHourlyEvents(EVENTS_END_TIME, 3, zeroSessionEventHasNullJa3AndSslSubject)
                .sorted(comparing(TlsEvent::getSessionSplit))
                .collect(toList());

        events.get(0).setSslSubject(null);
        events.get(0).setJa3(null);
        return events.stream();
    }

    private Stream<TlsEvent> secondEventHaveJa3AndSslSubjectTestEvents() {
        List<TlsEvent> events = generateSessionSplitHourlyEvents(EVENTS_END_TIME, 3, secondEventHaveJa3AndSslSubject)
                .sorted(comparing(TlsEvent::getSessionSplit))
                .collect(toList());

        events.get(2).setSslSubject("dummy");
        events.get(2).setJa3("dummy");
        return events.stream();
    }




    /**
     * utilities
     */

    public static class TestDataParameters {
        public final String id;
        public final String srcIp;
        public final int srcPort;
        public final String dstIp;
        public final int dstPort;
        public final String sslSubject;
        public final String ja3;
        public final String ja3s;
        public final String sslCa;

        private TestDataParameters(String id, String srcIp, int srcPort, String dstIp, int dstPort, String sslSubject, String ja3, String ja3s, String sslCa) {
            this.id = id;
            this.srcIp = srcIp;
            this.srcPort = srcPort;
            this.dstIp = dstIp;
            this.dstPort = dstPort;
            this.sslSubject = sslSubject;
            this.ja3 = ja3;
            this.ja3s = ja3s;
            this.sslCa = sslCa;
        }
    }

    private static TestDataParameters generateDataParamsByIndex(int index) {
        return generateDataParamsByIndex(index, index);
    }

    private static TestDataParameters generateDataParamsByIndex(int srcDstIndex, int enrichmentFieldsIndex) {
        return new TestDataParameters(
                SCENARIO_NAME.concat("_").concat(String.valueOf(enrichmentFieldsIndex)),
                "66.55.44.33",
                666,
                "33.44.55.66",
                666 + 1000 * srcDstIndex,
                "session_split_simple_test_" + enrichmentFieldsIndex,
                "session_split_simple_test_" + enrichmentFieldsIndex,
                "session_split_simple_test_" + enrichmentFieldsIndex,
                "session_split_simple_test_" + enrichmentFieldsIndex
        );
    }

    private BiConsumer<Integer, TlsEvent> markPreviousEvent = (i, e) -> {
        if (e.getSessionSplit() == i - 1) {
            e.setEventId(MARKER + e.getEventId());
            e.setDestinationOrganization(MARKER + e.getDestinationOrganization());
        }
    };

    private Stream<TlsEvent> generateSessionSplitHourlyEvents(Instant lastEventTime, int eventsAmount, TestDataParameters params) {
        UnaryOperator<TlsEvent> commonFieldsSetup = event -> {
            event.setSourceIp(params.srcIp);
            event.setSourcePort(params.srcPort);
            event.setDstIp(params.dstIp);
            event.setDestinationPort(params.dstPort);
            return event;
        };

        UnaryOperator<TlsEvent> enrichmentFieldsSetup = event -> {
            if (event.getSessionSplit() == 0) {
                event.setSslSubject(params.sslSubject);
                event.setJa3(params.ja3);
                event.setJa3s(params.ja3s);
                event.setSslCa(Lists.newArrayList(params.sslCa));
            }
            return event;
        };

        List<TlsEvent> event = IntStream.range(0, eventsAmount)
                .mapToObj(i -> hourlyEventsWithSessionSplit(eventsAmount - i - 1, lastEventTime, i + 1, params.id))
                .map(commonFieldsSetup)
                .map(enrichmentFieldsSetup)
                .collect(toList());

        return event.stream();
    }

    private ITimeGenerator singleSampleTimeGen(Instant lastEventTime, int hourBackFromLastEventTime) {
        return new SingleSampleTimeGenerator(lastEventTime.minus(hourBackFromLastEventTime, ChronoUnit.HOURS));
    }


    /**  'id' (event identifier) is placed into DestinationOrganization field   */
    private TlsEvent hourlyEventsWithSessionSplit(int splitIndex, Instant lastEventTime, int hoursBackward, String id) {
        try {
            TlsEvent event = new TlsEvent(singleSampleTimeGen(lastEventTime, hoursBackward).getNext());
            event.setEventId(id.concat("#").concat(String.valueOf(splitIndex)).concat("#").concat(String.valueOf(System.nanoTime())));
            event.setDestinationOrganization(id.concat("#").concat(String.valueOf(splitIndex)).concat("#").concat(String.valueOf(System.nanoTime())));
            event.setSessionSplit(splitIndex);
            event.setDirection(NETWORK_DIRECTION_TYPE.OUTBOUND);
            event.setNumOfBytesReceived((long) 1.3e9);
            event.setNumOfBytesSent((long) 1.4e9);
            return event;
        } catch (GeneratorException e) {
            e.printStackTrace();
        }
        return null;
    }

}
