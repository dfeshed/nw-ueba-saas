package com.rsa.netwitness.presidio.automation.converter.producers;

import com.rsa.netwitness.presidio.automation.converter.events.ConverterEventBase;
import fortscale.common.general.Schema;

import java.util.List;
import java.util.Map;

public class DailyCefFilesNotSendPrinterImpl extends CefFilesPrinter implements NetwitnessEventsProducer {
    @Override
    public Map<Schema, Long> send(List<ConverterEventBase> eventsList) {
        return printDailyFiles(eventsList);
    }
}
