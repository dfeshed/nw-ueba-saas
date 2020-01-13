package com.rsa.netwitness.presidio.automation.converter.formatters;

import com.rsa.netwitness.presidio.automation.converter.events.NetwitnessEvent;
import com.rsa.netwitness.presidio.automation.domain.store.NetwitnessEventStore;
import presidio.nw.flume.domain.test.NetwitnessStoredData;

import java.util.Objects;

public class NetwitnessStoredDataFormatter implements EventFormatter<NetwitnessEvent, NetwitnessStoredData>  {

    private final MongoKeyValueFormatter mongoKeyValueFormatter;

    public NetwitnessStoredDataFormatter(NetwitnessEventStore netwitnessEventStore) {
        Objects.requireNonNull(netwitnessEventStore);
        mongoKeyValueFormatter = new MongoKeyValueFormatter();
    }

    @Override
    public NetwitnessStoredData format(NetwitnessEvent event) {
        return new NetwitnessStoredData(mongoKeyValueFormatter.format(event));
    }
}
