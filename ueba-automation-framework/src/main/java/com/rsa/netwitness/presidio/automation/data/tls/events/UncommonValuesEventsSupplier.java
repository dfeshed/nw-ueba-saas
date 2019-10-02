package com.rsa.netwitness.presidio.automation.data.tls.events;

import com.rsa.netwitness.presidio.automation.data.tls.feilds.TlsEventsGen;
import com.rsa.netwitness.presidio.automation.data.tls.model.EventsGenerator;
import presidio.data.domain.event.network.NetworkEvent;

import java.util.List;
import java.util.stream.Stream;

public class UncommonValuesEventsSupplier extends IndicatorGen implements EventsGenerator {
    TlsEventsGen commonValuesGen, uncommonValuesGen;


    public UncommonValuesEventsSupplier(TlsEventsGen commonValuesGen, TlsEventsGen uncommonValuesGen) {
        this.commonValuesGen = commonValuesGen;
        this.uncommonValuesGen = uncommonValuesGen;
    }

    @Override
    public Stream<NetworkEvent> generate() {
        commonValuesGen.setTimeGenerator(getHistoryOfUncommonValuesTimeGen());
        List<NetworkEvent> events = generate.apply(commonValuesGen);
        uncommonValuesGen.setTimeGenerator(getUncommonValuesTimeGen());
        events.addAll(generate.apply(uncommonValuesGen));
        return events.stream();
    }
}
