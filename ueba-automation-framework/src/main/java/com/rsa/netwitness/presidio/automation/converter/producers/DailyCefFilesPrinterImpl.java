package com.rsa.netwitness.presidio.automation.converter.producers;

import fortscale.common.general.Schema;
import com.rsa.netwitness.presidio.automation.converter.events.ConverterEventBase;

import java.util.List;
import java.util.Map;

public class DailyCefFilesPrinterImpl extends CefFilesPrinter implements NetwitnessEventsProducer {
    @Override
    public Map<Schema, Long> send(List<ConverterEventBase> eventsList) {
        printDailyFiles(eventsList);
        return sendToBroker();
    }
}
