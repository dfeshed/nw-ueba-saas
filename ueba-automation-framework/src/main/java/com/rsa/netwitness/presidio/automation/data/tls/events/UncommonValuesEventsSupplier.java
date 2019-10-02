package com.rsa.netwitness.presidio.automation.data.tls.events;

import com.google.common.collect.Lists;
import com.rsa.netwitness.presidio.automation.data.tls.feilds.TlsEventsGen;
import com.rsa.netwitness.presidio.automation.data.tls.model.EventsGenerator;
import presidio.data.domain.event.network.NetworkEvent;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class UncommonValuesEventsSupplier extends IndicatorGen implements EventsGenerator {
    List<TlsEventsGen> eventGenerators = Lists.newLinkedList();

    public UncommonValuesEventsSupplier(int dataPeriod, int uncommonStartDay) {
        daysBackFrom = dataPeriod;
        daysBackFromAnomaly = uncommonStartDay;
    }

    public UncommonValuesEventsSupplier setCommonValuesGen(final TlsEventsGen gen) {
        TlsEventsGen copyGen = gen.copy();
        copyGen.setTimeGenerator(getCommonValuesTimeGen());
        eventGenerators.add(copyGen);
        return this;
    }

    public UncommonValuesEventsSupplier setUncommonValuesHistoryGen(final TlsEventsGen gen) {
        TlsEventsGen copyGen = gen.copy();
        copyGen.setTimeGenerator(getUncommonValuesHistoryTimeGen());
        eventGenerators.add(copyGen);
        return this;
    }

    public UncommonValuesEventsSupplier setUncommonValuesAnomalyGen(final TlsEventsGen gen) {
        TlsEventsGen copyGen = gen.copy();
        copyGen.setTimeGenerator(getUncommonValuesAnomalyTimeGen());
        eventGenerators.add(copyGen);
        return this;
    }


    @Override
    public Stream<NetworkEvent> generate() {
        assertThat(eventGenerators).isNotEmpty();
        List<NetworkEvent> events = Lists.newLinkedList();
        eventGenerators.forEach(generator -> events.addAll(generate.apply(generator)));
        return events.stream();
    }
}
