package com.rsa.netwitness.presidio.automation.utils.ade.inserter.file;

import presidio.ade.domain.record.enriched.EnrichedRecord;
import presidio.ade.sdk.common.AdeManagerSdk;
import presidio.data.domain.event.Event;
import presidio.data.domain.event.file.FileEvent;
import com.rsa.netwitness.presidio.automation.utils.ade.inserter.AdeInserter;

import java.util.List;

/**
 * Created by YaronDL on 7/10/2017.
 */
public class AdeFileEventsInserter extends AdeInserter{

    public AdeFileEventsInserter(AdeManagerSdk adeManagerSDK) {
        super(adeManagerSDK);
    }

    @Override
    public List<? extends EnrichedRecord> convert(List<? extends Event> evList) {
        AdeFileEventsConverter converter = new AdeFileEventsConverter();
        return converter.convert(evList);
    }

    @Override
    public String getDataSource() {
        return "file";
    }

    @Override
    protected Class<? extends Event> getEventClass() {
        return FileEvent.class;
    }
}
