package com.rsa.netwitness.presidio.automation.utils.ade.inserter.process;

import presidio.ade.domain.record.enriched.EnrichedRecord;
import presidio.ade.sdk.common.AdeManagerSdk;
import presidio.data.domain.event.Event;
import presidio.data.domain.event.process.ProcessEvent;
import com.rsa.netwitness.presidio.automation.utils.ade.inserter.AdeInserter;

import java.util.List;

/**
 * Created by YaronDL on 7/10/2017.
 */
public class AdeProcessEventsInserter extends AdeInserter{

    public AdeProcessEventsInserter(AdeManagerSdk adeManagerSDK) {
        super(adeManagerSDK);
    }

    @Override
    public List<? extends EnrichedRecord> convert(List<? extends Event> evList) {
        AdeProcessEventsConverter converter = new AdeProcessEventsConverter();
        return converter.convert(evList);
    }

    @Override
    public String getDataSource() {
        return "process";
    }

    @Override
    protected Class<? extends Event> getEventClass() {
        return ProcessEvent.class;
    }
}
