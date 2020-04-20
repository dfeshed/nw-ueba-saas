package com.rsa.netwitness.presidio.automation.converter.producers;

import com.rsa.netwitness.presidio.automation.converter.events.NetwitnessEvent;
import com.rsa.netwitness.presidio.automation.converter.formatters.EventFormatter;
import fortscale.common.general.Schema;

import java.util.Map;
import java.util.stream.Stream;

class HourlyCefFileProducer extends CefFilesPrinter implements EventsProducer<NetwitnessEvent> {

    HourlyCefFileProducer(EventFormatter<NetwitnessEvent, String> formatter) {
        super(formatter);
    }

    @Override
    public Map<Schema, Long> send(Stream<NetwitnessEvent> eventsList) {
        return printHourlyFiles(eventsList);
    }
}
