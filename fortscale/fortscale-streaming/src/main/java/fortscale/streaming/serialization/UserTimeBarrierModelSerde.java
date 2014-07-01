package fortscale.streaming.serialization;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import fortscale.streaming.model.UserTimeBarrierModel;
import fortscale.streaming.service.UserTopEvents;
import org.apache.samza.serializers.Serde;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class UserTimeBarrierModelSerde implements Serde<UserTimeBarrierModel> {

    private static final Logger logger = LoggerFactory.getLogger(UserTimeBarrierModelSerde.class);

    private ObjectMapper mapper;

    public UserTimeBarrierModelSerde() {
        mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
    }

    @Override
    public UserTimeBarrierModel fromBytes(byte[] bytes) {
        try {
            return mapper.readValue(bytes, UserTimeBarrierModel.class);
        } catch (IOException e) {
            logger.error("error converting bytes to model ",  e);
            return null;
        }
    }

    @Override
    public byte[] toBytes(UserTimeBarrierModel userTimeBarrierModel) {
        try {
            return mapper.writeValueAsString(userTimeBarrierModel).getBytes("UTF-8");
        } catch (UnsupportedEncodingException | JsonProcessingException e) {
            logger.error("error converting model to bytes",  e);
            return null;
        }
    }
}
