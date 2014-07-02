package fortscale.streaming.serialization;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.apache.samza.serializers.Serde;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class GenericJacksonSerde<T> implements Serde<T> {

    private static final Logger logger = LoggerFactory.getLogger(GenericJacksonSerde.class);

    private ObjectMapper mapper;
    private Class<T> type;

    public GenericJacksonSerde(Class<T> type) {
        mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        this.type = type;
    }

    @Override
    public T fromBytes(byte[] bytes) {
        try {
            return mapper.readValue(bytes, type);
        } catch (IOException e) {
            logger.error("error converting bytes to model ",  e);
            return null;
        }
    }

    @Override
    public byte[] toBytes(T model) {
        try {
            return mapper.writeValueAsString(model).getBytes("UTF-8");
        } catch (UnsupportedEncodingException | JsonProcessingException e) {
            logger.error("error converting model to bytes",  e);
            return null;
        }
    }
}
