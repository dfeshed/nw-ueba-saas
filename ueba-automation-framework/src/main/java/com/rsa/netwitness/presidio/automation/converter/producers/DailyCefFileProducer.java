package com.rsa.netwitness.presidio.automation.converter.producers;

import com.rsa.netwitness.presidio.automation.converter.events.NetwitnessEvent;
import com.rsa.netwitness.presidio.automation.converter.formatters.EventFormatter;
import fortscale.common.general.Schema;

import java.util.List;
import java.util.Map;

class DailyCefFileProducer extends CefFilesPrinter implements EventsProducer<List<NetwitnessEvent>> {

    DailyCefFileProducer(EventFormatter<String> formatter) {
        super(formatter);
    }

    @Override
    public Map<Schema, Long> send(List<NetwitnessEvent> eventsList) {
        return printDailyFiles(eventsList);
    }
}
