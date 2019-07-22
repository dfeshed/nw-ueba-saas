package com.rsa.netwitness.presidio.automation.converter.conveters;

import com.rsa.netwitness.presidio.automation.converter.events.NetwitnessEvent;
import presidio.data.domain.event.Event;
import presidio.data.domain.event.activedirectory.ActiveDirectoryEvent;
import presidio.data.domain.event.authentication.AuthenticationEvent;
import presidio.data.domain.event.file.FileEvent;
import presidio.data.domain.event.network.NetworkEvent;
import presidio.data.domain.event.process.ProcessEvent;
import presidio.data.domain.event.registry.RegistryEvent;

public class PresidioToNetwitnessEventConverterImpl implements PresidioEventConverter {


    @Override
    public NetwitnessEvent convert(Event event) {

        if (event instanceof ActiveDirectoryEvent) {
            return convertActiveDirectoryEvent((ActiveDirectoryEvent) event);
        }
        if (event instanceof AuthenticationEvent) {
            return convertAuthenticationEvent((AuthenticationEvent) event);
        }
        if (event instanceof FileEvent) {
            return convertFileEvent((FileEvent) event);
        }
        if (event instanceof ProcessEvent) {
            return convertProcessEvent((ProcessEvent) event);
        }
        if (event instanceof RegistryEvent) {
            return convertRegistryEvent((RegistryEvent) event);
        }
        if (event instanceof NetworkEvent) {
            return convertNetworkEvent((NetworkEvent) event);
        }

        throw new RuntimeException("Event type is not supported for: " + event.getClass().getTypeName());
    }

    private NetwitnessEvent convertActiveDirectoryEvent(ActiveDirectoryEvent event) {
        NetwitnessActiveDirectoryEventConverter generator = new NetwitnessActiveDirectoryEventConverter();
        return generator.getNext(event);
    }

    private NetwitnessEvent convertAuthenticationEvent(AuthenticationEvent event) {
        NetwitnessAuthenticationEventConverter generator = new NetwitnessAuthenticationEventConverter();
        return generator.getNext(event);
    }

    private NetwitnessEvent convertFileEvent(FileEvent event) {
        NetwitnessFileEventConverter generator = new NetwitnessFileEventConverter();
        return generator.getNext(event);
    }

    private NetwitnessEvent convertProcessEvent(ProcessEvent event) {
        return new NetwitnessProcessEventBuilder(event).getProcessEvent();
    }

    private NetwitnessEvent convertRegistryEvent(RegistryEvent event) {
        return new NetwitnessRegistryEventBuilder(event).getRegistryEvent();
    }

    private NetwitnessEvent convertNetworkEvent(NetworkEvent event) {
        return new NetwitnessTlsEventBuilder(event);
    }

}
