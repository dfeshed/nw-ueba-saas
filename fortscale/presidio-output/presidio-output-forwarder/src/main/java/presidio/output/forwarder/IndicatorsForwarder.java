package presidio.output.forwarder;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.collect.ImmutableMap;
import presidio.output.domain.records.alerts.Alert;
import presidio.output.forwarder.payload.JsonPayloadBuilder;
import presidio.output.domain.records.alerts.Indicator;
import presidio.output.domain.records.alerts.IndicatorEvent;
import presidio.output.domain.services.alerts.AlertPersistencyService;
import presidio.output.forwarder.strategy.ForwarderStrategy;
import presidio.output.forwarder.strategy.ForwarderConfiguration;
import presidio.output.forwarder.strategy.ForwarderStrategyFactory;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class IndicatorsForwarder extends Forwarder<Indicator> {

    AlertPersistencyService alertPersistencyService;
    JsonPayloadBuilder payloadBuilder;

    public IndicatorsForwarder(AlertPersistencyService alertPersistencyService, ForwarderConfiguration forwarderStrategyConfiguration, ForwarderStrategyFactory forwarderStrategyFactory) {
        super(forwarderStrategyConfiguration, forwarderStrategyFactory);
        this.alertPersistencyService = alertPersistencyService;
        if (!forwarderStrategyConfiguration.extendEntity(ForwarderStrategy.PAYLOAD_TYPE.INDICATOR)) {
            payloadBuilder = new JsonPayloadBuilder<Indicator>(Indicator.class, IndicatorJsonMixin.class);
        } else {
            payloadBuilder = new JsonPayloadBuilder<Indicator>(Indicator.class, ExtendedIndicatorJsonMixin.class);
        }
    }

    public ForwardedInstances forwardIndicators(List<String> alertIds){
        Stream<Indicator> indicators = alertPersistencyService.findIndicatorsByAlertIds(alertIds);
        return doForward(indicators, false);
    }

    @Override
    String getId(Indicator indicator) {
        return indicator.getId();
    }

    @Override
    String buildPayload(Indicator indicator) throws Exception {
        return payloadBuilder.buildPayload(indicator);
    }

    @Override
    Map buildHeader(Indicator entity) throws Exception {
        return ImmutableMap.of("carlos.event.name",entity.getName(),"id",entity.getId(),"carlos.event.severity", entity.getScore()
                ,"carlos.event.timestamp",entity.getStartDate().toInstant().toString());
    }

    @Override
    ForwarderStrategy.PAYLOAD_TYPE getPayloadType() {
        return ForwarderStrategy.PAYLOAD_TYPE.INDICATOR;
    }

    @JsonFilter(JsonPayloadBuilder.INCLUDE_PROPERTIES_FILTER)
    @JsonPayloadBuilder.JsonIncludeProperties({"id","startDate","endDate","indicatorType","schema","name","alertId","score","scoreContribution","anomalyValue","eventsNum","alert","events"})
    @JsonPropertyOrder({"alert","id","startDate","endDate","indicatorType","schema","name","alertId","score","scoreContribution","anomalyValue","eventsNum","events"})
    class IndicatorJsonMixin extends Indicator {
    }


    class ExtendedIndicatorJsonMixin extends IndicatorJsonMixin {

        @JsonProperty
        public List<IndicatorEvent> getEvents() {
            return super.getEvents();
        }


        @JsonUnwrapped
        @JsonSerialize(using = UnwrappingAlertSerializer.class)
        public Alert getAlert() {
            return super.getAlert();
        }
    }


    static class UnwrappingAlertSerializer extends  JsonSerializer<Alert> {

        public UnwrappingAlertSerializer() {
        }

        @Override
        public void serialize(Alert alert, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException, JsonProcessingException {

            jsonGenerator.writeStringField("entityId", alert.getEntityDocumentId());
            jsonGenerator.writeStringField("entityName", alert.getVendorEntityId());
            jsonGenerator.writeStringField("alertId", alert.getId());
            jsonGenerator.writeStringField("alertSeverity", alert.getSeverity().name());
            jsonGenerator.writeStringField("alertClassification", alert.getClassifications().get(0));
            jsonGenerator.writeNumberField("alertScore", alert.getScore());
        }

        @Override
        public boolean isUnwrappingSerializer() {
            return true;
        }

    }

}

