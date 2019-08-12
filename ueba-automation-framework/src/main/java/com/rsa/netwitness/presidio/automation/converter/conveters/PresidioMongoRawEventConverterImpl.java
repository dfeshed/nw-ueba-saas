package com.rsa.netwitness.presidio.automation.converter.conveters;

import com.rsa.netwitness.presidio.automation.converter.conveters.mongo.*;
import com.rsa.netwitness.presidio.automation.converter.events.ConverterEventBase;
import com.rsa.netwitness.presidio.automation.converter.events.MongoKeyValueEvent;
import fortscale.common.general.Schema;
import presidio.data.domain.event.Event;
import presidio.data.domain.event.activedirectory.ActiveDirectoryEvent;
import presidio.data.domain.event.authentication.AuthenticationEvent;
import presidio.data.domain.event.file.FileEvent;
import presidio.data.domain.event.network.NetworkEvent;
import presidio.data.domain.event.process.ProcessEvent;
import presidio.data.domain.event.registry.RegistryEvent;

import java.util.Map;

public class PresidioMongoRawEventConverterImpl implements PresidioEventConverter {

    @Override
    public ConverterEventBase convert(Event event) {

        if (event instanceof ActiveDirectoryEvent) {
            Map<String, Object> converted = new EventToMetadataConverterActiveDirectory().convert((ActiveDirectoryEvent) event);
            return new MongoKeyValueEvent(converted, Schema.ACTIVE_DIRECTORY);
        }
        if (event instanceof AuthenticationEvent) {
            Map<String, Object> converted = new EventToMetadataConverterAuthentication().convert((AuthenticationEvent) event);
            return new MongoKeyValueEvent(converted, Schema.AUTHENTICATION);
        }
        if (event instanceof FileEvent) {
            Map<String, Object> converted = new EventToMetadataConverterFile().convert((FileEvent) event);
            return new MongoKeyValueEvent(converted, Schema.FILE);
        }
        if (event instanceof ProcessEvent) {
            Map<String, Object> converted = new EventToMetadataConverterProcess().convert((ProcessEvent) event);
            return new MongoKeyValueEvent(converted, Schema.PROCESS);
        }
        if (event instanceof RegistryEvent) {
            Map<String, Object> converted = new EventToMetadataConverterRegistry().convert((RegistryEvent) event);
            return new MongoKeyValueEvent(converted, Schema.REGISTRY);
        }

        if (event instanceof NetworkEvent) {
            return new MongoKeyValueEvent(new MongoTlsEventBuilder((NetworkEvent) event).getTlsRawEvent().getAsMongoKeyValue(), Schema.TLS);
        }

        throw new RuntimeException("Event type is not supported for: " + event.getClass().getTypeName());
    }
}
