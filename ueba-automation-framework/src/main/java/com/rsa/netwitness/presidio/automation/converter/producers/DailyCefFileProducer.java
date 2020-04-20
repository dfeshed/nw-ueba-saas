package com.rsa.netwitness.presidio.automation.converter.producers;

import com.rsa.netwitness.presidio.automation.converter.events.NetwitnessEvent;
import com.rsa.netwitness.presidio.automation.converter.formatters.EventFormatter;
import fortscale.common.general.Schema;

import java.util.Map;
import java.util.stream.Stream;

class DailyCefFileProducer extends CefFilesPrinter implements EventsProducer<NetwitnessEvent> {

    DailyCefFileProducer(EventFormatter<NetwitnessEvent, String> formatter) {
        super(formatter);
    }

    @Override
    public Map<Schema, Long> send(Stream<NetwitnessEvent> eventsList) {
        return printDailyFiles(eventsList);
    }
}
