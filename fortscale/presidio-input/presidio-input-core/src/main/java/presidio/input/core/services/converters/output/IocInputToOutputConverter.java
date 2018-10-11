package presidio.input.core.services.converters.output;

import presidio.output.domain.records.events.EnrichedEvent;
import presidio.output.domain.records.events.IocEnrichedEvent;
import presidio.sdk.api.domain.AbstractInputDocument;
import presidio.sdk.api.domain.transformedevents.IocTransformedEvent;

public class IocInputToOutputConverter implements InputOutputConverter {
    @Override
    public EnrichedEvent convert(AbstractInputDocument document) {
        IocTransformedEvent transformedEvent = (IocTransformedEvent) document;
        IocEnrichedEvent outputEvent = new IocEnrichedEvent();

        outputEvent.setEventId(transformedEvent.getEventId());
        outputEvent.setEventDate(transformedEvent.getDateTime());
        outputEvent.setDataSource(transformedEvent.getDataSource());
        outputEvent.setName(transformedEvent.getName());
        outputEvent.setTactic(transformedEvent.getTactic());
        outputEvent.setLevel(transformedEvent.getLevel());
        outputEvent.setUserId(transformedEvent.getUserId());
        outputEvent.setUserName(transformedEvent.getUserName());
        outputEvent.setMachineId(transformedEvent.getMachineId());
        outputEvent.setMachineName(transformedEvent.getMachineName());
        outputEvent.setAdditionalInfo(transformedEvent.getAdditionalInfo());

        return outputEvent;
    }
}
