package presidio.output.forwarder;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import presidio.output.forwarder.payload.JsonPayloadBuilder;
import presidio.output.domain.records.alerts.Indicator;
import presidio.output.domain.records.alerts.IndicatorEvent;
import presidio.output.domain.services.alerts.AlertPersistencyService;
import presidio.output.forwarder.strategy.ForwarderStrategy;
import presidio.output.forwarder.strategy.ForwarderConfiguration;
import presidio.output.forwarder.strategy.ForwarderStrategyFactory;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.stream.Stream;

public class IndicatorsForwarder extends Forwarder<Indicator> {

    AlertPersistencyService alertPersistencyService;
    JsonPayloadBuilder payloadBuilder;


    public IndicatorsForwarder(AlertPersistencyService alertPersistencyService, ForwarderConfiguration forwarderStrategyConfiguration, ForwarderStrategyFactory forwarderStrategyFactory) {
        super(forwarderStrategyConfiguration, forwarderStrategyFactory);
        this.alertPersistencyService = alertPersistencyService;
        payloadBuilder = new JsonPayloadBuilder<Indicator>(Indicator.class, IndicatorJsonMixin.class, IndicatorEvent.class, new CustomEventSerializer());
    }

    @Override
    Stream<Indicator> getEntitiesToForward(Instant startDate, Instant endDate) {
        return alertPersistencyService.findIndicatorByDate(startDate, endDate); //TODO: add events
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
    ForwarderStrategy.PAYLOAD_TYPE getPayloadType() {
        return ForwarderStrategy.PAYLOAD_TYPE.INDICATOR;
    }

    @JsonFilter(JsonPayloadBuilder.INCLUDE_PROPERTIES_FILTER)
    @JsonPayloadBuilder.JsonIncludeProperties({"id","startDate","endDate","indicatorType","schema","indicatorName","UebaAlertId","score","scoreContribution","anomalyValue","eventsNum"})
    @JsonPropertyOrder({"id","startDate","endDate","indicatorType","schema","indicatorName","UebaAlertId","score","scoreContribution","anomalyValue","eventsNum"})
    class IndicatorJsonMixin extends Indicator {

        @JsonProperty("UebaAlertId")
        String alertId;

        @JsonProperty
        public List<IndicatorEvent> getEvents() {
            return super.getEvents();
        }
    }

    class CustomEventSerializer extends JsonSerializer<IndicatorEvent> {

        @Override
        public void serialize(IndicatorEvent value, JsonGenerator generator,
                              SerializerProvider provider) throws IOException, JsonProcessingException {
            generator.writeString(value.getFeatures().get("eventId").toString());
        }
    }
}

