package com.rsa.netwitness.presidio.automation.converter.producers;

import com.rsa.netwitness.presidio.automation.converter.events.NetwitnessEvent;
import com.rsa.netwitness.presidio.automation.converter.formatters.EventFormatter;
import fortscale.common.general.Schema;

import java.util.List;
import java.util.Map;

class DailyBrokerCefProducer extends CefFilesPrinter implements EventsProducer {

    DailyBrokerCefProducer(EventFormatter<String> formatter) {
        super(formatter);
    }

    @Override
    public Map<Schema, Long> send(List<NetwitnessEvent> eventsList) {
        printDailyFiles(eventsList);
        return sendToBroker();
    }
}
