package presidio.output.forwarder;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import presidio.output.forwarder.payload.JsonPayloadBuilder;
import presidio.output.domain.records.users.User;
import presidio.output.domain.services.users.UserPersistencyService;
import presidio.output.forwarder.strategy.ForwarderStrategy;
import presidio.output.forwarder.strategy.ForwarderConfiguration;
import presidio.output.forwarder.strategy.ForwarderStrategyFactory;

import java.time.Instant;
import java.util.stream.Stream;

public class UsersForwarder extends Forwarder<User> {

    UserPersistencyService userPersistencyService;
    JsonPayloadBuilder payloadBuilder;


    public UsersForwarder(UserPersistencyService userPersistencyService, ForwarderConfiguration forwarderStrategyConfiguration, ForwarderStrategyFactory forwarderStrategyFactory) {
        super(forwarderStrategyConfiguration, forwarderStrategyFactory);
        this.userPersistencyService = userPersistencyService;
        payloadBuilder = new JsonPayloadBuilder<>(User.class, UserJsonMixin.class);
    }

    @Override
    Stream<User> getEntitiesToForward(Instant startDate, Instant endDate) {
        return userPersistencyService.findUsersByUpdatedDate(startDate, endDate);
    }

    @Override
    String getId(User user) {
        return user.getId();
    }

    @Override
    String buildPayload(User entity) throws Exception {
        return payloadBuilder.buildPayload(entity);
    }

    @Override
    ForwarderStrategy.PAYLOAD_TYPE getPayloadType() {
        return ForwarderStrategy.PAYLOAD_TYPE.ENTITY;
    }


    @JsonFilter(JsonPayloadBuilder.INCLUDE_PROPERTIES_FILTER)
    @JsonPayloadBuilder.JsonIncludeProperties({"id","entitiyId","entitytType","severity","alertsCount"})
    @JsonPropertyOrder({"id","entitiyId","entitytType","severity","alertsCount"})
    class UserJsonMixin extends User {

        @JsonProperty("entitiyId")
        String userId;

    }
}
