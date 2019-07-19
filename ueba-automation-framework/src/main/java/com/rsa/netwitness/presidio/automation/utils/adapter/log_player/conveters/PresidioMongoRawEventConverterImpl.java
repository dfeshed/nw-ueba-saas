package com.rsa.netwitness.presidio.automation.utils.adapter.log_player.conveters;

import com.rsa.netwitness.presidio.automation.utils.adapter.log_player.conveters.mongo.EventToMetadataConverterFactory;
import com.rsa.netwitness.presidio.automation.utils.adapter.log_player.events.ConverterEventBase;
import com.rsa.netwitness.presidio.automation.utils.adapter.log_player.events.MongoKeyValueEvent;
import fortscale.common.general.Schema;
import presidio.data.domain.event.Event;
import presidio.data.domain.event.activedirectory.ActiveDirectoryEvent;
import presidio.data.domain.event.authentication.AuthenticationEvent;
import presidio.data.domain.event.file.FileEvent;
import presidio.data.domain.event.network.NetworkEvent;
import presidio.data.domain.event.process.ProcessEvent;
import presidio.data.domain.event.registry.RegistryEvent;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.rsa.netwitness.presidio.automation.utils.adapter.ReferenceIdGeneratorFactory.REFERENCE_ID_GENERATOR_TYPE_CONFIG_KEY;

public class PresidioMongoRawEventConverterImpl implements PresidioEventConverter {

    private Map<String, String> config = new HashMap<>();

    @Override
    public ConverterEventBase convert(Event event) {
        config.putIfAbsent(REFERENCE_ID_GENERATOR_TYPE_CONFIG_KEY, "cyclic");

        if (event instanceof ActiveDirectoryEvent) {
            List<Map<String, Object>> converted = new EventToMetadataConverterFactory().getConverter(Schema.ACTIVE_DIRECTORY)
                    .convert(config, Collections.singletonList(event));
            return new MongoKeyValueEvent(converted.get(0),Schema.ACTIVE_DIRECTORY);
        }
        if (event instanceof AuthenticationEvent) {
            List<Map<String, Object>> converted = new EventToMetadataConverterFactory().getConverter(Schema.AUTHENTICATION)
                    .convert(config, Collections.singletonList(event));
            return new MongoKeyValueEvent(converted.get(0),Schema.AUTHENTICATION);
        }
        if (event instanceof FileEvent) {
            List<Map<String, Object>> converted = new EventToMetadataConverterFactory().getConverter(Schema.FILE)
                    .convert(config, Collections.singletonList(event));
            return new MongoKeyValueEvent(converted.get(0),Schema.FILE);
        }
        if (event instanceof ProcessEvent) {
            List<Map<String, Object>> converted = new EventToMetadataConverterFactory().getConverter(Schema.PROCESS)
                    .convert(config, Collections.singletonList(event));
            return new MongoKeyValueEvent(converted.get(0),Schema.PROCESS);
        }
        if (event instanceof RegistryEvent) {
            List<Map<String, Object>> converted = new EventToMetadataConverterFactory().getConverter(Schema.REGISTRY)
                    .convert(config, Collections.singletonList(event));
            return new MongoKeyValueEvent(converted.get(0),Schema.REGISTRY);
        }

        if (event instanceof NetworkEvent) {
            return new MongoKeyValueEvent(new MongoTlsEventBuilder((NetworkEvent) event).getTlsRawEvent().getAsMongoKeyValue(), Schema.TLS);

            // return new MongoTlsEventBuilder((NetworkEvent) event).getTlsRawEvent();
        }

        throw new RuntimeException("Event type is not supported for: " + event.getClass().getTypeName());
    }
}
