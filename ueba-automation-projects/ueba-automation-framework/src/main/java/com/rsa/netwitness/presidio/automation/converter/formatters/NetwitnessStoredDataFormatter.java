package com.rsa.netwitness.presidio.automation.converter.formatters;

import com.rsa.netwitness.presidio.automation.converter.events.NetwitnessEvent;
import presidio.nw.flume.domain.test.NetwitnessStoredData;

public class NetwitnessStoredDataFormatter implements EventFormatter<NetwitnessEvent, NetwitnessStoredData>  {

    private final MongoKeyValueFormatter mongoKeyValueFormatter;

    public NetwitnessStoredDataFormatter() {
        mongoKeyValueFormatter = new MongoKeyValueFormatter();
    }

    @Override
    public NetwitnessStoredData format(NetwitnessEvent event) {
        return new NetwitnessStoredData(mongoKeyValueFormatter.format(event));
    }
}
