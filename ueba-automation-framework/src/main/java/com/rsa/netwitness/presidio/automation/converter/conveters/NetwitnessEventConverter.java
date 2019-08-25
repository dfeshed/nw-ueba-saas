package com.rsa.netwitness.presidio.automation.converter.conveters;

import com.rsa.netwitness.presidio.automation.converter.conveters.active_directory.NetwitnessActiveDirectoryEventConverter;
import com.rsa.netwitness.presidio.automation.converter.events.NetwitnessEvent;
import presidio.data.domain.event.Event;

public class NetwitnessEventConverter<T extends Event> {
    private INetwitnessEventConverter<T> converter;
    private T event;

    public NetwitnessEventConverter(T event) {
        this.event = event;
        NetwitnessActiveDirectoryEventConverter c = new NetwitnessActiveDirectoryEventConverter();
    }

    public NetwitnessEvent toNetwitnessEvent(T event) {
        converter.toNetwitnessEvent(event);
        return null;
    }
//
//    private INetwitnessEventConverter<T> selectConverter() {
//
//        if (event instanceof ActiveDirectoryEvent) {
//            return convertActiveDirectoryEvent((ActiveDirectoryEvent) event);
//        }
//        if (event instanceof AuthenticationEvent) {
//            return convertAuthenticationEvent((AuthenticationEvent) event);
//        }
//        if (event instanceof FileEvent) {
//            return convertFileEvent((FileEvent) event);
//        }
//        if (event instanceof ProcessEvent) {
//            return convertProcessEvent((ProcessEvent) event);
//        }
//        if (event instanceof RegistryEvent) {
//            return convertRegistryEvent((RegistryEvent) event);
//        }
//        if (event instanceof NetworkEvent) {
//            return convertNetworkEvent((NetworkEvent) event);
//        }
//
//        throw new RuntimeException("Event type is not supported for: " + event.getClass().getTypeName());
//    }



}
