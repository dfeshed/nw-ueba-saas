package com.rsa.netwitness.presidio.automation.converter.producers;

import com.rsa.netwitness.presidio.automation.converter.events.NetwitnessEvent;
import com.rsa.netwitness.presidio.automation.converter.formatters.EventFormatter;
import fortscale.common.general.Schema;

import java.util.Map;
import java.util.stream.Stream;

public class PartitionedCefFileProducer extends PartitionedFilesPrinter implements EventsProducer<Stream<NetwitnessEvent>> {

    public PartitionedCefFileProducer(EventFormatter<String> formatter) {
        super(formatter);
    }

    @Override
    public Map<Schema, Long> send(Stream<NetwitnessEvent> eventsList) {
        return printFiles(eventsList);
    }
}
