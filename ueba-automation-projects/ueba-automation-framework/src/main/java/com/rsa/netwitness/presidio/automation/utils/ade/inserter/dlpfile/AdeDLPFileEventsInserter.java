package com.rsa.netwitness.presidio.automation.utils.ade.inserter.dlpfile;

import presidio.ade.domain.record.enriched.EnrichedRecord;
import presidio.ade.sdk.common.AdeManagerSdk;
import presidio.data.domain.event.Event;
import presidio.data.domain.event.dlpfile.DLPFileEvent;
import com.rsa.netwitness.presidio.automation.utils.ade.inserter.AdeInserter;

import java.util.List;

/**
 * Created by YaronDL on 7/10/2017.
 */
public class AdeDLPFileEventsInserter extends AdeInserter{

    public AdeDLPFileEventsInserter(AdeManagerSdk adeManagerSDK) {
        super(adeManagerSDK);
    }

    @Override
    public List<? extends EnrichedRecord> convert(List<? extends Event> evList) {
        AdeDLPFileEventsConverter converter = new AdeDLPFileEventsConverter();
        return converter.convert(evList);
    }

    @Override
    public String getDataSource() {
        return "dlpfile";
    }

    @Override
    protected Class<? extends Event> getEventClass() {
        return DLPFileEvent.class;
    }
}
