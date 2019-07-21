package com.rsa.netwitness.presidio.automation.utils.input.inserter.activedirectory;


import com.rsa.netwitness.presidio.automation.domain.config.Consts;
import com.rsa.netwitness.presidio.automation.utils.input.inserter.InputInserter;
import presidio.data.domain.event.Event;
import presidio.data.domain.event.activedirectory.ActiveDirectoryEvent;
import presidio.sdk.api.domain.AbstractInputDocument;

import java.util.List;

public class InputActiveDirectoryEventsInserter extends InputInserter {

    public InputActiveDirectoryEventsInserter() {
        super();
    }
    @Override
    protected List<? extends AbstractInputDocument> convert(List<? extends Event> evList) {
        InputActiveDirectoryEventsConverter converter = new InputActiveDirectoryEventsConverter();
        return converter.convert(evList);
    }

    @Override
    protected String getDataSource() {
        return Consts.DataSource.ACTIVE_DIRECTORY.toString();
    }

    @Override
    protected Class<? extends Event> getEventClass() {
        return ActiveDirectoryEvent.class;
    }
}
