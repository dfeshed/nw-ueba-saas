package presidio.output.forwarder;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import presidio.output.domain.records.entity.Entity;
import presidio.output.domain.services.entities.EntityPersistencyService;
import presidio.output.forwarder.payload.JsonPayloadBuilder;
import presidio.output.forwarder.strategy.ForwarderStrategy;
import presidio.output.forwarder.strategy.ForwarderConfiguration;
import presidio.output.forwarder.strategy.ForwarderStrategyFactory;

import java.time.Instant;
import java.util.Map;
import java.util.stream.Stream;

public class EntitiesForwarder extends Forwarder<Entity> {

    EntityPersistencyService entityPersistencyService;
    JsonPayloadBuilder payloadBuilder;

    public EntitiesForwarder(EntityPersistencyService entityPersistencyService, ForwarderConfiguration forwarderStrategyConfiguration, ForwarderStrategyFactory forwarderStrategyFactory) {
        super(forwarderStrategyConfiguration, forwarderStrategyFactory);
        this.entityPersistencyService = entityPersistencyService;
        payloadBuilder = new JsonPayloadBuilder<>(Entity.class, EntityJsonMixin.class);
    }

    public ForwardedInstances forwardEntities(Instant startDate, Instant endDate, String entityType){
        Stream<Entity> entities = entityPersistencyService.findEntitiesByLastUpdateLogicalDateAndEntityType(startDate, endDate, entityType);
        return doForward(entities, false);
    }

    @Override
    String getId(Entity entity) {
        return entity.getId();
    }

    @Override
    String buildPayload(Entity entity) throws Exception {
        return payloadBuilder.buildPayload(entity);
    }

    @Override
    Map buildHeader(Entity entity) {
        return null;
    }

    @Override
    ForwarderStrategy.PAYLOAD_TYPE getPayloadType() {
        return ForwarderStrategy.PAYLOAD_TYPE.ENTITY;
    }


    @JsonFilter(JsonPayloadBuilder.INCLUDE_PROPERTIES_FILTER)
    @JsonPayloadBuilder.JsonIncludeProperties({"id","entityId","entityType","severity","alertsCount"})
    @JsonPropertyOrder({"id","entityId","entityType","severity","alertsCount"})
    class EntityJsonMixin extends Entity {

        @JsonProperty("entityId")
        String entityId;

    }
}