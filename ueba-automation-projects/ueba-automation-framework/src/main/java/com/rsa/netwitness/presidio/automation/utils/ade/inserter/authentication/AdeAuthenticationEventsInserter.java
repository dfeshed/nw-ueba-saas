package com.rsa.netwitness.presidio.automation.utils.ade.inserter.authentication;

import presidio.ade.domain.record.enriched.EnrichedRecord;
import presidio.ade.sdk.common.AdeManagerSdk;
import presidio.data.domain.event.Event;
import presidio.data.domain.event.authentication.AuthenticationEvent;
import com.rsa.netwitness.presidio.automation.utils.ade.inserter.AdeInserter;

import java.util.List;


public class AdeAuthenticationEventsInserter extends AdeInserter{

    public AdeAuthenticationEventsInserter(AdeManagerSdk adeManagerSDK) {
        super(adeManagerSDK);
    }

    @Override
    public List<? extends EnrichedRecord> convert(List<? extends Event> evList) {
        AdeAuthenticationEventsConverter converter = new AdeAuthenticationEventsConverter();
        return converter.convert(evList);
    }

    @Override
    public String getDataSource() {
        return "authentication";
    }

    @Override
    protected Class<? extends Event> getEventClass() {
        return AuthenticationEvent.class;
    }
}
