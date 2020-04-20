package com.rsa.netwitness.presidio.automation.utils.ade.inserter.registry;

import presidio.ade.domain.record.enriched.EnrichedRecord;
import presidio.ade.sdk.common.AdeManagerSdk;
import presidio.data.domain.event.Event;
import presidio.data.domain.event.registry.RegistryEvent;
import com.rsa.netwitness.presidio.automation.utils.ade.inserter.AdeInserter;

import java.util.List;

/**
 * Created by YaronDL on 7/10/2017.
 */
public class AdeRegistryEventsInserter extends AdeInserter{

    public AdeRegistryEventsInserter(AdeManagerSdk adeManagerSDK) {
        super(adeManagerSDK);
    }

    @Override
    public List<? extends EnrichedRecord> convert(List<? extends Event> evList) {
        AdeRegistryEventsConverter converter = new AdeRegistryEventsConverter();
        return converter.convert(evList);
    }

    @Override
    public String getDataSource() {
        return "registry";
    }

    @Override
    protected Class<? extends Event> getEventClass() {
        return RegistryEvent.class;
    }
}
