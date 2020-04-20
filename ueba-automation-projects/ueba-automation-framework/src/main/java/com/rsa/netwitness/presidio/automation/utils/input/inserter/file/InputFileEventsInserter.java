package com.rsa.netwitness.presidio.automation.utils.input.inserter.file;

import com.rsa.netwitness.presidio.automation.domain.config.Consts;
import com.rsa.netwitness.presidio.automation.utils.input.inserter.InputInserter;
import presidio.data.domain.event.Event;
import presidio.data.domain.event.file.FileEvent;
import presidio.sdk.api.domain.rawevents.FileRawEvent;

import java.util.List;


public class InputFileEventsInserter extends InputInserter {

    public InputFileEventsInserter() {
        super();
    }

    @Override
    protected List<? extends FileRawEvent> convert(List<? extends Event> evList) {
        InputFileEventsConverter converter = new InputFileEventsConverter();
        return converter.convert(evList);
    }

    @Override
    protected String getDataSource() {
        return Consts.DataSource.FILE.toString();
    }

    @Override
    protected Class<? extends Event> getEventClass() {
        return FileEvent.class;
    }
}
