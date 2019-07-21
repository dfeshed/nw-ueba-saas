package com.rsa.netwitness.presidio.automation.utils.ade.inserter.activedirectory;

import presidio.ade.domain.record.enriched.EnrichedRecord;
import presidio.ade.sdk.common.AdeManagerSdk;
import presidio.data.domain.event.Event;
import presidio.data.domain.event.activedirectory.ActiveDirectoryEvent;
import com.rsa.netwitness.presidio.automation.utils.ade.inserter.AdeInserter;

import java.util.List;

public class AdeActiveDirectoryEventsInserter extends AdeInserter{

    public AdeActiveDirectoryEventsInserter(AdeManagerSdk adeManagerSDK) {
        super(adeManagerSDK);
    }

    @Override
    public List<? extends EnrichedRecord> convert(List<? extends Event> evList) {
        AdeActiveDirectoryEventsConverter converter = new AdeActiveDirectoryEventsConverter();
        return converter.convert(evList);
    }

    @Override
    public String getDataSource() {
        return "active_directory";
    }

    @Override
    protected Class<? extends Event> getEventClass() {
        return ActiveDirectoryEvent.class;
    }
}
