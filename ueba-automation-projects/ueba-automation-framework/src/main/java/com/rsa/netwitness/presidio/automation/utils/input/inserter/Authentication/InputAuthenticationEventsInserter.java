package com.rsa.netwitness.presidio.automation.utils.input.inserter.Authentication;

import com.rsa.netwitness.presidio.automation.domain.config.Consts;
import com.rsa.netwitness.presidio.automation.utils.input.inserter.InputInserter;
import fortscale.domain.core.AbstractAuditableDocument;
import presidio.data.domain.event.Event;
import presidio.data.domain.event.authentication.AuthenticationEvent;

import java.util.List;

/**
 * Created by presidio on 7/23/17.
 */
public class InputAuthenticationEventsInserter extends InputInserter {

    public InputAuthenticationEventsInserter() {
        super();
    }

    @Override
    public List<? extends AbstractAuditableDocument> convert(List<? extends Event> evList) {
        InputAuthenticationEventsConverter converter = new InputAuthenticationEventsConverter();
        return converter.convert(evList);
    }

    @Override
    public String getDataSource() {
        return Consts.DataSource.AUTHENTICATION.toString();
    }

    @Override
    protected Class<? extends Event> getEventClass() {
        return AuthenticationEvent.class;
    }
}
