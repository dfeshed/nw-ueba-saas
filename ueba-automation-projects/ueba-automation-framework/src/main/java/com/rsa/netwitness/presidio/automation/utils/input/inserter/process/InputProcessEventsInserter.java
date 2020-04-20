package com.rsa.netwitness.presidio.automation.utils.input.inserter.process;


import com.rsa.netwitness.presidio.automation.domain.config.Consts;
import com.rsa.netwitness.presidio.automation.utils.input.inserter.InputInserter;
import presidio.data.domain.event.Event;
import presidio.data.domain.event.process.ProcessEvent;
import presidio.sdk.api.domain.AbstractInputDocument;

import java.util.List;

public class InputProcessEventsInserter extends InputInserter {

    public InputProcessEventsInserter() {
        super();
    }
    @Override
    protected List<? extends AbstractInputDocument> convert(List<? extends Event> evList) {
        InputProcessEventsConverter converter = new InputProcessEventsConverter();
        return converter.convert(evList);
    }

    @Override
    protected String getDataSource() {
        return Consts.DataSource.PROCESS.toString();
    }

    @Override
    protected Class<? extends Event> getEventClass() {
        return ProcessEvent.class;
    }
}
