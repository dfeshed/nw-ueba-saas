package com.rsa.netwitness.presidio.automation.utils.input.inserter.registry;


import com.rsa.netwitness.presidio.automation.domain.config.Consts;
import com.rsa.netwitness.presidio.automation.utils.input.inserter.InputInserter;
import presidio.data.domain.event.Event;
import presidio.data.domain.event.registry.RegistryEvent;
import presidio.sdk.api.domain.AbstractInputDocument;

import java.util.List;

public class InputRegistryEventsInserter extends InputInserter {

    public InputRegistryEventsInserter() {
        super();
    }
    @Override
    protected List<? extends AbstractInputDocument> convert(List<? extends Event> evList) {
        InputRegistryEventsConverter converter = new InputRegistryEventsConverter();
        return converter.convert(evList);
    }

    @Override
    protected String getDataSource() {
        return Consts.DataSource.REGISTRY.toString();
    }

    @Override
    protected Class<? extends Event> getEventClass() {
        return RegistryEvent.class;
    }
}
