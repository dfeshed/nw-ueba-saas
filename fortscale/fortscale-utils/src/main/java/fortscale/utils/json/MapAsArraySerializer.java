package fortscale.utils.json;

import java.io.IOException;
import java.util.Map;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

/**
 * Custom json serializer that converts Map<?,?> to array of values
 */
public class MapAsArraySerializer extends JsonSerializer<Map<?, ?>> {
    @Override
    public void serialize(final Map<?, ?> value, final JsonGenerator jgen, final SerializerProvider provider) 
    		throws IOException, JsonProcessingException {
    	provider.defaultSerializeValue(value.values(), jgen);
    }
}
