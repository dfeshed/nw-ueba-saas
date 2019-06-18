package presidio.output.forwarder;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import presidio.output.domain.records.alerts.Alert;
import presidio.output.domain.services.alerts.AlertPersistencyService;
import presidio.output.forwarder.payload.JsonPayloadBuilder;
import presidio.output.forwarder.strategy.ForwarderStrategy;
import presidio.output.forwarder.strategy.ForwarderConfiguration;
import presidio.output.forwarder.strategy.ForwarderStrategyFactory;

import java.time.Instant;
import java.util.Map;
import java.util.stream.Stream;

public class AlertsForwarder extends Forwarder<Alert> {

    AlertPersistencyService alertPersistencyService;
    JsonPayloadBuilder payloadBuilder;

    public AlertsForwarder(AlertPersistencyService alertPersistencyService, ForwarderConfiguration forwarderConfiguration, ForwarderStrategyFactory forwarderStrategyFactory) {
        super(forwarderConfiguration, forwarderStrategyFactory);
        this.alertPersistencyService = alertPersistencyService;
        payloadBuilder = new JsonPayloadBuilder<>(Alert.class, AlertJsonMixin.class);
    }

    public ForwardedEntity forwardAlerts(Instant startDate, Instant endDate, String entityType){
        Stream<Alert> alerts = getAlertsToForward(startDate, endDate, entityType);
        return doForward(alerts);
    }

    private Stream<Alert> getAlertsToForward(Instant startDate, Instant endDate, String entityType) {
        return alertPersistencyService.findAlertsByDateAndEntityType(startDate, endDate, entityType);
    }

    @Override
    String getId(Alert alert) {
        return alert.getId();
    }

    @Override
    String buildPayload(Alert alert) throws Exception {
        return payloadBuilder.buildPayload(alert);
    }

    @Override
    Map buildHeader(Alert entity) throws Exception {
        return null;
    }

    @Override
    ForwarderStrategy.PAYLOAD_TYPE getPayloadType() {
        return ForwarderStrategy.PAYLOAD_TYPE.ALERT;
    }


    @JsonFilter(JsonPayloadBuilder.INCLUDE_PROPERTIES_FILTER)
    @JsonPayloadBuilder.JsonIncludeProperties({"id","startDate","endDate","UebaEntityId","entityDocumentId","score","severity","indicatorsNum","indicatorsNames","classifications","scoreContribution"})
    @JsonPropertyOrder({ "id","startDate","endDate","UebaEntityId","entityDocumentId","score","severity","indicatorsNum","indicatorsNames","classifications","scoreContribution" })
    class AlertJsonMixin extends Alert {

        @JsonProperty("UebaEntityId")
        String entityId;

        @JsonProperty("entityVendorId")
        String vendorEntityId;

    }
}
