package com.rsa.netwitness.presidio.automation.converter.conveters;

import com.rsa.netwitness.presidio.automation.converter.conveters.active_directory.NetwitnessActiveDirectoryEventConverter;
import com.rsa.netwitness.presidio.automation.converter.conveters.authentication.NetwitnessAuthenticationEventConverter;
import com.rsa.netwitness.presidio.automation.converter.conveters.endpoint.NetwitnessProcessEventConverter;
import com.rsa.netwitness.presidio.automation.converter.conveters.endpoint.NetwitnessRegistryEventConverter;
import com.rsa.netwitness.presidio.automation.converter.conveters.file.NetwitnessFileEventConverter;
import com.rsa.netwitness.presidio.automation.converter.conveters.network.NetwitnessTlsEventConverter;
import com.rsa.netwitness.presidio.automation.converter.events.NetwitnessEvent;
import presidio.data.domain.event.Event;
import presidio.data.domain.event.activedirectory.ActiveDirectoryEvent;
import presidio.data.domain.event.authentication.AuthenticationEvent;
import presidio.data.domain.event.file.FileEvent;
import presidio.data.domain.event.network.TlsEvent;
import presidio.data.domain.event.process.ProcessEvent;
import presidio.data.domain.event.registry.RegistryEvent;

public class PresidioEventConverter<T extends Event> implements EventConverter<T> {

    @Override
    public NetwitnessEvent convert(T event) {

        if (event instanceof ActiveDirectoryEvent) {
            return new NetwitnessActiveDirectoryEventConverter().convert((ActiveDirectoryEvent) event);
        }
        if (event instanceof AuthenticationEvent) {
            return new NetwitnessAuthenticationEventConverter().convert((AuthenticationEvent) event);
        }
        if (event instanceof FileEvent) {
            return new NetwitnessFileEventConverter().convert((FileEvent) event);
        }
        if (event instanceof ProcessEvent) {
            return new NetwitnessProcessEventConverter().convert((ProcessEvent) event);
        }
        if (event instanceof RegistryEvent) {
            return new NetwitnessRegistryEventConverter().convert((RegistryEvent) event);
        }
        if (event instanceof TlsEvent) {
            return new NetwitnessTlsEventConverter().convert((TlsEvent) event);
        }

        throw new RuntimeException("Event type is not supported for: " + event.getClass().getTypeName());
    }

}
