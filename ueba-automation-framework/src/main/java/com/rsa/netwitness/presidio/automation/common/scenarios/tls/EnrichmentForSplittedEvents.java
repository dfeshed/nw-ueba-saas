package com.rsa.netwitness.presidio.automation.common.scenarios.tls;

import presidio.data.domain.event.network.NETWORK_DIRECTION_TYPE;
import presidio.data.domain.event.network.NetworkEvent;
import presidio.data.generators.IBaseGenerator;
import presidio.data.generators.common.GeneratorException;
import presidio.data.generators.common.random.GaussianLongGenerator;
import presidio.data.generators.common.time.ITimeGenerator;
import presidio.data.generators.common.time.SingleSampleTimeGenerator;
import presidio.data.generators.event.network.NetworkEventsGenerator;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.stream.Stream;

public class EnrichmentForSplittedEvents extends NetworkScenarioBase {

    private final IBaseGenerator<Long> regularTrafficGenerator =
            new GaussianLongGenerator(1e9, 10e6);

    private final IBaseGenerator<Long> unusualTrafficGenerator =
            new GaussianLongGenerator(1.5e9, 10e6);


    public EnrichmentForSplittedEvents(int dataPeriod, int uncommonStartDay) {
        this.daysBackFrom = dataPeriod;
        this.daysBackFromAnomaly = uncommonStartDay;
    }


    @Override
    String getScenarioName() {
        return "splitted_events";
    }

    //
    public Stream<NetworkEvent> simpleEnreachment() throws GeneratorException {
        NetworkEventsGenerator firstSample = new NetworkEventsGenerator(singleSampleTimeGen(4));

        firstSample.modify()
                .setSSLSubjectEntityValue(SSLSubjEntity(0))
                .setJa3EntityValue(Ja3Entity(0))
                .fixJa3s()
                .fixSslCa();

        NetworkEvent firstEvent = firstSample.generateNext();
        firstEvent.setSessionSplit(0);
        firstEvent.setSourceIp("67.55.44.33");
        firstEvent.setSourcePort(666);
        firstEvent.setDstIp("33.44.55.66");
        firstEvent.setDestinationPort(1666);

        NetworkEvent secondEvent = new NetworkEvent(singleSampleTimeGen(3).getNext());
        secondEvent.setSessionSplit(1);
        secondEvent.setSourceIp("67.55.44.33");
        secondEvent.setSourcePort(666);
        secondEvent.setDstIp("33.44.55.66");
        secondEvent.setDestinationPort(1666);
        secondEvent.setDirection(NETWORK_DIRECTION_TYPE.OUTBOUND);
        secondEvent.setEventId("dsfkjhfdkjhfdshbkj");
        secondEvent.setNumOfBytesReceived(999999999);
        secondEvent.setNumOfBytesSent(999999999);

        return Stream.of(firstEvent, secondEvent);
    }

    private ITimeGenerator singleSampleTimeGen(int hourBackFromNow) {
        return new SingleSampleTimeGenerator(Instant.now().minus(hourBackFromNow, ChronoUnit.HOURS));
    }


    private Instant oneSample(int hourBackFromNow) {
        return Instant.now().minus(hourBackFromNow, ChronoUnit.HOURS);
    }
}
